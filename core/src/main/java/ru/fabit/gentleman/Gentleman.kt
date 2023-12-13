package ru.fabit.gentleman

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import ru.fabit.gentleman.appearance.Appearance
import ru.fabit.gentleman.internal.ContractResult
import ru.fabit.gentleman.internal.Dummy
import ru.fabit.gentleman.internal.OpenSettingsContract
import ru.fabit.gentleman.internal.RequestPermissionsContract
import ru.fabit.gentleman.internal.log

class Gentleman internal constructor() {
    companion object {
        var DEBUG = false

        internal const val RESULT_CODE = 0x9e471e
        internal const val LAYOUT_RES_ID = "LAYOUT_RES_ID"
        internal const val PERMISSIONS = "PERMISSIONS"
        internal const val PERMISSIONS_RESULT = "PERMISSIONS_RESULT"

        private var instance: Gentleman? = null

        fun askForPermissions(
            params: GentlemanSet,
            appearanceClass: Class<out Appearance>,
            rationaleLayoutResId: Int,
            settingLayoutResId: Int
        ): Boolean {
            log("Asked for permissions ${params.permissions} with${if (params.retry == none) "out" else ""} retry, ${params.manner} manner; appearance: $appearanceClass")
            if (instance != null) return false
            instance = Gentleman().apply {
                retry = params.retry
                manner = params.manner
                callback = params.callback
                permissions = params.permissions
                openSettings = params.openSettings
                this.appearanceClass = appearanceClass
                this.rationaleLayoutResId = rationaleLayoutResId
                this.settingLayoutResId = settingLayoutResId
            }

            ask(params)

            return true
        }

        private fun ask(params: GentlemanSet) {
            val context = params.context ?: return
            if (context.isAllPermissionsGranted(params.permissions)) {
                instance?.sendResult(
                    context,
                    retry = false,
                    AwaitResult(granted = params.permissions)
                )
            } else {
                context.startActivity(
                    Intent(
                        context,
                        Dummy::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }

        internal fun bind(activity: Dummy) {
            instance?.bind(activity)
        }

        internal fun onResume(activity: Dummy) {
            instance?.onResume(activity)
        }

        private fun Context.isAllPermissionsGranted(permissions: List<String>): Boolean {
            return permissions.all {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    private var appearanceClass: Class<out Appearance>? = null
    private var rationaleLayoutResId: Int? = null
    private var settingLayoutResId: Int? = null

    private var permissions: List<String> = listOf()
    private var callback: Callback? = null
    private var retry: Retry = none
    private var manner: Manner = usual
    private var openSettings: Boolean = false

    private var awaitSettings: Boolean = false
    private var sendResultAfterSettings = false

    private fun bind(activity: ComponentActivity) {
        val settingsRegister = activity.registerForSettings()
        val requestRegister = activity.registerForRequest(settingsRegister)
        when (manner) {
            usual -> {
                if (activity.isAnyRationale(permissions)) {
                    log("Requires rationale activity")
                    launchRationale(activity, requestRegister)
                } else {
                    requestRegister.launch()
                }
            }

            gentle -> {
                launchRationale(activity, requestRegister)
            }

            rude -> {
                requestRegister.launch()
            }
        }
    }

    private fun onResume(activity: ComponentActivity) {
        if (sendResultAfterSettings) {
            sendResultAfterSettings = false
            activity.sendGrantedPermissions()
        }
        if (awaitSettings) {
            activity.startSettingsActivity()
            awaitSettings = false
            sendResultAfterSettings = true
        }
    }

    private fun ComponentActivity.startSettingsActivity() {
        log("Opening settings")
        /*
            TODO проверки специальных разрешений
            android.permission.SYSTEM_ALERT_WINDOW
            android.permission.SCHEDULE_EXACT_ALARM
            android.permission.MANAGE_EXTERNAL_STORAGE
         */
        val action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val intent = Intent(action)
        intent.data = Uri.fromParts("package", packageName, null)
        startActivity(intent)
    }

    private fun launchRationale(activity: ComponentActivity, requestRegister: RequestRegister) {
        val appearance = appearanceClass ?: return
        val layout = rationaleLayoutResId ?: return
        val contract = RequestPermissionsContract(layout, appearance)
        val rationaleActivityRegister = activity.registerForActivityResult(contract) { result ->
            if (result == ContractResult.POSITIVE)
                requestRegister.launch()
            else {
                activity.sendGrantedPermissions()
            }
        }
        rationaleActivityRegister.launch()
    }

    private fun sendResult(context: Context, retry: Boolean, result: AwaitResult) {
        val appearance = appearanceClass
        if (retry
            && result.denied.isNotEmpty()
            && appearance != null
            && context is AppCompatActivity
            && context.isAnyRationale(permissions)
        ) {
            log("Permissions denied, performing retry")
            this.retry = none
            val set = GentlemanSet()
            set.with(context)
            set.ask(permissions)
            set.await(callback)
            ask(set)
        } else {
            log("Result received: $result")
            callback?.invoke(result)
            instance = null
        }
        if (context is Dummy)
            context.finish()
    }

    private fun ComponentActivity.registerForRequest(settingsRegister: SettingsRegister?): RequestRegister {
        val contract = ActivityResultContracts.RequestMultiplePermissions()
        return registerForActivityResult(contract) { result ->
            val granted = getGrantedPermissions(permissions)
            val denied = permissions - granted.toSet()
            val shouldOpenSettings = openSettings
                    && denied.containsAll(permissions)
                    && !isAnyRationale(permissions)
                    && settingsRegister != null
            if (shouldOpenSettings) {
                settingsRegister?.launch(null)
            } else {
                sendGrantedPermissions(retry == once)
                finish()
            }
        }
    }

    private fun ComponentActivity.registerForSettings(): SettingsRegister? {
        if (!openSettings) return null
        val appearance = appearanceClass ?: return null
        val settingsLayout = settingLayoutResId ?: return null
        val openSettingsContract = OpenSettingsContract(settingsLayout, appearance)
        return registerForActivityResult(openSettingsContract) { result ->
            if (result == ContractResult.POSITIVE) {
                awaitSettings = true
                if (lifecycle.currentState == Lifecycle.State.RESUMED)
                    onResume(this)
            } else {
                sendGrantedPermissions()
            }
        }
    }

    private fun Context.sendGrantedPermissions(retry: Boolean = false) {
        val granted = getGrantedPermissions(permissions)
        sendResult(
            this,
            retry,
            AwaitResult(
                granted = granted,
                denied = permissions - granted.toSet()
            )
        )
    }

    private fun Context.getGrantedPermissions(permissions: List<String>): List<String> {
        return permissions.filter {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun ComponentActivity.isAnyRationale(permissions: List<String>): Boolean {
        return permissions.any {
            ActivityCompat.shouldShowRequestPermissionRationale(this, it)
        }
    }

    private fun RequestRegister.launch() {
        launch(permissions.toTypedArray())
    }
}

typealias RequestRegister = ActivityResultLauncher<Array<String>>
typealias SettingsRegister = ActivityResultLauncher<Any?>