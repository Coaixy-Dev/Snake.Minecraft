package ren.lawliet.snake;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Arrays;

/**
 * @author Coaixy
 * @createTime 2024-09-10
 * @packageName ren.lawliet.snake
 */


public class GameObj {
    private final int gameWidth;
    private final int gameHeight;
    private final SnakeObj snake;
    private final PositionObj gamePos;
    private final FoodObj food;
    private boolean isOver = false;
    private final Player player;
    private int platformLineZ = 0;
    private File datasetsFile;
    private GameState currentGameState;
    private final int threadId;
    private QLearningSnake qLearning;
    private long timeOut = 0;

    public GameObj(int gameWidth, int gameHeight, Location gamePosition, Player player, int threadId, QLearningSnake qLearning) {
        this.threadId = threadId;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        int x = (int) (gamePosition.getX() + (double) gameWidth / 2);
        int y = (int) (gamePosition.getY() + (double) gameHeight / 2);

        platformLineZ = (int) gamePosition.getZ();
        this.gamePos = new PositionObj(gamePosition.getX(), gamePosition.getY());
        this.snake = new SnakeObj(new PositionObj(x, y));
        this.food = new FoodObj(new PositionObj(x, y));
        food.generate(gameWidth, gameHeight, gamePos);
        this.player = player;

        this.qLearning = qLearning;
        this.currentGameState = getCurrentState();
    }

    private void newGame() {
        snake.getBodyList().clear();
        snake.getBodyList().add(new PositionObj(gamePos.x + (double) gameWidth / 2, gamePos.y + (double) gameHeight / 2));
        snake.setDirection(Direction.UP);
        blockGenerate();
        isOver = false;
        timeOut = 0;
    }

    private GameState getCurrentState() {
        int snakeHeadX;
        int snakeHeadY;
        int foodX;
        int foodY;
        Direction currentDirection;
        int distanceToWallUp;
        int distanceToWallDown;
        int distanceToWallLeft;
        int distanceToWallRight;
        snakeHeadX = snake.getHead().x;
        snakeHeadY = snake.getHead().y;
        foodX = food.position().x;
        foodY = food.position().y;
        currentDirection = snake.getDirection();
        distanceToWallUp = gamePos.y + gameHeight - snakeHeadY;
        distanceToWallDown = snakeHeadY - gamePos.y;
        distanceToWallLeft = snakeHeadX - gamePos.x;
        distanceToWallRight = gamePos.x + gameWidth - snakeHeadX;
        return new GameState(snakeHeadX, snakeHeadY, foodX, foodY, currentDirection, distanceToWallUp, distanceToWallDown, distanceToWallLeft, distanceToWallRight);
    }

//    private void changeDirection(Player player) {
//        int amount = player.getInventory().getItemInMainHand().getAmount();
//        switch (amount) {
//            case 2:
//                changeDirection(Direction.DOWN);
//                break;
//            case 3:
//                changeDirection(Direction.LEFT);
//                break;
//            case 4:
//                changeDirection(Direction.RIGHT);
//                break;
//            default:
//                changeDirection(Direction.UP);
//                break;
//        }
//        saveData(datasetsFile);
//    }

    private void changeDirection(Direction direction) {
        if (Direction.getCanonicalDirection(direction) != snake.getDirection()) {
            snake.setDirection(direction);
        }
    }

