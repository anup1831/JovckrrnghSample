package com.anup.jovckrrnghsample.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.anup.jovckrrnghsample.ui.screen.AddTaskScreen
import com.anup.jovckrrnghsample.ui.screen.TaskListScreen

@Composable
fun TaskNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "task_list"
    ) {
        composable("task_list") {
            TaskListScreen(
                onNavigateToAddTask = {
                    navController.navigate("add_task")
                }
            )
        }

        composable("add_task") {
            AddTaskScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}