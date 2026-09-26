package com.hernandezgonzalezdavidalexis.catalogoui

import android.os.Bundle
import android.content.Context
import android.content.res.Configuration
import java.util.Locale
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SmartButton
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        val configuracion = Configuration(newBase.resources.configuration)
        configuracion.setLocale(Locale("es", "MX"))
        super.attachBaseContext(newBase.createConfigurationContext(configuracion))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { CatalogoTheme { App() } }
    }
}

private data class Seccion(val titulo: String, val icono: ImageVector)

private val secciones = listOf(
    Seccion("Entrada de texto", Icons.Default.Keyboard),
    Seccion("Botones y acciones", Icons.Default.SmartButton),
    Seccion("Selección", Icons.Default.CheckBox),
    Seccion("Listas y colecciones", Icons.AutoMirrored.Filled.List),
    Seccion("Información y retroalimentación", Icons.Default.Info),
    Seccion("Contenedores y estructura", Icons.Default.Dashboard),
)

/** Índice 0 = inicio; 1..6 = secciones. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    var indice by rememberSaveable { mutableIntStateOf(0) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    val mostrar: (String) -> Unit = { msg ->
        scope.launch {
            snackbar.currentSnackbarData?.dismiss()
            snackbar.showSnackbar(msg)
        }
    }

    BackHandler(enabled = drawerState.isOpen || indice != 0) {
        if (drawerState.isOpen) scope.launch { drawerState.close() } else indice = 0
    }

    fun ir(i: Int) {
        indice = i
        scope.launch { drawerState.close() }
    }

    CompositionLocalProvider(LocalMensajes provides mostrar) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = drawerState.isOpen,
            drawerContent = {
                ModalDrawerSheet(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Secciones", Modifier.padding(28.dp, 16.dp), style = MaterialTheme.typography.titleLarge)
                    NavigationDrawerItem(
                        label = { Text("Inicio") },
                        icon = { Icon(Icons.Default.Home, null) },
                        selected = indice == 0,
                        onClick = { ir(0) },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    secciones.forEachIndexed { i, s ->
                        NavigationDrawerItem(
                            label = { Text(s.titulo) },
                            icon = { Icon(s.icono, null) },
                            selected = indice == i + 1,
                            onClick = { ir(i + 1) },
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(if (indice == 0) "Catálogo de UI" else secciones[indice - 1].titulo) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                            }
                        },
                        actions = {
                            if (indice != 0) {
                                IconButton(onClick = { ir(0) }) {
                                    Icon(Icons.Default.Home, contentDescription = "Inicio")
                                }
                            }
                        }
                    )
                },
                snackbarHost = { SnackbarHost(snackbar) }
            ) { padding ->
                Box(Modifier.padding(padding)) {
                    when (indice) {
                        0 -> Inicio(onIr = ::ir)
                        1 -> EntradaTextoScreen()
                        2 -> BotonesScreen()
                        3 -> SeleccionScreen()
                        4 -> ListasScreen()
                        5 -> InformacionScreen()
                        6 -> ContenedoresScreen()
                    }
                }
            }
        }
    }
}

@Composable
private fun Inicio(onIr: (Int) -> Unit) {
    Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("Catálogo interactivo de elementos de interfaz", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            "Explora los componentes básicos de una interfaz móvil. " +
                "Elige una sección para ver cada elemento funcionando."
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Elementos en la lista (Sección 4): ${AppState.elementos.size}",
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(Modifier.height(16.dp))
        secciones.forEachIndexed { i, s ->
            Card(Modifier.padding(bottom = 8.dp).clickable { onIr(i + 1) }) {
                ListItem(
                    leadingContent = { Icon(s.icono, contentDescription = null) },
                    headlineContent = { Text("${i + 1}. ${s.titulo}") }
                )
            }
        }
    }
}
