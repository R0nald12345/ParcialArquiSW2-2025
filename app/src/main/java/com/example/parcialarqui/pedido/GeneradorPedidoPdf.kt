package com.example.parcialarqui.pedido

import android.content.Context
import android.os.Environment
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import java.io.File

class GeneradorPedidoPdf {
    fun generarPdf(context: Context, pedido: PedidoCompleto, callback: (String?) -> Unit) {
        try {
            val fileName = "Pedido_${String.format("%03d", pedido.id)}.pdf"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)

            val writer = PdfWriter(file)
            val pdf = PdfDocument(writer)
            val doc = Document(pdf)

            doc.add(Paragraph("PEDIDO #${String.format("%03d", pedido.id)}").setFontSize(20f).setBold())
            doc.add(Paragraph("Fecha: ${pedido.fecha}"))
            doc.add(Paragraph("Cliente: ${pedido.clienteNombre}"))
            doc.add(Paragraph("Total: Bs. ${String.format("%.2f", pedido.monto)}"))

            doc.close()
            callback(file.absolutePath)
        } catch (e: Exception) {
            callback(null)
        }
    }

}


