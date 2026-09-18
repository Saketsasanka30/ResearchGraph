package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.researchgraph.ui.components.*
import com.example.researchgraph.ui.screens.*
import com.example.researchgraph.viewmodel.AppDestination
import com.example.researchgraph.viewmodel.ResearchViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ResearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ResearchGraphApp(viewModel = viewModel)
            }
        }
    }
}

data class NavItem(
    val destination: AppDestination,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResearchGraphApp(viewModel: ResearchViewModel) {
    val currentDestination by viewModel.currentDestination.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val currentAlert by viewModel.snackbarAlert.collectAsState()

    // Dialog state collectors
    val showAddSource by viewModel.showAddSourceDialog.collectAsState()
    val showAddNote by viewModel.showAddNoteDialog.collectAsState()
    val showAddTopic by viewModel.showAddTopicDialog.collectAsState()
    val showCompare by viewModel.showCompareDialog.collectAsState()
    val showCitation by viewModel.showCitationModal.collectAsState()
    val showLitReview by viewModel.showLitReviewDialog.collectAsState()
    val showNewQuestion by viewModel.showNewQuestionDialog.collectAsState()
    val showResearchResult by viewModel.showResearchResultDialog.collectAsState()
    val lastResearchResult by viewModel.lastAiResearchResult.collectAsState()

    // Show Snackbar notification system when errors or alerts occur
    LaunchedEffect(currentAlert) {
        currentAlert?.let { alert ->
            val result = snackbarHostState.showSnackbar(
                message = alert.message,
                actionLabel = alert.actionLabel,
                duration = if (alert.isError) SnackbarDuration.Long else SnackbarDuration.Short,
                withDismissAction = true
            )
            if (result == SnackbarResult.ActionPerformed) {
                alert.onAction?.invoke()
            }
            viewModel.clearSnackbarAlert()
        }
    }

    val navItems = listOf(
        NavItem(AppDestination.DASHBOARD, Icons.Default.Dashboard, Icons.Outlined.Dashboard, "Dashboard"),
        NavItem(AppDestination.SOURCES, Icons.Default.Source, Icons.Outlined.Source, "Sources"),
        NavItem(AppDestination.GRAPH, Icons.Default.Hub, Icons.Outlined.Hub, "Graph"),
        NavItem(AppDestination.READER, Icons.Default.MenuBook, Icons.Outlined.MenuBook, "Reader"),
        NavItem(AppDestination.NOTES, Icons.Default.EditNote, Icons.Outlined.EditNote, "Notes"),
        NavItem(AppDestination.PROJECTS, Icons.Default.FolderSpecial, Icons.Outlined.FolderSpecial, "Projects")
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("research_graph_app"),
        topBar = {
            ResearchTopBar(viewModel = viewModel)
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("app_navigation_bar"),
                tonalElevation = 6.dp
            ) {
                navItems.forEach { item ->
                    val isSelected = currentDestination == item.destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.navigateTo(item.destination) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                            )
                        },
                        modifier = Modifier.testTag("nav_tab_${item.label.lowercase()}")
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.testTag("research_snackbar_host")
            ) { snackbarData ->
                val isFailure = snackbarData.visuals.message.contains("failed", ignoreCase = true) ||
                        snackbarData.visuals.message.contains("error", ignoreCase = true) ||
                        snackbarData.visuals.message.contains("unreachable", ignoreCase = true) ||
                        snackbarData.visuals.message.contains("timed out", ignoreCase = true)

                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = if (isFailure) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.inverseSurface,
                    contentColor = if (isFailure) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.inverseOnSurface,
                    actionColor = if (isFailure) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.inversePrimary,
                    dismissActionContentColor = if (isFailure) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.inverseOnSurface,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                AppDestination.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                AppDestination.SOURCES -> SourcesScreen(viewModel = viewModel)
                AppDestination.GRAPH -> KnowledgeGraphScreen(viewModel = viewModel)
                AppDestination.READER -> ReadingScreen(viewModel = viewModel)
                AppDestination.NOTES -> NotesScreen(viewModel = viewModel)
                AppDestination.PROJECTS -> ProjectsScreen(viewModel = viewModel)
                AppDestination.QUESTIONS -> QuestionsScreen(viewModel = viewModel)
            }
        }
    }

    // Workspace Dialogs
    if (showAddSource) {
        AddSourceDialog(viewModel = viewModel, onDismiss = { viewModel.showAddSourceDialog.value = false })
    }
    if (showAddNote) {
        AddNoteDialog(viewModel = viewModel, onDismiss = { viewModel.showAddNoteDialog.value = false })
    }
    if (showAddTopic) {
        AddTopicDialog(viewModel = viewModel, onDismiss = { viewModel.showAddTopicDialog.value = false })
    }
    if (showCompare) {
        PaperComparisonDialog(viewModel = viewModel, onDismiss = { viewModel.showCompareDialog.value = false })
    }
    if (showCitation) {
        CitationModal(viewModel = viewModel, onDismiss = { viewModel.showCitationModal.value = false })
    }
    if (showLitReview) {
        LiteratureReviewDialog(viewModel = viewModel, onDismiss = { viewModel.showLitReviewDialog.value = false })
    }
    if (showNewQuestion) {
        AddQuestionDialog(viewModel = viewModel, onDismiss = { viewModel.showNewQuestionDialog.value = false })
    }
    if (showResearchResult && lastResearchResult != null) {
        AIResearchResultDialog(
            result = lastResearchResult!!,
            viewModel = viewModel,
            onDismiss = { viewModel.showResearchResultDialog.value = false }
        )
    }
}
