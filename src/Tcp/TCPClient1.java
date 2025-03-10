package Tcp;

import java.io.*;
import java.net.*;

public class TCPClient1 {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5000;
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            String mensaje = "¡Hola, servidor!";
            out.println(mensaje); // Envía el mensaje
            System.out.println("Mensaje enviado: " + mensaje);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
