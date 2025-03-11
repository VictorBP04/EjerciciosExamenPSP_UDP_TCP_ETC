package Preguntas_Y_Respuestas;

import java.io.*;
import java.net.*;

public class TCPServerQuiz {
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
        // Preguntas y respuestas (cada subarreglo: {pregunta, respuesta})
        String[][] quiz = {
                {"¿Cuál es la capital de Francia?", "paris"},
                {"¿Cuántos continentes hay en el mundo?", "7"},
                {"¿En qué año llegó el hombre a la Luna?", "1969"}
        };

        int port = 7000;
        int score = 0;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor TCP Quiz escuchando en el puerto " + port);
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println("Bienvenido al Quiz. Tendrás " + maxAttempts + " intentos por pregunta.");

            // Procesar cada pregunta
            for (int i = 0; i < quiz.length; i++) {
                String question = quiz[i][0];
                String correctAnswer = quiz[i][1].toLowerCase();
                int attemptsLeft = maxAttempts;
                boolean answeredCorrectly = false;

                out.println("Pregunta " + (i + 1) + ": " + question);

                while (attemptsLeft > 0 && !answeredCorrectly) {
                    String clientResponse = in.readLine();
                    if (clientResponse == null) break;
                    clientResponse = clientResponse.trim().toLowerCase();
                    if (clientResponse.equals(correctAnswer)) {
                        out.println("¡Correcto!");
                        score++;
                        answeredCorrectly = true;
                    } else {
                        attemptsLeft--;
                        if (attemptsLeft > 0) {
                            out.println("Incorrecto. Inténtalo de nuevo. Intentos restantes: " + attemptsLeft);
                        } else {
                            out.println("Incorrecto. Se han agotado los intentos. La respuesta correcta es: " + correctAnswer);
                        }
                    }
                }
            }
            out.println("Quiz terminado. Tu puntuación es: " + score + " de " + quiz.length);
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

