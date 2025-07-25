package com.anup.jovckrrnghsample.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anup.jovckrrnghsample.data.model.Task
import com.anup.jovckrrnghsample.data.network.NetworkResult
import com.anup.jovckrrnghsample.repository.TaskRepository
import com.anup.jovckrrnghsample.ui.uistates.AddTaskUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTaskUiState())
    val uiState: StateFlow<AddTaskUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun saveTask(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        if (currentState.title.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "Title cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true)

            val task = Task(
                id = System.currentTimeMillis().toInt(),
                title = currentState.title,
                description = currentState.description
            )

            when (val result = repository.insertTask(task)) {
                is NetworkResult.Success -> {
                    _uiState.value = AddTaskUiState() // Reset state
                    onSuccess()
                }

                is NetworkResult.Error -> {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }

                is NetworkResult.Loading -> {
                    _uiState.value = currentState.copy(isLoading = true)
                }
            }
        }
    }
}