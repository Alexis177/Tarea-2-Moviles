package com.hernandezgonzalezdavidalexis.catalogoviews

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import java.util.Calendar
import java.util.Locale

class SeleccionFragment : Fragment(R.layout.fragment_seleccion) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<MaterialCheckBox>(R.id.acepto).setOnCheckedChangeListener { _, marcado -> view.texto(R.id.estado_acepto, if (marcado) "Términos aceptados" else "Términos sin aceptar") }
        val todos = view.findViewById<MaterialCheckBox>(R.id.todos)
        val hijos = listOf(R.id.canal_correo, R.id.canal_sms, R.id.canal_avisos).map { view.findViewById<MaterialCheckBox>(it) }
        var actualizando = false
        fun actualizarGrupo() {
            actualizando = true
            todos.checkedState = when { hijos.all { it.isChecked } -> MaterialCheckBox.STATE_CHECKED; hijos.none { it.isChecked } -> MaterialCheckBox.STATE_UNCHECKED; else -> MaterialCheckBox.STATE_INDETERMINATE }
            actualizando = false
        }
        hijos.first().isChecked = true
        actualizarGrupo()
        hijos.forEach { it.setOnCheckedChangeListener { _, _ -> if (!actualizando) actualizarGrupo() } }
        todos.addOnCheckedStateChangedListener { _, estado ->
            if (!actualizando) {
                actualizando = true
                hijos.forEach { it.isChecked = estado != MaterialCheckBox.STATE_UNCHECKED }
                actualizando = false; actualizarGrupo()
            }
        }
        view.findViewById<RadioGroup>(R.id.envio).setOnCheckedChangeListener { grupo, id -> view.texto(R.id.estado_envio_opcion, "Elegiste: ${grupo.findViewById<RadioButton>(id).text}") }
        view.findViewById<MaterialSwitch>(R.id.avisos).setOnCheckedChangeListener { _, activo -> view.texto(R.id.estado_avisos, if (activo) "Avisos activados" else "Avisos desactivados") }
        view.texto(R.id.estado_volumen, "Volumen de ejemplo: 40")
        view.findViewById<Slider>(R.id.volumen).addOnChangeListener { _, valor, _ -> view.texto(R.id.estado_volumen, "Volumen de ejemplo: ${valor.toInt()}") }
        view.findViewById<RangeSlider>(R.id.rango).apply {
            setValues(20f, 70f)
            view.texto(R.id.estado_rango, "Rango: 20 a 70")
            addOnChangeListener { slider, _, _ -> view.texto(R.id.estado_rango, "Rango: ${slider.values.first().toInt()} a ${slider.values.last().toInt()}") }
        }
        view.findViewById<Spinner>(R.id.talla).apply {
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf("Chica", "Mediana", "Grande"))
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
                override fun onItemSelected(parent: AdapterView<*>?, item: View?, position: Int, id: Long) { view.texto(R.id.estado_talla, "Talla: ${parent?.getItemAtPosition(position)}") }
            }
        }
        view.accion(R.id.fecha) {
            val hoy = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, a, m, d -> view.texto(R.id.estado_fecha, String.format(Locale("es", "MX"), "%02d/%02d/%04d", d, m + 1, a)) }, hoy.get(Calendar.YEAR), hoy.get(Calendar.MONTH), hoy.get(Calendar.DAY_OF_MONTH)).show()
        }
        view.accion(R.id.hora) {
            val hoy = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, h, m -> view.texto(R.id.estado_hora, String.format(Locale("es", "MX"), "%02d:%02d", h, m)) }, hoy.get(Calendar.HOUR_OF_DAY), hoy.get(Calendar.MINUTE), true).show()
        }
        view.findViewById<ChipGroup>(R.id.filtros).setOnCheckedStateChangeListener { grupo, ids ->
            view.texto(R.id.estado_filtros, "Filtros: " + ids.joinToString { grupo.findViewById<Chip>(it).text }.ifEmpty { "ninguno" })
        }
    }
}
