package com.inu.andoid.criminalintentnav.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.inu.andoid.criminalintentnav.data.CrimeDatabase
import com.inu.andoid.criminalintentnav.repository.CrimeRepository
import com.inu.andoid.criminalintentnav.model.Crime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class CrimeViewModel(application: Application): AndroidViewModel(application) {

    val readAllData : LiveData<List<Crime>>
    private val repository: CrimeRepository

    init {
        val crimeDao = CrimeDatabase.getDatabase(application).crimeDao()
        repository  = CrimeRepository(crimeDao)
        readAllData = repository.readAllData
    }

    fun getPhotoFile(crime: Crime): File {
        return repository.getPhotoFile(crime)
    }

    fun addCrime(crime: Crime){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addCrime(crime)
        }
    }

    fun updateCrime(crime: Crime){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCrime(crime)
        }
    }

    fun deleteCrime(crime: Crime){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteUser(crime)
        }
    }

    fun deleteAllCrimes(){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteAllUCrimes()
        }
    }

    fun resetID(){
        viewModelScope.launch(Dispatchers.IO){
            repository.resetID()
        }
    }
}