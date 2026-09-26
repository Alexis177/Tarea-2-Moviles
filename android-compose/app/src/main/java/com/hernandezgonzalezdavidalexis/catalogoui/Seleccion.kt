package com.hernandezgonzalezdavidalexis.catalogoui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SeleccionScreen() {
    val context = LocalContext.current
    var acepto by remember { mutableStateOf(false) }
    val canales = remember { mutableStateListOf(true, false, false) }
    var envio by remember { mutableStateOf("Estándar") }
    var notificaciones by remember { mutableStateOf(true) }
    var volumen by remember { mutableFloatStateOf(40f) }
    var rango by remember { mutableStateOf(20f..70f) }
    var talla by remember { mutableStateOf("M") }
    var abierto by remember { mutableStateOf(false) }
    var fecha by remember { mutableStateOf("Sin fecha seleccionada") }
    var hora by remember { mutableStateOf("Sin hora seleccionada") }
    val filtros = remember { mutableStateListOf("Música") }

    SeccionLista {
        ElementoDoc("Casilla de verificación", "Checkbox permite activar opciones independientes. Marca o desmarca la casilla para cambiar su estado.") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = acepto, onCheckedChange = { acepto = it })
                Text("Acepto los términos: ${if (acepto) "sí" else "no"}")
            }
        }
        ElementoDoc("Casilla indeterminada", "TriStateCheckbox representa todos, ninguno o parte de un grupo. La casilla principal cambia los tres canales a la vez.") {
            val estado = when {
                canales.all { it } -> ToggleableState.On
                canales.none { it } -> ToggleableState.Off
                else -> ToggleableState.Indeterminate
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                TriStateCheckbox(state = estado, onClick = {
                    val nuevo = estado != ToggleableState.On
                    canales.indices.forEach { canales[it] = nuevo }
                })
                Text("Todos los canales")
            }
            listOf("Correo", "SMS", "Notificaciones").forEachIndexed { i, nombre ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = canales[i], onCheckedChange = { canales[i] = it })
                    Text(nombre)
                }
            }
        }
        ElementoDoc("Botones de opción", "RadioButton permite escoger una sola alternativa dentro de un grupo. Al seleccionar otra forma de envío se desmarca la anterior.") {
            listOf("Estándar", "Rápido", "Recoger en tienda").forEach { opcion ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = envio == opcion, onClick = { envio = opcion })
                    Text(opcion)
                }
            }
            Text("Elegiste: $envio")
        }
        ElementoDoc("Interruptor", "Switch activa o desactiva una preferencia inmediatamente. El texto inferior muestra el valor actual de la demostración.") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = notificaciones, onCheckedChange = { notificaciones = it })
                Spacer(Modifier.width(12.dp))
                Text(if (notificaciones) "Avisos activados" else "Avisos desactivados")
            }
        }
        ElementoDoc("Deslizador de valor único", "Slider selecciona un número en un intervalo. Arrastra el control para cambiar el volumen de ejemplo entre 0 y 100.") {
            Slider(value = volumen, onValueChange = { volumen = it }, valueRange = 0f..100f)
            Text("Volumen de ejemplo: ${volumen.toInt()} %")
        }
        ElementoDoc("Deslizador de rango", "RangeSlider selecciona un límite inferior y otro superior. Mueve ambos extremos para definir el intervalo de precios.") {
            RangeSlider(value = rango, onValueChange = { rango = it }, valueRange = 0f..100f)
            Text("Rango: ${rango.start.toInt()} a ${rango.endInclusive.toInt()}")
        }
        ElementoDoc("Lista desplegable", "DropdownMenu presenta varias opciones en un espacio reducido. Pulsa el botón y selecciona una talla para actualizarlo.") {
            Box {
                OutlinedButton(onClick = { abierto = true }) { Text("Talla: $talla · Cambiar") }
                DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
                    listOf("CH", "M", "G", "XG").forEach { opcion ->
                        DropdownMenuItem(text = { Text(opcion) }, onClick = { talla = opcion; abierto = false })
                    }
                }
            }
        }
        ElementoDoc("Selector de fecha", "DatePickerDialog abre el calendario nativo de Android. Confirma una fecha para mostrarla; cancelar conserva la elección anterior.") {
            OutlinedButton(onClick = {
                val hoy = Calendar.getInstance()
                DatePickerDialog(context, { _, anio, mes, dia ->
                    fecha = String.format(Locale("es", "MX"), "%02d/%02d/%04d", dia, mes + 1, anio)
                }, hoy.get(Calendar.YEAR), hoy.get(Calendar.MONTH), hoy.get(Calendar.DAY_OF_MONTH)).show()
            }) { Text("Elegir fecha") }
            Text(fecha)
        }
        ElementoDoc("Selector de hora", "TimePickerDialog permite elegir horas y minutos. En esta demostración se utiliza el formato de 24 horas.") {
            OutlinedButton(onClick = {
                val ahora = Calendar.getInstance()
                TimePickerDialog(context, { _, h, m ->
                    hora = String.format(Locale("es", "MX"), "%02d:%02d", h, m)
                }, ahora.get(Calendar.HOUR_OF_DAY), ahora.get(Calendar.MINUTE), true).show()
            }) { Text("Elegir hora") }
            Text(hora)
        }
        ElementoDoc("Chips de filtro", "FilterChip permite activar varias categorías. Pulsa cada chip para añadirlo o quitarlo de la selección.") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Música", "Deporte", "Lectura").forEach { filtro ->
                    FilterChip(selected = filtro in filtros, onClick = {
                        if (filtro in filtros) filtros.remove(filtro) else filtros.add(filtro)
                    }, label = { Text(filtro) })
                }
            }
            Text("Filtros: ${filtros.joinToString().ifEmpty { "ninguno" }}")
        }
    }
}
