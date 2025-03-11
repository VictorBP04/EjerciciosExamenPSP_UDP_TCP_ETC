package Claculadora;

import java.io.*;
import java.net.*;

public class UDPClientCalculator {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 17000;
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

            // Enviar un paquete inicial para iniciar la comunicación
            String initMsg = "Inicio de sesión Calculadora UDP";
            DatagramPacket initPacket = new DatagramPacket(initMsg.getBytes(), initMsg.getBytes().length, address, port);
            socket.send(initPacket);

            // Recibir mensaje de bienvenida
            DatagramPacket welcomePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(welcomePacket);
            System.out.println("Servidor: " + new String(welcomePacket.getData(), 0, welcomePacket.getLength()));

            while (true) {
                String userInput = stdIn.readLine();
                DatagramPacket packet = new DatagramPacket(userInput.getBytes(), userInput.getBytes().length, address, port);
                socket.send(packet);

                DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(responsePacket);
                String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
                System.out.println("Servidor: " + response);
                if (userInput.equalsIgnoreCase("salir"))
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

