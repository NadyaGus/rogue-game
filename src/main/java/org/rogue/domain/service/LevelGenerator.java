package org.rogue.domain.service;

import org.rogue.domain.entity.character.Enemy;
import org.rogue.domain.entity.item.Item;
import org.rogue.domain.entity.level.Corridor;
import org.rogue.domain.entity.level.Level;
import org.rogue.domain.entity.level.Room;
import org.rogue.domain.entity.level.Tile;
import org.rogue.domain.service.ExploreService.ExploredTiles;

import java.awt.*;
import java.util.*;
import java.util.List;

public class LevelGenerator {
    //  Высота и ширина игрового поля
    private static final int WIDTH = 68; // мин.размер комнаты*3 + 2
    private static final int HEIGHT = 26;

    private static final int MIN_ROOM_SIZE = 4;
    private static final int MAX_ROOMS_AMOUNT = 9;
    private static final int GRID_ROOMS_SIZE = 3;
    private static final int ROOM_SIZE_X = WIDTH / GRID_ROOMS_SIZE;
    private static final int ROOM_SIZE_Y = HEIGHT / GRID_ROOMS_SIZE;

    // от сложности должно зависеть количество врагов
    private int difficulty;
    private Level level;
    private Item[] items;

    /**
     * @param levelNumber Difficulty of level
     */
    public LevelGenerator(int levelNumber) {
        this.difficulty = levelNumber;
    }

    /**
     * @return Level with rooms, corridors, enemies and items
     */
    public Level createNewLevel(ItemService itemService) {
        level = new Level(WIDTH, HEIGHT);
        Room[] rooms = generateRooms();
        level.setRooms(rooms);
        Room startingRoom = randomizeStartingRoom(rooms);
        Point exit = randomizeEndingPoint(rooms);
        setTilesForRooms(rooms);

        Corridor[] corridors = generateCorridors(rooms);
        level.setCorridors(corridors);

        items = itemService.generateItems(rooms, startingRoom, exit, difficulty++);
        level.setItems(items);

        level.setEnemies(generateEnemies(rooms, itemService.getItemsMap()));
        return level;
    }

    private void setTile(int x, int y, Tile tile) {
        level.setTile(x, y, tile);
    }

    /**
     * @return Array of rooms with random size
     */
    private Room[] generateRooms() {
        Room[] rooms = new Room[MAX_ROOMS_AMOUNT];
        Random random = new Random();

        for (int i = 0; i < MAX_ROOMS_AMOUNT; i++) {
            int cellXStart = (i % GRID_ROOMS_SIZE) * ROOM_SIZE_X;
            int cellYStart = (i / GRID_ROOMS_SIZE) * ROOM_SIZE_Y;

            int width = random.nextInt(MIN_ROOM_SIZE, ROOM_SIZE_X - 5);
            int height = random.nextInt(MIN_ROOM_SIZE, ROOM_SIZE_Y - 2);

            int x1 = random.nextInt(cellXStart + 1, cellXStart + (ROOM_SIZE_X - width));
            int y1 = random.nextInt(cellYStart + 1, cellYStart + (ROOM_SIZE_Y - height));
            int x2 = x1 + width;
            int y2 = y1 + height;
            // Пока оставлю для отладки
//            System.out.println("ROOM " + i + ". SIZE: " + width + "x" + height);
//            System.out.println("CORDS:" + x1 + " " + y1 + " | " + x2 + " " + y2);

            rooms[i] = new Room(new Point(x1, y1), new Point(x2, y2));
        }

        return rooms;
    }

    private Room randomizeStartingRoom(Room[] rooms) {
        Random random = new Random();
        int randomIndex = random.nextInt(0, rooms.length);
        Room startingRoom = rooms[randomIndex];
        level.setStartingRoom(startingRoom);

        for (int x = startingRoom.getBottomLeft().x; x < startingRoom.getBottomRight().x + 1; x++) {
            for (int y = startingRoom.getTopLeft().y; y < startingRoom.getBottomRight().y + 1; y++) {
                level.getExploreService().setTile(x, y, ExploredTiles.EXPLORED);
            }
        }

        return startingRoom;
    }

    private Point randomizeEndingPoint(Room[] rooms) {
        Random random = new Random();
        int randomIndex;

        do {
            randomIndex = random.nextInt(0, rooms.length);
        } while (rooms[randomIndex] == level.getStartingRoom());

        level.setEndingRoom(rooms[randomIndex]);
        return level.getExitPoint();
    }

