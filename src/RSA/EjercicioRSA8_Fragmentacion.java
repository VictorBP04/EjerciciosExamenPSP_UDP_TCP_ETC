package RSA;

import java.security.*;
import javax.crypto.Cipher;
import java.util.Base64;

public class EjercicioRSA8_Fragmentacion {
    public static void main(String[] args) {
        try {
            // Generar un par de claves RSA de 2048 bits
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();

            // Mensaje largo a encriptar
            String mensaje = "Este es un mensaje muy largo que necesita ser fragmentado para ser encriptado con RSA, ya que RSA sólo puede encriptar bloques de datos pequeños.";
            byte[] mensajeBytes = mensaje.getBytes("UTF-8");

            // Configurar Cipher con RSA y PKCS1Padding
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());

            // Tamaño máximo de bloque para RSA 2048 bits (aprox. 214 bytes)
            int maxBlockSize = 214;
            int inputLength = mensajeBytes.length;
            int offset = 0;
            StringBuilder mensajeCifradoBase64 = new StringBuilder();

            // Fragmentar y encriptar cada bloque
            while (offset < inputLength) {
                int blockSize = Math.min(maxBlockSize, inputLength - offset);
                byte[] block = cipher.doFinal(mensajeBytes, offset, blockSize);
                mensajeCifradoBase64.append(Base64.getEncoder().encodeToString(block)).append("\n");
                offset += blockSize;
            }
            System.out.println("Mensaje encriptado en fragmentos:\n" + mensajeCifradoBase64.toString());

            // Nota: Para desencriptar se debe dividir la cadena en bloques y procesarlos individualmente.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

