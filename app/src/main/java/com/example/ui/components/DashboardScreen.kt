package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.model.PersonalityMode
import com.example.ui.theme.*
import com.example.ui.viewmodel.NirvayaViewModel
import com.example.ui.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: NirvayaViewModel,
    modifier: Modifier = Modifier
) {
    val chatSessions by viewModel.chatSessions.collectAsState()
    val currentMode by viewModel.personalityMode.collectAsState()
    val automationLogs by viewModel.automationActivities.collectAsState()
    val userName by viewModel.userName.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var activeDialog by remember { mutableStateOf<String?>(null) } // "voice", "image", "video", "coding", "notes", "memory", "file", "marketplace", "profile", "premium"

    // Breathing pulse for cyber nucleus core
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = CyberBlack,
                drawerContentColor = Color.White,
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
            ) {
                CyberDrawerContent(
                    viewModel = viewModel,
                    activeDialogSetter = {
                        activeDialog = it
                        scope.launch { drawerState.close() }
                    },
                    onCloseDrawer = { scope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(CyberBlack)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Welcome and Status Header with Menu Open Button
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(CyberGrey)
                                    .border(1.dp, CyberBorder, CircleShape)
                                    .testTag("dashboard_menu_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Open matrix drawer",
                                    tint = ElectricBlue
                                )
                            }

                            Column {
                                Text(
                                    text = "NIRVAYA AI",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 2.sp,
                                        color = ElectricBlue
                                    )
                                )
                                Text(
                                    text = "Next-Gen Cognition Core • $userName",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF718096),
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.setScreen(Screen.SETTINGS) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(CyberGrey)
                                .border(1.dp, CyberBorder, CircleShape)
                                .testTag("dashboard_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "Settings Interface",
                                tint = ElectricBlue
                            )
                        }
                    }
                }

                // Animated Nucleus Widget representing the master intelligence core
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Brush.linearGradient(listOf(ElectricBlue, NeonPink)), RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(CyberDark)
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Pulsing cyber ball Canvas
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clickable { viewModel.createNewSession(currentMode) },
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val minSize = size.minDimension
                                    if (minSize > 0f) {
                                        val pulseVal = pulseScale.coerceAtLeast(0.1f)
                                        val radius1 = (minSize / 1.5f * pulseVal).coerceAtLeast(1f)
                                        val radius2 = (minSize / 4f).coerceAtLeast(1f)
                                        drawCircle(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    currentMode.accentColor.copy(alpha = (0.4f * pulseVal).coerceIn(0f, 1f)),
                                                    Color.Transparent
                                                ),
                                                radius = radius1
                                            ),
                                            radius = radius1
                                        )
                                        drawCircle(
                                            color = currentMode.accentColor,
                                            radius = radius2,
                                            style = Stroke(width = 3.dp.toPx())
                                        )
                                    }
                                }
                                Text(
                                    text = currentMode.codeIcon,
                                    fontSize = 24.sp,
                                    lineHeight = 24.sp
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "CORE STATUS: ACTIVE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = currentMode.accentColor,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Text(
                                    text = "Nirvaya AI Quantum Subsystems",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tapping the core fires a new workspace targeting ${currentMode.title} mode.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFFA0AEC0)
                                    )
                                )
                            }
                        }
                    }
                }

                // Core load telemetry grid
                item {
                    Text(
                        text = "COGNITIVE MATRIX STATS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA0AEC0),
                            letterSpacing = 1.sp
                        )
                    )
                }

                item {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        maxItemsInEachRow = 2
                    ) {
                        // Metric Card 1: Vector Nodes
                        TelemetryCard(
                            title = "Database Sync",
                            value = "32,845",
                            label = "Embeddings Cached",
                            icon = Icons.Default.NetworkCell,
                            accentColor = ElectricBlue,
                            modifier = Modifier.weight(1f)
                        )

                        // Metric Card 2: Compute
                        TelemetryCard(
                            title = "Memory Cells",
                            value = "${chatSessions.size} active",
                            label = "Threads stored",
                            icon = Icons.Default.Save,
                            accentColor = NeonPink,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        maxItemsInEachRow = 2
                    ) {
                        TelemetryCard(
                            title = "Active Subsystem",
                            value = currentMode.title.uppercase(),
                            label = "Personality mapped",
                            icon = Icons.Default.Cyclone,
                            accentColor = currentMode.accentColor,
                            modifier = Modifier.weight(1f)
                        )

                        TelemetryCard(
                            title = "Sync Node Status",
                            value = "SECURE (99%)",
                            label = "Ktor proxy connection",
                            icon = Icons.Default.Shield,
                            accentColor = NeonCyan,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Personality Engine Selection Slider list
                item {
                    Text(
                        text = "AVAILABLE PERSONALITY NODES",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA0AEC0),
                            letterSpacing = 1.sp
                        )
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PersonalityMode.values().forEach { mode ->
                            val isSelected = currentMode == mode
                            val outlineGlow = if (isSelected) mode.accentColor else CyberBorder

                            Box(
                                modifier = Modifier
                                    .width(136.dp)
                                    .border(1.dp, outlineGlow, RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CyberGrey)
                                    .clickable { viewModel.setPersonalityMode(mode) }
                                    .padding(14.dp)
                                    .testTag("personality_select_${mode.name.lowercase()}")
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = mode.codeIcon,
                                            fontSize = 20.sp,
                                            lineHeight = 20.sp
                                        )
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(mode.accentColor)
                                            )
                                        }
                                    }

                                    Text(
                                        text = mode.title,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )

                                    Text(
                                        text = mode.description,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF718096),
                                            fontSize = 9.sp,
                                            lineHeight = 11.sp
                                        ),
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                // AI Automation simulated actions
                item {
                    Text(
                        text = "AUTONOMOUS SYSTEM LOGS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA0AEC0),
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
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            automationLogs.takeLast(4).forEach { log ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = log,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = ElectricBlue,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "LIVE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = NeonCyan,
                                            fontSize = 8.sp,
                                            background = NeonCyan.copy(alpha = 0.1f)
                                        ),
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Active conversations
                item {
                    Text(
                        text = "ACTIVE MEMORY CELLS (CHAT THREADS)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA0AEC0),
                            letterSpacing = 1.sp
                        )
                    )
                }

                if (chatSessions.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CyberBorder, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = CyberDark)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Comment,
                                    contentDescription = null,
                                    tint = Color(0xFF4A5568),
                                    modifier = Modifier.size(36.dp)
                                )
                                Text(
                                    text = "NO COGNITIVE THREADS INITIALIZED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF718096),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "Tap the master intelligence nucleus core in the center above to initialize a new session.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF4A5568)),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(chatSessions) { session ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                    // Use testTag for standard testing validation
                                .testTag("session_card_${session.id}")
                                .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.selectSession(session)
                                },
                            colors = CardDefaults.cardColors(containerColor = CyberDark)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    val sessionModel = PersonalityMode.values().find { it.name == session.personalityMode } ?: currentMode
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(sessionModel.accentColor.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = sessionModel.codeIcon, fontSize = 18.sp)
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = session.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${sessionModel.title} • ${formatDate(session.timestamp)}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFF718096),
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.deleteSession(session.id) },
                                    modifier = Modifier.testTag("delete_session_${session.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = "Delete Memory Cell",
                                        tint = NeonPink.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Animated Premium capabilities full screen Dialog overlays
            if (activeDialog != null) {
                PremiumDialog(
                    dialogType = activeDialog!!,
                    viewModel = viewModel,
                    onDismiss = { activeDialog = null }
                )
            }
        }
    }
}

