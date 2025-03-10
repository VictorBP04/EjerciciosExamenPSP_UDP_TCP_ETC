package mIXTO;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Base64;

public class Comprehensive_Secure_Server {
    public static void main(String[] args) {
        final int PUERTO_TCP = 5950;
        final int PUERTO_UDP = 5951;
        try (ServerSocket serverSocket = new ServerSocket(PUERTO_TCP)) {
            System.out.println("Servidor integral seguro: esperando conexión TCP...");
            Socket socketTCP = serverSocket.accept();
            DataOutputStream dos = new DataOutputStream(socketTCP.getOutputStream());
            DataInputStream dis = new DataInputStream(socketTCP.getInputStream());

            // Generar par RSA para el servidor
            KeyPairGenerator keyGenRSA = KeyPairGenerator.getInstance("RSA");
            keyGenRSA.initialize(2048);
            KeyPair keyPairServer = keyGenRSA.generateKeyPair();
            String clavePublica = Base64.getEncoder().encodeToString(keyPairServer.getPublic().getEncoded());
            dos.writeUTF(clavePublica);
            System.out.println("Clave pública RSA enviada al cliente.");

            // Autenticación basada en reto
            byte[] reto = new byte[16];
            new SecureRandom().nextBytes(reto);
            dos.writeInt(reto.length);
            dos.write(reto);
            // Recibir respuesta del cliente
            int tamRespuesta = dis.readInt();
            byte[] respuesta = new byte[tamRespuesta];
            dis.readFully(respuesta);
            // En este ejemplo se asume que la respuesta es correcta si coincide
            // (Se podría implementar firma digital para verificar)
            boolean autenticado = java.util.Arrays.equals(reto, respuesta);
            if (!autenticado) {
                System.out.println("Autenticación fallida.");
                socketTCP.close();
                return;
            }
            System.out.println("Cliente autenticado.");

            // Intercambio de clave AES
            KeyGenerator keyGenAES = KeyGenerator.getInstance("AES");
            keyGenAES.init(128);
            SecretKey claveAES = keyGenAES.generateKey();
            Cipher cipherRSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipherRSA.init(Cipher.ENCRYPT_MODE, keyPairServer.getPublic());
            byte[] claveAESCifrada = cipherRSA.doFinal(claveAES.getEncoded());
            dos.writeInt(claveAESCifrada.length);
            dos.write(claveAESCifrada);
            System.out.println("Clave AES enviada (cifrada con RSA).");

            // Ahora, usar UDP para transferir un mensaje seguro
            try (DatagramSocket socketUDP = new DatagramSocket(PUERTO_UDP)) {
                byte[] buffer = new byte[1024];
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socketUDP.receive(paquete);
                int tamMsg = paquete.getLength();
                byte[] mensajeCifrado = new byte[tamMsg];
                System.arraycopy(buffer, 0, mensajeCifrado, 0, tamMsg);
                Cipher cipherAES = Cipher.getInstance("AES/ECB/PKCS5Padding");
                SecretKeySpec claveAESSpec = new SecretKeySpec(claveAES.getEncoded(), "AES");
                cipherAES.init(Cipher.DECRYPT_MODE, claveAESSpec);
                byte[] mensajeDescifrado = cipherAES.doFinal(mensajeCifrado);
                System.out.println("Mensaje UDP recibido: " + new String(mensajeDescifrado, "UTF-8"));
            }
            dis.close();
            dos.close();
            socketTCP.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

