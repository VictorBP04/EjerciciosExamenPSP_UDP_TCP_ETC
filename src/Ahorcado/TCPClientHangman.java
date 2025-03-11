package Ahorcado;

import java.io.*;
import java.net.*;

public class TCPClientHangman {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 6000;

        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("El puerto debe ser un número.");
                return;
            }
        }

        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            // Lee mensaje inicial del servidor
            System.out.println(in.readLine());

            String userInput;
            // Bucle de envío de letras
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                String response = in.readLine();
                System.out.println(response);
                if (response.contains("Felicidades") || response.contains("Se han acabado")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

