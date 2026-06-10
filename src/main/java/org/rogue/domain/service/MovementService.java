package org.rogue.domain.service;

import org.rogue.domain.entity.character.*;
import org.rogue.domain.entity.character.Character;
import org.rogue.domain.entity.game.GameSession;
import org.rogue.domain.entity.level.Corridor;
import org.rogue.domain.entity.level.Level;
import org.rogue.domain.entity.level.Room;
import org.rogue.domain.entity.level.Tile;
import org.rogue.domain.entity.modifier.Modifier;
import org.rogue.domain.service.attackService.CombatEngine;
import org.rogue.presentation.input.InputCommand;
import org.rogue.domain.entity.modifier.effect.*;


import java.awt.*;
import java.util.Objects;
import java.util.*;
import java.util.List;

public class MovementService {
    // обработка перемещения, столкновний с врагами, стенами, лутом и т.д
    private final GameSession gameSession;
    private final ItemService itemService;

    public MovementService(GameSession gameSession, ItemService itemService) {
        this.gameSession = gameSession;
        this.itemService = itemService;
    }


    // Прямые направления
    public static final Point UP = new Point(0, -1);
    public static final Point DOWN = new Point(0, 1);
    public static final Point LEFT = new Point(-1, 0);
    public static final Point RIGHT = new Point(1, 0);

    // Диагональные направления
    public static final Point UP_LEFT = new Point(-1, -1);
    public static final Point UP_RIGHT = new Point(1, -1);
    public static final Point DOWN_LEFT = new Point(-1, 1);
    public static final Point DOWN_RIGHT = new Point(1, 1);

    // Списки направлений
    public static final List<Point> STRAIGHT_DIRECTIONS = Arrays.asList(UP, DOWN, LEFT, RIGHT);
    public static final List<Point> DIAGONAL_DIRECTIONS = Arrays.asList(UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT);
    public static final List<Point> ALL_DIRECTIONS = new ArrayList<>() {{
        addAll(STRAIGHT_DIRECTIONS);
        addAll(DIAGONAL_DIRECTIONS);
    }};


    public void nextGameStep(InputCommand command) {
        if (!isCharacterSkipStep(gameSession.getPlayer())) {
            switch (command) {
                case MOVE_UP -> playerAction(UP);
                case MOVE_DOWN -> playerAction(DOWN);
                case MOVE_LEFT -> playerAction(LEFT);
                case MOVE_RIGHT -> playerAction(RIGHT);
            }
        }
        // действия всех остальных врагов (движение/атака):
        List<Enemy> enemies = gameSession.getLevel().getEnemies();
        for (Enemy enemy : enemies) {
            if (!isCharacterSkipStep(enemy)) {
                enemyAction(enemy);
            }
        }
    }

    // FIXME: КАК-ТО ПЕРЕПИСАТЬ ПО РКАСИВЕЕ. (это проверка эффектов перед выполнением хода.)
    //  так же тут уменьшается таймер эффектов, удаляются истекшие эффекты.
    private boolean isCharacterSkipStep(Character character) {
        boolean skipStep = false;
        List<Modifier> modifiers = character.getModifiers();
        for (Modifier mod : modifiers) {
            switch (mod) {
                case SleepEffect effect:
                    if (!effect.isExpired()) {   // сон активен, прпоускаем ход
                        skipStep = true;
                        System.out.println(character.getName() + " missed a turn due to the SleepEffect");
                    }
                    break;
                case CounterAttackEffect effect:
                    if (effect.isRestFlag()) { // отдых еще действует, пропускаем ход и сбрасываем флаг отдыха.
                        skipStep = true;
                        effect.setRestFlag(false);
                        System.out.println(character.getName() + " missed a turn due to the RestFlag in CounterAttackEffect");
                    }
                    break;
                default:
                    break;
            }
        }
        for (Modifier mod : modifiers) {
            mod.reduceDuration();
        }
        Iterator<Modifier> iterator = modifiers.iterator();
        while (iterator.hasNext()) {
            Modifier mod = iterator.next();
            if (mod.isExpired()) {
                iterator.remove();   // удаление через итератор
            }
        }
        return skipStep;
    }

    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  ДЕЙСТВИЕ ИГРОКА  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    private void playerAction(Point direction) {
        Player player = gameSession.getPlayer();
        Point position = player.getPosition();
        int x = position.x + direction.x;
        int y = position.y + direction.y;
        Point newPos = new Point(x, y);

        Enemy enemy = getEnemyFromPoint(newPos);
        if (enemy != null) {
            CombatEngine.processAttack(player, enemy);
            gameSession.getStatistics().incrementHitsDealt();
            if (!enemy.isAlive()) {
                System.out.println(enemy.getName() + " died!\n");
                int value = (int) (enemy.getStrength() + enemy.getAgility()
                        + enemy.getHostility() + enemy.getHostility()) / 2;
                itemService.dropGold(newPos, value);
                gameSession.getLevel().getEnemies().remove(enemy);
                gameSession.getStatistics().incrementEnemiesDefeated();
            }
        } else if (!isObstacle(newPos)) {
            position.move(x, y);
            handleItem(position.x, position.y);
        }

        look(position.x, position.y);

        if (position.equals(gameSession.getLevel().getExitPoint())) {
            gameSession.getGameEngine().levelUp();
        }
    }


