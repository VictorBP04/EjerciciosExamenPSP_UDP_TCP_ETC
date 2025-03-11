package PPTijeras;

import java.io.*;
import java.net.*;
import java.util.Random;

public class UDPServerRPS {
    public static void main(String[] args) {
        int rounds = 3;
        if (args.length >= 1) {
            try {
                rounds = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("El número de rondas debe ser numérico. Se usará 3 rondas.");
            }
        }
        String[] options = {"piedra", "papel", "tijeras"};
        Random random = new Random();
        int scoreServer = 0, scoreClient = 0;

        int port = 13000;
        byte[] buffer = new byte[1024];

        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Servidor UDP Piedra, Papel o Tijeras escuchando en el puerto " + port);
            // Espera el primer paquete para obtener la dirección del cliente
            DatagramPacket initPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(initPacket);
            InetAddress clientAddress = initPacket.getAddress();
            int clientPort = initPacket.getPort();

            String welcomeMsg = "Bienvenido a Piedra, Papel o Tijeras UDP. Se jugarán " + rounds + " rondas.";
            DatagramPacket welcomePacket = new DatagramPacket(welcomeMsg.getBytes(), welcomeMsg.getBytes().length, clientAddress, clientPort);
            socket.send(welcomePacket);

            for (int i = 1; i <= rounds; i++) {
                String prompt = "Ronda " + i + ": Elige piedra, papel o tijeras:";
                DatagramPacket promptPacket = new DatagramPacket(prompt.getBytes(), prompt.getBytes().length, clientAddress, clientPort);
                socket.send(promptPacket);

                // Recibe la elección del cliente
                DatagramPacket clientPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(clientPacket);
                String clientChoice = new String(clientPacket.getData(), 0, clientPacket.getLength()).trim().toLowerCase();

                boolean valid = false;
                for (String op : options) {
                    if (op.equals(clientChoice)) {
                        valid = true;
                        break;
                    }
                }
                if (!valid) {
                    String invalid = "Elección inválida. Se cuenta como derrota en esta ronda.";
                    DatagramPacket invalidPacket = new DatagramPacket(invalid.getBytes(), invalid.getBytes().length, clientAddress, clientPort);
                    socket.send(invalidPacket);
                    scoreServer++;
                    continue;
                }

                String serverChoice = options[random.nextInt(options.length)];
                String roundResult;
                if (clientChoice.equals(serverChoice)) {
                    roundResult = "Empate. Ambos eligieron " + clientChoice + ".";
                } else if ((clientChoice.equals("piedra") && serverChoice.equals("tijeras")) ||
                        (clientChoice.equals("tijeras") && serverChoice.equals("papel")) ||
                        (clientChoice.equals("papel") && serverChoice.equals("piedra"))) {
                    roundResult = "¡Ganaste la ronda! Tú: " + clientChoice + " vs Servidor: " + serverChoice;
                    scoreClient++;
                } else {
                    roundResult = "Perdiste la ronda. Tú: " + clientChoice + " vs Servidor: " + serverChoice;
                    scoreServer++;
                }
                String scoreMsg = roundResult + " | Marcador: Cliente " + scoreClient + " - Servidor " + scoreServer;
                DatagramPacket resultPacket = new DatagramPacket(scoreMsg.getBytes(), scoreMsg.getBytes().length, clientAddress, clientPort);
                socket.send(resultPacket);
            }

            String finalResult;
            if (scoreClient > scoreServer) {
                finalResult = "¡Felicidades! Ganaste el juego.";
            } else if (scoreClient < scoreServer) {
                finalResult = "Perdiste el juego. Mejor suerte la próxima vez.";
            } else {
                finalResult = "El juego terminó en empate.";
            }
            String finalMsg = "Juego terminado. " + finalResult + " (Marcador final: Cliente " + scoreClient + " - Servidor " + scoreServer + ")";
            DatagramPacket finalPacket = new DatagramPacket(finalMsg.getBytes(), finalMsg.getBytes().length, clientAddress, clientPort);
            socket.send(finalPacket);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
