package com.example.vendigoo.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vendigoo.data.entities.Supplier
import com.example.vendigoo.ui.components.DeleteDialog
import com.example.vendigoo.viewmodel.WholesaleViewModel

// ui/screens/SuppliersScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuppliersScreen(
    navController: NavController,
    viewModel: WholesaleViewModel,
    districtId: Int
) {
    val suppliers by viewModel.getSuppliers(districtId).collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var newSupplierName by remember { mutableStateOf("") }
    var newSupplierPhone by remember { mutableStateOf("") }
    var supplierToDelete by remember { mutableStateOf<Supplier?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Təchizatçılar") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true },
                modifier = Modifier.padding(bottom = 30.dp) // 30 dp yuxarı qaldırır
            ) {
                Icon(Icons.Default.Add, "Yeni təchizatçı")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(suppliers) { supplier ->
                SupplierItem(
                    supplier = supplier,
                    onClick = { navController.navigate("finance/${supplier.id}") },
                    onLongClick = { supplierToDelete = supplier }
                )
            }
        }

        // Əlavə et dialogu
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Yeni Təchizatçı") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newSupplierName,
                            onValueChange = { newSupplierName = it },
                            label = { Text("Ad") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newSupplierPhone,
                            onValueChange = { newSupplierPhone = it },
                            label = { Text("Telefon") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.addSupplier(districtId, newSupplierName, newSupplierPhone)
                            newSupplierName = ""
                            newSupplierPhone = ""
                            showAddDialog = false
                        },
                        enabled = newSupplierName.isNotBlank() && newSupplierPhone.isNotBlank()
                    ) { Text("Əlavə et") }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) { Text("Ləğv et") }
                }
            )
        }

        // Silmə dialogu
        supplierToDelete?.let { supplier ->
            DeleteDialog(
                title = "Təchizatçını Sil",
                message = "'${supplier.name}' silinsin?",
                onDismiss = { supplierToDelete = null },
                onConfirm = {
                    viewModel.deleteSupplier(supplier)
                    supplierToDelete = null
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SupplierItem(
    supplier: Supplier,
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
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = supplier.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = supplier.phone,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}