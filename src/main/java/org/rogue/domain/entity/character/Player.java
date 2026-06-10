/*
- Персонаж:
  + максимальный уровень здоровья,
  + здоровье,
  + ловкость,
  + сила,
  + текущее оружие;

### Логика персонажа
- Характеристика здоровья персонажа должна показывать его текущий уровень здоровья, и когда здоровье персонажа достигает 0
    или становится меньше 0, игра должна закончиться.
- Характеристика максимального уровня здоровья должна показывать максимальный уровень здоровья персонажа, который может
    быть восстановлен путем употребления еды.
- Характеристика ловкости должна участвовать в формуле вычисления вероятности попадания противников по персонажу
    и персонажа по противникам.
- Характеристика силы должна определять базовый урон, наносимый персонажем без оружия, а также должна участвовать
    в формуле вычисления урона при использовании оружия.
- За победу над противником персонаж получает количество сокровищ, зависящее от сложности противника.
- Персонаж может поднимать предметы и складывать в свой рюкзак, а затем использовать их.
- Каждый предмет при использовании может временно или постоянно изменять одну из характеристик персонажа.
- Достигнув выхода из уровня, персонаж автоматически попадает на следующий уровень.
 */

package org.rogue.domain.entity.character;


import org.rogue.domain.entity.game.Backpack;
import org.rogue.domain.entity.item.Weapon;

import java.awt.*;


public class Player extends Character {
    private static final int BASE_HEALTH = 100;
    private static final int BASE_STRENGTH = 14;
    private static final int BASE_AGILITY = 20;

    private Weapon currentWeapon;
    private final Backpack backpack;  // FIXME: backpack есть в Player и в GameSession

    public Player(Point position) {
        super(BASE_HEALTH, BASE_STRENGTH, BASE_AGILITY, position, "PLAYER");
        this.currentWeapon = null;
        this.backpack = new Backpack(); // FIXME: backpack есть в Player и в GameSession
    }


    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    public Backpack getBackpack() {
        return backpack;
    } // FIXME: backpack есть в Player и в GameSession

    public void setCurrentWeapon(Weapon weapon) {
        this.currentWeapon = weapon;
    }

    @Override
    public int attack() {
        int damage = getStrength();
        if (currentWeapon != null) {
            damage += currentWeapon.getSubtype().getWeaponStrength();
        }
        return damage;
    }

    public void increaseStats(StatType stats, int value) {
        switch (stats) {
            case MAX_HP -> {
                maxHealth += value;
                health += value;
            }
            case AGILITY -> agility += value;
            case STRENGTH -> strength += value;
            case HP -> health = Math.min(health + value, maxHealth);
        }
    }

    public void decreaseStats(StatType stats, int value) {
        switch (stats) {
            case MAX_HP -> {
                maxHealth -= value;
                int reduction = value <= 0 ? 1 : value;
                health = Math.max(health - reduction, 1);
            }
            case AGILITY -> agility -= value;
            case STRENGTH -> strength -= value;
        }
    }
}









