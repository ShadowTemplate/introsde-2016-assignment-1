package health;

import javax.xml.xpath.XPathExpressionException;

public enum Operator {
    EQUAL,
    GREATER,
    LOWER;

    public String symbol() throws XPathExpressionException {
        switch (this) {
            case EQUAL:
                return "=";
            case GREATER:
                return ">";
            case LOWER:
                return "<";
            default:
                throw new XPathExpressionException("Invalid operator: " + this.toString());
        }
    }
}
