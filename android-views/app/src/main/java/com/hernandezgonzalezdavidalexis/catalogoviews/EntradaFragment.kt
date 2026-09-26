package com.hernandezgonzalezdavidalexis.catalogoviews

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout

class EntradaFragment : Fragment(R.layout.fragment_entrada) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val nombre = view.findViewById<EditText>(R.id.nombre)
        view.accion(R.id.agregar) {
            val valor = nombre.text.toString().trim()
            if (valor.isBlank()) view.findViewById<TextInputLayout>(R.id.layout_nombre).error = "Escribe un nombre"
            else {
                view.findViewById<TextInputLayout>(R.id.layout_nombre).error = null
                CatalogoDatos.agregar(valor); nombre.text.clear()
                view.mensaje("$valor agregado a Listas")
            }
        }
        view.findViewById<EditText>(R.id.usuario).doAfterTextChanged {
            view.findViewById<TextInputLayout>(R.id.layout_usuario).error = if (!it.isNullOrEmpty() && it.length < 3) "Mínimo 3 caracteres" else null
        }
        view.findViewById<AutoCompleteTextView>(R.id.pais).apply {
            setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, listOf("México", "Argentina", "Colombia", "Chile", "España", "Perú")))
            setOnItemClickListener { parent, _, posicion, _ -> view.mensaje("Elegiste: ${parent.getItemAtPosition(posicion)}") }
        }
        view.findViewById<SearchView>(R.id.busqueda).setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean { view.mensaje("Búsqueda: ${query.orEmpty()}"); return true }
            override fun onQueryTextChange(newText: String?): Boolean { view.texto(R.id.resultado_busqueda, if (newText.isNullOrBlank()) "Sin búsqueda" else "Buscando: $newText"); return true }
        })
    }
}
