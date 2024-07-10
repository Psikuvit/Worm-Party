package me.psikuvit.wormparty;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigUtils {

    private final FileConfiguration config;

    public ConfigUtils(File configFile) {
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public String headTexture() {
        return config.getString("worm-head-texture");
    }

    public String segmentTexture() {
        return config.getString("worm-segment-texture");
    }

}
