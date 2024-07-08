package me.psikuvit.wormparty.entity;

import me.psikuvit.wormparty.WormParty;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
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

    public void spawnWorm(Location location) {
        Worm customWorm = new Worm(location, 5, 0.5, 0.2, 20.0);
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        world.addEntity(customWorm);
        worms.add(customWorm);

        Bukkit.getScheduler().runTaskLater(plugin, () -> customWorm.setInvisible(true), 10);

    }

    public void killWorm(Worm worm) {
        worm.killWorm();
        worms.remove(worm);
    }

    public List<Worm> getWorms() {
        return worms;
    }
}
