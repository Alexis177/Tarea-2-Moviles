package com.hernandezgonzalezdavidalexis.catalogoui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

private val ancho = Modifier.fillMaxWidth()
private val paises = listOf("México", "Argentina", "Colombia", "Chile", "España", "Perú", "Uruguay")

@Composable
fun EntradaTextoScreen() {
    val mostrar = LocalMensajes.current

    SeccionLista {
        ElementoDoc(
            "Campo de texto simple",
            "Permite escribir una línea de texto. La etiqueta flota al enfocar. " +
                "Aquí, lo escrito se agrega a la lista de la Sección 4."
        ) {
            var texto by remember { mutableStateOf("") }
            OutlinedTextField(
                value = texto, onValueChange = { texto = it }, modifier = ancho,
                label = { Text("Nombre") }, placeholder = { Text("Escribe un elemento") },
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            FilledTonalButton(
                modifier = Modifier.align(Alignment.End),
                onClick = {
                    val t = texto.trim()
                    if (t.isEmpty()) mostrar("Escribe algo primero")
                    else {
                        AppState.agregar(t)
                        texto = ""
                        mostrar("\"$t\" agregado a Listas (Sección 4)")
                    }
                }
            ) { Text("Agregar a la lista") }
        }

        ElementoDoc(
            "Campo con validación",
            "Muestra un mensaje de error visible cuando el valor no cumple la regla. " +
                "Escribe menos de 3 caracteres para ver el error."
        ) {
            var usuario by remember { mutableStateOf("") }
            val error = usuario.isNotEmpty() && usuario.length < 3
            OutlinedTextField(
                value = usuario, onValueChange = { usuario = it }, modifier = ancho,
                label = { Text("Usuario") }, singleLine = true, isError = error,
                supportingText = { if (error) Text("Mínimo 3 caracteres") }
            )
        }

        ElementoDoc(
            "Campo de contraseña",
            "Oculta el contenido con puntos. El ícono del ojo alterna entre mostrar y ocultar."
        ) {
            var clave by remember { mutableStateOf("") }
            var ocultar by remember { mutableStateOf(true) }
            OutlinedTextField(
                value = clave, onValueChange = { clave = it }, modifier = ancho,
                label = { Text("Contraseña") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation =
                    if (ocultar) PasswordVisualTransformation() else VisualTransformation.None,
                trailingIcon = {
                    IconButton(onClick = { ocultar = !ocultar }) {
                        Icon(
                            if (ocultar) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (ocultar) "Mostrar contraseña" else "Ocultar contraseña"
                        )
                    }
                }
            )
        }

        ElementoDoc(
            "Tipos de teclado",
            "El parámetro keyboardType cambia el teclado en pantalla según el dato esperado."
        ) {
            var num by remember { mutableStateOf("") }
            var correo by remember { mutableStateOf("") }
            var tel by remember { mutableStateOf("") }
            OutlinedTextField(num, { num = it }, ancho, singleLine = true,
                label = { Text("Numérico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(correo, { correo = it }, ancho, singleLine = true,
                label = { Text("Correo electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(tel, { tel = it }, ancho, singleLine = true,
                label = { Text("Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
        }

        ElementoDoc(
            "Campo multilínea",
            "Admite varias líneas de texto; crece hasta el máximo indicado y luego se desplaza."
        ) {
            var coment by remember { mutableStateOf("") }
            OutlinedTextField(
                value = coment, onValueChange = { coment = it }, modifier = ancho,
                label = { Text("Comentarios") }, minLines = 3, maxLines = 5
            )
        }

        ElementoDoc(
            "Campo con sugerencias",
            "Muestra opciones que coinciden con lo escrito. Prueba con \"a\" o \"ch\"."
        ) {
            var pais by remember { mutableStateOf("") }
            val sugerencias = if (pais.isBlank()) emptyList()
            else paises.filter { it.contains(pais, ignoreCase = true) && !it.equals(pais, true) }
            OutlinedTextField(
                value = pais, onValueChange = { pais = it }, modifier = ancho,
                label = { Text("País") }, singleLine = true
            )
            if (sugerencias.isNotEmpty()) {
                Card(Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    Column {
                        sugerencias.forEach { s ->
                            ListItem(
                                headlineContent = { Text(s) },
                                modifier = Modifier.clickable {
                                    pais = s
                                    mostrar("Elegiste: $s")
                                }
                            )
                        }
                    }
                }
            }
        }

        ElementoDoc(
            "Barra de búsqueda",
            "Campo especializado para consultas, con ícono de búsqueda y botón para limpiar."
        ) {
            var consulta by remember { mutableStateOf("") }
            OutlinedTextField(
                value = consulta, onValueChange = { consulta = it }, modifier = ancho,
                placeholder = { Text("Buscar…") }, singleLine = true,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (consulta.isNotEmpty()) {
                        IconButton(onClick = { consulta = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar")
                        }
                    }
                }
            )
            Spacer(Modifier.height(8.dp))
            Text(if (consulta.isEmpty()) "Sin búsqueda" else "Buscando: $consulta")
        }
    }
}
