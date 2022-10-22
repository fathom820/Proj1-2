import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author Michael Frank
 */
public class Client extends Thread {
    Socket socket;
    boolean lock, askForIP;

    public Client(boolean askForIP) {
        socket = null;
        lock = true;
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



            DataOutputStream out = new DataOutputStream((socket.getOutputStream()));
            out.flush();

            Scanner serverIn = new Scanner(socket.getInputStream()); // messages from server



            while (true) {
                String serverMessage = serverIn.nextLine();
//                System.out.println(serverMessage);

                // Lock condition
                if (serverMessage.length() == 1) {
                    if (serverMessage.equals("1")) lock = true;
                    else if (serverMessage.equals("0")) lock = false;
                    else throw new RuntimeException();

                } else {
                    if (serverMessage.equals("win")) {
                        System.out.println("Congratulations, you win!");
                        socket.close();
                    } else if (serverMessage.equals("garb")) {
                        System.out.println("Invalid move.");
                    } else {
                        System.out.println(serverMessage);

                    }
                }

                if (!lock) {
                    System.out.print("Which cell would you like to draw in?\n> ");
                    System.out.println(out.toString());
                    out.writeBytes(userIn.nextLine());
//                    out.flush();
//                    lock = true;
                }
            }

        } catch (Exception e) {
            System.out.println("Client Error: " + e);
        }
    }
}
