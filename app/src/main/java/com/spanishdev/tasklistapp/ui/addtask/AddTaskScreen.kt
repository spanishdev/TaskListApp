package com.spanishdev.tasklistapp.ui.addtask

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spanishdev.tasklistapp.ui.addtask.AddTaskViewModel.Event

@Composable
fun AddTaskScreen(
    viewModel: AddTaskViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    Content(
        state = state,
        sendEvent = { event -> viewModel.sendEvent(event)},
        modifier = modifier
    )
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

        var name by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }


        OutlinedTextField(
            value = name,
            onValueChange = {},
            label = { Text(text = stringResource(state.nameLabel)) },
            placeholder = { Text(text = stringResource(state.nameHint)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { descriptionFocusRequester.requestFocus() }
            ),
            isError = state.error != null,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = {},
            label = { Text(text = stringResource(state.descriptionLabel)) },
            placeholder = { Text(text = stringResource(state.descriptionHint)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    sendEvent(Event.AddTask(name, description))
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


