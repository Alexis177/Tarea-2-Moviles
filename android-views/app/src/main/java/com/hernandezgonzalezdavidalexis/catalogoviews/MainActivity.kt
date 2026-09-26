package com.hernandezgonzalezdavidalexis.catalogoviews

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private var actual = 0
    private lateinit var barra: MaterialToolbar
    private val titulos = listOf("Catálogo de UI", "Entrada de texto", "Botones y acciones", "Selección", "Listas y colecciones", "Información", "Contenedores")
    private val regresar = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() { abrir(0) }
    }

    override fun attachBaseContext(newBase: Context) {
        val configuracion = Configuration(newBase.resources.configuration)
        configuracion.setLocale(Locale("es", "MX"))
        super.attachBaseContext(newBase.createConfigurationContext(configuracion))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val area = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            v.setPadding(area.left, area.top, area.right, area.bottom)
            insets
        }
        barra = findViewById(R.id.toolbar)
        barra.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size)
        barra.navigationContentDescription = "Abrir menú de secciones"
        barra.setNavigationOnClickListener {
            PopupMenu(this, barra).apply {
                titulos.forEachIndexed { i, titulo -> menu.add(0, i, i, if (i == 0) "Inicio" else titulo) }
                setOnMenuItemClickListener { abrir(it.itemId); true }
                show()
            }
        }
        barra.menu.add(0, 100, 0, "Inicio").setIcon(android.R.drawable.ic_menu_revert).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        barra.setOnMenuItemClickListener { abrir(0); true }
        onBackPressedDispatcher.addCallback(this, regresar)
        actual = savedInstanceState?.getInt("seccion") ?: 0
        if (savedInstanceState == null) abrir(0) else actualizarTitulo()
    }

    fun abrir(indice: Int) {
        actual = indice
        val fragmento: Fragment = when (indice) {
            1 -> EntradaFragment()
            2 -> BotonesFragment()
            3 -> SeleccionFragment()
            4 -> ListasFragment()
            5 -> InformacionFragment()
            6 -> ContenedoresFragment()
            else -> InicioFragment()
        }
        supportFragmentManager.beginTransaction().setReorderingAllowed(true).replace(R.id.contenedor, fragmento).commit()
        actualizarTitulo()
    }

    private fun actualizarTitulo() { barra.title = titulos[actual]; regresar.isEnabled = actual != 0 }
    override fun onSaveInstanceState(outState: Bundle) { outState.putInt("seccion", actual); super.onSaveInstanceState(outState) }
}

class InicioFragment : Fragment(R.layout.fragment_inicio) {
    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        listOf(R.id.abrir_1, R.id.abrir_2, R.id.abrir_3, R.id.abrir_4, R.id.abrir_5, R.id.abrir_6).forEachIndexed { i, id ->
            view.findViewById<android.view.View>(id).setOnClickListener { (requireActivity() as MainActivity).abrir(i + 1) }
        }
    }
}
