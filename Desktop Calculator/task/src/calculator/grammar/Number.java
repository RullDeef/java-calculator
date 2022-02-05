package calculator.grammar;

import java.math.BigDecimal;

public class Number implements Expression {
    private final BigDecimal value;

    public Number(String strValue) {
        value = new BigDecimal(strValue);
    }

    @Override
    public BigDecimal getResult() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
