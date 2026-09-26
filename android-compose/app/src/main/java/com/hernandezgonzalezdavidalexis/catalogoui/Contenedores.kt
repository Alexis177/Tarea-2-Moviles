package com.hernandezgonzalezdavidalexis.catalogoui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenedoresScreen() {
    val mostrar = LocalMensajes.current
    var filaInvertida by remember { mutableStateOf(false) }
    var columnaInvertida by remember { mutableStateOf(false) }
    var arriba by remember { mutableStateOf(false) }
    var destino by remember { mutableIntStateOf(0) }
    var peso by remember { mutableFloatStateOf(2f) }
    var actualizaciones by remember { mutableIntStateOf(0) }
    SeccionLista {
        ElementoDoc("Distribución en fila", "Row coloca los elementos horizontalmente. Invierte el orden y pulsa un elemento para comprobar su posición y respuesta.") {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (if (filaInvertida) listOf("C", "B", "A") else listOf("A", "B", "C")).forEach { nombre ->
                    Button(onClick = { mostrar("Elemento $nombre de la fila") }, modifier = Modifier.weight(1f)) { Text(nombre) }
                }
            }
            TextButton(onClick = { filaInvertida = !filaInvertida }) { Text("Invertir fila") }
        }
        ElementoDoc("Distribución en columna", "Column sitúa los elementos uno debajo de otro. Cambia su orden y pulsa cada fila para ver una respuesta.") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                (if (columnaInvertida) listOf("Tres", "Dos", "Uno") else listOf("Uno", "Dos", "Tres")).forEach { nombre ->
                    OutlinedButton(onClick = { mostrar("Elemento $nombre de la columna") }, modifier = Modifier.fillMaxWidth()) { Text(nombre) }
                }
            }
            TextButton(onClick = { columnaInvertida = !columnaInvertida }) { Text("Invertir columna") }
        }
        ElementoDoc("Distribución superpuesta", "Box apila sus elementos y permite alinearlos dentro del mismo espacio. Mueve la etiqueta entre dos esquinas y pulsa el fondo.") {
            Box(Modifier.fillMaxWidth().height(140.dp).background(MaterialTheme.colorScheme.secondaryContainer).clickable { mostrar("Pulsaste el fondo de Box") }) {
                Text("Contenido de fondo", Modifier.align(Alignment.Center))
                Surface(modifier = Modifier.align(if (arriba) Alignment.TopStart else Alignment.BottomEnd).padding(8.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                    Text("Etiqueta superpuesta", Modifier.padding(8.dp))
                }
            }
            TextButton(onClick = { arriba = !arriba }) { Text("Mover etiqueta") }
        }
        ElementoDoc("Contenedor con desplazamiento vertical", "verticalScroll permite recorrer contenido que no cabe en su contenedor. Desliza dentro del recuadro y pulsa una de las doce filas.") {
            Column(Modifier.fillMaxWidth().height(180.dp).background(MaterialTheme.colorScheme.surfaceVariant).verticalScroll(rememberScrollState())) {
                repeat(12) { i ->
                    Text("Fila desplazable ${i + 1}", Modifier.fillMaxWidth().clickable { mostrar("Seleccionaste la fila ${i + 1}") }.padding(16.dp))
                }
            }
        }
        ElementoDoc("Barra superior", "TopAppBar presenta un título y acciones contextualizadas. Pulsa Actualizar para incrementar el contador de esta barra de ejemplo.") {
            TopAppBar(title = { Text("Mi catálogo") }, actions = {
                IconButton(onClick = { actualizaciones++ }) { Icon(Icons.Default.Refresh, contentDescription = "Actualizar ejemplo") }
            })
            Text("Actualizaciones: $actualizaciones")
        }
        ElementoDoc("Barra de navegación inferior", "NavigationBar permite elegir un destino principal. Esta demostración cambia el panel inferior; el menú lateral de la aplicación abre las seis secciones reales.") {
            val nombres = listOf("Inicio", "Favoritos", "Acerca de")
            val iconos = listOf(Icons.Default.Home, Icons.Default.Favorite, Icons.Default.Info)
            NavigationBar {
                nombres.forEachIndexed { i, nombre ->
                    NavigationBarItem(selected = destino == i, onClick = { destino = i }, icon = { Icon(iconos[i], contentDescription = null) }, label = { Text(nombre) })
                }
            }
            Text(when (destino) {
                0 -> "Inicio: bienvenida al catálogo interactivo."
                1 -> "Favoritos: aquí se muestran los componentes que deseas consultar."
                else -> "Acerca de: demostración con Jetpack Compose."
            }, Modifier.padding(vertical = 12.dp))
        }
        ElementoDoc("Pesos proporcionales", "Modifier.weight reparte el ancho disponible según el peso de cada elemento. Ajusta el peso central para comparar la distribución con sus vecinos.") {
            Text("Proporción: 1 : ${peso.toInt()} : 1")
            Slider(value = peso, onValueChange = { peso = it }, valueRange = 1f..4f, steps = 2)
            Row(Modifier.fillMaxWidth().height(64.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(1f, peso, 1f).forEachIndexed { i, valor ->
                    Box(Modifier.weight(valor).fillMaxHeight().background(if (i == 1) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer).clickable { mostrar("Bloque ${i + 1}, peso ${valor.toInt()}") }, contentAlignment = Alignment.Center) {
                        Text("${valor.toInt()}")
                    }
                }
            }
        }
    }
}
