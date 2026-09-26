import 'package:flutter/material.dart';

import '../doc_card.dart';
import '../state.dart';

class EntradaTexto extends StatefulWidget {
  const EntradaTexto({super.key});
  @override
  State<EntradaTexto> createState() => _EntradaTextoState();
}

class _EntradaTextoState extends State<EntradaTexto> {
  final _simple = TextEditingController();
  final _consulta = TextEditingController();
  String _validado = '';
  bool _ocultar = true;
  String _busqueda = '';
  static const _paises = [
    'México',
    'Argentina',
    'Colombia',
    'Chile',
    'España',
    'Perú',
    'Uruguay',
  ];

  @override
  void dispose() {
    _simple.dispose();
    _consulta.dispose();
    super.dispose();
  }

  InputDecoration _dec(
    String label, {
    String? hint,
    String? error,
    Widget? sufijo,
  }) => InputDecoration(
    labelText: label,
    hintText: hint,
    errorText: error,
    suffixIcon: sufijo,
    border: const OutlineInputBorder(),
  );

  @override
  Widget build(BuildContext context) {
    return SeccionLista(
      children: [
        ElementoDoc(
          nombre: 'Campo de texto simple',
          descripcion:
              'Permite escribir una línea de texto. La etiqueta flota al enfocar. '
              'Aquí, lo escrito se agrega a la lista de la Sección 4.',
          demo: Column(
            children: [
              TextField(
                controller: _simple,
                decoration: _dec('Nombre', hint: 'Escribe un elemento'),
              ),
              const SizedBox(height: 8),
              Align(
                alignment: Alignment.centerRight,
                child: FilledButton.tonal(
                  onPressed: () {
                    final t = _simple.text.trim();
                    if (t.isEmpty)
                      return mostrarMensaje(context, 'Escribe algo primero');
                    appState.agregar(t);
                    _simple.clear();
                    mostrarMensaje(
                      context,
                      '"$t" agregado a Listas (Sección 4)',
                    );
                  },
                  child: const Text('Agregar a la lista'),
                ),
              ),
            ],
          ),
        ),
        ElementoDoc(
          nombre: 'Campo con validación',
          descripcion:
              'Muestra un mensaje de error visible cuando el valor no cumple la regla. '
              'Escribe menos de 3 caracteres para ver el error.',
          demo: TextField(
            onChanged: (v) => setState(() => _validado = v),
            decoration: _dec(
              'Usuario',
              error: _validado.isNotEmpty && _validado.length < 3
                  ? 'Mínimo 3 caracteres'
                  : null,
            ),
          ),
        ),
        ElementoDoc(
          nombre: 'Campo de contraseña',
          descripcion: 'Oculta el contenido con puntos. El ícono del ojo alterna entre mostrar y ocultar.',
          demo: TextField(
            obscureText: _ocultar,
            decoration: _dec(
              'Contraseña',
              sufijo: IconButton(
                icon: Icon(_ocultar ? Icons.visibility : Icons.visibility_off),
                onPressed: () => setState(() => _ocultar = !_ocultar),
              ),
            ),
          ),
        ),
        ElementoDoc(
          nombre: 'Tipos de teclado',
          descripcion: 'El parámetro keyboardType cambia el teclado en pantalla según el dato esperado.',
          demo: Column(
            children: [
              TextField(
                keyboardType: TextInputType.number,
                decoration: _dec('Numérico', hint: '123'),
              ),
              const SizedBox(height: 12),
              TextField(
                keyboardType: TextInputType.emailAddress,
                decoration: _dec(
                  'Correo electrónico',
                  hint: 'nombre@correo.com',
                ),
              ),
              const SizedBox(height: 12),
              TextField(
                keyboardType: TextInputType.phone,
                decoration: _dec('Teléfono', hint: '55 1234 5678'),
              ),
            ],
          ),
        ),
        ElementoDoc(
          nombre: 'Campo multilínea',
          descripcion: 'Admite varias líneas de texto; crece hasta el máximo indicado y luego se desplaza.',
          demo: TextField(
            minLines: 3,
            maxLines: 5,
            keyboardType: TextInputType.multiline,
            decoration: _dec('Comentarios', hint: 'Escribe varias líneas…'),
          ),
        ),
        ElementoDoc(
          nombre: 'Campo con sugerencias',
          descripcion: 'Muestra opciones desplegables que coinciden con lo escrito. Prueba con "a" o "ch".',
          demo: Autocomplete<String>(
            optionsBuilder: (v) => v.text.isEmpty
                ? const Iterable<String>.empty()
                : _paises.where(
                    (o) => o.toLowerCase().contains(v.text.toLowerCase()),
                  ),
            onSelected: (s) => mostrarMensaje(context, 'Elegiste: $s'),
            fieldViewBuilder: (ctx, ctrl, focus, _) => TextField(
              controller: ctrl,
              focusNode: focus,
              decoration: _dec('País'),
            ),
          ),
        ),
        ElementoDoc(
          nombre: 'Barra de búsqueda',
          descripcion: 'Campo especializado para consultas, con ícono de búsqueda y botón para limpiar.',
          demo: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              SearchBar(
                controller: _consulta,
                hintText: 'Buscar…',
                leading: const Icon(Icons.search),
                onChanged: (v) => setState(() => _busqueda = v),
                trailing: [
                  if (_busqueda.isNotEmpty)
                    IconButton(
                      tooltip: 'Limpiar búsqueda',
                      icon: const Icon(Icons.close),
                      onPressed: () {
                        _consulta.clear();
                        setState(() => _busqueda = '');
                      },
                    ),
                ],
              ),
              const SizedBox(height: 8),
              Text(_busqueda.isEmpty ? 'Sin búsqueda' : 'Buscando: $_busqueda'),
            ],
          ),
        ),
      ],
    );
  }
}
