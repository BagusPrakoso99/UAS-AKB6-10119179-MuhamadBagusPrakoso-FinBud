package com.bagus.finbud.activity

import android.content.Intent
import android.os.Bundle
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bagus.finbud.R
import com.bagus.finbud.adapter.CategoryAdapter
import com.bagus.finbud.databinding.ActivityCreateBinding
import com.bagus.finbud.model.Category
import com.bagus.finbud.model.Transaction
import com.bagus.finbud.model.User
import com.bagus.finbud.preferences.PreferenceManager
import com.bagus.finbud.util.PrefUtil
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateActivity : BaseActivity() {
    final val TAG: String = "CreateActivity"

    private val binding by lazy { ActivityCreateBinding.inflate(layoutInflater) }
    private lateinit var categoryAdapter: CategoryAdapter
    private var type: String = "";
    private var category: String = "";

    private val db by lazy { Firebase.firestore }
    private val pref by lazy { PreferenceManager(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupList()
        setupListener()


    }

    override fun onStart() {
        super.onStart()
        getCategory()

    }

    private fun setupList(){
        categoryAdapter = CategoryAdapter(this, arrayListOf(), object: CategoryAdapter.AdapterListener {
            override fun onClick(category: Category) {
                this@CreateActivity.category = category.name!!;
                Log.e(TAG, this@CreateActivity.category)

            }
        })
        binding.listCategory.adapter = categoryAdapter

    }

    private fun setupListener(){

        binding.buttonIn.setOnClickListener {
            type = "IN"
            setButton( it as MaterialButton )
            setText( it as MaterialButton )
        }
        binding.buttonOut.setOnClickListener {
            type = "OUT"
            setButton( it as MaterialButton )
            setText( it as MaterialButton )
        }

        binding.buttonSave.setOnClickListener {
            if (isRequired()) addTransaction()
            else Toast.makeText(applicationContext, "Isi data dengan lengkap", Toast.LENGTH_SHORT)
                .show()
        }

    }
    private fun addTransaction(){
        progress(true)
        val transaction = Transaction(
            id = null,
            username = pref.getString( PrefUtil.pref_username )!!,
            category = category,
            type = type,
            amount = binding.editAmount.text.toString().toInt(),
            note = binding.editNote.text.toString(),
            created = Timestamp.now()
        )
        db.collection("transaction")
            .add( transaction )
            .addOnSuccessListener {
                progress(false)
                Toast.makeText(applicationContext, "Berhasil Menambahkan Transaksi", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun setButton(buttonSelected: MaterialButton){
        Log.e(TAG, type)
        listOf<MaterialButton>(binding.buttonIn, binding.buttonOut).forEach {
            it.setBackgroundColor(ContextCompat.getColor(this, R.color.blue1))
        }
        buttonSelected.setBackgroundColor(ContextCompat.getColor(this, R.color.orange1))
    }
    private fun setText(buttonSelected: MaterialButton){
        listOf<MaterialButton>(binding.buttonIn, binding.buttonOut).forEach {
            it.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
        buttonSelected.setTextColor(ContextCompat.getColor(this, R.color.black))
    }

    private fun getCategory(){
        val categories: ArrayList<Category> = arrayListOf()
        db.collection("category")
            .get()
            .addOnSuccessListener { result ->
                result.forEach { document ->
                    categories.add( Category( document.data["name"].toString() ) )
                }
                Log.e("HomeActivity", "categories $categories")
                categoryAdapter.setData( categories )
            }

    }
    private fun progress(progress: Boolean){
        when (progress) {
            true -> {
                binding.buttonSave.text = "Loading..."
                binding.buttonSave.isEnabled = false
            }
            false -> {
                binding.buttonSave.text = "SIMPAN"
                binding.buttonSave.isEnabled = true
            }
        }
    }
    private fun isRequired(): Boolean {
        return (
                binding.editAmount.text.toString().isNotEmpty() &&
                        binding.editNote.text.toString().isNotEmpty()
                )
    }

}