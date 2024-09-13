package ren.lawliet.snake;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Coaixy
 * @createTime 2024-09-13
 * @packageName ren.lawliet.snake
 **/


public class DataSets {
    public static void writeNewData(double[] input, double[] outputs, File file) throws IOException {
        if (!file.exists()) {
            System.out.println(file.createNewFile());
        }
        StringBuilder sb = new StringBuilder();
        for (double i : input) {
            sb.append(i).append(",");
        }
        sb.append("|");
        for (double i : outputs) {
            sb.append(i).append(",");
        }
        sb.append("\n");
        Bukkit.getLogger().info(sb.toString());
        try (FileWriter fw = new FileWriter(file, true)) {
            fw.write(sb.toString());
        }

    }
}
