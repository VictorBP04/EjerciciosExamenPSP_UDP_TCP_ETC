package Simon;

import java.io.*;
import java.net.*;

public class UDPClientSimon {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 9001;
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
            byte[] buffer = new byte[2048];

            // Enviar paquete inicial para iniciar la comunicación
            String initMsg = "Inicio de juego Simon UDP";
            DatagramPacket initPacket = new DatagramPacket(initMsg.getBytes(), initMsg.getBytes().length, address, port);
            socket.send(initPacket);

            boolean gameOver = false;
            while (!gameOver) {
                DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(responsePacket);
                String serverMsg = new String(responsePacket.getData(), 0, responsePacket.getLength());
                System.out.println("Servidor: " + serverMsg);
                if (serverMsg.contains("introduce la secuencia")) {
                    String userInput = stdIn.readLine();
                    byte[] userBytes = userInput.getBytes();
                    DatagramPacket answerPacket = new DatagramPacket(userBytes, userBytes.length, address, port);
                    socket.send(answerPacket);
                }
                if (serverMsg.contains("Fallo") || serverMsg.contains("Juego terminado")) {
                    gameOver = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
