package com.inu.andoid.criminalintentnav.repository

import android.app.Application
import android.content.Context
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import com.inu.andoid.criminalintentnav.MainActivity
import com.inu.andoid.criminalintentnav.data.CrimeDao
import com.inu.andoid.criminalintentnav.fragments.update.UpdateFragment
import com.inu.andoid.criminalintentnav.model.Crime
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CrimeRepository(private val crimeDao: CrimeDao): Application(){


    val readAllData: LiveData<List<Crime>> = crimeDao.readAllData()

//    var filePath : String = file.absolutePath

    fun getPhotoFile(crime: Crime): File = File(UpdateFragment().fileDir, crime.photoFileName)
  /* fun getPhotoFile(crime: Crime): File = File.createTempFile(
       "JPEG_${timeStamp}_",
       ".jpg",
       storageDir
   )*/

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