    private Enemy getEnemyFromPoint(Point point) {
        List<Enemy> enemies = gameSession.getLevel().getEnemies();
        for (Enemy e : enemies) {
            if (e.getPosition().equals(point)) return e;
        }
        return null;
    }

    private boolean isObstacle(Point point) {
        Tile[][] tiles = gameSession.getLevel().getTiles();
        switch (tiles[point.x][point.y]) {
            case FLOOR, CORRIDOR, DOOR, EXIT:
                return false;
            default:
                return true;
        }
    }

    private void explore(int x, int y) {
        Tile[][] tiles = gameSession.getLevel().getTiles();

        if (Objects.requireNonNull(tiles[x][y]) == Tile.DOOR) {
            gameSession.getLevel().getExploreService().exploreCorridor(x, y);
        } else if (Objects.requireNonNull(tiles[x][y]) == Tile.FLOOR) {
            gameSession.getLevel().getExploreService().exploreRoom(x, y, gameSession.getLevel().getRooms());
        }
    }

    public void look(int x, int y) {
        explore(x, y);

        Level level = gameSession.getLevel();
        Room[] rooms = level.getRooms();
        Room currentRoom = level.getExploreService().findRoom(x, y, rooms);
        Corridor[] corridors = level.getCorridors();
        Corridor currentCorridor = level.getExploreService().findCorridor(x, y, corridors);
        level.getExploreService().calculateVisibleCells(x, y, currentRoom, rooms, currentCorridor, corridors);
    }

    private void handleItem(int x, int y) {
        itemService.pickUpItem(new Point(x, y));
    }
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  ДЕЙСТВИЕ ИГРОКА  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  ДЕЙСТВИЯ ВРАГОВ  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    /**
     * Если игрок находится в пределах hostility enemy и есть путь до игрока, то enemy:
     * - преследует игрока (движется в сторону игрока) согласно своему паттерну, обходя препятствия;
     * - атакует игрока (enemy находится в соседней клетке от игрока).
     * Если игрок НЕ находится в пределах hostility enemy или нет пути до игрока, то enemy:
     * - патрулирует комнату согласно своему паттерну.
     * Путь до игрока рассчитывается в пределах комнаты, где находится enemy.
     */
    private void enemyAction(Enemy enemy) {
        Point enemyPos = enemy.getPosition();
        Point playerPos = gameSession.getPlayer().getPosition();
        // distanceToPlayer = аналог Math.sqrt(dx*dx + dy*dy):
        double distanceToPlayer = Math.hypot(playerPos.x - enemyPos.x, playerPos.y - enemyPos.y);
        if (distanceToPlayer == 1.0) {
            CombatEngine.processAttack(enemy, gameSession.getPlayer()); // враг атакует игрока
            gameSession.getStatistics().incrementHitsTake();
            if (!gameSession.getPlayer().isAlive()) gameSession.handleDeath();
            return; // враг использовал ход на атаку
        }

        double hostility = enemy.getHostility();
        boolean pursuit = (distanceToPlayer <= hostility); // Преследование
        if (pursuit) {
            defaultPursuitPattern(enemy, enemyPos);
        } else {
            performPatrol(enemy, enemyPos);
        }
    }
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  ДЕЙСТВИЯ ВРАГОВ  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  ПРЕСЛЕДОВАНИЕ  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    private void defaultPursuitPattern(Enemy enemy, Point enemyPos) {
        Point nextStep = findNextStepToPlayer(enemy, ALL_DIRECTIONS);
        if (nextStep != null) {
            enemy.setPosition(nextStep);
            return;
        }
        performPatrol(enemy, enemyPos);
    }

