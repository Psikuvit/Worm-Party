package me.psikuvit.wormparty.listener;

import me.psikuvit.wormparty.entity.WormMethods;
import me.psikuvit.wormparty.WormParty;
import me.psikuvit.wormparty.entity.Worm;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class WormListeners implements Listener {

    private final WormMethods wormMethods;
    private final WormParty plugin;


    public WormListeners(WormParty plugin, WormMethods wormMethods) {
        this.plugin = plugin;
        this.wormMethods = wormMethods;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (event.getDamager().getType() != EntityType.PLAYER) return;

        if (event.getEntity().getType() == EntityType.PIG || event.getEntity().getType() == EntityType.ARMOR_STAND) {

            PersistentDataContainer pdc = entity.getPersistentDataContainer();
            if (!pdc.has(plugin.getKey(), PersistentDataType.STRING)) return;

            String uuid = pdc.get(plugin.getKey(), PersistentDataType.STRING);
            if (uuid == null) return;

            UUID wormID = UUID.fromString(uuid);
            Worm worm = wormMethods.getWorm(wormID);

            double currentHP = wormMethods.getWormHP(worm) - event.getDamage();
            event.setDamage(0);

            if (currentHP <= 0) {
                wormMethods.killWorm(worm);
                wormMethods.getWorms().remove(worm);
            } else {
                wormMethods.updateHP(worm, currentHP);
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() == EntityType.PIG) {
            PersistentDataContainer pdc = entity.getPersistentDataContainer();
            if (!pdc.has(plugin.getKey(), PersistentDataType.STRING)) return;

            String uuid = pdc.get(plugin.getKey(), PersistentDataType.STRING);
            if (uuid == null) return;

            UUID wormID = UUID.fromString(uuid);
            Worm worm = wormMethods.getWorm(wormID);

            worm.killWorm();
            wormMethods.getWorms().remove(worm);

        }
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (entity instanceof ArmorStand || entity instanceof Pig) {
            event.setCancelled(true);

            if (!event.getPlayer().getInventory().getItemInMainHand().equals(plugin.getWormPetter())) {
                return;
            }

            EntityPlayer entityPlayer = ((CraftPlayer) event.getPlayer()).getHandle();
            PersistentDataContainer pdc = entity.getPersistentDataContainer();
            if (!pdc.has(plugin.getKey(), PersistentDataType.STRING)) return;

            String uuid = pdc.get(plugin.getKey(), PersistentDataType.STRING);
            if (uuid == null) return;

            UUID wormID = UUID.fromString(uuid);
            Worm worm = wormMethods.getWorm(wormID);

            entityPlayer.startRiding(worm);
        }
    }
}
