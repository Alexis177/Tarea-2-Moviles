import 'package:flutter/material.dart';

import '../doc_card.dart';
import '../state.dart';

void _detalle(BuildContext context, String texto, int indice) {
  Navigator.push(
    context,
    MaterialPageRoute(
      builder: (_) => Scaffold(
        appBar: AppBar(title: const Text('Detalle')),
        body: Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Icon(
                Icons.article,
                size: 64,
                color: Theme.of(context).colorScheme.primary,
              ),
              const SizedBox(height: 16),
              Text(texto, style: Theme.of(context).textTheme.headlineSmall),
              const SizedBox(height: 8),
              Text('Posición en la lista: ${indice + 1}'),
              const Text('Esta pantalla se abrió al seleccionar el elemento.'),
            ],
          ),
        ),
      ),
    ),
  );
}

class _Nota extends StatelessWidget {
  const _Nota(this.nombre, this.descripcion);
  final String nombre, descripcion;

  @override
  Widget build(BuildContext context) => Padding(
    padding: const EdgeInsets.only(bottom: 12),
    child: Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          nombre,
          style: Theme.of(context).textTheme.titleMedium
              ?.copyWith(fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 4),
        Text(descripcion, style: Theme.of(context).textTheme.bodySmall),
      ],
    ),
  );
}

/// Sección 4: tres pestañas con contenido deslizable (TabBarView).
class Listas extends StatelessWidget {
  const Listas({super.key});

  @override
  Widget build(BuildContext context) {
    return const DefaultTabController(
      length: 3,
      child: Column(
        children: [
          TabBar(
            tabs: [
              Tab(icon: Icon(Icons.list), text: 'Lista'),
              Tab(icon: Icon(Icons.grid_view), text: 'Cuadrícula'),
              Tab(icon: Icon(Icons.view_agenda), text: 'Encabezados'),
            ],
          ),
          Expanded(
            child: TabBarView(
              children: [_TabLista(), _TabGrid(), _TabEncabezados()],
            ),
          ),
        ],
      ),
    );
  }
}

class _TabLista extends StatelessWidget {
  const _TabLista();

  @override
  Widget build(BuildContext context) {
    return ListenableBuilder(
      listenable: appState,
      builder: (context, _) {
        final lista = appState.elementos;
        if (lista.isEmpty) return const _Vacio();
        return RefreshIndicator(
          onRefresh: () async {
            await Future.delayed(const Duration(seconds: 1));
            if (!context.mounted) return;
            appState.agregar('Actualizado ${TimeOfDay.now().format(context)}');
          },
          child: ListView.builder(
            physics: const AlwaysScrollableScrollPhysics(),
            padding: const EdgeInsets.all(16),
            itemCount: lista.length + 1,
            itemBuilder: (context, i) {
              if (i == 0) {
                return Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const _Nota(
                      'Lista vertical, deslizar y actualizar',
                      'Toca un elemento para ver su detalle, deslízalo a un lado para '
                          'eliminarlo o arrastra hacia abajo para actualizar. '
                          'Incluye lo que agregaste en la Sección 1.',
                    ),
                    Wrap(
                      spacing: 8,
                      children: [
                        OutlinedButton(
                          onPressed: appState.vaciar,
                          child: const Text('Vaciar lista'),
                        ),
                        TextButton(
                          onPressed: appState.restaurar,
                          child: const Text('Restaurar ejemplos'),
                        ),
                      ],
                    ),
                  ],
                );
              }
              final idx = i - 1;
              final t = lista[idx];
              return Dismissible(
                key: ValueKey(t.id),
                background: Container(
                  color: Theme.of(context).colorScheme.errorContainer,
                  alignment: Alignment.centerLeft,
                  padding: const EdgeInsets.only(left: 16),
                  child: const Icon(Icons.delete),
                ),
                secondaryBackground: Container(
                  color: Theme.of(context).colorScheme.errorContainer,
                  alignment: Alignment.centerRight,
                  padding: const EdgeInsets.only(right: 16),
                  child: const Icon(Icons.delete),
                ),
                onDismissed: (_) {
                  appState.eliminar(t);
                  ScaffoldMessenger.of(context)
                    ..hideCurrentSnackBar()
                    ..showSnackBar(
                      SnackBar(
                        content: Text('"${t.nombre}" eliminado'),
                        action: SnackBarAction(
                          label: 'Deshacer',
                          onPressed: () => appState.insertarEn(idx, t),
                        ),
                      ),
                    );
                },
                child: ListTile(
                  leading: CircleAvatar(child: Text('${idx + 1}')),
                  title: Text(t.nombre),
                  trailing: const Icon(Icons.chevron_right),
                  onTap: () => _detalle(context, t.nombre, idx),
                ),
              );
            },
          ),
        );
      },
    );
  }
}

