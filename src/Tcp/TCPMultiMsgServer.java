package Tcp;

import java.io.*;
import java.net.*;

public class TCPMultiMsgServer {
    public static void main(String[] args) {
        int port = 5002;
        int numMensajes = 5; // Número de mensajes a recibir
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor TCP esperando " + numMensajes + " mensajes en puerto " + port);
            try (Socket clientSocket = serverSocket.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                for (int i = 0; i < numMensajes; i++) {
                    String mensaje = in.readLine();
                    System.out.println("Mensaje " + (i+1) + ": " + mensaje);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

