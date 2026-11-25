import java.io.IOException;
import java.util.*;
import modelo.*;
import servicio.Sistema;
import static util.Entrada.*;

public class App {
    private static final String USUARIO = "admin", CLAVE = "12345";
    private static Sistema sistema;

    public static void main(String[] args) {
        try {
            sistema = new Sistema();
            if (login()) menu();
        } catch (IOException e) {
            System.out.println("Error al iniciar: " + e.getMessage());
        } finally {
            cerrar();
            System.out.println("Sistema finalizado.");
        }
    }

    private static boolean login() {
        System.out.println("=== DISTRIBUIDORA DE LICORES ===");
        for (int i = 1; i <= 3; i++) {
            String u = texto("Usuario: "), c = texto("Contrasena: ");
            if (USUARIO.equals(u) && CLAVE.equals(c)) {
                System.out.println("Acceso autorizado."); return true;
            }
            System.out.println("Credenciales incorrectas. Intentos restantes: " + (3 - i));
        }
        return false;
    }

    private static void menu() {
        int op;
        do {
            System.out.println("\n========= MENU PRINCIPAL =========");
            System.out.println("1. Registrar producto");
            System.out.println("2. Consultar productos");
            System.out.println("3. Modificar producto");
            System.out.println("4. Desactivar producto");
            System.out.println("5. Actualizar inventario");
            System.out.println("6. Registrar venta");
            System.out.println("7. Historial de ventas");
            System.out.println("8. Alertas de existencias");
            System.out.println("9. Reporte general");
            System.out.println("0. Salir");
            op = entero("Opcion: ");
            try {
                switch (op) {
                    case 1: registrar(); break;
                    case 2: listar(); break;
                    case 3: modificar(); break;
                    case 4: desactivar(); break;
                    case 5: inventario(); break;
                    case 6: vender(); break;
                    case 7: historial(); break;
                    case 8: alertas(); break;
                    case 9: reporte(); break;
                    case 0: System.out.println("Cerrando sistema..."); break;
                    default: System.out.println("Opcion no valida.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Aviso: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Error al guardar: " + e.getMessage());
            }
        } while (op != 0);
    }

    // PRODUCTOS
    private static void registrar() throws IOException {
        sistema.agregar(texto("Nombre: "), texto("Marca: "), texto("Categoria: "),
                decimal("Precio: "), noNegativo("Cantidad inicial: "), noNegativo("Stock minimo: "));
        System.out.println("Producto registrado.");
    }

    private static void listar() {
        System.out.println("\nID  NOMBRE               MARCA           CATEGORIA       PRECIO       STOCK  MINIMO");
        boolean hay = false;
        for (Producto p : sistema.productos) if (p.activo) {
            System.out.println(p); hay = true;
        }
        if (!hay) System.out.println("No hay productos activos.");
    }

    private static void modificar() throws IOException {
        listar();
        Producto p = sistema.exigirActivo(entero("ID a modificar: "));
        sistema.editar(p, texto("Nuevo nombre: "), texto("Nueva marca: "),
                texto("Nueva categoria: "), decimal("Nuevo precio: "), noNegativo("Nuevo stock minimo: "));
        System.out.println("Producto actualizado.");
    }

    private static void desactivar() throws IOException {
        listar();
        Producto p = sistema.exigirActivo(entero("ID a desactivar: "));
        if (confirmar("Desactivar " + p.nombre + "? (S/N): ")) {
            p.activo = false; sistema.guardarProductos();
            System.out.println("Producto desactivado.");
        }
    }

    // INVENTARIO
    private static void inventario() throws IOException {
        listar();
        Producto p = sistema.exigirActivo(entero("ID del producto: "));
        int n = entero("Unidades que desea agregar: ");
        if (n <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        p.cantidad += n; sistema.guardarProductos();
        System.out.println("Inventario actualizado.");
    }

    private static void alertas() {
        System.out.println("\n=== ALERTAS DE EXISTENCIAS ===");
        boolean hay = false;
        for (Producto p : sistema.productos) if (p.activo) {
            if (p.cantidad == 0) {
                System.out.println("[AGOTADO] " + p.nombre); hay = true;
            } else if (p.cantidad <= p.minimo) {
                System.out.println("[STOCK BAJO] " + p.nombre + " - Disponible: " + p.cantidad); hay = true;
            }
        }
        if (!hay) System.out.println("No hay alertas.");
    }

    // VENTAS
    private static void vender() throws IOException {
        listar();
        Map<Integer, Integer> carrito = new LinkedHashMap<Integer, Integer>();
        System.out.println("Agregue productos. Digite 0 para terminar.");
        while (true) {
            int id = entero("ID (0 termina): ");
            if (id == 0) break;
            Producto p = sistema.exigirActivo(id);
            int n = entero("Cantidad: ");
            if (n <= 0 || n + carrito.getOrDefault(id, 0) > p.cantidad)
                throw new IllegalArgumentException("Cantidad invalida o stock insuficiente.");
            carrito.put(id, carrito.getOrDefault(id, 0) + n);
        }
        if (carrito.isEmpty()) { System.out.println("Venta cancelada."); return; }
        System.out.printf("Total: $%,.2f%n", sistema.total(carrito));
        if (!confirmar("Confirmar venta? (S/N): ")) return;
        Venta v = sistema.vender(carrito);
        System.out.println("Venta #" + v.id + " | " + v.fecha);
        for (Detalle d : v.detalles)
            System.out.printf("%s | %d x $%,.2f = $%,.2f%n", d.nombre, d.cantidad, d.precio, d.subtotal());
        System.out.printf("TOTAL: $%,.2f%n", v.total());
    }

    private static void historial() {
        System.out.println("\n=== HISTORIAL DE VENTAS ===");
        if (sistema.ventas.isEmpty()) System.out.println("No hay ventas.");
        for (Venta v : sistema.ventas)
            System.out.printf("Venta #%d | %s | Total: $%,.2f%n", v.id, v.fecha, v.total());
    }

    private static void reporte() {
        int activos = 0, unidades = 0;
        for (Producto p : sistema.productos) if (p.activo) { activos++; unidades += p.cantidad; }
        System.out.println("\n=== REPORTE GENERAL ===");
        System.out.println("Productos activos: " + activos);
        System.out.println("Unidades disponibles: " + unidades);
        System.out.println("Ventas registradas: " + sistema.ventas.size());
        System.out.printf("Ingresos acumulados: $%,.2f%n", sistema.ingresos());
    }
}
