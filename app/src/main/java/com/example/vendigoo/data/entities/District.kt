package com.example.vendigoo.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// data/entities/District.kt
@Entity(tableName = "districts")
data class District(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

// data/entities/Supplier.kt
@Entity(
    tableName = "suppliers",
    foreignKeys = [ForeignKey(
        entity = District::class,
        parentColumns = ["id"],
        childColumns = ["districtId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Supplier(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val districtId: Int,
    val name: String,
    val phone: String,
    val createdAt: Long = System.currentTimeMillis()
)

// data/entities/InitialBalance.kt
@Entity(
    tableName = "initial_balances",
    foreignKeys = [ForeignKey(
        entity = Supplier::class,
        parentColumns = ["id"],
        childColumns = ["supplierId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class InitialBalance(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val supplierId: Int,
    val amount: Double,
    val createdAt: Long = System.currentTimeMillis()
)

// data/entities/Transaction.kt
@Entity(
    tableName = "transactions",
    foreignKeys = [ForeignKey(
        entity = Supplier::class,
        parentColumns = ["id"],
        childColumns = ["supplierId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("supplierId"), Index("type")]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val supplierId: Int,
    val amount: Double,
    val type: String, // "GIVEN_GOODS" or "TAKEN_MONEY"
    val createdAt: Long = System.currentTimeMillis()
)