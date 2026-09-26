package com.hernandezgonzalezdavidalexis.catalogoviews

import android.view.View
import android.widget.TextView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.snackbar.Snackbar

data class ElementoCatalogo(val id: Long, val nombre: String)

/** Estado compartido en memoria; los identificadores distinguen nombres repetidos. */
object CatalogoDatos {
    private var siguiente = 0L
    private val datos = mutableListOf<ElementoCatalogo>()
    private val observadores = mutableSetOf<() -> Unit>()
    val elementos: List<ElementoCatalogo> get() = datos.toList()
    init { crearEjemplos() }

    private fun crearEjemplos() { repeat(15) { datos.add(ElementoCatalogo(siguiente++, "Elemento ${it + 1}")) } }
    private fun notificar() { observadores.toList().forEach { it() } }
    fun agregar(nombre: String) {
        if (nombre.isBlank()) return
        datos.add(0, ElementoCatalogo(siguiente++, nombre.trim())); notificar()
    }
    fun eliminar(elemento: ElementoCatalogo) { if (datos.remove(elemento)) notificar() }
    fun reinsertar(indice: Int, elemento: ElementoCatalogo) {
        if (datos.any { it.id == elemento.id }) return
        datos.add(indice.coerceIn(0, datos.size), elemento); notificar()
    }
    fun vaciar() { datos.clear(); notificar() }
    fun restaurar() { datos.clear(); crearEjemplos(); notificar() }
    fun observar(owner: LifecycleOwner, accion: () -> Unit) {
        observadores.add(accion)
        owner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) { observadores.remove(accion); owner.lifecycle.removeObserver(this) }
        })
        accion()
    }
}

fun View.mensaje(texto: String) { Snackbar.make(this, texto, Snackbar.LENGTH_SHORT).show() }
fun View.texto(id: Int, texto: String) { findViewById<TextView>(id).text = texto }
fun View.accion(id: Int, accion: () -> Unit) { findViewById<View>(id).setOnClickListener { accion() } }
