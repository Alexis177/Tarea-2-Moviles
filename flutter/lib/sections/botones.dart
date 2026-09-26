import 'package:flutter/material.dart';

import '../doc_card.dart';

class Botones extends StatefulWidget {
  const Botones({super.key});
  @override
  State<Botones> createState() => _BotonesState();
}

class _BotonesState extends State<Botones> {
  String _modo = 'dia';
  bool _cargando = false;

  void _msg(String t) => mostrarMensaje(context, t);

  @override
  Widget build(BuildContext context) {
    return SeccionLista(
      children: [
        ElementoDoc(
          nombre: 'Relleno, contorno y texto',
          descripcion:
              'Tres niveles de énfasis: el relleno para la acción principal, '
              'el contorno para la secundaria y el de texto para la menos importante.',
          demo: Wrap(
            spacing: 8,
            runSpacing: 8,
            children: [
              FilledButton(
                onPressed: () => _msg('Botón relleno'),
                child: const Text('Relleno'),
              ),
              OutlinedButton(
                onPressed: () => _msg('Botón con contorno'),
                child: const Text('Contorno'),
              ),
              TextButton(
                onPressed: () => _msg('Botón de texto'),
                child: const Text('Texto'),
              ),
            ],
          ),
        ),
        ElementoDoc(
          nombre: 'Botones con ícono',
          descripcion: 'Pueden mostrar solo un ícono (acciones compactas) o ícono más texto (más claridad).',
          demo: Wrap(
            spacing: 8,
            runSpacing: 8,
            crossAxisAlignment: WrapCrossAlignment.center,
            children: [
              IconButton.filled(
                onPressed: () => _msg('Ícono: favorito'),
                icon: const Icon(Icons.favorite),
              ),
              FilledButton.icon(
                onPressed: () => _msg('Ícono + texto: compartir'),
                icon: const Icon(Icons.share),
                label: const Text('Compartir'),
              ),
            ],
          ),
        ),
        ElementoDoc(
          nombre: 'Botón de acción flotante',
          descripcion: 'Representa la acción principal de la pantalla. La versión extendida añade una etiqueta de texto.',
          demo: Wrap(
            spacing: 12,
            runSpacing: 12,
            crossAxisAlignment: WrapCrossAlignment.center,
            children: [
              FloatingActionButton(
                heroTag: null,
                onPressed: () => _msg('FAB normal'),
                child: const Icon(Icons.add),
              ),
              FloatingActionButton.extended(
                heroTag: null,
                onPressed: () => _msg('FAB extendido'),
                icon: const Icon(Icons.edit),
                label: const Text('Redactar'),
              ),
            ],
          ),
        ),
        ElementoDoc(
          nombre: 'Selector segmentado',
          descripcion: 'Grupo de botones donde solo uno puede estar activo. Útil para alternar entre vistas u opciones cortas.',
          demo: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              SegmentedButton<String>(
                segments: const [
                  ButtonSegment(
                    value: 'dia',
                    label: Text('Día'),
                    icon: Icon(Icons.light_mode),
                  ),
                  ButtonSegment(
                    value: 'noche',
                    label: Text('Noche'),
                    icon: Icon(Icons.dark_mode),
                  ),
                ],
                selected: {_modo},
                onSelectionChanged: (s) => setState(() => _modo = s.first),
              ),
              const SizedBox(height: 8),
              Text('Seleccionado: ${_modo == 'dia' ? 'Día' : 'Noche'}'),
            ],
          ),
        ),
        ElementoDoc(
          nombre: 'Deshabilitado y en carga',
          descripcion: 'Un botón sin acción (onPressed nulo) se ve atenuado. El de carga bloquea la pulsación mientras trabaja.',
          demo: Wrap(
            spacing: 8,
            runSpacing: 8,
            children: [
              const FilledButton(onPressed: null, child: Text('Deshabilitado')),
              FilledButton(
                onPressed: _cargando
                    ? null
                    : () async {
                        setState(() => _cargando = true);
                        await Future.delayed(const Duration(seconds: 2));
                        if (!mounted) return;
                        setState(() => _cargando = false);
                        _msg('Envío completado');
                      },
                child: _cargando
                    ? const SizedBox(
                        width: 18,
                        height: 18,
                        child: CircularProgressIndicator(strokeWidth: 2),
                      )
                    : const Text('Enviar'),
              ),
            ],
          ),
        ),
      ],
    );
  }
}
