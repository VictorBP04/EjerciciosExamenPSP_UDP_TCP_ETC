package mIXTO;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class Hybrid_Secure_Server {
    public static void main(String[] args) {
        final int PUERTO_TCP = 5900;
        final int PUERTO_UDP = 5901;
        try (ServerSocket serverTCP = new ServerSocket(PUERTO_TCP)) {
            System.out.println("Servidor híbrido: Esperando conexión TCP...");
            Socket socketTCP = serverTCP.accept();
            DataOutputStream dos = new DataOutputStream(socketTCP.getOutputStream());
            DataInputStream dis = new DataInputStream(socketTCP.getInputStream());

            // Generar par RSA para el servidor
            KeyPairGenerator keyGenRSA = KeyPairGenerator.getInstance("RSA");
            keyGenRSA.initialize(2048);
            KeyPair keyPairServer = keyGenRSA.generateKeyPair();

            // Enviar clave pública RSA en Base64 al cliente
            String clavePublica = Base64.getEncoder().encodeToString(keyPairServer.getPublic().getEncoded());
            dos.writeUTF(clavePublica);
            System.out.println("Servidor híbrido: Clave pública RSA enviada.");

            // Recibir clave AES cifrada del cliente
            int tamAES = dis.readInt();
            byte[] aesCifrado = new byte[tamAES];
            dis.readFully(aesCifrado);

            // Descifrar la clave AES con la clave privada RSA
            Cipher cipherRSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipherRSA.init(Cipher.DECRYPT_MODE, keyPairServer.getPrivate());
            byte[] claveAESBytes = cipherRSA.doFinal(aesCifrado);
            SecretKeySpec claveAES = new SecretKeySpec(claveAESBytes, "AES");
            System.out.println("Servidor híbrido: Clave AES establecida.");

            // Ahora, recibir archivo cifrado vía UDP
            try (DatagramSocket socketUDP = new DatagramSocket(PUERTO_UDP);
                 DataOutputStream fos = new DataOutputStream(new java.io.FileOutputStream("archivo_hibrido_recibido.dat"))) {
                // Se fija el número de paquetes para el ejemplo
                int numPaquetes = dis.readInt();
                Cipher cipherAES = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipherAES.init(Cipher.DECRYPT_MODE, claveAES);
                for (int i = 0; i < numPaquetes; i++) {
                    byte[] buffer = new byte[1024];
                    DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                    socketUDP.receive(paquete);
                    byte[] datosCifrado = new byte[paquete.getLength()];
                    System.arraycopy(buffer, 0, datosCifrado, 0, paquete.getLength());
                    byte[] datosDescifrado = cipherAES.doFinal(datosCifrado);
                    fos.write(datosDescifrado);
                    System.out.println("Paquete UDP " + (i+1) + " recibido y descifrado.");
                }
            }
            System.out.println("Transferencia de archivo híbrido completada.");
            dis.close();
            dos.close();
            socketTCP.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
