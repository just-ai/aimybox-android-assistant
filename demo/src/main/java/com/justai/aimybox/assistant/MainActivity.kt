package com.justai.aimybox.assistant

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)

        viewModel = ViewModelProviders.of(this, ContextViewModelFactory(this)).get(MainViewModel::class.java)


        // TODO request permission
        val assistantFragment = AssistantDialogFragmentImpl()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.assistant_container, assistantFragment)
            commit()
        }
    }
}