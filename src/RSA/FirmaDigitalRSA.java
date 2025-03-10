package RSA;

import java.security.*;
import java.util.Base64;

public class FirmaDigitalRSA {
    public static void main(String[] args) {
        try {
            // Generar par de claves RSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();

            String mensaje = "Mensaje para firmar digitalmente";

            // Crear la firma digital
            Signature firma = Signature.getInstance("SHA256withRSA");
            firma.initSign(keyPair.getPrivate());
            firma.update(mensaje.getBytes("UTF-8"));
            byte[] firmaDigital = firma.sign();
            String firmaBase64 = Base64.getEncoder().encodeToString(firmaDigital);
            System.out.println("Firma digital generada: " + firmaBase64);

            // Verificar la firma digital
            Signature verificador = Signature.getInstance("SHA256withRSA");
            verificador.initVerify(keyPair.getPublic());
            verificador.update(mensaje.getBytes("UTF-8"));
            boolean verificado = verificador.verify(firmaDigital);
            System.out.println("Firma verificada: " + verificado);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

