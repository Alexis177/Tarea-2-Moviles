import 'package:flutter/material.dart';

import '../doc_card.dart';

class Contenedores extends StatefulWidget {
  const Contenedores({super.key});
  @override
  State<Contenedores> createState() => _ContenedoresState();
}

class _ContenedoresState extends State<Contenedores> {
  int _destino = 0;
  int _pesoCentral = 2;
  bool _estrellaArriba = false;
  static const _nombres = ['Inicio', 'Buscar', 'Perfil'];

  Widget _caja(String t, Color fondo, Color texto, {double tam = 48}) =>
      GestureDetector(
        onTap: () => mostrarMensaje(context, 'Pulsaste el bloque $t'),
        child: Container(
          width: tam,
          height: tam,
          alignment: Alignment.center,
          decoration: BoxDecoration(
            color: fondo,
            borderRadius: BorderRadius.circular(8),
          ),
          child: Text(
            t,
            style: TextStyle(color: texto, fontWeight: FontWeight.bold),
          ),
        ),
      );

  Widget _peso(String t, int flex, Color fondo, Color texto) => Expanded(
    flex: flex,
    child: Container(
      height: 56,
      margin: const EdgeInsets.symmetric(horizontal: 2),
      alignment: Alignment.center,
      decoration: BoxDecoration(
        color: fondo,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Text(t, style: TextStyle(color: texto)),
    ),
  );

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return SeccionLista(
      children: [
        ElementoDoc(
          nombre: 'Fila (Row)',
          descripcion:
              'Coloca a sus hijos uno junto al otro en horizontal. '
              'mainAxisAlignment controla el reparto del espacio libre.',
          demo: Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              _caja('A', cs.primary, cs.onPrimary),
              _caja('B', cs.secondary, cs.onSecondary),
              _caja('C', cs.tertiary, cs.onTertiary),
            ],
          ),
        ),
        ElementoDoc(
          nombre: 'Columna (Column)',
          descripcion:
              'Coloca a sus hijos uno debajo del otro en vertical. '
              'crossAxisAlignment controla su alineación horizontal.',
          demo: Column(
            children: [
              _caja('1', cs.primary, cs.onPrimary),
              const SizedBox(height: 8),
              _caja('2', cs.secondary, cs.onSecondary),
              const SizedBox(height: 8),
              _caja('3', cs.tertiary, cs.onTertiary),
            ],
          ),
        ),
        ElementoDoc(
          nombre: 'Superposición (Stack)',
          descripcion:
              'Apila los hijos unos sobre otros. Positioned o Align los ubican '
              'en cualquier parte del contenedor.',
          demo: SizedBox(
            height: 130,
            child: Stack(
              children: [
                Positioned.fill(
                  child: Container(
                    decoration: BoxDecoration(
                      color: cs.primaryContainer,
                      borderRadius: BorderRadius.circular(12),
                    ),
                  ),
                ),
                const Positioned(
                  left: 12,
                  top: 12,
                  child: Text('Arriba izquierda'),
                ),
                Align(
                  alignment: _estrellaArriba
                      ? Alignment.topCenter
                      : Alignment.center,
                  child: IconButton(
                    tooltip: 'Mover estrella',
                    icon: const Icon(Icons.star, size: 40),
                    onPressed: () =>
                        setState(() => _estrellaArriba = !_estrellaArriba),
                  ),
                ),
                const Positioned(
                  right: 12,
                  bottom: 12,
                  child: Chip(label: Text('Abajo derecha')),
                ),
              ],
            ),
          ),
        ),
        ElementoDoc(
          nombre: 'Contenedor con desplazamiento vertical',
          descripcion:
              'SingleChildScrollView permite desplazar contenido más alto que su '
              'espacio. Desliza dentro del recuadro.',
          demo: Container(
            height: 140,
            decoration: BoxDecoration(
              border: Border.all(color: cs.outline),
              borderRadius: BorderRadius.circular(8),
            ),
            child: SingleChildScrollView(
              child: Column(
                children: [
                  for (var i = 1; i <= 12; i++)
                    ListTile(
                      dense: true,
                      title: Text('Fila $i'),
                      onTap: () =>
                          mostrarMensaje(context, 'Elegiste la fila $i'),
                    ),
                ],
              ),
            ),
          ),
        ),
        ElementoDoc(
          nombre: 'Barra superior (AppBar)',
          descripcion:
              'Muestra el título de la pantalla y acciones a la derecha. '
              'Esta app usa una AppBar real en la parte superior.',
          demo: ClipRRect(
            borderRadius: BorderRadius.circular(8),
            child: SizedBox(
              height: kToolbarHeight,
              child: MediaQuery.removePadding(
                context: context,
                removeTop: true,
                child: AppBar(
                  automaticallyImplyLeading: false,
                  title: const Text('Mi pantalla'),
                  actions: [
                    IconButton(
                      icon: const Icon(Icons.search),
                      onPressed: () =>
                          mostrarMensaje(context, 'Acción: buscar'),
                    ),
                    IconButton(
                      icon: const Icon(Icons.more_vert),
                      onPressed: () => mostrarMensaje(context, 'Acción: más'),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ),
        ElementoDoc(
          nombre: 'Barra de navegación inferior',
          descripcion:
              'Permite cambiar entre destinos principales. Esta app también '
              'usa un menú lateral (NavigationDrawer) para las secciones.',
          demo: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              ClipRRect(
                borderRadius: BorderRadius.circular(8),
                child: MediaQuery.removePadding(
                  context: context,
                  removeBottom: true,
                  child: NavigationBar(
                    selectedIndex: _destino,
                    onDestinationSelected: (i) => setState(() => _destino = i),
                    destinations: const [
                      NavigationDestination(
                        icon: Icon(Icons.home),
                        label: 'Inicio',
                      ),
                      NavigationDestination(
                        icon: Icon(Icons.search),
                        label: 'Buscar',
                      ),
                      NavigationDestination(
                        icon: Icon(Icons.person),
                        label: 'Perfil',
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 8),
              Text('Destino actual: ${_nombres[_destino]}'),
            ],
          ),
        ),
        ElementoDoc(
          nombre: 'Pesos proporcionales (Expanded)',
          descripcion:
              'Expanded con flex reparte el espacio en proporción. '
              'Cambia el peso central para observar cómo se distribuye el ancho disponible.',
          demo: Column(
            children: [
              Row(
                children: [
                  _peso('1', 1, cs.primary, cs.onPrimary),
                  _peso(
                    '$_pesoCentral',
                    _pesoCentral,
                    cs.secondary,
                    cs.onSecondary,
                  ),
                  _peso('1', 1, cs.tertiary, cs.onTertiary),
                ],
              ),
              TextButton(
                onPressed: () => setState(
                  () => _pesoCentral = _pesoCentral == 4 ? 1 : _pesoCentral + 1,
                ),
                child: const Text('Cambiar peso central'),
              ),
            ],
          ),
        ),
      ],
    );
  }
}
