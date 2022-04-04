package com.inu.andoid.criminalintentnav.repository

import android.content.Context
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.inu.andoid.criminalintentnav.data.CrimeDao
import com.inu.andoid.criminalintentnav.fragments.update.UpdateFragment
import com.inu.andoid.criminalintentnav.model.Crime
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CrimeRepository constructor(context: Context, private val crimeDao: CrimeDao){
    companion object {
        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context, crimeDao: CrimeDao) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context, crimeDao)
            }
        }

        fun get(): CrimeRepository {
            return INSTANCE ?:
            throw IllegalStateException("CrimeRepository must be initialized")
        }
    }

    val readAllData: LiveData<List<Crime>> = crimeDao.readAllData()
    val fileDir = context?.cacheDir
   // val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    fun getPhotoFile(crime: Crime): File = File(fileDir, crime.photoFileName)

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