package com.example

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.database.AppDatabase
import com.example.data.repository.ChatRepository
import com.example.ui.components.ChatScreen
import com.example.ui.components.DashboardScreen
import com.example.ui.components.SettingsScreen
import com.example.ui.components.OnboardingScreen
import com.example.ui.components.AuthScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.NirvayaViewModel
import com.example.ui.viewmodel.NirvayaViewModelFactory
import com.example.ui.viewmodel.Screen
import java.util.Locale

class MainActivity : ComponentActivity() {

    private var viewModelReference: NirvayaViewModel? = null

    // Register voice dictation speech-to-text contract
    private val voiceInputLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull() ?: ""
            viewModelReference?.stopSpeechRecognizerSimulation(spokenText)
        } else {
            viewModelReference?.stopSpeechRecognizerSimulation("")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Establish database and repository dependencies locally
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ChatRepository(database.chatDao())

        setContent {
            MyApplicationTheme {
                // Construct the system ViewModel using our custom Factory
                val nirvayaViewModel: NirvayaViewModel = viewModel(
                    factory = NirvayaViewModelFactory(application, repository)
                )
                viewModelReference = nirvayaViewModel

                val currentScreen by nirvayaViewModel.currentScreen.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // Route state-based navigation switcher
                    when (currentScreen) {
                        Screen.ONBOARDING -> {
                            OnboardingScreen(
                                viewModel = nirvayaViewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Screen.AUTH -> {
                            AuthScreen(
                                viewModel = nirvayaViewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Screen.DASHBOARD -> {
                            DashboardScreen(
                                viewModel = nirvayaViewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        Screen.CHAT -> {
                            ChatScreen(
                                viewModel = nirvayaViewModel,
                                onLaunchSpeechInput = { launchVoiceInputIntent() },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        Screen.SETTINGS -> {
                            SettingsScreen(
                                viewModel = nirvayaViewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }

    // Launch official system speech detection overlay safely
    private fun launchVoiceInputIntent() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Transmit instruction to Nirvaya AI...")
        }
        viewModelReference?.startSpeechRecognizerSimulation()
        try {
            voiceInputLauncher.launch(intent)
        } catch (e: Exception) {
            viewModelReference?.stopSpeechRecognizerSimulation("")
        }
    }
}
