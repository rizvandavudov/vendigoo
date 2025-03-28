package com.example.vendigoo.data.entities.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.vendigoo.data.entities.District
import com.example.vendigoo.data.entities.InitialBalance
import com.example.vendigoo.data.entities.Supplier
import com.example.vendigoo.data.entities.Transaction
import kotlinx.coroutines.flow.Flow

// data/dao/WholesaleDao.kt
@Dao
interface WholesaleDao {
    // Rayonlar üçün
    @Insert
    suspend fun insertDistrict(district: District): Long

    @Query("SELECT * FROM districts ORDER BY createdAt DESC")
    fun getAllDistricts(): Flow<List<District>>

    @Delete
    suspend fun deleteDistrict(district: District)

    // Təchizatçılar üçün
    @Insert
    suspend fun insertSupplier(supplier: Supplier): Long

    @Query("SELECT * FROM suppliers WHERE districtId = :districtId ORDER BY createdAt DESC")
    fun getSuppliersByDistrict(districtId: Int): Flow<List<Supplier>>

    @Delete
    suspend fun deleteSupplier(supplier: Supplier)

    // İlkin qalıq üçün
    @Insert
    suspend fun insertInitialBalance(balance: InitialBalance): Long

    @Query("SELECT * FROM initial_balances WHERE supplierId = :supplierId ORDER BY createdAt DESC")
    fun getInitialBalances(supplierId: Int): Flow<List<InitialBalance>>

    @Delete
    suspend fun deleteInitialBalance(balance: InitialBalance)

    @Query("SELECT * FROM initial_balances")
    suspend fun getAllInitialBalancesList(): List<InitialBalance> // 🔥 bunu əlavə et

    // Əməliyyatlar üçün
    @Insert
    suspend fun insertTransaction(transaction: Transaction): Long

    @Query("SELECT * FROM transactions WHERE supplierId = :supplierId AND type = :type ORDER BY createdAt DESC")
    fun getTransactionsByType(supplierId: Int, type: String): Flow<List<Transaction>>

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    // --- Backup üçün əlavə olunanlar ---
    @Query("SELECT * FROM districts")
    suspend fun getAllDistrictsList(): List<District>

    @Query("SELECT * FROM suppliers")
    suspend fun getAllSuppliersList(): List<Supplier>

    @Query("SELECT * FROM transactions")
    suspend fun getAllTransactionsList(): List<Transaction>

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    @Query("DELETE FROM suppliers")
    suspend fun deleteAllSuppliers()

    @Query("DELETE FROM districts")
    suspend fun deleteAllDistricts()

    @Query("DELETE FROM initial_balances")
    suspend fun deleteAllInitialBalances()

    @Insert
    suspend fun insertAllDistricts(districts: List<District>)

    @Insert
    suspend fun insertAllSuppliers(suppliers: List<Supplier>)

    @Insert
    suspend fun insertAllTransactions(transactions: List<Transaction>)

    @Insert
    suspend fun insertAllInitialBalances(balances: List<InitialBalance>)


}