package com.example.parcialarqui.GeneradorCatalogo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

class WhatsAppHelper {


    companion object {
        fun compartirPdfPorWhatsApp(context: Context, rutaPdf: String, numeroTelefono: String = "") {
            try {
                val file = File(rutaPdf)
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_TEXT, "¡Aquí tienes nuestro catálogo de productos! 📋")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    setPackage("com.whatsapp")

                    // 👉 Solo poner el número si fue pasado
                    if (numeroTelefono.isNotEmpty()) {
                        putExtra("jid", "$numeroTelefono@s.whatsapp.net")
                    }
                }

                context.startActivity(Intent.createChooser(intent, "Compartir con WhatsApp"))

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
            }
        }
    }



}
