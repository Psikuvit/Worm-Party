package me.psikuvit.wormparty.runnables;

import me.psikuvit.wormparty.Utils;
import me.psikuvit.wormparty.arena.Arena;
import me.psikuvit.wormparty.arena.GameState;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

public class InGameRunnable extends BukkitRunnable {

    private final Arena arena;
    private final int timeTillPvP;
    private int timer;

    public InGameRunnable(Arena arena) {
       this.arena = arena;
       this.timeTillPvP = arena.getTimeTillPvP();
       arena.setGameState(GameState.INGAME);
       setupBorder();
       arena.getPlayers().forEach(player -> {
           player.teleport(arena.getMapLoc());
           player.setLevel(0);
           player.setGameMode(GameMode.SURVIVAL);
       });
    }

    @Override
    public void run() {
        if (timer <= timeTillPvP) {
            int newTime = timeTillPvP - timer;
            Scoreboard updatedScoreboard = arena.getScoreboard().editLine(3, "&bTime Till PvP: &7" + Utils.secToMin(newTime)).build();
            arena.getPlayers().forEach(player -> player.setScoreboard(updatedScoreboard));
        } else {
            Scoreboard updatedScoreboard = arena.getScoreboard().editLine(3, "&bTimer: &7" + Utils.secToMin(timer)).build();
            arena.getPlayers().forEach(player -> player.setScoreboard(updatedScoreboard));

        }
        if (arena.getPlayers().size() == 1) {
            arena.setGameState(GameState.END);
            Bukkit.getOnlinePlayers().forEach(player -> Utils.sendTitle(player, arena.getPlayers().get(0).getName(), "&bIs the WINNER!!", 20, 1200, 20));
            cancel();
        }

        timer++;
    }

    public void setupBorder() {
        WorldBorder worldBorder = Bukkit.getWorlds().get(0).getWorldBorder();
        worldBorder.setCenter(arena.getMapLoc());
        worldBorder.setSize(2000);
        worldBorder.setWarningDistance(2);
        worldBorder.setDamageAmount(1);
        worldBorder.setDamageBuffer(1);
    }
}