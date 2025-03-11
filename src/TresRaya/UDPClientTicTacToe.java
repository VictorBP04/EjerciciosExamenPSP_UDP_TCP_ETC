package TresRaya;

import java.io.*;
import java.net.*;

public class UDPClientTicTacToe {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 15000;
        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch(NumberFormatException e) {
                System.out.println("El puerto debe ser numérico.");
                return;
            }
        }
        try (DatagramSocket socket = new DatagramSocket();
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            InetAddress address = InetAddress.getByName(host);
            byte[] buffer = new byte[2048];

            // Enviar un paquete inicial para iniciar la comunicación
            String initMsg = "Inicio de sesión Tres en Raya UDP";
            DatagramPacket initPacket = new DatagramPacket(initMsg.getBytes(), initMsg.getBytes().length, address, port);
            socket.send(initPacket);

            boolean gameOver = false;
            while (!gameOver) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String serverMsg = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Servidor: " + serverMsg);

                if (serverMsg.contains("Tu turno")) {
                    System.out.print("Ingresa tu movimiento (fila columna): ");
                    String userInput = stdIn.readLine();
                    DatagramPacket answerPacket = new DatagramPacket(userInput.getBytes(), userInput.getBytes().length, address, port);
                    socket.send(answerPacket);
                }
                if (serverMsg.contains("Ganaste") || serverMsg.contains("Perdiste") || serverMsg.contains("Empate")) {
                    gameOver = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

