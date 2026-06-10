package org.rogue.domain.entity.game;

import org.rogue.domain.entity.character.Player;
import org.rogue.domain.entity.level.Level;
import org.rogue.domain.service.GameEngine;

public class GameSession {
    private final Statistics statistics;
    private boolean isFinish;
    private int levelNumber;
    private Level level;
    private Player player;
    private GameEngine gameEngine;

    public GameSession(GameEngine gameEngine) {
        isFinish = false;
        levelNumber = 1;
        statistics = new Statistics();
        this.gameEngine = gameEngine;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void increaseLevelNumber() {
        if (this.levelNumber < 21) {
            this.levelNumber++;
            statistics.incrementLevel();
        }
        else this.isFinish = true;
    }

    public void handleDeath() {
        isFinish = true;
        gameEngine.quit();
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public GameEngine getGameEngine() {
        return gameEngine;
    }
}
