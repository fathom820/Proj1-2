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
                if  (serverMessage.equals("1")) {
                    lock = true;
                }
                if (serverMessage.equals("0")) {
                    lock = false;
                }

                if  (lock && !serverMessage.equals("1")) {
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
                            // last line of game board
                            if (serverMessage.equals("")) {
                                out.writeBytes("1\n");
                                System.out.print("Which cell would you like to draw in?\n> ");
                                out.writeBytes(userIn.nextLine() + "\n");
                                lock = false;
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
