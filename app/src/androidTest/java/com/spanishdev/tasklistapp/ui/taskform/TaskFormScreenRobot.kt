package com.spanishdev.tasklistapp.ui.taskform

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.spanishdev.tasklistapp.ui.taskform.TaskFormScreenTestTag.NAME_TEXT
import com.spanishdev.tasklistapp.ui.taskform.TaskFormScreenTestTag.DESCRIPTION_TEXT
import com.spanishdev.tasklistapp.ui.taskform.TaskFormScreenTestTag.MAIN_BUTTON
import com.spanishdev.tasklistapp.ui.taskform.TaskFormScreenTestTag.BACK_BUTTON
import com.spanishdev.tasklistapp.ui.taskform.TaskFormScreenTestTag.DONE_BUTTON


class TaskFormScreenRobot(private val composeTestRule: ComposeTestRule) {

    fun typeName(text: String): TaskFormScreenRobot = apply {
        composeTestRule.onNodeWithTag(NAME_TEXT).performTextInput(text)
    }

    fun typeDescription(text: String): TaskFormScreenRobot = apply {
        composeTestRule.onNodeWithTag(DESCRIPTION_TEXT).performTextInput(text)
    }

    fun clickMainButton(): TaskFormScreenRobot = apply {
        composeTestRule.onNodeWithTag(MAIN_BUTTON).performClick()
    }

    fun clickBackButton(): TaskFormScreenRobot = apply {
        composeTestRule.onNodeWithTag(BACK_BUTTON).performClick()
    }

    fun clickDoneButton(): TaskFormScreenRobot = apply {
        composeTestRule.onNodeWithTag(DONE_BUTTON).performClick()
    }

    fun assertName(text: String) = apply {
        composeTestRule.onNodeWithTag(NAME_TEXT, useUnmergedTree = true).assertTextEquals(text)
    }

    fun assertDescription(text: String) = apply {
        composeTestRule.onNodeWithTag(DESCRIPTION_TEXT, useUnmergedTree = true).assertTextEquals(text)
    }
}