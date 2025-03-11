package Simon;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPServerSimon {
    public static void main(String[] args) {
        int port = 9000;
        // Lista de colores disponibles
        String[] colors = {"rojo", "azul", "verde", "amarillo"};
        Random random = new Random();
        List<String> sequence = new ArrayList<>();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor TCP Simon escuchando en el puerto " + port);
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println("Bienvenido al juego Simon. Reproduce la secuencia de colores que se te muestre.");

            int round = 0;
            boolean gameOver = false;
            while (!gameOver) {
                round++;
                // Se añade un color aleatorio a la secuencia
                String nextColor = colors[random.nextInt(colors.length)];
                sequence.add(nextColor);

                // Enviar la secuencia al cliente
                out.println("Ronda " + round + ": Memoriza la siguiente secuencia:");
                out.println(String.join(" ", sequence));
                // Solicita al cliente que introduzca la secuencia (los colores separados por espacios)
                out.println("Ahora, introduce la secuencia separada por espacios:");

                // Leer respuesta del cliente
                String clientResponse = in.readLine();
                if (clientResponse == null) break;
                clientResponse = clientResponse.trim().toLowerCase();
                String correctSequence = String.join(" ", sequence);
                if (clientResponse.equals(correctSequence)) {
                    out.println("¡Correcto! Pasas a la siguiente ronda.");
                } else {
                    out.println("Fallo. La secuencia correcta era: " + correctSequence);
                    out.println("Juego terminado. Llegaste a la ronda " + round);
                    gameOver = true;
                }
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

