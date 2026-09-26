import 'package:flutter_test/flutter_test.dart';
import 'package:catalogo_ui/main.dart';

void main() {
  testWidgets('inicio permite abrir la sección de entrada de texto', (
    tester,
  ) async {
    await tester.pumpWidget(const CatalogoApp());
    await tester.pumpAndSettle();
    expect(
      find.text('Catálogo interactivo de elementos de interfaz'),
      findsOneWidget,
    );
    await tester.tap(find.text('1. Entrada de texto'));
    await tester.pumpAndSettle();
    expect(find.text('Campo de texto simple'), findsOneWidget);
    expect(find.text('Agregar a la lista'), findsOneWidget);
  });
}
