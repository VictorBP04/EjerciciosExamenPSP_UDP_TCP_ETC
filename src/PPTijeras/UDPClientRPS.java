package PPTijeras;

import java.io.*;
import java.net.*;

public class UDPClientRPS {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 13000;
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

            // Enviar paquete inicial para iniciar la comunicación
            String initMsg = "Inicio de juego Piedra, Papel o Tijeras UDP";
            DatagramPacket initPacket = new DatagramPacket(initMsg.getBytes(), initMsg.getBytes().length, address, port);
            socket.send(initPacket);

            // Recibir mensaje de bienvenida
            DatagramPacket welcomePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(welcomePacket);
            System.out.println("Servidor: " + new String(welcomePacket.getData(), 0, welcomePacket.getLength()));

            boolean gameOver = false;
            while (!gameOver) {
                DatagramPacket promptPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(promptPacket);
                String prompt = new String(promptPacket.getData(), 0, promptPacket.getLength());
                System.out.println("Servidor: " + prompt);

                // Si el mensaje final ya fue enviado, salimos
                if (prompt.contains("Juego terminado")) {
                    break;
                }

                // Si se solicita elección, la enviamos
                if (prompt.contains("Elige")) {
                    System.out.print("Tu elección: ");
                    String userInput = stdIn.readLine();
                    DatagramPacket answerPacket = new DatagramPacket(userInput.getBytes(), userInput.getBytes().length, address, port);
                    socket.send(answerPacket);
                }

                DatagramPacket resultPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(resultPacket);
                String result = new String(resultPacket.getData(), 0, resultPacket.getLength());
                System.out.println("Servidor: " + result);
                if (result.contains("Juego terminado")) {
                    gameOver = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

