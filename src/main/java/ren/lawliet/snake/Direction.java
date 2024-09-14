package ren.lawliet.snake;

/**
 * @author Coaixy
 * @createTime 2024-09-10
 * @packageName ren.lawliet.snake
 */

public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    public static Direction randomDirection(Direction nowDirection) {
        Direction direction = nowDirection;
        while (direction == nowDirection || direction == getCanonicalDirection(nowDirection)) {
            int index = (int) (Math.random() * 4);
            direction = switch (index) {
                case 0 -> Direction.UP;
                case 1 -> Direction.DOWN;
                case 2 -> Direction.LEFT;
                case 3 -> Direction.RIGHT;
                default -> throw new IllegalStateException("Unexpected value: " + index);
            };
        }
        return direction;
    }

    public static Direction getCanonicalDirection(Direction direction) {
        return switch (direction) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case UP -> "UP";
            case DOWN -> "DOWN";
            case LEFT -> "LEFT";
            case RIGHT -> "RIGHT";
        };
    }
}
