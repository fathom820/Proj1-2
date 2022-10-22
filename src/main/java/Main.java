import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    private static volatile boolean serverStarted = false;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String currentInput = "";

//        System.out.print("Welcome to the game. Would you like to host a new game, or connect to an existing one?\n> ");

        while (!currentInput.equals("host")  && !currentInput.equals("join")) {
            System.out.print("Welcome to the game. Would you like to host a new game, or join an existing one?\n(Type \"host\" or \"join\")\n> ");
            currentInput = input.nextLine();
        }

        if (currentInput.equals("host")) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("google.com", 80));
                String hostIP =  socket.getLocalAddress().getHostAddress();
                System.out.println("Your IP address is " + hostIP);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            (new Server()).start();

            // don't start client until server is started
            while (!serverStarted) Thread.onSpinWait();

            (new Client(false)).start();
            (new Client(false)).start();


        } else {
            (new Client(true)).start();
        }
    }

    public static void setServerStarted(boolean serverStarted) {
        Main.serverStarted = serverStarted;
    }
}
