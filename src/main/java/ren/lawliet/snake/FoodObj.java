package ren.lawliet.snake;

import java.util.Random;

/**
 * @author Coaixy
 * @createTime 2024-09-10
 * @packageName ren.lawliet.snake
 **/


public record FoodObj(PositionObj position) {

    public void generate(int frontierX, int frontierZ) {
        Random random = new Random();
        position.setX(random.nextDouble(frontierX));
        position.setY(random.nextDouble(frontierZ));
    }

    @Override
    public String toString() {
        return "FoodObj{" +
                "position=" + position +
                '}';
    }
}
