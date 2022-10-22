import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author Michael Frank
 */
public class Client extends Thread {
    Socket socket;
    boolean lock;

    public Client() {
        socket = null;
        lock = true;
    }

    public void run() {
        try {
            System.out.println("Starting client...");
            socket = new Socket("127.0.0.1", 4446);

            DataOutputStream out = new DataOutputStream((socket.getOutputStream()));
            out.flush();

            Scanner serverIn = new Scanner(socket.getInputStream()); // messages from server
            Scanner userIn = new Scanner(System.in); // messages from user


            while (true) {
                String serverMessage = serverIn.nextLine();
                System.out.println(serverMessage);

                // Lock condition
                if (serverMessage.length() == 1) {
                    lock = serverMessage.equals("1");
                }

                if (!lock) {
                    if (serverMessage.equals("win")) {
                        System.out.println("Congratulations, you win!");
                        socket.close();
                    } else if (serverMessage.equals("garb")) {
                        System.out.println("You cannot draw in an occupied space!");
                    } else {
                        // if game board is sent, then print it
                        System.out.println(serverMessage);
                    }

                    System.out.print("Enter which cell you'd like to draw in.\n> ");

                }
            }

        } catch (Exception e) {
            System.out.println("Client Error: " + e);
        }
    }
}
