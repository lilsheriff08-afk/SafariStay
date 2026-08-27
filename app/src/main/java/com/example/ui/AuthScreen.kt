package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.SafariViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(viewModel: SafariViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val uiEvent by viewModel.uiEvent.collectAsState()

    LaunchedEffect(uiEvent) {
        uiEvent?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUiEvent()
        }
    }

    Scaffold(
        containerColor = Color(0xFF1E2124), // Dark minimalist theme
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Branding
                Icon(
                    imageVector = Icons.Default.Landscape,
                    contentDescription = "Safari Stay Logo",
                    tint = Color(0xFFD4AF37),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "SAFARI STAY",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isSignUp) "Create an account to continue" else "Sign in to view your bookings",
                    fontSize = 14.sp,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Input Section
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("EMAIL ADDRESS", color = Color.Gray, fontSize = 12.sp) },
                    placeholder = { Text("you@example.com", color = Color.DarkGray) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD4AF37),
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("PASSWORD", color = Color.Gray, fontSize = 12.sp) },
                    placeholder = { Text("Enter your password", color = Color.DarkGray) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD4AF37),
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            if (isSignUp) {
                                viewModel.registerWithEmail(email, password)
                            } else {
                                viewModel.loginWithEmail(email, password)
                            }
                        } else {
                            viewModel.setUiEvent("Please enter email and password")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37))
                ) {
                    Text(if (isSignUp) "SIGN UP" else "SIGN IN", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { isSignUp = !isSignUp }) {
                    Text(
                        text = if (isSignUp) "Already have an account? Sign In" else "Don't have an account? Sign Up",
                        color = Color(0xFFD4AF37),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // OR Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray)
                    Text("  OR  ", color = Color.Gray, fontSize = 12.sp)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Social Auth
                OutlinedButton(
                    onClick = { viewModel.loginWithProvider("Google") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.DarkGray),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Continue with Google")
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { viewModel.loginWithProvider("Apple") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.DarkGray),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Continue with Apple")
                }

                Spacer(modifier = Modifier.weight(1f))

                // Footer
                Text(
                    text = "By continuing, you agree to our Terms of Service\nand Privacy Policy.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun SelectableChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Color(0xFFD4AF37).copy(alpha = 0.2f) else Color.Transparent,
        border = BorderStroke(1.dp, if (selected) Color(0xFFD4AF37) else Color.DarkGray)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = if (selected) Color(0xFFD4AF37) else Color.Gray,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
