package Treasure;

import java.io.*;
import java.net.*;

public class UDPClientTreasure {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 11000;
        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("El puerto debe ser numérico.");
                return;
            }
        }
        try (DatagramSocket socket = new DatagramSocket();
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {
            InetAddress address = InetAddress.getByName(host);
            byte[] buffer = new byte[1024];

            // Enviar paquete inicial para iniciar la sesión
            String initMsg = "Inicio de sesión Búsqueda del Tesoro UDP";
            DatagramPacket initPacket = new DatagramPacket(initMsg.getBytes(), initMsg.getBytes().length, address, port);
            socket.send(initPacket);

            // Recibir mensaje de bienvenida
            DatagramPacket welcomePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(welcomePacket);
            System.out.println("Servidor: " + new String(welcomePacket.getData(), 0, welcomePacket.getLength()));

            boolean gameOver = false;
            while (!gameOver) {
                System.out.print("Introduce tu intento (x y): ");
                String userInput = stdIn.readLine();
                DatagramPacket guessPacket = new DatagramPacket(userInput.getBytes(), userInput.getBytes().length, address, port);
                socket.send(guessPacket);

                DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(responsePacket);
                String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
                System.out.println("Servidor: " + response);
                if (response.contains("¡Felicidades!") || response.contains("Se han agotado")) {
                    gameOver = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
