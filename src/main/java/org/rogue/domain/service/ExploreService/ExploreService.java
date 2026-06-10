package org.rogue.domain.service.ExploreService;

import org.rogue.domain.entity.level.Corridor;
import org.rogue.domain.entity.level.Room;
import org.rogue.domain.entity.level.Tile;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExploreService {
    private ExploredTiles[][] exploredTiles;
    private VisibleTiles[][] visibleTiles;
    private Tile[][] levelTiles;

    private Map<Point, Corridor> entryPointsCorridors = new HashMap<>();

    public ExploreService(Tile[][] tiles) {
        this.levelTiles = tiles;
        initTiles(tiles.length, tiles[0].length);
    }

    private void initTiles(int width, int height) {
        this.exploredTiles = new ExploredTiles[width][height];
        this.visibleTiles = new VisibleTiles[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                this.exploredTiles[x][y] = ExploredTiles.UNEXPLORED;
                this.visibleTiles[x][y] = VisibleTiles.INVISIBLE;
            }
        }
    }

    public void setTile(int x, int y, ExploredTiles tile) {
        this.exploredTiles[x][y] = tile;
    }

    public ExploredTiles[][] getExploredTiles() {
        return exploredTiles;
    }

    public VisibleTiles[][] getVisibleTiles() {
        return visibleTiles;
    }

    public void setEntryPoints(Map<Point, Corridor> entryPoints) {
        this.entryPointsCorridors = entryPoints;
    }

    public void exploreCorridor(int x, int y) {
        Point exploredPoint = new Point(x, y);
        Corridor corridor = entryPointsCorridors.get(exploredPoint);

        if (corridor != null) {
            for (Point point : corridor.getPoints()) {
                setTile(point.x, point.y, ExploredTiles.EXPLORED);
            }
        }
    }

    public void exploreRoom(int x, int y, Room[] rooms) {
        Room exploredRoom = null;
        exploredRoom = findRoom(x, y, rooms);

        if (exploredRoom == null) return;

        for (int roomX = exploredRoom.getBottomLeft().x; roomX < exploredRoom.getBottomRight().x + 1; roomX++) {
            for (int roomY = exploredRoom.getTopLeft().y; roomY < exploredRoom.getBottomRight().y + 1; roomY++) {
                setTile(roomX, roomY, ExploredTiles.EXPLORED);
            }
        }
    }

    public Room findRoom(int x, int y, Room[] rooms) {
        for (Room room : rooms) {
            if (x > room.getTopLeft().x && x < room.getTopRight().x) {
                if (y > room.getTopRight().y && y < room.getBottomRight().y) {
                    return room;
                }
            }
        }

        return null;
    }

    public Corridor findCorridor(int x, int y, Corridor[] corridors) {
        for (Corridor corridor : corridors) {
            if (corridor.getPoints().contains(new Point(x, y))) return corridor;
        }

        return null;
    }

    public void calculateVisibleCells(int playerX, int playerY,
                                      Room room, Room[] rooms, Corridor corridor, Corridor[] corridors) {
        visibleTiles[playerX][playerY] = VisibleTiles.VISIBLE;

        for (int x = 0; x < visibleTiles.length; x++) {
            for (int y = 0; y < visibleTiles[0].length; y++) {
                visibleTiles[x][y] = VisibleTiles.INVISIBLE;
            }
        }

        for (int x = 0; x < visibleTiles.length; x++) {
            for (int y = 0; y < visibleTiles[0].length; y++) {
                if (x == playerX && y == playerY) continue;
                List<Point> line = BresenhamLine.getLine(playerX, playerY, x, y);

                for (int i = 0; i < line.size(); i++) {
                    Point p = line.get(i);

                    if (levelTiles[p.x][p.y] == Tile.EMPTY || levelTiles[p.x][p.y] == Tile.WALL) break;
                    if (room != null) {
                        if (findRoom(p.x, p.y, rooms) != null && findRoom(p.x, p.y, rooms) != room) break;
                    } else {
                        if (i > 10) break;
                        if (findCorridor(p.x, p.y, corridors) != null && findCorridor(p.x, p.y, corridors) != corridor) break;
                    }

                    visibleTiles[p.x][p.y] = VisibleTiles.VISIBLE;
                }
            }
        }
    }
}