    private void setTilesForRooms(Room[] rooms) {
        for (int i = 0; i < MAX_ROOMS_AMOUNT; i++) {
            Room room = rooms[i];
            Point currentPoint = new Point(room.getTopLeft().x, room.getTopLeft().y);

            for (int y = currentPoint.y; y <= room.getBottomLeft().y; y++) {
                currentPoint.setLocation(room.getTopLeft().x, y);
                for (int x = currentPoint.x; x <= room.getTopRight().x; x++) {
                    currentPoint.setLocation(x, y);
                    if (currentPoint.y == room.getTopLeft().y) {
                        setTile(currentPoint.x, currentPoint.y, Tile.WALL);
                    } else if (currentPoint.y == room.getBottomRight().y) {
                        setTile(currentPoint.x, currentPoint.y, Tile.WALL);
                    } else if (currentPoint.x == room.getTopLeft().x) {
                        setTile(currentPoint.x, currentPoint.y, Tile.WALL);
                    } else if (currentPoint.x == room.getBottomRight().x) {
                        setTile(currentPoint.x, currentPoint.y, Tile.WALL);
                    } else if (currentPoint.x == level.getExitPoint().x && currentPoint.y == level.getExitPoint().y) {
                        setTile(currentPoint.x, currentPoint.y, Tile.EXIT);
                    } else {
                        setTile(currentPoint.x, currentPoint.y, Tile.FLOOR);
                    }
                }
            }
        }
    }

    /**
     * @return List of all variants of edges (corridors between rooms)
     */
    private ArrayList<int[]> generateEdges() {
        ArrayList<int[]> edges = new ArrayList<>();

        int rows = GRID_ROOMS_SIZE;
        int cols = GRID_ROOMS_SIZE;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int current = r * cols + c;
                if (c + 1 < cols) {
                    int right = r * cols + (c + 1);
                    edges.add(new int[]{current, right});
                }
                if (r + 1 < rows) {
                    int down = (r + 1) * cols + c;
                    edges.add(new int[]{current, down});
                }
            }
        }

        return edges;
    }

    /**
     * @param edges List of all edges
     * @return list of pairs of rooms, which connected by corridor
     */
    private List<int[]> calculatePairsOfRooms(ArrayList<int[]> edges) {
        Collections.shuffle(edges);
        DSU dsu = new DSU(MAX_ROOMS_AMOUNT);
        List<int[]> selectedEdges = new ArrayList<>();

        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];
            if (dsu.find(u) != dsu.find(v)) {
                dsu.union(u, v);
                selectedEdges.add(edge);
            }

            if (selectedEdges.size() == MAX_ROOMS_AMOUNT - 1) break;
        }

