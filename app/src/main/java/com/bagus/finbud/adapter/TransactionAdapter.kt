package com.bagus.finbud.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bagus.finbud.R
import com.bagus.finbud.databinding.AdapterTransactionBinding
import com.bagus.finbud.model.Transaction
import com.bagus.finbud.util.amountFormat
import com.bagus.finbud.util.timestampToString

class TransactionAdapter(
    var transactions: ArrayList<Transaction>,
    var listener: AdapterListener?
): RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionAdapter.ViewHolder {
        return ViewHolder(
            AdapterTransactionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: TransactionAdapter.ViewHolder, position: Int) {
        val transaction = transactions[position]

        if (transaction.type.toUpperCase() == "IN") holder.binding.imageType.setImageResource(R.drawable.ic_in)
        else holder.binding.imageType.setImageResource(R.drawable.ic_out)

        holder.binding.textNote.text = transaction.note
        holder.binding.textCategory.text = transaction.category
        holder.binding.textAmount.text = amountFormat( transaction.amount )
        holder.binding.textDate.text = timestampToString( transaction.created!! )

        holder.binding.container.setOnClickListener {
            listener?.onClick( transaction )
        }
        holder.binding.container.setOnLongClickListener() {
            listener?.onLongClick( transaction )
            true
        }

    }

    override fun getItemCount() = transactions.size


    class ViewHolder(val binding: AdapterTransactionBinding): RecyclerView.ViewHolder(binding.root)

    public fun setData(data: List<Transaction>) {
        transactions.clear()
        transactions.addAll(data)
        notifyDataSetChanged()

    }

    interface AdapterListener {
        fun onClick(transaction: Transaction)
        fun onLongClick(transaction: Transaction)

    }
}