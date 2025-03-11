package TresRaya;

import java.io.*;
import java.net.*;
import java.util.*;

public class UDPServerTicTacToe {
    public static void main(String[] args) {
        int port = 15000;
        byte[] buffer = new byte[2048];
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Servidor UDP Tres en Raya escuchando en el puerto " + port);
            // Esperar el primer paquete para obtener la dirección y puerto del cliente
            DatagramPacket initPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(initPacket);
            InetAddress clientAddress = initPacket.getAddress();
            int clientPort = initPacket.getPort();

            // Inicializar el tablero
            char[][] board = new char[3][3];
            for (int i = 0; i < 3; i++) {
                Arrays.fill(board[i], ' ');
            }

            String welcomeMsg = "Bienvenido al Tres en Raya UDP. Eres 'X' y yo soy 'O'. Ingresa tus movimientos en formato: fila columna (0 a 2).";
            sendMessage(socket, welcomeMsg, clientAddress, clientPort);
            sendBoard(socket, board, clientAddress, clientPort);

            boolean gameOver = false;
            Random random = new Random();

            while (!gameOver) {
                sendMessage(socket, "Tu turno. Ingresa tu movimiento:", clientAddress, clientPort);
                DatagramPacket movePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(movePacket);
                String input = new String(movePacket.getData(), 0, movePacket.getLength()).trim();
                String[] tokens = input.split("\\s+");
                if (tokens.length != 2) {
                    sendMessage(socket, "Formato inválido. Debes ingresar dos números separados por un espacio.", clientAddress, clientPort);
                    continue;
                }
                int row, col;
                try {
                    row = Integer.parseInt(tokens[0]);
                    col = Integer.parseInt(tokens[1]);
                } catch (NumberFormatException e) {
                    sendMessage(socket, "Formato inválido. Asegúrate de ingresar números.", clientAddress, clientPort);
                    continue;
                }
                if (row < 0 || row >= 3 || col < 0 || col >= 3 || board[row][col] != ' ') {
                    sendMessage(socket, "Movimiento inválido. La posición está ocupada o fuera de rango.", clientAddress, clientPort);
                    continue;
                }
                board[row][col] = 'X';
                if (checkWin(board, 'X')) {
                    sendBoard(socket, board, clientAddress, clientPort);
                    sendMessage(socket, "¡Felicidades! Ganaste.", clientAddress, clientPort);
                    gameOver = true;
                    break;
                }
                if (isBoardFull(board)) {
                    sendBoard(socket, board, clientAddress, clientPort);
                    sendMessage(socket, "Empate.", clientAddress, clientPort);
                    gameOver = true;
                    break;
                }
                // Movimiento del servidor: elegir aleatoriamente una casilla vacía
                List<int[]> available = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (board[i][j] == ' ') {
                            available.add(new int[]{i, j});
                        }
                    }
                }
                if (available.isEmpty()) {
                    sendBoard(socket, board, clientAddress, clientPort);
                    sendMessage(socket, "Empate.", clientAddress, clientPort);
                    gameOver = true;
                    break;
                }
                int[] move = available.get(random.nextInt(available.size()));
                board[move[0]][move[1]] = 'O';
                sendMessage(socket, "Mi movimiento: " + move[0] + " " + move[1], clientAddress, clientPort);
                if (checkWin(board, 'O')) {
                    sendBoard(socket, board, clientAddress, clientPort);
                    sendMessage(socket, "Perdiste. Yo gané.", clientAddress, clientPort);
                    gameOver = true;
                    break;
                }
                if (isBoardFull(board)) {
                    sendBoard(socket, board, clientAddress, clientPort);
                    sendMessage(socket, "Empate.", clientAddress, clientPort);
                    gameOver = true;
                    break;
                }
                sendBoard(socket, board, clientAddress, clientPort);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage(DatagramSocket socket, String msg, InetAddress address, int port) throws IOException {
        byte[] data = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        socket.send(packet);
    }

    private static void sendBoard(DatagramSocket socket, char[][] board, InetAddress address, int port) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("Tablero:\n");
        for (int i = 0; i < 3; i++) {
            sb.append("|");
            for (int j = 0; j < 3; j++) {
                sb.append(board[i][j]).append("|");
            }
            sb.append("\n");
        }
        sendMessage(socket, sb.toString(), address, port);
    }

    private static boolean checkWin(char[][] board, char player) {
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
