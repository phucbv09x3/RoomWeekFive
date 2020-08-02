package com.monstar_lab_lifetime.roomweekfive.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.monstar_lab_lifetime.roomweekfive.database.SinhVienDAO
import com.monstar_lab_lifetime.roomweekfive.model.MainActivity


@Database(entities= [Entitys::class],version = 3,exportSchema = false)
abstract  class SinhVienDatabase :RoomDatabase(){

    abstract fun sinhVienDao(): SinhVienDAO
    companion object {
        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: SinhVienDatabase? = null


        fun getDatabase(context: Context): SinhVienDatabase {
            val tempInstance =
                INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context!!.applicationContext,
                    SinhVienDatabase::class.java,
                    "sinhvien_database"
                ) .fallbackToDestructiveMigration().build()
                //allowMainThreadQueries : tránh exeption . vì nó k cho truy xuât database trên mani thread
                INSTANCE = instance
                return instance
            }
        }
    }
}