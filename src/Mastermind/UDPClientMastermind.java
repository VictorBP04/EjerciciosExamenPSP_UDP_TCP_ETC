package Mastermind;

import java.io.*;
import java.net.*;

public class UDPClientMastermind {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8001;
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
            byte[] buffer = new byte[1024];
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            // Enviar un primer paquete para iniciar la comunicación
            String initMsg = "Inicio del Mastermind UDP";
            byte[] initBytes = initMsg.getBytes();
            DatagramPacket initPacket = new DatagramPacket(initBytes, initBytes.length, address, port);
            socket.send(initPacket);

            // Recibe el mensaje de bienvenida
            DatagramPacket welcomePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(welcomePacket);
            System.out.println(new String(welcomePacket.getData(), 0, welcomePacket.getLength()));

            boolean gameOver = false;
            while (!gameOver) {
                DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(incomingPacket);
                String serverMsg = new String(incomingPacket.getData(), 0, incomingPacket.getLength());
                System.out.println("Servidor: " + serverMsg);
                if (serverMsg.contains("¡Correcto!") || serverMsg.contains("Se han agotado")) {
                    break;
                }
                System.out.print("Introduce tu intento: ");
                String userInput = stdIn.readLine();
                byte[] userBytes = userInput.getBytes();
                DatagramPacket answerPacket = new DatagramPacket(userBytes, userBytes.length, address, port);
                socket.send(answerPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