//        selectedEdges.forEach(edge -> System.out.println("Corridor:" + Arrays.toString(edge)));
        return selectedEdges;
    }

    private Corridor calculateHorizontalCorridor(Room startRoom, Room endRoom) {
        Corridor corridor = new Corridor();
        Random random = new Random();

        int startPointX = startRoom.getTopRight().x;
        int startPointY = random.nextInt(startRoom.getTopRight().y + 1, startRoom.getBottomRight().y - 1);
        int endPointX = endRoom.getTopLeft().x;
        int endPointY = random.nextInt(endRoom.getTopLeft().y + 1, endRoom.getBottomLeft().y - 1);

        int verticalEdgePosition = random.nextInt(startPointX + 1, endPointX);

//        System.out.println("Start Point: " + startPointX + " " + startPointY
//                + " End Point: " + endPointX + " " + endPointY
//                + " vertical: " + verticalEdgePosition);

        for (int x = startPointX; x <= verticalEdgePosition; x++) {
            corridor.addPoint(new Point(x, startPointY));
            setTile(x, startPointY, Tile.CORRIDOR);
//            System.out.println("Point: " + x + " " + startPointY);
        }

        int stepY = endPointY > startPointY ? 1 : -1;
        for (int y = startPointY + stepY; y != endPointY + stepY; y += stepY) {
            corridor.addPoint(new Point(verticalEdgePosition, y));
            setTile(verticalEdgePosition, y, Tile.CORRIDOR);
//            System.out.println("Point: " + verticalEdgePosition + " " + y);
        }

        for (int x = verticalEdgePosition + 1; x <= endPointX; x++) {
            corridor.addPoint(new Point(x, endPointY));
            setTile(x, endPointY, Tile.CORRIDOR);
//            System.out.println("Point: " + x + " " + endPointY);
        }

        setTile(startPointX, startPointY, Tile.DOOR);
        setTile(endPointX, endPointY, Tile.DOOR);

        return corridor;
    }

    private Corridor calculateVerticalCorridor(Room startRoom, Room endRoom) {
        Corridor corridor = new Corridor();
        Random random = new Random();

        int startPointX = random.nextInt(startRoom.getBottomLeft().x + 1, startRoom.getBottomRight().x - 1);
        int startPointY = startRoom.getBottomLeft().y;
        int endPointX = random.nextInt(endRoom.getBottomLeft().x + 1, endRoom.getBottomRight().x - 1);
        int endPointY = endRoom.getTopLeft().y;

        int horizontalEdgePosition = random.nextInt(startPointY + 1, endPointY);

//        System.out.println("Start Point: " + startPointX + " " + startPointY
//                + " End Point: " + endPointX + " " + endPointY
//                + " horizontal: " + horizontalEdgePosition);

        for (int y = startPointY; y <= horizontalEdgePosition; y++) {
            corridor.addPoint(new Point(startPointX, y));
            setTile(startPointX, y, Tile.CORRIDOR);
//            System.out.println("Point: " + startPointX + " " + y);
        }

        int stepX = endPointX > startPointX ? 1 : -1;
        for (int x = startPointX + stepX; x != endPointX + stepX; x += stepX) {
            corridor.addPoint(new Point(x, horizontalEdgePosition));
            setTile(x, horizontalEdgePosition, Tile.CORRIDOR);
//            System.out.println("Point: " + x + " " + horizontalEdgePosition);
        }

        for (int y = horizontalEdgePosition + 1; y <= endPointY; y++) {
            corridor.addPoint(new Point(endPointX, y));
            setTile(endPointX, y, Tile.CORRIDOR);
//            System.out.println("Point: " + endPointX + " " + y);
        }

        setTile(startPointX, startPointY, Tile.DOOR);
        setTile(endPointX, endPointY, Tile.DOOR);

        return corridor;
    }

    private Corridor[] generateCorridors(Room[] rooms) {
        List<int[]> pairsOfRooms = calculatePairsOfRooms(generateEdges());
        Corridor[] corridors = new Corridor[MAX_ROOMS_AMOUNT - 1];

        for (int i = 0; i < pairsOfRooms.size(); i++) {
            int roomIndex1 = pairsOfRooms.get(i)[0];
            int roomIndex2 = pairsOfRooms.get(i)[1];

            boolean isHorizontalNeighbor = (roomIndex2 - roomIndex1) == 1;
//            System.out.println(i + " " + "Corridor:" + roomIndex1 + " "
//                    + roomIndex2 + " " + isHorizontalNeighbor);

            if (isHorizontalNeighbor) {
                corridors[i] = calculateHorizontalCorridor(rooms[roomIndex1], rooms[roomIndex2]);
            } else {
                corridors[i] = calculateVerticalCorridor(rooms[roomIndex1], rooms[roomIndex2]);
            }
        }

        Map<Point, Corridor> entryPoints = new HashMap<>();

        for (Corridor corridor : corridors) {
            entryPoints.put(corridor.getPoints().getFirst(), corridor);
            entryPoints.put(corridor.getPoints().getLast(), corridor);
        }
        level.getExploreService().setEntryPoints(entryPoints);

        return corridors;
    }

    private List<Enemy> generateEnemies(Room[] rooms, Map<Point, Item> itemsMap) {
        List<Enemy> enemies = new ArrayList<>();
        Random random = new Random();
        int roomIndex = 0;

        int numOfEnemies = (int) (5 + (difficulty * 1.1));
        for (int i = 0; i < numOfEnemies; ++i) {
            roomIndex += random.nextInt(1, 3);
            if (roomIndex >= MAX_ROOMS_AMOUNT) roomIndex -= MAX_ROOMS_AMOUNT;
            if (Objects.equals(level.getStartingRoom(), rooms[roomIndex])) {
                if (roomIndex == MAX_ROOMS_AMOUNT - 1) roomIndex -=2 ;
                else roomIndex++;
            }
            Point pos = getRandomSpawnPoint(rooms[roomIndex], itemsMap, enemies);
            enemies.add(EnemyFactory.createRandomEnemy(pos, difficulty));
        }
        return enemies;
    }

    private Point getRandomSpawnPoint(Room room, Map<Point, Item> itemsMap, List<Enemy> enemies) {
        Random random = new Random();
        int attempts = 0;
        while (attempts < 1000) {
            int x = room.getTopLeft().x + random.nextInt(1, room.width);
            int y = room.getTopLeft().y + random.nextInt(1, room.height);
            Point point = new Point(x, y);
            // Проверяем, что на клетке нет другого предмета и нет врага и нет ExitPoint
            if (!itemsMap.containsKey(point) &&
                    !isEnemyAt(point, enemies) &&
                    !point.equals(level.getExitPoint())) {
                return point;
            }
            attempts++;
        }
        throw new RuntimeException("Не удалось найти свободную точку для спауна врага");
    }

    private boolean isEnemyAt(Point point, List<Enemy> enemies) {
        for (Enemy e : enemies) {
            if (e.getPosition().equals(point)) return true;
        }
        return false;
    }
}
