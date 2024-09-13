package ren.lawliet.snake;

/**
 * @author Coaixy
 * @createTime 2024-09-10
 * @packageName ren.lawliet.snake
 */

public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    public static Direction randomDirection() {
        return values()[(int) (Math.random() * values().length)];
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
