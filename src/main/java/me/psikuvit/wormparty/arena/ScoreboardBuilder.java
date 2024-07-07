package me.psikuvit.wormparty.arena;

import me.psikuvit.wormparty.Utils;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScoreboardBuilder {

    private final Scoreboard scoreboard;
    private String title;
    private List<String> lines;

    public ScoreboardBuilder() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.title = "";
        this.lines = new ArrayList<>();
    }

    public ScoreboardBuilder setTitle(String title) {
        this.title = Utils.color(title);
        return this;
    }
    public ScoreboardBuilder setLines(String... lines) {
        this.lines = Arrays.asList(lines);
        return this;
    }
    public ScoreboardBuilder editLine(int line, String newLine) {
        this.lines.set(line, newLine);
        return this;
    }

    public Scoreboard build() {
        Objective objective = scoreboard.registerNewObjective("test", "dummy", this.title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (int i = 0; i < lines.size(); i++) {
            Score score1 = objective.getScore(Utils.color(this.lines.get(i)));
            score1.setScore(i + 1);
        }

        return scoreboard;
    }

}
