package com.example.vendigoo.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vendigoo.data.entities.District
import com.example.vendigoo.ui.components.DeleteDialog
import com.example.vendigoo.viewmodel.WholesaleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: WholesaleViewModel
) {
    val districts by viewModel.districts.collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var newDistrictName by remember { mutableStateOf("") }
    var districtToDelete by remember { mutableStateOf<District?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rayonlar") },
                actions = {
                    TextButton(onClick = { navController.navigate("support") }) {
                        Text("📞 Dəstək")
                    }
                    TextButton(onClick = { navController.navigate("about") }) {
                        Text("ℹ️ Haqqında")
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Ayarlar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.padding(bottom = 30.dp) // 30 dp yuxarı qaldırır
            ) {

                Icon(Icons.Default.Add, contentDescription = "Yeni rayon")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            // Rayonlar siyahısı
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(districts) { district ->
                    DistrictItem(
                        district = district,
                        onClick = { navController.navigate("suppliers/${district.id}") },
                        onLongClick = { districtToDelete = district }
                    )
                }
            }
        }

        // Əlavə et dialogu
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Yeni Rayon") },
                text = {
                    OutlinedTextField(
                        value = newDistrictName,
                        onValueChange = { newDistrictName = it },
                        label = { Text("Rayon adı") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.addDistrict(newDistrictName)
                            newDistrictName = ""
                            showAddDialog = false
                        },
                        enabled = newDistrictName.isNotBlank()
                    ) { Text("Əlavə et") }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) { Text("Ləğv et") }
                }
            )
        }

        // Silmə dialogu
        districtToDelete?.let { district ->
            DeleteDialog(
                title = "Rayonu Sil",
                message = "'${district.name}' silinsin?",
                onDismiss = { districtToDelete = null },
                onConfirm = {
                    viewModel.deleteDistrict(district)
                    districtToDelete = null
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DistrictItem(
    district: District,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Text(
            text = district.name,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}