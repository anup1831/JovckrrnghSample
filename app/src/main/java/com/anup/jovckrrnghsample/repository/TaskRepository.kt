package com.anup.jovckrrnghsample.repository

import com.anup.jovckrrnghsample.data.local.TaskDao
import com.anup.jovckrrnghsample.data.model.Task
import com.anup.jovckrrnghsample.data.network.ApiService
import com.anup.jovckrrnghsample.data.network.NetworkResult
import com.anup.jovckrrnghsample.data.usecase.TaskDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val apiService: ApiService
){
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun syncTasks(): NetworkResult<List<Task>> {
        return try {
            val response = apiService.getTasks()
            if (response.isSuccessful) {
                response.body()?.let { taskDtos ->
                    val tasks = taskDtos.map { dto ->
                        Task(
                            id = dto.id,
                            title = dto.title,
                            description = dto.description,
                            completed = dto.completed
                        )
                    }

                    // Clear local data and insert new data
                    taskDao.deleteAllTasks()
                    tasks.forEach { taskDao.insertTask(it) }

                    NetworkResult.Success(tasks)

                } ?: NetworkResult.Error("Empty response")
            }else {
                NetworkResult.Error("Failed to fetch tasks: ${response.message()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network error: ${e.message}")
        }
    }

    suspend fun insertTask(task: Task) : NetworkResult<Task> {
        return try {
            val taskDto = TaskDto(
                id = task.id,
                title = task.title,
                description = task.description,
                completed = task.completed
            )

            val response = apiService.createTask(taskDto)
            if (response.isSuccessful){
                taskDao.insertTask(task)
                NetworkResult.Success(task)
            } else {
                // Still save locally even if network fails
                taskDao.insertTask(task)
                NetworkResult.Error("Failed to sync with server, saved locally")
            }
        } catch (e: Exception) {
            taskDao.insertTask(task)
            NetworkResult.Error("Network error, saved locally: ${e.message}")
        }

    }

    suspend fun updateTask(task: Task): NetworkResult<Task> {
        return try {
            val taskDto = TaskDto(
                id = task.id,
                title = task.title,
                description = task.description,
                completed = task.completed
            )

            val response = apiService.updateTask(task.id, taskDto)
            if (response.isSuccessful) {
                taskDao.updateTask(task)
                NetworkResult.Success(task)
            } else {
                taskDao.updateTask(task)
                NetworkResult.Error("Failed to sync with server, updated locally")
            }
        } catch (e: Exception) {
            taskDao.updateTask(task)
            NetworkResult.Error("Network error, updated locally: ${e.message}")
        }
    }

    suspend fun deleteTask(task: Task): NetworkResult<Unit> {
        return try {
            val response = apiService.deleteTask(task.id)
            if (response.isSuccessful) {
                taskDao.deleteTask(task)
                NetworkResult.Success(Unit)
            } else {
                taskDao.deleteTask(task)
                NetworkResult.Error("Failed to sync with server, deleted locally")
            }
        } catch (e: Exception) {
            taskDao.deleteTask(task)
            NetworkResult.Error("Network error, deleted locally: ${e.message}")
        }
    }

}