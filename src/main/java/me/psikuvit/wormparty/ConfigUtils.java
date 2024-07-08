package me.psikuvit.wormparty;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigUtils {

    private final FileConfiguration config;

    public ConfigUtils(File configFile) {
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public Location waitingLoc() {
        World world = Bukkit.getWorld(config.getString("waitingLoc.world"));
        double x = config.getDouble("waitingLoc.x");
        double y = config.getDouble("waitingLoc.y");
        double z = config.getDouble("waitingLoc.z");
        return new Location(world, x, y, z);
    }

    public Location mapLoc() {
        World world = Bukkit.getWorld(config.getString("mapLoc.world"));
        double x = config.getDouble("mapLoc.x");
        double y = config.getDouble("mapLoc.y");
        double z = config.getDouble("mapLoc.z");
        return new Location(world, x, y, z);
    }

    public int timeTillPvP() {
        return config.getInt("timeTillPvP");
    }

    public int minToStart() {
        return config.getInt("minimumToStart");
    }

    public String headTexture() {
        return config.getString("worm-head-texture");
    }

    public String segmentTexture() {
        return config.getString("worm-segment-texture");
    }

}
