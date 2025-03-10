package RSA;

import java.security.*;

public class EjercicioRSA2_TamanosClave {
    public static void main(String[] args) {
        try {
            // Generar par de claves RSA de 1024 bits
            KeyPairGenerator keyGen1024 = KeyPairGenerator.getInstance("RSA");
            keyGen1024.initialize(1024);
            KeyPair keyPair1024 = keyGen1024.generateKeyPair();

            // Generar par de claves RSA de 2048 bits
            KeyPairGenerator keyGen2048 = KeyPairGenerator.getInstance("RSA");
            keyGen2048.initialize(2048);
            KeyPair keyPair2048 = keyGen2048.generateKeyPair();

            System.out.println("Longitud de la clave pública de 1024 bits: " +
                    keyPair1024.getPublic().getEncoded().length + " bytes");
            System.out.println("Longitud de la clave pública de 2048 bits: " +
                    keyPair2048.getPublic().getEncoded().length + " bytes");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

