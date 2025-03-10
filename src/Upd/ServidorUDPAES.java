package Upd;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ServidorUDPAES {
    public static void main(String[] args) {
        final int PUERTO = 5560;
        // Clave AES fija (16 bytes para AES-128)
        final byte[] claveBytes = "1234567890abcdef".getBytes();
        SecretKeySpec clave = new SecretKeySpec(claveBytes, "AES");

        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {
            byte[] buffer = new byte[1024];
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
            socket.receive(paquete);
            byte[] datosCifrados = paquete.getData();
            int tam = paquete.getLength();

            // Descifrar el mensaje recibido
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, clave);
            byte[] mensajeDescifrado = cipher.doFinal(datosCifrados, 0, tam);
            System.out.println("Mensaje descifrado: " + new String(mensajeDescifrado, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
