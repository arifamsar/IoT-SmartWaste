package com.arfsar.smarttrash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arfsar.smarttrash.ui.theme.SmartTrashTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val sensorViewModel by viewModel<SensorViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartTrashTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center,

                    ) {
                        TrashBinDemo(
                            capacity = sensorViewModel.capacity,
                            timestamp = sensorViewModel.timestamp,
                            nh3 = sensorViewModel.nh3,
                            co2 = sensorViewModel.co2,
                            acetone = sensorViewModel.acetone,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrashBin(capacityPercentage: Float) {
    val shakeAnimation = remember { Animatable(0f) }
    val lidAngle = remember { Animatable(0f) }
    val fillColor = remember(capacityPercentage) {
        when {
            capacityPercentage >= 80 -> Color.Red
            capacityPercentage >= 50 -> Color.Yellow
            else -> Color.Green
        }
    }

    LaunchedEffect(capacityPercentage) {
        if (capacityPercentage >= 80) {
            // Shake the bin when it's full
            shakeAnimation.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            shakeAnimation.animateTo(0f)
        }

        // Open lid slightly for animation
        lidAngle.animateTo(15f)
        lidAngle.animateTo(0f)
    }

    Box(contentAlignment = Alignment.TopCenter) {
        // Trash lid
        Canvas(
            modifier = Modifier
                .size(150.dp)
                .padding(16.dp)
                .graphicsLayer {
                    rotationZ = lidAngle.value
                    translationX = shakeAnimation.value * 10f
                }
        ) {
            val lidWidth = size.width * 0.7f
            val lidHeight = size.height * 0.1f
            val lidLeft = (size.width - lidWidth) / 2
            val lidTop = size.height * 0.05f

            drawRect(
                color = Color.Gray,
                topLeft = Offset(lidLeft, lidTop),
                size = Size(lidWidth, lidHeight)
            )
        }

        // Bin body
        Canvas(
            modifier = Modifier
                .size(150.dp)
                .padding(16.dp)
                .graphicsLayer {
                    translationX = shakeAnimation.value * 10f
                }
        ) {
            // Bin dimensions
            val binWidth = size.width * 0.6f
            val binHeight = size.height * 0.8f
            val binLeft = (size.width - binWidth) / 2
            val binTop = size.height * 0.1f

            // Draw bin outline
            drawRect(
                color = Color.Black,
                topLeft = Offset(binLeft, binTop),
                size = Size(binWidth, binHeight),
                style = Stroke(width = 4f),
            )

            // Gradient fill
            val gradientFill = androidx.compose.ui.graphics.Brush.verticalGradient(
                colors = listOf(fillColor.copy(alpha = 0.6f), fillColor)
            )
            val fillHeight = binHeight * (capacityPercentage / 100)
            drawRect(
                brush = gradientFill,
                topLeft = Offset(binLeft, binTop + binHeight - fillHeight),
                size = Size(binWidth, fillHeight)
            )
        }

        // Floating trash animation
        if (capacityPercentage >= 80) {
            DynamicFloatingTrashAnimation()
        }
    }
}

@Composable
fun FloatingTrashAnimation() {
    val trashOffsets = listOf(
        remember { Animatable(0f) },
        remember { Animatable(0f) },
        remember { Animatable(0f) }
    )

    LaunchedEffect(Unit) {
        trashOffsets.forEach { offset ->
            offset.animateTo(
                targetValue = -100f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
            )
            offset.snapTo(0f)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        trashOffsets.forEach { offset ->
            Canvas(
                modifier = Modifier
                    .size(30.dp)
                    .graphicsLayer {
                        translationY = offset.value
                    }
                    .align(Alignment.Center)
            ) {
                drawCircle(color = Color.Gray)
            }
        }
    }
}

@Composable
fun DynamicFloatingTrashAnimation() {
    val floatingShapes = List(5) {
        remember {
            FloatingTrashState(
                offsetX = Animatable(0f),
                offsetY = Animatable(0f),
                scale = Animatable(1f),
                alpha = Animatable(1f)
            )
        }
    }

    LaunchedEffect(Unit) {
        floatingShapes.forEach { shape ->
            launch {
                while (true) {
                    // Reset animation
                    shape.offsetX.snapTo(((-30..30).random()).toFloat())
                    shape.offsetY.snapTo(0f)
                    shape.scale.snapTo(1f)
                    shape.alpha.snapTo(1f)

                    // Animate upward with rotation and fading
                    launch {
                        shape.offsetY.animateTo(
                            targetValue = -200f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    }
                    launch {
                        shape.offsetX.animateTo(
                            targetValue = (shape.offsetX.value + (-30..30).random()).toFloat(),
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    }
                    launch {
                        shape.scale.animateTo(
                            targetValue = 1.5f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    }
                    launch {
                        shape.alpha.animateTo(0f, animationSpec = spring(stiffness = Spring.StiffnessVeryLow))
                    }

                    // Wait before repeating
                    delay((500..1000).random().toLong())
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp), // Adjust to sit above the trash bin
        contentAlignment = Alignment.Center
    ) {
        floatingShapes.forEach { shape ->
            Canvas(
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer {
                        translationX = shape.offsetX.value
                        translationY = shape.offsetY.value
                        scaleX = shape.scale.value
                        scaleY = shape.scale.value
                        alpha = shape.alpha.value
                    }
            ) {
                drawCircle(
                    color = Color(
                        red = (150..255).random() / 255f,
                        green = (100..200).random() / 255f,
                        blue = (50..150).random() / 255f,
                        alpha = 0.8f
                    ),
                    radius = size.width / 2
                )
            }
        }
    }
}

data class FloatingTrashState(
    val offsetX: Animatable<Float, AnimationVector1D>,
    val offsetY: Animatable<Float, AnimationVector1D>,
    val scale: Animatable<Float, AnimationVector1D>,
    val alpha: Animatable<Float, AnimationVector1D>
)

@Composable
fun TrashBinDemo(
    capacity: Float,
    timestamp: String,
    nh3: Float,
    co2: Float,
    acetone: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TrashBin(capacityPercentage = capacity)

        Card(
            modifier = Modifier
                .padding(16.dp)
                .clip(MaterialTheme.shapes.medium)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Kapasitas: ${capacity.toInt()}%",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Terakhir diperbarui: $timestamp",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (capacity >= 80) "Tong sampah Anda penuh" else if (capacity >= 50) "Tong sampah Anda hampir penuh" else "Tong sampah Anda masih kosong",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = if (capacity >= 80) Color.Red else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (nh3 > 10) {
                    Text(
                        text = "Warning: NH3 level is high!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red
                    )
                } else
                    Text(
                        text = "NH3: $nh3 ppm",
                        style = MaterialTheme.typography.bodyMedium
                    )
                if (co2 > 1000) {
                    Text(
                        text = "Warning: CO2 level is high!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red
                    )
                } else
                    Text(
                        text = "CO2: $co2 ppm",
                        style = MaterialTheme.typography.bodyMedium
                    )
                if (acetone > 5) {
                    Text(
                        text = "Warning: Acetone level is high!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red
                    )
                } else
                    Text(
                        text = "Acetone: $acetone ppm",
                        style = MaterialTheme.typography.bodyMedium
                    )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SmartTrashTheme {
        TrashBinDemo(
            capacity = 50f,
            timestamp = "12:00",
            nh3 = 0f,
            co2 = 0f,
            acetone = 0f
        )
    }
}