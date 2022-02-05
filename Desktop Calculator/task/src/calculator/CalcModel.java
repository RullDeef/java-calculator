package calculator;

import calculator.grammar.GrammarException;
import calculator.grammar.Operation;
import calculator.grammar.Parser;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.function.Consumer;

public class CalcModel {

    public enum InputState {
        GOOD, INVALID
    }

    private Consumer<String> onInputStringChange = s -> {};
    private Consumer<String> onResultStringChange = s -> {};
    private Consumer<InputState> onInputStateChange = s -> {};

    private String inputText = "";

    public void setOnResultStringChange(Consumer<String> onResultStringChange) {
        this.onResultStringChange = onResultStringChange;
    }

    public void setOnInputStringChange(Consumer<String> onInputStringChange) {
        this.onInputStringChange = onInputStringChange;
    }

    public void setOnInputStateChange(Consumer<InputState> onInputStateChange) {
        this.onInputStateChange = onInputStateChange;
    }

    public void insertDigit(int digit) {
        inputText += digit;

        notifyInputStringChanged();
    }

    public void insertDot() {
        inputText += ".";

        notifyInputStringChanged();
    }

    public void insertBrace() {
        int braceCount = inputText.chars().
                map(c -> c == '(' ? 1 : (c == ')' ? -1 : 0)).
                reduce(0, Integer::sum);
        if (braceCount == 0 || inputText.matches(".*\\($") ||
                Operation.all.stream()
                        .anyMatch(op -> op.toString()
                                .equals(inputText.substring(inputText.length() - 1)))) {
            inputText += "(";
        } else {
            inputText += ")";
        }

        notifyInputStringChanged();
    }

    public void insertPlusMinus() {
        if (inputText.isEmpty()) {
            inputText += "(-";
        } else if (inputText.matches(".*\\(-(\\d+(\\.\\d*)?)?$")) {
            inputText = inputText.replaceAll("\\(-((\\d+(\\.\\d*)?)?)$", "$1");
        } else if (inputText.matches(".*\\d+(\\.\\d*)?$")) {
            inputText = inputText.replaceAll("(\\d+(\\.\\d*)?)$", "(-$1");
        } else {
            inputText += "(-";
        }

        notifyInputStringChanged();
    }

    public void delete() {
        if (inputText.length() > 0) {
            inputText = inputText.substring(0, inputText.length() - 1);

            notifyInputStringChanged();
        }
    }

    public void clear() {
        inputText = "";

        notifyInputStringChanged();
    }

    public void clearRecent() {
        System.out.println("not implemented");
    }

    public void makeOp(Operation operation) {
        if (operation.getType() == Operation.Type.SQRT) {
            inputText += operation + "(";

            formatInput();
            notifyInputStringChanged();
        } else if (!inputText.isEmpty()) {
            var len = inputText.length();
            String tail = inputText.substring(len - 1, len);
            if (Operation.all.stream().anyMatch(op ->
                    op.isArithmetic() && tail.equals(op.toString()))) {
                inputText = inputText.substring(0, len - 1);
            }

            inputText += operation;
            formatInput();
            notifyInputStringChanged();
        }
    }

    public void calculate() {
        try {
            formatInput();
            notifyInputStringChanged();

            String resultText = new DecimalFormat("0.#")
                    .format(new Parser().parse(inputText).getResult());
            onResultStringChange.accept(resultText);

            onInputStateChange.accept(InputState.GOOD);
        } catch (ArithmeticException | NumberFormatException | GrammarException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();

            onInputStateChange.accept(InputState.INVALID);
        }
    }

    private void formatInput() {
        inputText = inputText.replaceAll("^\\.", "0.");
        inputText = inputText.replaceAll("([^0-9])\\.", "$10.");
        inputText = inputText.replaceAll("\\.([^0-9])", ".0$1");
    }

    private void notifyInputStringChanged() {
        onInputStringChange.accept(inputText);
    }
}
