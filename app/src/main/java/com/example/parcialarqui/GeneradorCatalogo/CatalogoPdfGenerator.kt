

package com.example.parcialarqui.GeneradorCatalogo

import android.content.Context
import android.os.Environment
import com.example.parcialarqui.categoria.Categoria
import com.example.parcialarqui.producto.Producto
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class CatalogoPdfGenerator {

    fun generarCatalogoCategoriaPdf(
        context: Context,
        categoria: com.example.parcialarqui.categoria.Categoria,
        productos: List<Producto>,
        callback: (String?) -> Unit
    ) {
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Catalogo_${categoria.nombre}_$timeStamp.pdf"

            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            val pdfWriter = PdfWriter(FileOutputStream(file))
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)

            // Título
            document.add(
                Paragraph("CATÁLOGO DE ${categoria.nombre.uppercase()}")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20f)
                    .setBold()
            )
            if (categoria.descripcion.isNotBlank()) {
                document.add(Paragraph(categoria.descripcion))
            }
            document.add(Paragraph("Fecha: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())}"))
            document.add(Paragraph(" "))

            // Tabla de productos
            val table = Table(UnitValue.createPercentArray(floatArrayOf(40f, 40f, 20f)))
                .setWidth(UnitValue.createPercentValue(100f))

            table.addHeaderCell("Producto")
            table.addHeaderCell("Descripción")
            table.addHeaderCell("Precio (Bs.)")

            productos.forEach { producto ->
                table.addCell(producto.nombre)
                table.addCell(producto.descripcion)
                table.addCell("${producto.precio}")
            }

            document.add(table)

            document.add(Paragraph(" "))
            document.add(
                Paragraph("¡Contáctanos para realizar tu pedido!")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(12f)
                    .setBold()
            )

            document.close()
            callback(file.absolutePath)

        } catch (e: Exception) {
            e.printStackTrace()
            callback(null)
        }
    }

    // ⭐ NUEVO: Método para generar PDF general sin categoría específica
    fun generarCatalogoPdf(
        context: Context,
        titulo: String,
        productos: List<Producto>,
        callback: (String?) -> Unit
    ) {
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Catalogo_${titulo.replace(" ", "_")}_$timeStamp.pdf"

            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            val pdfWriter = PdfWriter(FileOutputStream(file))
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)

            // Título
            document.add(
                Paragraph(titulo.uppercase())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20f)
                    .setBold()
            )
            document.add(Paragraph("Fecha: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())}"))
            document.add(Paragraph(" "))

            // Tabla de productos
            val table = Table(UnitValue.createPercentArray(floatArrayOf(40f, 40f, 20f)))
                .setWidth(UnitValue.createPercentValue(100f))

            table.addHeaderCell("Producto")
            table.addHeaderCell("Descripción")
            table.addHeaderCell("Precio (Bs.)")

            productos.forEach { producto ->
                table.addCell(producto.nombre)
                table.addCell(producto.descripcion)
                table.addCell("${producto.precio}")
            }

            document.add(table)

            document.add(Paragraph(" "))
            document.add(
                Paragraph("¡Contáctanos para realizar tu pedido!")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(12f)
                    .setBold()
            )

            document.close()
            callback(file.absolutePath)

        } catch (e: Exception) {
            e.printStackTrace()
            callback(null)
        }
    }
}