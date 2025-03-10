package RSA;

import java.security.*;
import java.nio.file.*;
import java.util.Base64;

public class EjercicioRSA7_FirmaArchivo {
    public static void main(String[] args) {
        try {
            // Leer el contenido de un archivo pequeño (archivo.txt)
            byte[] datos = Files.readAllBytes(Paths.get("archivo.txt"));

            // Generar un par de claves RSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();

            // Firmar el contenido del archivo
            Signature firma = Signature.getInstance("SHA256withRSA");
            firma.initSign(keyPair.getPrivate());
            firma.update(datos);
            byte[] firmaDigital = firma.sign();
            String firmaBase64 = Base64.getEncoder().encodeToString(firmaDigital);
            System.out.println("Firma digital del archivo (Base64): " + firmaBase64);

            // Verificar la firma
            Signature verificador = Signature.getInstance("SHA256withRSA");
            verificador.initVerify(keyPair.getPublic());
            verificador.update(datos);
            boolean verificado = verificador.verify(firmaDigital);
            System.out.println("Firma del archivo verificada: " + verificado);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
