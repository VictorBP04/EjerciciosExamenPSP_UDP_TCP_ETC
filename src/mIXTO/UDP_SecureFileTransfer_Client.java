package mIXTO;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.File;
import java.io.FileInputStream;
import java.util.zip.CRC32;

public class UDP_SecureFileTransfer_Client {
    public static void main(String[] args) {
        final int PUERTO = 5555;
        final String HOST = "localhost";
        // Usamos la misma clave AES
        final byte[] claveBytes = "claveAES12345678".getBytes();
        SecretKeySpec claveAES = new SecretKeySpec(claveBytes, "AES");

        try (DatagramSocket socket = new DatagramSocket()) {
            // Leer el archivo a enviar (asegúrate de tener un archivo "archivo_a_enviar.dat")
            File archivo = new File("archivo_a_enviar.dat");
            byte[] archivoBytes = new byte[(int) archivo.length()];
            try (FileInputStream fis = new FileInputStream(archivo)) {
                fis.read(archivoBytes);
            }

            int tamBloque = 512; // tamaño de bloque en bytes
            int numBloques = (int) Math.ceil(archivoBytes.length / (double) tamBloque);
            InetAddress direccion = InetAddress.getByName(HOST);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, claveAES);

            for (int i = 0; i < numBloques; i++) {
                int inicio = i * tamBloque;
                int tam = Math.min(tamBloque, archivoBytes.length - inicio);
                byte[] bloque = new byte[tam];
                System.arraycopy(archivoBytes, inicio, bloque, 0, tam);
                // Cifrar el bloque
                byte[] bloqueCifrado = cipher.doFinal(bloque);
                // Calcular checksum (CRC32) del bloque original
                CRC32 crc = new CRC32();
                crc.update(bloque);
                long checksum = crc.getValue();
                // Convertir el checksum a 8 bytes
                byte[] checksumBytes = new byte[8];
                for (int j = 7; j >= 0; j--) {
                    checksumBytes[j] = (byte) (checksum & 0xff);
                    checksum >>= 8;
                }
                // Combinar bloque cifrado y checksum
                byte[] paqueteEnvio = new byte[bloqueCifrado.length + 8];
                System.arraycopy(bloqueCifrado, 0, paqueteEnvio, 0, bloqueCifrado.length);
                System.arraycopy(checksumBytes, 0, paqueteEnvio, bloqueCifrado.length, 8);

                DatagramPacket paquete = new DatagramPacket(paqueteEnvio, paqueteEnvio.length, direccion, PUERTO);
                socket.send(paquete);
                System.out.println("Bloque " + (i + 1) + " enviado.");
            }
            System.out.println("Archivo enviado completamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

