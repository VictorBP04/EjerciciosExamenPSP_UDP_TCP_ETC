package mIXTO;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.FileOutputStream;
import java.util.zip.CRC32;

public class UDP_SecureFileTransfer_Server {
    public static void main(String[] args) {
        final int PUERTO = 5555;
        final int NUM_BLOQUES = 5; // Número de bloques fijos para este ejemplo
        // Clave AES de 16 bytes (asegúrate de que tenga 16 caracteres)
        final byte[] claveBytes = "claveAES12345678".getBytes();
        SecretKeySpec claveAES = new SecretKeySpec(claveBytes, "AES");

        try (DatagramSocket socket = new DatagramSocket(PUERTO);
             FileOutputStream fos = new FileOutputStream("archivo_recibido.dat")) {

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, claveAES);

            // Recorrer cada bloque esperado (no usamos while(true), se fija el número de bloques)
            for (int i = 0; i < NUM_BLOQUES; i++) {
                // Suponemos que cada paquete contiene: [datos cifrados] + [8 bytes de checksum]
                byte[] buffer = new byte[550]; // 512 bytes cifrados + 8 bytes checksum (puede variar según padding)
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);
                int totalBytes = paquete.getLength();
                // Separar datos cifrados y checksum
                int tamCifrado = totalBytes - 8;
                byte[] bloqueCifrado = new byte[tamCifrado];
                System.arraycopy(buffer, 0, bloqueCifrado, 0, tamCifrado);
                byte[] checksumBytes = new byte[8];
                System.arraycopy(buffer, tamCifrado, checksumBytes, 0, 8);

                // Descifrar el bloque
                byte[] bloqueDescifrado = cipher.doFinal(bloqueCifrado);

                // Calcular checksum del bloque descifrado
                CRC32 crc = new CRC32();
                crc.update(bloqueDescifrado);
                long checksumCalculado = crc.getValue();

                // Convertir checksum recibido a long
                long checksumRecibido = 0;
                for (int j = 0; j < 8; j++) {
                    checksumRecibido = (checksumRecibido << 8) | (checksumBytes[j] & 0xff);
                }

                if (checksumCalculado == checksumRecibido) {
                    fos.write(bloqueDescifrado);
                    System.out.println("Bloque " + (i + 1) + " recibido, verificado y escrito.");
                } else {
                    System.out.println("Error de integridad en el bloque " + (i + 1) + ". Se requiere retransmisión.");
                    // En este ejemplo no se implementa lógica de retransmisión real,
                    // pero podrías reintentar enviar ese bloque (número de intentos finito).
                }
            }
            System.out.println("Transferencia de archivo completada.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

