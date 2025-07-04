package com.spanishdev.tasklistapp.ui.taskform

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.spanishdev.tasklistapp.HiltTestActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class TaskFormScreenTest {

    @get:Rule(order = 0)
    val hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    private lateinit var robot: TaskFormScreenRobot

    @Before
    fun setUp() {
        hiltTestRule.inject()

        composeTestRule.setContent {
            val viewModel: TaskFormViewModel = hiltViewModel()
            TaskFormScreen(
                viewModel = viewModel,
                onBackNavigation = {},
                onSuccessNavigation = {}
            )
        }

        robot = TaskFormScreenRobot(composeTestRule)
    }

    @Test
    fun test_activity_launches() {
        // Test básico para verificar que la activity se lanza
        composeTestRule.setContent {
            Text("Hello World")
        }

        composeTestRule.onNodeWithText("Hello World").assertIsDisplayed()
    }

//    @Test
//    fun WHEN_send_name_and_description_THEN_blablabla() {
//        val name = "Test task"
//        val description = "Test description"
//        robot
//            .typeName(name)
//            .typeDescription(description)
//            .clickMainButton()
//            .assertName(name)
//            .assertName(description)
//
//    }

}