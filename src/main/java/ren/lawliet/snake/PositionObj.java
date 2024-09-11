package ren.lawliet.snake;

/**
 * @author Coaixy
 * @createTime 2024-09-10
 * @packageName ren.lawliet.snake
 */

public class PositionObj {
    public double x;
    public double y;

    public PositionObj(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setX(double newX) {
        this.x = newX;
    }

    public void setY(double newY) {
        this.y = newY;
    }

    @Override
    public String toString() {
        return "PositionObj{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
