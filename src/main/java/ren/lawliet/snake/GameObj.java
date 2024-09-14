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
    private double platformLineZ = 0.0;
    private File datasetsFile;
    private final QLearningSnake qLearning;
    private GameState currentGameState;
    private int threadId;

    public GameObj(int gameWidth, int gameHeight, Location gamePosition, Player player, int threadId) {
        this.threadId = threadId;
        this.gameWidth = gameWidth - 1;
        this.gameHeight = gameHeight - 1;
        double x = gamePosition.getX() + (double) gameWidth / 2;
        double y = gamePosition.getY() + (double) gameHeight / 2;

        platformLineZ = gamePosition.getZ();
        this.gamePos = new PositionObj(gamePosition.getX(), gamePosition.getY());
        this.snake = new SnakeObj(new PositionObj(x, y));
        this.food = new FoodObj(new PositionObj(gamePosition.getX(), gamePosition.getY()));
        this.player = player;
        snake.setHead(new PositionObj(x, y));
        food.generate(gameWidth, gameHeight, gamePos);

        this.qLearning = new QLearningSnake(0.1, 0.9, 0.8);
        this.currentGameState = getCurrentState();
    }

    private void newGame() {
        snake.getBodyList().clear();
        snake.getBodyList().add(new PositionObj(gamePos.x + (double) gameWidth / 2, gamePos.y + (double) gameHeight / 2));
        snake.setDirection(Direction.RIGHT);
        food.generate(gameWidth, gameHeight, gamePos);
        blockGenerate();
        isOver = false;
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
        snakeHeadX = (int) snake.getHead().x;
        snakeHeadY = (int) snake.getHead().y;
        foodX = (int) (food.position().x);
        foodY = (int) (food.position().y);
        currentDirection = snake.getDirection();
        distanceToWallUp = (int) (gamePos.y + gameHeight - snakeHeadY);
        distanceToWallDown = (int) (snakeHeadY - gamePos.y);
        distanceToWallLeft = (int) (snakeHeadX - gamePos.x);
        distanceToWallRight = (int) (gamePos.x + gameWidth - snakeHeadX);
        return new GameState(snakeHeadX, snakeHeadY, foodX, foodY, currentDirection,
                distanceToWallUp, distanceToWallDown, distanceToWallLeft, distanceToWallRight);
    }

    private void changeDirection(Player player) {
        int amount = player.getInventory().getItemInMainHand().getAmount();
        switch (amount) {
            case 2:
                changeDirection(Direction.DOWN);
                break;
            case 3:
                changeDirection(Direction.LEFT);
                break;
            case 4:
                changeDirection(Direction.RIGHT);
                break;
            default:
                changeDirection(Direction.UP);
                break;
        }
        saveData(datasetsFile);
    }

    private void changeDirection(Direction direction) {
        if (canDirectionChange(direction)) {
            snake.setDirection(direction);
        }
    }

    private boolean canDirectionChange(Direction direction) {
        return switch (snake.getDirection()) {
            case UP -> direction != Direction.DOWN;
            case DOWN -> direction != Direction.UP;
            case LEFT -> direction != Direction.RIGHT;
            case RIGHT -> direction != Direction.LEFT;
        };
    }

    public void start(Snake pluginInstance) {
        datasetsFile = new File(pluginInstance.getDataFolder(), "datasets.txt");
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isOver) {
                    newGame();
                    LearnState.epoch++;
                }
                if (LearnState.epoch % 500 == 0) {
                    qLearning.setExplorationRate(qLearning.getExplorationRate() * 0.9);
                    Bukkit.getServer().broadcastMessage("Learning Epoch - " + LearnState.epoch);
                }
                Player player1 = Bukkit.getPlayer(player.getName());
                if (player1 != null) {
                    if (player1.getInventory().getItemInMainHand().getType() == Material.OAK_LOG) {
                        cancel();
                        return;
                    }
                }
//                changeDirection(player);
                update(player);
            }
        }.runTaskTimer(pluginInstance, 0, 1);
    }

    public void update(Player player) {
        // 选择动作
        Direction action = qLearning.chooseAction(currentGameState);

        // 根据动作改变蛇的方向
        changeDirection(action);

        // 更新蛇的位置
        snake.move();

        // 获取下一个状态
        GameState nextGameState = getCurrentState();
//        Bukkit.getLogger().info(nextGameState.toString());

        // 计算奖励
        double reward = calculateReward(nextGameState);

        // 更新食物和障碍物
        blockGenerate();
        if (snake.isEat(food.position().x, food.position().y)) {
            snake.grow();
            food.generate(gameWidth, gameHeight, gamePos);
        }

        // 检查蛇是否死亡
        if (snake.isDead(gameWidth, gameHeight, this.gamePos)) {
            isOver = true;
        }

        // 在currentState更新为nextState之前更新Q值
        qLearning.updateQValue(currentGameState, action, reward, nextGameState);

        // 将当前状态更新为下一状态
        currentGameState = nextGameState;
    }

    private double calculateReward(GameState nextGameState) {
        if (snake.isEat(food.position().x + gamePos.x, food.position().y + gamePos.y)) {
            return 1000;
        }
        if (snake.isDead(gamePos.x + gameWidth, gamePos.y + gameHeight, this.gamePos)) {
            return -500;
        }
        double lastRelativeDistance = Math.sqrt(
                Math.pow(currentGameState.getSnakeHeadX() - currentGameState.getFoodX(), 2) +
                        Math.pow(currentGameState.getSnakeHeadY() - currentGameState.getFoodY(), 2)
        );
        double nowRelativeDistance = Math.sqrt(
                Math.pow(nextGameState.getSnakeHeadX() - nextGameState.getFoodX(), 2) +
                        Math.pow(nextGameState.getSnakeHeadY() - nextGameState.getFoodY(), 2)
        );
        if (lastRelativeDistance > nowRelativeDistance) {
            return 50;
        } else {
            return -100;
        }
    }

    private void saveData(File file) {
        try {
            double[] inputs = generateInputs();
            double[] outputs = generateOutputs();
            System.out.println(Arrays.toString(inputs));
            System.out.println(Arrays.toString(outputs));
            DataSets.writeNewData(inputs, outputs, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double[] generateOutputs() {
        double[] output = new double[4];
        switch (snake.getDirection()) {
            case UP -> output[0] = 1;
            case DOWN -> output[1] = 1;
            case LEFT -> output[2] = 1;
            case RIGHT -> output[3] = 1;
        }
        return output;
    }

    private double[] generateInputs() {
        double[] input = new double[8];
        switch (snake.getDirection()) {
            case UP -> {
                input[0] = 1;
                input[1] = 0;
                input[2] = 0;
                input[3] = 0;
            }
            case DOWN -> {
                input[0] = 0;
                input[1] = 1;
                input[2] = 0;
                input[3] = 0;
            }
            case LEFT -> {
                input[0] = 0;
                input[1] = 0;
                input[2] = 1;
                input[3] = 0;
            }
            case RIGHT -> {
                input[0] = 0;
                input[1] = 0;
                input[2] = 0;
                input[3] = 1;
            }
        }
        input[4] = snake.getHead().x;
        input[5] = snake.getHead().y;
        input[6] = food.position().x + gamePos.x;
        input[7] = food.position().y + gamePos.y;
        return input;
    }

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
        Location foodLocation = new Location(player.getWorld(),
                food.position().x,
                food.position().y,
                platformLineZ);
        foodLocation.getBlock().setType(org.bukkit.Material.DIAMOND_BLOCK);
    }

}
