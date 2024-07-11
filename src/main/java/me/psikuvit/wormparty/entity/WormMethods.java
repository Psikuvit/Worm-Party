package me.psikuvit.wormparty.entity;

import me.psikuvit.wormparty.MinecraftReflection;
import me.psikuvit.wormparty.WormParty;
import me.psikuvit.wormparty.api.WormKillEvent;
import me.psikuvit.wormparty.api.WormSpawnEvent;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WormMethods {

    private final List<Worm> worms;
    private final WormParty plugin;

    public WormMethods(WormParty plugin) {
        this.plugin = plugin;
        this.worms = new ArrayList<>();
    }

    public Worm getWorm(UUID wormID) {
        for (Worm worm : worms) {
            if (worm.getWormID().equals(wormID)) {
                return worm;
            }
        }
        return null;
    }
    public double getWormHP(Worm worm) {
        PersistentDataContainer pdc = worm.getWormPDC();
        if (pdc.has(plugin.getKey(), PersistentDataType.DOUBLE)) {
            Double value = pdc.get(plugin.getKey(), PersistentDataType.DOUBLE);
            return value != null ? value : 0;
        }
        return 0;
    }

    public void updateHP(Worm worm, double newHP) {
        PersistentDataContainer pdc = worm.getWormPDC();
        pdc.set(plugin.getKey(), PersistentDataType.DOUBLE, newHP);
    }

    public void spawnWorm(Location location, CreatureSpawnEvent.SpawnReason spawnReason) {
        Worm customWorm = new Worm(location, 5, 20.0);
        ServerLevel nmsWorld = (ServerLevel) MinecraftReflection.getNMSWorld(location.getWorld());

        WormSpawnEvent wormSpawnEvent = new WormSpawnEvent(customWorm);
        Bukkit.getPluginManager().callEvent(wormSpawnEvent);

        if (wormSpawnEvent.isCancelled()) {
            return;
        }
        nmsWorld.addEntity(customWorm, spawnReason);
        worms.add(customWorm);

        Bukkit.getScheduler().runTaskLater(plugin, () -> customWorm.setInvisible(true), 10);

    }

    public void killWorm(Worm worm, Entity killer) {
        WormKillEvent wormKillEvent = new WormKillEvent(worm, killer);
        Bukkit.getPluginManager().callEvent(wormKillEvent);

        if (wormKillEvent.isCancelled()) {
            return;
        }
        worm.killWorm();
        worms.remove(worm);
    }

    public List<Worm> getWorms() {
        return worms;
    }
}
