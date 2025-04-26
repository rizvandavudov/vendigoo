package com.example.vendigoo.ui.screens

import androidx.compose.foundation.lazy.items
import com.example.vendigoo.ui.components.TransactionItem
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vendigoo.data.entities.Transaction
import com.example.vendigoo.ui.components.AmountDialog
import com.example.vendigoo.ui.components.DeleteDialog
import com.example.vendigoo.viewmodel.WholesaleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TakenMoneyScreen(
    navController: NavController,
    viewModel: WholesaleViewModel,
    supplierId: Int
) {
    val transactions by viewModel.getTransactions(supplierId, "TAKEN_MONEY").collectAsState(emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Götürülən Pul") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true },
                modifier = Modifier.padding(bottom = 80.dp) // 30 dp yuxarı qaldırır
            ) {
                Icon(Icons.Default.Add, "Yeni əməliyyat")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(transactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onLongClick = { transactionToDelete = transaction }
                    )
                }
            }

            // Total sum section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 10.dp) ,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(
                    text = "Cəmi: ${transactions.sumOf { it.amount }} AZN",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Add dialog
        if (showAddDialog) {
            AmountDialog(
                title = "Götürülən Pul Əlavə Et",
                onDismiss = { showAddDialog = false },
                onConfirm = { amount ->
                    viewModel.addTransaction(supplierId, amount, "TAKEN_MONEY")
                    showAddDialog = false
                }
            )
        }

        // Delete dialog
        transactionToDelete?.let { transaction ->
            DeleteDialog(
                title = "Əməliyyatı Sil",
                message = "${transaction.amount} AZN silinsin?",
                onDismiss = { transactionToDelete = null },
                onConfirm = {
                    viewModel.deleteTransaction(transaction)
                    transactionToDelete = null
                }
            )
        }
    }
}

