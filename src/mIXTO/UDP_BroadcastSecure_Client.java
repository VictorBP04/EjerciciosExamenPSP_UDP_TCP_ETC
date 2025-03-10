package mIXTO;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class UDP_BroadcastSecure_Client {
    public static void main(String[] args) {
        final int PUERTO = 5930;
        final byte[] claveBytes = "broadcastAESclave".substring(0,16).getBytes();
        SecretKeySpec claveAES = new SecretKeySpec(claveBytes, "AES");

        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {
            byte[] buffer = new byte[2048];
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
            socket.receive(paquete);
            String recibido = new String(paquete.getData(), 0, paquete.getLength(), "UTF-8");
            // Separar mensaje cifrado y firma
            String[] partes = recibido.split("\\|\\|");
            String mensajeCifradoB64 = partes[0];
            String firmaBase64 = partes[1];
            byte[] mensajeCifrado = Base64.getDecoder().decode(mensajeCifradoB64);

            // Descifrar el mensaje con AES
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, claveAES);
            byte[] mensajeDescifrado = cipher.doFinal(mensajeCifrado);
            String mensaje = new String(mensajeDescifrado, "UTF-8");
            System.out.println("Mensaje broadcast recibido: " + mensaje);

            // Para verificar la firma, se necesita la clave pública del servidor.
            // Aquí se asume que se conoce (para el ejemplo, se omite la verificación real).
            // Puedes imprimir la firma recibida:
            System.out.println("Firma recibida: " + firmaBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

