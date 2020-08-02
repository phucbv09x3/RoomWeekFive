package com.monstar_lab_lifetime.roomweekfive.database

import androidx.room.*
import com.monstar_lab_lifetime.roomweekfive.database.Entitys

@Dao
interface SinhVienDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAccount(entitys: Entitys)

    @Query("select * from sinhvien")
    fun getAll(): List<Entitys>

    @Query("select * from sinhvien where masv=:masv")
    fun searchSv(masv: String): Entitys

    @Query("delete from sinhvien where masv=:masv")
    fun deleteSv(masv: String)

    @Query("update sinhvien set name=:name,date=:date,gender=:gender,address=:address,majors=:major,image=:image where masv=:masv ")
    fun update(
        name: String,
        date: String,
        gender: String,
        address: String,
        major: String,
        image: String,
        masv: String
    )

    @Query("select * from sinhvien order by name desc")
    fun sort(): List<Entitys>


}