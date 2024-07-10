package me.psikuvit.wormparty;

import me.psikuvit.wormparty.entity.Worm;
import me.psikuvit.wormparty.entity.WormMethods;
import me.psikuvit.wormparty.listener.WormListeners;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class WormParty extends JavaPlugin {

    private NamespacedKey key;
    private WormMethods wormMethods;
    private ConfigUtils configUtils;
    private ItemStack wormPetter;

    @Override
    public void onEnable() {
        // Plugin startup logic
        key = new NamespacedKey(this, "worm");
        registerRecipe();
        wormMethods = new WormMethods(this);

        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        configUtils = new ConfigUtils(configFile);

        getServer().getPluginManager().registerEvents(
                new WormListeners(this, wormMethods), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        wormMethods.getWorms().forEach(Worm::killWorm);
        wormMethods.getWorms().clear();
    }

    public NamespacedKey getKey() {
        return key;
    }

    public WormMethods getWormMethods() {
        return wormMethods;
    }

    public ConfigUtils getConfigUtils() {
        return configUtils;
    }



    public void registerRecipe() {
        NamespacedKey recipeKey = new NamespacedKey(this, "worm_mount");

        wormPetter = new ItemStack(Material.STICK);
        ItemMeta itemMeta = wormPetter.getItemMeta();
        itemMeta.setDisplayName(Utils.color("&eWorm Petter"));
        wormPetter.setItemMeta(itemMeta);

        ShapedRecipe recipe = new ShapedRecipe(recipeKey, wormPetter);

        recipe.shape("AAA", "CBC", "AAA");

        recipe.setIngredient('A', Material.GOLD_INGOT);
        recipe.setIngredient('B', Material.STICK);
        recipe.setIngredient('C', Material.DIAMOND);

        getServer().addRecipe(recipe);
    }

    public ItemStack getWormPetter() {
        return wormPetter;
    }
}