package ren.lawliet.snake;

/**
 * @author Coaixy
 * @createTime 2024-09-13
 * @packageName ren.lawliet.snake
 **/


import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class QLearningSnake {

    private Map<String, Double> qTable;
    private double learningRate;
    private double discountFactor;
    private double explorationRate;
    private Random random;

    public QLearningSnake(double learningRate, double discountFactor, double explorationRate) {
        this.qTable = new HashMap<>();
        this.learningRate = learningRate;
        this.discountFactor = discountFactor;
        this.explorationRate = explorationRate;
        this.random = new Random();
    }

    public void setExplorationRate(double explorationRate) {
        this.explorationRate = explorationRate;
    }

    public Direction chooseAction(GameState gameState) {
        if (random.nextDouble() < explorationRate) {
            return Direction.randomDirection();
        } else {
            return getBestAction(gameState);
        }
    }

    public void updateQValue(GameState gameState, Direction action, double reward, GameState nextGameState) {
        StateActionPair saPair = new StateActionPair(gameState, action);
        double oldQValue = qTable.getOrDefault(saPair.toString(), -0.1);
        double bestNextQValue = qTable.getOrDefault((new StateActionPair(nextGameState, getBestAction(nextGameState)).toString()), -0.1);
        double newQValue = oldQValue + learningRate * (reward + discountFactor * bestNextQValue - oldQValue);
//        Bukkit.getLogger().info(saPair.toString());
//        Bukkit.getLogger().info(String.valueOf(reward));
//        Bukkit.getLogger().info("oldQValue: " + oldQValue + " bestNextQValue: " + bestNextQValue + " newQValue: " + newQValue);
        qTable.put(saPair.toString(), newQValue);
    }

    private Direction getBestAction(GameState gameState) {
        Direction bestAction = Direction.UP;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (Direction action : Direction.values()) {
            StateActionPair saPair = new StateActionPair(gameState, action);
            double qValue = qTable.getOrDefault(saPair.toString(), -0.1);
            if (qValue > bestValue) {
                bestValue = qValue;
                bestAction = action;
            }
        }

        return bestAction;
    }

    public double getExplorationRate() {
        return explorationRate;
    }
}
