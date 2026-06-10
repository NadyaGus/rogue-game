package org.rogue.domain.service.ExploreService;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class BresenhamLine {

    /**
     * Возвращает список всех точек (клеток) на линии от (x0, y0) до (x1, y1)
     * используя алгоритм Брезенхэма
     */
    public static List<Point> getLine(int x0, int y0, int x1, int y1) {
        List<Point> points = new ArrayList<>();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;

        int err = dx - dy;

        int x = x0;
        int y = y0;

        while (true) {
            points.add(new Point(x, y));

            if (x == x1 && y == y1) {
                break;
            }

            int e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }

            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }

        return points;
    }
}