package com.inu.andoid.criminalintentnav.data

import android.os.FileObserver.DELETE
import androidx.lifecycle.LiveData
import androidx.room.*
import com.inu.andoid.criminalintentnav.model.Crime

@Dao
interface CrimeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCrime(crime: Crime)

    @Update
    suspend fun updateCrime(crime: Crime)

    @Delete
    suspend fun deleteCrime(crime: Crime)

//    @Query("DELETE FROM crime_table")
    @Query("DELETE FROM crime_table")
    suspend fun deleteAllCrimes()


    @Query("Update sqlite_sequence SET seq = 0 WHERE name = 'crime_table'")
    suspend fun resetID()

    @Query("SELECT * FROM crime_table ORDER BY id ASC")
    fun readAllData() : LiveData<List<Crime>>

    // SQLite Auto Increment Reset
  //  @Query(DELETE FROM ) // SQLITE_SEQUENCE WHERE NAME = " + TABLE_NAME)
}