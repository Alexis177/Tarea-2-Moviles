package com.hernandezgonzalezdavidalexis.catalogoui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BotonesScreen() {
    val mostrar = LocalMensajes.current
    val separacion = Arrangement.spacedBy(8.dp)

    SeccionLista {
        ElementoDoc(
            "Relleno, contorno y texto",
            "Tres niveles de énfasis: el relleno para la acción principal, " +
                "el contorno para la secundaria y el de texto para la menos importante."
        ) {
            FlowRow(horizontalArrangement = separacion, verticalArrangement = separacion) {
                Button(onClick = { mostrar("Botón relleno") }) { Text("Relleno") }
                OutlinedButton(onClick = { mostrar("Botón con contorno") }) { Text("Contorno") }
                TextButton(onClick = { mostrar("Botón de texto") }) { Text("Texto") }
            }
        }

        ElementoDoc(
            "Botones con ícono",
            "Pueden mostrar solo un ícono (acciones compactas) o ícono más texto (más claridad)."
        ) {
            FlowRow(horizontalArrangement = separacion, verticalArrangement = separacion) {
                FilledIconButton(onClick = { mostrar("Ícono: favorito") }) {
                    Icon(Icons.Default.Favorite, contentDescription = "Favorito")
                }
                Button(onClick = { mostrar("Ícono + texto: compartir") }) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Compartir")
                }
            }
        }

        ElementoDoc(
            "Botón de acción flotante",
            "Representa la acción principal de la pantalla. " +
                "La versión extendida añade una etiqueta de texto."
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FloatingActionButton(onClick = { mostrar("FAB normal") }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                }
                ExtendedFloatingActionButton(
                    onClick = { mostrar("FAB extendido") },
                    icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    text = { Text("Redactar") }
                )
            }
        }

        ElementoDoc(
            "Selector segmentado",
            "Grupo de botones donde solo uno puede estar activo. " +
                "Útil para alternar entre vistas u opciones cortas."
        ) {
            var modo by remember { mutableStateOf(0) }
            val opciones = listOf("Día" to Icons.Default.LightMode, "Noche" to Icons.Default.DarkMode)
            SingleChoiceSegmentedButtonRow {
                opciones.forEachIndexed { i, (nombre, icono) ->
                    SegmentedButton(
                        selected = modo == i,
                        onClick = { modo = i },
                        shape = SegmentedButtonDefaults.itemShape(i, opciones.size),
                        icon = { Icon(icono, contentDescription = null, Modifier.size(18.dp)) }
                    ) { Text(nombre) }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Seleccionado: ${opciones[modo].first}")
        }

        ElementoDoc(
            "Deshabilitado y en carga",
            "Un botón con enabled = false se ve atenuado. " +
                "El de carga bloquea la pulsación mientras trabaja."
        ) {
            var cargando by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()
            FlowRow(horizontalArrangement = separacion, verticalArrangement = separacion) {
                Button(onClick = {}, enabled = false) { Text("Deshabilitado") }
                Button(
                    enabled = !cargando,
                    onClick = {
                        scope.launch {
                            cargando = true
                            delay(2000)
                            cargando = false
                            mostrar("Envío completado")
                        }
                    }
                ) {
                    if (cargando) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    else Text("Enviar")
                }
            }
        }
    }
}
