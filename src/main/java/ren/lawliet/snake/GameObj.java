package ren.lawliet.snake;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
    private QLearningSnake qLearning;
    private State currentState;
    private long epoch;

    public GameObj(int gameWidth, int gameHeight, Location gamePosition, Player player) {
        this.gameWidth = gameWidth - 1;
        this.gameHeight = gameHeight - 1;
        double x = gamePosition.getX() + (double) gameWidth / 2;
        double y = gamePosition.getY() + (double) gameHeight / 2;

        platformLineZ = gamePosition.getZ();
        Location location = new Location(
                player.getWorld(),
                x,
                y,
                platformLineZ
        );
        this.gamePos = new PositionObj(gamePosition.getX(), gamePosition.getY());
        this.snake = new SnakeObj(new PositionObj(x, y));
        this.food = new FoodObj(new PositionObj(x, y));
        this.player = player;
        snake.setHead(new PositionObj(x, y));
        food.generate(gameWidth, gameHeight);
        player.teleport(location.clone().add(0, 0, -15));

        this.qLearning = new QLearningSnake(0.01, 0.9, 0.5);
        this.currentState = getCurrentState();
        epoch = 1;
    }

    private void newGame() {
        snake.getBodyList().clear();
        snake.getBodyList().add(new PositionObj(gamePos.x + (double) gameWidth / 2, gamePos.y + (double) gameHeight / 2));
        snake.setDirection(Direction.RIGHT);
        food.generate(gameWidth, gameHeight);
        blockGenerate();
        isOver = false;
    }

    private State getCurrentState() {
        int snakeHeadX; // 蛇头X坐标
        int snakeHeadY; // 蛇头Y坐标
        int foodX; // 食物X坐标
        int foodY; // 食物Y坐标
        Direction currentDirection; // 当前方向
        int distanceToWallUp; // 到上方墙壁的距离
        int distanceToWallDown; // 到下方墙壁的距离
        int distanceToWallLeft; // 到左侧墙壁的距离
        int distanceToWallRight; // 到右侧墙壁的距离
        snakeHeadX = (int) snake.getHead().x;
        snakeHeadY = (int) snake.getHead().y;
        foodX = (int) (food.position().x + gamePos.x);
        foodY = (int) (food.position().y + gamePos.y);
        currentDirection = snake.getDirection();
        distanceToWallUp = (int) (gamePos.y + gameHeight - snakeHeadY);
        distanceToWallDown = (int) (snakeHeadY - gamePos.y);
        distanceToWallLeft = (int) (snakeHeadX - gamePos.x);
        distanceToWallRight = (int) (gamePos.x + gameWidth - snakeHeadX);
        State state = new State(snakeHeadX, snakeHeadY, foodX, foodY, currentDirection,
                distanceToWallUp, distanceToWallDown, distanceToWallLeft, distanceToWallRight);
//        System.out.println(state);
        return state;
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
                    Bukkit.getServer().broadcastMessage("Learning finished! - " + epoch + " - " + snake.getBodyList().size());
                    newGame();
                    epoch++;
                }
                if (epoch % 100 == 0){
                    qLearning.setExplorationRate(qLearning.getExplorationRate() * 0.9);
                }
                Player player1 = Bukkit.getOfflinePlayers()[0].getPlayer();
                if (player1 != null) {
                    if (player1.getInventory().getItemInMainHand().getAmount() == 64) {
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
        Direction action = qLearning.chooseAction(currentState);

        // 根据动作改变蛇的方向
        changeDirection(action);

        // 更新蛇的位置
        snake.move();

        // 获取下一个状态
        State nextState = getCurrentState();

        // 计算奖励
        double reward = calculateReward(nextState);

        // 更新食物和障碍物
        blockGenerate();
        if (snake.isEat(food.position().x + gamePos.x, food.position().y + gamePos.y)) {
            snake.grow();
            food.generate(gameWidth, gameHeight);
        }

        // 检查蛇是否死亡
        if (snake.isDead(gamePos.x + gameWidth, gamePos.y + gameHeight, this.gamePos)) {
            isOver = true;
        }

        // 在currentState更新为nextState之前更新Q值
        qLearning.updateQValue(currentState, action, reward, nextState);

        // 将当前状态更新为下一状态
        currentState = nextState;
    }

    private double calculateReward(State nextState) {
        if (snake.isEat(food.position().x + gamePos.x, food.position().y + gamePos.y)) {
            return 1000;
        }
        if (snake.isDead(gamePos.x + gameWidth, gamePos.y + gameHeight, this.gamePos)) {
            return -500;
        }
        double lastRelativeDistance = Math.sqrt(
                Math.pow(currentState.getSnakeHeadX() - currentState.getFoodX() - gamePos.x, 2) +
                        Math.pow(currentState.getSnakeHeadY() - currentState.getFoodY() - gamePos.y, 2)
        );
        double nowRelativeDistance = Math.sqrt(
                Math.pow(nextState.getSnakeHeadX() - nextState.getFoodX() - gamePos.x, 2) +
                        Math.pow(nextState.getSnakeHeadY() - nextState.getFoodY() - gamePos.y, 2)
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
        for (int i = -3; i < gameWidth + 3; i++) {
            for (int j = -3; j < gameHeight + 3; j++) {
                Location location = new Location(player.getWorld(), gamePos.x + i, gamePos.y + j, platformLineZ);
                location.getBlock().setType(Material.POLISHED_DIORITE);
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
                food.position().x + gamePos.x,
                food.position().y + gamePos.y,
                platformLineZ);
        foodLocation.getBlock().setType(org.bukkit.Material.DIAMOND_BLOCK);
    }

}
