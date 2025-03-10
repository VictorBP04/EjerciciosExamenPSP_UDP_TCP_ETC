package mIXTO;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.util.Base64;

public class UDP_BroadcastSecure_Server {
    public static void main(String[] args) {
        final int PUERTO = 5930;
        // Clave AES fija
        final byte[] claveBytes = "broadcastAESclave".substring(0,16).getBytes();
        SecretKeySpec claveAES = new SecretKeySpec(claveBytes, "AES");

        try (DatagramSocket socket = new DatagramSocket()) {
            String mensaje = "Mensaje de difusión seguro para todos los clientes.";
            // Cifrar el mensaje con AES
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, claveAES);
            byte[] mensajeCifrado = cipher.doFinal(mensaje.getBytes("UTF-8"));

            // Generar firma RSA del mensaje
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(keyPair.getPrivate());
            signature.update(mensaje.getBytes("UTF-8"));
            byte[] firma = signature.sign();
            String firmaBase64 = Base64.getEncoder().encodeToString(firma);

            // Concatenar mensaje cifrado y firma separados por "||"
            String envio = Base64.getEncoder().encodeToString(mensajeCifrado) + "||" + firmaBase64;
            byte[] bufferEnvio = envio.getBytes("UTF-8");

            // Enviar broadcast (a localhost para este ejemplo)
            DatagramPacket paquete = new DatagramPacket(bufferEnvio, bufferEnvio.length,
                    InetAddress.getByName("255.255.255.255"), PUERTO);
            socket.setBroadcast(true);
            socket.send(paquete);
            System.out.println("Mensaje broadcast seguro enviado.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

