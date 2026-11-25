package modelo;

import java.util.*;

public class Venta {
    public int id;
    public String fecha;
    public List<Detalle> detalles;
    public Venta(int i, String f, List<Detalle> d) { id=i; fecha=f; detalles=new ArrayList<Detalle>(d); }
    public double total() {
        double t=0;
        for (Detalle d : detalles) t += d.subtotal();
        return t;
    }
}
