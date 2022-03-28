package com.inu.andoid.criminalintentnav.repository

import androidx.lifecycle.LiveData
import com.inu.andoid.criminalintentnav.data.CrimeDao
import com.inu.andoid.criminalintentnav.model.Crime

class CrimeRepository (private val crimeDao: CrimeDao){

    val readAllData: LiveData<List<Crime>> = crimeDao.readAllData()

    suspend fun addCrime(crime: Crime){
        crimeDao.addCrime(crime)
    }

    suspend fun updateCrime(crime: Crime) {
        crimeDao.updateCrime(crime)
    }

    suspend fun deleteUser(crime: Crime){
        crimeDao.deleteCrime(crime)
    }

    suspend fun deleteAllUCrimes(){
        crimeDao.deleteAllCrimes()
    }

    suspend fun resetID() {
        crimeDao.resetID()
    }
}