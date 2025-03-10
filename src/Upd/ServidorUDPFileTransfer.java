package Upd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.FileOutputStream;

public class ServidorUDPFileTransfer {
    public static void main(String[] args) {
        final int PUERTO = 5555;
        final int NUM_PAQUETES = 5; // Suponemos que el archivo se divide en 5 paquetes
        try (DatagramSocket socket = new DatagramSocket(PUERTO);
             FileOutputStream fos = new FileOutputStream("archivo_recibido.dat")) {
            // Recibir un número fijo de paquetes
            for (int i = 0; i < NUM_PAQUETES; i++) {
                byte[] buffer = new byte[512];
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);
                // Escribir el contenido del paquete en el archivo
                fos.write(paquete.getData(), 0, paquete.getLength());
                System.out.println("Paquete " + (i+1) + " recibido y escrito.");
            }
            System.out.println("Archivo reconstruido exitosamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
