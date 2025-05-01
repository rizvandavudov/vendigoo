package com.example.vendigoo.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vendigoo.data.entities.District
import com.example.vendigoo.data.entities.InitialBalance
import com.example.vendigoo.data.entities.Supplier
import com.example.vendigoo.data.entities.Transaction
import com.example.vendigoo.data.entities.database.WholesaleDatabase
import com.example.vendigoo.repository.WholesaleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class WholesaleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: WholesaleRepository
    private val context: Context = application.applicationContext

    init {
        val dao = WholesaleDatabase.getDatabase(application).wholesaleDao()
        repository = WholesaleRepository(dao)
    }

    // Rayonlar
    val districts = repository.getAllDistricts().flowOn(Dispatchers.IO)
    fun addDistrict(name: String) = viewModelScope.launch {
        repository.addDistrict(name)
        backupDataToDownloads(context)
    }
    fun deleteDistrict(district: District) = viewModelScope.launch {
        repository.deleteDistrict(district)
        backupDataToDownloads(context)
    }

    // Təchizatçılar
    fun getSuppliers(districtId: Int) = repository.getSuppliers(districtId)
    fun addSupplier(districtId: Int, name: String, phone: String) = viewModelScope.launch {
        repository.addSupplier(districtId, name, phone)
        backupDataToDownloads(context)
    }
    fun deleteSupplier(supplier: Supplier) = viewModelScope.launch {
        repository.deleteSupplier(supplier)
        backupDataToDownloads(context)
    }

    // İlkin qalıq
    fun getInitialBalances(supplierId: Int) = repository.getInitialBalances(supplierId)
    fun addInitialBalance(supplierId: Int, amount: Double) = viewModelScope.launch {
        repository.addInitialBalance(supplierId, amount)
        backupDataToDownloads(context)
    }
    fun deleteInitialBalance(balance: InitialBalance) = viewModelScope.launch {
        repository.deleteInitialBalance(balance)
        backupDataToDownloads(context)
    }

    // Əməliyyatlar
    fun getTransactions(supplierId: Int, type: String) = repository.getTransactions(supplierId, type)
    fun addTransaction(supplierId: Int, amount: Double, type: String) = viewModelScope.launch {
        repository.addTransaction(supplierId, amount, type)
        backupDataToDownloads(context)
    }
    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.deleteTransaction(transaction)
        backupDataToDownloads(context)
    }

    // Rayon ekranı üçün lazımlı metodlar (Real-time)
    val allSuppliers = repository.getAllSuppliersFlow().flowOn(Dispatchers.IO)
    val allBalances = repository.getAllInitialBalancesFlow().flowOn(Dispatchers.IO)

    suspend fun getTransactionsNow(supplierId: Int, type: String): List<Transaction> {
        return repository.getTransactionsNow(supplierId, type)
    }

    fun backupDataToDownloads(context: Context) = viewModelScope.launch {
        try {
            val data = repository.getBackupData()
            val file = repository.createBackupFileInDownloads(context, data)

            val toast = Toast.makeText(context, "✅", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.TOP or Gravity.END, 50, 100)
            toast.show()

            val uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "text/plain")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Faylı aç"))

        } catch (e: Exception) {
            Toast.makeText(context, "✅ ", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    fun restoreBackupFile(context: Context, uri: Uri) = viewModelScope.launch {
        val backupData = repository.parseBackupFile(context, uri)
        if (backupData != null) {
            repository.restoreToDatabase(backupData)
            Toast.makeText(context, "Bərpa edildi!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Fayl uyğun deyil və ya zədəlidir!", Toast.LENGTH_LONG).show()
        }
    }
}
