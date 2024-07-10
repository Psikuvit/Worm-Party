package me.psikuvit.wormparty;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Utils {

    public static ItemStack getCustomSkull(String url) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        if (url.isEmpty()) return skull;

        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", url));
        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public static float interpolateYaw(float currentYaw, float targetYaw, float factor) {
        float delta = targetYaw - currentYaw;
        if (delta > 180) delta -= 360;
        if (delta < -180) delta += 360;
        return currentYaw + delta * factor;
    }

    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String secToMin(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%d:%02d", minutes, remainingSeconds);
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(
                color(title),
                color(subtitle),
                fadeIn,
                stay,
                fadeOut
        );
    }

    public static List<ItemStack> rewards() {
        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
        ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        ItemStack boots = new ItemStack(Material.DIAMOND_HELMET);
        List<ItemStack> armors = Arrays.asList(helmet, chestplate, leggings, boots);
        armors.forEach(armor -> {
            ItemMeta itemMeta = armor.getItemMeta();
            itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, true);
            itemMeta.addEnchant(Enchantment.DURABILITY, 5, true);
            armor.setItemMeta(itemMeta);

        });
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
        sword.setItemMeta(swordMeta);

        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = sword.getItemMeta();
        bowMeta.addEnchant(Enchantment.ARROW_DAMAGE, 5, true);
        bow.setItemMeta(bowMeta);

        ItemStack apples = new ItemStack(Material.GOLDEN_APPLE);
        ItemStack enderpearls = new ItemStack(Material.ENDER_PEARL, 16);

        List<ItemStack> rewards = new ArrayList<>(armors);
        rewards.add(sword);
        rewards.add(bow);
        rewards.add(apples);
        rewards.add(enderpearls);

        return rewards;

    }

    public static ItemStack randomReward() {
        Random rnd = new Random();
        int i = rnd.nextInt(rewards().size());
        return rewards().get(i);
    }
}
