package Tcp;

import java.io.*;
import java.net.*;

public class TCPMultiMsgClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5002;
        int numMensajes = 5; // Número de mensajes a enviar
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            for (int i = 0; i < numMensajes; i++) {
                String mensaje = "Mensaje número " + (i+1);
                out.println(mensaje);
                System.out.println("Enviado: " + mensaje);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

