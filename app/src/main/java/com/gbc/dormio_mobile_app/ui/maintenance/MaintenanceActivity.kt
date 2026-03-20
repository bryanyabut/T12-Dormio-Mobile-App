package com.gbc.dormio_mobile_app.ui.maintenance

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gbc.dormio_mobile_app.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MaintenanceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_maintenance)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val role = intent.getStringExtra("user_role") ?: "STUDENT"
        // Read REQUEST_ID from intent: try String first, fall back to Int
        val requestIdString = intent.getStringExtra("REQUEST_ID")
        val requestId = requestIdString ?: intent.getIntExtra("REQUEST_ID", -1).takeIf { it != -1 }?.toString()

        if (savedInstanceState == null) {
            val fragment = MaintenanceListFragment()

            val bundle = Bundle().apply {
                putString("user_role", role)
                // Only put REQUEST_ID if it's valid (non-null and non-empty)
                requestId?.let { putString("REQUEST_ID", it) }
            }
            fragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()
        }
    }
}