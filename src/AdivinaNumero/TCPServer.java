package AdivinaNumero;

import java.io.*;
import java.net.*;
import java.util.Random;

public class TCPServer {
    public static void main(String[] args) {
        int min = 1, max = 100, attempts;

        // Si se pasan argumentos, se configuran min, max y attempts
        if (args.length >= 2) {
            try {
                min = Integer.parseInt(args[0]);
                max = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Introduce valores numéricos para min y max.");
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
        } else {
            // Calcula el número de intentos como el techo de log₂(max - min)
            attempts = (int) Math.ceil(Math.log(max - min) / Math.log(2));
        }

        int port = 5000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor TCP escuchando en el puerto " + port);
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Genera el número secreto en el rango [min, max]
            Random random = new Random();
            int secret = random.nextInt(max - min + 1) + min;
            System.out.println("Número secreto: " + secret); // Para depuración

            int tries = 0;
            String inputLine;
            while (tries < attempts && (inputLine = in.readLine()) != null) {
                int guess;
                try {
                    guess = Integer.parseInt(inputLine.trim());
                } catch (NumberFormatException e) {
                    out.println("Entrada no válida. Intenta de nuevo.");
                    continue;
                }
                tries++;
                if (guess == secret) {
                    out.println("¡Correcto! Has adivinado el número en " + tries + " intentos.");
                    break;
                } else if (guess < secret) {
                    out.println("Mayor");
                } else {
                    out.println("Menor");
                }
            }
            if (tries >= attempts) {
                out.println("Se han acabado el número de intentos. Has perdido");
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

