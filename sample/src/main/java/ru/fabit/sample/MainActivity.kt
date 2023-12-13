package ru.fabit.sample

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.fabit.gentleman.Gentleman
import ru.fabit.gentleman.appearance.Tuxedo
import ru.fabit.gentleman.gentle
import ru.fabit.gentleman.innerChamber
import ru.fabit.gentleman.once

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val context = applicationContext
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            Gentleman in Tuxedo {
                with(context)
                gentle manner ask(ACCESS_COARSE_LOCATION) retry once
                suggest goTo innerChamber
                await { result ->
                    showMessage("granted=${result.granted}")
                    showMessage("denied=${result.denied}")
                    if (result.denied.isEmpty())
                        button.text = "granted"
                    if (result.granted.isEmpty())
                        button.text = "denied"
                }
            }
        }
    }

    private fun showMessage(text: String) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }
}