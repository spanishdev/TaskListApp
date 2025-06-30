package com.spanishdev.tasklistapp.ui.addtask

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import com.spanishdev.tasklistapp.ui.addtask.AddTaskViewModel.Event

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    viewModel: AddTaskViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(state.resources.navbarTitle)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.sendEvent(Event.GoBack) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
            onValueChange = { sendEvent(Event.NameChanged(it)) },
            label = { Text(text = stringResource(state.resources.nameLabel)) },
            placeholder = { Text(text = stringResource(state.resources.nameHint)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { descriptionFocusRequester.requestFocus() }
            ),
            isError = state.error != null,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.description,
            onValueChange = { sendEvent(Event.DescriptionChanged(it)) },
            label = { Text(text = stringResource(state.resources.descriptionLabel)) },
            placeholder = { Text(text = stringResource(state.resources.descriptionHint)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    sendEvent(Event.CreateTask)
                }
            ),
            isError = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .focusRequester(descriptionFocusRequester)
        )
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


