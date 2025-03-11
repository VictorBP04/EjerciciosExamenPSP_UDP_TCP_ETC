package PPTijeras;

import java.io.*;
import java.net.*;
import java.util.Random;

public class TCPServerRPS {
    public static void main(String[] args) {
        // Número de rondas a jugar (por defecto 3)
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

        int port = 12000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor TCP Piedra, Papel o Tijeras escuchando en el puerto " + port);
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println("Bienvenido a Piedra, Papel o Tijeras. Se jugarán " + rounds + " rondas.");

            for (int i = 1; i <= rounds; i++) {
                out.println("Ronda " + i + ": Elige piedra, papel o tijeras:");
                String clientChoice = in.readLine();
                if (clientChoice == null) break;
                clientChoice = clientChoice.trim().toLowerCase();

                // Validar la elección
                boolean valid = false;
                for (String op : options) {
                    if (op.equals(clientChoice)) {
                        valid = true;
                        break;
                    }
                }
                if (!valid) {
                    out.println("Elección inválida. Se cuenta como derrota en esta ronda.");
                    scoreServer++;
                    continue;
                }

                // Elección del servidor
                String serverChoice = options[random.nextInt(options.length)];
                String roundResult;

                // Determinar el ganador de la ronda
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
                out.println(roundResult);
                out.println("Marcador: Cliente " + scoreClient + " - Servidor " + scoreServer);
            }

            // Resultado final
            String finalResult;
            if (scoreClient > scoreServer) {
                finalResult = "¡Felicidades! Ganaste el juego.";
            } else if (scoreClient < scoreServer) {
                finalResult = "Perdiste el juego. Mejor suerte la próxima vez.";
            } else {
                finalResult = "El juego terminó en empate.";
            }
            out.println("Juego terminado. " + finalResult + " (Marcador final: Cliente " + scoreClient + " - Servidor " + scoreServer + ")");

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
