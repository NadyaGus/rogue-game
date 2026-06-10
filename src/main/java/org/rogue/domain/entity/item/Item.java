package org.rogue.domain.entity.item;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Item {
    private static final AtomicInteger nextId = new AtomicInteger(1);
    private final int id;
    private final int difficulty;
    private Point position;

    public Item(int difficulty) {
        this.difficulty = difficulty;
        this.id = nextId.getAndIncrement();
    }

    public int getId() {
        return id;
    }

    public abstract ItemType getType();

    public abstract String getName();

    public int getDifficulty() {
        return difficulty;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }
}
