package mIXTO;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Hybrid_Secure_Client {
    public static void main(String[] args) {
        final String HOST = "localhost";
        final int PUERTO_TCP = 5900;
        final int PUERTO_UDP = 5901;
        try (Socket socketTCP = new Socket(HOST, PUERTO_TCP);
             DataInputStream dis = new DataInputStream(socketTCP.getInputStream());
             DataOutputStream dos = new DataOutputStream(socketTCP.getOutputStream())) {

            // Recibir clave pública RSA del servidor
            String clavePublicaStr = dis.readUTF();
            byte[] clavePubBytes = Base64.getDecoder().decode(clavePublicaStr);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(clavePubBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            var clavePublicaServer = keyFactory.generatePublic(spec);
            System.out.println("Cliente híbrido: Clave pública RSA recibida.");

            // Generar clave AES
            KeyGenerator keyGenAES = KeyGenerator.getInstance("AES");
            keyGenAES.init(128);
            SecretKey claveAES = keyGenAES.generateKey();

            // Cifrar clave AES con clave pública RSA del servidor
            Cipher cipherRSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipherRSA.init(Cipher.ENCRYPT_MODE, clavePublicaServer);
            byte[] aesCifrado = cipherRSA.doFinal(claveAES.getEncoded());
            dos.writeInt(aesCifrado.length);
            dos.write(aesCifrado);
            System.out.println("Cliente híbrido: Clave AES cifrada enviada.");

            // Leer archivo a enviar (asegúrate de tener "archivo_hibrido.dat")
            File archivo = new File("archivo_hibrido.dat");
            byte[] archivoBytes = new byte[(int) archivo.length()];
            try (FileInputStream fis = new FileInputStream(archivo)) {
                fis.read(archivoBytes);
            }
            // Dividir en fragmentos de 512 bytes y cifrar cada uno con AES
            int tamBloque = 512;
            int numBloques = (int) Math.ceil(archivoBytes.length / (double) tamBloque);
            dos.writeInt(numBloques); // Enviar número de bloques al servidor

            Cipher cipherAES = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipherAES.init(Cipher.ENCRYPT_MODE, claveAES);
            InetAddress direccion = InetAddress.getByName(HOST);
            try (DatagramSocket socketUDP = new DatagramSocket()) {
                for (int i = 0; i < numBloques; i++) {
                    int inicio = i * tamBloque;
                    int tam = Math.min(tamBloque, archivoBytes.length - inicio);
                    byte[] bloque = new byte[tam];
                    System.arraycopy(archivoBytes, inicio, bloque, 0, tam);
                    byte[] bloqueCifrado = cipherAES.doFinal(bloque);
                    DatagramPacket paquete = new DatagramPacket(bloqueCifrado, bloqueCifrado.length, direccion, PUERTO_UDP);
                    socketUDP.send(paquete);
                    System.out.println("Bloque " + (i+1) + " enviado vía UDP.");
                }
            }
            System.out.println("Archivo enviado en sistema híbrido.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

