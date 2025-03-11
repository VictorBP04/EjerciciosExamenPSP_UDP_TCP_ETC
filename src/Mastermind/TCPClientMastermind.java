package Mastermind;

import java.io.*;
import java.net.*;

public class TCPClientMastermind {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8000;
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
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            String serverMsg;
            // Lee el mensaje de bienvenida
            if ((serverMsg = in.readLine()) != null) {
                System.out.println("Servidor: " + serverMsg);
            }
            // Bucle de intentos
            while ((serverMsg = in.readLine()) != null) {
                System.out.println("Servidor: " + serverMsg);
                // Si se ganó o se acabaron los intentos, finaliza.
                if (serverMsg.contains("¡Correcto!") || serverMsg.contains("Se han agotado")) {
                    break;
                }
                System.out.print("Introduce tu intento: ");
                String userInput = stdIn.readLine();
                out.println(userInput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

