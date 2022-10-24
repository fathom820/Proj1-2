import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author Michael Frank
 */
public class Client extends Thread {
    Socket socket;
    boolean enabled, askForIP;

    // IP will be needed if game is running in client-only mode
    // otherwise it will automatically connect to the local server's IP
    public Client(boolean askForIP) {
        socket = null;
        enabled = true;
        this.askForIP = askForIP;
    }

    public void run() {
        try {
            Scanner userIn = new Scanner(System.in); // messages from user

            System.out.println("Starting client...");

            if (askForIP) {
                System.out.print("Enter the IP you'd like to connect to: ");
                String hostIP = userIn.nextLine();
                socket = new Socket(hostIP, 4446); // 127.0.0.1
            } else {
                socket = new Socket("127.0.0.1", 4446);
            }

            // used to talk to server
            DataOutputStream out = new DataOutputStream((socket.getOutputStream()));
            out.flush();

            // used for server to talk to client
            Scanner serverIn = new Scanner(socket.getInputStream()); // messages from server

            while (true) {
                String serverMessage = serverIn.nextLine();

                // server-client locking mechanism, or mutex
                if  (serverMessage.equals("1")) {
                    enabled = true;
                }
                if (serverMessage.equals("0")) {
                    enabled = false;
                }

                // if server sends a message to player that isn't lock
                if  (enabled && !serverMessage.equals("1")) {
                    switch(serverMessage) {
                        case "win":
                            System.out.println("Congratulations, you win!");
                            socket.close();
                            break;
                        case "garb":
                            System.out.println("Invalid move");
                            break;
                        default:
                            System.out.println(serverMessage);
                            if (serverMessage.equals("")) { // last line of game board is empty so client knows when to send lock
                                out.writeBytes("1\n"); // send unlock to server
                                System.out.print("Which cell would you like to draw in?\n> ");
                                out.writeBytes(userIn.nextLine() + "\n"); // take user input and send to server
                            }
                            break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Client Error: " + e);
        }
    }
}
