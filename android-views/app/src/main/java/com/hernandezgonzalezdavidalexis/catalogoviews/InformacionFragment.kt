package com.hernandezgonzalezdavidalexis.catalogoviews

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDivider
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

@SuppressLint("UnsafeOptInUsageError")
class InformacionFragment : Fragment(R.layout.fragment_informacion) {
    private var descarga: Job? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var negrita = false
        view.accion(R.id.alternar_enfasis) {
            negrita = !negrita
            view.findViewById<TextView>(R.id.texto_enfasis).setTypeface(null, if (negrita) Typeface.BOLD else Typeface.NORMAL)
        }
        val remota = view.findViewById<ImageView>(R.id.imagen_remota)
        var recortar = false
        view.accion(R.id.cambiar_escala) {
            recortar = !recortar
            val escala = if (recortar) ImageView.ScaleType.CENTER_CROP else ImageView.ScaleType.FIT_CENTER
            remota.scaleType = escala
            view.findViewById<ImageView>(R.id.imagen_local).scaleType = escala
            view.mensaje(if (recortar) "Modo: recortar" else "Modo: encajar")
        }
        fun cargarImagen() {
            descarga?.cancel()
            descarga = viewLifecycleOwner.lifecycleScope.launch {
                view.texto(R.id.estado_imagen, "Cargando imagen…")
                try {
                    val bitmap = withContext(Dispatchers.IO) {
                        val conexion = URL("https://picsum.photos/id/237/600/300").openConnection() as HttpURLConnection
                        try {
                            conexion.connectTimeout = 10000; conexion.readTimeout = 10000
                            conexion.inputStream.use { BitmapFactory.decodeStream(it) } ?: throw IllegalStateException("Imagen no válida")
                        } finally { conexion.disconnect() }
                    }
                    remota.setImageBitmap(bitmap)
                    view.texto(R.id.estado_imagen, "Imagen remota cargada")
                } catch (e: CancellationException) { throw e }
                catch (_: Exception) { view.texto(R.id.estado_imagen, "No se pudo cargar. Comprueba tu conexión y pulsa Recargar.") }
            }
        }
        view.accion(R.id.recargar_imagen, ::cargarImagen)
        cargarImagen()
        view.texto(R.id.estado_progreso, "Avance: 40 %")
        view.findViewById<Slider>(R.id.avance).addOnChangeListener { _, valor, _ ->
            view.findViewById<LinearProgressIndicator>(R.id.progreso_lineal).setProgressCompat(valor.toInt(), true)
            view.findViewById<CircularProgressIndicator>(R.id.progreso_circular).setProgressCompat(valor.toInt(), true)
            view.texto(R.id.estado_progreso, "Avance: ${valor.toInt()} %")
        }
        view.accion(R.id.alternar_progreso) {
            val indicador = view.findViewById<View>(R.id.lineal_indeterminado)
            val visible = if (indicador.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            indicador.visibility = visible; view.findViewById<View>(R.id.circular_indeterminado).visibility = visible
        }
        view.accion(R.id.mostrar_toast) { Toast.makeText(requireContext(), "Aviso breve del catálogo", Toast.LENGTH_SHORT).show() }
        view.texto(R.id.estado_archivo, "Ejemplo activo")
        view.accion(R.id.mostrar_snackbar) {
            view.texto(R.id.estado_archivo, "Ejemplo archivado")
            Snackbar.make(view, "Ejemplo archivado", Snackbar.LENGTH_LONG).setAction("Deshacer") { view.texto(R.id.estado_archivo, "Ejemplo activo") }.show()
        }
        view.accion(R.id.mostrar_dialogo) {
            MaterialAlertDialogBuilder(requireContext()).setTitle("¿Confirmar la acción?").setMessage("Actualizará el estado de esta demostración.")
                .setPositiveButton("Confirmar") { _, _ -> view.texto(R.id.estado_confirmacion, "Acción confirmada") }
                .setNegativeButton("Cancelar") { _, _ -> view.mensaje("Acción cancelada") }.show()
        }
        view.accion(R.id.mostrar_hoja) {
            val dialogo = BottomSheetDialog(requireContext())
            val contenido = layoutInflater.inflate(R.layout.hoja_opciones, null)
            dialogo.setContentView(contenido)
            mapOf(R.id.opcion_compartir to "Compartir", R.id.opcion_guardar to "Guardar", R.id.opcion_copiar to "Copiar").forEach { (id, texto) ->
                contenido.accion(id) { view.texto(R.id.estado_hoja, "Elegiste: $texto"); dialogo.dismiss() }
            }
            dialogo.show()
        }
        var visitas = 0
        view.accion(R.id.tarjeta_interactiva) { visitas++; view.texto(R.id.visitas_tarjeta, "Pulsa la tarjeta · $visitas visitas") }
        var grueso = false
        view.accion(R.id.alternar_separador) {
            grueso = !grueso
            view.findViewById<MaterialDivider>(R.id.separador).dividerThickness = ((if (grueso) 4 else 1) * resources.displayMetrics.density).toInt()
        }
        val icono = view.findViewById<ImageButton>(R.id.badge_icono)
        val marco = view.findViewById<FrameLayout>(R.id.badge_contenedor)
        val badge = BadgeDrawable.create(requireContext()).apply { number = 3 }
        icono.post { if (this.view === view) BadgeUtils.attachBadgeDrawable(badge, icono, marco) }
        var avisos = 3
        view.texto(R.id.estado_badge, "3 avisos pendientes")
        view.accion(R.id.badge_icono) { avisos = 0; badge.isVisible = false; view.texto(R.id.estado_badge, "Avisos leídos") }
        view.accion(R.id.agregar_aviso) { avisos++; badge.number = avisos; badge.isVisible = true; view.texto(R.id.estado_badge, "$avisos avisos pendientes") }
    }
    override fun onDestroyView() { descarga?.cancel(); descarga = null; super.onDestroyView() }
}
