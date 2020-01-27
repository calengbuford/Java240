package hangman;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner;
import java.util.Iterator;

@SuppressWarnings("CheckStyle")
public class EvilHangman {

    public static void main(String[] args) {
        // Check that 3 inputs were given
        if (args.length < 3) {
            System.out.println("USAGE ERROR: Not enough arguments given");
            return;
        }

        // Set inputs to variables
        String dictionaryFileName = args[0];
        int wordLength = 0;
        int numGuesses = 0;

        // checking valid integer using parseInt()
        try {
            wordLength = Integer.parseInt(args[1]);
            numGuesses = Integer.parseInt(args[2]);
            // Test integer input sizes
            if (wordLength < 2 || numGuesses < 1) {
                System.out.println("USAGE ERROR: Not valid integer input");
                return;
            }
        }
        catch (NumberFormatException e) {
            System.out.println("USAGE ERROR: Not valid integer input");
            return;
        }

        File dictFile = null;
        // Read and begin scanning file
        try {
            dictFile = new File(dictionaryFileName);
        }
        catch (Exception e) {
            System.out.println(e);
            return;
        }


        // Instantiate a hangman game and start game
        EvilHangmanGame game = new EvilHangmanGame();
        try {
            // Start the game by building dictionary with words of length wordLength
            game.startGame(dictFile, wordLength);

            Scanner userInput = new Scanner(System.in);
            String curInput = null;

            // Receive guess inputs from user
            while (numGuesses > 0) {
                // Print user and game information
                System.out.printf("You have %d guesses left\n", numGuesses);
                System.out.print("Used letters:");

                // Iterate over guessedLetters TreeSet
                for (char letter : game.getGuessedLetters()) {
                    System.out.printf(" %c", letter);
                }

                System.out.printf("\nWord: %s", game.curAnswer);
                System.out.print("\nEnter a guess: ");
                curInput = userInput.next();   // Get guess input from user

                // Verify input is valid
                while (!IsValidInput(curInput)) {
                    System.out.println("Invalid input");
                    System.out.print("Enter a guess: ");
                    curInput = userInput.next();   // Get guess input from user
                }
                curInput = curInput.toLowerCase();
                char charGuess = curInput.charAt(0);


                // Make guesses while guess is not new
                boolean guessRequired = true;
                while (guessRequired) {
                    try {
                        // If an occurrence appears, they guessed correctly
                        int numOccurrences = game.guessLetter(charGuess);
                        if (numOccurrences > 0) {
                            System.out.printf("Yes, there is %d %c\n\n", numOccurrences, charGuess);

                            // If all spaces of curAnswer have been filled, they win
                            if (game.checkIfWon()) {
                                System.out.printf("You win!\nThe word was: %s\n", game.curAnswer);
                                userInput.close();
                                return;
                            }
                        }
                        else {
                            System.out.printf("Sorry, there are no %c's\n\n", charGuess);
                            numGuesses--;   // Decrement the number of guesses remaining
                        }

                        guessRequired = false;
                    }
                    catch (Exception e) {
                        System.out.println(e);
                        System.out.print("Enter a guess: ");
                        curInput = userInput.next();   // Get guess input from user

                        // Verify input is valid
                        while (!IsValidInput(curInput)) {
                            System.out.println("Invalid input");
                            System.out.print("Enter a guess: ");
                            curInput = userInput.next();   // Get guess input from user
                        }
                        curInput = curInput.toLowerCase();
                        charGuess = curInput.charAt(0);
                    }
                }
            }


            // Ran out of guesses
            System.out.printf("You lose!\nThe word was: %s\n", game.getLosingAnswer());
            userInput.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public static Boolean IsValidInput(String userInput) {
        // Check that the input is length 1
        if (userInput.length() > 1) {
            return false;
        }
        // Check that the input is a letter
        return Character.isLetter(userInput.charAt(0));
    }

}
