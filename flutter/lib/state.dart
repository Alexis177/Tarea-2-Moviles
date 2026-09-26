import 'package:flutter/foundation.dart';

class ElementoCatalogo {
  const ElementoCatalogo(this.id, this.nombre);
  final int id;
  final String nombre;
}

/// Estado compartido en memoria, con identificadores estables para las filas.
class AppState extends ChangeNotifier {
  AppState() {
    _crearEjemplos();
  }
  int _siguienteId = 0;
  final List<ElementoCatalogo> _elementos = [];
  List<ElementoCatalogo> get elementos => List.unmodifiable(_elementos);

  void _crearEjemplos() {
    _elementos.addAll(
      List.generate(
        15,
        (i) => ElementoCatalogo(_siguienteId++, 'Elemento ${i + 1}'),
      ),
    );
  }

  void agregar(String texto) {
    final nombre = texto.trim();
    if (nombre.isEmpty) return;
    _elementos.insert(0, ElementoCatalogo(_siguienteId++, nombre));
    notifyListeners();
  }

  void eliminar(ElementoCatalogo elemento) {
    if (_elementos.remove(elemento)) notifyListeners();
  }

  void insertarEn(int indice, ElementoCatalogo elemento) {
    if (_elementos.any((e) => e.id == elemento.id)) return;
    final seguro = indice < 0
        ? 0
        : indice > _elementos.length
        ? _elementos.length
        : indice;
    _elementos.insert(seguro, elemento);
    notifyListeners();
  }

  void vaciar() {
    _elementos.clear();
    notifyListeners();
  }

  void restaurar() {
    _elementos.clear();
    _crearEjemplos();
    notifyListeners();
  }
}

final appState = AppState();
