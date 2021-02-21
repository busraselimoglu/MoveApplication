package com.busra.moveapplication

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.cardview.view.*
import kotlinx.android.synthetic.main.fragment_film_ekle.view.*
import java.util.*
import kotlin.collections.ArrayList
import androidx.cardview.widget.CardView
import kotlinx.android.synthetic.main.fragment_film_ekle.*
import android.widget.RatingBar
import androidx.core.content.ContextCompat.startActivity


class film_listesi_recycler(var moviename : ArrayList<String>,
                            var id : ArrayList<Int>,
                            var konu : ArrayList<String>,
                            val gorsel : ArrayList<ByteArray>,
                            var puan : ArrayList<Float>
                            ):RecyclerView.Adapter<film_listesi_recycler.FilmHolder>(),Filterable {
    var movieFilterList = ArrayList<String>()
    var subFilterList = ArrayList<String>()

    init {
        movieFilterList = moviename
        subFilterList = konu
    }

    class FilmHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmHolder {
        //inflater
        val inf= LayoutInflater.from(parent.context)
        val mview=inf.inflate(R.layout.cardview,parent,false)
        return FilmHolder(mview)
    }

    override fun onBindViewHolder(holder: FilmHolder, position: Int) {

        holder.itemView.filmadicard.text = movieFilterList.get(position)
        holder.itemView.filmkonucard.text = subFilterList.get(position)
        //görsel gösterme
        val bitmap = BitmapFactory.decodeByteArray(gorsel.get(position),0,gorsel.get(position).size)
        holder.itemView.gorseleee.setImageBitmap(bitmap)

        //puanlama
        holder.itemView.ratingbarcard.rating = puan.get(position)

        //paylaş
        holder.itemView.paylascard.setOnClickListener {
            val inte = Intent(Intent.ACTION_SEND)
            inte.putExtra(Intent.EXTRA_TEXT,movieFilterList.get(position))
            inte.type = "text/plain"
            startActivity(it.context,inte,null)
        }

        holder.itemView.setOnClickListener {
            val action = film_listesiDirections.actionFilmListesiToFilmEkle("recyclerdangeldim",id[position])
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return movieFilterList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    movieFilterList = moviename
                } else {
                    var i =0
                    val resultList = ArrayList<String>()
                    val resultL = ArrayList<String>()
                    for (row in movieFilterList) {
                        if (row.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                            resultL.add(subFilterList[i])
                        }
                        i++
                    }
                    movieFilterList = resultList
                    subFilterList = resultL
                }
                val filterResults = FilterResults()
                filterResults.values = movieFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                movieFilterList = results?.values as ArrayList<String>
                notifyDataSetChanged()
            }
        }
    }
}