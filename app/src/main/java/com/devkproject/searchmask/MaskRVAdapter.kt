//package com.devkproject.searchmask
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.devkproject.searchmask.model.MaskDetailResponse
//
//class MaskRVAdapter (val context: Context, val maskDetailResponse: ArrayList<MaskDetailResponse?>) : RecyclerView.Adapter<MaskRVAdapter.ViewHolder>() {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val v = LayoutInflater.from(parent.context).inflate(R.layout.rv_item, parent,false)
//
//        return ViewHolder(v)
//    }
//
//    override fun getItemCount(): Int {
//        return maskDetailResponse.count()
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(maskDetailResponse[position], context)
//    }
//
//    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val name = view?.findViewById<TextView>(R.id.mask_name)
//        val addr = view?.findViewById<TextView>(R.id.mask_addr)
//        val remain = view?.findViewById<TextView>(R.id.mask_remain)
//        val stock = view?.findViewById<TextView>(R.id.mask_stock)
//        val create = view?.findViewById<TextView>(R.id.mask_create)
//
//        fun bind(itemMask: MaskDetailResponse?, context: Context) {
//
//            remain.text = itemMask?.remain_stat
//            name.text = itemMask?.name
//            addr.text = itemMask?.addr
//            stock.text = itemMask?.stock_at
//            create.text = itemMask?.created_at
//        }
//    }
//}