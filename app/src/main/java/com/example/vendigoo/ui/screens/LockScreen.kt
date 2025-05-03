package com.example.vendigoo.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vendigoo.R
import androidx.compose.ui.graphics.Color


@SuppressLint("UseKtx")
@Composable
fun LockScreen(
    navController: NavController,
    sharedPreferences: SharedPreferences
) {
    val context = LocalContext.current
    val correctCode = "masterkey"
    var inputCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showDeveloperInfo by remember { mutableStateOf(false) }
    val primaryButtonColor = Color(0xFFF4B740) // Logo rəngi ilə uyğun narıncı
    val DevlButtonColor = Color(0xFFFFDC97) //  narıncı

    val isActivated = sharedPreferences.getBoolean("is_activated", false)
    LaunchedEffect(Unit) {
        if (isActivated) {
            navController.navigate("main") {
                popUpTo("lock") { inclusive = true }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.vendigoo_logo),
                    contentDescription = "VendiGoo Logo",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                )



                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Tətbiqə giriş üçün kod daxil edin",
                    style = MaterialTheme.typography.headlineSmall,
                            fontSize = 20.sp,
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
                    } },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryButtonColor,
                        contentColor = Color.Black
                    )
                ) {
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
                            .padding(horizontal = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = DevlButtonColor
                        )
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
