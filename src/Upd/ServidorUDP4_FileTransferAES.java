package Upd;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.FileOutputStream;

public class ServidorUDP4_FileTransferAES {
    public static void main(String[] args) {
        final int PUERTO = 5600;
        final int NUM_PAQUETES = 5;
        // Clave AES fija de 16 bytes
        final byte[] claveBytes = "claveAESparaFile".getBytes();
        SecretKeySpec clave = new SecretKeySpec(claveBytes, "AES");
        try (DatagramSocket socket = new DatagramSocket(PUERTO);
             FileOutputStream fos = new FileOutputStream("archivo_recibido_aes.dat")) {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, clave);
            for (int i = 0; i < NUM_PAQUETES; i++) {
                byte[] buffer = new byte[512];
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);
                int tam = paquete.getLength();
                byte[] datosCifrados = paquete.getData();
                byte[] datosDescifrados = cipher.doFinal(datosCifrados, 0, tam);
                fos.write(datosDescifrados);
                System.out.println("ServidorUDP4_FileTransferAES: Paquete " + (i+1) + " recibido y descifrado.");
            }
            System.out.println("ServidorUDP4_FileTransferAES: Archivo reconstruido exitosamente con AES.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
