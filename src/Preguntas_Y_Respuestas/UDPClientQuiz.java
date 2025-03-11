package Preguntas_Y_Respuestas;

import java.io.*;
import java.net.*;

public class UDPClientQuiz {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 7001;

        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("El puerto debe ser numérico. Se usará el valor por defecto (7001).");
            }
        }

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(host);
            byte[] buffer = new byte[1024];
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            // Enviar paquete inicial para iniciar la comunicación
            String initMsg = "Inicio del Quiz UDP";
            byte[] initBytes = initMsg.getBytes();
            DatagramPacket initPacket = new DatagramPacket(initBytes, initBytes.length, address, port);
            socket.send(initPacket);

            // Recibir mensaje de bienvenida
            DatagramPacket welcomePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(welcomePacket);
            System.out.println(new String(welcomePacket.getData(), 0, welcomePacket.getLength()));

            boolean gameOver = false;
            while (!gameOver) {
                DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(incomingPacket);
                String serverMessage = new String(incomingPacket.getData(), 0, incomingPacket.getLength());
                System.out.println("Servidor: " + serverMessage);

                // Si es una pregunta, se solicita la respuesta
                if (serverMessage.startsWith("Pregunta")) {
                    String userInput = stdIn.readLine();
                    byte[] userBytes = userInput.getBytes();
                    DatagramPacket answerPacket = new DatagramPacket(userBytes, userBytes.length, address, port);
                    socket.send(answerPacket);
                }
                // Si se envía el mensaje final, se finaliza el juego
                if (serverMessage.startsWith("Quiz terminado")) {
                    gameOver = true;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
