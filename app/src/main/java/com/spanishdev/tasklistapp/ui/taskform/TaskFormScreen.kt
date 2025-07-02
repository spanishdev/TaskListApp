package com.spanishdev.tasklistapp.ui.taskform

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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import com.spanishdev.tasklistapp.ui.taskform.TaskFormViewModel.Event
import com.spanishdev.tasklistapp.ui.taskform.TaskFormViewModel.Error

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(
    viewModel: TaskFormViewModel,
    onBackNavigation: () -> Unit,
    onSuccessNavigation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val isInEditMode = viewModel.isInEditMode
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                TaskFormViewModel.NavigationEvent.TaskSavedSuccessfully -> {
                    onSuccessNavigation()
                }

                TaskFormViewModel.NavigationEvent.GoBack -> {
                    onBackNavigation()
                }
            }
        }
    }

    LaunchedEffect(state.error) {
        (state.error as? Error.GenericError)?.let { e ->
            val result = snackbarHostState.showSnackbar(
                message = e.message,
                actionLabel = "Dismiss",
                duration = SnackbarDuration.Long,
                withDismissAction = true
            )

            when (result) {
                SnackbarResult.ActionPerformed -> viewModel.sendEvent(Event.ClearError)
                SnackbarResult.Dismissed -> viewModel.sendEvent(Event.ClearError)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (isInEditMode) {
                                R.string.edit_task
                            } else {
                                R.string.add_task_title
                            }
                        )
                    )
                },
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
                        onClick = { viewModel.sendEvent(Event.SubmitForm) },
                        enabled = true //For accessibility is better to keep this enabled
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save Task"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddingValues ->
        Content(
            state = state,
            isInEditMode = isInEditMode,
            sendEvent = { event -> viewModel.sendEvent(event) },
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
        )
    }
}

@Composable
private fun Content(
    state: TaskFormViewModel.State,
    isInEditMode: Boolean,
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
            label = { Text(text = stringResource(R.string.task_name_label)) },
            placeholder = { Text(text = stringResource(R.string.task_name_hint)) },
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
            label = { Text(text = stringResource(R.string.task_description_label)) },
            placeholder = { Text(text = stringResource(R.string.task_description_hint)) },
            singleLine = false,
            maxLines = 4,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    sendEvent(Event.SubmitForm)
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
            onClick = { sendEvent(Event.SubmitForm) },
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
                Text(
                    stringResource(
                        if (isInEditMode) {
                            R.string.edit_task
                        } else {
                            R.string.add_task_button
                        }
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewTaskForm() {
    Content(
        state = TaskFormViewModel.State(),
        isInEditMode = false,
        sendEvent = {}
    )
}


