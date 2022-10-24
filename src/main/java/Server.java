/**
 * @Author Michael Frank
 */

import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.File;

public class Server extends Thread {

    ServerSocket serverSocket;
    static TicTacToe game;

    public static File logFile;
    static ServerClientThread currentPlayerThread;

    static ServerClientThread playerXThread;
    static ServerClientThread playerOThread;


    public void run() {
        // Create two instances of a thread process for handling clients

        playerXThread = null;
        playerOThread = null;


        // setup for log file
        logFile = new File("game.log");


        try {
            if (logFile.createNewFile()) System.out.println("Created new log file " + logFile.getAbsolutePath());
            else { logFile.delete(); logFile.createNewFile(); System.out.println("Cleared log " + logFile.getAbsolutePath()); }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // create new socket
        try {
            serverSocket = new ServerSocket(4446);
        } catch (Exception e) {
            System.out.println("Server: " + e);
        }


        try {
            // Have server listen and accept if someone tries to connect.
            // tell main process it's OK to start up clients now
            Main.setServerStarted(true);

            serverMsg("Waiting for clients...");

            // Let both sockets accept both clients
            for (int i = 0; i < 2; i++) {
                Socket incomingClient = serverSocket.accept();

                if (i == 0) {
                    // create thread for X and start it
                    playerXThread = new ServerClientThread(incomingClient, 'X');
                    playerXThread.start();
                    serverMsg("X player connected.");
                } else {
                    // create thread for O and start it
                    playerOThread = new ServerClientThread(incomingClient, 'O');
                    playerOThread.start();
                    serverMsg("O player connected.");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // create new instance of TicTacToe game, set current player to X
        game = new TicTacToe();
        game.setPlayer(1);
        playerXThread.enabled = true;


        // THIS SECTION WILL LOOP UNTIL GAME ENDS //
        while(true) {
            // check whose turn it is in the game and change which thread is serving accordingly
            if (game.getPlayer() == 1) {
                // set current player thread to the one server X
                currentPlayerThread = playerXThread;
            }
            else if (game.getPlayer() == 0){
                // set the current player thread to the one serving O
                currentPlayerThread = playerOThread;
//                System.out.println("O's turn");
            } else {
                throw new RuntimeException();
            }

            try {
                if (currentPlayerThread.enabled) {
                    if (!currentPlayerThread.clientEnabled) {
                        currentPlayerThread.sendToClient(game.toString());
                        currentPlayerThread.enableClient();
                    }
                }

            } catch (Exception e) {
                System.out.println(e);
            }

            if (game.checkForWin()) {
                try {
                    playerXThread.sendToClient(game.toString());
                    playerOThread.sendToClient(game.toString());


                    if (game.getPlayer() == 0) {
                        playerXThread.sendToClient("O wins!");
                        playerOThread.sendToClient("O wins!");
                    } else if (game.getPlayer() == 1) {
                        playerXThread.sendToClient("O wins!");
                        playerOThread.sendToClient("O wins!");
                    }
                } catch (IOException e) {
                    System.out.println(e);
                }

                game.resetGame();
                game.setPlayer(1);
//                try {
//                    playerXThread.sendToClient("1\n");
//                    playerOThread.sendToClient("0\n");
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
                playerXThread.enabled = true;
                playerOThread.enabled = false;

            }
        }
    }

    public static void serverMsg(String msg) {
        System.out.println("[Server] " + msg);
    }

    public static void swapPlayerThread() {
        if (currentPlayerThread.equals(playerXThread)) game.setPlayer(0);
        if (currentPlayerThread.equals(playerOThread)) game.setPlayer(1);
    }

}
