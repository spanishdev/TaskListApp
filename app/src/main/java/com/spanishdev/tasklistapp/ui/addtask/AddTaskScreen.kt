package com.spanishdev.tasklistapp.ui.addtask

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AddTaskScreen(
    viewModel: AddTaskViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    Content(
        state = state,
        addTaskCallback = { name, description -> viewModel.addTask(name, description) },
        modifier = modifier
    )
}

@Composable
private fun Content(
    state: AddTaskViewModel.State,
    addTaskCallback: (String, String) -> Unit,
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
                    addTaskCallback(name, description)
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
        addTaskCallback = { _,_ -> },
    )
}


