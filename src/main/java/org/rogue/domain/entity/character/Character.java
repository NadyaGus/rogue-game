package org.rogue.domain.entity.character;

import org.rogue.domain.entity.modifier.Modifier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Базовый класс для всех живых существ (игрок, противники).
 */
public abstract class Character {
    private List<Modifier> modifiers;
    protected int maxHealth;
    protected int health;
    protected int strength;
    protected int agility;
    private Point position;
    private final String name;

    public Character(int maxHealth, int strength, int agility, Point position, String name) {
        this.modifiers = new ArrayList<>();
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.strength = strength;
        this.agility = agility;
        this.position = position;
        this.name = name;
    }


    public void addModifier(Modifier modifier) {
        modifiers.add(modifier);
    }

    public void removeModifier(Modifier modifier) {
        modifiers.remove(modifier);
    }

    public List<Modifier> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<Modifier> modifiers) {
        this.modifiers = modifiers;
    }

    public int attack() {
        return getStrength();
    }

    /**
     * Получение урона — только изменение здоровья, без логики эффектов.
     * Если здоровье становится меньше 0, оно устанавливается в 0.
     *
     * @param damage количество урона, которое будет вычтено из здоровья
     */
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }

    public boolean isAlive() {
        return health > 0;
    }


    public int getMaxHealth() {
        return maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public int getStrength() {
        return strength;
    }

    public int getAgility() {
        return agility;
    }

    public Point getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }


    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setHealth(int health) {
        this.health = Math.min(Math.max(health, 0), maxHealth);
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public void setPosition(Point position) {
        this.position = position;
    }


}
