package calculator.grammar;

import java.math.BigDecimal;

public interface Expression {
    BigDecimal getResult();

    String toString();
}
