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
    boolean lock;

    public ServerClientThread(Socket incomingClient, char player) {
        client = incomingClient;
        clientLocked = true;
        lock = false;
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
                if (message.equals("1")) lock = true;
                if (message.equals("0")) lock = false;

                if (lock && !message.equals("1")) {

                }
            }

        } catch (IOException e) {
            System.out.println("ServerClientThread: " + e);
        }

    }

    public void sendToClient(String msg) throws IOException {
        out.writeBytes(msg + "\n");
        out.flush();
    }

    public void setClientLock(boolean clientLocked) throws IOException {
        this.clientLocked = clientLocked;
    }
}
