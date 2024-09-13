package ren.lawliet.snake;

/**
 * @author Coaixy
 * @createTime 2024-09-13
 * @packageName ren.lawliet.snake
 **/


import java.util.Objects;

public class State {

    private double snakeHeadX; // 蛇头X坐标
    private double snakeHeadY; // 蛇头Y坐标
    private double foodX; // 食物X坐标
    private double foodY; // 食物Y坐标
    private Direction currentDirection; // 当前方向
    private double distanceToWallUp; // 到上方墙壁的距离
    private double distanceToWallDown; // 到下方墙壁的距离
    private double distanceToWallLeft; // 到左侧墙壁的距离
    private double distanceToWallRight; // 到右侧墙壁的距离

    public State(double snakeHeadX, double snakeHeadY, double foodX, double foodY, Direction currentDirection,
                 double distanceToWallUp, double distanceToWallDown, double distanceToWallLeft, double distanceToWallRight) {
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
        State state = (State) o;
        return snakeHeadX == state.snakeHeadX && snakeHeadY == state.snakeHeadY &&
                foodX == state.foodX && foodY == state.foodY &&
                currentDirection == state.currentDirection &&
                distanceToWallUp == state.distanceToWallUp &&
                distanceToWallDown == state.distanceToWallDown &&
                distanceToWallLeft == state.distanceToWallLeft &&
                distanceToWallRight == state.distanceToWallRight;
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

    public double getSnakeHeadX() {
        return snakeHeadX;
    }

    public void setSnakeHeadX(double snakeHeadX) {
        this.snakeHeadX = snakeHeadX;
    }

    public double getSnakeHeadY() {
        return snakeHeadY;
    }

    public void setSnakeHeadY(double snakeHeadY) {
        this.snakeHeadY = snakeHeadY;
    }

    public double getFoodX() {
        return foodX;
    }

    public void setFoodX(double foodX) {
        this.foodX = foodX;
    }

    public double getFoodY() {
        return foodY;
    }

    public void setFoodY(double foodY) {
        this.foodY = foodY;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(Direction currentDirection) {
        this.currentDirection = currentDirection;
    }

    public double getDistanceToWallUp() {
        return distanceToWallUp;
    }

    public void setDistanceToWallUp(double distanceToWallUp) {
        this.distanceToWallUp = distanceToWallUp;
    }

    public double getDistanceToWallDown() {
        return distanceToWallDown;
    }

    public void setDistanceToWallDown(double distanceToWallDown) {
        this.distanceToWallDown = distanceToWallDown;
    }

    public double getDistanceToWallLeft() {
        return distanceToWallLeft;
    }

    public void setDistanceToWallLeft(double distanceToWallLeft) {
        this.distanceToWallLeft = distanceToWallLeft;
    }

    public double getDistanceToWallRight() {
        return distanceToWallRight;
    }

    public void setDistanceToWallRight(double distanceToWallRight) {
        this.distanceToWallRight = distanceToWallRight;
    }

    // Getters and setters 可以根据需要添加
}

