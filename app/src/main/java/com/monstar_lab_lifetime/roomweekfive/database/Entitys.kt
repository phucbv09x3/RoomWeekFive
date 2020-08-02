package com.monstar_lab_lifetime.roomweekfive.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sinhvien")
data class Entitys(@PrimaryKey(autoGenerate = true)var id:Int=0,
                   @ColumnInfo(name = "masv")var maSv:String,
                   @ColumnInfo(name = "name") var name:String,
                   @ColumnInfo(name = "date")var date:String,
                   @ColumnInfo(name = "gender")var gender:String,
                   @ColumnInfo(name = "address") var address: String,
                   @ColumnInfo(name = "majors")var majors:String,
                   @ColumnInfo(name = "image")var image:String) {
}