import 'package:flutter/material.dart';

import '../doc_card.dart';

class Seleccion extends StatefulWidget {
  const Seleccion({super.key});
  @override
  State<Seleccion> createState() => _SeleccionState();
}

class _SeleccionState extends State<Seleccion> {
  bool _acepto = false;
  final _canales = {'Correo': true, 'SMS': false, 'Notificaciones': false};
  String _envio = 'Estándar';
  bool _notif = true;
  double _volumen = 40;
  RangeValues _rango = const RangeValues(20, 70);
  String _talla = 'M';
  DateTime? _fecha;
  TimeOfDay? _hora;
  final _filtros = <String>{'Música'};

  /// true = todos, false = ninguno, null = estado indeterminado.
  bool? get _todos {
    final v = _canales.values;
    if (v.every((e) => e)) return true;
    if (v.every((e) => !e)) return false;
    return null;
  }

  String _fmtFecha(DateTime d) =>
      '${d.day.toString().padLeft(2, '0')}/${d.month.toString().padLeft(2, '0')}/${d.year}';

  @override
  Widget build(BuildContext context) {
    return SeccionLista(
      children: [
        ElementoDoc(
          nombre: 'Casilla de verificación',
          descripcion:
              'Permite marcar o desmarcar una opción. La casilla superior es tristate: '
              'queda indeterminada cuando solo algunas opciones están marcadas.',
          demo: Column(
            children: [
              CheckboxListTile(
                contentPadding: EdgeInsets.zero,
                title: const Text('Acepto los términos'),
                value: _acepto,
                onChanged: (v) => setState(() => _acepto = v ?? false),
              ),
              CheckboxListTile(
                contentPadding: EdgeInsets.zero,
                title: const Text('Todos los canales'),
                tristate: true,
                value: _todos,
                onChanged: (_) {
                  final nuevo = _todos != true;
                  setState(() => _canales.updateAll((_, __) => nuevo));
                },
              ),
              for (final e in _canales.entries)
                CheckboxListTile(
                  contentPadding: const EdgeInsets.only(left: 24),
                  title: Text(e.key),
                  value: e.value,
                  onChanged: (v) =>
                      setState(() => _canales[e.key] = v ?? false),
                ),
            ],
          ),
        ),
        ElementoDoc(
          nombre: 'Botones de opción',
          descripcion:
              'Grupo donde solo una opción puede estar seleccionada a la vez.',
          demo: RadioGroup<String>(
            groupValue: _envio,
            onChanged: (v) {
              if (v != null) setState(() => _envio = v);
            },
            child: Column(
              children: [
                for (final o in ['Estándar', 'Rápido', 'Recoger en tienda'])
                  RadioListTile<String>(
                    contentPadding: EdgeInsets.zero,
                    title: Text(o),
                    value: o,
                  ),
              ],
            ),
          ),
        ),
        ElementoDoc(
          nombre: 'Interruptor (Switch)',
          descripcion: 'Activa o desactiva un ajuste con efecto inmediato.',
          demo: SwitchListTile(
            contentPadding: EdgeInsets.zero,
            title: Text(
              _notif
                  ? 'Notificaciones activadas'
                  : 'Notificaciones desactivadas',
            ),
            value: _notif,
            onChanged: (v) => setState(() => _notif = v),
          ),
        ),
        ElementoDoc(
          nombre: 'Deslizadores',
          descripcion: 'El deslizador simple elige un valor; el de rango elige un mínimo y un máximo.',
          demo: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text('Volumen: ${_volumen.round()}'),
              Slider(
                value: _volumen,
                max: 100,
                divisions: 20,
                label: _volumen.round().toString(),
                onChanged: (v) => setState(() => _volumen = v),
              ),
              Text(
                'Rango de precio: \$${_rango.start.round()} – \$${_rango.end.round()}',
              ),
              RangeSlider(
                values: _rango,
                max: 100,
                labels: RangeLabels(
                  _rango.start.round().toString(),
                  _rango.end.round().toString(),
                ),
                onChanged: (v) => setState(() => _rango = v),
              ),
            ],
          ),
        ),
        ElementoDoc(
          nombre: 'Lista desplegable',
          descripcion: 'Muestra un menú con varias opciones y guarda la elegida, ahorrando espacio en pantalla.',
          demo: DropdownMenu<String>(
            initialSelection: _talla,
            label: const Text('Talla'),
            dropdownMenuEntries: const [
              DropdownMenuEntry(value: 'S', label: 'Chica (S)'),
              DropdownMenuEntry(value: 'M', label: 'Mediana (M)'),
              DropdownMenuEntry(value: 'L', label: 'Grande (L)'),
            ],
            onSelected: (v) => setState(() => _talla = v ?? _talla),
          ),
        ),
        ElementoDoc(
          nombre: 'Selector de fecha y hora',
          descripcion: 'Abren un diálogo del sistema para elegir una fecha en el calendario o una hora en el reloj.',
          demo: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Wrap(
                spacing: 8,
                children: [
                  OutlinedButton.icon(
                    icon: const Icon(Icons.calendar_today),
                    label: const Text('Elegir fecha'),
                    onPressed: () async {
                      final d = await showDatePicker(
                        context: context,
                        initialDate: _fecha ?? DateTime.now(),
                        firstDate: DateTime(2020),
                        lastDate: DateTime(2035),
                      );
                      if (mounted && d != null) setState(() => _fecha = d);
                    },
                  ),
                  OutlinedButton.icon(
                    icon: const Icon(Icons.access_time),
                    label: const Text('Elegir hora'),
                    onPressed: () async {
                      final h = await showTimePicker(
                        context: context,
                        initialTime: _hora ?? TimeOfDay.now(),
                      );
                      if (mounted && h != null) setState(() => _hora = h);
                    },
                  ),
                ],
              ),
              const SizedBox(height: 8),
              Text(
                'Fecha: ${_fecha == null ? 'sin elegir' : _fmtFecha(_fecha!)}',
              ),
              Text(
                'Hora: ${_hora == null ? 'sin elegir' : _hora!.format(context)}',
              ),
            ],
          ),
        ),
        ElementoDoc(
          nombre: 'Chips de filtro',
          descripcion: 'Etiquetas compactas que se activan y desactivan para filtrar contenido. Se pueden combinar varias.',
          demo: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Wrap(
                spacing: 8,
                children: [
                  for (final c in ['Música', 'Cine', 'Deportes', 'Libros'])
                    FilterChip(
                      label: Text(c),
                      selected: _filtros.contains(c),
                      onSelected: (s) => setState(
                        () => s ? _filtros.add(c) : _filtros.remove(c),
                      ),
                    ),
                ],
              ),
              const SizedBox(height: 8),
              Text(
                _filtros.isEmpty
                    ? 'Sin filtros activos'
                    : 'Filtros: ${_filtros.join(', ')}',
              ),
            ],
          ),
        ),
      ],
    );
  }
}
