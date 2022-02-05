package calculator.grammar;

import java.math.BigDecimal;

public class Negation implements Expression {
    private final Expression value;

    public Negation(Expression value) {
        this.value = value;
    }

    @Override
    public BigDecimal getResult() {
        return value.getResult().negate();
    }

    @Override
    public String toString() {
        return "NEG(" + value.toString() + ")";
    }
}
