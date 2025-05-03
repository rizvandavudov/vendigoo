package com.example.vendigoo.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vendigoo.data.entities.Supplier
import com.example.vendigoo.ui.components.DeleteDialog
import com.example.vendigoo.viewmodel.WholesaleViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first


@SuppressLint("AutoboxingStateCreation")
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

    var searchQuery by remember { mutableStateOf("") }
    val filteredSuppliers = if (searchQuery.isBlank()) {
        suppliers
    } else {
        suppliers.filter { it.name.startsWith(searchQuery, ignoreCase = true) }
    }

    val coroutineScope = rememberCoroutineScope()
    var totalBalance by remember { mutableStateOf(0.0) }

    LaunchedEffect(filteredSuppliers) {
        coroutineScope.launch(Dispatchers.IO) {
            var total = 0.0
            for (supplier in filteredSuppliers) {
                val initial = viewModel.getInitialBalances(supplier.id).first().sumOf { it.amount }
                val given = viewModel.getTransactions(supplier.id, "GIVEN_GOODS").first().sumOf { it.amount }
                val taken = viewModel.getTransactions(supplier.id, "TAKEN_MONEY").first().sumOf { it.amount }
                total += initial + given - taken
            }
            totalBalance = total
        }
    }

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
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.padding(bottom = 65.dp)
            ) {
                Icon(Icons.Default.Add, "Yeni təchizatçı")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val containerColor = Color(0xFFFAF3E0)
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Axtar...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 10.dp)
                    .height(52.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = containerColor,
                    unfocusedContainerColor = containerColor,
                    disabledContainerColor = containerColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                shape = MaterialTheme.shapes.small
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 0.dp)
            ) {
                items(filteredSuppliers) { supplier ->
                    val initialBalances by viewModel.getInitialBalances(supplier.id)
                        .collectAsState(initial = emptyList())
                    val givenGoods by viewModel.getTransactions(supplier.id, "GIVEN_GOODS")
                        .collectAsState(initial = emptyList())
                    val takenMoney by viewModel.getTransactions(supplier.id, "TAKEN_MONEY")
                        .collectAsState(initial = emptyList())

                    val totalInitial = initialBalances.sumOf { it.amount }
                    val totalGiven = givenGoods.sumOf { it.amount }
                    val totalTaken = takenMoney.sumOf { it.amount }
                    val result = totalInitial + totalGiven - totalTaken

                    SupplierItem(
                        supplier = supplier,
                        qaliq = result,
                        onClick = { navController.navigate("finance/${supplier.id}") },
                        onLongClick = { supplierToDelete = supplier }
                    )
                }
            }
            // Cəm bölməsi
            Surface(
                color = Color(0xFFFAF3E0),
                tonalElevation = 4.dp,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 15.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Cəmi:",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = "${"%.2f".format(totalBalance)} AZN",
                          style = MaterialTheme.typography.titleLarge,
                    )
                }
            }
        }

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
    qaliq: Double,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
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
                text = "${supplier.name}   Aktiv Borc   -   ${"%.2f".format(qaliq)} AZN",
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
