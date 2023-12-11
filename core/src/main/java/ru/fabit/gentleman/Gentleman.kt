package ru.fabit.gentleman

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ru.fabit.gentleman.internal.Dummy
import ru.fabit.gentleman.internal.RationalResult
import ru.fabit.gentleman.internal.TrueGentlemanContract
import ru.fabit.gentleman.internal.log

class Gentleman internal constructor() {
    companion object {
        var DEBUG = false

        internal const val RESULT_CODE = 0x9e471e
        internal const val PERMISSION_KEY = "PERMISSION_KEY"
        internal const val PERMISSION_RESULT = "PERMISSION_RESULT"

        private var instance: Gentleman? = null

        fun askForPermissions(params: GentlemanSet, appearanceClass: Class<*>): Boolean {
            log("Asked for permissions ${params.permissions} with${if (params.retry == none) "out" else ""} retry; appearance: $appearanceClass")
            if (instance != null) return false
            instance = Gentleman().apply {
                retry = params.retry
                callback = params.callback
                permissions = params.permissions
                this.appearanceClass = appearanceClass
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

        private fun Context.isAllPermissionsGranted(permissions: List<String>): Boolean {
            return permissions.all {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    private var permissions: List<String> = listOf()
    private var callback: Callback? = null
    private var appearanceClass: Class<*>? = null
    private var retry: Retry = none

    private fun bind(activity: ComponentActivity) {
        val appearance = appearanceClass ?: return
        val requestRegister = activity.registerForRequest()
        if (activity.isAnyRationale(permissions)) {
            log("Requires rationale activity")
            val contract = TrueGentlemanContract(appearance)
            val rationaleActivityRegister = Register(
                activity.registerForActivityResult(contract) { result ->
                    if (result == RationalResult.REQUEST_PERMISSIONS)
                        requestRegister.launch()
                    else {
                        val granted = activity.getGrantedPermissions(permissions)
                        sendResult(
                            activity,
                            false,
                            AwaitResult(
                                granted = granted,
                                denied = permissions - granted.toSet()
                            )
                        )
                    }
                }
            )
            rationaleActivityRegister.launch()
        } else {
            requestRegister.launch()
        }
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

    private fun ComponentActivity.registerForRequest(): Register {
        val contract = ActivityResultContracts.RequestMultiplePermissions()
        val launcher = registerForActivityResult(contract) { result ->
            val granted = getGrantedPermissions(permissions)
            sendResult(
                this,
                retry == once,
                AwaitResult(
                    granted = granted,
                    denied = permissions - granted.toSet()
                )
            )
            finish()
        }
        return Register(launcher)
    }

    private inner class Register(
        private val launcher: ActivityResultLauncher<Array<String>>
    ) {
        fun launch() {
            launcher.launch(permissions.toTypedArray())
        }
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
}