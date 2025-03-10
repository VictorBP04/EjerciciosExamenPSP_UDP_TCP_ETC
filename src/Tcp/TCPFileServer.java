package Tcp;

import java.io.*;
import java.net.*;

public class TCPFileServer {
    public static void main(String[] args) {
        int port = 5001;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor TCP para archivos en puerto " + port);
            try (Socket clientSocket = serverSocket.accept();
                 DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                 FileOutputStream fos = new FileOutputStream("archivo_recibido.txt")) {
                // Se lee el tamaño del archivo
                int fileSize = dis.readInt();
                byte[] buffer = new byte[1024];
                int bytesRead, totalRead = 0;
                // Se leen los bytes del archivo según su tamaño
                while (totalRead < fileSize && (bytesRead = dis.read(buffer, 0, Math.min(buffer.length, fileSize - totalRead))) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                }
                System.out.println("Archivo recibido y guardado como 'archivo_recibido.txt'");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

