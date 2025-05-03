package com.example.vendigoo.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vendigoo.data.entities.District
import com.example.vendigoo.ui.components.DeleteDialog
import com.example.vendigoo.viewmodel.WholesaleViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@SuppressLint("AutoboxingStateCreation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: WholesaleViewModel
) {
    val districts by viewModel.districts.collectAsState(initial = emptyList())
    val suppliers by viewModel.allSuppliers.collectAsState(initial = emptyList())

    var showAddDialog by remember { mutableStateOf(false) }
    var newDistrictName by remember { mutableStateOf("") }
    var districtToDelete by remember { mutableStateOf<District?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    var districtResults by remember { mutableStateOf<Map<Int, Double>>(emptyMap()) }
    var totalOverallBalance by remember { mutableStateOf(0.0) }

    LaunchedEffect(districts, suppliers) {
        coroutineScope.launch(Dispatchers.IO) {
            val resultMap = mutableMapOf<Int, Double>()
            var total = 0.0
            for (district in districts) {
                val suppliersInDistrict = suppliers.filter { it.districtId == district.id }
                var districtTotal = 0.0
                for (supplier in suppliersInDistrict) {
                    val initial = viewModel.getInitialBalances(supplier.id).first().sumOf { it.amount }
                    val given = viewModel.getTransactions(supplier.id, "GIVEN_GOODS").first().sumOf { it.amount }
                    val taken = viewModel.getTransactions(supplier.id, "TAKEN_MONEY").first().sumOf { it.amount }
                    districtTotal += initial + given - taken
                }
                resultMap[district.id] = districtTotal
                total += districtTotal
            }
            districtResults = resultMap
            totalOverallBalance = total
        }
    }

    val filteredDistricts = if (searchQuery.isBlank()) {
        districts
    } else {
        districts.filter {
            it.name.startsWith(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rayonlar") },
                actions = {
                    TextButton(onClick = { navController.navigate("support") }) {
                        Text("\uD83D\uDCDE Dəstək")
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
                modifier = Modifier.padding(bottom = 65.dp)
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

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(filteredDistricts) { district ->
                    val suppliersInDistrict = suppliers.filter { it.districtId == district.id }
                    val totalSuppliers = suppliersInDistrict.size
                    val totalBalance = districtResults[district.id] ?: 0.0

                    DistrictItem(
                        district = district,
                        totalSuppliers = totalSuppliers,
                        totalBalance = totalBalance,
                        onClick = { navController.navigate("suppliers/${district.id}") },
                        onLongClick = { districtToDelete = district }
                    )
                }
            }

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
                        text = "Ümumi Qalıq:",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "${"%.2f".format(totalOverallBalance)} AZN",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }

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
    totalSuppliers: Int = 0,
    totalBalance: Double = 0.0,
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
                text = district.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Şəxs sayı: $totalSuppliers  |  Qalıq: ${"%.2f".format(totalBalance)} AZN",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
