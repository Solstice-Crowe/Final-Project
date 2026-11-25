package util;

import java.util.Scanner;

public class Entrada {
    private static final Scanner sc = new Scanner(System.in);

    public static void cerrar() { sc.close(); }

    public static String texto(String msg) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("El campo es obligatorio.");
        }
    }

    public static int entero(String msg) {
        while (true) {
            System.out.print(msg);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Ingrese un entero valido."); }
        }
    }

    public static int noNegativo(String msg) {
        int n;
        do { n = entero(msg); if (n < 0) System.out.println("No puede ser negativo."); } while (n < 0);
        return n;
    }

    public static double decimal(String msg) {
        while (true) {
            System.out.print(msg);
            try {
                double n = Double.parseDouble(sc.nextLine().trim().replace(",", "."));
                if (Double.isFinite(n) && n > 0) return n;
            } catch (NumberFormatException e) { }
            System.out.println("Ingrese un valor mayor que cero.");
        }
    }

    public static boolean confirmar(String msg) {
        while (true) {
            String r = texto(msg);
            if (r.equalsIgnoreCase("S")) return true;
            if (r.equalsIgnoreCase("N")) return false;
            System.out.println("Responda S o N.");
        }
    }
}
