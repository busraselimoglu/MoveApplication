package com.busra.moveapplication

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.cardview.*
import kotlinx.android.synthetic.main.fragment_film_listesi.*


class film_listesi : Fragment() {
    var filmadii = ArrayList<String>()
    var id = ArrayList<Int>()
    private lateinit var listeAdapter : film_listesi_recycler
    var konus = ArrayList<String>()
    var gl = ArrayList<ByteArray>()
    var puan = ArrayList<Float>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_film_listesi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Recycler ile bağlama
        listeAdapter = film_listesi_recycler(filmadii,id,konus,gl,puan)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = listeAdapter


        arama.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                listeAdapter.filter.filter(newText)
                listeAdapter.notifyDataSetChanged()
                return false
            }
        })
        sqlverialma()
    }
    fun sqlverialma(){
        try {
            activity?.let {
                val database = it.openOrCreateDatabase("FilmListe", Context.MODE_PRIVATE,null)
                val cursor = database.rawQuery("SELECT * FROM filmler",null)
                val filmismiIndex = cursor.getColumnIndex("filmismi")
                val filmidIndex = cursor.getColumnIndex("id")
                val filmkonuIndex = cursor.getColumnIndex("filmkonusu")
                val gorselIndex = cursor.getColumnIndex("gorsel")
                val puanIndex= cursor.getColumnIndex("puan")

                //önceden içnde veri varsa temizle
                filmadii.clear()
                id.clear()
                konus.clear()
                gl.clear()
                puan.clear()

                while (cursor.moveToNext()){
                    filmadii.add(cursor.getString(filmismiIndex))
                    id.add(cursor.getInt(filmidIndex))
                    konus.add(cursor.getString(filmkonuIndex))
                    gl.add(cursor.getBlob(gorselIndex))
                    puan.add(cursor.getFloat(puanIndex))
                }
                //yeni veriler eklediğinde güncellemeyi sağlıyor
                listeAdapter.notifyDataSetChanged()
                cursor.close()
            }
        }catch (e :Exception){
            e.printStackTrace()
        }
    }
}
