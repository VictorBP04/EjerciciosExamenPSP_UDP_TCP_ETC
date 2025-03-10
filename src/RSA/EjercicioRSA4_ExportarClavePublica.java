package RSA;

import java.security.*;
import java.util.Base64;

public class EjercicioRSA4_ExportarClavePublica {
    public static void main(String[] args) {
        try {
            // Generar un par de claves RSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();

            // Exportar la clave pública en Base64
            String clavePublicaBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            System.out.println("Clave pública (Base64): " + clavePublicaBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

