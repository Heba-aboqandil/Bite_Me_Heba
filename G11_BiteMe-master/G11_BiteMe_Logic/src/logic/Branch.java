package logic;

public enum Branch {
    NORTH(1),
    SOUTH(2),
    CENTER(3);

    private final int value;

    Branch(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Branch fromValue(int value) {
        for (Branch branch : Branch.values()) {
            if (branch.getValue() == value) {
                return branch;
            }
        }
        throw new IllegalArgumentException("Invalid branch value: " + value);
    }
}

