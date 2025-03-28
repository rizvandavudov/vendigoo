package com.example.vendigoo.model

import com.example.vendigoo.data.entities.District
import com.example.vendigoo.data.entities.InitialBalance
import com.example.vendigoo.data.entities.Supplier
import com.example.vendigoo.data.entities.Transaction

data class BackupData(
    val districts: List<District>,
    val suppliers: List<Supplier>,
    val transactions: List<Transaction>,
    val initialBalances: List<InitialBalance>
)
