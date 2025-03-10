package Tcp;

import java.io.*;
import java.net.*;

public class TCPServer1 {
    public static void main(String[] args) {
        int port = 5000; // Puerto de escucha
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor TCP esperando conexión en el puerto " + port);
            // Acepta una única conexión
            try (Socket clientSocket = serverSocket.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String mensaje = in.readLine(); // Lee el mensaje enviado por el cliente
                System.out.println("Mensaje recibido: " + mensaje);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
