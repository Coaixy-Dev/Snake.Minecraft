package ren.lawliet.snake;

import java.util.Objects;

/**
 * @author Coaixy
 * @createTime 2024-09-13
 * @packageName ren.lawliet.snake
 **/


public class StateActionPair {
    private GameState gameState;
    private Direction action;

    public StateActionPair(GameState gameState, Direction action) {
        this.gameState = gameState;
        this.action = action;
    }

    // 需要实现 equals 和 hashCode 方法，以便能够正确地将对象作为 HashMap 的键
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StateActionPair that = (StateActionPair) o;
        return gameState.equals(that.gameState) && action == that.action;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameState, action);
    }

    @Override
    public String toString() {
        return "StateActionPair{" +
                "state=" + gameState +
                ", action=" + action +
                '}';
    }
}
