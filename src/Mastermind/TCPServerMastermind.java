package Mastermind;

import java.io.*;
import java.net.*;
import java.util.Random;

public class TCPServerMastermind {
    public static void main(String[] args) {
        // Parámetros configurables: longitud de la secuencia y número de intentos.
        int sequenceLength = 4;
        int maxAttempts = 10;
        if (args.length >= 1) {
            try {
                sequenceLength = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("La longitud de la secuencia debe ser numérica.");
                return;
            }
        }
        if (args.length >= 2) {
            try {
                maxAttempts = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("El número de intentos debe ser numérico.");
                return;
            }
        }

        // Generar la secuencia secreta: dígitos entre 0 y 9.
        Random random = new Random();
        StringBuilder secretBuilder = new StringBuilder();
        for (int i = 0; i < sequenceLength; i++) {
            secretBuilder.append(random.nextInt(10));
        }
        String secret = secretBuilder.toString();
        System.out.println("Número secreto (para depuración): " + secret);

        int port = 8000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor TCP Mastermind escuchando en el puerto " + port);
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println("Bienvenido a Mastermind. Tienes " + maxAttempts + " intentos para adivinar una secuencia de " + sequenceLength + " dígitos.");

            int attempts = 0;
            String inputLine;
            while (attempts < maxAttempts && (inputLine = in.readLine()) != null) {
                inputLine = inputLine.trim();
                if (inputLine.length() != sequenceLength || !inputLine.matches("\\d+")) {
                    out.println("Entrada inválida. Debes introducir exactamente " + sequenceLength + " dígitos.");
                    continue;
                }
                attempts++;
                // Calcular aciertos exactos y parciales.
                int exactos = 0, parciales = 0;
                boolean[] secretUsed = new boolean[sequenceLength];
                boolean[] guessUsed = new boolean[sequenceLength];
                // Primero, cuenta los dígitos exactos.
                for (int i = 0; i < sequenceLength; i++) {
                    if (secret.charAt(i) == inputLine.charAt(i)) {
                        exactos++;
                        secretUsed[i] = true;
                        guessUsed[i] = true;
                    }
                }
                // Luego, cuenta los dígitos parciales.
                for (int i = 0; i < sequenceLength; i++) {
                    if (!guessUsed[i]) {
                        for (int j = 0; j < sequenceLength; j++) {
                            if (!secretUsed[j] && inputLine.charAt(i) == secret.charAt(j)) {
                                parciales++;
                                secretUsed[j] = true;
                                break;
                            }
                        }
                    }
                }
                if (exactos == sequenceLength) {
                    out.println("¡Correcto! Has adivinado la secuencia en " + attempts + " intentos.");
                    break;
                } else {
                    out.println("Exactos: " + exactos + ", Parciales: " + parciales + ". Intentos: " + attempts + "/" + maxAttempts);
                }
            }
            if (attempts >= maxAttempts) {
                out.println("Se han agotado los intentos. La secuencia secreta era: " + secret);
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

