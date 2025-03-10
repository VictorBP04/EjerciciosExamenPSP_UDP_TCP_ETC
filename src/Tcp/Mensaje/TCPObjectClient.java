package Tcp.Mensaje;

import java.io.*;
import java.net.*;

public class TCPObjectClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5003;
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            Message msg = new Message("¡Hola, este es un objeto serializado!");
            oos.writeObject(msg);
            System.out.println("Objeto enviado: " + msg.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
