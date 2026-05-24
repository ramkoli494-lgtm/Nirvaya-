package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.NirvayaViewModel

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val featureHeading: String,
    val features: List<String>
)

@Composable
fun OnboardingScreen(
    viewModel: NirvayaViewModel,
    modifier: Modifier = Modifier
) {
    var currentPageIndex by remember { mutableStateOf(0) }
    
    val pages = remember {
        listOf(
            OnboardingPage(
                title = "QUANTUM COGNITION CORE",
                description = "Nirvaya AI operates beyond traditional helper lines. Access superintelligent reasoning at sub-second speeds.",
                icon = Icons.Default.Memory,
                color = ElectricBlue,
                featureHeading = "NEURAL CAPABILITIES",
                features = listOf("Multiple customizable personality cores", "Real-time thinking & reasoning logs", "Smart local memory systems")
            ),
            OnboardingPage(
                title = "MULTIMODAL SYNAPSE",
                description = "Streamline files, voice commands, math formulation, code synthesis, and images directly into a unified reasoning pipeline.",
                icon = Icons.Default.Mic,
                color = NeonPink,
                featureHeading = "INPUT CHANNELS",
                features = listOf("High fidelity voice synthesis & TTS", "OCR PDF document scans & extraction", "Dall-E style image creators")
            ),
            OnboardingPage(
                title = "AUTONOMOUS AGENT ACTIONS",
                description = "Deploy modular agent routines to research topics, write scripts, build complete layouts, and generate synthetic ideas.",
                icon = Icons.Default.Code,
                color = NeonCyan,
                featureHeading = "WORKPLACE PLUGINS",
                features = listOf("Mathematical LaTeX resolvers", "Integrated code compiler simulation", "Global real-time web search nodes")
            )
        )
    }

    val page = pages[currentPageIndex]

    // Breathing pulse for geometric background glow
    val infiniteTransition = rememberInfiniteTransition(label = "onboard_pulse")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 180f,
        targetValue = 260f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

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
            // Ambient glowing radial backgrounds behind elements
            Box(
                modifier = Modifier
                    .size(glowScale.dp)
                    .align(Alignment.TopCenter)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                page.color.copy(alpha = 0.12f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header logo
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = page.color,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "NIRVAYA AI",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = Color.White
                        )
                    )
                }

                // Main Slider content (Crossfade transition)
                Crossfade(
                    targetState = page,
                    animationSpec = tween(500, easing = EaseInOutQuart),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    label = "page_transition"
                ) { currentPage ->
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Holographic Frame for Icon
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .border(
                                    1.dp,
                                    Brush.linearGradient(listOf(currentPage.color, CyberBorder)),
                                    RoundedCornerShape(24.dp)
                                )
                                .clip(RoundedCornerShape(24.dp))
                                .background(CyberDark)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = currentPage.icon,
                                contentDescription = null,
                                tint = currentPage.color,
                                modifier = Modifier.size(52.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // Text block
                        Text(
                            text = currentPage.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = Color.White
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = currentPage.description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFFA0AEC0),
                                lineHeight = 22.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Capabilities Panel list
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CyberBorder.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = CyberDark.copy(alpha = 0.6f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = currentPage.featureHeading,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = currentPage.color,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                                currentPage.features.forEach { item ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(currentPage.color, CircleShape)
                                        )
                                        Text(
                                            text = item,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFFE2E8F0),
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Controls Row (Indicators and Navigation Buttons)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Page Indicator Dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        pages.forEachIndexed { index, _ ->
                            val active = index == currentPageIndex
                            val width by animateDpAsState(
                                targetValue = if (active) 24.dp else 8.dp,
                                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                label = "dot_width"
                            )
                            Box(
                                modifier = Modifier
                                    .size(height = 8.dp, width = width)
                                    .clip(CircleShape)
                                    .background(if (active) page.color else CyberBorder)
                                    .clickable { currentPageIndex = index }
                            )
                        }
                    }

                    // Button Action bars
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Skip text btn
                        TextButton(
                            onClick = { viewModel.completeOnboarding() },
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("onboarding_skip_button"),
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF718096))
                        ) {
                            Text(
                                text = "SKIP CORE",
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp,
                                fontSize = 12.sp
                            )
                        }

                        // Next Core / Get Started
                        Button(
                            onClick = {
                                if (currentPageIndex < pages.lastIndex) {
                                    currentPageIndex++
                                } else {
                                    viewModel.completeOnboarding()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("onboarding_next_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = page.color)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(horizontal = 4.dp)
                            ) {
                                Text(
                                    text = if (currentPageIndex == pages.lastIndex) "COMMENCE CORE" else "NEXT MODULE",
                                    color = CyberBlack,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    fontSize = 12.sp
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = CyberBlack,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
