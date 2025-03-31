package com.example.vendigoo.ui.components

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LockScreen(
    navController: NavController,
    sharedPreferences: SharedPreferences
) {
    val context = LocalContext.current
    val correctCode = "12345" // Giriş kodunuzu buraya yazın
    var inputCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showDeveloperInfo by remember { mutableStateOf(false) }

    val isActivated = sharedPreferences.getBoolean("is_activated", false)
    LaunchedEffect(Unit) {
        if (isActivated) {
            navController.navigate("main") {
                popUpTo("lock") { inclusive = true }
            }
        }
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Tətbiqə giriş üçün kod daxil edin",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = inputCode,
                    onValueChange = { inputCode = it },
                    label = { Text("Giriş Kodu") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    if (inputCode == correctCode) {
                        sharedPreferences.edit().putBoolean("is_activated", true).apply()
                        navController.navigate("main") {
                            popUpTo("lock") { inclusive = true }
                        }
                    } else {
                        errorMessage = "Kod yanlışdır!"
                    }
                }) {
                    Text("Daxil ol")
                }

                if (errorMessage.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(errorMessage, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = { showDeveloperInfo = true }) {
                    Text("🔒 Developera müraciət et")
                }

                if (showDeveloperInfo) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("📱 Tətbiq: VendiGoo")
                            Text("👨‍💻 Developer: Rizvan Davudov")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "📞 Əlaqə nömrəsi: +994 70 611 48 81",
                                modifier = Modifier.clickable {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:+994706114881")
                                    }
                                    context.startActivity(intent)
                                },
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
