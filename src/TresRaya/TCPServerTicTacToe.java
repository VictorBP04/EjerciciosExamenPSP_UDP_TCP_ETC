package TresRaya;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPServerTicTacToe {
    public static void main(String[] args) {
        int port = 14000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor TCP Tres en Raya escuchando en el puerto " + port);
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Inicializar tablero 3x3
            char[][] board = new char[3][3];
            for (int i = 0; i < 3; i++) {
                Arrays.fill(board[i], ' ');
            }

            // El cliente juega con 'X' y el servidor con 'O'
            out.println("Bienvenido al Tres en Raya. Eres 'X' y yo soy 'O'.");
            printBoard(board, out);

            boolean gameOver = false;
            Random random = new Random();

            while (!gameOver) {
                // Turno del cliente
                out.println("Tu turno. Ingresa tu movimiento en formato: fila columna (índices de 0 a 2):");
                String input = in.readLine();
                if (input == null) break;
                String[] tokens = input.trim().split("\\s+");
                if (tokens.length != 2) {
                    out.println("Formato inválido. Debes ingresar dos números separados por un espacio.");
                    continue;
                }
                int row, col;
                try {
                    row = Integer.parseInt(tokens[0]);
                    col = Integer.parseInt(tokens[1]);
                } catch (NumberFormatException e) {
                    out.println("Formato inválido. Asegúrate de ingresar números.");
                    continue;
                }
                if (row < 0 || row >= 3 || col < 0 || col >= 3 || board[row][col] != ' ') {
                    out.println("Movimiento inválido. La posición está ocupada o fuera de rango.");
                    continue;
                }
                board[row][col] = 'X';
                if (checkWin(board, 'X')) {
                    printBoard(board, out);
                    out.println("¡Felicidades! Ganaste.");
                    gameOver = true;
                    break;
                }
                if (isBoardFull(board)) {
                    printBoard(board, out);
                    out.println("Empate.");
                    gameOver = true;
                    break;
                }

                // Turno del servidor: elegir aleatoriamente una casilla vacía
                List<int[]> available = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (board[i][j] == ' ') {
                            available.add(new int[]{i, j});
                        }
                    }
                }
                if (available.isEmpty()) {
                    printBoard(board, out);
                    out.println("Empate.");
                    gameOver = true;
                    break;
                }
                int[] move = available.get(random.nextInt(available.size()));
                board[move[0]][move[1]] = 'O';
                out.println("Mi movimiento: " + move[0] + " " + move[1]);
                if (checkWin(board, 'O')) {
                    printBoard(board, out);
                    out.println("Perdiste. Yo gané.");
                    gameOver = true;
                    break;
                }
                if (isBoardFull(board)) {
                    printBoard(board, out);
                    out.println("Empate.");
                    gameOver = true;
                    break;
                }
                printBoard(board, out);
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printBoard(char[][] board, PrintWriter out) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tablero:\n");
        for (int i = 0; i < 3; i++) {
            sb.append("|");
            for (int j = 0; j < 3; j++) {
                sb.append(board[i][j]).append("|");
            }
            sb.append("\n");
        }
        out.println(sb.toString());
    }

    private static boolean checkWin(char[][] board, char player) {
        // Revisa filas, columnas y diagonales
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player)
                return true;
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player)
                return true;
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player)
            return true;
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player)
            return true;
        return false;
    }

    private static boolean isBoardFull(char[][] board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ')
                    return false;
            }
        }
        return true;
    }
}
