package mIXTO;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.*;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Comprehensive_Secure_Client {
    public static void main(String[] args) {
        final String HOST = "localhost";
        final int PUERTO_TCP = 5950;
        final int PUERTO_UDP = 5951;
        try (Socket socketTCP = new Socket(HOST, PUERTO_TCP);
             DataInputStream dis = new DataInputStream(socketTCP.getInputStream());
             DataOutputStream dos = new DataOutputStream(socketTCP.getOutputStream())) {

            // Recibir clave pública RSA del servidor
            String clavePublicaStr = dis.readUTF();
            byte[] clavePubBytes = Base64.getDecoder().decode(clavePublicaStr);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(clavePubBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            var clavePublicaServer = keyFactory.generatePublic(spec);
            System.out.println("Clave pública RSA del servidor recibida.");

            // Autenticación: recibir reto
            int tamReto = dis.readInt();
            byte[] reto = new byte[tamReto];
            dis.readFully(reto);
            // En este ejemplo, la respuesta es el mismo reto (en un sistema real, se firmaría)
            dos.writeInt(reto.length);
            dos.write(reto);
            System.out.println("Reto respondido.");

            // Recibir clave AES cifrada
            int tamAES = dis.readInt();
            byte[] claveAESCifrada = new byte[tamAES];
            dis.readFully(claveAESCifrada);
            // Descifrar la clave AES con RSA (en un ejemplo real, se usaría la clave privada del cliente, aquí se simplifica)
            Cipher cipherRSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipherRSA.init(Cipher.DECRYPT_MODE, clavePublicaServer); // En este ejemplo se usa la clave pública para simular
            // Para fines de demostración, asumimos que el cliente ya tiene la clave AES en claro
            // Se omite la descifrado real y se simula:
            SecretKeySpec claveAES = new SecretKeySpec("claveSimuladaAES12".getBytes(), "AES"); // 16 bytes

            System.out.println("Clave AES establecida en el cliente.");

            // Enviar un mensaje seguro vía UDP utilizando la clave AES acordada
            String mensaje = "Mensaje seguro enviado vía UDP en sistema integral.";
            Cipher cipherAES = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipherAES.init(Cipher.ENCRYPT_MODE, claveAES);
            byte[] mensajeCifrado = cipherAES.doFinal(mensaje.getBytes("UTF-8"));
            DatagramSocket socketUDP = new DatagramSocket();
            DatagramPacket paquete = new DatagramPacket(mensajeCifrado, mensajeCifrado.length,
                    InetAddress.getByName(HOST), PUERTO_UDP);
            socketUDP.send(paquete);
            System.out.println("Mensaje UDP enviado.");
            socketUDP.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

