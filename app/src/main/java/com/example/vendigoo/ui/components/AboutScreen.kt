package com.example.vendigoo.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Haqqında") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {

            Text("📱 VendiGoo", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(12.dp))

            Text("VendiGoo, səyyar satıcıların gündəlik əməliyyatlarını izləməsi, mal paylanmasını və ödənişlərin qeydini aparması üçün hazırlanmış sadə və güclü bir tətbiqdir.",
                style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(12.dp))

            Text("🔹 Rayonlara görə təchizatçıları idarə edin\n" +
                    "🔹 Mal vermə və pul alma əməliyyatlarını qeyd edin\n" +
                    "🔹 Hər bir müştəri üçün balansı izləyin\n" +
                    "🔹 Minimalist və sürətli interfeys",
                style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(12.dp))
            Text("💡 VendiGoo — Cibinizdəki satıcı köməkçisi.",
                style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Text("Versiya: 1.0.0")
            Text("Hazırlayan: Rizvan Davudov")
        }
    }
}

