package ren.lawliet.snake;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Snake extends JavaPlugin {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (command.getName().equalsIgnoreCase("snake")) {
                int size = Integer.parseInt(args[0]);
                Location location = player.getLocation().clone().add(0, 0, -3);
                GameObj game = new GameObj(size, size, location, player);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        game.start(Snake.this);
                    }
                }.runTaskLater(this, 20);
            }
            return true;
        } else {
            sender.sendMessage("You must be a player!");
            return false;
        }
    }
}
