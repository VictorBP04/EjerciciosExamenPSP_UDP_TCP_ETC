package Anagramas;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPServerAnagram {
    // Método para mezclar las letras de una palabra
    private static String scrambleWord(String word) {
        List<Character> characters = new ArrayList<>();
        for (char c : word.toCharArray()) {
            characters.add(c);
        }
        Collections.shuffle(characters);
        StringBuilder sb = new StringBuilder();
        for (char c : characters) {
            sb.append(c);
        }
        // Si por azar la palabra queda igual, se vuelve a barajar
        if(sb.toString().equals(word)) {
            return scrambleWord(word);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        // Lista de palabras para el juego
        String[] words = {"java", "programacion", "socket", "anagrama", "cliente"};
        // Seleccionar una palabra aleatoria
        Random random = new Random();
        String original = words[random.nextInt(words.length)];
        String scrambled = scrambleWord(original);

        // Número de intentos permitidos
        int maxAttempts = 3;
        if(args.length >= 1) {
            try {
                maxAttempts = Integer.parseInt(args[0]);
            } catch(NumberFormatException e) {
                System.out.println("El número de intentos debe ser numérico. Se usará 3 intentos.");
            }
        }

        int port = 18000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor TCP Anagramas escuchando en el puerto " + port);
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Enviar bienvenida y datos del juego
            out.println("Bienvenido al juego de Anagramas.");
            out.println("Palabra mezclada: " + scrambled);
            out.println("Tienes " + maxAttempts + " intentos para adivinar la palabra original.");

            int attempts = 0;
            String guess;
            boolean acierto = false;
            while(attempts < maxAttempts && (guess = in.readLine()) != null) {
                attempts++;
                if(guess.trim().equalsIgnoreCase(original)) {
                    out.println("¡Correcto! Has adivinado la palabra en " + attempts + " intentos.");
                    acierto = true;
                    break;
                } else {
                    if(attempts < maxAttempts)
                        out.println("Incorrecto. Intenta de nuevo. Te quedan " + (maxAttempts - attempts) + " intentos.");
                }
            }
            if(!acierto) {
                out.println("Se han agotado los intentos. La palabra original era: " + original);
            }
            clientSocket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
