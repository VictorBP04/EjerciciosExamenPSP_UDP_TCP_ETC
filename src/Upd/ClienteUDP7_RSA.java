package Upd;

import javax.crypto.Cipher;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class ClienteUDP7_RSA {
    public static void main(String[] args) {
        final int PUERTO = 5700;
        final String HOST = "localhost";
        try (DatagramSocket socket = new DatagramSocket()) {
            // REEMPLAZA el valor de PUBLIC_KEY_BASE64 con la clave pública generada por el servidor
            String PUBLIC_KEY_BASE64 = "REEMPLAZAR_CON_CLAVE_PUBLICA_DEL_SERVIDOR";
            byte[] publicKeyBytes = Base64.getDecoder().decode(PUBLIC_KEY_BASE64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            var publicKey = keyFactory.generatePublic(spec);

            String mensaje = "Mensaje cifrado con RSA vía UDP";
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] mensajeCifrado = cipher.doFinal(mensaje.getBytes("UTF-8"));

            InetAddress direccion = InetAddress.getByName(HOST);
            DatagramPacket paquete = new DatagramPacket(mensajeCifrado, mensajeCifrado.length, direccion, PUERTO);
            socket.send(paquete);
            System.out.println("ClienteUDP7_RSA: Mensaje cifrado enviado.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
