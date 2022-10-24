/**
 * @author Michael Frank
 * This class is used by each thread run by the server
 * to handle the 2 clients. Each thread utilizes locks,
 * as do the clients. This is what allows the concurrency
 * to work in tandem with the game's logic.
 */

import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ServerClientThread extends Thread {

    private Socket client;
    Scanner in;
    DataOutputStream out;

    char player; // X or O representing which player this thread is representing

    boolean clientEnabled; // whether or not the client is locked
    boolean enabled; // whether or not this thread is locked

    public ServerClientThread(Socket incomingClient, char player) {
        client = incomingClient;
        clientEnabled = false;
        enabled = false;
        this.player = player;
    }

    public void run() {
        try {
            in = new Scanner(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
            out.flush();

            InetSocketAddress socketAddress = (InetSocketAddress) client.getRemoteSocketAddress();
                String clientIpAddress = socketAddress.getAddress().getHostAddress();
            String message;

            // filewriter needed to write to log file
            FileWriter fileWriter = new FileWriter(Server.logFile.getAbsolutePath());

            while(true) {
                message = in.nextLine();
                // client will tell thread when to unlock so it can process input
                if (message.equals("1")) enabled = true;
                if (message.equals("0")) enabled = false;

                // if the player sends a message that isn't a lock
                if (enabled && !message.equals("1")) {
                    // player, move, IP from, IP to (server IP)
                    String logOut = (player + " " + message + " " + clientIpAddress + " 127.0.0.1" + "\n");
                    fileWriter.write(logOut);
                    fileWriter.flush();
                    Server.game.makeMove(message);

                    if (Server.game.checkForWin()) {
                        System.out.println(Server.game.toString());
                        System.out.println(player + " wins!");
                    }
                    sendToClient(Server.game.toString());
                    Server.swapPlayerThread();
                    enabled = false;
                }
            }
        } catch (IOException e) {
            System.out.println("ServerClientThread: " + e);
        }
    }

    // sends a text string to the respective client
    public void sendToClient(String msg) throws IOException {
        out.writeBytes(msg + "\n");
        out.flush();
    }

    // sends the unlock key to the client
    // updates the local clientEnabled variable
    public void enableClient() throws IOException {
        out.writeBytes("1");
        out.flush();
        clientEnabled = true;
    }
}
