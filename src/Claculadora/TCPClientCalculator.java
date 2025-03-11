package Claculadora;

import java.io.*;
import java.net.*;

public class TCPClientCalculator {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 16000;
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

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            // Mostrar mensajes iniciales del servidor
            String serverMsg;
            while ((serverMsg = in.readLine()) != null) {
                System.out.println("Servidor: " + serverMsg);
                if (serverMsg.contains("salir"))
                    break;
                // Si ya se mostró la bienvenida, leer la siguiente expresión del usuario
                if (!serverMsg.startsWith("Bienvenido") && !serverMsg.startsWith("Envía")) {
                    System.out.print("Tu expresión: ");
                    String userInput = stdIn.readLine();
                    out.println(userInput);
                    if (userInput.equalsIgnoreCase("salir")) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
