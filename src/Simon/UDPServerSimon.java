package Simon;

import java.io.*;
import java.net.*;
import java.util.*;

public class UDPServerSimon {
    public static void main(String[] args) {
        int port = 9001;
        String[] colors = {"rojo", "azul", "verde", "amarillo"};
        Random random = new Random();
        List<String> sequence = new ArrayList<>();

        byte[] buffer = new byte[2048];
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Servidor UDP Simon escuchando en el puerto " + port);
            // Espera el primer paquete para obtener la dirección y puerto del cliente
            DatagramPacket initPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(initPacket);
            InetAddress clientAddress = initPacket.getAddress();
            int clientPort = initPacket.getPort();

            String welcomeMsg = "Bienvenido al juego Simon UDP. Reproduce la secuencia de colores.";
            DatagramPacket welcomePacket = new DatagramPacket(welcomeMsg.getBytes(), welcomeMsg.getBytes().length, clientAddress, clientPort);
            socket.send(welcomePacket);

            int round = 0;
            boolean gameOver = false;
            while (!gameOver) {
                round++;
                String nextColor = colors[random.nextInt(colors.length)];
                sequence.add(nextColor);
                String sequenceStr = String.join(" ", sequence);
                String roundMsg = "Ronda " + round + ":\nMemoriza la siguiente secuencia:\n" + sequenceStr + "\nAhora, introduce la secuencia separada por espacios:";
                DatagramPacket roundPacket = new DatagramPacket(roundMsg.getBytes(), roundMsg.getBytes().length, clientAddress, clientPort);
                socket.send(roundPacket);

                // Espera la respuesta del cliente
                DatagramPacket answerPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(answerPacket);
                String clientResponse = new String(answerPacket.getData(), 0, answerPacket.getLength()).trim().toLowerCase();
                if (clientResponse.equals(sequenceStr)) {
                    String correctMsg = "¡Correcto! Pasas a la siguiente ronda.";
                    DatagramPacket correctPacket = new DatagramPacket(correctMsg.getBytes(), correctMsg.getBytes().length, clientAddress, clientPort);
                    socket.send(correctPacket);
                } else {
                    String failMsg = "Fallo. La secuencia correcta era: " + sequenceStr + "\nJuego terminado. Llegaste a la ronda " + round;
                    DatagramPacket failPacket = new DatagramPacket(failMsg.getBytes(), failMsg.getBytes().length, clientAddress, clientPort);
                    socket.send(failPacket);
                    gameOver = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

