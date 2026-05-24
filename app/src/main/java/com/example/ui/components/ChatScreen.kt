package com.example.ui.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.database.ChatMessage
import com.example.ui.theme.*
import com.example.ui.viewmodel.NirvayaViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: NirvayaViewModel,
    onLaunchSpeechInput: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentSession by viewModel.currentSession.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val isThinking by viewModel.isThinking.collectAsState()
    val thinkingProcess by viewModel.thinkingProcess.collectAsState()
    val inputQuery by viewModel.inputQuery.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val isImageGenMode by viewModel.isImageGenMode.collectAsState()
    val activeMode by viewModel.personalityMode.collectAsState()
    val isTtsActive by viewModel.isTtsActive.collectAsState()
    val speechRecognizing by viewModel.speechRecognizing.collectAsState()
    val activeStreamingMessage by viewModel.activeStreamingMessage.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Activity picker to select screenshots or docs
    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.setSelectedImage(context, uri)
        }
    }

    // High-performance scroll controller: animate scroll only on major state/size changes to avoid animation load
    val isStreaming = activeStreamingMessage != null
    LaunchedEffect(messages.size, isThinking, isStreaming) {
        try {
            val totalItems = messages.size + (if (isStreaming) 1 else 0) + (if (isThinking) 1 else 0)
            if (totalItems > 0) {
                val targetIndex = totalItems - 1
                if (isStreaming) {
                    listState.scrollToItem(targetIndex)
                } else if (messages.isNotEmpty()) {
                    listState.animateScrollToItem(targetIndex)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ChatScreen", "Scroll animation failed gracefully", e)
        }
    }

    // Direct synchronous snap scroll tracking character append events with safety character count throttle
    var lastScrollLength by remember { mutableStateOf(0) }
    LaunchedEffect(activeStreamingMessage) {
        if (activeStreamingMessage == null) {
            lastScrollLength = 0
        }
    }

    LaunchedEffect(activeStreamingMessage?.length) {
        val currentLength = activeStreamingMessage?.length ?: 0
        if (activeStreamingMessage != null && currentLength > 0 && (currentLength - lastScrollLength >= 35)) {
            lastScrollLength = currentLength
            try {
                val totalItems = messages.size + 1 + (if (isThinking) 1 else 0)
                if (totalItems > 0) {
                    listState.scrollToItem(totalItems - 1)
                }
            } catch (e: Exception) {
                android.util.Log.e("ChatScreen", "Snap scroll failed gracefully", e)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(activeMode.accentColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(activeMode.codeIcon, fontSize = 16.sp)
                        }

                        Column {
                            Text(
                                text = currentSession?.title ?: "Cognition Workspace",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                ),
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Nirvaya • ${activeMode.title} Mode",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = activeMode.accentColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.setScreen(Screen.DASHBOARD) },
                        modifier = Modifier.testTag("chat_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to dashboard",
                            tint = ElectricBlue
                        )
                    }
                },
                actions = {
                    // Speech voice reading synthesis toggle
                    IconButton(
                        onClick = {
                            if (isTtsActive) {
                                viewModel.stopSpeaking()
                            } else {
                                if (messages.isNotEmpty()) {
                                    viewModel.speak(messages.last().content)
                                } else {
                                    viewModel.speak("I am Nirvaya AI. Set your objective.")
                                }
                            }
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isTtsActive) activeMode.accentColor.copy(alpha = 0.25f) else Color.Transparent)
                            .testTag("tts_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isTtsActive) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                            contentDescription = "Speech synthesiser",
                            tint = if (isTtsActive) activeMode.accentColor else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberBlack)
            )
        },
        bottomBar = {
            // High tech prompt control bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberBlack)
                    .navigationBarsPadding()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Attached file miniature preview section
                if (selectedImageUri != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                            .background(CyberDark)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "User Attachment Preview",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Column {
                                Text(
                                    text = "screenshot_or_document.jpg",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "Ready to transmit to core",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeonCyan
                                    )
                                )
                            }
                        }

                        IconButton(onClick = { viewModel.setSelectedImage(context, null) }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear uploaded file",
                                tint = NeonPink
                            )
                        }
                    }
                }

                // Input and control flow line
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Document attach trigger
                    IconButton(
                        onClick = {
                            pickMedia.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(CyberGrey)
                            .border(1.dp, CyberBorder, CircleShape)
                            .size(44.dp)
                            .testTag("photo_picker_trigger")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Attach image or document",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Mode: ImageGen lightning toggle
                    IconButton(
                        onClick = { viewModel.isImageGenMode.value = !isImageGenMode },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isImageGenMode) NeonAmber.copy(alpha = 0.25f) else CyberGrey)
                            .border(
                                1.dp,
                                if (isImageGenMode) NeonAmber else CyberBorder,
                                CircleShape
                            )
                            .size(44.dp)
                            .testTag("image_gen_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Toggle Visual Generator Mode",
                            tint = if (isImageGenMode) NeonAmber else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Primary text query wrapper
                    TextField(
                        value = inputQuery,
                        onValueChange = { viewModel.inputQuery.value = it },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(max = 120.dp)
                            .border(1.dp, CyberBorder, RoundedCornerShape(24.dp))
                            .clip(RoundedCornerShape(24.dp))
                            .testTag("query_text_input"),
                        placeholder = {
                            Text(
                                text = if (isImageGenMode) "Describe visual details..." else "Command Nirvaya AI...",
                                color = Color(0xFF718096),
                                fontSize = 14.sp
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CyberDark,
                            unfocusedContainerColor = CyberDark,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledContainerColor = CyberDark,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        maxLines = 4,
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                    )

                    // Unified voice dictation/send bubble
                    if (inputQuery.trim().isEmpty() && selectedImageUri == null) {
                        // Dictation active launcher
                        IconButton(
                            onClick = { onLaunchSpeechInput() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(activeMode.accentColor.copy(alpha = 0.15f))
                                .border(1.dp, activeMode.accentColor, CircleShape)
                                .size(44.dp)
                                .testTag("speech_dictation_trigger")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Dictation",
                                tint = activeMode.accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        // Action Send Trigger
                        IconButton(
                            onClick = { viewModel.sendMessage() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(activeMode.accentColor)
                                .size(44.dp)
                                .testTag("send_query_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send packet",
                                tint = CyberBlack,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberBlack)
                .padding(innerPadding)
        ) {
            // Conversational stream listing
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message = message, activeModeColor = activeMode.accentColor)
                }

                // Render dynamic real-time simulated typewriter output
                activeStreamingMessage?.let { streamText ->
                    item {
                        MessageBubble(
                            message = ChatMessage(
                                sessionId = currentSession?.id ?: "",
                                role = "model",
                                content = streamText,
                                hasThinkingProcess = false
                            ),
                            activeModeColor = activeMode.accentColor
                        )
                    }
                }

                // Core reasoning thinking loader block
                if (isThinking) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, activeMode.accentColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                                .background(CyberDark)
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = activeMode.accentColor,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "NIRVAYA COGNITION PROCESSOR ENGAGED...",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = activeMode.accentColor,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }

                            if (thinkingProcess != null) {
                                Text(
                                    text = thinkingProcess ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF718096),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    activeModeColor: Color
) {
    val isUser = message.role == "user"
    val clipboardManager = LocalClipboardManager.current
    var isTelemetryVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Label indicating sender role
        Text(
            text = if (isUser) "TRANSMISSION NODE [USER]" else "NIRVAYA INTELLIGENCE CORE",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                color = if (isUser) ElectricBlue else activeModeColor,
                letterSpacing = 0.8.sp
            ),
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp, end = 4.dp)
        )

        // Main content card
        Card(
            modifier = Modifier
                .widthIn(max = 340.dp)
                .border(
                    1.dp,
                    if (isUser) ElectricBlue.copy(alpha = 0.3f) else activeModeColor.copy(alpha = 0.2f),
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) CyberGrey else CyberDark
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Render attached user image if present
                if (isUser && message.hasImage && message.imageBase64 != null) {
                    val userBitmap = remember(message.imageBase64) {
                        try {
                            val decodedBytes = Base64.decode(message.imageBase64, Base64.DEFAULT)
                            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)?.asImageBitmap()
                        } catch (e: Exception) {
                            null
                        }
                    }

                    if (userBitmap != null) {
                        Image(
                            bitmap = userBitmap,
                            contentDescription = "User Attachment Document",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 180.dp)
                                .padding(bottom = 10.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Render dynamic text payload
                FormattedText(text = message.content)

                // Render model synthesized visual result if image gen mode populated
                if (!isUser && message.isImageResult && message.generatedImageUri != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, NeonAmber, RoundedCornerShape(8.dp))
                    ) {
                        val genBitmap = remember(message.generatedImageUri) {
                            try {
                                val decodedRes = Base64.decode(message.generatedImageUri, Base64.DEFAULT)
                                BitmapFactory.decodeByteArray(decodedRes, 0, decodedRes.size)?.asImageBitmap()
                            } catch (e: Exception) {
                                null
                            }
                        }

                        if (genBitmap != null) {
                            Image(
                                bitmap = genBitmap,
                                contentDescription = "AI Cinematic Synthesised Visual",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // Offline visual backup simulation in case API isn't registered
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.sweepGradient(
                                            listOf(
                                                ElectricBlue,
                                                NeonPink,
                                                NeonAmber,
                                                ElectricBlue
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text(
                                        text = "[Visual Render Cached]",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                if (!isUser) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Copy to clipboard helper
                        Text(
                            text = "Copy Code / Prompt",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = activeModeColor,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(message.content))
                                }
                                .padding(vertical = 4.dp, horizontal = 6.dp)
                        )

                        // Collapsible thinking telemetry loop launcher
                        if (message.hasThinkingProcess && message.thinkingProcessText != null) {
                            Text(
                                text = if (isTelemetryVisible) "Hide Log" else "Explain Logic",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF718096),
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier
                                    .clickable { isTelemetryVisible = !isTelemetryVisible }
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Expanded thinking logs panel
        if (!isUser && isTelemetryVisible && message.thinkingProcessText != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                modifier = Modifier
                    .widthIn(max = 340.dp)
                    .border(
                        1.dp,
                        CyberBorder,
                        RoundedCornerShape(8.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF040609)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "CORE REASONING DIAGNOSTICS:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            fontSize = 9.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = message.thinkingProcessText ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF718096),
                            fontSize = 10.sp,
                            lineHeight = 13.sp
                        )
                    )
                }
            }
        }
    }
}

// Visual text compiler rendering markdown markers properly (including inline headers/bullets/code)
@Composable
fun FormattedText(text: String) {
    val items = text.split("```")
    Column {
        items.forEachIndexed { index, part ->
            if (index % 2 == 1) {
                // Code block style
                CodeBlock(code = part)
            } else {
                // Standard markdown parser
                val lines = part.split("\n")
                lines.forEach { line ->
                    if (line.trim().startsWith("#")) {
                        val headerText = line.replace(Regex("^#+\\s*"), "")
                        Text(
                            text = headerText,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else if (line.trim().startsWith("-") || line.trim().startsWith("*")) {
                        val bulletText = line.replace(Regex("^[-*]\\s*"), "• ")
                        Text(
                            text = bulletText,
                            color = Color(0xFFE2E8F0),
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    } else {
                        if (line.isNotEmpty()) {
                            Text(
                                text = line,
                                color = Color(0xFFE2E8F0),
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Formatted Code card showing horizontal scrolling
@Composable
fun CodeBlock(code: String) {
    val lines = code.trim().split("\n")
    val language = if (lines.isNotEmpty() && !lines.first().contains(" ") && lines.first().length < 12) lines.first() else ""
    val actualCode = if (language.isNotEmpty()) lines.drop(1).joinToString("\n") else code

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
            .background(Color(0xFF030407))
    ) {
        if (language.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0B0D15))
                    .padding(vertical = 4.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = language.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = NeonCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "SOURCE FILE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF4A5568),
                        fontSize = 9.sp
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(10.dp)
        ) {
            Text(
                text = actualCode,
                fontFamily = FontFamily.Monospace,
                color = ElectricBlue,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}
