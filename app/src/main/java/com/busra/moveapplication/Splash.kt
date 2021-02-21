package com.busra.moveapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.WindowManager

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //1. yöntem
        //splash ekran için bir Thread oluşturuyoruz
        /*val background = object : Thread() {
            override fun run() {
                try {
                    //threaad 5 sn yani 5000 ms uyusun
                        // Ekraanda 5 sn boyunca kalması sağlanıyor
                    Thread.sleep(5000)
                    //intent ile splash ekranından sonra MainActivity ekranı açılsın diyoruz
                    //Bu kod ile Splash Ekran açılır ve MainActivity 5 sn boyunca uyur. 5 saniye sonunda ise MainActivity ekranı açılır.
                    val intent = Intent(baseContext, MainActivity::class.java)
                    startActivity(intent)
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
        }
        //background.start() ile; uygulama her açıldığında yani SplachScreenActivitydeki onCreate metodu her çalıştığında bu fonksiyon tekrar çalışsın diyoruz.
        background.start()*/

        //2. yöntem
        /*object : CountDownTimer(1500, 3000) {
            override fun onFinish() {
                val intent = Intent(this@Splash,MainActivity::class.java)
                startActivity(intent)
            }

            override fun onTick(p0: Long) {
                Log.d("SplashActivity", p0.toString())
            }
        }.start()*/

        //3. yöntem
        //Tek seferli splash çalışıyor
        // This is used to hide the status bar and make
        // the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000) // 3000 is the delayed time in milliseconds.
    }
}