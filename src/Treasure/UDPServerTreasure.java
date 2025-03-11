package Treasure;

import java.io.*;
import java.net.*;
import java.util.Random;

public class UDPServerTreasure {
    public static void main(String[] args) {
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

        // Generar la posición del tesoro
        Random random = new Random();
        int treasureX = random.nextInt(maxX + 1);
        int treasureY = random.nextInt(maxY + 1);
        System.out.println("DEBUG: Tesoro en (" + treasureX + ", " + treasureY + ")");

        int port = 11000;
        byte[] buffer = new byte[1024];

        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Servidor UDP Búsqueda del Tesoro escuchando en el puerto " + port);
            // Recibir el primer paquete para obtener la dirección y puerto del cliente
            DatagramPacket initPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(initPacket);
            InetAddress clientAddress = initPacket.getAddress();
            int clientPort = initPacket.getPort();

            String welcomeMsg = "Bienvenido a la Búsqueda del Tesoro UDP.\nLa cuadrícula va de 0 a " + maxX +
                    " en X y de 0 a " + maxY + " en Y.\nTienes " + attempts +
                    " intentos. Envía tus intentos en el formato: x y";
            DatagramPacket welcomePacket = new DatagramPacket(welcomeMsg.getBytes(), welcomeMsg.getBytes().length,
                    clientAddress, clientPort);
            socket.send(welcomePacket);

            int currentAttempt = 0;
            boolean found = false;

            while (currentAttempt < attempts && !found) {
                DatagramPacket guessPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(guessPacket);
                String guessStr = new String(guessPacket.getData(), 0, guessPacket.getLength()).trim();
                String[] parts = guessStr.split("\\s+");
                String response;
                if (parts.length != 2) {
                    response = "Formato incorrecto. Usa: x y";
                    DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length,
                            clientAddress, clientPort);
                    socket.send(responsePacket);
                    continue;
                }
                int guessX, guessY;
                try {
                    guessX = Integer.parseInt(parts[0]);
                    guessY = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    response = "Formato incorrecto. Asegúrate de enviar números.";
                    DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length,
                            clientAddress, clientPort);
                    socket.send(responsePacket);
                    continue;
                }
                currentAttempt++;
                if (guessX == treasureX && guessY == treasureY) {
                    response = "¡Felicidades! Has encontrado el tesoro en " + currentAttempt + " intentos.";
                    found = true;
                } else {
                    String hintX = (guessX < treasureX) ? "más a la derecha" : "más a la izquierda";
                    String hintY = (guessY < treasureY) ? "más arriba" : "más abajo";
                    response = "Incorrecto. Pista: Prueba " + hintX + " y " + hintY +
                            ". Intento " + currentAttempt + " de " + attempts;
                }
                DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length,
                        clientAddress, clientPort);
                socket.send(responsePacket);
            }
            if (!found) {
                String finalMsg = "Se han agotado los intentos. El tesoro estaba en (" + treasureX + ", " + treasureY + ").";
                DatagramPacket finalPacket = new DatagramPacket(finalMsg.getBytes(), finalMsg.getBytes().length,
                        clientAddress, clientPort);
                socket.send(finalPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