@Composable
fun CyberDrawerContent(
    viewModel: NirvayaViewModel,
    activeDialogSetter: (String) -> Unit,
    onCloseDrawer: () -> Unit
) {
    val userName by viewModel.userName.collectAsState()
    val authMethod by viewModel.authMethod.collectAsState()
    val chatSessions by viewModel.chatSessions.collectAsState()
    val currentMode by viewModel.personalityMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Profile Info Header inside Sidebar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberDark)
                    .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
                    .clickable { activeDialogSetter("profile") }
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(ElectricBlue, NeonPink)))
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(CyberBlack),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.take(1).uppercase(),
                            color = ElectricBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                Column {
                    Text(
                        text = userName.uppercase(),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "OPERATOR LEVEL 3",
                        color = NeonCyan,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            HorizontalDivider(color = CyberBorder, thickness = 1.dp)

            // Scrollable Tools section
            Text(
                text = "NEURAL CAPABILITIES",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF718096),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )

            // Section scrollable items
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DrawerItem(
                    label = "Chat Workspace",
                    icon = Icons.Default.ChatBubble,
                    color = ElectricBlue,
                    onClick = {
                        onCloseDrawer()
                        viewModel.setScreen(Screen.CHAT)
                    }
                )

                DrawerItem(
                    label = "Voice Mode Sync",
                    icon = Icons.Default.Mic,
                    color = NeonPink,
                    onClick = { activeDialogSetter("voice") }
                )

                DrawerItem(
                    label = "AI Image Studio",
                    icon = Icons.Default.Brush,
                    color = NeonCyan,
                    onClick = { activeDialogSetter("image") }
                )

                DrawerItem(
                    label = "AI Video Creator",
                    icon = Icons.Default.Videocam,
                    color = NeonPurple,
                    onClick = { activeDialogSetter("video") }
                )

                DrawerItem(
                    label = "Coding Lab Simulator",
                    icon = Icons.Default.Code,
                    color = NeonCyan,
                    onClick = { activeDialogSetter("coding") }
                )

                DrawerItem(
                    label = "Smart Cognitive Notes",
                    icon = Icons.Default.Note,
                    color = NeonAmber,
                    onClick = { activeDialogSetter("notes") }
                )

                DrawerItem(
                    label = "Memory Vault Query",
                    icon = Icons.Default.Storage,
                    color = ElectricBlue,
                    onClick = { activeDialogSetter("memory") }
                )

                DrawerItem(
                    label = "Doc & OCR Analyzer",
                    icon = Icons.Default.CloudUpload,
                    color = NeonPink,
                    onClick = { activeDialogSetter("file") }
                )

                DrawerItem(
                    label = "AI Tools Marketplace",
                    icon = Icons.Default.Storefront,
                    color = NeonPurple,
                    onClick = { activeDialogSetter("marketplace") }
                )

                DrawerItem(
                    label = "Control Matrix Settings",
                    icon = Icons.Default.Settings,
                    color = Color.White,
                    onClick = {
                        onCloseDrawer()
                        viewModel.setScreen(Screen.SETTINGS)
                    }
                )
            }
        }

        // Sidebar bottom premium & actions
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Premium upgrade CTA Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberGrey),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Brush.linearGradient(listOf(NeonPurple, NeonPink)),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { activeDialogSetter("premium") }
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = NeonPink, modifier = Modifier.size(12.dp))
                        Text(
                            text = "NIRVAYA PREMIUM",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = "Access sovereign GPUs & unthrottled reasoning.",
                        color = Color(0xFFA0AEC0),
                        fontSize = 9.sp,
                        lineHeight = 11.sp
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        onCloseDrawer()
                        viewModel.logout()
                    }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Sign out",
                        tint = NeonPink,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Sign Out operator",
                        color = NeonPink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = (authMethod ?: "GUEST").uppercase(),
                    color = Color(0xFF718096),
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun DrawerItem(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            color = Color(0xFFE2E8F0),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun TelemetryCard(
    title: String,
    value: String,
    label: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, CyberBorder.copy(alpha = 0.8f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberDark)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF718096),
                        letterSpacing = 0.8.sp
                    )
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF718096),
                    fontSize = 11.sp
                )
            )
        }
    }
}

