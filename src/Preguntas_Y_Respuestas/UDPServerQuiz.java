package Preguntas_Y_Respuestas;

import java.io.*;
import java.net.*;

public class UDPServerQuiz {
    public static void main(String[] args) {
        // Configuración: número máximo de intentos por pregunta (por defecto 3)
        int maxAttempts = 3;
        if (args.length >= 1) {
            try {
                maxAttempts = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("El número de intentos debe ser numérico. Se usará el valor por defecto (3).");
            }
        }
        // Preguntas y respuestas
        String[][] quiz = {
                {"¿Cuál es la capital de Italia?", "roma"},
                {"¿Cuántos días tiene un año bisiesto?", "366"},
                {"¿Cuál es el océano más grande del mundo?", "pacifico"}
        };

        int port = 7001;
        int score = 0;
        byte[] buffer = new byte[1024];

        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Servidor UDP Quiz escuchando en el puerto " + port);

            // Espera el primer paquete para obtener la dirección y puerto del cliente
            DatagramPacket initPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(initPacket);
            InetAddress clientAddress = initPacket.getAddress();
            int clientPort = initPacket.getPort();

            String welcomeMsg = "Bienvenido al Quiz UDP. Tendrás " + maxAttempts + " intentos por pregunta.";
            byte[] welcomeBytes = welcomeMsg.getBytes();
            DatagramPacket welcomePacket = new DatagramPacket(welcomeBytes, welcomeBytes.length, clientAddress, clientPort);
            socket.send(welcomePacket);

            // Procesa cada pregunta
            for (int i = 0; i < quiz.length; i++) {
                String question = quiz[i][0];
                String correctAnswer = quiz[i][1].toLowerCase();
                int attemptsLeft = maxAttempts;
                boolean answeredCorrectly = false;

                String questionMsg = "Pregunta " + (i + 1) + ": " + question;
                byte[] questionBytes = questionMsg.getBytes();
                DatagramPacket questionPacket = new DatagramPacket(questionBytes, questionBytes.length, clientAddress, clientPort);
                socket.send(questionPacket);

                while (attemptsLeft > 0 && !answeredCorrectly) {
                    DatagramPacket answerPacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(answerPacket);
                    String clientResponse = new String(answerPacket.getData(), 0, answerPacket.getLength()).trim().toLowerCase();

                    String response;
                    if (clientResponse.equals(correctAnswer)) {
                        response = "¡Correcto!";
                        score++;
                        answeredCorrectly = true;
                    } else {
                        attemptsLeft--;
                        if (attemptsLeft > 0) {
                            response = "Incorrecto. Intenta de nuevo. Intentos restantes: " + attemptsLeft;
                        } else {
                            response = "Incorrecto. Se han agotado los intentos. La respuesta correcta es: " + correctAnswer;
                        }
                    }
                    byte[] responseBytes = response.getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, clientAddress, clientPort);
                    socket.send(responsePacket);
                }
            }

            String finalMsg = "Quiz terminado. Tu puntuación es: " + score + " de " + quiz.length;
            byte[] finalMsgBytes = finalMsg.getBytes();
            DatagramPacket finalPacket = new DatagramPacket(finalMsgBytes, finalMsgBytes.length, clientAddress, clientPort);
            socket.send(finalPacket);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

