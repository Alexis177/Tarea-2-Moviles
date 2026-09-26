import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';

import 'sections/botones.dart';
import 'sections/contenedores.dart';
import 'sections/entrada_texto.dart';
import 'sections/informacion.dart';
import 'sections/listas.dart';
import 'sections/seleccion.dart';
import 'state.dart';

void main() => runApp(const CatalogoApp());

class CatalogoApp extends StatelessWidget {
  const CatalogoApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Catálogo de UI',
      debugShowCheckedModeBanner: false,
      locale: const Locale('es'),
      supportedLocales: const [Locale('es')],
      localizationsDelegates: const [
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
      ],
      themeMode: ThemeMode.system, // sigue el modo claro/oscuro del sistema
      theme: ThemeData(colorSchemeSeed: Colors.indigo, useMaterial3: true),
      darkTheme: ThemeData(
        colorSchemeSeed: Colors.indigo,
        brightness: Brightness.dark,
        useMaterial3: true,
      ),
      home: const Principal(),
    );
  }
}

class _Seccion {
  const _Seccion(this.titulo, this.icono, this.builder);
  final String titulo;
  final IconData icono;
  final Widget Function() builder;
}

final _secciones = <_Seccion>[
  _Seccion('Entrada de texto', Icons.keyboard, () => const EntradaTexto()),
  _Seccion('Botones y acciones', Icons.smart_button, () => const Botones()),
  _Seccion('Selección', Icons.check_box, () => const Seleccion()),
  _Seccion('Listas y colecciones', Icons.list, () => const Listas()),
  _Seccion(
    'Información y retroalimentación',
    Icons.info,
    () => const Informacion(),
  ),
  _Seccion(
    'Contenedores y estructura',
    Icons.dashboard,
    () => const Contenedores(),
  ),
];

class Principal extends StatefulWidget {
  const Principal({super.key});
  @override
  State<Principal> createState() => _PrincipalState();
}

class _PrincipalState extends State<Principal> {
  int _indice = 0; // 0 = inicio, 1..6 = secciones

  void _ir(int i) => setState(() => _indice = i);

  @override
  Widget build(BuildContext context) {
    final titulo = _indice == 0
        ? 'Catálogo de UI'
        : _secciones[_indice - 1].titulo;
    return Scaffold(
      appBar: AppBar(
        title: Text(titulo),
        actions: [
          if (_indice != 0)
            IconButton(
              tooltip: 'Inicio',
              icon: const Icon(Icons.home),
              onPressed: () => _ir(0),
            ),
        ],
      ),
      drawer: NavigationDrawer(
        selectedIndex: _indice,
        onDestinationSelected: (i) {
          Navigator.pop(context); // cierra el menú
          _ir(i);
        },
        children: [
          const Padding(
            padding: EdgeInsets.fromLTRB(28, 16, 16, 10),
            child: Text('Secciones', style: TextStyle(fontSize: 18)),
          ),
          const NavigationDrawerDestination(
            icon: Icon(Icons.home),
            label: Text('Inicio'),
          ),
          for (final s in _secciones)
            NavigationDrawerDestination(
              icon: Icon(s.icono),
              label: Text(s.titulo),
            ),
        ],
      ),
      body: _indice == 0
          ? _Inicio(onIr: _ir)
          : _secciones[_indice - 1].builder(),
    );
  }
}

class _Inicio extends StatelessWidget {
  const _Inicio({required this.onIr});
  final void Function(int) onIr;

  @override
  Widget build(BuildContext context) {
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        Text(
          'Catálogo interactivo de elementos de interfaz',
          style: Theme.of(context).textTheme.headlineSmall,
        ),
        const SizedBox(height: 8),
        const Text(
          'Explora los componentes básicos de una interfaz móvil. '
          'Elige una sección para ver cada elemento funcionando.',
        ),
        const SizedBox(height: 8),
        ListenableBuilder(
          listenable: appState,
          builder: (_, __) => Text(
            'Elementos en la lista (Sección 4): ${appState.elementos.length}',
            style: Theme.of(context).textTheme.labelLarge,
          ),
        ),
        const SizedBox(height: 16),
        for (var i = 0; i < _secciones.length; i++)
          Card(
            child: ListTile(
              leading: Icon(_secciones[i].icono),
              title: Text('${i + 1}. ${_secciones[i].titulo}'),
              trailing: const Icon(Icons.chevron_right),
              onTap: () => onIr(i + 1),
            ),
          ),
      ],
    );
  }
}
