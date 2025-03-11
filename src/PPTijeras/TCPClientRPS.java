package PPTijeras;

import java.io.*;
import java.net.*;

public class TCPClientRPS {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 12000;
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
            // Mostrar los mensajes del servidor
            while ((serverMsg = in.readLine()) != null) {
                System.out.println("Servidor: " + serverMsg);
                if (serverMsg.contains("Elige piedra") || serverMsg.contains("Elige papel") || serverMsg.contains("Elige tijeras")) {
                    System.out.print("Tu elección: ");
                    String userInput = stdIn.readLine();
                    out.println(userInput);
                }
                // Finalizar si se indica el término del juego
                if (serverMsg.contains("Juego terminado")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
