package Ahorcado;

import java.io.*;
import java.net.*;

public class UDPClientHangman {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 6001;

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

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(host);
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            byte[] buffer = new byte[1024];

            // Recibe mensaje inicial del servidor
            DatagramPacket initPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(initPacket);
            System.out.println(new String(initPacket.getData(), 0, initPacket.getLength()));

            boolean gameOver = false;
            while (!gameOver) {
                System.out.print("Introduce tu letra: ");
                String userInput = stdIn.readLine();
                if (userInput == null) break;
                byte[] sendData = userInput.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
                socket.send(sendPacket);

                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(receivePacket);
                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Servidor: " + response);
                if (response.contains("Felicidades") || response.contains("Se han acabado")) {
                    gameOver = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
