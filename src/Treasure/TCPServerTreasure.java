package Treasure;

import java.io.*;
import java.net.*;
import java.util.Random;

public class TCPServerTreasure {
    public static void main(String[] args) {
        // Parámetros configurables: ancho y alto de la cuadrícula y número de intentos.
        int maxX = 10, maxY = 10, attempts = 5;
        if (args.length >= 2) {
            try {
                maxX = Integer.parseInt(args[0]);
                maxY = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Los límites de la cuadrícula deben ser numéricos.");
                return;
            }
        }
        if (args.length >= 3) {
            try {
                attempts = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.out.println("El número de intentos debe ser numérico.");
                return;
            }
        }

        // Genera aleatoriamente la posición del tesoro dentro de la cuadrícula [0, maxX] y [0, maxY]
        Random random = new Random();
        int treasureX = random.nextInt(maxX + 1);
        int treasureY = random.nextInt(maxY + 1);
        System.out.println("DEBUG: Tesoro en (" + treasureX + ", " + treasureY + ")");

        int port = 10000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor TCP Búsqueda del Tesoro escuchando en el puerto " + port);
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println("Bienvenido a la Búsqueda del Tesoro.");
            out.println("La cuadrícula va de 0 a " + maxX + " en X y de 0 a " + maxY + " en Y.");
            out.println("Tienes " + attempts + " intentos para adivinar la posición del tesoro (formato: x y).");

            int currentAttempt = 0;
            boolean found = false;

            while (currentAttempt < attempts && !found) {
                String input = in.readLine();
                if (input == null) break;
                String[] parts = input.trim().split("\\s+");
                if (parts.length != 2) {
                    out.println("Formato incorrecto. Introduce dos números separados por espacio (por ejemplo: 3 7).");
                    continue;
                }
                int guessX, guessY;
                try {
                    guessX = Integer.parseInt(parts[0]);
                    guessY = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    out.println("Formato incorrecto. Asegúrate de introducir números.");
                    continue;
                }
                currentAttempt++;

                if (guessX == treasureX && guessY == treasureY) {
                    out.println("¡Felicidades! Has encontrado el tesoro en " + currentAttempt + " intentos.");
                    found = true;
                } else {
                    String hintX = (guessX < treasureX) ? "más a la derecha" : "más a la izquierda";
                    String hintY = (guessY < treasureY) ? "más arriba" : "más abajo";
                    out.println("No es correcto. Pista: Prueba " + hintX + " y " + hintY + ".");
                    out.println("Intento " + currentAttempt + " de " + attempts + ".");
                }
            }
            if (!found) {
                out.println("Se han agotado los intentos. El tesoro estaba en (" + treasureX + ", " + treasureY + ").");
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

