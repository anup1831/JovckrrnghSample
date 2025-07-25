package com.anup.jovckrrnghsample.ui.uistates

data class AddTaskUiState(
    val title: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
