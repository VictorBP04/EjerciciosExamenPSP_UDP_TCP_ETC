package Upd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.File;
import java.io.FileInputStream;

public class ClienteUDPFileTransfer {
    public static void main(String[] args) {
        final int PUERTO = 5555;
        final String HOST = "localhost";
        try (DatagramSocket socket = new DatagramSocket()) {
            // Leer el archivo (aquí se simula leyendo un archivo real)
            File archivo = new File("archivo_a_enviar.dat");
            byte[] archivoBytes = new byte[(int) archivo.length()];
            try (FileInputStream fis = new FileInputStream(archivo)) {
                fis.read(archivoBytes);
            }
            // Dividir el archivo en paquetes de 512 bytes
            int numPaquetes = (int) Math.ceil(archivoBytes.length / 512.0);
            InetAddress direccion = InetAddress.getByName(HOST);
            for (int i = 0; i < numPaquetes; i++) {
                int inicio = i * 512;
                int tam = Math.min(512, archivoBytes.length - inicio);
                byte[] buffer = new byte[tam];
                System.arraycopy(archivoBytes, inicio, buffer, 0, tam);
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, direccion, PUERTO);
                socket.send(paquete);
                System.out.println("Enviado paquete " + (i+1) + "/" + numPaquetes);
            }
            System.out.println("Archivo enviado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
