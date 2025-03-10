package Tcp;

import java.io.*;
import java.net.*;

public class TCPFileClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5001;
        String filePath = "archivo_enviar.txt"; // Archivo a enviar
        File file = new File(filePath);
        try (Socket socket = new Socket(host, port);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             FileInputStream fis = new FileInputStream(file)) {
            // Envía el tamaño del archivo
            dos.writeInt((int) file.length());
            byte[] buffer = new byte[1024];
            int bytesRead;
            // Envía el contenido del archivo
            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }
            System.out.println("Archivo enviado: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
