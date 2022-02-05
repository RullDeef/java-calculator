package calculator.grammar;

public class GrammarException extends Exception {
    public GrammarException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "Invalid expression syntax: " + super.getMessage();
    }
}
