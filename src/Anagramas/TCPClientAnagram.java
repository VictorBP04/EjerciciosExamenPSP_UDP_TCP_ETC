package Anagramas;

import java.io.*;
import java.net.*;

public class TCPClientAnagram {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 18000;
        if(args.length >= 1) {
            host = args[0];
        }
        if(args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch(NumberFormatException e) {
                System.out.println("El puerto debe ser numérico.");
                return;
            }
        }

        try(Socket socket = new Socket(host, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            String serverMsg;
            // Mostrar mensajes del servidor (bienvenida, palabra mezclada, etc.)
            while((serverMsg = in.readLine()) != null) {
                System.out.println("Servidor: " + serverMsg);
                // Si se pide la respuesta, leer desde el usuario
                if(serverMsg.toLowerCase().contains("intentos para adivinar")) {
                    while(true) {
                        System.out.print("Tu respuesta: ");
                        String userGuess = stdIn.readLine();
                        out.println(userGuess);
                        // Leer respuesta del servidor sobre el intento
                        String feedback = in.readLine();
                        System.out.println("Servidor: " + feedback);
                        // Si el mensaje indica acierto o se han agotado los intentos, salir
                        if(feedback.toLowerCase().contains("correcto") || feedback.toLowerCase().contains("se han agotado"))
                            break;
                    }
                }
                if(serverMsg.toLowerCase().contains("correcto") || serverMsg.toLowerCase().contains("se han agotado"))
                    break;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
