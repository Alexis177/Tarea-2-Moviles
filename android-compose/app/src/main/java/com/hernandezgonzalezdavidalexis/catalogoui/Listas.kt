package com.hernandezgonzalezdavidalexis.catalogoui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListasScreen() {
    val pager = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val mensaje = LocalMensajes.current
    var detalle by remember { mutableStateOf<CatalogoElemento?>(null) }
    var actualizando by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        Text("Pestañas deslizables", Modifier.padding(start = 16.dp, top = 8.dp), style = MaterialTheme.typography.titleMedium)
        Text("TabRow y HorizontalPager muestran tres colecciones. Pulsa una pestaña o desliza su contenido; los nombres capturados en Entrada de texto aparecen aquí.", Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.bodySmall)
        TabRow(selectedTabIndex = pager.currentPage) {
            listOf("Lista", "Cuadrícula", "Secciones").forEachIndexed { i, titulo ->
                Tab(selected = pager.currentPage == i, onClick = { scope.launch { pager.animateScrollToPage(i) } }, text = { Text(titulo) })
            }
        }
        HorizontalPager(state = pager, modifier = Modifier.weight(1f)) { pagina ->
            when (pagina) {
                0 -> PullToRefreshBox(isRefreshing = actualizando, onRefresh = {
                    if (!actualizando) scope.launch {
                        actualizando = true
                        try {
                            delay(800)
                            AppState.completarEjemplos()
                            mensaje("Lista actualizada; se conservaron los elementos capturados")
                        } finally { actualizando = false }
                    }
                }, modifier = Modifier.fillMaxSize()) {
                    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                        item {
                            ElementoDoc("Lista vertical y detalle", "LazyColumn muestra inicialmente quince elementos. Pulsa una fila para abrir su información en un diálogo.") {
                                Text("Total: ${AppState.elementos.size}")
                            }
                            ElementoDoc("Deslizar para eliminar y arrastrar para actualizar", "Desliza una fila horizontalmente para borrarla. Desde el inicio de la lista, arrastra hacia abajo para recuperar los ejemplos sin perder tus entradas.") {
                                Text("Desliza dentro de una fila para eliminar; fuera de ella puedes cambiar de pestaña.")
                            }
                            ElementoDoc("Estado vacío", "Una colección sin datos muestra una ilustración y una explicación. Vacía la lista para verlo y usa Restaurar para recuperar los quince ejemplos.") {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(onClick = { AppState.elementos.clear() }) { Text("Vaciar") }
                                    Button(onClick = { AppState.restaurar() }) { Text("Restaurar") }
                                }
                            }
                        }
                        if (AppState.elementos.isEmpty()) {
                            item {
                                Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Inbox, contentDescription = "Bandeja vacía", modifier = Modifier.size(72.dp))
                                    Text("No hay elementos", style = MaterialTheme.typography.titleLarge)
                                    Text("Agrega un nombre desde Entrada de texto o pulsa Restaurar.")
                                }
                            }
                        }
                        items(AppState.elementos, key = { it.id }) { elemento ->
                            FilaDeslizable(elemento, onDetalle = { detalle = elemento }, onEliminar = {
                                AppState.elementos.remove(elemento)
                                mensaje("${elemento.nombre} eliminado")
                            })
                        }
                    }
                }
                1 -> Column(Modifier.fillMaxSize().padding(16.dp)) {
                    ElementoDoc("Cuadrícula", "LazyVerticalGrid distribuye la colección en columnas. Pulsa una tarjeta para consultar su detalle; comparte los datos con la lista vertical.") {
                        Text("${AppState.elementos.size} elementos · 2 columnas")
                    }
                    if (AppState.elementos.isEmpty()) Text("Sin elementos. Regresa a Lista y pulsa Restaurar.")
                    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(AppState.elementos, key = { it.id }) { elemento ->
                            Card(Modifier.fillMaxWidth().clickable { detalle = elemento }) {
                                Text(elemento.nombre, Modifier.padding(20.dp))
                            }
                        }
                    }
                }
                else -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                    item {
                        ElementoDoc("Lista con encabezados", "Una colección puede mezclar encabezados y filas de datos. Aquí hay dos grupos con estilos distintos; pulsa una fila para abrir su detalle.") { Text("Tipos de elemento: encabezado y dato") }
                    }
                    val grupos = listOf("Primeros elementos" to AppState.elementos.take(8), "Más elementos" to AppState.elementos.drop(8))
                    grupos.forEach { (titulo, datos) ->
                        item(key = titulo, contentType = "encabezado") {
                            Text(titulo, Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer).padding(12.dp), style = MaterialTheme.typography.titleMedium)
                        }
                        if (datos.isEmpty()) item { Text("No hay elementos en este grupo", Modifier.padding(12.dp)) }
                        items(datos, key = { it.id }, contentType = { "dato" }) { elemento ->
                            ListItem(headlineContent = { Text(elemento.nombre) }, supportingContent = { Text("Pulsa para consultar") }, modifier = Modifier.clickable { detalle = elemento })
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
    detalle?.let { elemento ->
        AlertDialog(onDismissRequest = { detalle = null }, title = { Text("Detalle del elemento") }, text = { Text("Nombre: ${elemento.nombre}\nIdentificador: ${elemento.id}") }, confirmButton = { TextButton(onClick = { detalle = null }) { Text("Cerrar") } })
    }
}

@Composable
private fun FilaDeslizable(elemento: CatalogoElemento, onDetalle: () -> Unit, onEliminar: () -> Unit) {
    var desplazamiento by remember(elemento.id) { mutableFloatStateOf(0f) }
    val umbral = with(LocalDensity.current) { 90.dp.toPx() }
    val eliminarActual by rememberUpdatedState(onEliminar)
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.errorContainer)) {
        Text("Eliminar", Modifier.align(Alignment.Center).padding(16.dp))
        ListItem(
            headlineContent = { Text(elemento.nombre) },
            supportingContent = { Text("Pulsa para detalle · desliza para eliminar") },
            modifier = Modifier.offset { IntOffset(desplazamiento.roundToInt(), 0) }
                .pointerInput(elemento.id, umbral) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (abs(desplazamiento) >= umbral) eliminarActual()
                            desplazamiento = 0f
                        },
                        onDragCancel = { desplazamiento = 0f },
                        onHorizontalDrag = { cambio, distancia -> cambio.consume(); desplazamiento += distancia },
                    )
                }.clickable(onClick = onDetalle),
        )
    }
    HorizontalDivider()
}
