package com.example.loginmvvm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginmvvm.data.local.database.AppDatabase
import com.example.loginmvvm.repository.UserRepository
import com.example.loginmvvm.ui.screen.LoginScreen
import com.example.loginmvvm.ui.theme.LoginMVVMTheme
import com.example.loginmvvm.viewmodel.LoginViewModel
import com.example.loginmvvm.viewmodel.LoginViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginMVVMTheme {
                val context = LocalContext.current
                val factory = remember {
                    val database = AppDatabase.getInstance(context)
                    LoginViewModelFactory(UserRepository(database.userDao()))
                }
                val viewModel: LoginViewModel = viewModel(factory = factory)
                val uiState by viewModel.uiState.collectAsState()

                LoginScreen(
                    uiState = uiState,
                    onUsernameChange = viewModel::onUsernameChange,
                    onPasswordChange = viewModel::onPasswordChange,
                    onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                    onShowLogin = viewModel::showLogin,
                    onShowRegister = viewModel::showRegister,
                    onLogin = viewModel::login,
                    onRegister = viewModel::register,
                    onLogout = viewModel::logout
                )
            }
        }
    }
}
