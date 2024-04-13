package com.takara2314.playernameuuidtable;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class PlayerNameUUIDTable extends JavaPlugin {
    static String outputCSVFileName = "player_table.csv";

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Generating player table...");

        boolean result = generatePlayerTable();

        if (result) {
            getLogger().info("Generated player table!");
        } else {
            getLogger().info("An error occurred while generating the player table.");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private String convertArrayToCSVStr(String[] data) {
        // { "a", "b", "c" } -> "a,b,c"
        return Stream.of(data).reduce((a, b) -> a + "," + b).orElse("");
    }

    private boolean generatePlayerTable() {
        List<PlayerNameUUID> dataLines = new ArrayList<>();

        for (OfflinePlayer player : getServer().getOfflinePlayers()) {
            dataLines.add(new PlayerNameUUID(
                    player.getName(),
                    player.getUniqueId()
            ));
        }

        try {
            // Prepare data
            String[] header = { "name", "uuid" };
            String[] data = dataLines.stream()
                    .map(p -> convertArrayToCSVStr(new String[] { p.name, p.uuid.toString() }))
                    .toArray(String[]::new);

            // Create data folder if not exists
            if (!getDataFolder().exists()) {
                try {
                    getDataFolder().mkdir();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Write to file
            String filePath = getDataFolder() + File.separator + outputCSVFileName;
            FileWriter writer = new FileWriter(filePath);
            writer.write(convertArrayToCSVStr(header) + "\n");
            for (String line : data) {
                writer.write(line + "\n");
            }
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
