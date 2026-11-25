package modelo;

public class Producto {
    public int id, cantidad, minimo;
    public String nombre, marca, categoria;
    public double precio;
    public boolean activo;

    public Producto(int id, String n, String m, String c, double p, int cant, int min, boolean a) {
        if (n.trim().isEmpty() || m.trim().isEmpty() || c.trim().isEmpty() ||
                !Double.isFinite(p) || p <= 0 || cant < 0 || min < 0)
            throw new IllegalArgumentException("Datos del producto invalidos.");
        this.id=id; nombre=n; marca=m; categoria=c; precio=p;
        cantidad=cant; minimo=min; activo=a;
    }

    public String toString() {
        return String.format("%-3d %-20s %-15s %-15s $%-10.2f %-6d %-6d",
                id, nombre, marca, categoria, precio, cantidad, minimo);
    }
}
