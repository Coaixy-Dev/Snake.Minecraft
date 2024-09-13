package ren.lawliet.snake;

import java.util.Random;

/**
 * @author Coaixy
 * @createTime 2024-09-10
 * @packageName ren.lawliet.snake
 **/


public record FoodObj(PositionObj position) {

    public void generate(int gameWeight, int gameHeight, PositionObj gamePosition) {
        Random random = new Random();
        position.x = random.nextInt(gameWeight) + (int) gamePosition.x;
        position.y = random.nextInt(gameHeight) + (int) gamePosition.y;
    }

    @Override
    public String toString() {
        return "FoodObj{" +
                "position=" + position +
                '}';
    }
}
