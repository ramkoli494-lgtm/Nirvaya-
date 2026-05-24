package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.NirvayaViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: NirvayaViewModel,
    modifier: Modifier = Modifier
) {
    val userName by viewModel.userName.collectAsState()
    val plugins by viewModel.enabledPlugins.collectAsState()
    val isDeveloper by viewModel.developerMode.collectAsState()
    val apiKeyRaw = viewModel.apiKey

    var nameInput by remember { mutableStateOf(userName) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "CONTROL MATRIX",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Color.White
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.setScreen(Screen.DASHBOARD) },
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to dashboard",
                            tint = ElectricBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberBlack)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberBlack)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Profile identity parameters
            item {
                Text(
                    text = "NEURAL IDENTITY PREFERENCE",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color(0xFF718096),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = CyberDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Set your alphanumeric handle below. Nirvaya AI commits this to its long-term memory registers.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFA0AEC0)
                            )
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .testTag("username_input_field"),
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(
                                    fontSize = 14.sp,
                                    color = Color.White
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = CyberGrey,
                                    unfocusedContainerColor = CyberGrey,
                                    focusedIndicatorColor = ElectricBlue,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )

                            Button(
                                onClick = {
                                    if (nameInput.trim().isNotEmpty()) {
                                        viewModel.userName.value = nameInput.trim()
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                modifier = Modifier.testTag("save_username_button")
                            ) {
                                Text(text = "COMMIT", color = CyberBlack, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Plugin tools registry panel
            item {
                Text(
                    text = "INTEGRATED TOOL REGISTRY (PLUGINS)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color(0xFF718096),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = CyberDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Enable specific analytical plugins for computational reasoning tasks:",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFA0AEC0)
                            )
                        )

                        plugins.forEach { (name, enabled) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberGrey)
                                    .padding(vertical = 4.dp, horizontal = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (enabled) Icons.Default.ToggleOn else Icons.Default.ToggleOff,
                                        contentDescription = null,
                                        tint = if (enabled) NeonCyan else Color(0xFF718096)
                                    )
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                Switch(
                                    checked = enabled,
                                    onCheckedChange = { viewModel.togglePlugin(name) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = NeonCyan,
                                        checkedTrackColor = NeonCyan.copy(alpha = 0.2f),
                                        uncheckedThumbColor = Color(0xFF718096),
                                        uncheckedTrackColor = CyberBlack
                                    ),
                                    modifier = Modifier.testTag("plugin_switch_${name.replace(" ", "_")}")
                                )
                            }
                        }
                    }
                }
            }

            // Security core alert warnings
            item {
                Text(
                    text = "SECURITY COGNITION WARN",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color(0xFF718096),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeonPink, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = CyberDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Security Core Error Warn",
                                tint = NeonPink,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "DECOMPILE SENSITIVITY NOTICE",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = NeonPink
                                )
                            )
                        }

                        Text(
                            text = "Security Warning: I have included your API keys in the generated APK file for this prototype. Please be aware that Android APKs can be easily decompiled, and these keys can be extracted by anyone who has access to the file. Do not share this APK file publicly or with unauthorized individuals to prevent potential misuse.",
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "BuildConfig Key Sync: ${if (apiKeyRaw.isNotEmpty() && apiKeyRaw != "MY_GEMINI_API_KEY") "CONNECTED (BuildConfig ✔)" else "OFFLINE (Simulated Engine active)"}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = if (apiKeyRaw.isNotEmpty() && apiKeyRaw != "MY_GEMINI_API_KEY") NeonCyan else Color(0xFF718096),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Diagnostic systems stats
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Nirvaya AI Quantum Subsystems • v1.8.4",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF4A5568),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Build system status: Fully Synchronized\nNo persistent anomalies detected.",
                        color = Color(0xFF4A5568),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
