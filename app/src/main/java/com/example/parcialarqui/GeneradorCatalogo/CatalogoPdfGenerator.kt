/*
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

    fun generarCatalogoPdf(
        context: Context,
        categorias: List<Categoria>,
        productos: List<Producto>,
        callback: (String?) -> Unit
    ) {
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Catalogo_$timeStamp.pdf"

            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            val pdfWriter = PdfWriter(FileOutputStream(file))
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)

            // Título principal
            document.add(
                Paragraph("CATÁLOGO DE PRODUCTOS")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20f)
                    .setBold()
            )

            document.add(Paragraph("Fecha: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())}"))
            document.add(Paragraph(" "))

            // Agrupar productos por categoría
            val productosPorCategoria = productos.groupBy { it.categoriaId }

            categorias.forEach { categoria ->
                val productosCategoria = productosPorCategoria[categoria.id] ?: emptyList()

                if (productosCategoria.isNotEmpty()) {
                    // Nombre de categoría
                    document.add(
                        Paragraph(categoria.nombre.uppercase())
                            .setFontSize(16f)
                            .setBold()
                            .setTextAlignment(TextAlignment.LEFT)
                    )

                    if (categoria.descripcion.isNotBlank()) {
                        document.add(Paragraph(categoria.descripcion).setFontSize(10f))
                    }

                    // Tabla de productos
                    val table = Table(UnitValue.createPercentArray(floatArrayOf(40f, 40f, 20f)))
                        .setWidth(UnitValue.createPercentValue(100f))

                    // Headers
                    table.addHeaderCell("Producto")
                    table.addHeaderCell("Descripción")
                    table.addHeaderCell("Precio (Bs.)")

                    // Productos
                    productosCategoria.forEach { producto ->
                        table.addCell(producto.nombre)
                        table.addCell(producto.descripcion)
                        table.addCell("${producto.precio}")
                    }

                    document.add(table)
                    document.add(Paragraph(" "))
                }
            }

            // Pie de página
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
*/

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

    fun generarCatalogoPdf(
        context: Context,
        categorias: List<Categoria>,
        productos: List<Producto>,
        callback: (String?) -> Unit
    ) {
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Catalogo_$timeStamp.pdf"

            //  Guardar en carpeta privada de la app (no necesita permisos)
            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            val pdfWriter = PdfWriter(FileOutputStream(file))
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)

            // Título principal
            document.add(
                Paragraph("CATÁLOGO DE PRODUCTOS")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20f)
                    .setBold()
            )

            document.add(Paragraph("Fecha: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())}"))
            document.add(Paragraph(" "))

            // Agrupar productos por categoría
            val productosPorCategoria = productos.groupBy { it.categoriaId }

            categorias.forEach { categoria ->
                val productosCategoria = productosPorCategoria[categoria.id] ?: emptyList()

                if (productosCategoria.isNotEmpty()) {
                    // Nombre de categoría
                    document.add(
                        Paragraph(categoria.nombre.uppercase())
                            .setFontSize(16f)
                            .setBold()
                            .setTextAlignment(TextAlignment.LEFT)
                    )

                    if (categoria.descripcion.isNotBlank()) {
                        document.add(Paragraph(categoria.descripcion).setFontSize(10f))
                    }

                    // Tabla de productos
                    val table = Table(UnitValue.createPercentArray(floatArrayOf(40f, 40f, 20f)))
                        .setWidth(UnitValue.createPercentValue(100f))

                    // Headers
                    table.addHeaderCell("Producto")
                    table.addHeaderCell("Descripción")
                    table.addHeaderCell("Precio (Bs.)")

                    // Productos
                    productosCategoria.forEach { producto ->
                        table.addCell(producto.nombre)
                        table.addCell(producto.descripcion)
                        table.addCell("${producto.precio}")
                    }

                    document.add(table)
                    document.add(Paragraph(" "))
                }
            }

            // Pie de página
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
