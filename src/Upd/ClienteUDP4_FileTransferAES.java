package Upd;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.File;
import java.io.FileInputStream;

public class ClienteUDP4_FileTransferAES {
    public static void main(String[] args) {
        final int PUERTO = 5600;
        final String HOST = "localhost";
        // Usamos la misma clave AES que el servidor
        final byte[] claveBytes = "claveAESparaFile".getBytes();
        SecretKeySpec clave = new SecretKeySpec(claveBytes, "AES");
        try (DatagramSocket socket = new DatagramSocket()) {
            File archivo = new File("archivo_a_enviar.dat");
            byte[] archivoBytes = new byte[(int) archivo.length()];
            try (FileInputStream fis = new FileInputStream(archivo)) {
                fis.read(archivoBytes);
            }
            int numPaquetes = (int) Math.ceil(archivoBytes.length / 512.0);
            InetAddress direccion = InetAddress.getByName(HOST);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, clave);
            for (int i = 0; i < numPaquetes; i++) {
                int inicio = i * 512;
                int tam = Math.min(512, archivoBytes.length - inicio);
                byte[] bloque = new byte[tam];
                System.arraycopy(archivoBytes, inicio, bloque, 0, tam);
                byte[] bloqueCifrado = cipher.doFinal(bloque);
                DatagramPacket paquete = new DatagramPacket(bloqueCifrado, bloqueCifrado.length, direccion, PUERTO);
                socket.send(paquete);
                System.out.println("ClienteUDP4_FileTransferAES: Paquete " + (i+1) + " cifrado y enviado.");
            }
            System.out.println("ClienteUDP4_FileTransferAES: Archivo cifrado enviado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
