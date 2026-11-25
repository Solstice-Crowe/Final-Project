package modelo;

public class Detalle {
    public int id, cantidad;
    public String nombre;
    public double precio;
    public Detalle(int i, String n, double p, int c) { id=i; nombre=n; precio=p; cantidad=c; }
    public double subtotal() { return precio * cantidad; }
}
