package ren.lawliet.snake;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
    }

    private void changeDirection() {
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
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isOver) {
                    player.sendMessage("Game Over!");
                    cancel();
                    return;
                }
                changeDirection();
                update(player);
            }
        }.runTaskTimer(pluginInstance, 0, 6);
    }

    public void update(Player player) {
        snake.move();
        blockGenerate();
        if (snake.isEat(food.position().x + gamePos.x, food.position().y + gamePos.y)) {
            snake.grow();
            food.generate(gameWidth, gameHeight);
            player.sendMessage("Score: " + snake.getBodyList().size());
        }
        if (snake.isDead(gamePos.x + gameWidth, gamePos.y + gameHeight, this.gamePos)) {
            isOver = true;
        }
    }

    private void blockGenerate() {
        for (int i = 0; i < gameWidth; i++) {
            for (int j = 0; j < gameHeight; j++) {
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
