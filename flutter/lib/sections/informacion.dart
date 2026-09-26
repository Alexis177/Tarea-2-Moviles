import 'dart:async';

import 'package:flutter/material.dart';

import '../doc_card.dart';
import '../state.dart';

const _urlImagen = 'https://picsum.photos/id/237/600/300';

class Informacion extends StatefulWidget {
  const Informacion({super.key});
  @override
  State<Informacion> createState() => _InformacionState();
}

class _InformacionState extends State<Informacion> {
  BoxFit _ajuste = BoxFit.cover;
  double _progreso = 0.3;
  String _resultado = 'Sin respuesta';
  bool _enfasis = false;
  bool _archivado = false;
  bool _separadorGrueso = false;
  int _visitas = 0;
  int _recarga = 0;
  Timer? _temporizador;
  OverlayEntry? _aviso;

  void _quitarAviso() {
    _temporizador?.cancel();
    _aviso?.remove();
    _aviso?.dispose();
    _aviso = null;
  }

  @override
  void dispose() {
    _quitarAviso();
    super.dispose();
  }

  /// Toast: Flutter no tiene uno nativo, se simula con un OverlayEntry.
  void _toast(String texto) {
    _quitarAviso();
    final overlay = Overlay.of(context);
    late OverlayEntry entrada;
    entrada = OverlayEntry(
      builder: (_) => Positioned(
        bottom: 96,
        left: 32,
        right: 32,
        child: Center(
          child: Material(
            color: Colors.black87,
            borderRadius: BorderRadius.circular(20),
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
              child: Text(texto, style: const TextStyle(color: Colors.white)),
            ),
          ),
        ),
      ),
    );
    overlay.insert(entrada);
    _aviso = entrada;
    _temporizador = Timer(const Duration(seconds: 2), _quitarAviso);
  }

  Widget _imagen(String etiqueta, Widget img) => Padding(
    padding: const EdgeInsets.only(top: 8),
    child: Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(etiqueta, style: Theme.of(context).textTheme.labelSmall),
        const SizedBox(height: 4),
        ClipRRect(
          borderRadius: BorderRadius.circular(8),
          child: ColoredBox(
            color: Theme.of(context).colorScheme.surfaceContainerHighest,
            child: SizedBox(height: 120, width: double.infinity, child: img),
          ),
        ),
      ],
    ),
  );

  @override
  Widget build(BuildContext context) {
    final t = Theme.of(context).textTheme;
    final cs = Theme.of(context).colorScheme;
    return SeccionLista(
      children: [
        ElementoDoc(
          nombre: 'Textos con estilos',
          descripcion:
              'Text admite distintos tamaños, pesos, colores y decoraciones. '
              'Con Text.rich se mezclan varios estilos en un mismo párrafo.',
          demo: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text('Título grande', style: t.headlineSmall),
              Text('Subtítulo mediano', style: t.titleMedium),
              Text(
                'Texto de cuerpo que cambia',
                style: t.bodyMedium?.copyWith(
                  fontWeight: _enfasis ? FontWeight.bold : FontWeight.normal,
                ),
              ),
              TextButton(
                onPressed: () => setState(() => _enfasis = !_enfasis),
                child: const Text('Alternar énfasis'),
              ),
              const Text(
                'Negrita',
                style: TextStyle(fontWeight: FontWeight.bold),
              ),
              const Text(
                'Cursiva',
                style: TextStyle(fontStyle: FontStyle.italic),
              ),
              Text(
                'Color y subrayado',
                style: TextStyle(
                  color: cs.primary,
                  decoration: TextDecoration.underline,
                ),
              ),
              const Text(
                'Tachado',
                style: TextStyle(decoration: TextDecoration.lineThrough),
              ),
              Text.rich(
                TextSpan(
                  text: 'Mezcla de ',
                  children: [
                    const TextSpan(
                      text: 'negrita',
                      style: TextStyle(fontWeight: FontWeight.bold),
                    ),
                    const TextSpan(text: ' y '),
                    TextSpan(
                      text: 'color',
                      style: TextStyle(color: cs.tertiary, fontSize: 20),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
        ElementoDoc(
          nombre: 'Imágenes y modos de escalado',
          descripcion:
              'Una imagen local (assets) y otra descargada de una URL. El modo de '
              'escalado (BoxFit) decide cómo se ajustan al espacio disponible.',
          demo: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              SegmentedButton<BoxFit>(
                segments: const [
                  ButtonSegment(value: BoxFit.cover, label: Text('Cubrir')),
                  ButtonSegment(value: BoxFit.contain, label: Text('Contener')),
                  ButtonSegment(value: BoxFit.fill, label: Text('Rellenar')),
                ],
                selected: {_ajuste},
                onSelectionChanged: (s) => setState(() => _ajuste = s.first),
              ),
              _imagen(
                'Imagen local',
                Image.asset('assets/imagen_local.png', fit: _ajuste),
              ),
              _imagen(
                'Imagen desde URL',
                Image.network(
                  _urlImagen,
                  key: ValueKey(_recarga),
                  fit: _ajuste,
                  loadingBuilder: (_, child, p) => p == null
                      ? child
                      : const Center(child: CircularProgressIndicator()),
                  errorBuilder: (_, __, ___) => const Center(
                    child: Text('No se pudo cargar (¿sin conexión?)'),
                  ),
                ),
              ),
              TextButton(
                onPressed: () async {
                  await const NetworkImage(_urlImagen).evict();
                  if (mounted) setState(() => _recarga++);
                },
                child: const Text('Volver a cargar imagen'),
              ),
            ],
          ),
        ),
        ElementoDoc(
          nombre: 'Indicadores de progreso',
          descripcion:
              'Determinado: muestra cuánto falta. Indeterminado: indica que hay '
              'trabajo en curso sin conocer su duración. Mueve el deslizador.',
          demo: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text('Determinado: ${(_progreso * 100).round()}%'),
              const SizedBox(height: 8),
              LinearProgressIndicator(value: _progreso),
              Slider(
                value: _progreso,
                onChanged: (v) => setState(() => _progreso = v),
              ),
              Row(
                children: [
                  CircularProgressIndicator(value: _progreso),
                  const SizedBox(width: 16),
                  const Text('Circular determinado'),
                ],
              ),
              const SizedBox(height: 16),
              const Text('Indeterminado'),
              const SizedBox(height: 8),
              const LinearProgressIndicator(),
              const SizedBox(height: 12),
              const Row(
                children: [
                  CircularProgressIndicator(),
                  SizedBox(width: 16),
                  Text('Circular indeterminado'),
                ],
              ),
            ],
          ),
        ),
        ElementoDoc(
          nombre: 'Toast y Snackbar',
          descripcion:
              'El toast es un aviso breve que desaparece solo. El snackbar aparece '
              'abajo y puede incluir una acción, como "Deshacer".',
          demo: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(_archivado ? 'Ejemplo archivado' : 'Ejemplo activo'),
              Wrap(
                spacing: 8,
                children: [
                  OutlinedButton(
                    onPressed: () => _toast('Esto es un toast'),
                    child: const Text('Mostrar toast'),
                  ),
                  OutlinedButton(
                    onPressed: () {
                      setState(() => _archivado = true);
                      ScaffoldMessenger.of(context)
                        ..hideCurrentSnackBar()
                        ..showSnackBar(
                          SnackBar(
                            content: const Text('Elemento archivado'),
                            action: SnackBarAction(
                              label: 'Deshacer',
                              onPressed: () {
                                if (mounted) setState(() => _archivado = false);
                              },
                            ),
                          ),
                        );
                    },
                    child: const Text('Mostrar snackbar'),
                  ),
                ],
              ),
            ],
          ),
        ),
        ElementoDoc(
          nombre: 'Diálogo de confirmación',
          descripcion:
              'Ventana modal que interrumpe al usuario para confirmar una acción '
              'importante antes de continuar.',
          demo: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              FilledButton(
                onPressed: () async {
                  final ok = await showDialog<bool>(
                    context: context,
                    builder: (c) => AlertDialog(
                      title: const Text('¿Eliminar elemento?'),
                      content: const Text('Esta acción no se puede deshacer.'),
                      actions: [
                        TextButton(
                          onPressed: () => Navigator.pop(c, false),
                          child: const Text('Cancelar'),
                        ),
                        FilledButton(
                          onPressed: () => Navigator.pop(c, true),
                          child: const Text('Eliminar'),
                        ),
                      ],
                    ),
                  );
                  if (!mounted) return;
                  setState(
                    () => _resultado = ok == true
                        ? 'Confirmaste la acción'
                        : 'Cancelaste',
                  );
                },
                child: const Text('Abrir diálogo'),
              ),
              const SizedBox(height: 8),
              Text('Resultado: $_resultado'),
            ],
          ),
        ),
        ElementoDoc(
          nombre: 'Hoja inferior (Bottom sheet)',
          descripcion:
              'Panel que sube desde la parte inferior con acciones u opciones '
              'relacionadas con el contexto actual.',
          demo: FilledButton.tonal(
            onPressed: () => showModalBottomSheet(
              context: context,
              builder: (c) => SafeArea(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    for (final o in [
                      (Icons.share, 'Compartir'),
                      (Icons.copy, 'Copiar enlace'),
                      (Icons.delete, 'Eliminar'),
                    ])
                      ListTile(
                        leading: Icon(o.$1),
                        title: Text(o.$2),
                        onTap: () {
                          Navigator.pop(c);
                          _toast('Elegiste: ${o.$2}');
                        },
                      ),
                  ],
                ),
              ),
            ),
            child: const Text('Abrir hoja inferior'),
          ),
        ),
        ElementoDoc(
          nombre: 'Tarjeta, separador y distintivo',
          descripcion:
              'La tarjeta agrupa contenido, el separador divide zonas y el '
              'distintivo (badge) muestra un conteo. Este cuenta los elementos '
              'de la lista de la Sección 4.',
          demo: Column(
            children: [
              Card(
                elevation: 4,
                child: InkWell(
                  onTap: () => setState(() => _visitas++),
                  child: Padding(
                    padding: EdgeInsets.all(16),
                    child: Row(
                      children: [
                        const Icon(Icons.credit_card),
                        const SizedBox(width: 12),
                        Expanded(
                          child: Text('Pulsa la tarjeta: $_visitas visitas'),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
              Divider(height: 24, thickness: _separadorGrueso ? 4 : 1),
              TextButton(
                onPressed: () =>
                    setState(() => _separadorGrueso = !_separadorGrueso),
                child: const Text('Cambiar separador'),
              ),
              Row(
                children: [
                  ListenableBuilder(
                    listenable: appState,
                    builder: (_, __) => Badge(
                      label: Text('${appState.elementos.length}'),
                      child: IconButton(
                        tooltip: 'Agregar elemento de ejemplo',
                        icon: const Icon(Icons.list, size: 32),
                        onPressed: () =>
                            appState.agregar('Agregado desde el distintivo'),
                      ),
                    ),
                  ),
                  const SizedBox(width: 16),
                  const Expanded(
                    child: Text('Elementos en la lista (Sección 4)'),
                  ),
                ],
              ),
            ],
          ),
        ),
      ],
    );
  }
}
