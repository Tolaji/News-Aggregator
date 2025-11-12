package com.myfirsteverapp.newsaggregator.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.myfirsteverapp.newsaggregator.R
import kotlinx.coroutines.delay

// Weflut Brand Colors
private val WeflutCyan = Color(0xFF00BCD4)
private val WeflutPurple = Color(0xFF9C27B0)
private val WeflutBlue = Color(0xFF03A9F4)
private val WeflutDarkNavy = Color(0xFF0A1929)
private val WeflutDeepBlue = Color(0xFF132F4C)

@Composable
fun SplashScreen(
    onNavigateToMain: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    var countdown by remember { mutableStateOf(3) }

    // Countdown timer
    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000)
            countdown--
        }

        if (isAuthenticated) {
            onNavigateToMain()
        } else {
            onNavigateToLogin()
        }
    }

    // Animated logo scale
    val infiniteTransition = rememberInfiniteTransition(label = "splash_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_animation"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha_animation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        WeflutDarkNavy,
                        WeflutDeepBlue,
                        Color(0xFF1A2332)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Weflut Logo
            Image(
                painter = painterResource(id = R.drawable.weflut),
                contentDescription = "Weflut Logo",
                modifier = Modifier
                    .size((180 * scale).dp)
                    .padding(16.dp),
                alpha = alpha
            )

            // Brand name with gradient effect
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Weflut",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge
                )

                Text(
                    text = "LIVE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = WeflutCyan,
                    letterSpacing = 6.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Loading indicator with brand colors
            LinearProgressIndicator(
                modifier = Modifier
                    .width(200.dp)
                    .height(4.dp),
                color = WeflutCyan,
                trackColor = Color.White.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Countdown text
            Text(
                text = "Loading in $countdown...",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Light
            )
        }

        // Tagline at bottom
        Text(
            text = "Stay Informed, Stay Connected",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.5f),
            fontWeight = FontWeight.Light,
            letterSpacing = 1.sp
        )
    }
}