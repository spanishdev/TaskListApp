package com.spanishdev.tasklistapp.ui.tasklist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spanishdev.tasklistapp.R
import com.spanishdev.tasklistapp.domain.model.Status
import com.spanishdev.tasklistapp.domain.model.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel,
    onAddTaskNavigation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.state.collectAsState()
    val isRefreshingState by viewModel.isRefreshingState.collectAsState()
    val isRefreshing = isRefreshingState

    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTaskNavigation) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task"
                )
            }
        },
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshTasks() },
            state = pullToRefreshState,
            modifier = modifier.padding(paddingValues),
            indicator = {
                Indicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = isRefreshing,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    state = pullToRefreshState
                )
            },
        ) {
            Content(uiState)
        }
    }
}

@Composable
fun Content(
    state: TaskListViewModel.State,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        when (state) {
            is TaskListViewModel.State.Empty -> item {
                EmptyView(modifier = Modifier.fillParentMaxSize())
            }

            is TaskListViewModel.State.Error -> item {
                ErrorView(
                    message = state.message,
                    modifier = Modifier.fillParentMaxSize()
                )
            }

            is TaskListViewModel.State.Loading -> item {
                LoadingView(modifier = Modifier.fillParentMaxSize())
            }

            is TaskListViewModel.State.Success -> items(state.tasks) { task ->
                TaskListView(task = task)
            }
        }
    }
}

@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.wrapContentSize(),
            text = "No tasks available"
        )
    }
}

@Composable
fun ErrorView(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.wrapContentSize(),
            text = message,
            color = MaterialTheme.colorScheme.error,
        )
    }
}


@Composable
fun TaskListView(task: Task) {
    val statusColor = when (task.status) {
        Status.Pending -> Color.Gray.copy(alpha = 0.1f)
        Status.InProgress -> Color.Blue.copy(alpha = 0.1f)
        Status.Done -> Color.Green.copy(alpha = 0.1f)
        Status.Cancelled -> Color.Red.copy(alpha = 0.1f)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = statusColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Text(
                text = task.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = task.createdAt,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            )
        }

    }
}

@Preview
@Composable
fun PreviewLoading() {
    LoadingView()
}

@Preview
@Composable
fun PreviewEmpty() {
    EmptyView()
}

@Preview
@Composable
fun PreviewError() {
    ErrorView("An error has occurred")
}


@Preview
@Composable
fun PreviewContent() {
    val tasks = listOf(
        Task(
            id = 1,
            name = "Task 1",
            description = "A done task",
            status = Status.Done,
            createdAt = "11-06-2025 13:00"
        ),
        Task(
            id = 2,
            name = "Task 2",
            description = "A pending task",
            status = Status.Pending,
            createdAt = "20-06-2025 18:00"
        ),
        Task(
            id = 3,
            name = "Task 3",
            description = "A in progress task",
            status = Status.InProgress,
            createdAt = "19-06-2025 22:00"
        ),
    )
    val state = TaskListViewModel.State.Success(tasks)
    Content(state)
}


