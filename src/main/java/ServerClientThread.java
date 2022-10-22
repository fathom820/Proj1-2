import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ServerClientThread extends Thread {

    private Socket client;
    Scanner in;
    DataOutputStream out;

    char player;

    boolean clientLocked;
    public ServerClientThread(Socket incomingClient, char player) {
        client = incomingClient;
        clientLocked = true;
        this.player = player;
    }

    public void run() {
        try {
            in = new Scanner(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
            out.flush();

            String message;

            while(true) {
                message = in.nextLine();
                System.out.println(message);
            }

        } catch (IOException e) {
            System.out.println("ServerClientThread: " + e);
        }

    }

    public void sendToClient(String msg) throws IOException {
        out.writeBytes(msg + "\n");
        out.flush();
    }

    public void setClientLocked(boolean clientLocked) {
        this.clientLocked = clientLocked;
    }
}
