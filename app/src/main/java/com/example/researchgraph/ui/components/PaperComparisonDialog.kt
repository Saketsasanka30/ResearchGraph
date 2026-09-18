package com.example.researchgraph.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.researchgraph.model.Paper
import com.example.researchgraph.viewmodel.ResearchViewModel

@Composable
fun PaperComparisonDialog(
    viewModel: ResearchViewModel,
    onDismiss: () -> Unit
) {
    val papers by viewModel.papers.collectAsState()
    var selectedAIndex by remember { mutableStateOf(0) }
    var selectedBIndex by remember { mutableStateOf(if (papers.size > 1) 1 else 0) }

    val isComparing by viewModel.isAiComparing.collectAsState()
    val comparisonResult by viewModel.paperComparisonResult.collectAsState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .padding(8.dp)
                .testTag("paper_comparison_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.CompareArrows,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI Paper Comparison",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Synthesize methodology & findings side-by-side",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Paper Selection Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Paper A Selector
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Paper A:", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary))
                            Text(
                                text = papers.getOrNull(selectedAIndex)?.title ?: "Select",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                papers.forEachIndexed { idx, p ->
                                    if (idx != selectedBIndex) {
                                        AssistChip(
                                            onClick = { selectedAIndex = idx },
                                            label = { Text("#${idx + 1}") },
                                            colors = AssistChipDefaults.assistChipColors(
                                                containerColor = if (selectedAIndex == idx) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Paper B Selector
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Paper B:", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.secondary))
                            Text(
                                text = papers.getOrNull(selectedBIndex)?.title ?: "Select",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                papers.forEachIndexed { idx, p ->
                                    if (idx != selectedAIndex) {
                                        AssistChip(
                                            onClick = { selectedBIndex = idx },
                                            label = { Text("#${idx + 1}") },
                                            colors = AssistChipDefaults.assistChipColors(
                                                containerColor = if (selectedBIndex == idx) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Compare Trigger Button
                Button(
                    onClick = {
                        val pA = papers.getOrNull(selectedAIndex)
                        val pB = papers.getOrNull(selectedBIndex)
                        if (pA != null && pB != null) {
                            viewModel.triggerComparePapers(pA, pB)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("execute_compare_button"),
                    enabled = !isComparing && selectedAIndex != selectedBIndex
                ) {
                    if (isComparing) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Synthesizing with AI...")
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Synthesize Comparison")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Results Container
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(14.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (comparisonResult != null) {
                        val res = comparisonResult!!

                        // Synthesis Summary
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Synthesis Summary", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(res.synthesisSummary, style = MaterialTheme.typography.bodyMedium)
                            }
                        }

                        // Common Ground
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Shared Theoretical & Empirical Ground", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                                Spacer(modifier = Modifier.height(6.dp))
                                res.commonGround.forEach { pt ->
                                    Row(modifier = Modifier.padding(vertical = 3.dp)) {
                                        Text("• ", fontWeight = FontWeight.Bold)
                                        Text(pt, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }

                        // Contrasting Methodologies
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Methodological Divergence", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary))
                                Spacer(modifier = Modifier.height(6.dp))
                                res.contrastingMethodologies.forEach { pt ->
                                    Row(modifier = Modifier.padding(vertical = 3.dp)) {
                                        Text("• ", fontWeight = FontWeight.Bold)
                                        Text(pt, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }

                        // Synergy Opportunities
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Synergy & Future Research Frontiers", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary))
                                Spacer(modifier = Modifier.height(6.dp))
                                res.synergyOpportunities.forEach { pt ->
                                    Row(modifier = Modifier.padding(vertical = 3.dp)) {
                                        Text("• ", fontWeight = FontWeight.Bold)
                                        Text(pt, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CompareArrows, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Select two papers and tap 'Synthesize Comparison'",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
