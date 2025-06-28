package com.spanishdev.tasklistapp.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spanishdev.tasklistapp.domain.entities.Status
import com.spanishdev.tasklistapp.domain.entities.Task
import com.spanishdev.tasklistapp.ui.viewmodel.TaskListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.state.collectAsState()
    val isRefreshingState by viewModel.isRefreshingState.collectAsState()
    val isRefreshing = isRefreshingState

    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refreshTasks() },
        state = pullToRefreshState,
        modifier = modifier,
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
        when (val state = uiState) {
            is TaskListViewModel.State.Empty -> EmptyView()
            is TaskListViewModel.State.Error -> ErrorView(state.message)
            is TaskListViewModel.State.Loading -> LoadingView()
            is TaskListViewModel.State.Success -> TaskListView(tasks = state.tasks)
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
        modifier = modifier.fillMaxSize(),
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
fun TaskListView(
    tasks: List<Task>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        items(tasks) { task ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "${task.name}: ${task.description}",
                    modifier = Modifier.padding(16.dp)
                )
            }
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
fun PreviewTaskList() {
    val tasks = listOf(
        Task(id = 1, name = "Task 1", description = "A done task", Status.Done),
        Task(id = 2, name = "Task 2", description = "A pending task", Status.Pending),
        Task(id = 3, name = "Task 3", description = "A in progress task", Status.InProgress),
    )
    TaskListView(tasks)
}

