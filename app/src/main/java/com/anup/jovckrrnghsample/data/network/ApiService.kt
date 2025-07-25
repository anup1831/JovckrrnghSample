package com.anup.jovckrrnghsample.data.network

import com.anup.jovckrrnghsample.data.usecase.TaskDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("tasks")
    suspend fun getTasks(): Response<List<TaskDto>>

    @POST("tasks")
    suspend fun createTask(@Body task: TaskDto): Response<TaskDto>

    @PUT("tasks/{id}")
    suspend fun updateTask(@Path("id") id: Int, @Body task: TaskDto): Response<TaskDto>

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Int): Response<Unit>
}