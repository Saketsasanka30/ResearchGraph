package com.example.researchgraph.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.researchgraph.viewmodel.ResearchViewModel

@Composable
fun ResearchSearchBar(
    viewModel: ResearchViewModel,
    modifier: Modifier = Modifier,
    onResultSelected: (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current
    var queryText by remember { mutableStateOf("") }
    val isFetching by viewModel.isAiFetchingResearch.collectAsState()
    val isNetworkSimulated = viewModel.isSimulatingNetworkFailure()

    val suggestedKeywords = listOf(
        "Self-Attention Efficiency",
        "Graph Neural Networks",
        "Retrieval-Augmented Generation",
        "Neuro-Symbolic Reasoning",
        "FlashAttention Kernel",
        "Diffusion Probabilistic Models"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("research_search_bar_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row with Title and Network Simulation Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = "AI Research Query",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Scholarly Research Query",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Error simulation toggle button to test failure Snackbar
                AssistChip(
                    onClick = { viewModel.toggleSimulateNetworkFailure() },
                    label = {
                        Text(
                            text = if (isNetworkSimulated) "Offline Test: ON" else "Test Network Fail",
                            fontSize = 11.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            if (isNetworkSimulated) Icons.Default.WifiOff else Icons.Default.Wifi,
                            contentDescription = "Toggle Network Simulation",
                            modifier = Modifier.size(14.dp),
                            tint = if (isNetworkSimulated) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (isNetworkSimulated) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.testTag("test_network_error_chip")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Input Field
            OutlinedTextField(
                value = queryText,
                onValueChange = {
                    queryText = it
                    // Also filter active screen items
                    viewModel.searchQuery.value = it
                },
                placeholder = {
                    Text(
                        "Input research keywords or topic...",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search Icon", tint = MaterialTheme.colorScheme.primary)
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (queryText.isNotBlank()) {
                            IconButton(onClick = {
                                queryText = ""
                                viewModel.searchQuery.value = ""
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear Input", modifier = Modifier.size(18.dp))
                            }
                        }

                        // Prominent Query AI Button
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.queryAiResearchMaterials(queryText)
                            },
                            enabled = !isFetching && queryText.isNotBlank(),
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .testTag("query_research_button"),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            if (isFetching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Query AI", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("research_query_input"),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        if (queryText.isNotBlank()) {
                            viewModel.queryAiResearchMaterials(queryText)
                        }
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Animated Visual Feedback while Fetching Data from AI
            AnimatedVisibility(
                visible = isFetching,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .testTag("ai_fetching_indicator_card")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.5.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Synthesizing research materials with Gemini AI...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "Extracting paradigms, methodologies, and open questions",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            // Keyword Suggestion Chips
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Try:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                )
                suggestedKeywords.forEach { kw ->
                    SuggestionChip(
                        onClick = {
                            queryText = kw
                            viewModel.searchQuery.value = kw
                            focusManager.clearFocus()
                            viewModel.queryAiResearchMaterials(kw)
                        },
                        label = { Text(kw, fontSize = 11.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(28.dp)
                    )
                }
            }
        }
    }
}
