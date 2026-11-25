package servicio;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Base64;
import modelo.*;

public class Sistema {
    public List<Producto> productos = new ArrayList<Producto>();
    public List<Venta> ventas = new ArrayList<Venta>();
    Path carpeta = Paths.get("datos");
    Path archivoP = carpeta.resolve("productos.txt"), archivoV = carpeta.resolve("ventas.txt");

    public Sistema() throws IOException {
        Files.createDirectories(carpeta);
        if (!Files.exists(archivoP)) Files.createFile(archivoP);
        if (!Files.exists(archivoV)) Files.createFile(archivoV);
        cargarProductos(); cargarVentas();
    }

    public Producto buscar(int id) {
        for (Producto p : productos) if (p.id == id) return p;
        return null;
    }

    public Producto exigirActivo(int id) {
        Producto p = buscar(id);
        if (p == null || !p.activo) throw new IllegalArgumentException("Producto no encontrado o inactivo.");
        return p;
    }

    public void agregar(String n, String m, String c, double precio, int cant, int min) throws IOException {
        for (Producto p : productos)
            if (p.activo && p.nombre.equalsIgnoreCase(n) && p.marca.equalsIgnoreCase(m))
                throw new IllegalArgumentException("Ese producto ya existe.");
        int id=1;
        for (Producto p : productos) if (p.id >= id) id=p.id+1;
        productos.add(new Producto(id,n,m,c,precio,cant,min,true));
        guardarProductos();
    }

    public void editar(Producto p, String n, String m, String c, double precio, int min) throws IOException {
        p.nombre=n; p.marca=m; p.categoria=c; p.precio=precio; p.minimo=min;
        guardarProductos();
    }

    public double total(Map<Integer,Integer> carrito) {
        double t=0;
        for (Map.Entry<Integer,Integer> e : carrito.entrySet()) {
            Producto p=exigirActivo(e.getKey());
            if (e.getValue() <= 0 || e.getValue() > p.cantidad)
                throw new IllegalArgumentException("Stock insuficiente para " + p.nombre);
            t += p.precio * e.getValue();
        }
        return t;
    }

    public Venta vender(Map<Integer,Integer> carrito) throws IOException {
        total(carrito);
        List<Detalle> ds = new ArrayList<Detalle>();
        for (Map.Entry<Integer,Integer> e : carrito.entrySet()) {
            Producto p=buscar(e.getKey());
            ds.add(new Detalle(p.id,p.nombre,p.precio,e.getValue()));
        }
        int id=1;
        for (Venta v : ventas) if (v.id >= id) id=v.id+1;
        String fecha=LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Venta v=new Venta(id,fecha,ds);
        for (Map.Entry<Integer,Integer> e : carrito.entrySet()) buscar(e.getKey()).cantidad-=e.getValue();
        ventas.add(v);
        try { guardarProductos(); guardarVentas(); }
        catch (IOException ex) {
            ventas.remove(v);
            for (Map.Entry<Integer,Integer> e : carrito.entrySet()) buscar(e.getKey()).cantidad+=e.getValue();
            try { guardarProductos(); } catch (IOException ignorado) { }
            throw ex;
        }
        return v;
    }

    public double ingresos() {
        double t=0;
        for (Venta v : ventas) t+=v.total();
        return t;
    }

    String enc(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }
    String dec(String s) {
        return new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8);
    }

    public void guardarProductos() throws IOException {
        List<String> lineas=new ArrayList<String>();
        for (Producto p : productos)
            lineas.add(p.id+"|"+enc(p.nombre)+"|"+enc(p.marca)+"|"+enc(p.categoria)+"|"+
                    p.precio+"|"+p.cantidad+"|"+p.minimo+"|"+p.activo);
        Files.write(archivoP,lineas,StandardCharsets.UTF_8);
    }

    public void cargarProductos() throws IOException {
        for (String l : Files.readAllLines(archivoP,StandardCharsets.UTF_8)) if (!l.trim().isEmpty()) {
            try {
                String[] a=l.split("\\|",-1);
                productos.add(new Producto(Integer.parseInt(a[0]),dec(a[1]),dec(a[2]),dec(a[3]),
                        Double.parseDouble(a[4]),Integer.parseInt(a[5]),Integer.parseInt(a[6]),
                        Boolean.parseBoolean(a[7])));
            } catch (RuntimeException e) { throw new IOException("Registro invalido en productos.txt.",e); }
        }
    }

    public void guardarVentas() throws IOException {
        List<String> lineas=new ArrayList<String>();
        for (Venta v : ventas) {
            List<String> ds=new ArrayList<String>();
            for (Detalle d : v.detalles)
                ds.add(d.id+";"+enc(d.nombre)+";"+d.precio+";"+d.cantidad);
            lineas.add(v.id+"|"+v.fecha+"|"+enc(String.join("~",ds)));
        }
        Files.write(archivoV,lineas,StandardCharsets.UTF_8);
    }

    public void cargarVentas() throws IOException {
        for (String l : Files.readAllLines(archivoV,StandardCharsets.UTF_8)) if (!l.trim().isEmpty()) {
            try {
                String[] a=l.split("\\|",-1);
                List<Detalle> ds=new ArrayList<Detalle>();
                String bloque=dec(a[2]);
                if (!bloque.isEmpty()) for (String r : bloque.split("~")) {
                    String[] d=r.split(";",-1);
                    ds.add(new Detalle(Integer.parseInt(d[0]),dec(d[1]),
                            Double.parseDouble(d[2]),Integer.parseInt(d[3])));
                }
                ventas.add(new Venta(Integer.parseInt(a[0]),a[1],ds));
            } catch (RuntimeException e) { throw new IOException("Registro invalido en ventas.txt.",e); }
        }
    }
}
