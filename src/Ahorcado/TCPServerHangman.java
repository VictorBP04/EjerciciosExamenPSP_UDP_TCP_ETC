package Ahorcado;

import java.io.*;
import java.net.*;
import java.util.Random;

public class TCPServerHangman {
    public static void main(String[] args) {
        // Lista de palabras (puedes ampliarla o hacerla configurable)
        String[] words = {"java", "programacion", "cliente", "servidor", "red"};
        // Escoge una palabra al azar
        Random random = new Random();
        String secretWord = words[random.nextInt(words.length)];

        // Inicializa el estado de la palabra (guiones bajos para letras no adivinadas)
        char[] currentState = new char[secretWord.length()];
        for (int i = 0; i < currentState.length; i++) {
            currentState[i] = '_';
        }

        // Número de intentos: por defecto, longitud de la palabra + 3
        int maxAttempts = secretWord.length() + 3;
        if (args.length >= 1) {
            try {
                maxAttempts = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("El número de intentos debe ser numérico. Se usará el valor por defecto.");
            }
        }

        int port = 6000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor TCP Ahorcado escuchando en el puerto " + port);
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Envía el estado inicial de la palabra y número de intentos
            out.println("Bienvenido al Ahorcado. Palabra: " + String.valueOf(currentState) +
                    " | Intentos: " + maxAttempts);

            int attemptsLeft = maxAttempts;
            String inputLine;
            // Bucle del juego
            while (attemptsLeft > 0 && new String(currentState).contains("_") && (inputLine = in.readLine()) != null) {
                // Se espera una única letra
                inputLine = inputLine.trim().toLowerCase();
                if (inputLine.length() != 1 || !Character.isLetter(inputLine.charAt(0))) {
                    out.println("Introduce solo una letra. Estado: " + String.valueOf(currentState) +
                            " | Intentos restantes: " + attemptsLeft);
                    continue;
                }

                char letter = inputLine.charAt(0);
                boolean acierto = false;
                // Comprueba si la letra se encuentra en la palabra y actualiza el estado
                for (int i = 0; i < secretWord.length(); i++) {
                    if (secretWord.charAt(i) == letter && currentState[i] == '_') {
                        currentState[i] = letter;
                        acierto = true;
                    }
                }

                if (!acierto) {
                    attemptsLeft--;
                    out.println("La letra '" + letter + "' no está en la palabra. Estado: " +
                            String.valueOf(currentState) + " | Intentos restantes: " + attemptsLeft);
                } else {
                    out.println("¡Bien! La letra '" + letter + "' está en la palabra. Estado: " +
                            String.valueOf(currentState) + " | Intentos restantes: " + attemptsLeft);
                }
            }

            // Final del juego
            if (!new String(currentState).contains("_")) {
                out.println("¡Felicidades! Has adivinado la palabra: " + secretWord);
            } else {
                out.println("Se han acabado los intentos. La palabra era: " + secretWord);
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

