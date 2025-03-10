package Upd;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClienteUDPAES {
    public static void main(String[] args) {
        final int PUERTO = 5560;
        final String HOST = "localhost";
        // Misma clave AES fija
        final byte[] claveBytes = "1234567890abcdef".getBytes();
        SecretKeySpec clave = new SecretKeySpec(claveBytes, "AES");

        try (DatagramSocket socket = new DatagramSocket()) {
            String mensaje = "Mensaje seguro vía UDP con AES";

            // Cifrar el mensaje
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, clave);
            byte[] mensajeCifrado = cipher.doFinal(mensaje.getBytes("UTF-8"));

            InetAddress direccion = InetAddress.getByName(HOST);
            DatagramPacket paquete = new DatagramPacket(mensajeCifrado, mensajeCifrado.length, direccion, PUERTO);
            socket.send(paquete);
            System.out.println("Mensaje cifrado y enviado.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

