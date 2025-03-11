package AdivinaNumero;

import java.io.*;
import java.net.*;
import java.util.Random;

public class UDPServer {
    public static void main(String[] args) {
        int min = 1, max = 100, attempts;

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
            attempts = (int) Math.ceil(Math.log(max - min) / Math.log(2));
        }

        int port = 5001;
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Servidor UDP escuchando en el puerto " + port);

            // Genera el número secreto
            Random random = new Random();
            int secret = random.nextInt(max - min + 1) + min;
            System.out.println("Número secreto: " + secret);

            int tries = 0;
            byte[] buffer = new byte[1024];
            boolean gameOver = false;

            while (!gameOver) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength()).trim();
                int guess;
                try {
                    guess = Integer.parseInt(received);
                } catch (NumberFormatException e) {
                    String msg = "Entrada no válida. Intenta de nuevo.";
                    byte[] msgBytes = msg.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(msgBytes, msgBytes.length, packet.getAddress(), packet.getPort());
                    socket.send(sendPacket);
                    continue;
                }
                tries++;
                String response;
                if (guess == secret) {
                    response = "¡Correcto! Has adivinado el número en " + tries + " intentos.";
                    gameOver = true;
                } else if (guess < secret) {
                    response = "Mayor";
                } else {
                    response = "Menor";
                }
                if (tries >= attempts && guess != secret) {
                    response = "Se han acabado el número de intentos. Has perdido";
                    gameOver = true;
                }
                byte[] responseBytes = response.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, packet.getAddress(), packet.getPort());
                socket.send(responsePacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

