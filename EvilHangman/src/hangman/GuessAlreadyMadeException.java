package hangman;

public class GuessAlreadyMadeException extends Exception {
  public GuessAlreadyMadeException() {

    super("You already used that letter\nInvalid input");
  }
}
