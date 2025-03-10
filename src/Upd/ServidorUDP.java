package Upd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ServidorUDP {
    public static void main(String[] args) {
        final int PUERTO = 9876;
        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {
            // Preparamos un buffer para recibir el mensaje (máximo 1024 bytes)
            byte[] buffer = new byte[1024];
            // Recibir un único paquete UDP
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
            socket.receive(paquete);
            String mensaje = new String(paquete.getData(), 0, paquete.getLength(), "UTF-8");
            System.out.println("Mensaje recibido: " + mensaje);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