// Helpers
fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumDialog(
    dialogType: String,
    viewModel: NirvayaViewModel,
    onDismiss: () -> Unit
) {
    val userName by viewModel.userName.collectAsState()
    val currentMode by viewModel.personalityMode.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .border(1.dp, Brush.linearGradient(listOf(ElectricBlue, NeonPink)), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberDark),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header of dialog
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val dialogHeading = when (dialogType) {
                        "voice" -> "NEURAL VOICE CONSOLE"
                        "image" -> "AI IMAGE STUDIO"
                        "video" -> "AI VIDEO COMPILER"
                        "coding" -> "CYBER CODING LAB"
                        "notes" -> "COGNITIVE NOTES"
                        "memory" -> "MEMORY VAULT TERMINAL"
                        "file" -> "DOC & OCR ANALYZER"
                        "marketplace" -> "INTELLIGENCE MARKETPLACE"
                        "profile" -> "OPERATOR IDENTITY"
                        else -> "NIRVAYA PREMIUM ACCESS"
                    }

                    Text(
                        text = dialogHeading,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = NeonPink)
                    }
                }

                // Sub-feature interactive screens
                when (dialogType) {
                    "voice" -> {
                        var isMuted by remember { mutableStateOf(false) }
                        var spokenLog by remember { mutableStateOf("Nirvaya is listening... Speak when matrix triggers.") }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Soundwave simulator
                            Row(
                                modifier = Modifier.height(60.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val infiniteTransition = rememberInfiniteTransition(label = "wave")
                                repeat(7) { index ->
                                    val waveHeight by infiniteTransition.animateFloat(
                                        initialValue = 12f,
                                        targetValue = if (isMuted) 8f else (24f + (index * 6) % 36f),
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(400 + index * 120, easing = EaseInOutSine),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "waveHeight"
                                    )
                                    val soundwaveBrush = if (isMuted) {
                                        Brush.verticalGradient(listOf(Color.Gray, Color.Gray))
                                    } else {
                                        Brush.verticalGradient(listOf(ElectricBlue, NeonPink))
                                    }
                                    Box(
                                        modifier = Modifier
                                            .width(6.dp)
                                            .height(waveHeight.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(soundwaveBrush)
                                    )
                                }
                            }

                            Text(
                                text = spokenLog,
                                color = Color(0xFFE2E8F0),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        isMuted = !isMuted
                                        spokenLog = if (isMuted) "Sovereign voice transceiver suspended [MUTED]." else "Real-time voice stream online."
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isMuted) Color.Gray else NeonPink)
                                ) {
                                    Icon(
                                        imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = CyberBlack,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isMuted) "UNMUTE MICROPHONE" else "MUTE VOICE", color = CyberBlack, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }

                                Button(
                                    onClick = {
                                        spokenLog = "Analyzing audio... Sending synthetic prompt to ${currentMode.title} core..."
                                        viewModel.speak(
                                            "System check positive. This is Nirvaya Artificial Intelligence Core. Secure quantum vocal channels verified and online."
                                        )
                                        spokenLog = "Nirvaya Speaking: 'Sovereign quantum vocal channels verified.'"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                                ) {
                                    Text("TEST SPEECH SYNTHESIS", color = CyberBlack, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    "image" -> {
                        var promptInput by remember { mutableStateOf("") }
                        var isLoadingImage by remember { mutableStateOf(false) }
                        var wasTriggered by remember { mutableStateOf(false) }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Submit structured descriptors to generate synthetic images utilizing sovereign Stable Diffusion grids.",
                                color = Color(0xFFA0AEC0),
                                fontSize = 11.sp
                            )

                            OutlinedTextField(
                                value = promptInput,
                                onValueChange = { promptInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("neon violet neural mainframe loading core, high contrast digital art...", color = Color(0xFF4A5568)) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = CyberBorder
                                )
                            )

                            Button(
                                onClick = {
                                    if (promptInput.trim().isNotEmpty()) {
                                        isLoadingImage = true
                                        wasTriggered = true
                                        kotlinx.coroutines.MainScope().launch {
                                            kotlinx.coroutines.delay(1800L)
                                            isLoadingImage = false
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                            ) {
                                Text("COMPUTE AI IMAGE", color = CyberBlack, fontWeight = FontWeight.Bold)
                            }

                            if (wasTriggered) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .border(1.dp, CyberBorder, RoundedCornerShape(12.dp)),
                                    colors = CardDefaults.cardColors(containerColor = CyberBlack),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (isLoadingImage) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                CircularProgressIndicator(color = NeonCyan, modifier = Modifier.size(24.dp))
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Text("Synthesizing pixel tensors on H100 GPU...", color = Color.Gray, fontSize = 10.sp)
                                            }
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.linearGradient(
                                                        listOf(CyberGrey, CyberMuted)
                                                    )
                                                )
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(
                                                    imageVector = Icons.Default.FilterVintage,
                                                    contentDescription = null,
                                                    tint = ElectricBlue,
                                                    modifier = Modifier.size(36.dp)
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text("GENERATED HIGH QUALITY RENDER", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text(promptInput, color = Color.Gray, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "video" -> {
                        var videoPrompt by remember { mutableStateOf("") }
                        var renderProgress by remember { mutableStateOf(0f) }
                        var isRendering by remember { mutableStateOf(false) }
                        var activeStatusText by remember { mutableStateOf("Ready to render frames.") }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Compile 4K cinematics using Sora. Render simulation outputs complete temporal frame arrays.",
                                color = Color(0xFFA0AEC0),
                                fontSize = 11.sp
                            )

                            OutlinedTextField(
                                value = videoPrompt,
                                onValueChange = { videoPrompt = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("camera pans slowly across floating luminous cities, cinematic lighting...", color = Color(0xFF4A5568)) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonPurple,
                                    unfocusedBorderColor = CyberBorder
                                )
                            )

                            Button(
                                onClick = {
                                    if (videoPrompt.trim().isNotEmpty() && !isRendering) {
                                        isRendering = true
                                        renderProgress = 0f
                                        kotlinx.coroutines.MainScope().launch {
                                            activeStatusText = "Allocating virtual compute cores..."
                                            kotlinx.coroutines.delay(650L)
                                            activeStatusText = "Drawing temporal optic Flow coordinates..."
                                            renderProgress = 0.35f
                                            kotlinx.coroutines.delay(800L)
                                            activeStatusText = "Rasterizing H100 matrix blocks..."
                                            renderProgress = 0.70f
                                            kotlinx.coroutines.delay(700L)
                                            activeStatusText = "Applying super-resolution filters..."
                                            renderProgress = 1.0f
                                            isRendering = false
                                            activeStatusText = "Render complete (1080p Cine). Video saved to cache."
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                            ) {
                                Text("COMPILE AI VIDEO", color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            if (isRendering || renderProgress > 0f) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, CyberBorder, RoundedCornerShape(12.dp)),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = CyberBlack)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        LinearProgressIndicator(
                                            progress = { renderProgress },
                                            modifier = Modifier.fillMaxWidth(),
                                            color = NeonPurple,
                                            trackColor = CyberGrey
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = activeStatusText, fontSize = 10.sp, color = ElectricBlue)
                                            Text(text = "${(renderProgress * 100).toInt()}%", fontSize = 10.sp, color = NeonCyan)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "coding" -> {
                        var targetObjective by remember { mutableStateOf("") }
                        var typingCodeResult by remember { mutableStateOf("") }
                        var isTypingInProcess by remember { mutableStateOf(false) }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Nirvaya Sandbox compiler simulates typewriter code synthesis instantly.",
                                color = Color(0xFFA0AEC0),
                                fontSize = 11.sp
                            )

                            OutlinedTextField(
                                value = targetObjective,
                                onValueChange = { targetObjective = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Write a Kotlin binary search tree solver...", color = Color(0xFF4A5568)) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = CyberBorder
                                )
                            )

                            Button(
                                onClick = {
                                    if (targetObjective.trim().isNotEmpty() && !isTypingInProcess) {
                                        isTypingInProcess = true
                                        typingCodeResult = ""
                                        val fullCode = """
                                            // COMPUTED COGNITIVE SOLVER FOR: ${targetObjective.uppercase()}
                                            class SecureMainframe {
                                                fun resolveMatrix(nodes: List<String>) {
                                                    val seed = 32845L
                                                    println("Allocated security grid...")
                                                    nodes.forEach { node ->
                                                        val hash = node.hashCode() xor seed.toInt()
                                                        Log.i("NIRVAYA", "Locking synapped: s%" + hash)
                                                    }
                                                }
                                            }
                                        """.trimIndent()
                                        
                                        kotlinx.coroutines.MainScope().launch {
                                            val lines = fullCode.split("\n")
                                            for (line in lines) {
                                                typingCodeResult += line + "\n"
                                                kotlinx.coroutines.delay(120L)
                                            }
                                            isTypingInProcess = false
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                            ) {
                                Text("RUN LIVE COMPILER SYNTAX", color = CyberBlack, fontWeight = FontWeight.Bold)
                            }

                            if (typingCodeResult.isNotEmpty()) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .border(1.dp, CyberBorder, RoundedCornerShape(8.dp)),
                                    colors = CardDefaults.cardColors(containerColor = CyberBlack)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState())
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = typingCodeResult,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                color = Color(0xFF00FFCC),
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    "notes" -> {
                        val items = remember { mutableStateListOf("Anchor context initialized.", "Query parameters synched with room database.") }
                        var noteText by remember { mutableStateOf("") }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Store and persist custom long-term learning summaries directly into local cache nodes.",
                                color = Color(0xFFA0AEC0),
                                fontSize = 11.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = noteText,
                                    onValueChange = { noteText = it },
                                    modifier = Modifier.weight(1f),
                                    placeholder = { Text("Note core fact...", color = Color(0xFF4A5568)) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ElectricBlue,
                                        unfocusedBorderColor = CyberBorder
                                    )
                                )

                                Button(
                                    onClick = {
                                        if (noteText.trim().isNotEmpty()) {
                                            items.add(noteText.trim())
                                            noteText = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                                ) {
                                    Text("SAVE", color = CyberBlack, fontWeight = FontWeight.Bold)
                                }
                            }

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items.forEach { item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(CyberGrey)
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(ElectricBlue, CircleShape)
                                        )
                                        Text(item, color = Color.White, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    "memory" -> {
                        val sessionSize = viewModel.chatSessions.collectAsState().value.size
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Nirvaya SQLite Room Database Statistics:",
                                color = Color.Gray,
                                fontSize = 10.sp
                            )

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = CyberGrey),
                                border = BorderStroke(1.dp, CyberBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("DATABASE FILE: app_database.db", color = Color.Green, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    Text("ACTIVE USER LOGGED IN: ${userName.uppercase()}", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    Text("MEMORY CHATS COUNT: $sessionSize", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    Text("KOTLIN SYMBOL CODES: Room KSP Integration", color = ElectricBlue, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    Text("ENCRYPTION METHOD: AES-256 Symmetric Virtual Lock", color = Color.LightGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                }
                            }

                            Button(
                                onClick = {
                                    kotlinx.coroutines.MainScope().launch {
                                        // Empty/clear operations
                                    }
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                modifier = Modifier.fillMaxWidth()
                              ) {
                                Text("DEFRAGMENT DATABASE & ERASE MEMORY", color = CyberBlack, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    "file" -> {
                        var isScanningFiles by remember { mutableStateOf(false) }
                        var scannnedOutput by remember { mutableStateOf<String?>(null) }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Upload files (PDF, DOCS, images) directly for deep cognitive reasoning scan simulation.",
                                color = Color(0xFFA0AEC0),
                                fontSize = 11.sp
                            )

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                                    .clickable {
                                        isScanningFiles = true
                                        scannnedOutput = null
                                        kotlinx.coroutines.MainScope().launch {
                                            kotlinx.coroutines.delay(1600L)
                                            isScanningFiles = false
                                            scannnedOutput = "OCR RESOLUTE CONSOLE:\nExtracted 2,845 tokens detailed in quantum telemetry. Summary: The reactor operates safely at 420 megawatt peak inputs..."
                                        }
                                    },
                                colors = CardDefaults.cardColors(containerColor = CyberBlack)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isScanningFiles) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            CircularProgressIndicator(color = NeonPink, modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text("LASER SCANNING OCR MATRIX LINES...", color = NeonPink, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                        }
                                    } else {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(28.dp))
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("TAP TO SELECT FILE / SIMULATE OCR", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                            Text("Supports PDF, JPG, PNG", color = Color.Gray, fontSize = 9.sp)
                                        }
                                    }
                                }
                            }

                            if (scannnedOutput != null) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = CyberGrey)
                                ) {
                                    Text(
                                        text = scannnedOutput!!,
                                        color = NeonCyan,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }

                    "marketplace" -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Enable custom nodes & modules for spatial processing mapping:",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )

                            val toolsList = remember {
                                listOf(
                                    "LaTeX Complex Solver Parser" to "Active (Integrated)",
                                    "Spanner Database Multi-Sync" to "Active (Integrated)",
                                    "Veo Cinematic Render Super-Pipe" to "Buy 100 Credit Card",
                                    "Google Secure Auth node bypass" to "Licensed Node ✔"
                                )
                            }

                            toolsList.forEach { (title, description) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CyberGrey)
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(text = "INTELLIGENCE EXTENSION", color = Color.Gray, fontSize = 8.sp)
                                    }
                                    Text(
                                        text = description,
                                        color = if (description.contains("Active") || description.contains("Licensed")) NeonCyan else NeonPink,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    "profile" -> {
                        var editedUser by remember { mutableStateOf(userName) }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Adjust user alphanumeric identifier token below. Changes update SharedPreferences cache natively.",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )

                            OutlinedTextField(
                                value = editedUser,
                                onValueChange = { editedUser = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Set handle name...", color = Color.Gray) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue)
                            )

                            Button(
                                onClick = {
                                    if (editedUser.trim().isNotEmpty()) {
                                        viewModel.userName.value = editedUser.trim()
                                    }
                                    onDismiss()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                            ) {
                                Text("COMMIT ALPHANUMERIC HANDLE", color = CyberBlack, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    else -> { // premium
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = NeonPink, modifier = Modifier.size(44.dp))
                            Text(
                                text = "NIRVAYA SOVEREIGN GRID",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Upgrade node to bypass local cache limits. Allocate maximum parallel compute threads on sovereign H100 arrays without rate limits.",
                                color = Color.Gray,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )

                            HorizontalDivider(color = CyberBorder, thickness = 1.dp)

                            Text(
                                text = "✔ Unlimited Context Window Memory Cells\n✔ Low-latency GPU processing pipelines\n✔ Voice sync streaming audio synthesis\n✔ Advanced OCR PDF multi-batch scanning",
                                color = Color(0xFFE2E8F0),
                                fontSize = 11.sp,
                                lineHeight = 18.sp,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            Button(
                                onClick = { onDismiss() },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                            ) {
                                Text("SECURE SOVEREIGN LICENSE ACCESS", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
