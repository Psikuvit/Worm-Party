package me.psikuvit.wormparty.runnables;

import me.psikuvit.wormparty.Utils;
import me.psikuvit.wormparty.WormParty;
import me.psikuvit.wormparty.arena.Arena;
import me.psikuvit.wormparty.arena.GameState;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

public class LobbyRunnable extends BukkitRunnable {

    private final Arena arena;
    private final WormParty plugin;
    private int cooldown;

    public LobbyRunnable(WormParty plugin, Arena arena) {
        this.arena = arena;
        arena.setGameState(GameState.STARTING);
        this.plugin = plugin;
        this.cooldown = arena.getCooldown();

    }

    @Override
    public void run() {
        if (cooldown == 0) {
            cancel();
            (new InGameRunnable(arena)).runTaskTimer(plugin, 0, 20);
        }


        Scoreboard updatedScoreboard = arena.getScoreboard().editLine(3, "&bTimer: " + "&7" + Utils.secToMin(cooldown)).build();
        arena.getPlayers().forEach(player -> {
            player.setScoreboard(updatedScoreboard);
            player.setLevel(cooldown);
        });

        cooldown--;
    }
}
