package Upd;

import javax.crypto.Cipher;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class ServidorUDP7_RSA {
    public static void main(String[] args) {
        final int PUERTO = 5700;
        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {
            // Generar par de claves RSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();

            // Imprimir la clave pública en Base64 para uso en el cliente
            String publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            System.out.println("ServidorUDP7_RSA: Clave pública RSA (copiar en el cliente): " + publicKeyBase64);

            // Recibir mensaje cifrado por RSA
            byte[] buffer = new byte[256]; // Tamaño adecuado para RSA (2048 bits)
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
            socket.receive(paquete);
            int tam = paquete.getLength();
            byte[] mensajeCifrado = new byte[tam];
            System.arraycopy(paquete.getData(), 0, mensajeCifrado, 0, tam);

            // Descifrar el mensaje usando la clave privada
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] mensajeDescifrado = cipher.doFinal(mensajeCifrado);
            System.out.println("ServidorUDP7_RSA: Mensaje descifrado: " + new String(mensajeDescifrado, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

