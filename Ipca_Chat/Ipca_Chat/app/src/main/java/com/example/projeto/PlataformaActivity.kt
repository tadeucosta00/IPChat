package com.example.projeto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView

class PlataformaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plataforma)

        val webview = findViewById<WebView>(R.id.web)
        webview.loadUrl("https://portal.ipca.pt/Intranet/Home/PublicIndex")

    }
}