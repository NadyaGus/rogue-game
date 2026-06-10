package org.rogue.domain.entity.level;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Corridor {
    private final ArrayList<Point> points;

    public Corridor() {
        this.points = new ArrayList<>();
    }

    public void addPoint(Point point) {
        this.points.add(point);
    }

    public List<Point> getPoints() {
        return points;
    }
}
