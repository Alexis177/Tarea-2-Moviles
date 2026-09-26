package com.hernandezgonzalezdavidalexis.catalogoviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ListasFragment : Fragment(R.layout.fragment_listas) {
    private var mediador: TabLayoutMediator? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val paginas = view.findViewById<ViewPager2>(R.id.paginas)
        paginas.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 3
            override fun createFragment(position: Int) = ColeccionFragment().apply { arguments = Bundle().apply { putInt("tipo", position) } }
        }
        mediador = TabLayoutMediator(view.findViewById<TabLayout>(R.id.tabs), paginas) { tab, posicion -> tab.text = listOf("Lista", "Cuadrícula", "Grupos")[posicion] }.also { it.attach() }
    }
    override fun onDestroyView() { mediador?.detach(); mediador = null; super.onDestroyView() }
}

class ColeccionFragment : Fragment(R.layout.pagina_coleccion) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tipo = arguments?.getInt("tipo") ?: 0
        view.texto(R.id.descripcion_coleccion, when (tipo) {
            0 -> "Lista vertical: quince ejemplos iniciales. Pulsa para ver detalle, desliza una fila para eliminar y arrastra desde arriba para actualizar. Vaciar muestra el estado sin datos con ilustración."
            1 -> "Cuadrícula: RecyclerView con GridLayoutManager distribuye los mismos datos en dos columnas. Pulsa una tarjeta para abrir su detalle."
            else -> "Encabezados: esta lista mezcla títulos de grupo y filas de datos con diseños distintos. Pulsa una fila para consultar su información."
        })
        val lista = view.findViewById<RecyclerView>(R.id.coleccion)
        val adaptador = ColeccionAdapter(tipo) { elemento ->
            MaterialAlertDialogBuilder(requireContext()).setTitle("Detalle del elemento").setMessage("Nombre: ${elemento.nombre}\nIdentificador: ${elemento.id}").setPositiveButton("Cerrar", null).show()
        }
        if (tipo == 1) {
            lista.layoutManager = GridLayoutManager(requireContext(), 2).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() { override fun getSpanSize(position: Int) = if (adaptador.getItemViewType(position) == 2) 2 else 1 }
            }
        } else lista.layoutManager = LinearLayoutManager(requireContext())
        lista.adapter = adaptador
        CatalogoDatos.observar(viewLifecycleOwner) { adaptador.actualizar(CatalogoDatos.elementos) }
        view.accion(R.id.vaciar_lista) { CatalogoDatos.vaciar() }
        view.accion(R.id.restaurar_lista) { CatalogoDatos.restaurar() }
        view.findViewById<View>(R.id.acciones_lista).visibility = if (tipo == 0) View.VISIBLE else View.GONE
        val refrescar = view.findViewById<SwipeRefreshLayout>(R.id.refrescar)
        refrescar.isEnabled = tipo == 0
        refrescar.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    delay(800)
                    CatalogoDatos.agregar("Actualización ${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale("es", "MX")).format(java.util.Date())}")
                    view.mensaje("Lista actualizada")
                } finally { refrescar.isRefreshing = false }
            }
        }
        if (tipo == 0) {
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun getSwipeDirs(rv: RecyclerView, holder: RecyclerView.ViewHolder): Int = if (holder.itemViewType == 1) super.getSwipeDirs(rv, holder) else 0
                override fun onMove(rv: RecyclerView, a: RecyclerView.ViewHolder, b: RecyclerView.ViewHolder) = false
                override fun onSwiped(holder: RecyclerView.ViewHolder, direction: Int) {
                    val posicion = holder.bindingAdapterPosition
                    val elemento = adaptador.elemento(posicion) ?: return
                    val indice = CatalogoDatos.elementos.indexOf(elemento)
                    CatalogoDatos.eliminar(elemento)
                    Snackbar.make(view, "${elemento.nombre} eliminado", Snackbar.LENGTH_LONG).setAction("Deshacer") { CatalogoDatos.reinsertar(indice, elemento) }.show()
                }
                override fun onSelectedChanged(holder: RecyclerView.ViewHolder?, actionState: Int) {
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) lista.parent.requestDisallowInterceptTouchEvent(true)
                    super.onSelectedChanged(holder, actionState)
                }
            }).attachToRecyclerView(lista)
        }
    }
}

private data class FilaCatalogo(val titulo: String? = null, val elemento: ElementoCatalogo? = null)

private class ColeccionAdapter(private val tipo: Int, private val abrir: (ElementoCatalogo) -> Unit) : RecyclerView.Adapter<ColeccionAdapter.Holder>() {
    private var filas = emptyList<FilaCatalogo>()
    init { setHasStableIds(true) }
    fun actualizar(datos: List<ElementoCatalogo>) {
        filas = if (datos.isEmpty()) listOf(FilaCatalogo()) else if (tipo != 2) datos.map { FilaCatalogo(elemento = it) } else buildList {
            add(FilaCatalogo(titulo = "Primeros elementos"))
            addAll(datos.take(8).map { FilaCatalogo(elemento = it) })
            add(FilaCatalogo(titulo = "Más elementos"))
            addAll(datos.drop(8).map { FilaCatalogo(elemento = it) })
        }
        notifyDataSetChanged()
    }
    fun elemento(posicion: Int) = filas.getOrNull(posicion)?.elemento
    override fun getItemCount() = filas.size
    override fun getItemId(position: Int): Long = filas[position].elemento?.id ?: if (filas[position].titulo == "Primeros elementos") -2L else if (filas[position].titulo != null) -3L else -4L
    override fun getItemViewType(position: Int) = when { filas[position].titulo != null -> 0; filas[position].elemento != null -> 1; else -> 2 }
    class Holder(view: View) : RecyclerView.ViewHolder(view)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(LayoutInflater.from(parent.context).inflate(when (viewType) { 0 -> R.layout.item_encabezado; 1 -> R.layout.item_dato; else -> R.layout.item_vacio }, parent, false))
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val fila = filas[position]
        fila.elemento?.let { elemento ->
            holder.itemView.findViewById<TextView>(R.id.nombre_item).text = elemento.nombre
            holder.itemView.setOnClickListener { abrir(elemento) }
        }
        fila.titulo?.let { holder.itemView.findViewById<TextView>(R.id.titulo_grupo).text = it }
    }
}
