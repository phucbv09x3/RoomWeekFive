package com.monstar_lab_lifetime.roomweekfive.model

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.monstar_lab_lifetime.roomweekfive.Interface.OnClickIt
import com.monstar_lab_lifetime.roomweekfive.R
import com.monstar_lab_lifetime.roomweekfive.adapter.SinhVienAdapter
import com.monstar_lab_lifetime.roomweekfive.database.Entitys
import com.monstar_lab_lifetime.roomweekfive.database.SinhVienDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope,
    OnClickIt {


    companion object {
        private const val REQUES_CODE = 1
    }

    private var isInsert:Boolean=false
    private var mAdapter = SinhVienAdapter(this)
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
    private var mSinhVien: MutableList<SinhVien> = mutableListOf()
    private var mGetPossi: Int = 0
    private var mGetUriImg: String = ""

    private var isCheckBoolean:Boolean=false
    private var mLisstIt: List<Entitys?>? = listOf()

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rdbtn_female.isChecked = true
        var rcy = findViewById(R.id.rcy_sinhvien) as RecyclerView
        rcy.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rcy.setHasFixedSize(true)
        var mSinhVienDatabase = SinhVienDatabase.getDatabase(this)

        //load data tư database lên recycler
        asyncTaskGetAll({
            writeFun()
        }, {
            this.mLisstIt = it
            it?.forEachIndexed { index, entitys ->
                mSinhVien.add(
                    SinhVien(
                        it[index]!!.maSv,
                        it[index]!!.name,
                        it[index]!!.date,
                        it[index]!!.gender,
                        it[index]!!.address,
                        it[index]!!.majors,
                        it[index]!!.image
                    )
                )

                if (it != null) {
                    rcy_sinhvien.adapter = mAdapter
                    mAdapter.setList(mSinhVien)
                }
                resetText()
            }

        }).execute()

        //bắt sự kiện nut back(reset recycler)
        btn_back.setOnClickListener {
            mSinhVien.clear()
            asyncTaskGetAll({
                writeFun()
            }, {
                it?.forEachIndexed { index, entitys ->
                    mSinhVien.add(
                        SinhVien(
                            it[index]!!.maSv,
                            it[index]!!.name,
                            it[index]!!.date,
                            it[index]!!.gender,
                            it[index]!!.address,
                            it[index]!!.majors,
                            it[index]!!.image
                        )
                    )

                    if (it != null) {
                        mAdapter.setList(mSinhVien)
                        rcy_sinhvien.adapter = mAdapter
                    }
                    resetText()
                }

            }).execute()
        }

        imgbtn_image.setOnClickListener {
            val inten = Intent(Intent.ACTION_OPEN_DOCUMENT)
            inten.type = "image/*"
            startActivityForResult(Intent.createChooser(inten, "Pick"), REQUES_CODE)
        }



        //Bắt sự kiện nút add
        btn_add.setOnClickListener {
            val isCheck = radio_gr.checkedRadioButtonId
            val isCheckRadio = findViewById<RadioButton>(isCheck)
            var getMasv = edt_masv.text.toString()
            var getName = edt_name.text.toString()
            var getDate = edt_date.text.toString()
            var getGender = isCheckRadio.text.toString()
            var getAdd = edt_address.text.toString()
            var getMajor = edt_majors.text.toString()
            var getUriImg = mGetUriImg
            if (getMasv.isEmpty()) {
                edt_masv.error = "Không được để trống"
            } else if (getName.isEmpty()) {
                edt_name.error = "Không được để trống"
            } else if (getDate.isEmpty()) {
                edt_date.error = "Không được để trống"
            } else if (getAdd.isEmpty()) {
                edt_address.error = "Không được để trống"
            } else if (getMajor.isEmpty()) {
                edt_majors.error = "Không được để trống"
            } else {
               asyncTaskSearch({search()},{
                   if (it?.name==getName){
                       Toast.makeText(this, "Mã Sv này đã tồn tại !", Toast.LENGTH_SHORT)
                           .show()
                   }
                   else{
                      asyncTask {
                          mSinhVienDatabase.sinhVienDao().insertAccount(
                               Entitys(
                                   maSv = getMasv,
                                   name = getName,
                                   date = getDate,
                                   gender = getGender,
                                   address = getAdd,
                                   majors = getMajor,
                                   image = getUriImg
                               )
                           )
                       }.execute()

                       Toast.makeText(this, "Add Thành Công !", Toast.LENGTH_SHORT)
                           .show()
                       mSinhVien.add(
                           SinhVien(
                               maSv = edt_masv.text.toString(),
                               name = edt_name.text.toString(),
                               date = edt_date.text.toString(),
                               gender = isCheckRadio!!.text.toString(),
                               address = edt_address.text.toString(),
                               majors = edt_majors.text.toString(),
                               image = getUriImg
                           )
                       )

                       mAdapter.setList(mSinhVien)
                       rcy.adapter = mAdapter
                       resetText()
                   }
               }).execute()

            }
        }

        //băt sự kiện nút update
        btn_update.setOnClickListener {
            val isCheck = radio_gr.checkedRadioButtonId
            val isCheckcontinue = findViewById<RadioButton>(isCheck)
            val getMasv = edt_masv.text.toString()
            var getName = edt_name.text.toString()
            var getDate = edt_date.text.toString()
            var getGender = isCheckcontinue.text.toString()
            var getAdd = edt_address.text.toString()
            var getMajor = edt_majors.text.toString()
            var getUriImg = mGetUriImg.toString()
            if (getName.isEmpty() || getDate.isEmpty() || getAdd.isEmpty() || getMajor.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin để update", Toast.LENGTH_SHORT)
                    .show()
            } else {
                asyncTaskUpdate {
                    mSinhVienDatabase.sinhVienDao().update(
                        masv = getMasv,
                        name = getName,
                        date = getDate,
                        gender = getGender,
                        address = getAdd,
                        major = getMajor,
                        image = getUriImg
                    )

                }.execute()
                mSinhVien.set(
                    mGetPossi!!, SinhVien(
                        maSv = getMasv,
                        name = getName,
                        date = getDate,
                        gender = getGender,
                        address = getAdd,
                        majors = getMajor,
                        image = getUriImg
                    )
                )
                mAdapter.notifyDataSetChanged()
                resetText()
            }
        }

        //Bắt sự kiện nút delete
        btn_delete.setOnClickListener {
            var ma = edt_masv.text.toString()
            asyncTaskDelete {
                mSinhVienDatabase.sinhVienDao().deleteSv(masv = ma)
            }.execute()
            mSinhVien.removeAt(mGetPossi!!)
            mAdapter.notifyDataSetChanged()
            resetText()
        }
//        val myCallback = object: ItemTouchHelper.SimpleCallback(0,
//            ItemTouchHelper.LEFT) {
//            override fun onMove(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder,
//                target: RecyclerView.ViewHolder
//            ): Boolean = false
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                val position:Int=viewHolder.adapterPosition
//                mSinhVien.removeAt(position)
//                mAdapter.notifyDataSetChanged()
//            }
//
//        }
//        val itemTouchHelper = ItemTouchHelper(myCallback)
//        itemTouchHelper.attachToRecyclerView(rcy)

        //Bắt sự kiện nút search
        btn_timkiem.setOnClickListener {
            asyncTaskSearch({ search() }, {
                mSinhVien.clear()
                it?.let {
                    mSinhVien.add(
                        SinhVien(
                            it.maSv,
                            it.name,
                            it.date,
                            it.gender,
                            it.address,
                            it.majors,
                            it.image
                        )
                    )

                     rcy_sinhvien.adapter = mAdapter
                    mAdapter.setList(mSinhVien)
                    mAdapter.notifyDataSetChanged()
                }

            }).execute()
            resetText()
        }
        btn_sapxep.setOnClickListener {

            asyncTaskSort({
                getList()
            }, {
                mSinhVien.clear()
                it?.let {
                    it.forEachIndexed { index, entitys ->
                        mSinhVien.add(
                            SinhVien(
                                it[index]!!.maSv,
                                it[index]!!.name,
                                it[index]!!.date,
                                it[index]!!.gender,
                                it[index]!!.address,
                                it[index]!!.majors,
                                it[index]!!.image
                            )
                        )

                        if (it != null) {
                            mAdapter.setList(mSinhVien)
                            mAdapter.notifyDataSetChanged()
                            rcy_sinhvien.adapter = mAdapter
                        }
                        resetText()

                    }
                }
            }).execute()
        }


    }

    private fun getList(): List<Entitys> {
        var listSort = SinhVienDatabase.getDatabase(this).sinhVienDao().sort()
        return listSort
    }

    private fun asyncTaskSort(callback: () -> List<Entitys?>, updateUi: (List<Entitys?>?) -> Unit) =
        object : AsyncTask<Void, Void, List<Entitys?>>() {
            override fun doInBackground(vararg params: Void?): List<Entitys?> {
                return callback()
            }

            override fun onPostExecute(result: List<Entitys?>?) {
                super.onPostExecute(result)
                updateUi(result)
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUES_CODE && resultCode == Activity.RESULT_OK && data != null) {
            var uriImg = Uri.parse(data.dataString)
            this.mGetUriImg = uriImg.toString()
            imgbtn_image.setImageURI(uriImg)
        }
    }

    private fun search(): Entitys {
        var getMasv = edt_masv.text.toString()
        var search = SinhVienDatabase.getDatabase(this).sinhVienDao().searchSv(masv = getMasv)
        return search
    }

    private fun writeFun(): List<Entitys> {
        var listEntitys = SinhVienDatabase.getDatabase(this).sinhVienDao().getAll()
        return listEntitys
    }


    private fun resetText() {
        edt_masv.setText("")
        edt_name.setText("")
        edt_date.setText("")
        edt_address.setText("")
        edt_majors.setText("")
    }


    private fun asyncTaskGetAll(
        callback: () -> List<Entitys?>,
        updateUi: (List<Entitys?>?) -> Unit
    ) =
        object : AsyncTask<Void, Void, List<Entitys?>>() {
            override fun doInBackground(vararg params: Void?): List<Entitys?> {
                return callback()
            }

            override fun onPostExecute(result: List<Entitys?>?) {
                super.onPostExecute(result)
                updateUi(result)
            }
        }

    private fun asyncTaskSearch(callback: () -> Entitys, updateUi: (Entitys?) -> Unit) =
        object : AsyncTask<Void, Void, Entitys?>() {
            override fun doInBackground(vararg params: Void?): Entitys? {
                return callback()
            }

            override fun onPostExecute(result: Entitys?) {
                super.onPostExecute(result)
                updateUi(result)
                // sinhVien.add(SinhVien(result!!.maSv,result.name,result.date,result.gender,result.address,result.majors))
            }
        }


    private fun asyncTaskUpdate(callback: () -> Unit) = object : AsyncTask<Void, Void, Entitys?>() {
        override fun doInBackground(vararg params: Void?): Entitys? {
            callback()
            return null
        }

    }

    private fun asyncTaskDelete(callback: () -> Unit) = object : AsyncTask<Void, Void, String?>() {
        override fun doInBackground(vararg params: Void?): String? {
            callback()
            return null
        }

    }

    private fun asyncTask(callback: () -> Unit) = object : AsyncTask<Void, Void, String?>() {
        override fun doInBackground(vararg params: Void?): String? {
            callback()
            return null
        }

    }

     fun onclickdate(view: View) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                c.set(year, monthOfYear, dayOfMonth)
                edt_date.setText("" + dayOfMonth + " ," + (monthOfYear + 1) + ", " + year)

            },
            year,
            month,
            day
        )
        dpd.show()
    }

    override fun Onitem(sinhVien: SinhVien, position: Int) {
        edt_masv.setText(sinhVien.maSv)
        edt_name.setText(sinhVien.name)
        edt_date.setText(sinhVien.date)
        edt_address.setText(sinhVien.address)
        edt_majors.setText(sinhVien.majors)
        imgbtn_image.setImageURI(Uri.parse(sinhVien.image))

        this.mGetPossi = position

//        val isCheck = radio_gr.checkedRadioButtonId
//        val isCheckcontinue = findViewById<RadioButton>(isCheck)
//        val getMasv = edt_masv.text.toString()
//        var getName = edt_name.text.toString()
//        var getDate = edt_date.text.toString()
//        var getGender = isCheckcontinue.text.toString()
    }
    

}