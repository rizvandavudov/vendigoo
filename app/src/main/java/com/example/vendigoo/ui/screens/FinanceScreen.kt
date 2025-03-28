package com.example.vendigoo.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vendigoo.data.entities.InitialBalance
import com.example.vendigoo.ui.components.AmountDialog
import com.example.vendigoo.ui.components.DeleteDialog
import com.example.vendigoo.viewmodel.WholesaleViewModel
import java.text.SimpleDateFormat
import java.util.Date
// ui/screens/FinanceScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    navController: NavController,
    viewModel: WholesaleViewModel,
    supplierId: Int
) {
    val initialBalances by viewModel.getInitialBalances(supplierId).collectAsState(emptyList())
    val givenGoods by viewModel.getTransactions(supplierId, "GIVEN_GOODS").collectAsState(emptyList())
    val takenMoney by viewModel.getTransactions(supplierId, "TAKEN_MONEY").collectAsState(emptyList())

    var showInitialBalanceDialog by remember { mutableStateOf(false) }
    var balanceToDelete by remember { mutableStateOf<InitialBalance?>(null) }

    // Balans hesablamaları
    val totalInitial = initialBalances.sumOf { it.amount }
    val totalGiven = givenGoods.sumOf { it.amount }
    val totalTaken = takenMoney.sumOf { it.amount }
    val result = totalInitial + totalGiven - totalTaken

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Maliyyə") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // İlkin qalıq bölməsi
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "İlkin Qalıq",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { showInitialBalanceDialog = true }) {
                    Icon(Icons.Default.Add, "İlkin qalıq əlavə et")
                }
            }

            initialBalances.forEach { balance ->
                BalanceItem(
                    balance = balance,
                    onLongClick = { balanceToDelete = balance }
                )
            }

            // Verilən mal bölməsi
            Text(
                text = "Verilən Mal: ${totalGiven} AZN",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("given_goods/$supplierId") }
                    .padding(16.dp)
            )

            // Götürülən pul bölməsi
            Text(
                text = "Götürülən Pul: ${totalTaken} AZN",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("taken_money/$supplierId") }
                    .padding(16.dp)
            )

            // Nəticə bölməsi
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Hesabat",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("İlkin qalıq: $totalInitial AZN")
                    Text("Verilən mal: $totalGiven AZN")
                    Text("Götürülən pul: $totalTaken AZN")
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = "Qalıq: $result AZN",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }

        // İlkin qalıq əlavə et dialogu
        if (showInitialBalanceDialog) {
            AmountDialog(
                title = "İlkin Qalıq Əlavə Et",
                onDismiss = { showInitialBalanceDialog = false },
                onConfirm = { amount ->
                    viewModel.addInitialBalance(supplierId, amount)
                    showInitialBalanceDialog = false
                }
            )
        }

        // İlkin qalıq silmə dialogu
        balanceToDelete?.let { balance ->
            DeleteDialog(
                title = "İlkin Qalığı Sil",
                message = "${balance.amount} AZN silinsin?",
                onDismiss = { balanceToDelete = null },
                onConfirm = {
                    viewModel.deleteInitialBalance(balance)
                    balanceToDelete = null
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BalanceItem(
    balance: InitialBalance,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .combinedClickable(
                onClick = { /* Detal baxışı əlavə edilə bilər */ },
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${balance.amount} AZN",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = SimpleDateFormat("dd.MM.yyyy").format(Date(balance.createdAt)),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}