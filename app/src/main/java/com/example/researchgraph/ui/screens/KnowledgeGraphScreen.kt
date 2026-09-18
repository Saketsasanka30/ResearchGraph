package com.example.researchgraph.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.researchgraph.ui.components.KnowledgeGraphCanvas
import com.example.researchgraph.viewmodel.ResearchViewModel

@Composable
fun KnowledgeGraphScreen(
    viewModel: ResearchViewModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("knowledge_graph_screen")
    ) {
        KnowledgeGraphCanvas(
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize()
        )
    }
}
