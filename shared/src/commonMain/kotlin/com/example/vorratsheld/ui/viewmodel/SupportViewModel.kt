package com.example.vorratsheld.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vorratsheld.data.enums.SupportCategory
import com.example.vorratsheld.data.model.SupportRequest
import com.example.vorratsheld.data.repository.AuthRepository
import com.example.vorratsheld.data.repository.SupportRepository
import com.example.vorratsheld.platform.APP_VERSION
import com.example.vorratsheld.platform.platformName
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.help_error_empty
import com.example.vorratsheld.resources.help_error_send
import com.example.vorratsheld.ui.state.SupportUiState
import com.example.vorratsheld.util.DateHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

// Hilfe-Formular ausfüllen und abschicken
class SupportViewModel(
    private val authRepository: AuthRepository,
    private val supportRepository: SupportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SupportUiState(replyEmail = authRepository.getCurrentUser()?.email ?: "")
    )
    val uiState: StateFlow<SupportUiState> = _uiState.asStateFlow()

    fun updateCategory(category: SupportCategory) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun updateSubject(value: String) {
        _uiState.value = _uiState.value.copy(subject = value)
    }

    fun updateMessage(value: String) {
        _uiState.value = _uiState.value.copy(message = value)
    }

    fun updateReplyEmail(value: String) {
        _uiState.value = _uiState.value.copy(replyEmail = value)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun send() {
        val state = _uiState.value
        viewModelScope.launch {
            if (state.subject.isBlank() || state.message.isBlank()) {
                _uiState.value = state.copy(errorMessage = getString(Res.string.help_error_empty))
                return@launch
            }

            _uiState.value = state.copy(isSending = true)
            supportRepository.send(
                SupportRequest(
                    userId = authRepository.getCurrentUser()?.uid ?: "",
                    replyEmail = state.replyEmail.trim(),
                    category = state.category,
                    subject = state.subject.trim(),
                    message = state.message.trim(),
                    platform = platformName,
                    appVersion = APP_VERSION,
                    createdAt = DateHelper.nowMillis()
                )
            )
                .onSuccess {
                    _uiState.value = SupportUiState(replyEmail = state.replyEmail, isSent = true)
                }
                .onFailure { e ->
                    _uiState.value = state.copy(
                        isSending = false,
                        errorMessage = getString(Res.string.help_error_send, e.message ?: "")
                    )
                }
        }
    }
}
