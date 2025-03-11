package Preguntas_Y_Respuestas;

import java.io.*;
import java.net.*;

public class TCPClientQuiz {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 7000;

        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("El puerto debe ser numérico. Se usará el valor por defecto (7000).");
            }
        }

        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            String serverMessage;
            // Mostrar mensajes iniciales del servidor
            if ((serverMessage = in.readLine()) != null) {
                System.out.println("Servidor: " + serverMessage);
            }

            while ((serverMessage = in.readLine()) != null) {
                System.out.println("Servidor: " + serverMessage);
                // Si se envía una pregunta, leer respuesta del usuario
                if (serverMessage.startsWith("Pregunta")) {
                    String userInput = stdIn.readLine();
                    out.println(userInput);
                }
                // Si se indica que se agotaron intentos o el quiz terminó, finalizar
                if (serverMessage.startsWith("Quiz terminado")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

