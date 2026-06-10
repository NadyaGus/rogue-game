package org.rogue.domain.service;

import org.rogue.domain.entity.character.Player;
import org.rogue.domain.entity.game.GameSession;
import org.rogue.domain.entity.item.ItemType;
import org.rogue.domain.entity.level.Level;
import org.rogue.domain.entity.level.Room;

import java.awt.*;
import java.util.Random;

/**
 * Движок игры
 * Вся бизнес логика должна быть тут или дергать другие сервисы
 */
public class GameEngine {
    GameSession gameSession;
    private MovementService movementService;
    private ItemService itemService;
    boolean isGameOver;

    public GameEngine() {
        this.isGameOver = false;
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    public void startGame() {
        isGameOver = false;
        gameSession = new GameSession(this);
        itemService = new ItemService(gameSession);
        Level level = new LevelGenerator(gameSession.getLevelNumber()).createNewLevel(itemService);
        gameSession.setLevel(level);
        movementService = new MovementService(gameSession, itemService);
        createPlayer();
    }

    private void createPlayer() {
        Player player = new Player(randomizePlayerPosition());
        gameSession.setPlayer(player);
        movementService.look(player.getPosition().x, player.getPosition().y);
    }

    private Point randomizePlayerPosition() {
        Random random = new Random();
        Room startintRoom = gameSession.getLevel().getStartingRoom();

        int x = random.nextInt(startintRoom.getTopLeft().x + 1, startintRoom.getTopRight().x - 1);
        int y = random.nextInt(startintRoom.getTopLeft().y + 1, startintRoom.getBottomLeft().y - 1);

        return new Point(x, y);
    }

    public boolean isGameOver() {
        return this.isGameOver;
    }

    public void useItemFromInventory(ItemType type, int order) {
        if (type == ItemType.FOOD || type == ItemType.SCROLL || type == ItemType.POTION) {
            itemService.consumeItem(type, order);
        } else if (type == ItemType.WEAPON) {
            itemService.useWeaponFromInventory(order);
        }
    }

    public void stepUp() {
        itemService.processPotionTime();
        gameSession.getStatistics().incrementSteps();
//        System.out.println("CURRENT WEAPON " + gameSession.getPlayer().getCurrentWeapon());
    }

    public void levelUp() {
        gameSession.increaseLevelNumber();
        if (!gameSession.isFinish()) {
            Level level = new LevelGenerator(gameSession.getLevelNumber()).createNewLevel(itemService);
            gameSession.setLevel(level);
            Point pos = randomizePlayerPosition();
            gameSession.getPlayer().setPosition(pos);
            movementService.look(pos.x, pos.y);
        } else {
            quit();
        }
    }

    public void quit() {
        this.isGameOver = true;
    }

    public MovementService getMovementService() {
        return movementService;
    }
}