    /**
     * Находит кратчайший путь от текущей позиции enemy до любой клетки, соседней с player,
     * используя только определенные направления для движения "List<Point> directions"
     * Возвращает первую клетку, в которую нужно шагнуть из текущей позиции enemy.
     */
    private Point findNextStepToPlayer(Enemy enemy, List<Point> directions) {
        Point playerPos = gameSession.getPlayer().getPosition();
        Point start = enemy.getPosition();

        Queue<Point> queue = new LinkedList<>();
        Map<Point, Point> cameFrom = new HashMap<>();
        Set<Point> visited = new HashSet<>();

        queue.add(start);
        cameFrom.put(start, null);
        visited.add(start);

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            // Если достигли клетки, смежной с игроком (путь найден), восстанавливаем первый шаг
            if (isAdjacentToPlayer(current, playerPos)) {
                Point step = current;
                while (cameFrom.get(step) != start && cameFrom.get(step) != null) {
                    step = cameFrom.get(step);
                }
                return step; // первая клетка после start
            }

            for (Point dir : directions) {
                Point neighbor = new Point(current.x + dir.x, current.y + dir.y);
                if (visited.contains(neighbor)) continue;
                if (!canEnemyMoveTo(neighbor)) continue;
                visited.add(neighbor);
                cameFrom.put(neighbor, current);
                queue.add(neighbor);
            }
        }
        return null; // путь не найден
    }

    /**
     * Проверяет, является ли клетка cell соседней с игроком.
     */
    private boolean isAdjacentToPlayer(Point cell, Point playerPos) {
        int dx = Math.abs(cell.x - playerPos.x);
        int dy = Math.abs(cell.y - playerPos.y);
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  ПРЕСЛЕДОВАНИЕ  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  ПАТРУЛИРОВАНИЕ  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    private void performPatrol(Enemy enemy, Point enemyPos) {
        switch (enemy.getType()) {
            case GHOST:
                ghostPatrolPattern(enemy, enemyPos);
                break;
            case OGRE:
                ogrePatrolPattern(enemy, enemyPos);
                break;
            case SNAKE_MAGE:
                snakeMagePatrolPattern(enemy, enemyPos);
                break;
            case MIMIC:
                break;
            default:
                defaultPatrolPattern(enemy, enemyPos);
        }
    }

    /**
     * Случайное движение на 1 клетку (верх, низ, лево, право):
     */
    private void defaultPatrolPattern(Enemy enemy, Point enemyPos) {
        List<Point> directions = new ArrayList<>(STRAIGHT_DIRECTIONS);
        Collections.shuffle(directions);

        for (Point dir : directions) {
            int newX = enemyPos.x + dir.x;
            int newY = enemyPos.y + dir.y;
            Point newPos = new Point(newX, newY);

            if (canEnemyMoveTo(newPos)) {
                enemy.setPosition(newPos);
                break;
            }
        }
    }

    /**
     * // Телепортация в случайную и свободную клетку той же комнаты
     */
    private void ghostPatrolPattern(Enemy enemy, Point enemyPos) {
        Room currentRoom = getRoomByPosition(enemyPos);
        if (currentRoom == null) return;

        boolean temporaryVisible = false;
        if (!enemy.isVisible() && Math.random() < Ghost.VISIBILITY_CHANCE) {
            enemy.setVisible(true);
            temporaryVisible = true;
        }

        Random random = new Random();
        int attempts = 0;
        while (attempts < 100) {
            int x = currentRoom.getTopLeft().x + random.nextInt(1, currentRoom.width);
            int y = currentRoom.getTopLeft().y + random.nextInt(1, currentRoom.height);
            Point newPos = new Point(x, y);
            if (canEnemyMoveTo(newPos)) {
                enemy.setPosition(newPos);
                if (!temporaryVisible) enemy.setVisible(false);
                return;
            }
            attempts++;
        }
    }

    private Room getRoomByPosition(Point point) {
        for (Room room : gameSession.getLevel().getRooms()) {
            if (room.containsPoint(point)) return room;
        }
        return null;
    }

    /**
     * Движение на 2 клетки в случайном направлении (преимущественно прямо, либо прямо и в бок)
     */
    private void ogrePatrolPattern(Enemy enemy, Point enemyPos) {
        List<Point> directions = new ArrayList<>(STRAIGHT_DIRECTIONS);
        Collections.shuffle(directions);

        for (Point firstDir : directions) {
            // Пытаемся найти направление для первого шага
            Point step1 = new Point(enemyPos.x + firstDir.x, enemyPos.y + firstDir.y);
            if (!canEnemyMoveTo(step1)) {
                continue;
            }

            // Пробуем сделать шаг №2 в том же направлении
            Point step2 = new Point(step1.x + firstDir.x, step1.y + firstDir.y);
            if (canEnemyMoveTo(step2)) {
                enemy.setPosition(step2); // сделали два шага
                return;
            }
            // Шаг №2 в том же направлении невозможен - пробуем повернуть вбок:
            List<Point> sideDirs = getPerpendicularDirections(firstDir);
            Collections.shuffle(sideDirs);
            for (Point sideDir : sideDirs) {
                Point sideStep = new Point(step1.x + sideDir.x, step1.y + sideDir.y);
                if (canEnemyMoveTo(sideStep)) {
                    enemy.setPosition(sideStep);
                    return;
                }
            }
            // Если ни вбок, ни прямо не получилось - остаёмся на первом шаге:
            enemy.setPosition(step1);
            return;
        }
        // Если ни одно направление не подошло для первого шага — враг не двигается
    }

    // Возвращает два перпендикулярных направления (влево и вправо) относительно заданного
    private List<Point> getPerpendicularDirections(Point dir) {
        // если заданное направление вертикальное (вверх/вниз), то вернем 2 направления горизонтальных (влево/вправо):
        if (dir.x == 0) {
            return new ArrayList<>(List.of(new Point(-1, 0), new Point(1, 0)));
            // тут наоборот, если горизонтальное (влево/вправо), то вернем 2 напрв. вертикал. (вверх/вниз):
        } else {
            return new ArrayList<>(List.of(new Point(0, -1), new Point(0, 1)));
        }
    }

    /**
     * Движение на 1 клетку по диагонали в случайном направлении.
     * Если нет возможности походить по диагонали, то использует commonPatrolPattern(enemy);
     */
    private void snakeMagePatrolPattern(Enemy enemy, Point enemyPos) {
        List<Point> diagonals = new ArrayList<>(DIAGONAL_DIRECTIONS);
        Collections.shuffle(diagonals);

        for (Point dir : diagonals) {
            Point newPos = new Point(enemyPos.x + dir.x, enemyPos.y + dir.y);
            if (canEnemyMoveTo(newPos)) {
                enemy.setPosition(newPos);
                return;
            }
        }
        defaultPatrolPattern(enemy, enemyPos);
    }

    /**
     * Проверяет клетку на возможность сделать на нее ход врагом
     */
    private boolean canEnemyMoveTo(Point point) {
        Tile tile = gameSession.getLevel().getTiles()[point.x][point.y];
        if (tile != Tile.FLOOR && tile != Tile.CORRIDOR && tile != Tile.DOOR) {
            return false;
        }
        // Проверка, нет ли другого врага
        if (getEnemyFromPoint(point) != null) {
            return false;
        }
        // Проверка, нет ли предмета
        if (itemService.getItemsMap().containsKey(point)) {
            return false;
        }
        // Проверка, нет ли exitPoint
        if (gameSession.getLevel().getExitPoint().equals(point)) {
            return false;
        }
        // Проверка, нет ли игрока
        if (gameSession.getPlayer().getPosition().equals(point)) {
            return false;
        }

        return true;
    }
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  ПАТРУЛИРОВАНИЕ  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}
