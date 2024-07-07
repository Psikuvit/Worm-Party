package me.psikuvit.wormparty.listener;

import me.psikuvit.wormparty.WormParty;
import me.psikuvit.wormparty.arena.GameState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Random;

public class ArenaListeners implements Listener {

    private final WormParty plugin;

    public ArenaListeners(WormParty plugin) {
        this.plugin = plugin;

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        plugin.getArena().addPlayer(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        plugin.getArena().removePlayer(player);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER || event.getDamager().getType() == EntityType.PLAYER) {
            if (plugin.getArena().getGameState() == GameState.STARTING) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (plugin.getArena().getGameState() == GameState.INGAME) {
            Player player = event.getEntity();
            plugin.getArena().removePlayer(player);
        }
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (event.getEntity().getType() == EntityType.SHEEP) {
            Random rnd = new Random();
            if (rnd.nextInt(2) == 1) {
                event.setCancelled(true);
                plugin.getWormMethods().spawnWorm(event.getLocation());
            }
        }
    }
}
