package com.anup.jovckrrnghsample.data.usecase

//Response
data class TaskDto(
    val id: Int,
    val title: String,
    val description: String,
    val completed: Boolean
)
