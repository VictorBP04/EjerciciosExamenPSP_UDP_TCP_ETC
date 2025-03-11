package Anagramas;

import java.io.*;
import java.net.*;
import java.util.*;

public class UDPServerAnagram {
    // Método para mezclar las letras de una palabra
    private static String scrambleWord(String word) {
        List<Character> letters = new ArrayList<>();
        for (char c : word.toCharArray()) {
            letters.add(c);
        }
        Collections.shuffle(letters);
        StringBuilder sb = new StringBuilder();
        for (char c : letters) {
            sb.append(c);
        }
        if(sb.toString().equals(word)) {
            return scrambleWord(word);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String[] words = {"java", "programacion", "socket", "anagrama", "cliente"};
        Random random = new Random();
        String original = words[random.nextInt(words.length)];
        String scrambled = scrambleWord(original);

        int maxAttempts = 3;
        if(args.length >= 1) {
            try {
                maxAttempts = Integer.parseInt(args[0]);
            } catch(NumberFormatException e) {
                System.out.println("El número de intentos debe ser numérico. Se usará 3 intentos.");
            }
        }

        int port = 19000;
        byte[] buffer = new byte[1024];

        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Servidor UDP Anagramas escuchando en el puerto " + port);
            // Recibir primer paquete para conocer la dirección del cliente
            DatagramPacket initPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(initPacket);
            InetAddress clientAddress = initPacket.getAddress();
            int clientPort = initPacket.getPort();

            String welcome = "Bienvenido al juego de Anagramas UDP.\nPalabra mezclada: " + scrambled +
                    "\nTienes " + maxAttempts + " intentos para adivinar la palabra original.";
            DatagramPacket welcomePacket = new DatagramPacket(welcome.getBytes(), welcome.getBytes().length, clientAddress, clientPort);
            socket.send(welcomePacket);

            int attempts = 0;
            boolean acierto = false;
            while(attempts < maxAttempts) {
                DatagramPacket guessPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(guessPacket);
                String guess = new String(guessPacket.getData(), 0, guessPacket.getLength()).trim();
                attempts++;
                String response;
                if(guess.equalsIgnoreCase(original)) {
                    response = "¡Correcto! Has adivinado la palabra en " + attempts + " intentos.";
                    acierto = true;
                } else {
                    if(attempts < maxAttempts) {
                        response = "Incorrecto. Te quedan " + (maxAttempts - attempts) + " intentos.";
                    } else {
                        response = "Se han agotado los intentos. La palabra original era: " + original;
                    }
                }
                DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, clientAddress, clientPort);
                socket.send(responsePacket);
                if(acierto) break;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

