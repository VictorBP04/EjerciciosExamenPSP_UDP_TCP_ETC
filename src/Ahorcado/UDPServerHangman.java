package Ahorcado;

import java.io.*;
import java.net.*;
import java.util.Random;

public class UDPServerHangman {
    public static void main(String[] args) {
        String[] words = {"java", "programacion", "cliente", "servidor", "red"};
        Random random = new Random();
        String secretWord = words[random.nextInt(words.length)];
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
        int attemptsLeft = maxAttempts;
        int port = 6001;

        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Servidor UDP Ahorcado escuchando en el puerto " + port);
            byte[] buffer = new byte[1024];
            // Envía mensaje inicial al primer cliente que contacte
            DatagramPacket initPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(initPacket);
            InetAddress clientAddress = initPacket.getAddress();
            int clientPort = initPacket.getPort();
            String initMsg = "Bienvenido al Ahorcado. Palabra: " + String.valueOf(currentState) +
                    " | Intentos: " + attemptsLeft;
            byte[] initMsgBytes = initMsg.getBytes();
            DatagramPacket sendInit = new DatagramPacket(initMsgBytes, initMsgBytes.length, clientAddress, clientPort);
            socket.send(sendInit);

            boolean gameOver = false;
            while (!gameOver) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength()).trim().toLowerCase();
                String response;
                // Valida que se reciba una sola letra
                if (received.length() != 1 || !Character.isLetter(received.charAt(0))) {
                    response = "Introduce solo una letra. Estado: " + String.valueOf(currentState) +
                            " | Intentos restantes: " + attemptsLeft;
                } else {
                    char letter = received.charAt(0);
                    boolean acierto = false;
                    for (int i = 0; i < secretWord.length(); i++) {
                        if (secretWord.charAt(i) == letter && currentState[i] == '_') {
                            currentState[i] = letter;
                            acierto = true;
                        }
                    }
                    if (!acierto) {
                        attemptsLeft--;
                        response = "La letra '" + letter + "' no está en la palabra. Estado: " +
                                String.valueOf(currentState) + " | Intentos restantes: " + attemptsLeft;
                    } else {
                        response = "¡Bien! La letra '" + letter + "' está en la palabra. Estado: " +
                                String.valueOf(currentState) + " | Intentos restantes: " + attemptsLeft;
                    }
                }
                // Comprueba si se ha ganado o perdido
                if (!new String(currentState).contains("_")) {
                    response = "¡Felicidades! Has adivinado la palabra: " + secretWord;
                    gameOver = true;
                } else if (attemptsLeft <= 0) {
                    response = "Se han acabado los intentos. La palabra era: " + secretWord;
                    gameOver = true;
                }
                byte[] responseBytes = response.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(responseBytes, responseBytes.length,
                        packet.getAddress(), packet.getPort());
                socket.send(sendPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

