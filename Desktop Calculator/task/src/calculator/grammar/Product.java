package calculator.grammar;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class Product implements Expression {
    private final List<Expression> terms;

    public Product(Expression... terms) {
        this.terms = List.of(terms);
    }

    @Override
    public BigDecimal getResult() {
        return terms.stream()
                .map(Expression::getResult)
                .reduce(new BigDecimal(1), BigDecimal::multiply);
    }

    @Override
    public String toString() {
        return terms.stream()
                .map(Expression::toString)
                .collect(Collectors.joining(",", "MUL(", ")"));
    }
}
