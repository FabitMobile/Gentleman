package ru.fabit.gentleman.internal

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.fabit.gentleman.Gentleman

class Dummy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Gentleman.bind(this)
        log("Dummy_${hashCode()} created")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Gentleman.bind(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        log("Dummy_${hashCode()} destroyed")
    }
}