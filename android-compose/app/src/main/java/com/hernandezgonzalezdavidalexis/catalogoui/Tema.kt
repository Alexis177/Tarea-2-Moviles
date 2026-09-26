package com.hernandezgonzalezdavidalexis.catalogoui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Tema que sigue automáticamente el modo claro/oscuro del sistema. */
@Composable
fun CatalogoTheme(
    oscuro: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val esquema = if (oscuro) {
        darkColorScheme(primary = Color(0xFFB8C3FF), secondary = Color(0xFFC5C4DD))
    } else {
        lightColorScheme(primary = Color(0xFF3F51B5), secondary = Color(0xFF5B5D72))
    }
    MaterialTheme(colorScheme = esquema, content = content)
}
