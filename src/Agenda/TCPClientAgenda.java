package Agenda;

import java.io.*;
import java.net.*;

public class TCPClientAgenda {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 20000;
        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Puerto inválido.");
                return;
            }
        }
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader stdIn = new BufferedReader(
                     new InputStreamReader(System.in))) {

            String serverMsg;
            // Mostrar mensajes iniciales del servidor
            while ((serverMsg = in.readLine()) != null) {
                System.out.println("Servidor: " + serverMsg);
                if (serverMsg.contains("Comandos:")) {
                    break;
                }
            }
            // Enviar comandos al servidor
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                if (userInput.equalsIgnoreCase("EXIT")) {
                    break;
                }
                // Leer respuesta(s) del servidor
                // Aquí leemos una línea de respuesta y la mostramos
                String response = in.readLine();
                if (response != null) {
                    System.out.println("Servidor: " + response);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
