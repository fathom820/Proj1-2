
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

    ServerSocket serverSocket;

    public void run() {
        ServerClientThread playerXThread= null;
        ServerClientThread playerOThread = null;

        try {
            serverSocket = new ServerSocket(4446);
        } catch (Exception e) {
            System.out.println("Server: " + e);
        }

        try {
            // Have server listen and accept if someone tries to connect.
            Main.setServerStarted(true);

            serverMsg("Waiting for clients...");
            for (int i = 0; i < 2; i++) {
                Socket incomingClient = serverSocket.accept();

                if (i == 0) {
                    playerXThread = new ServerClientThread(incomingClient, 'X');
                    playerXThread.start();
                    serverMsg("X player connected.");
                } else {
                    playerOThread = new ServerClientThread(incomingClient, 'O');
                    playerOThread.start();
                    serverMsg("O player connected.");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        TicTacToe game = new TicTacToe();
        game.setPlayer(1);

        while(true) {
            // check whose turn it is
            ServerClientThread currentPlayerThread;
            if (game.getPlayer() == 1) {
                currentPlayerThread = playerXThread;
            }
            else if (game.getPlayer() == 0){
                currentPlayerThread = playerOThread;
            } else {
                throw new RuntimeException();
            }

            try {
                // unlock player
                if (currentPlayerThread.clientLocked) {
                    currentPlayerThread.sendToClient(game.toString());
                    currentPlayerThread.sendToClient("0");
                    currentPlayerThread.setClientLocked(false);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void serverMsg(String msg) {
        System.out.println("[Server] " + msg);
    }
}
