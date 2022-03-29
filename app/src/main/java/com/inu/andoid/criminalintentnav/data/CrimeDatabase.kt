package com.inu.andoid.criminalintentnav.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.inu.andoid.criminalintentnav.model.Crime

@Database(entities = [Crime::class], version = 2, exportSchema = false)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDatabase: RoomDatabase() {

    abstract fun crimeDao(): CrimeDao

    companion object{
        @Volatile
        private var INSTANCE: CrimeDatabase? = null

        fun getDatabase(context: Context): CrimeDatabase {
           val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CrimeDatabase::class.java,
                    "crime_database"
                ).addMigrations(migration_1_2).build()
                INSTANCE = instance
                return instance
            }
        }

        val migration_1_2 = object: Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE crime_table ADD COLUMN suspect TEXT NOT NULL DEFAULT ''"
                )
            }
        }
    }
}