import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String currentInput = "";

//        System.out.print("Welcome to the game. Would you like to host a new game, or connect to an existing one?\n> ");

        while (!currentInput.equals("host")  && !currentInput.equals("join")) {
            System.out.print("Welcome to the game. Would you like to host a new game, or join to an existing one?\n> ");
            currentInput = input.nextLine();
        }

        if (currentInput.equals("host")) {

        } else {
            (new Client()).start();
        }
    }
}
