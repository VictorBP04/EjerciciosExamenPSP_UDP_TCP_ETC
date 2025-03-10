package RSA;

import java.security.*;
import java.util.Base64;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class EjercicioRSA6_Serializacion {
    public static void main(String[] args) {
        try {
            // Generar un par de claves RSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();

            // Guardar la clave pública en un archivo
            try (FileOutputStream fos = new FileOutputStream("clavePublica.ser");
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(keyPair.getPublic());
            }
            System.out.println("Clave pública guardada en 'clavePublica.ser'");

            // Leer la clave pública del archivo
            PublicKey clavePublicaRecuperada;
            try (FileInputStream fis = new FileInputStream("clavePublica.ser");
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                clavePublicaRecuperada = (PublicKey) ois.readObject();
            }

            // Imprimir la clave pública en Base64
            String clavePublicaBase64 = Base64.getEncoder().encodeToString(clavePublicaRecuperada.getEncoded());
            System.out.println("Clave pública recuperada (Base64): " + clavePublicaBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

