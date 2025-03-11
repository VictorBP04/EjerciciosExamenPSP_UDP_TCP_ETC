package AdivinaNumero;

import java.io.*;
import java.net.*;

public class UDPClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5001;

        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("El puerto debe ser un número.");
                return;
            }
        }

        try (DatagramSocket socket = new DatagramSocket()) {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            InetAddress address = InetAddress.getByName(host);
            boolean gameOver = false;
            while (!gameOver) {
                System.out.print("Introduce tu apuesta: ");
                String userInput = stdIn.readLine();
                if (userInput == null) break;
                byte[] sendData = userInput.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
                socket.send(sendPacket);

                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);
                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Servidor: " + response);
                if (response.contains("¡Correcto!") || response.contains("Has perdido")) {
                    gameOver = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

