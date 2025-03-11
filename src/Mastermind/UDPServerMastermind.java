package Mastermind;

import java.io.*;
import java.net.*;
import java.util.Random;

public class UDPServerMastermind {
    public static void main(String[] args) {
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

        // Generar la secuencia secreta
        Random random = new Random();
        StringBuilder secretBuilder = new StringBuilder();
        for (int i = 0; i < sequenceLength; i++) {
            secretBuilder.append(random.nextInt(10));
        }
        String secret = secretBuilder.toString();
        System.out.println("Número secreto (para depuración): " + secret);

        int port = 8001;
        byte[] buffer = new byte[1024];

        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Servidor UDP Mastermind escuchando en el puerto " + port);

            // Esperar el primer paquete para obtener la dirección del cliente
            DatagramPacket initPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(initPacket);
            InetAddress clientAddress = initPacket.getAddress();
            int clientPort = initPacket.getPort();

            String welcomeMsg = "Bienvenido a Mastermind UDP. Tienes " + maxAttempts + " intentos para adivinar una secuencia de " + sequenceLength + " dígitos.";
            byte[] welcomeBytes = welcomeMsg.getBytes();
            DatagramPacket welcomePacket = new DatagramPacket(welcomeBytes, welcomeBytes.length, clientAddress, clientPort);
            socket.send(welcomePacket);

            int attempts = 0;
            boolean gameOver = false;
            while (attempts < maxAttempts && !gameOver) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String guess = new String(packet.getData(), 0, packet.getLength()).trim();

                if (guess.length() != sequenceLength || !guess.matches("\\d+")) {
                    String invalidMsg = "Entrada inválida. Debes introducir exactamente " + sequenceLength + " dígitos.";
                    byte[] invalidBytes = invalidMsg.getBytes();
                    DatagramPacket invalidPacket = new DatagramPacket(invalidBytes, invalidBytes.length, clientAddress, clientPort);
                    socket.send(invalidPacket);
                    continue;
                }
                attempts++;
                // Calcular aciertos exactos y parciales
                int exactos = 0, parciales = 0;
                boolean[] secretUsed = new boolean[sequenceLength];
                boolean[] guessUsed = new boolean[sequenceLength];
                for (int i = 0; i < sequenceLength; i++) {
                    if (secret.charAt(i) == guess.charAt(i)) {
                        exactos++;
                        secretUsed[i] = true;
                        guessUsed[i] = true;
                    }
                }
                for (int i = 0; i < sequenceLength; i++) {
                    if (!guessUsed[i]) {
                        for (int j = 0; j < sequenceLength; j++) {
                            if (!secretUsed[j] && guess.charAt(i) == secret.charAt(j)) {
                                parciales++;
                                secretUsed[j] = true;
                                break;
                            }
                        }
                    }
                }
                String response;
                if (exactos == sequenceLength) {
                    response = "¡Correcto! Has adivinado la secuencia en " + attempts + " intentos.";
                    gameOver = true;
                } else {
                    response = "Exactos: " + exactos + ", Parciales: " + parciales + ". Intentos: " + attempts + "/" + maxAttempts;
                }
                byte[] responseBytes = response.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, clientAddress, clientPort);
                socket.send(responsePacket);
            }
            if (!gameOver) {
                String finalMsg = "Se han agotado los intentos. La secuencia secreta era: " + secret;
                byte[] finalBytes = finalMsg.getBytes();
                DatagramPacket finalPacket = new DatagramPacket(finalBytes, finalBytes.length, clientAddress, clientPort);
                socket.send(finalPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

