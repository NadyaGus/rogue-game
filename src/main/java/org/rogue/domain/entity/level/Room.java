package org.rogue.domain.entity.level;

import java.awt.*;

public class Room {
    private final Point topLeft;
    private final Point bottomRight;
    private final Point bottomLeft;
    private final Point topRight;
    public final int width;
    public final int height;

    public Room(Point topLeft, Point bottomRight) {
        this.topLeft = topLeft;
        this.topRight = new Point(bottomRight.x, topLeft.y);
        this.bottomLeft = new Point(topLeft.x, bottomRight.y);
        this.bottomRight = bottomRight;
        this.width = topRight.x - topLeft.x;
        this.height = bottomRight.y - topRight.y;
    }

    public Point getTopLeft() {
        return topLeft;
    }

    public Point getTopRight() {
        return topRight;
    }

    public Point getBottomLeft() {
        return bottomLeft;
    }

    public Point getBottomRight() {
        return bottomRight;
    }

    public boolean containsPoint(Point point) {
        return point.x >= topLeft.x && point.x <= topRight.x &&
                point.y >= topLeft.y && point.y <= bottomLeft.y;
    }

}
