package org.rogue.domain.entity.item;

public class Gold extends Item {
    private final int value;

    // TODO: сделать рассчет стоимости голды от сложности (?) или убрать наследование от итема, т.к это не расходник
    public Gold(int difficulty) {
        super(difficulty);
        this.value = difficulty;
    }

    public int getValue() {
        return value;
    }

    @Override
    public ItemType getType() {
        return ItemType.GOLD;
    }

    @Override
    public String getName() {
        return "Gold";
    }

}
