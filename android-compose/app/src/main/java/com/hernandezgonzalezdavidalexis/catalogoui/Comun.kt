package com.hernandezgonzalezdavidalexis.catalogoui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Estado compartido: conecta la Sección 1 con la Sección 4. */
data class CatalogoElemento(val id: Long, val nombre: String)

object AppState {
    private var siguienteId = 0L
    val elementos = mutableStateListOf<CatalogoElemento>().apply {
        addAll(List(15) { CatalogoElemento(siguienteId++, "Elemento ${it + 1}") })
    }

    fun agregar(nombre: String) {
        elementos.add(0, CatalogoElemento(siguienteId++, nombre))
    }

    fun completarEjemplos() {
        repeat(15) { i ->
            val nombre = "Elemento ${i + 1}"
            if (elementos.none { it.nombre == nombre }) {
                elementos.add(CatalogoElemento(siguienteId++, nombre))
            }
        }
    }

    fun restaurar() {
        elementos.clear()
        completarEjemplos()
    }
}

/** Función para mostrar un Snackbar desde cualquier pantalla. */
val LocalMensajes = compositionLocalOf<(String) -> Unit> { {} }

/** Tarjeta que documenta un elemento: nombre, explicación y demostración. */
@Composable
fun ElementoDoc(
    nombre: String,
    descripcion: String,
    demo: @Composable ColumnScope.() -> Unit
) {
    Card(Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(descripcion, style = MaterialTheme.typography.bodySmall)
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            demo()
        }
    }
}

/** Contenedor estándar de una sección, con desplazamiento y margen consistente. */
@Composable
fun SeccionLista(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        content = content
    )
}
