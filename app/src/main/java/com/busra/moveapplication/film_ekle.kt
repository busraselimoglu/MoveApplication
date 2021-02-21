package com.busra.moveapplication

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.RatingBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_film_ekle.*
import java.io.ByteArrayOutputStream
import android.content.SharedPreferences





class film_ekle : Fragment() {
    var secilengorsel : Uri? = null
    var bitmapp : Bitmap? = null
    var secilenid : Int? = null
    lateinit var izledimkt : CheckBox
    lateinit var izleyecegimkt : CheckBox
    lateinit var sharedpref : SharedPreferences
    lateinit var izleme :String
    var izlemedurumu : String ? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_film_ekle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //CheckBox ile ilgili
        /*arguments?.let {
            secilenid= film_ekleArgs.fromBundle(it).id
            activity?.let {
                izledimkt = it.findViewById(R.id.izledim)
                izleyecegimkt = it.findViewById(R.id.izleyecegim)
                sharedpref =it.getSharedPreferences("key",Context.MODE_PRIVATE)
                izlemedurumu = sharedpref.getString("key",null)
                if (izlemedurumu == "izledim"){
                    izledimkt.isChecked = true
                    izleyecegimkt.isChecked = false
                }
                if (izlemedurumu == "izleyecegim"){
                    izleyecegimkt.isChecked = true
                    izledimkt.isChecked = false
                }
            }
        }*/
        arguments?.let {
            var id=film_ekleArgs.fromBundle(it).id
            sharedpref = activity!!.getSharedPreferences("check", Context.MODE_PRIVATE)
            izlemedurumu = sharedpref.getString("${id}",null)
            if(izlemedurumu=="izledim"){
                izledim.isChecked=true
            }
            else if(izlemedurumu=="izleyecegim"){
                izleyecegim.isChecked=true
            }
        }
        kaydet.setOnClickListener {
            kaydet(it)
        }
        guncelle.setOnClickListener {
            guncelle(it)
            Radiobuttoncontrol()
        }
        gorselsecimi.setOnClickListener {
            gorselsec()
        }
        kamera.setOnClickListener {
            camerasec()
        }
        sil.setOnClickListener {
            sil(it)
        }
        arguments?.let {
            secilenid= film_ekleArgs.fromBundle(it).id
            var gelenBilgi = film_ekleArgs.fromBundle(it).bilgi

            if (gelenBilgi.equals("menudengeldim")){
                //yeni bir film eklemeye geldi
                film_adi.setText("")
                film_konu.setText("")
                // butonları görünürlüğü aktif ediyor
                kaydet.visibility = View.VISIBLE
                kamera.visibility = View.VISIBLE
                radiogrp.visibility = View.INVISIBLE
                val gorselSecme = BitmapFactory.decodeResource(context?.resources,R.drawable.gorselsecimi)
                gorselsecimi.setImageBitmap(gorselSecme)
            }else{
                // daha önce yazdığımız filmleri görüntüle
                kaydet.visibility = View.INVISIBLE
                kamera.visibility = View.INVISIBLE
                context?.let {
                    try {
                        val db = it.openOrCreateDatabase("FilmListe", Context.MODE_PRIVATE,null)
                        val cursor =db.rawQuery("SELECT * FROM filmler WHERE id = ?", arrayOf(secilenid.toString()))
                        val filmIsmiIndex= cursor.getColumnIndex("filmismi")
                        val filmkonusuIndex = cursor.getColumnIndex("filmkonusu")
                        val filmGorseli= cursor.getColumnIndex("gorsel")
                        val filmratingIndex = cursor.getColumnIndex("puan")
                        while (cursor.moveToNext()){
                            film_adi.setText(cursor.getString(filmIsmiIndex))
                            film_konu.setText(cursor.getString(filmkonusuIndex))
                            val byteDizisi = cursor.getBlob(filmGorseli)
                            rating.rating =cursor.getFloat(filmratingIndex)
                            val bitmap = BitmapFactory.decodeByteArray(byteDizisi,0,byteDizisi.size)
                            gorselsecimi.setImageBitmap(bitmap)
                        }
                        cursor.close()
                    }catch (e : Exception){
                        e.printStackTrace()
                    }
                }
            }
            paylas.setOnClickListener {
                val inte = Intent(Intent.ACTION_SEND)
                inte.putExtra(Intent.EXTRA_TEXT,secilenid)
                inte.type = "text/plain"
                startActivity(inte)
            }
        }
    }
    /*fun checkcontrols(){
        arguments?.let {
            secilenid= film_ekleArgs.fromBundle(it).id
            activity?.let {
                if (izledimkt.isChecked){
                    izleme = "izledim"
                }
                if (izleyecegimkt.isChecked){
                    izleme="izleyecegim"
                }
                sharedpref = it.getSharedPreferences("key",Context.MODE_PRIVATE)
                sharedpref.edit().putString("key", izleme).apply()
            }
        }

    }*/

    fun Radiobuttoncontrol(){
        val selectedId=radiogrp.checkedRadioButtonId
        arguments?.let {
            var id=film_ekleArgs.fromBundle(it).id
            if (selectedId==R.id.izledim){
                izleme="izledim"
            }
            else if (selectedId==R.id.izleyecegim){
                izleme="izleyecegim"
            }
            sharedpref = activity!!.getSharedPreferences("check", Context.MODE_PRIVATE)
            sharedpref.edit().putString("${id}",izleme).apply()
        }
    }
    fun kaydet(view: View){
        var filmadi = film_adi.text.toString()
        var filmkonu = film_konu.text.toString()
        var filmpuan = rating.rating
        try {
            context?.let {
                val database=it.openOrCreateDatabase("FilmListe", Context.MODE_PRIVATE,null)
                database.execSQL("CREATE TABLE IF NOT EXISTS filmler(id INTEGER PRIMARY KEY, filmismi VARCHAR, filmkonusu VARCHAR, gorsel BLOB, puan FLOAT)")
                val values = ContentValues()
                values.put("filmismi",filmadi)
                values.put("filmkonusu",filmkonu)
                values.put("puan",filmpuan)
                if (bitmapp != null){
                    var kucukbitmap = gorselkucultme(300,bitmapp!!)
                    var outputStream = ByteArrayOutputStream()
                    kucukbitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
                    val byteDizisi = outputStream.toByteArray()
                    values.put("gorsel",byteDizisi)
                }
                else{
                    val GSbitmap = BitmapFactory.decodeResource(context?.resources,R.drawable.gorselsecimi)
                    var kucukbitmap = gorselkucultme(300,GSbitmap)
                    var outputStream = ByteArrayOutputStream()
                    kucukbitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
                    val byteDizisi = outputStream.toByteArray()
                    values.put("gorsel",byteDizisi)
                }
                database.insert("filmler",null, values)
                database.close()
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
        // kayıt işlemi bittikten sonra liste fragment geri dönmek için fun oluşturuyoruz.
        var action = film_ekleDirections.actionFilmEkleToFilmListesi()
        Navigation.findNavController(view).navigate(action)
        Toast.makeText(context,"Başarılı bir şekilde kaydedildi",Toast.LENGTH_LONG).show()
    }

    fun sil(view: View){
        arguments?.let {
            var secilenid= film_ekleArgs.fromBundle(it).id
            context?.let {
                val uyarimesaji = AlertDialog.Builder(context)
                uyarimesaji.setTitle("Silme işlemi gerçekleşmek üzere")
                uyarimesaji.setMessage("Silmek istediğinize emin misiniz?")
                uyarimesaji.setPositiveButton("EVET",DialogInterface.OnClickListener { dialog, which ->
                    Toast.makeText(context,"Siliniyor",Toast.LENGTH_SHORT).show()
                    try {
                        val db = it.openOrCreateDatabase("FilmListe", Context.MODE_PRIVATE,null)
                        val cursor =db.rawQuery("DELETE FROM filmler WHERE id = ?", arrayOf(secilenid.toString()))
                        val filmIsmiIndex= cursor.getColumnIndex("filmismi")
                        val filmkonusuIndex = cursor.getColumnIndex("filmkonusu")
                        val filmGorseli= cursor.getColumnIndex("gorsel")
                        while (cursor.moveToNext()) {
                            film_adi.setText(cursor.getString(filmIsmiIndex))
                            film_konu.setText(cursor.getString(filmkonusuIndex))
                            val byteDizisi = cursor.getBlob(filmGorseli)
                            val bitmap = BitmapFactory.decodeByteArray(byteDizisi,0,byteDizisi.size)
                            gorselsecimi.setImageBitmap(bitmap)
                        }
                        cursor.close()
                    }catch (e : Exception){
                        e.printStackTrace()
                    }
                    // Silme işlemi bittikten sonra liste fragment geri dönmek için fun oluşturuyoruz.
                    var action = film_ekleDirections.actionFilmEkleToFilmListesi()
                    Navigation.findNavController(view).navigate(action)
                    Toast.makeText(context,"Başarılı bir şekilde silindi",Toast.LENGTH_LONG).show()
                })
                uyarimesaji.setNegativeButton("HAYIR",DialogInterface.OnClickListener { dialog, which ->
                    // Silme işlemi iptal edildikten sonra liste fragment geri dönmek için fun oluşturuyoruz.
                    var action = film_ekleDirections.actionFilmEkleToFilmListesi()
                    Navigation.findNavController(view).navigate(action)
                    Toast.makeText(context,"Silme işlemi iptal edildi",Toast.LENGTH_LONG).show()
                })
                uyarimesaji.show()
            }
        }
    }

    fun guncelle(view: View){
        var filmadi = film_adi.text.toString()
        var filmkonu = film_konu.text.toString()
        var filmpuan = rating.rating
        arguments?.let {
            var secilenid= film_ekleArgs.fromBundle(it).id
            context?.let {
                try {
                    val database=it.openOrCreateDatabase("FilmListe", Context.MODE_PRIVATE,null)
                    val values = ContentValues()
                    values.put("filmismi",filmadi)
                    values.put("filmkonusu",filmkonu)
                    values.put("puan",filmpuan)
                    if (bitmapp != null){
                        var kucukbitmap = gorselkucultme(300,bitmapp!!)
                        var outputStream = ByteArrayOutputStream()
                        kucukbitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
                        val byteDizisi = outputStream.toByteArray()
                        values.put("gorsel",byteDizisi)}
                    database.update("filmler",values,"id = ?", arrayOf(secilenid.toString()))
                    database.close()
                }catch (e :Exception){
                    e.printStackTrace()}
                // Güncelle işlemi bittikten sonra liste fragment geri dönmek için fun oluşturuyoruz.
                var action = film_ekleDirections.actionFilmEkleToFilmListesi()
                Navigation.findNavController(view).navigate(action)
                Toast.makeText(context,"Başarılı bir şekilde güncellendi",Toast.LENGTH_LONG).show()
            }
        }
    }

    fun gorselsec(){
        context?.let {
            if (ContextCompat.checkSelfPermission(it,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                // izin yok
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
            }else{
                val int = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(int,2)
            }
        }
    }

    fun camerasec(){
        context?.let {
            if (ContextCompat.checkSelfPermission(it,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                //izin yok
                requestPermissions(arrayOf(Manifest.permission.CAMERA),3)
            }else{
                val values = ContentValues()
                values.put(MediaStore.Images.Media.TITLE, "New Picture")
                secilengorsel = it.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                //camera intent
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, 4)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==1){
            if (grantResults.size>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val int = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(int,2)
            }
        }

        //camera
        else if (requestCode==3){
            if(grantResults.size>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //izin verildi, galeriye erişimi var
                val galeri= Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeri,4)
                Toast.makeText(context, "Permission allowed", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(context, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==2 && resultCode == Activity.RESULT_OK && data != null){
            secilengorsel = data.data
            if (secilengorsel != null){
                try {
                    context?.let {
                        if (Build.VERSION.SDK_INT >= 28){
                            val source = ImageDecoder.createSource(it.contentResolver,secilengorsel!!)
                            bitmapp = ImageDecoder.decodeBitmap(source)
                            gorselsecimi.setImageBitmap(bitmapp)

                        }else{
                            bitmapp = MediaStore.Images.Media.getBitmap(it.contentResolver,secilengorsel)
                            gorselsecimi.setImageBitmap(bitmapp)
                        }
                    }
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
        }
        else if (requestCode==4 && resultCode == Activity.RESULT_OK && data != null){
            bitmapp = data.extras?.get("data") as Bitmap
            gorselsecimi.setImageBitmap(bitmapp)
        }
    }

    fun gorselkucultme(maximum: Int,kucultmebitmap : Bitmap): Bitmap{
        var width = kucultmebitmap.width
        var height = kucultmebitmap.height
        var bitmaporani = width.toDouble()/height.toDouble()

        if (bitmaporani>1){
            //yatay
            width = maximum
            val kisaltilmisheight = width/bitmaporani
            height=kisaltilmisheight.toInt()
        }else{
            //dikey
            height = maximum
            val kisatilmiswidth = height / bitmaporani
            width = kisatilmiswidth.toInt()
        }
        return Bitmap.createScaledBitmap(kucultmebitmap,width,height,false)
    }

}