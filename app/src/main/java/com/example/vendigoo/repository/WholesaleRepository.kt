package com.example.vendigoo.repository

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.example.vendigoo.data.entities.District
import com.example.vendigoo.data.entities.InitialBalance
import com.example.vendigoo.data.entities.Supplier
import com.example.vendigoo.data.entities.Transaction
import com.example.vendigoo.data.entities.dao.WholesaleDao
import com.example.vendigoo.model.BackupData
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import java.io.File

class WholesaleRepository(private val dao: WholesaleDao) {

    // Rayonlar
    fun getAllDistricts() = dao.getAllDistricts()
    suspend fun addDistrict(name: String) = dao.insertDistrict(District(name = name))
    suspend fun deleteDistrict(district: District) = dao.deleteDistrict(district)

    fun getAllSuppliersFlow(): Flow<List<Supplier>> = dao.getAllSuppliersFlow()
    fun getAllInitialBalancesFlow(): Flow<List<InitialBalance>> = dao.getAllInitialBalancesFlow()

    // Təchizatçılar
    fun getSuppliers(districtId: Int) = dao.getSuppliersByDistrict(districtId)
    suspend fun addSupplier(districtId: Int, name: String, phone: String) =
        dao.insertSupplier(Supplier(districtId = districtId, name = name, phone = phone))
    suspend fun deleteSupplier(supplier: Supplier) = dao.deleteSupplier(supplier)

    // İlkin qalıq
    fun getInitialBalances(supplierId: Int) = dao.getInitialBalances(supplierId)
    suspend fun addInitialBalance(supplierId: Int, amount: Double) =
        dao.insertInitialBalance(InitialBalance(supplierId = supplierId, amount = amount))
    suspend fun deleteInitialBalance(balance: InitialBalance) = dao.deleteInitialBalance(balance)

    // Əməliyyatlar
    fun getTransactions(supplierId: Int, type: String) = dao.getTransactionsByType(supplierId, type)
    suspend fun addTransaction(supplierId: Int, amount: Double, type: String) =
        dao.insertTransaction(Transaction(supplierId = supplierId, amount = amount, type = type))
    suspend fun deleteTransaction(transaction: Transaction) = dao.deleteTransaction(transaction)

    // Backup üçün məlumatları çıxar
    suspend fun getBackupData(): BackupData {
        return BackupData(
            districts = dao.getAllDistrictsList(),
            suppliers = dao.getAllSuppliersList(),
            transactions = dao.getAllTransactionsList(),
            initialBalances = dao.getAllInitialBalancesList()
        )
    }

    // Faylı Downloads qovluğuna JSON formatında yaz
    fun createBackupFileInDownloads(context: Context, data: BackupData): File {
        val json = Gson().toJson(data)

        val internalFile = File(context.filesDir, "backup_vendigoo.txt")
        internalFile.writeText(json)

        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val targetFile = File(downloadsDir, "backup_vendigoo.txt")

        try {
            if (targetFile.exists()) {
                targetFile.delete()
            }
            internalFile.copyTo(targetFile, overwrite = true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return targetFile
    }

    // Fayldan backup-ı oxu
    fun parseBackupFile(context: Context, uri: Uri): BackupData? {
        return try {
            val text = context.contentResolver
                .openInputStream(uri)
                ?.bufferedReader()
                ?.use { it.readText() }

            Gson().fromJson(text, BackupData::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Backup məlumatlarını database-ə bərpa et
    suspend fun restoreToDatabase(data: BackupData) {
        dao.deleteAllTransactions()
        dao.deleteAllSuppliers()
        dao.deleteAllDistricts()
        dao.deleteAllInitialBalances()

        dao.insertAllDistricts(data.districts)
        dao.insertAllSuppliers(data.suppliers)
        dao.insertAllTransactions(data.transactions)
        dao.insertAllInitialBalances(data.initialBalances)
    }

    // ✅ Yeni əlavə etdiyin funksiya
    suspend fun getTransactionsNow(supplierId: Int, type: String): List<Transaction> {
        return dao.getTransactionsNow(supplierId, type)
    }
}
