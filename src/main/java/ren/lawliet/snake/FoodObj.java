package ren.lawliet.snake;

import java.util.Random;

/**
 * @author Coaixy
 * @createTime 2024-09-10
 * @packageName ren.lawliet.snake
 **/


public class FoodObj {
    private final PositionObj position;

    public FoodObj(PositionObj position) {
        this.position = position;
    }

    public void generate(int frontierX, int frontierZ) {
        Random random = new Random();
        position.setX(random.nextDouble(frontierX - 1));
        position.setY(random.nextDouble(frontierZ - 1));
    }

    public PositionObj getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "FoodObj{" +
                "position=" + position +
                '}';
    }
}
