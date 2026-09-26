package com.hernandezgonzalezdavidalexis.catalogoviews

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BotonesFragment : Fragment(R.layout.fragment_botones) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapOf(R.id.relleno to "Botón relleno", R.id.contorno to "Botón con contorno", R.id.texto to "Botón de texto", R.id.icono to "Compartir con ícono", R.id.icono_texto to "Compartir con ícono y texto", R.id.fab to "Agregar con FAB", R.id.fab_extendido to "Redactar con FAB extendido").forEach { (id, mensaje) -> view.accion(id) { view.mensaje(mensaje) } }
        view.texto(R.id.modo_elegido, "Seleccionado: Día")
        view.findViewById<MaterialButtonToggleGroup>(R.id.segmentado).addOnButtonCheckedListener { _, id, seleccionado ->
            if (seleccionado) view.texto(R.id.modo_elegido, "Seleccionado: ${if (id == R.id.dia) "Día" else "Noche"}")
        }
        val enviar = view.findViewById<MaterialButton>(R.id.enviar)
        enviar.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                enviar.isEnabled = false
                enviar.text = "Enviando…"
                view.findViewById<View>(R.id.carga_boton).visibility = View.VISIBLE
                view.texto(R.id.estado_envio, "Operación en curso")
                delay(2000)
                enviar.isEnabled = true; enviar.text = "Enviar"
                view.findViewById<View>(R.id.carga_boton).visibility = View.GONE
                view.texto(R.id.estado_envio, "Envío completado")
                view.mensaje("Envío completado")
            }
        }
    }
}
