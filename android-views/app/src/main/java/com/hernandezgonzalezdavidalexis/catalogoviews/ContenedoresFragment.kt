package com.hernandezgonzalezdavidalexis.catalogoviews

import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class ContenedoresFragment : Fragment(R.layout.fragment_contenedores) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listOf(R.id.fila_0, R.id.fila_1, R.id.fila_2, R.id.columna_0, R.id.columna_1, R.id.columna_2).forEach { id ->
            view.accion(id) { view.mensaje("Pulsaste el bloque ${view.findViewById<TextView>(id).text}") }
        }
        var arriba = false
        val etiqueta = view.findViewById<TextView>(R.id.etiqueta_superpuesta)
        etiqueta.setOnClickListener {
            arriba = !arriba
            etiqueta.layoutParams = (etiqueta.layoutParams as FrameLayout.LayoutParams).apply { gravity = if (arriba) Gravity.TOP or Gravity.START else Gravity.BOTTOM or Gravity.END }
        }
        listOf(R.id.scroll_0, R.id.scroll_1, R.id.scroll_2, R.id.scroll_3, R.id.scroll_4, R.id.scroll_5, R.id.scroll_6, R.id.scroll_7, R.id.scroll_8, R.id.scroll_9, R.id.scroll_10, R.id.scroll_11).forEachIndexed { i, id ->
            view.accion(id) { view.mensaje("Elegiste la fila ${i + 1}") }
        }
        var actualizaciones = 0
        view.findViewById<MaterialToolbar>(R.id.toolbar_demo).apply {
            menu.add("Actualizar").setIcon(android.R.drawable.ic_popup_sync).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            setOnMenuItemClickListener { actualizaciones++; view.texto(R.id.estado_toolbar, "Actualizaciones: $actualizaciones"); true }
        }
        view.texto(R.id.estado_navegacion, "Inicio: bienvenida al catálogo")
        view.findViewById<BottomNavigationView>(R.id.nav_demo).setOnItemSelectedListener {
            view.texto(R.id.estado_navegacion, when (it.itemId) {
                R.id.nav_favoritos -> "Favoritos: componentes que deseas consultar"
                R.id.nav_acerca -> "Acerca de: demostración con Views y XML"
                else -> "Inicio: bienvenida al catálogo"
            }); true
        }
        var peso = 2
        val central = view.findViewById<TextView>(R.id.peso_central)
        view.accion(R.id.cambiar_peso) {
            peso = if (peso == 4) 1 else peso + 1
            central.layoutParams = (central.layoutParams as LinearLayout.LayoutParams).apply { weight = peso.toFloat() }
            central.text = peso.toString()
        }
        listOf(R.id.peso_izquierdo, R.id.peso_central, R.id.peso_derecho).forEach { id -> view.accion(id) { view.mensaje("Peso del bloque: ${view.findViewById<TextView>(id).text}") } }
    }
}
