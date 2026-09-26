import 'package:flutter/material.dart';

/// Tarjeta que documenta un elemento: nombre, explicación y demostración.
class ElementoDoc extends StatelessWidget {
  const ElementoDoc({
    super.key,
    required this.nombre,
    required this.descripcion,
    required this.demo,
  });

  final String nombre;
  final String descripcion;
  final Widget demo;

  @override
  Widget build(BuildContext context) {
    final t = Theme.of(context).textTheme;
    return Card(
      margin: const EdgeInsets.only(bottom: 16),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              nombre,
              style: t.titleMedium?.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 4),
            Text(descripcion, style: t.bodySmall),
            const Divider(height: 24),
            demo,
          ],
        ),
      ),
    );
  }
}

/// Contenedor estándar de una sección (lista con margen consistente).
class SeccionLista extends StatelessWidget {
  const SeccionLista({super.key, required this.children});
  final List<Widget> children;

  @override
  Widget build(BuildContext context) =>
      ListView(padding: const EdgeInsets.all(16), children: children);
}

void mostrarMensaje(BuildContext context, String texto) {
  ScaffoldMessenger.of(context)
    ..hideCurrentSnackBar()
    ..showSnackBar(SnackBar(content: Text(texto)));
}
