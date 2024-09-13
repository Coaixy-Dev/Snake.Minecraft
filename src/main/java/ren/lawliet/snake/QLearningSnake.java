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

    private Map<StateActionPair, Double> qTable;
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

    public Direction chooseAction(State state) {
        if (random.nextDouble() < explorationRate) {
            return Direction.randomDirection();
        } else {
            return getBestAction(state);
        }
    }

    public void updateQValue(State state, Direction action, double reward, State nextState) {
        StateActionPair saPair = new StateActionPair(state, action);
        double oldQValue = qTable.getOrDefault(saPair, 0.0);
        double bestNextQValue = qTable.getOrDefault(new StateActionPair(nextState, getBestAction(nextState)), 0.0);
        double newQValue = oldQValue + learningRate * (reward + discountFactor * bestNextQValue - oldQValue);
        qTable.put(saPair, newQValue);
    }

    private Direction getBestAction(State state) {
        Direction bestAction = Direction.UP;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (Direction action : Direction.values()) {
            StateActionPair saPair = new StateActionPair(state, action);
            double qValue = qTable.getOrDefault(saPair, 0.0);
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
