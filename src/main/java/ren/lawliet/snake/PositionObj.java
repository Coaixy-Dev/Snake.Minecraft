package ren.lawliet.snake;

/**
 * @author Coaixy
 * @createTime 2024-09-10
 * @packageName ren.lawliet.snake
 */

public class PositionObj {
    public int x;
    public int y;

    public PositionObj(double x, double y) {
        this.x = (int) x;
        this.y = (int) y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "PositionObj{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
