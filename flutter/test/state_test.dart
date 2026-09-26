import 'package:flutter_test/flutter_test.dart';
import 'package:catalogo_ui/state.dart';

void main() {
  test('nombres repetidos mantienen identidad al eliminar y deshacer', () {
    final estado = AppState();
    addTearDown(estado.dispose);
    estado.agregar('David');
    estado.agregar('David');
    final primero = estado.elementos[0];
    final segundo = estado.elementos[1];
    expect(primero.id, isNot(segundo.id));
    estado.eliminar(primero);
    expect(estado.elementos.where((e) => e.nombre == 'David').length, 1);
    expect(estado.elementos.first.id, segundo.id);
    estado.insertarEn(0, primero);
    expect(estado.elementos.first.id, primero.id);
  });

  test('deshacer sigue siendo válido si la lista se vació', () {
    final estado = AppState();
    addTearDown(estado.dispose);
    final ultimo = estado.elementos.last;
    estado.eliminar(ultimo);
    estado.vaciar();
    estado.insertarEn(14, ultimo);
    expect(estado.elementos.single.id, ultimo.id);
    estado.insertarEn(14, ultimo);
    expect(estado.elementos.length, 1);
  });

  test('restaurar crea quince ejemplos y descarta entradas vacías', () {
    final estado = AppState();
    addTearDown(estado.dispose);
    estado.agregar('   ');
    expect(estado.elementos.length, 15);
    estado.vaciar();
    estado.restaurar();
    expect(estado.elementos.length, 15);
    expect(estado.elementos.map((e) => e.id).toSet().length, 15);
  });
}
