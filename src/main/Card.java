package main;

import java.util.ArrayList;

public interface Card {
    // default coordinates - card is not on the table
    int coordinateX = -1;
    int coordinateY = -1;
}

abstract class MinionCard implements Card {
    int mana = 0;
    int health = 0;
    int attackDamage = 0;
    String description = null;
    String colors = null;
    String name = null;

    boolean isFrozen = false;

    boolean isTank = false;

    boolean ableToAttack = true;

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColors() {
        return colors;
    }

    public void setColors(String colors) {
        this.colors = colors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }

    public boolean isTank() {
        return isTank;
    }

    public void setTank(boolean tank) {
        isTank = tank;
    }

    public boolean isAbleToAttack() {
        return ableToAttack;
    }

    public void setAbleToAttack(boolean ableToAttack) {
        this.ableToAttack = ableToAttack;
    }

    public boolean isDead() {
        if (this.getHealth() <= 0)
            return true;
        else
            return false;
    }

    public void attackMinion(MinionCard attackedCard) {
        attackedCard.setHealth(attackedCard.getHealth() - this.getAttackDamage());
        this.setAbleToAttack(false);
    }
}

abstract class StandardMinionCard extends MinionCard {

}

class Sentinel extends StandardMinionCard {
}

class Berserker extends StandardMinionCard {

}

class Goliath extends StandardMinionCard {

}

class Warden extends StandardMinionCard {

}

abstract class SpecialMinionCard extends MinionCard {

}

class Ripper extends SpecialMinionCard {

}

class Miraj extends SpecialMinionCard {

}

class CursedOne extends SpecialMinionCard {

}

class Disciple extends SpecialMinionCard {

}