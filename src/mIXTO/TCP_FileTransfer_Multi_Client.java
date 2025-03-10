package mIXTO;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;

public class TCP_FileTransfer_Multi_Client {
    private static final byte[] claveBytes = "multiAESclave1234".substring(0,16).getBytes();
    private static final SecretKeySpec claveAES = new SecretKeySpec(claveBytes, "AES");

    public static void main(String[] args) {
        final String HOST = "localhost";
        final int PUERTO = 5920;
        try (Socket socket = new Socket(HOST, PUERTO);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
            // Leer archivo a enviar (por ejemplo, "archivo_para_enviar.dat")
            File archivo = new File("archivo_para_enviar.dat");
            byte[] archivoBytes = new byte[(int) archivo.length()];
            try (FileInputStream fis = new FileInputStream(archivo)) {
                fis.read(archivoBytes);
            }

            // Cifrar el archivo con AES
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, claveAES);
            byte[] archivoCifrado = cipher.doFinal(archivoBytes);

            // Enviar nombre y tamaño del archivo seguido de los datos cifrados
            dos.writeUTF(archivo.getName());
            dos.writeLong(archivoCifrado.length);
            dos.write(archivoCifrado);
            System.out.println("Archivo enviado: " + archivo.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

