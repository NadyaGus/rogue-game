package org.rogue.presentation.presenter;

import org.rogue.datalayer.dto.RecordTable;
import org.rogue.domain.entity.item.ItemType;
import org.rogue.domain.service.GameEngine;
import org.rogue.datalayer.dto.GameStateDto;
import org.rogue.datalayer.dto.GameStateMapper;
import org.rogue.presentation.input.InputCommand;
import org.rogue.presentation.view.GameView;

public class GamePresenter {
    private final GameView view;
    private final GameEngine gameEngine; // или GameService
    private final GameStateMapper mapper;
    private final RecordTable recordTable;
    private boolean isInventoryOpen = false;
    private ItemType inventoryTypeOpen = ItemType.FOOD;
    private boolean isFogOfWarUsed = true;

    public GamePresenter(GameView view, GameEngine gameEngine, RecordTable recordTable) {
        this.view = view;
        this.gameEngine = gameEngine;
        this.mapper = new GameStateMapper();
        this.recordTable = recordTable;
    }

    public void startGame() {
        this.gameEngine.startGame();

        updateView();
        // главный игровой цикл
        while (!gameEngine.isGameOver()) {
            InputCommand cmd = view.getNextInput(this);
            processCommand(cmd);
            updateView();
        }
        recordTable.addResult(gameEngine.getGameSession().getStatistics());
    }

    private void processCommand(InputCommand command) {
        gameEngine.stepUp();

        switch (command) {
            case MOVE_UP, MOVE_DOWN, MOVE_LEFT, MOVE_RIGHT:
                gameEngine.getMovementService().nextGameStep(command);
                break;
            case OPEN_FOOD_INVENTORY:
                handleInventory(ItemType.FOOD);
                break;
            case OPEN_POTION_INVENTORY:
                handleInventory(ItemType.POTION);
                break;
            case OPEN_SCROLL_INVENTORY:
                handleInventory(ItemType.SCROLL);
                break;
            case OPEN_WEAPON_INVENTORY:
                handleInventory(ItemType.WEAPON);
                break;
            case USE_ITEM_0, USE_ITEM_1, USE_ITEM_2, USE_ITEM_3, USE_ITEM_4,
                 USE_ITEM_5, USE_ITEM_6, USE_ITEM_7, USE_ITEM_8, USE_ITEM_9:
                handleItemUse(command);
                break;
            case FOG_OF_WAR:
                isFogOfWarUsed = !isFogOfWarUsed;
                break;
            case QUIT:
                gameEngine.quit();
                break;
        }
    }

    private void handleInventory(ItemType type) {
        setInventoryOpen(!isInventoryOpen);
        inventoryTypeOpen = type;
    }

    public boolean getInventoryOpen() {
        return isInventoryOpen;
    }

    public void setInventoryOpen(boolean inventoryOpen) {
        isInventoryOpen = inventoryOpen;
    }

    private void handleItemUse(InputCommand command) {
        switch (command) {
            case USE_ITEM_0:
                gameEngine.useItemFromInventory(inventoryTypeOpen, 0);
                break;
            case USE_ITEM_1:
                gameEngine.useItemFromInventory(inventoryTypeOpen, 1);
                break;
            case USE_ITEM_2:
                gameEngine.useItemFromInventory(inventoryTypeOpen, 2);
                break;
            case USE_ITEM_3:
                gameEngine.useItemFromInventory(inventoryTypeOpen, 3);
                break;
            case USE_ITEM_4:
                gameEngine.useItemFromInventory(inventoryTypeOpen, 4);
                break;
            case USE_ITEM_5:
                gameEngine.useItemFromInventory(inventoryTypeOpen, 5);
                break;
            case USE_ITEM_6:
                gameEngine.useItemFromInventory(inventoryTypeOpen, 6);
                break;
            case USE_ITEM_7:
                gameEngine.useItemFromInventory(inventoryTypeOpen, 7);
                break;
            case USE_ITEM_8:
                gameEngine.useItemFromInventory(inventoryTypeOpen, 8);
                break;
            case USE_ITEM_9:
                gameEngine.useItemFromInventory(inventoryTypeOpen, 9);
                break;
        }

        isInventoryOpen = false;
    }

    private void updateView() {
        // апдейт по состоянию игры
        GameStateDto state = mapper.toDto(gameEngine.getGameSession(), isInventoryOpen, inventoryTypeOpen, isFogOfWarUsed);
        view.drawGame(state);
    }
}