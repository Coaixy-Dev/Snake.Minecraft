package ren.lawliet.snake;

/**
 * @author Coaixy
 * @createTime 2024-09-13
 * @packageName ren.lawliet.snake
 **/


import java.util.Objects;

public class GameState {
    private int snakeHeadX;
    private int snakeHeadY;
    private int foodX;
    private int foodY;
    private Direction currentDirection;
    private int distanceToWallUp;
    private int distanceToWallDown;
    private int distanceToWallLeft;
    private int distanceToWallRight;

    public GameState(int snakeHeadX, int snakeHeadY, int foodX, int foodY, Direction currentDirection,
                     int distanceToWallUp, int distanceToWallDown, int distanceToWallLeft, int distanceToWallRight) {
        this.snakeHeadX = snakeHeadX;
        this.snakeHeadY = snakeHeadY;
        this.foodX = foodX;
        this.foodY = foodY;
        this.currentDirection = currentDirection;
        this.distanceToWallUp = distanceToWallUp;
        this.distanceToWallDown = distanceToWallDown;
        this.distanceToWallLeft = distanceToWallLeft;
        this.distanceToWallRight = distanceToWallRight;
    }

    // 需要实现 equals 和 hashCode 方法，以便正确比较和存储状态
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameState gameState = (GameState) o;
        return snakeHeadX == gameState.snakeHeadX && snakeHeadY == gameState.snakeHeadY &&
                foodX == gameState.foodX && foodY == gameState.foodY &&
                currentDirection == gameState.currentDirection &&
                distanceToWallUp == gameState.distanceToWallUp &&
                distanceToWallDown == gameState.distanceToWallDown &&
                distanceToWallLeft == gameState.distanceToWallLeft &&
                distanceToWallRight == gameState.distanceToWallRight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(snakeHeadX, snakeHeadY, foodX, foodY, currentDirection,
                distanceToWallUp, distanceToWallDown, distanceToWallLeft, distanceToWallRight);
    }

    @Override
    public String toString() {
        return "State{" +
                "snakeHeadX=" + snakeHeadX +
                ", snakeHeadY=" + snakeHeadY +
                ", foodX=" + foodX +
                ", foodY=" + foodY +
                ", currentDirection=" + currentDirection +
                ", distanceToWallUp=" + distanceToWallUp +
                ", distanceToWallDown=" + distanceToWallDown +
                ", distanceToWallLeft=" + distanceToWallLeft +
                ", distanceToWallRight=" + distanceToWallRight +
                '}';
    }

    public int getSnakeHeadX() {
        return snakeHeadX;
    }

    public void setSnakeHeadX(int snakeHeadX) {
        this.snakeHeadX = snakeHeadX;
    }

    public int getSnakeHeadY() {
        return snakeHeadY;
    }

    public void setSnakeHeadY(int snakeHeadY) {
        this.snakeHeadY = snakeHeadY;
    }

    public int getFoodX() {
        return foodX;
    }

    public void setFoodX(int foodX) {
        this.foodX = foodX;
    }

    public int getFoodY() {
        return foodY;
    }

    public void setFoodY(int foodY) {
        this.foodY = foodY;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(Direction currentDirection) {
        this.currentDirection = currentDirection;
    }

    public int getDistanceToWallUp() {
        return distanceToWallUp;
    }

    public void setDistanceToWallUp(int distanceToWallUp) {
        this.distanceToWallUp = distanceToWallUp;
    }

    public int getDistanceToWallDown() {
        return distanceToWallDown;
    }

    public void setDistanceToWallDown(int distanceToWallDown) {
        this.distanceToWallDown = distanceToWallDown;
    }

    public int getDistanceToWallLeft() {
        return distanceToWallLeft;
    }

    public void setDistanceToWallLeft(int distanceToWallLeft) {
        this.distanceToWallLeft = distanceToWallLeft;
    }

    public int getDistanceToWallRight() {
        return distanceToWallRight;
    }

    public void setDistanceToWallRight(int distanceToWallRight) {
        this.distanceToWallRight = distanceToWallRight;
    }
}

