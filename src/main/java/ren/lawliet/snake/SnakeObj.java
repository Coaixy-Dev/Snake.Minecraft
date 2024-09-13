package ren.lawliet.snake;

import org.bukkit.Bukkit;

import java.util.ArrayList;

/**
 * @author Coaixy
 * @createTime 2024-09-10
 * @packageName ren.lawliet.snake
 */

public class SnakeObj {
    private final ArrayList<PositionObj> bodyList = new ArrayList<>();
    private Direction direction;

    public SnakeObj(PositionObj headPosition) {
        this.direction = Direction.LEFT;
        headPosition.setX(headPosition.x);
        headPosition.setY(headPosition.y);

        this.bodyList.add(0, headPosition);
    }

    public void setHead(PositionObj headPosition) {
        this.bodyList.set(0, headPosition);
    }

    public ArrayList<PositionObj> getBodyList() {
        return bodyList;
    }

    public void move() {
        PositionObj head = bodyList.get(0);
        PositionObj newHead = new PositionObj(head.x, head.y);
        switch (direction) {
            case UP:
                newHead.y++;
                break;
            case DOWN:
                newHead.y--;
                break;
            case LEFT:
                newHead.x++;
                break;
            case RIGHT:
                newHead.x--;
                break;
        }
        bodyList.add(0, newHead);
        bodyList.remove(bodyList.size() - 1);
    }

    public boolean isEat(double foodX, double foodY) {
        PositionObj head = bodyList.get(0);
        return (int) head.x == (int) foodX && (int) head.y == (int) foodY;
    }

    public void grow() {
        PositionObj tail = bodyList.get(bodyList.size() - 1);
        PositionObj newTail = new PositionObj(tail.x, tail.y);
        switch (direction) {
            case UP:
                newTail.y--;
                break;
            case DOWN:
                newTail.y++;
                break;
            case LEFT:
                newTail.x--;
                break;
            case RIGHT:
                newTail.x++;
                break;
        }
        bodyList.add(newTail);
    }

    public boolean isDead(double gameWidth, double gameHeight, PositionObj gamePos) {
        PositionObj head = bodyList.get(0);
        if (head.x > gameWidth + 1 || head.y > gameHeight + 1 || head.x < gamePos.x || head.y < gamePos.y) {
            return true;
        }
        for (int i = 1; i < bodyList.size(); i++) {
            PositionObj body = bodyList.get(i);
            if (head.x == body.x && head.y == body.y) {
                return true;
            }
        }
        return false;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "SnakeObj{" +
                "bodyList=" + bodyList +
                ", direction=" + direction +
                '}';
    }

    public PositionObj getHead() {
        return bodyList.get(0);
    }
}
