# Sistema de gestión de inventario y ventas — Distribuidora de licores

Aplicación de consola en Java (JDK 8 o superior).

## Estructura

```
src/
├── App.java                  Punto de entrada: inicio de sesión, menú y módulos
├── modelo/
│   ├── Producto.java         Datos de un producto y validación en el constructor
│   ├── Detalle.java          Línea de una venta (producto, precio, cantidad)
│   └── Venta.java            Venta con su lista de detalles
├── servicio/
│   └── Sistema.java          Reglas de negocio y lectura/escritura de archivos
└── util/
    └── Entrada.java          Lectura y validación de datos por teclado
datos/
├── productos.txt
└── ventas.txt
```

## Compilar y ejecutar (desde la carpeta raíz del proyecto)

```
javac -d bin src/App.java src/modelo/*.java src/servicio/*.java src/util/*.java
java -cp bin App
```

Usuario: `admin` · Contraseña: `12345`

## Ejecutar automáticamente al abrir el proyecto en VS Code

- `ejecutar.bat` (Windows) y `ejecutar.sh` (Linux/macOS) compilan y ejecutan el sistema con un solo comando.
- `.vscode/tasks.json` define la tarea «Ejecutar sistema», que corre ese archivo en la terminal apenas se abre la carpeta del proyecto en VS Code.
  La primera vez VS Code pide confirmar «Allow Automatic Tasks»; también se puede ejecutar a mano con
  Terminal → Ejecutar tarea → Ejecutar sistema.
