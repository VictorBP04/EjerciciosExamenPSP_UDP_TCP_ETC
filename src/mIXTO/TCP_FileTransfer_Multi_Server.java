package mIXTO;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Base64;

public class TCP_FileTransfer_Multi_Server {
    private static final byte[] claveBytes = "multiAESclave1234".substring(0,16).getBytes();
    private static final SecretKeySpec claveAES = new SecretKeySpec(claveBytes, "AES");

    public static void main(String[] args) {
        final int PUERTO = 5920;
        ExecutorService pool = Executors.newFixedThreadPool(3);
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor de transferencia multi-hilo iniciado en el puerto " + PUERTO);
            while (true) {
                Socket socket = serverSocket.accept();
                pool.execute(() -> manejarTransferencia(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void manejarTransferencia(Socket socket) {
        try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {
            // Recibir nombre del archivo y tamaño
            String nombreArchivo = dis.readUTF();
            long tamArchivo = dis.readLong();
            byte[] archivoCifrado = new byte[(int) tamArchivo];
            dis.readFully(archivoCifrado);

            // Descifrar el archivo con AES
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, claveAES);
            byte[] archivoDescifrado = cipher.doFinal(archivoCifrado);

            // Guardar el archivo
            try (FileOutputStream fos = new FileOutputStream("recibidos_" + nombreArchivo)) {
                fos.write(archivoDescifrado);
            }

            // Generar firma digital del archivo recibido para log
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(keyPair.getPrivate());
            signature.update(archivoDescifrado);
            byte[] firma = signature.sign();
            String firmaBase64 = Base64.getEncoder().encodeToString(firma);
            System.out.println("Archivo " + nombreArchivo + " recibido y firmado: " + firmaBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

