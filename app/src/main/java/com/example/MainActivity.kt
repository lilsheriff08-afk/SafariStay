package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.SafariRepository
import com.example.ui.SafariApp
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.SafariViewModel
import com.example.viewmodel.SafariViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Room persistence layer
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = SafariRepository(database.appDao())
        
        // Initialize ViewModel via Factory
        val viewModel = ViewModelProvider(
            this, 
            SafariViewModelFactory(repository)
        )[SafariViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    SafariApp(viewModel = viewModel)
                }
            }
        }
    }
}

