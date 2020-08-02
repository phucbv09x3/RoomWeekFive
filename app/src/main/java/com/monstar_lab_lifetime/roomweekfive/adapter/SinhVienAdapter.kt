package com.monstar_lab_lifetime.roomweekfive.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.monstar_lab_lifetime.roomweekfive.Interface.OnClickIt
import com.monstar_lab_lifetime.roomweekfive.R
import com.monstar_lab_lifetime.roomweekfive.model.SinhVien

class SinhVienAdapter (var onClickIt: OnClickIt): RecyclerView.Adapter<SinhVienAdapter.SinhVienViewHolder>() {
    private var itemList: MutableList<SinhVien> = mutableListOf()
    fun setList(   itemList: MutableList<SinhVien>){
        this.itemList=itemList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SinhVienViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return SinhVienViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: SinhVienViewHolder, position: Int) {
    var sinhVien=itemList[position]
        holder.masv.text=sinhVien.maSv
        holder.name.text=sinhVien.name
        holder.date.text= sinhVien.date
        holder.gender.text=sinhVien.gender
        holder.address.text=sinhVien.address
        holder.majors.text=sinhVien.majors
        holder.image.setImageURI(Uri.parse(sinhVien.image))
        holder.itemView.setOnClickListener {
            onClickIt.Onitem(sinhVien,position)
        }
    }
    class SinhVienViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val masv = itemView.findViewById(R.id.tv_masv) as TextView
        val name = itemView.findViewById(R.id.tv_name) as TextView
        val date = itemView.findViewById(R.id.tv_date) as TextView
        val gender = itemView.findViewById(R.id.tv_gender) as TextView
        val address = itemView.findViewById(R.id.tv_address) as TextView
        val majors = itemView.findViewById(R.id.tv_majors) as TextView
        val image=itemView.findViewById(R.id.tv_image) as ImageView
    }

}