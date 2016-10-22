package health;

public enum Operator {
    EQUAL,
    GREATER,
    LOWER;

    public String symbol() throws RuntimeException {
        switch (this) {
            case EQUAL:
                return "=";
            case GREATER:
                return ">";
            case LOWER:
                return "<";
            default:
                throw new RuntimeException("Invalid operator: " + this.toString());
        }
    }
}
