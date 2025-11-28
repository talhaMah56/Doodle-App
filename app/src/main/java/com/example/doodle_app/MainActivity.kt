package com.example.doodle_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doodle_app.ui.theme.DoodleappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoodleappTheme {
                DoodleApp()
            }
        }
    }
}

data class DrawPath(
    val path: Path,
    val color: Color,
    val strokeWidth: Float
)

@Composable
fun DoodleApp() {
    var brushSize by remember { mutableFloatStateOf(10f) }
    var brushColor by remember { mutableStateOf(Color.Black) }
    val paths = remember { mutableStateListOf<DrawPath>() }
    val undoStack = remember { mutableStateListOf<DrawPath>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F5F5))
        ) {
            // Tool Panel
            ToolPanel(
                brushSize = brushSize,
                onBrushSizeChange = { brushSize = it },
                brushColor = brushColor,
                onBrushColorChange = { brushColor = it },
                onClear = {
                    undoStack.addAll(paths)
                    paths.clear()
                },
                onUndo = {
                    if (paths.isNotEmpty()) {
                        val lastPath = paths.removeAt(paths.size - 1)
                        undoStack.add(lastPath)
                    }
                },
                onRedo = {
                    if (undoStack.isNotEmpty()) {
                        val redoPath = undoStack.removeAt(undoStack.size - 1)
                        paths.add(redoPath)
                    }
                },
                canUndo = paths.isNotEmpty(),
                canRedo = undoStack.isNotEmpty()
            )

            // Drawing Canvas
            DrawingCanvas(
                paths = paths,
                currentPath = currentPath,
                brushColor = brushColor,
                brushSize = brushSize,
                onDrawStart = { offset ->
                    currentPath = Path().apply { moveTo(offset.x, offset.y) }
                    undoStack.clear()
                },
                onDraw = { offset ->
                    currentPath?.lineTo(offset.x, offset.y)
                },
                onDrawEnd = {
                    currentPath?.let { path ->
                        paths.add(DrawPath(path, brushColor, brushSize))
                    }
                    currentPath = null
                }
            )
        }
    }
}

@Composable
fun ToolPanel(
    brushSize: Float,
    onBrushSizeChange: (Float) -> Unit,
    brushColor: Color,
    onBrushColorChange: (Color) -> Unit,
    onClear: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    canUndo: Boolean,
    canRedo: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Doodle Tools",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Brush Size Control
            Text(
                text = "Brush Size: ${brushSize.toInt()}px",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Slider(
                value = brushSize,
                onValueChange = onBrushSizeChange,
                valueRange = 5f..50f,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Color Palette
            Text(
                text = "Brush Color:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val colors = listOf(
                    Color.Black, Color.Red, Color.Blue, Color.Green,
                    Color.Yellow, Color.Magenta, Color.Cyan, Color(0xFFFF9800)
                )
                colors.forEach { color ->
                    ColorButton(
                        color = color,
                        isSelected = brushColor == color,
                        onClick = { onBrushColorChange(color) }
                    )
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onUndo,
                    enabled = canUndo,
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                ) {
                    Text("Undo")
                }
                Button(
                    onClick = onRedo,
                    enabled = canRedo,
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                ) {
                    Text("Redo")
                }
                Button(
                    onClick = onClear,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                ) {
                    Text("Clear")
                }
            }
        }
    }
}

@Composable
fun ColorButton(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(40.dp)
            .padding(4.dp),
        shape = CircleShape,
        color = color,
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
        } else null,
        onClick = onClick
    ) {}
}

@Composable
fun DrawingCanvas(
    paths: List<DrawPath>,
    currentPath: Path?,
    brushColor: Color,
    brushSize: Float,
    onDrawStart: (Offset) -> Unit,
    onDraw: (Offset) -> Unit,
    onDrawEnd: () -> Unit
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset -> onDrawStart(offset) },
                    onDrag = { change, _ ->
                        change.consume()
                        onDraw(change.position)
                    },
                    onDragEnd = { onDrawEnd() }
                )
            }
    ) {
        // Draw all completed paths
        paths.forEach { drawPath ->
            drawPath(
                path = drawPath.path,
                color = drawPath.color,
                style = Stroke(
                    width = drawPath.strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        // Draw current path being drawn
        currentPath?.let { path ->
            drawPath(
                path = path,
                color = brushColor,
                style = Stroke(
                    width = brushSize,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}