class _Vacio extends StatelessWidget {
  const _Vacio();

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(
              Icons.inbox_outlined,
              size: 96,
              color: Theme.of(context).colorScheme.outline,
            ),
            const SizedBox(height: 16),
            Text(
              'No hay elementos',
              style: Theme.of(context).textTheme.titleMedium,
            ),
            const SizedBox(height: 4),
            const Text(
              'Agrega uno desde la Sección 1 o restaura los ejemplos.',
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 16),
            FilledButton(
              onPressed: appState.restaurar,
              child: const Text('Restaurar ejemplos'),
            ),
          ],
        ),
      ),
    );
  }
}

class _TabGrid extends StatelessWidget {
  const _TabGrid();

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const Padding(
          padding: EdgeInsets.all(16),
          child: _Nota(
            'Cuadrícula',
            'Muestra los mismos elementos en columnas. Es útil para contenido '
                'visual. Toca uno para abrir su detalle.',
          ),
        ),
        Expanded(
          child: ListenableBuilder(
            listenable: appState,
            builder: (context, _) => GridView.builder(
              padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
              gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 3,
                mainAxisSpacing: 8,
                crossAxisSpacing: 8,
              ),
              itemCount: appState.elementos.length,
              itemBuilder: (context, i) {
                final t = appState.elementos[i];
                return Card(
                  child: InkWell(
                    onTap: () => _detalle(context, t.nombre, i),
                    child: Padding(
                      padding: const EdgeInsets.all(8),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          const Icon(Icons.widgets),
                          const SizedBox(height: 4),
                          Text(
                            t.nombre,
                            textAlign: TextAlign.center,
                            maxLines: 2,
                            overflow: TextOverflow.ellipsis,
                          ),
                        ],
                      ),
                    ),
                  ),
                );
              },
            ),
          ),
        ),
      ],
    );
  }
}

const _datos = <(bool, String)>[
  (true, 'Frutas'),
  (false, 'Manzana'),
  (false, 'Naranja'),
  (false, 'Plátano'),
  (true, 'Verduras'),
  (false, 'Zanahoria'),
  (false, 'Brócoli'),
  (true, 'Lácteos'),
  (false, 'Queso'),
  (false, 'Yogur'),
];

class _TabEncabezados extends StatelessWidget {
  const _TabEncabezados();

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: _datos.length + 1,
      itemBuilder: (context, i) {
        if (i == 0) {
          return const _Nota(
            'Lista con encabezados',
            'Combina dos tipos de elemento en una sola lista: encabezados de '
                'sección y filas de contenido.',
          );
        }
        final (esEncabezado, texto) = _datos[i - 1];
        if (esEncabezado) {
          return Container(
            color: cs.secondaryContainer,
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: Text(
              texto,
              style: TextStyle(
                fontWeight: FontWeight.bold,
                color: cs.onSecondaryContainer,
              ),
            ),
          );
        }
        return ListTile(
          leading: const Icon(Icons.circle, size: 12),
          title: Text(texto),
          onTap: () => mostrarMensaje(context, 'Elegiste: $texto'),
        );
      },
    );
  }
}
