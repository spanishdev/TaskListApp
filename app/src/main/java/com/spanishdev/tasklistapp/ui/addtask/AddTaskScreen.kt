package com.spanishdev.tasklistapp.ui.addtask

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spanishdev.tasklistapp.R
import com.spanishdev.tasklistapp.ui.addtask.AddTaskViewModel.Event
import com.spanishdev.tasklistapp.ui.addtask.AddTaskViewModel.Error

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    viewModel: AddTaskViewModel,
    onBackNavigation: () -> Unit,
    onSuccessNavigation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                AddTaskViewModel.NavigationEvent.TaskAddedSuccessfully -> {
                    onSuccessNavigation()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_task_title)) },
                navigationIcon = {
                    IconButton(onClick = { onBackNavigation() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.sendEvent(Event.CreateTask) },
                        enabled = true //state.canSaveTask
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save Task"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Content(
            state = state,
            sendEvent = { event -> viewModel.sendEvent(event) },
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
        )
    }
}

@Composable
private fun Content(
    state: AddTaskViewModel.State,
    sendEvent: (Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {

        val descriptionFocusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current

        OutlinedTextField(
            value = state.name,
            onValueChange = {
                sendEvent(Event.NameChanged(it))
            },
            label = { Text(text = stringResource(R.string.add_task_name_label)) },
            placeholder = { Text(text = stringResource(R.string.add_task_name_hint)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { descriptionFocusRequester.requestFocus() }
            ),
            isError = state.error is Error.InvalidName,
            supportingText = (state.error as? Error.InvalidName)?.let { { Text(text = it.message) } },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.description,
            onValueChange = {
                sendEvent(Event.DescriptionChanged(it))
            },
            label = { Text(text = stringResource(R.string.add_task_description_label)) },
            placeholder = { Text(text = stringResource(R.string.add_task_description_hint)) },
            singleLine = false,
            maxLines = 4,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    sendEvent(Event.CreateTask)
                }
            ),
            isError = state.error is Error.InvalidDescription,
            supportingText = (state.error as? Error.InvalidDescription)?.let { { Text(text = it.message) } },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .focusRequester(descriptionFocusRequester)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { sendEvent(Event.CreateTask) },
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(stringResource(R.string.add_task_button))
            }
        }
    }
}

@Preview
@Composable
fun PreviewAddTask() {
    Content(
        state = AddTaskViewModel.State(),
        sendEvent = {}
    )
}


