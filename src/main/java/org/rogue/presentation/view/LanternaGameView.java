package org.rogue.presentation.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import org.rogue.datalayer.dto.RecordTableDto;
import org.rogue.domain.entity.character.Enemy;
import org.rogue.domain.entity.character.Mimic;
import org.rogue.domain.entity.character.Player;
import org.rogue.domain.entity.item.Item;
import org.rogue.domain.entity.item.ItemType;
import org.rogue.domain.entity.level.Tile;
import org.rogue.datalayer.dto.GameStateDto;
import org.rogue.domain.service.ExploreService.ExploredTiles;
import org.rogue.domain.service.ExploreService.VisibleTiles;
import org.rogue.presentation.input.InputCommand;
import org.rogue.presentation.presenter.GamePresenter;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class LanternaGameView implements GameView {
    private final Screen screen;

    public LanternaGameView() throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        terminalFactory.setInitialTerminalSize(new TerminalSize(72, 30));
        Terminal terminal = terminalFactory.createTerminal();

        screen = new TerminalScreen(terminal);
        screen.startScreen();
        screen.setCursorPosition(null);
        screen.doResizeIfNecessary();
    }

    @Override
    public void drawMainMenu(String[] options, int selectedIndex) {
        screen.clear();

        int startX = 5;
        int startY = 5;
        String title = "ROGUE";
        drawString(title, new Point(startX, startY), TextColor.ANSI.YELLOW_BRIGHT);

        for (int i = 0; i < options.length; i++) {
            String prefix = (i == selectedIndex) ? "> " : "  ";
            String line = prefix + options[i];
            drawString(line, new Point(startX, startY + 2 + i), TextColor.ANSI.WHITE);
        }

        try {
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawRecordsTable(RecordTableDto dto) {
        screen.clear();

        int startX = 5;
        int startY = 2;
        String title = "RECORDS TABLE";
        drawString(title, new Point(startX, startY), TextColor.ANSI.YELLOW_BRIGHT);

        int orderIndex = 1;
        for (int i = 0; i < 20; i++) {
            String space = orderIndex < 10 ? "  " : " ";
            String order = space + (orderIndex++) + ". ";
            String value1 = dto.table.size() > i ? dto.table.get(i) : "-----";
            i++;
            String value2 = dto.table.size() > i ? dto.table.get(i) : "";
            drawString(order + value1, new Point(startX, startY + 2 + (i - 1)), TextColor.ANSI.WHITE);
            drawString("    " + value2, new Point(startX, startY + 2 + i), TextColor.ANSI.WHITE);
        }

        try {
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawGame(GameStateDto state) {
        screen.clear();

        // отрисовка уровня по состоянию игры
        drawTiles(state.getTilesMap(), state.getExploredTiles(), state.getVisibleTiles(), state.width, state.height, state.isFogOfWar());
        drawItems(state);
        drawEnemies(state);
        drawPlayer(state.getPlayerPosition());
        drawStatusBar(state);
        drawControls(state);
        if (state.isInventoryOpen()) drawInventory(state, state.getInventory());
        try {
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private char calculateVisibleForLevel(Tile tileType, ExploredTiles exploredTile, VisibleTiles visibleTile, boolean fogOfWar) {
        if (!fogOfWar || visibleTile == VisibleTiles.VISIBLE) {
            if (tileType == Tile.FLOOR) return '·';
            if (tileType == Tile.CORRIDOR) return 'º';
            if (tileType == Tile.DOOR) return 'D';
            if (tileType == Tile.WALL) return '×';
            if (tileType == Tile.EXIT) return 'E';
        }

        if (tileType == Tile.CORRIDOR) if (exploredTile == ExploredTiles.EXPLORED) return 'º';
        if (tileType == Tile.DOOR) if (exploredTile == ExploredTiles.EXPLORED) return 'D';
        if (tileType == Tile.WALL) if (exploredTile == ExploredTiles.EXPLORED) return '×';

        return ' ';
    }

    private void drawTiles(Tile[][] tiles, ExploredTiles[][] exploredTiles, VisibleTiles[][] visibleTiles, int width, int height, boolean fogOfWar) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile tile = tiles[x][y];
                char value;
                TextColor color = TextColor.ANSI.MAGENTA;

                switch (tile) {
                    case Tile.CORRIDOR -> color = TextColor.ANSI.WHITE;
                    case Tile.DOOR -> color = TextColor.ANSI.WHITE;
                    case Tile.WALL -> color = TextColor.ANSI.YELLOW;
                    case Tile.FLOOR -> color = TextColor.ANSI.GREEN;
                    case Tile.EXIT -> color = TextColor.ANSI.MAGENTA_BRIGHT;

                }
                value = calculateVisibleForLevel(tile, exploredTiles[x][y], visibleTiles[x][y], fogOfWar);

                screen.setCharacter(x, y, new TextCharacter(value, color, TextColor.ANSI.BLACK));
            }
        }
    }

    private void drawPlayer(Point position) {
        screen.setCharacter(position.x, position.y, new TextCharacter('@', TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK));
    }

    private char calculateVisibleForItems(Item item, VisibleTiles visibleTile, boolean fogOfWar) {
        if (!fogOfWar || visibleTile == VisibleTiles.VISIBLE) {
            return switch (item.getType()) {
                case ItemType.WEAPON -> 'W';
                case ItemType.FOOD -> 'F';
                case ItemType.POTION -> 'P';
                case ItemType.SCROLL -> 'S';
                default -> '*';
            };
        }

        return ' ';
    }

    private void drawItems(GameStateDto state) {
        Item[] items = state.getItems();
        VisibleTiles[][] visibleTiles = state.getVisibleTiles();

        for (Item currentItem : items) {
            int x = currentItem.getPosition().x;
            int y = currentItem.getPosition().y;
            char value = calculateVisibleForItems(currentItem, visibleTiles[x][y], state.isFogOfWar());
            screen.setCharacter(x, y, new TextCharacter(value, TextColor.ANSI.BLUE_BRIGHT, TextColor.ANSI.BLACK));
        }
    }

    private void drawInventory(GameStateDto state, Item[] inventory) {
        int marginX = 10;
        int marginY = 5;
        int width = state.width;
        int height = state.height;

        for (int x = marginX; x < width - marginX; x++) {
            for (int y = marginY; y < height - marginY; y++) {
                if (x == marginX || x == width - marginX - 1)
                    screen.setCharacter(x, y, new TextCharacter('*', TextColor.ANSI.GREEN_BRIGHT, TextColor.ANSI.BLACK));
                else if (y == marginY || y == height - marginY - 1)
                    screen.setCharacter(x, y, new TextCharacter('*', TextColor.ANSI.GREEN_BRIGHT, TextColor.ANSI.BLACK));
                else
                    screen.setCharacter(x, y, new TextCharacter(' ', TextColor.ANSI.GREEN_BRIGHT, TextColor.ANSI.BLACK));
            }
        }

        String title = state.getInventoryType() + " INVENTORY";
        int startX = calculateCenterForString(marginX, width, title);
        int startY = marginY;
        drawString(title, new Point(startX, startY), TextColor.ANSI.YELLOW_BRIGHT);
        startX = marginX * 2;

        for (int i = 0; i < inventory.length; i++) {
            String order = " " + (i + 1) + ". ";
            String value = inventory[i] == null ? "-----" : inventory[i].getName();
            drawString(order + value, new Point(startX, startY + 2 + i), TextColor.ANSI.WHITE);
        }

        String instruction = "WHAT " + state.getInventoryType() + " DO YOU WANT TO USE?";
        startX = calculateCenterForString(marginX, width, instruction);
        startY = height - marginY - 4;
        drawString(instruction, new Point(startX, startY), TextColor.ANSI.GREEN_BRIGHT);

        instruction = "PRESS 1-9 TO USE";
        startX = calculateCenterForString(marginX, width, instruction);
        drawString(instruction, new Point(startX, startY + 1), TextColor.ANSI.GREEN_BRIGHT);

        if (state.getInventoryType() == ItemType.WEAPON) {
            instruction = "0 TO PUT IN INVENTORY";
            startX = calculateCenterForString(marginX, width, instruction);
            drawString(instruction, new Point(startX, startY + 2), TextColor.ANSI.GREEN_BRIGHT);
        }


        try {
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private char calculateVisibleForEnemies(Enemy enemy, VisibleTiles visibleTile, boolean fogOfWar) {
        if (!fogOfWar || visibleTile == VisibleTiles.VISIBLE) {
            return switch (enemy.getType()) {
                case ZOMBIE -> 'Z';
                case VAMPIRE -> 'V';
                case GHOST -> 'G';
                case OGRE -> 'O';
                case SNAKE_MAGE -> 'S';
                case MIMIC -> {
                    if (!enemy.isVisible()) {
                        yield ((Mimic) enemy).getDisguiseChar();
                    } else {
                        yield 'M';
                    }
                }
                default -> 'X';
            };
        }

        return ' ';
    }

    private void drawEnemies(GameStateDto state) {
        List<Enemy> enemies = state.getEnemies();
        VisibleTiles[][] visibleTiles = state.getVisibleTiles();

        for (Enemy enemy : enemies) {
            int x = enemy.getPosition().x;
            int y = enemy.getPosition().y;
            char value = calculateVisibleForEnemies(enemy, visibleTiles[x][y], state.isFogOfWar());
            if (value == ' ') continue;

            TextColor color;
            switch (enemy.getType()) {
                case ZOMBIE -> color = TextColor.ANSI.GREEN_BRIGHT;
                case VAMPIRE -> color = TextColor.ANSI.RED_BRIGHT;
                case SNAKE_MAGE -> color = TextColor.ANSI.WHITE_BRIGHT;
                case GHOST -> {
                    if (enemy.isVisible()) {
                        color = TextColor.ANSI.WHITE_BRIGHT;
                    } else continue;
                }
                case OGRE -> color = TextColor.ANSI.YELLOW_BRIGHT;
                case MIMIC -> color = enemy.isVisible() ? TextColor.ANSI.WHITE_BRIGHT : TextColor.ANSI.BLUE_BRIGHT;
                default -> color = TextColor.ANSI.RED;
            }

            screen.setCharacter(x, y, new TextCharacter(value, color, TextColor.ANSI.BLACK));
        }
    }

    private void drawStatusBar(GameStateDto state) {
        int yPos = state.height + 1;

        Player player = state.getPlayer();
        String weaponName = player.getCurrentWeapon() == null ? "None" : player.getCurrentWeapon().getName();
        int weaponBase = player.getCurrentWeapon() == null ? 0 : player.getCurrentWeapon().getStrength();

        String status = String.format("HP:%d/%d  STR:%d  AGL:%d  WEAPON:%s(+%d)  Gold:%d  LVL:%d",
                player.getHealth(),
                player.getMaxHealth(),
                player.getStrength(),
                player.getAgility(),
                weaponName,
                weaponBase,
                player.getBackpack().getGoldAmount(),
                state.getLevelNumber());

        drawString(status, new Point(calculateCenterForString(1, state.width, status), yPos), TextColor.ANSI.GREEN_BRIGHT);
    }

    private void drawControls(GameStateDto state) {
        int yPos = state.height + 3;
        String controls = "WASD:MOVE  H:WEAPON  J:FOOD  K:POTION  E:SCROLL  F:FOG  Q:EXIT";
        drawString(controls, new Point(calculateCenterForString(1, state.width, controls), yPos), TextColor.ANSI.WHITE);
    }

    private void drawString(String s, Point pos, TextColor.ANSI color) {
        for (int i = 0; i < s.length(); i++) {
            screen.setCharacter(pos.x + i, pos.y, new TextCharacter(s.charAt(i), color, TextColor.ANSI.BLACK));
        }
    }

    private int calculateCenterForString(int marginX, int width, String str) {
        return marginX + ((width - marginX * 2) / 2 - (str.length() / 2)) - (str.length() / 2) % 2;
    }

    public InputCommand getNextInputFromMenu() {
        try {
            KeyStroke keyStroke = screen.readInput();
            return mapKeyToCommandFromMenu(keyStroke);
        } catch (IOException e) {
            return InputCommand.NONE;
        }
    }

    @Override
    public InputCommand getNextInput(GamePresenter presenter) {
        try {
            KeyStroke keyStroke = screen.readInput();
            return mapKeyToCommand(keyStroke, presenter);
        } catch (IOException e) {
            return InputCommand.NONE;
        }
    }

    private InputCommand mapKeyToCommandFromMenu(KeyStroke key) {
        if (key.getKeyType() == KeyType.Character) {
            char c = key.getCharacter();

            switch (c) {
                case 'w':
                    return InputCommand.UP;
                case 's':
                    return InputCommand.DOWN;
                case ' ':
                    return InputCommand.SELECT;
                case 'q':
                    return InputCommand.QUIT;
            }

        } else if (key.getKeyType() == KeyType.Escape) {
            return InputCommand.QUIT;
        } else if (key.getKeyType() == KeyType.Enter) {
            return InputCommand.SELECT;
        }

        return InputCommand.NONE;
    }

    private InputCommand mapKeyToCommand(KeyStroke key, GamePresenter presenter) {
        if (key.getKeyType() == KeyType.Character) {
            char c = key.getCharacter();

            switch (c) {
                case 'w':
                    return InputCommand.MOVE_UP;
                case 's':
                    return InputCommand.MOVE_DOWN;
                case ' ':
                    return InputCommand.SELECT;
                case 'a':
                    return InputCommand.MOVE_LEFT;
                case 'd':
                    return InputCommand.MOVE_RIGHT;
                case 'j', 'e', 'k', 'h':
                    return openInventory(c);
                case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9':
                    return presenter.getInventoryOpen() ? useItem(c, presenter) : InputCommand.NONE;
                case 'f':
                    return InputCommand.FOG_OF_WAR;
                case 'q':
                    return InputCommand.QUIT;
            }
        } else if (key.getKeyType() == KeyType.Escape) {
            return InputCommand.QUIT;
        }

        return InputCommand.NONE;
    }

    private InputCommand openInventory(char c) {
        return switch (c) {
            case 'j' -> InputCommand.OPEN_FOOD_INVENTORY;
            case 'k' -> InputCommand.OPEN_POTION_INVENTORY;
            case 'e' -> InputCommand.OPEN_SCROLL_INVENTORY;
            default -> InputCommand.OPEN_WEAPON_INVENTORY;
        };
    }

    private InputCommand useItem(char c, GamePresenter presenter) {
        presenter.setInventoryOpen(false);

        return switch (c) {
            case ('0') -> InputCommand.USE_ITEM_0;
            case ('1') -> InputCommand.USE_ITEM_1;
            case ('2') -> InputCommand.USE_ITEM_2;
            case ('3') -> InputCommand.USE_ITEM_3;
            case ('4') -> InputCommand.USE_ITEM_4;
            case ('5') -> InputCommand.USE_ITEM_5;
            case ('6') -> InputCommand.USE_ITEM_6;
            case ('7') -> InputCommand.USE_ITEM_7;
            case ('8') -> InputCommand.USE_ITEM_8;
            default -> InputCommand.USE_ITEM_9;
        };
    }

    @Override
    public void close() {
        try {
            screen.stopScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
