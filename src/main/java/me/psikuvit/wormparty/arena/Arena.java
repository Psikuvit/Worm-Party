package me.psikuvit.wormparty.arena;

import me.psikuvit.wormparty.WormParty;
import me.psikuvit.wormparty.runnables.LobbyRunnable;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.ArrayList;
import java.util.List;

public class Arena {

    private final List<Player> players;
    private final List<Player> spectators;
    private final Location waitingLoc;
    private final Location mapLoc;
    private GameState gameState;
    private final int minPlayers;
    private final int cooldown;
    private final WormParty plugin;

    public Arena(WormParty plugin, Location waitingLoc, Location mapLoc, int minPlayers) {
        this.players = new ArrayList<>();
        spectators = new ArrayList<>();
        this.waitingLoc = waitingLoc;
        this.mapLoc = mapLoc;
        this.gameState = GameState.WAITING;
        this.minPlayers = minPlayers;
        this.cooldown = 20;
        this.plugin = plugin;
    }

    public void addPlayer(Player player) {
        player.teleport(waitingLoc);
        if (getGameState() == GameState.END && getGameState() == GameState.INGAME) {
            addSpectator(player);
            return;
        }
        players.add(player);

        player.setGameMode(GameMode.ADVENTURE);
        players.forEach(player1 -> player1.setScoreboard(getScoreboard().build()));
        player.setLevel(cooldown);


        if (getPlayers().size() >= getMinPlayers()) {
            (new LobbyRunnable(plugin,this)).runTaskTimer(plugin, 0, 20L);
        }
    }

    public void removePlayer(Player player) {
        players.remove(player);
        spectators.remove(player);
        Bukkit.getOnlinePlayers().forEach(online -> online.showPlayer(plugin, player));
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
    }

    public void addSpectator(Player player) {
        spectators.add(player);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.getInventory().clear();
        Bukkit.getOnlinePlayers().forEach(online -> online.hidePlayer(plugin, player));
    }



    public ScoreboardBuilder getScoreboard() {
        return new ScoreboardBuilder().setTitle("&eWorm Party").
                setLines(" ", " ", "&bPlayers: " + "&7" + players.size(), "&bTimer: &7120", "&bGame Status: &7" + getGameState().toString());
    }


    public List<Player> getPlayers() {
        return players;
    }

    public GameState getGameState() {
        return gameState;
    }
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public Location getMapLoc() {
        return mapLoc;
    }

    public Location getWaitingLoc() {
        return waitingLoc;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getTimeTillPvP() {
        return plugin.getConfigUtils().timeTillPvP();
    }

    public int getCooldown() {
        return cooldown;
    }

    public List<Player> getSpectators() {
        return spectators;
    }
}
