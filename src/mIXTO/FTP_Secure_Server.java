package mIXTO;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.StringTokenizer;

public class FTP_Secure_Server {
    public static void main(String[] args) {
        final int PUERTO = 5800;
        // Clave AES fija (16 bytes)
        final byte[] claveBytes = "ftpAESclave12345".substring(0,16).getBytes();
        SecretKeySpec claveAES = new SecretKeySpec(claveBytes, "AES");

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor FTP seguro iniciado en el puerto " + PUERTO);
            // Aceptamos una única conexión para este ejemplo
            Socket socket = serverSocket.accept();
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            System.out.println("Cliente FTP conectado.");

            // Leer comandos cifrados
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            while (true) {
                int tam = dis.readInt();
                byte[] comandoCifrado = new byte[tam];
                dis.readFully(comandoCifrado);
                cipher.init(Cipher.DECRYPT_MODE, claveAES);
                String comando = new String(cipher.doFinal(comandoCifrado), "UTF-8");
                System.out.println("Comando recibido: " + comando);

                // Salir si se recibe QUIT
                if (comando.equalsIgnoreCase("QUIT")) {
                    break;
                }

                String respuesta = "";
                StringTokenizer st = new StringTokenizer(comando);
                String accion = st.nextToken().toUpperCase();
                switch (accion) {
                    case "USER":
                        respuesta = "Usuario aceptado.";
                        break;
                    case "PASS":
                        respuesta = "Conexión autenticada.";
                        break;
                    case "LIST":
                        // Listar archivos del directorio actual
                        File dir = new File(".");
                        StringBuilder list = new StringBuilder();
                        for (String f : dir.list()) {
                            list.append(f).append("\n");
                        }
                        respuesta = list.toString();
                        break;
                    case "GET":
                        String archivoSolicitado = st.nextToken();
                        File file = new File(archivoSolicitado);
                        if (file.exists()) {
                            respuesta = "OK|" + file.length();
                            // Enviar respuesta y luego el archivo (aquí solo enviamos mensaje)
                        } else {
                            respuesta = "ERROR: Archivo no encontrado.";
                        }
                        break;
                    case "PUT":
                        // Para este ejemplo se omite la implementación completa
                        respuesta = "Comando PUT recibido.";
                        break;
                    default:
                        respuesta = "Comando no reconocido.";
                        break;
                }
                // Enviar respuesta cifrada
                cipher.init(Cipher.ENCRYPT_MODE, claveAES);
                byte[] respCifrada = cipher.doFinal(respuesta.getBytes("UTF-8"));
                dos.writeInt(respCifrada.length);
                dos.write(respCifrada);
            }
            System.out.println("Conexión finalizada.");
            dis.close();
            dos.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
