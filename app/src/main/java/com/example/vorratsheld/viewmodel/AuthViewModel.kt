package com.example.vorratsheld.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vorratsheld.data.model.User
import com.example.vorratsheld.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Hält den Login-Zustand für die Login/Register-Screens
class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(authRepository.getCurrentUser())
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            result.onSuccess { user ->
                _currentUser.value = user
                _errorMessage.value = null
            }.onFailure { e ->
                _errorMessage.value = e.message
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.register(email, password)
            result.onSuccess { user ->
                _currentUser.value = user
                _errorMessage.value = null
            }.onFailure { e ->
                _errorMessage.value = e.message
            }
        }
    }

    // onSuccess wird nur bei erfolgreichem Wechsel aufgerufen (z. B. um ein "Erledigt"-Feedback zu zeigen)
    fun changePassword(currentPassword: String, newPassword: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = authRepository.changePassword(currentPassword, newPassword)
            result.onSuccess {
                _errorMessage.value = null
                onSuccess()
            }.onFailure { e ->
                _errorMessage.value = e.message
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _currentUser.value = null
    }
}