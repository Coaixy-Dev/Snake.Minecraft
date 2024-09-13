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
                int thread = Integer.parseInt(args[1]);
                Location location = player.getTargetBlock(null, 10).getLocation();
                player.teleport(location.clone().add(0, 0, -10));
                for (int i = 1; i < thread + 1; i++) {
                    GameObj game = new GameObj(size, size, location.clone().add(0, 0, (i-1) * 2), player, thread);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            game.start(Snake.this);
                        }
                    }.runTaskLater(this, i);
                }
            }
            return true;
        } else {
            sender.sendMessage("You must be a player!");
            return false;
        }
    }
}
