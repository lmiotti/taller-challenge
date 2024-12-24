package com.example.bootstrap.presentation.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bootstrap.data.repository.AuthRepository
import com.example.bootstrap.isValidEmail
import com.example.bootstrap.models.Resource
import com.example.bootstrap.presentation.ui.intent.LoginIntent
import com.example.bootstrap.presentation.ui.state.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _state: MutableStateFlow<LoginState> = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState>
        get() = _state

    private val _showResponse = MutableSharedFlow<String>()
    val showResponse: SharedFlow<String>
        get() = _showResponse

    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.OnEmailChanged -> _state.update {
                it.copy(email = intent.email, isEmailError = false)
            }
            is LoginIntent.OnPasswordChanged -> _state.update {
                it.copy(password = intent.password, isPasswordError = false)
            }
            is LoginIntent.OnLoginClicked -> login()
        }
    }

    private fun login() {
        if (!state.value.email.isValidEmail()) _state.update { it.copy(isEmailError = true) }
        if (state.value.password.isEmpty()) _state.update { it.copy(isPasswordError = true) }

        if (!state.value.isEmailError && !state.value.isPasswordError) {
            _state.update { it.copy(isEmailError = false, isPasswordError = false, isLoading = true) }
            viewModelScope.launch(Dispatchers.IO) {
                val result = authRepository.login(state.value.email, state.value.password)
                val response = when (result) {
                    is Resource.Success -> "Login Success"
                    is Resource.Failure -> result.error ?: ""
                }
                _showResponse.emit(response)
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}
