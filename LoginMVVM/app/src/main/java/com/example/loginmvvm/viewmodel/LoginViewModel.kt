package com.example.loginmvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.loginmvvm.repository.RegisterResult
import com.example.loginmvvm.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value, message = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, message = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, message = null) }
    }

    fun showLogin() {
        _uiState.update {
            it.copy(
                screenMode = AuthScreenMode.Login,
                password = "",
                confirmPassword = "",
                message = null
            )
        }
    }

    fun showRegister() {
        _uiState.update {
            it.copy(
                screenMode = AuthScreenMode.Register,
                password = "",
                confirmPassword = "",
                message = null
            )
        }
    }

    fun login() {
        val currentState = _uiState.value
        val validationMessage = validateLogin(currentState)
        if (validationMessage != null) {
            _uiState.update { it.copy(message = UiMessage.Error(validationMessage)) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }
            val user = repository.login(currentState.username, currentState.password)
            _uiState.update {
                if (user != null) {
                    it.copy(
                        isLoading = false,
                        loggedInUsername = user.username,
                        password = "",
                        confirmPassword = "",
                        message = UiMessage.Success("Login berhasil")
                    )
                } else {
                    it.copy(
                        isLoading = false,
                        message = UiMessage.Error("Username atau password salah")
                    )
                }
            }
        }
    }

    fun register() {
        val currentState = _uiState.value
        val validationMessage = validateRegister(currentState)
        if (validationMessage != null) {
            _uiState.update { it.copy(message = UiMessage.Error(validationMessage)) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }
            val result = repository.register(currentState.username, currentState.password)
            _uiState.update {
                when (result) {
                    RegisterResult.Success -> it.copy(
                        isLoading = false,
                        screenMode = AuthScreenMode.Login,
                        password = "",
                        confirmPassword = "",
                        message = UiMessage.Success("Register berhasil, silakan login")
                    )

                    RegisterResult.UsernameTaken -> it.copy(
                        isLoading = false,
                        message = UiMessage.Error("Username sudah digunakan")
                    )
                }
            }
        }
    }

    fun logout() {
        _uiState.update {
            LoginUiState(
                username = it.loggedInUsername.orEmpty(),
                message = UiMessage.Success("Logout berhasil")
            )
        }
    }

    private fun validateLogin(state: LoginUiState): String? {
        return when {
            state.username.isBlank() -> "Username wajib diisi"
            state.password.isBlank() -> "Password wajib diisi"
            else -> null
        }
    }

    private fun validateRegister(state: LoginUiState): String? {
        return when {
            state.username.isBlank() -> "Username wajib diisi"
            state.password.isBlank() -> "Password wajib diisi"
            state.confirmPassword.isBlank() -> "Konfirmasi password wajib diisi"
            state.password.length < 4 -> "Password minimal 4 karakter"
            state.password != state.confirmPassword -> "Konfirmasi password tidak sama"
            else -> null
        }
    }
}

class LoginViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class LoginUiState(
    val screenMode: AuthScreenMode = AuthScreenMode.Login,
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val loggedInUsername: String? = null,
    val message: UiMessage? = null
)

enum class AuthScreenMode {
    Login,
    Register
}

sealed interface UiMessage {
    val text: String

    data class Success(override val text: String) : UiMessage
    data class Error(override val text: String) : UiMessage
}
