package com.example.loginmvvm.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.loginmvvm.ui.theme.LoginMVVMTheme
import com.example.loginmvvm.viewmodel.AuthScreenMode
import com.example.loginmvvm.viewmodel.LoginUiState
import com.example.loginmvvm.viewmodel.UiMessage

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onShowLogin: () -> Unit,
    onShowRegister: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = Color(0xFFF4F7FB)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.loggedInUsername != null) {
                    HomeContent(
                        username = uiState.loggedInUsername,
                        message = uiState.message,
                        onLogout = onLogout
                    )
                } else {
                    AuthCard(
                        uiState = uiState,
                        onUsernameChange = onUsernameChange,
                        onPasswordChange = onPasswordChange,
                        onConfirmPasswordChange = onConfirmPasswordChange,
                        onShowLogin = onShowLogin,
                        onShowRegister = onShowRegister,
                        onLogin = onLogin,
                        onRegister = onRegister
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthCard(
    uiState: LoginUiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onShowLogin: () -> Unit,
    onShowRegister: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppIcon()
            Text(
                text = if (uiState.screenMode == AuthScreenMode.Login) "Login User" else "Register User",
                color = Color(0xFF14213D),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (uiState.screenMode == AuthScreenMode.Login) {
                    "Masukkan username dan password untuk masuk"
                } else {
                    "Buat akun baru dengan username dan password"
                },
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            AuthTabs(
                selectedMode = uiState.screenMode,
                onShowLogin = onShowLogin,
                onShowRegister = onShowRegister
            )

            OutlinedTextField(
                value = uiState.username,
                onValueChange = onUsernameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Username") },
                singleLine = true,
                enabled = !uiState.isLoading
            )

            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") },
                singleLine = true,
                enabled = !uiState.isLoading,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            if (uiState.screenMode == AuthScreenMode.Register) {
                OutlinedTextField(
                    value = uiState.confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Konfirmasi Password") },
                    singleLine = true,
                    enabled = !uiState.isLoading,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }

            uiState.message?.let { MessageBanner(message = it) }

            Button(
                onClick = if (uiState.screenMode == AuthScreenMode.Login) onLogin else onRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(if (uiState.screenMode == AuthScreenMode.Login) "LOGIN" else "REGISTER")
                }
            }
        }
    }
}

@Composable
private fun AuthTabs(
    selectedMode: AuthScreenMode,
    onShowLogin: () -> Unit,
    onShowRegister: () -> Unit
) {
    TabRow(
        selectedTabIndex = if (selectedMode == AuthScreenMode.Login) 0 else 1,
        modifier = Modifier.clip(RoundedCornerShape(10.dp))
    ) {
        Tab(
            selected = selectedMode == AuthScreenMode.Login,
            onClick = onShowLogin,
            text = { Text("Login") }
        )
        Tab(
            selected = selectedMode == AuthScreenMode.Register,
            onClick = onShowRegister,
            text = { Text("Register") }
        )
    }
}

@Composable
private fun HomeContent(
    username: String,
    message: UiMessage?,
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppIcon()
            Text(
                text = "Login Berhasil",
                color = Color(0xFF14532D),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Selamat datang, $username",
                color = Color(0xFF334155),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            message?.let { MessageBanner(message = it) }
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("LOGOUT")
            }
        }
    }
}

@Composable
private fun AppIcon() {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF60A5FA), Color(0xFF1D4ED8))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(34.dp)
                    .height(18.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .background(Color.White)
            )
        }
    }
}

@Composable
private fun MessageBanner(message: UiMessage) {
    val isSuccess = message is UiMessage.Success
    val containerColor = if (isSuccess) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val contentColor = if (isSuccess) Color(0xFF1B5E20) else Color(0xFFB71C1C)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(containerColor)
            .border(1.dp, contentColor.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(contentColor)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = message.text,
            color = contentColor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginMVVMTheme {
        LoginScreen(
            uiState = LoginUiState(message = UiMessage.Success("Register berhasil, silakan login")),
            onUsernameChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onShowLogin = {},
            onShowRegister = {},
            onLogin = {},
            onRegister = {},
            onLogout = {}
        )
    }
}
