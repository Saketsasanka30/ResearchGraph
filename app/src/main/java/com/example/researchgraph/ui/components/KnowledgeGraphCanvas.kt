package com.example.researchgraph.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.researchgraph.model.GraphEdge
import com.example.researchgraph.model.GraphNode
import com.example.researchgraph.model.NodeType
import com.example.researchgraph.viewmodel.AppDestination
import com.example.researchgraph.viewmodel.ResearchViewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun getNodeColor(type: NodeType): Color {
    return when (type) {
        NodeType.PAPER -> Color(0xFF4F46E5)
        NodeType.AUTHOR -> Color(0xFF0D9488)
        NodeType.CONCEPT -> Color(0xFF9333EA)
        NodeType.TOPIC -> Color(0xFFD97706)
        NodeType.CITATION -> Color(0xFFE11D48)
        NodeType.NOTE -> Color(0xFF059669)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeGraphCanvas(
    viewModel: ResearchViewModel,
    modifier: Modifier = Modifier
) {
    val allNodes by viewModel.graphNodes.collectAsState()
    val allEdges by viewModel.graphEdges.collectAsState()
    val visibleTypes by viewModel.visibleNodeTypes.collectAsState()
    val selectedNodeId by viewModel.selectedGraphNodeId.collectAsState()
    val searchQuery by viewModel.graphSearchQuery.collectAsState()

    var scale by remember { mutableStateOf(1.0f) }
    var panOffset by remember { mutableStateOf(Offset(0f, 0f)) }

    // Filter nodes according to visible types and search query
    val activeNodes = remember(allNodes, visibleTypes, searchQuery) {
        allNodes.filter { node ->
            visibleTypes.contains(node.type) &&
                    (searchQuery.isBlank() || node.label.contains(searchQuery, ignoreCase = true) ||
                            (node.subLabel?.contains(searchQuery, ignoreCase = true) == true))
        }
    }

    val activeNodeIds = remember(activeNodes) { activeNodes.map { it.id }.toSet() }

    val activeEdges = remember(allEdges, activeNodeIds) {
        allEdges.filter { activeNodeIds.contains(it.fromNodeId) && activeNodeIds.contains(it.toNodeId) }
    }

    val selectedNode = remember(allNodes, selectedNodeId) {
        allNodes.find { it.id == selectedNodeId }
    }

    val connectedEdges = remember(allEdges, selectedNodeId) {
        if (selectedNodeId == null) emptyList()
        else allEdges.filter { it.fromNodeId == selectedNodeId || it.toNodeId == selectedNodeId }
    }

    val connectedNodeIds = remember(connectedEdges, selectedNodeId) {
        connectedEdges.flatMap { listOf(it.fromNodeId, it.toNodeId) }.toSet()
    }

    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val edgeColor = if (isDark) Color(0x5594A3B8) else Color(0x4464748B)
    val edgeHighlightColor = MaterialTheme.colorScheme.primary
    val textPaint = remember(isDark) {
        Paint().apply {
            isAntiAlias = true
            textSize = 28f
            color = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.DKGRAY
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }
    }
    val edgeTextPaint = remember(isDark) {
        Paint().apply {
            isAntiAlias = true
            textSize = 20f
            color = if (isDark) android.graphics.Color.LTGRAY else android.graphics.Color.GRAY
            textAlign = Paint.Align.CENTER
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Main interactive Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("knowledge_graph_canvas")
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.4f, 3.0f)
                        panOffset += pan
                    }
                }
                .pointerInput(activeNodes, scale, panOffset) {
                    detectTapGestures { tapOffset ->
                        // Convert screen tap to world coordinates
                        val worldX = (tapOffset.x - panOffset.x) / scale
                        val worldY = (tapOffset.y - panOffset.y) / scale

                        // Find closest node within its hit radius
                        val hit = activeNodes.find { node ->
                            val dx = node.x - worldX
                            val dy = node.y - worldY
                            sqrt(dx * dx + dy * dy) <= (node.radius + 15f)
                        }
                        viewModel.selectGraphNode(hit?.id)
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Center base offset
            val baseOffsetX = canvasWidth / 2f + panOffset.x
            val baseOffsetY = canvasHeight / 2f + panOffset.y

            // Draw subtle background grid
            drawGraphGrid(panOffset, scale, size.width, size.height, isDark)

            // Draw Edges
            activeEdges.forEach { edge ->
                val fromNode = activeNodes.find { it.id == edge.fromNodeId }
                val toNode = activeNodes.find { it.id == edge.toNodeId }

                if (fromNode != null && toNode != null) {
                    val startX = baseOffsetX + (fromNode.x - 400f) * scale
                    val startY = baseOffsetY + (fromNode.y - 400f) * scale
                    val endX = baseOffsetX + (toNode.x - 400f) * scale
                    val endY = baseOffsetY + (toNode.y - 400f) * scale

                    val isConnectedToSelected = selectedNodeId != null &&
                            (edge.fromNodeId == selectedNodeId || edge.toNodeId == selectedNodeId)

                    val strokeWidth = if (isConnectedToSelected) 4.dp.toPx() else 1.5.dp.toPx()
                    val lineClr = if (isConnectedToSelected) edgeHighlightColor else edgeColor

                    drawLine(
                        color = lineClr,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = strokeWidth
                    )

                    // Draw relationship text on selected or connected edges
                    if (scale > 0.8f && (isConnectedToSelected || selectedNodeId == null)) {
                        val midX = (startX + endX) / 2f
                        val midY = (startY + endY) / 2f
                        drawContext.canvas.nativeCanvas.drawText(
                            edge.relationship.replace("_", " "),
                            midX,
                            midY - 6f,
                            edgeTextPaint
                        )
                    }
                }
            }

            // Draw Nodes
            activeNodes.forEach { node ->
                val nodeCenterX = baseOffsetX + (node.x - 400f) * scale
                val nodeCenterY = baseOffsetY + (node.y - 400f) * scale
                val nodeRadius = node.radius * scale

                val isSelected = node.id == selectedNodeId
                val isConnected = connectedNodeIds.contains(node.id)
                val nodeColor = getNodeColor(node.type)

                // Outer aura/glow for selected node
                if (isSelected) {
                    drawCircle(
                        color = nodeColor.copy(alpha = 0.35f),
                        radius = nodeRadius + 14f * scale,
                        center = Offset(nodeCenterX, nodeCenterY)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.8f),
                        radius = nodeRadius + 4f * scale,
                        center = Offset(nodeCenterX, nodeCenterY),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f * scale)
                    )
                } else if (isConnected && selectedNodeId != null) {
                    drawCircle(
                        color = nodeColor.copy(alpha = 0.2f),
                        radius = nodeRadius + 8f * scale,
                        center = Offset(nodeCenterX, nodeCenterY)
                    )
                }

                // Node Body
                drawCircle(
                    color = nodeColor,
                    radius = nodeRadius,
                    center = Offset(nodeCenterX, nodeCenterY)
                )

                // White inner border
                drawCircle(
                    color = Color.White.copy(alpha = 0.85f),
                    radius = nodeRadius,
                    center = Offset(nodeCenterX, nodeCenterY),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f * scale)
                )

                // Node Text Label
                if (scale > 0.6f) {
                    val labelText = if (node.label.length > 18) node.label.take(16) + "…" else node.label
                    textPaint.textSize = (11f * scale).coerceIn(16f, 32f)
                    drawContext.canvas.nativeCanvas.drawText(
                        labelText,
                        nodeCenterX,
                        nodeCenterY + nodeRadius + 22f * scale,
                        textPaint
                    )
                }
            }
        }

        // Top Filter Bar (Legend & Toggles)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                NodeType.values().forEach { type ->
                    val isChecked = visibleTypes.contains(type)
                    val typeColor = getNodeColor(type)
                    FilterChip(
                        selected = isChecked,
                        onClick = { viewModel.toggleNodeTypeFilter(type) },
                        label = {
                            Text(
                                text = type.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                            )
                        },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(typeColor, CircleShape)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = typeColor.copy(alpha = 0.25f),
                            selectedLabelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }

        // Zoom & Pan Controls Floating buttons
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 60.dp, end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SmallFloatingActionButton(
                onClick = { scale = (scale * 1.25f).coerceAtMost(3.0f) },
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.testTag("graph_zoom_in")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In")
            }
            SmallFloatingActionButton(
                onClick = { scale = (scale * 0.8f).coerceAtLeast(0.4f) },
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.testTag("graph_zoom_out")
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out")
            }
            SmallFloatingActionButton(
                onClick = {
                    scale = 1.0f
                    panOffset = Offset(0f, 0f)
                },
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.testTag("graph_reset_view")
            ) {
                Icon(Icons.Default.CenterFocusStrong, contentDescription = "Center View")
            }
        }

        // Selected Node Inspector Card at bottom
        if (selectedNode != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
                    .testTag("node_details_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(getNodeColor(selectedNode.type), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = selectedNode.type.displayName.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = getNodeColor(selectedNode.type),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = { viewModel.selectGraphNode(null) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedNode.label,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    selectedNode.subLabel?.let { sub ->
                        Text(
                            text = sub,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    // Connected relationships
                    if (connectedEdges.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${connectedEdges.size} Relational Connections:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            connectedEdges.take(5).forEach { edge ->
                                val targetId = if (edge.fromNodeId == selectedNode.id) edge.toNodeId else edge.fromNodeId
                                val targetNode = allNodes.find { it.id == targetId }
                                AssistChip(
                                    onClick = { viewModel.selectGraphNode(targetId) },
                                    label = {
                                        Text(
                                            text = "${edge.relationship.replace('_', ' ')} → ${targetNode?.label?.take(14) ?: targetId}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                                        )
                                    }
                                )
                            }
                        }
                    }

                    // Actions
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (selectedNode.relatedPaperId != null) {
                            Button(
                                onClick = {
                                    viewModel.openPaperInReader(selectedNode.relatedPaperId)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("open_paper_from_graph_btn")
                            ) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Read Paper")
                            }

                            val paper = allNodes.find { it.id == selectedNode.relatedPaperId }?.let { n ->
                                viewModel.papers.value.find { it.id == n.id }
                            }
                            if (paper != null) {
                                OutlinedButton(
                                    onClick = { viewModel.openCitationModal(paper) },
                                    modifier = Modifier.testTag("cite_from_graph_btn")
                                ) {
                                    Icon(Icons.Default.FormatQuote, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Cite")
                                }
                            }
                        } else {
                            FilledTonalButton(
                                onClick = {
                                    viewModel.showAddNoteDialog.value = true
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Annotate Concept")
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawGraphGrid(
    pan: Offset,
    scale: Float,
    width: Float,
    height: Float,
    isDark: Boolean
) {
    val gridSpacing = 40f * scale
    val dotColor = if (isDark) Color(0x22FFFFFF) else Color(0x18000000)

    val startX = (pan.x % gridSpacing)
    val startY = (pan.y % gridSpacing)

    var x = startX
    while (x < width) {
        var y = startY
        while (y < height) {
            drawCircle(dotColor, radius = 1.2f, center = Offset(x, y))
            y += gridSpacing
        }
        x += gridSpacing
    }
}
