/**
 * @author Michael Frank
 */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    // used to keep client from starting before server
    private static volatile boolean serverStarted = false;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String currentInput = "";

        // user must enter one of these two commands
        while (!currentInput.equals("host")  && !currentInput.equals("join")) {
            System.out.print("Welcome to the game. Would you like to host a new game, or join an existing one?\n(Type \"host\" or \"join\")\n> ");
            currentInput = input.nextLine();
        }

        // host mode
        if (currentInput.equals("host")) {
            // used to get the user's current IP address, it's a bit broken though.
            // there is definitely a better way of doing this, but i have no idea what it is.
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("google.com", 80));
                String hostIP =  socket.getLocalAddress().getHostAddress();
                System.out.println("Your IP address is " + hostIP);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // start server thread
            (new Server()).start();

            // don't start client until server is started
            while (!serverStarted) Thread.onSpinWait();

            // start 2 clients for testing purposes
            // in a real scenario, you would only have 1 client
            // be started locally.
            (new Client(false)).start();
            (new Client(false)).start();

        } else {
            // create single client in join mode that
            // requires the user to manually enter the IP
            // instead of just localhost
            (new Client(true)).start();
        }
    }

    public static void setServerStarted(boolean serverStarted) {
        Main.serverStarted = serverStarted;
    }
}
