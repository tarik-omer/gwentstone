package main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import fileio.CardInput;

import java.util.ArrayList;
import java.util.LinkedList;

//@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Card {

    private String description;

    private ArrayList<String> colors;

    private String name = null;

    private int mana = -1;

    Card (String name, int mana, String description, ArrayList<String> colors) {
        this.name = name;
        this.colors = new ArrayList<>(colors);
        this.mana = mana;
        this.description = description;
    }

    public Card() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public void setColors(ArrayList<String> colors) {
        this.colors = new ArrayList<>(colors);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

}
//@JsonInclude(JsonInclude.Include.NON_NULL)
class MinionCard extends Card {
    private int health;

    private int attackDamage;

    private boolean isFrozen = false;

    private boolean ableToAttack = true;

    private boolean isTank = false;

    public MinionCard() {

    }

    public MinionCard(CardInput rawCard) {
        super(rawCard.getName(), rawCard.getMana(), rawCard.getDescription(), rawCard.getColors());
        this.health = rawCard.getHealth();
        this.attackDamage = rawCard.getAttackDamage();
        // when needed, consider minion tank
        if (this.getName().equals("Warden") || this.getName().equals("Goliath")) {
            isTank = true;
        }
    }

    public boolean isDead() {
        if (this.health <= 0)
            return true;
        else
            return false;
    }

    public boolean isTank() {
        return isTank;
    }

    public void setTank(boolean tank) {
        isTank = tank;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public void attackMinion(MinionCard attackedCard) {
        attackedCard.health = attackedCard.health - this.getAttackDamage();
        this.ableToAttack = false;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }

    public boolean isAbleToAttack() {
        return ableToAttack;
    }

    public void setAbleToAttack(boolean ableToAttack) {
        this.ableToAttack = ableToAttack;
    }

    @Override
    public String toString() {
        return "{"
                +  "mana="
                + this.getMana()
                +  ", attackDamage="
                + attackDamage
                + ", health="
                + health
                +  ", description='"
                + this.getDescription()
                + '\''
                + ", colors="
                + this.getColors()
                + ", name='"
                +  ""
                + this.getName()
                + '\''
                + '}';
    }

}

class Ripper extends MinionCard {
    public Ripper(CardInput rawCard) {
        super(rawCard);
    }

    void weakKneesAbility() {

    }

}

class Miraj extends MinionCard {
    public Miraj(CardInput rawCard) {
        super(rawCard);
    }
    void skyjackAbility() {

    }
}

class CursedOne extends MinionCard {
    public CursedOne(CardInput rawCard) {
        super(rawCard);
    }
    void shapeshiftAbility() {

    }
}
class Disciple extends MinionCard {
    public Disciple(CardInput rawCard) {
        super(rawCard);
    }
    void godsPlanAbility() {

    }
}

class FirestormEnvironmentCard extends Card {
    public FirestormEnvironmentCard(CardInput rawCard) {
        super(rawCard.getName(), rawCard.getMana(), rawCard.getDescription(), rawCard.getColors());
    }

    void firestormEffect(LinkedList<MinionCard> cards) {
        // deal damage to all cards
        for (MinionCard card : cards) {
            card.setHealth(card.getHealth() - 1);
        }
    }
    @Override
    public String toString() {
        return "{"
                +  "mana="
                + this.getMana()
                +  ", description='"
                + this.getDescription()
                + '\''
                + ", colors="
                + this.getColors()
                + ", name='"
                +  ""
                + this.getName()
                + '\''
                + '}';
    }
}

class WinterfellEnvironmentCard extends Card {
    public WinterfellEnvironmentCard(CardInput rawCard) {
        super(rawCard.getName(), rawCard.getMana(), rawCard.getDescription(), rawCard.getColors());
    }

    void winterfellEffect(LinkedList<MinionCard> cards) {
        // take ability to attack (will be reset next round)
        for (MinionCard card : cards) {
            card.setFrozen(true);
            card.setAbleToAttack(false);
        }
    }
    @Override
    public String toString() {
        return "{"
                +  "mana="
                + this.getMana()
                +  ", description='"
                + this.getDescription()
                + '\''
                + ", colors="
                + this.getColors()
                + ", name='"
                +  ""
                + this.getName()
                + '\''
                + '}';
    }
}

class HeartHoundEnvironmentCard extends Card {
    public HeartHoundEnvironmentCard(CardInput rawCard) {
        super(rawCard.getName(), rawCard.getMana(), rawCard.getDescription(), rawCard.getColors());
    }

    MinionCard heartHoundEffect(LinkedList<MinionCard> cards) {
        // initially null; if row is empty, will return null
        MinionCard maxHealthMinionCard = null;

        if (cards.size() == 1) {
            // if only one card, it is max hp card
            maxHealthMinionCard = cards.getFirst();
        } else if (cards.size() > 1) {
            // if multiple, get max hp card
            maxHealthMinionCard = cards.getFirst();

            for (int i = 1; i < cards.size(); i++)
                if (cards.get(i).getHealth() > maxHealthMinionCard.getHealth())
                    maxHealthMinionCard = cards.get(i);
        }

        return maxHealthMinionCard;
    }

    @Override
    public String toString() {
        return "{"
                +  "mana="
                + this.getMana()
                +  ", description='"
                + this.getDescription()
                + '\''
                + ", colors="
                + this.getColors()
                + ", name='"
                +  ""
                + this.getName()
                + '\''
                + '}';
    }
}

class HeroCard extends Card {
    private int health = 30;

    private int attackDamage;

    public HeroCard() {

    }
    public HeroCard(CardInput rawCard) {
        super(rawCard.getName(), rawCard.getMana(), rawCard.getDescription(), rawCard.getColors());
        this.attackDamage = rawCard.getAttackDamage();
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

    @Override
    public String toString() {
        return "{"
                +  "mana="
                + this.getMana()
                +  ", attackDamage="
                + attackDamage
                + ", health="
                + health
                +  ", description='"
                + this.getDescription()
                + '\''
                + ", colors="
                + this.getColors()
                + ", name='"
                +  ""
                + this.getName()
                + '\''
                + '}';
    }
}

class LordRoyce extends HeroCard {
    public LordRoyce(CardInput rawCard) {
        super(rawCard);
    }

    void subZeroAbility() {

    }
}

class EmpressThorina extends HeroCard {
    public EmpressThorina(CardInput rawCard) {
        super(rawCard);
    }

    void lowBlowAbility() {

    }
}

class KingMudface extends HeroCard {
    public KingMudface(CardInput rawCard) {
        super(rawCard);
    }

    void earthBornAbility() {

    }
}

class GeneralKocioraw extends HeroCard {
    public GeneralKocioraw(CardInput rawCard) {
        super(rawCard);
    }

    void bloodThirstAbility() {

    }
}