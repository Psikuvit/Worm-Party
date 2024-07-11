package me.psikuvit.wormparty.api;

import me.psikuvit.wormparty.entity.Worm;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WormKillEvent extends Event implements Cancellable {

    private final HandlerList HANDLERS = new HandlerList();
    private final Worm worm;
    private final Entity killer;
    private boolean cancelled;

    public WormKillEvent(Worm worm, Entity killer) {
        this.cancelled = false;
        this.worm = worm;
        this.killer = killer;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public Worm getWorm() {
        return worm;
    }

    public Location getLocation() {
        return worm.getBukkitEntity().getLocation();
    }

    public Entity getKiller() {
        return killer;
    }
}