    public void start(Snake pluginInstance) {
        datasetsFile = new File(pluginInstance.getDataFolder(), "datasets.txt");
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isOver) {
                    newGame();
                    qLearning.epoch++;
                }
                if (qLearning.epoch % 1000 == 0) {
                    qLearning.setExplorationRate(qLearning.getExplorationRate() * 0.9);
                }
                if (qLearning.epoch >= 300000) {
                    cancel();
                    return;
                }
                if (timeOut >= 1000) {
                    isOver = true;
                }
                Player player1 = Bukkit.getPlayer(player.getName());
                if (player1 != null) {
                    if (player1.getInventory().getItemInMainHand().getType() == Material.OAK_LOG) {
                        cancel();
                        return;
                    }
                }
                update();
            }
        }.runTaskTimer(pluginInstance, 0, 3);
    }

    public void update() {
        Direction action = qLearning.chooseAction(currentGameState);
        changeDirection(action);
        snake.move();
        GameState nextGameState = getCurrentState();
        double reward = calculateReward(nextGameState);

        blockGenerate();
        if (snake.isEat(food.position().x, food.position().y)) {
            snake.grow();
            food.generate(gameWidth, gameHeight, gamePos);
        }
        if (snake.isDead(gameWidth, gameHeight, this.gamePos)) {
            isOver = true;
        }
        qLearning.updateQValue(currentGameState, action, reward, nextGameState);
        currentGameState = nextGameState;
    }

    private double calculateReward(GameState nextGameState) {
        timeOut++;
        if (snake.isEat(food.position().x, food.position().y)) {
            return 1000;
        }
        if (snake.isDead(gameWidth, gameHeight, this.gamePos)) {
            return -500;
        }
        double lastRelativeDistance = Math.sqrt(Math.pow(currentGameState.getSnakeHeadX() - currentGameState.getFoodX(), 2) + Math.pow(currentGameState.getSnakeHeadY() - currentGameState.getFoodY(), 2));
        double nowRelativeDistance = Math.sqrt(Math.pow(nextGameState.getSnakeHeadX() - nextGameState.getFoodX(), 2) + Math.pow(nextGameState.getSnakeHeadY() - nextGameState.getFoodY(), 2));
        if (lastRelativeDistance > nowRelativeDistance) {
            return 1;
        } else {
            return -10;
        }
    }

//    private void saveData(File file) {
//        try {
//            double[] inputs = generateInputs();
//            double[] outputs = generateOutputs();
//            System.out.println(Arrays.toString(inputs));
//            System.out.println(Arrays.toString(outputs));
//            DataSets.writeNewData(inputs, outputs, file);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    private double[] generateOutputs() {
//        double[] output = new double[4];
//        switch (snake.getDirection()) {
//            case UP -> output[0] = 1;
//            case DOWN -> output[1] = 1;
//            case LEFT -> output[2] = 1;
//            case RIGHT -> output[3] = 1;
//        }
//        return output;
//    }
//
//    private double[] generateInputs() {
//        double[] input = new double[8];
//        switch (snake.getDirection()) {
//            case UP -> {
//                input[0] = 1;
//                input[1] = 0;
//                input[2] = 0;
//                input[3] = 0;
//            }
//            case DOWN -> {
//                input[0] = 0;
//                input[1] = 1;
//                input[2] = 0;
//                input[3] = 0;
//            }
//            case LEFT -> {
//                input[0] = 0;
//                input[1] = 0;
//                input[2] = 1;
//                input[3] = 0;
//            }
//            case RIGHT -> {
//                input[0] = 0;
//                input[1] = 0;
//                input[2] = 0;
//                input[3] = 1;
//            }
//        }
//        input[4] = snake.getHead().x;
//        input[5] = snake.getHead().y;
//        input[6] = food.position().x + gamePos.x;
//        input[7] = food.position().y + gamePos.y;
//        return input;
//    }

    private void blockGenerate() {
        for (int i = -1; i < gameWidth + 1; i++) {
            for (int j = -1; j < gameHeight + 1; j++) {
                Location location = new Location(player.getWorld(), gamePos.x + i, gamePos.y + j, platformLineZ);
                if (i == -1 || i == gameWidth || j == -1 || j == gameHeight) {
                    location.getBlock().setType(Material.AIR);
                } else {
                    location.getBlock().setType(Material.POLISHED_ANDESITE);
                }
            }
        }

        for (PositionObj positionObj : this.snake.getBodyList()) {
            Location location = new Location(player.getWorld(), positionObj.x, positionObj.y, platformLineZ);
            if (positionObj.equals(snake.getHead())) {
                location.getBlock().setType(Material.EMERALD_BLOCK);
            } else {
                location.getBlock().setType(Material.GOLD_BLOCK);
            }
        }
        Location foodLocation = new Location(player.getWorld(), food.position().x, food.position().y, platformLineZ);
        foodLocation.getBlock().setType(org.bukkit.Material.DIAMOND_BLOCK);
    }

}
