package Tcp.Mensaje;

import java.io.*;
import java.net.*;

public class TCPObjectServer {
    public static void main(String[] args) {
        int port = 5003;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor TCP de objetos en puerto " + port);
            try (Socket clientSocket = serverSocket.accept();
                 ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream())) {
                Message msg = (Message) ois.readObject();
                System.out.println("Objeto recibido: " + msg.getText());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

