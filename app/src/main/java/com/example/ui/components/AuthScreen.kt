package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.NirvayaViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AuthTab {
    LOGIN,
    SIGNUP
}

enum class AuthFormMode {
    CREDENTIALS,
    PHONE_OTP
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: NirvayaViewModel,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var activeTab by remember { mutableStateOf(AuthTab.LOGIN) }
    var formMode by remember { mutableStateOf(AuthFormMode.CREDENTIALS) }

    // State parameters
    var emailOrUser by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    // Phone / OTP states
    var phoneNumber by remember { mutableStateOf("") }
    var isOtpRequested by remember { mutableStateOf(false) }
    var otpCodeInput by remember { mutableStateOf("") }
    var otpMessage by remember { mutableStateOf<String?>(null) }

    // General state modifiers
    var isLoading by remember { mutableStateOf(false) }
    var currentLoadingAction by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Authentic holographic breathing transition
    val infiniteTransition = rememberInfiniteTransition(label = "auth_breathe")
    val borderGlowPct by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderGlowPct"
    )

    if (isLoading) {
        // Futuristic Fullscreen Loading Dialogue
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(CyberBlack.copy(alpha = 0.95f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(
                    color = ElectricBlue,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = currentLoadingAction.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = ElectricBlue,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Syncing cryptographic tunnels to secure matrix nodes...",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF718096)),
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CyberBlack)
                    .padding(innerPadding)
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title section
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .border(
                                    1.dp,
                                    Brush.linearGradient(listOf(ElectricBlue, NeonPink)),
                                    RoundedCornerShape(16.dp)
                                )
                                .background(CyberDark, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ElectricBlue,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "NIRVAYA AI CORE",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                color = Color.White
                            )
                        )

                        Text(
                            text = "Authenticating identity sequence...",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF718096),
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    // Main Container Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .padding(vertical = 16.dp)
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        ElectricBlue.copy(alpha = borderGlowPct),
                                        NeonPink.copy(alpha = (1f - borderGlowPct))
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        colors = CardDefaults.cardColors(containerColor = CyberDark),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Tab Selectors (Login vs Signup)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberGrey)
                                    .padding(4.dp)
                            ) {
                                AuthTab.values().forEach { tab ->
                                    val isSelected = activeTab == tab
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(36.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) CyberBlack else Color.Transparent)
                                            .clickable {
                                                activeTab = tab
                                                errorMessage = null
                                            }
                                            .testTag("auth_tab_${tab.name.lowercase()}"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (tab == AuthTab.LOGIN) "LOGIN CORE" else "REGISTER CORE",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) ElectricBlue else Color(0xFF718096),
                                                letterSpacing = 1.sp
                                            )
                                        )
                                    }
                                }
                            }

                            // Error block
                            if (errorMessage != null) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = NeonPink.copy(alpha = 0.15f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, NeonPink.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                ) {
                                    Text(
                                        text = errorMessage!!,
                                        color = NeonPink,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(10.dp),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // OTP Toggle row
                            if (activeTab == AuthTab.LOGIN) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (formMode == AuthFormMode.CREDENTIALS) "Login using OTP" else "Login with secret matrix",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFA0AEC0)),
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    Text(
                                        text = if (formMode == AuthFormMode.CREDENTIALS) "USE PHONE" else "USE PASSWORD",
                                        color = NeonCyan,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier
                                            .clickable {
                                                formMode = if (formMode == AuthFormMode.CREDENTIALS) AuthFormMode.PHONE_OTP else AuthFormMode.CREDENTIALS
                                                errorMessage = null
                                                isOtpRequested = false
                                            }
                                            .padding(6.dp)
                                            .testTag("auth_form_mode_toggle")
                                    )
                                }
                            }

                            // Form Fields
                            if (activeTab == AuthTab.SIGNUP) {
                                // Name field
                                Text(
                                    text = "neural proxy descriptor".uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF718096), letterSpacing = 0.8.sp)
                                )
                                OutlinedTextField(
                                    value = nameInput,
                                    onValueChange = { nameInput = it },
                                    modifier = Modifier.fillMaxWidth().testTag("auth_signup_name"),
                                    placeholder = { Text("Alchemist (or choice handle)", color = Color(0xFF4A5568)) },
                                    singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = CyberBlack,
                                        unfocusedContainerColor = CyberBlack,
                                        focusedBorderColor = ElectricBlue,
                                        unfocusedBorderColor = CyberBorder
                                    )
                                )
                            }

                            if (formMode == AuthFormMode.CREDENTIALS || activeTab == AuthTab.SIGNUP) {
                                // User credentials mode
                                Text(
                                    text = "email or identifier".uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF718096), letterSpacing = 0.8.sp)
                                )
                                OutlinedTextField(
                                    value = emailOrUser,
                                    onValueChange = { emailOrUser = it },
                                    modifier = Modifier.fillMaxWidth().testTag("auth_email_field"),
                                    placeholder = { Text("alchemist@nirvaya.net", color = Color(0xFF4A5568)) },
                                    singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = CyberBlack,
                                        unfocusedContainerColor = CyberBlack,
                                        focusedBorderColor = ElectricBlue,
                                        unfocusedBorderColor = CyberBorder
                                    )
                                )

                                Text(
                                    text = "cognitive vault pass-key".uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF718096), letterSpacing = 0.8.sp)
                                )
                                OutlinedTextField(
                                    value = passwordInput,
                                    onValueChange = { passwordInput = it },
                                    modifier = Modifier.fillMaxWidth().testTag("auth_password_field"),
                                    placeholder = { Text("••••••••••••", color = Color(0xFF4A5568)) },
                                    singleLine = true,
                                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                    trailingIcon = {
                                        IconButton(onClick = { showPassword = !showPassword }) {
                                            Icon(
                                                imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = null,
                                                tint = Color(0xFF718096)
                                            )
                                        }
                                    },
                                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = CyberBlack,
                                        unfocusedContainerColor = CyberBlack,
                                        focusedBorderColor = ElectricBlue,
                                        unfocusedBorderColor = CyberBorder
                                    )
                                )
                            } else {
                                // Phone number login OTP mode
                                Text(
                                    text = "quantum phone node".uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF718096), letterSpacing = 0.8.sp)
                                )
                                OutlinedTextField(
                                    value = phoneNumber,
                                    onValueChange = { phoneNumber = it },
                                    modifier = Modifier.fillMaxWidth().testTag("auth_phone_field"),
                                    placeholder = { Text("+1 (555) 019-2150", color = Color(0xFF4A5568)) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = CyberBlack,
                                        unfocusedContainerColor = CyberBlack,
                                        focusedBorderColor = NeonCyan,
                                        unfocusedBorderColor = CyberBorder
                                    )
                                )

                                if (isOtpRequested) {
                                    Text(
                                        text = "transmission code (otp)".uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF718096), letterSpacing = 0.8.sp)
                                    )
                                    OutlinedTextField(
                                        value = otpCodeInput,
                                        onValueChange = { otpCodeInput = it },
                                        modifier = Modifier.fillMaxWidth().testTag("auth_otp_field"),
                                        placeholder = { Text("Code: 123456", color = Color(0xFF4A5568)) },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = CyberBlack,
                                            unfocusedContainerColor = CyberBlack,
                                            focusedBorderColor = ElectricBlue,
                                            unfocusedBorderColor = CyberBorder
                                        )
                                    )

                                    if (otpMessage != null) {
                                        Text(
                                            text = otpMessage!!,
                                            color = NeonCyan,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                        )
                                    }
                                }
                            }

                            // Submit Action button
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = {
                                    errorMessage = null
                                    if (activeTab == AuthTab.SIGNUP) {
                                        if (nameInput.trim().isEmpty() || emailOrUser.trim().isEmpty() || passwordInput.trim().isEmpty()) {
                                            errorMessage = "Please enter descriptor name, email, and password to register core."
                                            return@Button
                                        }
                                        isLoading = true
                                        currentLoadingAction = "Writing Security Records..."
                                        // Simulate secure local registration using standard scope launch
                                        scope.launch {
                                            delay(1200L)
                                            isLoading = false
                                            viewModel.login("Email Matrix", emailOrUser.trim(), nameInput.trim())
                                        }
                                    } else {
                                        if (formMode == AuthFormMode.CREDENTIALS) {
                                            if (emailOrUser.trim().isEmpty() || passwordInput.trim().isEmpty()) {
                                                errorMessage = "Vault inputs cannot be empty."
                                                return@Button
                                            }
                                            isLoading = true
                                            currentLoadingAction = "Consulting credentials vault..."
                                            scope.launch {
                                                delay(1000L)
                                                isLoading = false
                                                viewModel.login("Password Crypt", emailOrUser.trim(), emailOrUser.substringBefore("@"))
                                            }
                                        } else {
                                            if (!isOtpRequested) {
                                                if (phoneNumber.trim().isEmpty()) {
                                                    errorMessage = "Registered phone parameters absent."
                                                    return@Button
                                                }
                                                isLoading = true
                                                currentLoadingAction = "Transmitting OTP cell..."
                                                scope.launch {
                                                    delay(1500L)
                                                    isLoading = false
                                                    isOtpRequested = true
                                                    otpMessage = "SIMULATED TELECOM: Sent secure pairing OTP: 772150"
                                                }
                                            } else {
                                                if (otpCodeInput.trim().isEmpty()) {
                                                    errorMessage = "OTP node verification code required."
                                                    return@Button
                                                }
                                                isLoading = true
                                                currentLoadingAction = "Verifying pairing OTP..."
                                                scope.launch {
                                                    delay(1000L)
                                                    isLoading = false
                                                    viewModel.login("Phone OTP", "tel:$phoneNumber", "Proxy-${phoneNumber.takeLast(4)}")
                                                }
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("auth_submit_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (activeTab == AuthTab.SIGNUP) NeonPink else ElectricBlue
                                )
                            ) {
                                Text(
                                    text = if (activeTab == AuthTab.SIGNUP) "REGISTER & COMMIT CORE" else if (formMode == AuthFormMode.PHONE_OTP && !isOtpRequested) "REQUEST TRANSMISSION" else "DECRYPT SECURE ACCESS",
                                    color = CyberBlack,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Futuristic Alternative Logins & Guest Mode
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "SECURE PROTOCOL BYPASS CHANNELS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF718096),
                                letterSpacing = 1.5.sp
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Google Login Simulation
                            Button(
                                onClick = {
                                    isLoading = true
                                    currentLoadingAction = "Google Authentication Tunneling..."
                                    scope.launch {
                                        delay(1200L)
                                        isLoading = false
                                        viewModel.login("Google G-Node", "google-alchemist@gmail.com", "Hyper Google Explorer")
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("auth_google_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberGrey),
                                border = BorderStroke(1.dp, CyberBorder),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("🌐 GOOGLE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }

                            // Apple Login Simulation
                            Button(
                                onClick = {
                                    isLoading = true
                                    currentLoadingAction = "Apple Crypt Gateway..."
                                    scope.launch {
                                        delay(1200L)
                                        isLoading = false
                                        viewModel.login("Apple Secure-ID", "apple-alchemist@icloud.com", "Sleek Apple Nomad")
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("auth_apple_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberGrey),
                                border = BorderStroke(1.dp, CyberBorder),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("🍏 APPLE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }

                        // Biometrics / Secure Key Bypass
                        Button(
                            onClick = {
                                isLoading = true
                                currentLoadingAction = "Reading biometric synapse nodes..."
                                scope.launch {
                                    delay(1000L)
                                    isLoading = false
                                    viewModel.login("Biometric Synapse", "biometrics@nirvaya.net", "Sentinel Operator")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("auth_biometric_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGrey),
                            border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = null,
                                    tint = ElectricBlue
                                )
                                Text("SECURE BIOMETRIC SIGN-IN", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }

                        // Guest Mode Switch
                        Button(
                            onClick = {
                                viewModel.login("Simulated Guest", "guest@nirvaya.net", "Neo-Explorer")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("auth_guest_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            border = BorderStroke(1.dp, CyberBorder),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "ENTER AS NEURAL GUEST CO-PILOT",
                                color = Color(0xFFA0AEC0),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
