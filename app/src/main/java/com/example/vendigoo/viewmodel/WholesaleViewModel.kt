package com.example.vendigoo.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
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

    init {
        val dao = WholesaleDatabase.getDatabase(application).wholesaleDao()
        repository = WholesaleRepository(dao)
    }

    // Rayonlar
    val districts = repository.getAllDistricts().flowOn(Dispatchers.IO)
    fun addDistrict(name: String) = viewModelScope.launch { repository.addDistrict(name) }
    fun deleteDistrict(district: District) = viewModelScope.launch { repository.deleteDistrict(district) }

    // Təchizatçılar
    fun getSuppliers(districtId: Int) = repository.getSuppliers(districtId)
    fun addSupplier(districtId: Int, name: String, phone: String) = viewModelScope.launch {
        repository.addSupplier(districtId, name, phone)
    }
    fun deleteSupplier(supplier: Supplier) = viewModelScope.launch { repository.deleteSupplier(supplier) }

    // İlkin qalıq
    fun getInitialBalances(supplierId: Int) = repository.getInitialBalances(supplierId)
    fun addInitialBalance(supplierId: Int, amount: Double) = viewModelScope.launch {
        repository.addInitialBalance(supplierId, amount)
    }
    fun deleteInitialBalance(balance: InitialBalance) = viewModelScope.launch {
        repository.deleteInitialBalance(balance)
    }

    // Əməliyyatlar
    fun getTransactions(supplierId: Int, type: String) = repository.getTransactions(supplierId, type)
    fun addTransaction(supplierId: Int, amount: Double, type: String) = viewModelScope.launch {
        repository.addTransaction(supplierId, amount, type)
    }
    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.deleteTransaction(transaction)
    }

    fun backupDataToDownloads(context: Context) = viewModelScope.launch {
        try {
            val data = repository.getBackupData()
            val file = repository.createBackupFileInDownloads(context, data)

            Toast.makeText(context, "Yedəkləndi: ${file.name}", Toast.LENGTH_LONG).show()

            // 👉 Qovluğu aç
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.fromFile(file.parentFile), "*/*")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(context, "Qovluğu aç Butonuna Kilik edin!", Toast.LENGTH_LONG).show()
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
