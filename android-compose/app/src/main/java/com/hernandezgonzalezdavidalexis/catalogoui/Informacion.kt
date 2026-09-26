package com.hernandezgonzalezdavidalexis.catalogoui

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InformacionScreen() {
    val context = LocalContext.current
    val mostrar = LocalMensajes.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    var negrita by remember { mutableStateOf(false) }
    var recortar by remember { mutableStateOf(false) }
    var progreso by remember { mutableFloatStateOf(0.4f) }
    var dialogo by remember { mutableStateOf(false) }
    var confirmado by remember { mutableStateOf(false) }
    var hoja by remember { mutableStateOf(false) }
    var eleccion by remember { mutableStateOf("Ninguna opción elegida") }
    var archivado by remember { mutableStateOf(false) }
    var visitas by remember { mutableIntStateOf(0) }
    var avisos by remember { mutableIntStateOf(3) }

    Box(Modifier.fillMaxSize()) {
        SeccionLista {
            ElementoDoc("Textos con estilos", "Text permite establecer jerarquías mediante tamaño, peso y estilo. Pulsa Alternar énfasis para cambiar la apariencia del texto de ejemplo.") {
                Text("Título destacado", style = MaterialTheme.typography.headlineSmall)
                Text("Subtítulo de la demostración", style = MaterialTheme.typography.titleMedium)
                Text("Texto que cambia de énfasis", fontWeight = if (negrita) FontWeight.Bold else FontWeight.Normal)
                Text("Nota en cursiva", style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic)
                TextButton(onClick = { negrita = !negrita }) { Text("Alternar énfasis") }
            }
            ElementoDoc("Imagen local", "Image y painterResource muestran una imagen incluida en la aplicación. Cambia entre encajar y recortar para comparar cómo se adapta al mismo espacio.") {
                TextButton(onClick = { recortar = !recortar }) { Text(if (recortar) "Modo: recortar · Cambiar" else "Modo: encajar · Cambiar") }
                Image(painter = painterResource(R.drawable.imagen_local), contentDescription = "Ilustración local del catálogo", modifier = Modifier.fillMaxWidth().height(180.dp), contentScale = if (recortar) ContentScale.Crop else ContentScale.Fit)
            }
            ElementoDoc("Imagen desde URL", "La imagen se descarga por HTTPS fuera del hilo principal. Comparte el modo de escalado anterior y muestra carga, error y reintento cuando corresponda.") {
                ImagenRemota(recortar)
            }
            ElementoDoc("Progreso determinado", "Los indicadores lineal y circular representan una fracción conocida del trabajo. Mueve el deslizador para cambiar ambos porcentajes.") {
                Text("Avance: ${(progreso * 100).toInt()} %")
                Slider(value = progreso, onValueChange = { progreso = it })
                LinearProgressIndicator(progress = { progreso }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                CircularProgressIndicator(progress = { progreso })
            }
            ElementoDoc("Progreso indeterminado", "Se utiliza cuando no se conoce cuánto falta para terminar. La animación comunica que una operación sigue en curso.") {
                var visible by remember { mutableStateOf(true) }
                TextButton(onClick = { visible = !visible }) { Text(if (visible) "Detener demostración" else "Iniciar demostración") }
                if (visible) {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                    CircularProgressIndicator()
                } else Text("Operación detenida")
            }
            ElementoDoc("Mensaje breve (Toast)", "Toast muestra un aviso nativo de corta duración. Pulsa el botón para comprobar que desaparece automáticamente.") {
                Button(onClick = { Toast.makeText(context, "Mensaje breve del catálogo", Toast.LENGTH_SHORT).show() }) { Text("Mostrar aviso") }
            }
            ElementoDoc("Snackbar con acción", "Snackbar permite responder a una operación mediante una acción. Archiva el ejemplo y pulsa Deshacer para revertir su estado.") {
                Text(if (archivado) "Ejemplo archivado" else "Ejemplo activo")
                Button(onClick = {
                    archivado = true
                    scope.launch {
                        snackbar.currentSnackbarData?.dismiss()
                        if (snackbar.showSnackbar("Ejemplo archivado", actionLabel = "Deshacer", withDismissAction = true, duration = SnackbarDuration.Long) == SnackbarResult.ActionPerformed) archivado = false
                    }
                }) { Text("Archivar ejemplo") }
            }
            ElementoDoc("Diálogo de confirmación", "AlertDialog solicita una decisión antes de ejecutar una acción. Confirmar cambia el resultado visible y cancelar conserva el estado.") {
                OutlinedButton(onClick = { dialogo = true }) { Text("Abrir confirmación") }
                Text(if (confirmado) "Acción confirmada" else "Acción sin confirmar")
            }
            ElementoDoc("Hoja inferior", "ModalBottomSheet presenta opciones desde la parte inferior de la pantalla. Selecciona una opción para cerrar la hoja y mostrar el resultado.") {
                OutlinedButton(onClick = { hoja = true }) { Text("Abrir opciones") }
                Text(eleccion)
            }
            ElementoDoc("Tarjeta", "Card agrupa información relacionada en una superficie. Esta tarjeta es interactiva: cuenta las veces que la pulsas.") {
                Card(Modifier.fillMaxWidth().clickable { visitas++ }) {
                    Text("Pulsa esta tarjeta · $visitas visitas", Modifier.padding(16.dp))
                }
            }
            ElementoDoc("Separador", "HorizontalDivider diferencia grupos de contenido. Alterna su grosor para observar el cambio entre una separación discreta y una marcada.") {
                var grueso by remember { mutableStateOf(false) }
                Text("Contenido superior")
                HorizontalDivider(Modifier.padding(vertical = 12.dp), thickness = if (grueso) 4.dp else 1.dp)
                Text("Contenido inferior")
                TextButton(onClick = { grueso = !grueso }) { Text("Cambiar grosor") }
            }
            ElementoDoc("Distintivo numérico", "Badge muestra una cantidad pendiente junto a un ícono. Pulsa el ícono para leer los avisos o agrega uno nuevo.") {
                BadgedBox(badge = { if (avisos > 0) Badge { Text("$avisos") } }) {
                    IconButton(onClick = { avisos = 0; mostrar("Avisos marcados como leídos") }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Leer avisos")
                    }
                }
                TextButton(onClick = { avisos++ }) { Text("Agregar aviso") }
                Text("Elementos compartidos desde Entrada de texto: ${AppState.elementos.size}")
            }
            Spacer(Modifier.height(64.dp))
        }
        SnackbarHost(snackbar, Modifier.align(androidx.compose.ui.Alignment.BottomCenter))
    }
    if (dialogo) AlertDialog(
        onDismissRequest = { dialogo = false },
        title = { Text("¿Confirmar la acción?") },
        text = { Text("Esta demostración actualizará el mensaje de confirmación.") },
        confirmButton = { TextButton(onClick = { confirmado = true; dialogo = false }) { Text("Confirmar") } },
        dismissButton = { TextButton(onClick = { dialogo = false }) { Text("Cancelar") } },
    )
    if (hoja) ModalBottomSheet(onDismissRequest = { hoja = false }) {
        Text("Elige una acción", Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
        listOf("Compartir", "Guardar", "Copiar").forEach { opcion ->
            TextButton(onClick = { eleccion = "Elegiste: $opcion"; hoja = false }, modifier = Modifier.fillMaxWidth()) { Text(opcion) }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ImagenRemota(recortar: Boolean) {
    var intento by remember { mutableIntStateOf(0) }
    var imagen by remember { mutableStateOf<ImageBitmap?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(true) }
    LaunchedEffect(intento) {
        cargando = true
        error = null
        try {
            imagen = withContext(Dispatchers.IO) {
                val conexion = URL("https://picsum.photos/id/237/600/300").openConnection() as HttpURLConnection
                try {
                    conexion.connectTimeout = 10000
                    conexion.readTimeout = 10000
                    conexion.instanceFollowRedirects = true
                    conexion.inputStream.use { BitmapFactory.decodeStream(it)?.asImageBitmap() }
                        ?: throw IllegalStateException("Imagen no válida")
                } finally { conexion.disconnect() }
            }
        } catch (e: CancellationException) { throw e }
        catch (_: Exception) { error = "No se pudo cargar la imagen. Comprueba tu conexión y vuelve a intentar." }
        finally { cargando = false }
    }
    when {
        cargando -> { CircularProgressIndicator(); Text("Cargando imagen…") }
        error != null -> Text(error!!)
        imagen != null -> Image(bitmap = imagen!!, contentDescription = "Fotografía de un perro descargada desde una URL", modifier = Modifier.fillMaxWidth().height(180.dp), contentScale = if (recortar) ContentScale.Crop else ContentScale.Fit)
    }
    TextButton(onClick = { intento++ }, enabled = !cargando) { Text("Volver a cargar") }
}
