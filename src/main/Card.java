package main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fileio.CardInput;

import java.util.ArrayList;
import java.util.LinkedList;

public abstract class Card {

    private String description;

    private ArrayList<String> colors;

    private String name = null;

    private int mana = -1;

    public Card (Card cardToCopy) {
        this.mana = cardToCopy.mana;
        this.colors = new ArrayList<>(cardToCopy.getColors());
        this.description = cardToCopy.description;
        this.name = cardToCopy.name;
    }

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

    public static int correspondingRow(Card card) {
        if (card.getName().equals("Goliath") || card.getName().equals("Warden") ||
                card.getName().equals("The Ripper") || card.getName().equals("Miraj")) {
            // front row
            return 1;
        } else if (card.getName().equals("Disciple") || card.getName().equals("Sentinel") ||
                card.getName().equals("Berserker") || card.getName().equals("The Cursed One")) {
            // back row
            return -1;
        } else {
            // environment card
            return 0;
        }
    }
}
class MinionCard extends Card {
    private int health;
    private int attackDamage;
    @JsonIgnore
    private boolean isFrozen;
    @JsonIgnore
    private boolean ableToAttack;
    @JsonIgnore
    private boolean isTank;

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

    public MinionCard(MinionCard minionCardToCopy) {
        super(minionCardToCopy);
        this.health = minionCardToCopy.health;
        this.isTank = minionCardToCopy.isTank;
        this.ableToAttack = minionCardToCopy.isAbleToAttack();
        this.attackDamage = minionCardToCopy.attackDamage;
        this.isFrozen = minionCardToCopy.isFrozen;
    }

    public void loseHealth(int lostHealth) {
        this.health = this.health - lostHealth;
    }

    @JsonIgnore
    public boolean isDead() {
        if (this.health <= 0)
            return true;
        else
            return false;
    }
    @JsonIgnore
    public boolean isTank() {
        return isTank;
    }
    @JsonIgnore
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
    @JsonIgnore
    public boolean isFrozen() {
        return isFrozen;
    }
    @JsonIgnore
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

    public static LinkedList<MinionCard> getMinionCardListCopy(LinkedList<MinionCard> initialMinionCardList) {
        LinkedList<MinionCard> minionCardListCopy = new LinkedList<>();

        for (MinionCard minionCard : initialMinionCardList) {
            MinionCard minionCardCopy = new MinionCard(minionCard);
            minionCardListCopy.addLast(minionCardCopy);
        }

        return minionCardListCopy;
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
        LinkedList<MinionCard> deadCards = new LinkedList<>();
        // deal damage to all minions on row, marks dead minions
        for (MinionCard card : cards) {
            card.loseHealth(1);
            if (card.isDead()) {
                deadCards.add(card);
            }
        }
        // remove killed minion cards
        for (MinionCard deadCard : deadCards) {
            cards.removeFirstOccurrence(deadCard);
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

    int heartHoundEffect(LinkedList<MinionCard> cards, ArrayList<LinkedList<MinionCard>> table, int playerTurn) {
        // initially null; if row is empty, will stay null
        MinionCard maxHealthMinionCard = null;

        // get max health minion
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

        // first player used card
        if (playerTurn == 1 && MinionCard.correspondingRow(maxHealthMinionCard) == 1) {
            // destination row is full
            if (table.get(2).size() == 5)
                return 1;
            // remove the card at index of max health minion, place it at row of current player - other cases = similar
            table.get(2).addLast(table.get(1).remove(table.get(1).indexOf(maxHealthMinionCard)));
        } else if (playerTurn == 1 && MinionCard.correspondingRow(maxHealthMinionCard) == -1) {
            if (table.get(3).size() == 5)
                return 1;
            table.get(3).addLast(table.get(0).remove(table.get(0).indexOf(maxHealthMinionCard)));
        // second player used card
        } else if (playerTurn == 2 && MinionCard.correspondingRow(maxHealthMinionCard) == 1) {
            if (table.get(1).size() == 5)
                return 1;
            table.get(1).addLast(table.get(2).remove(table.get(2).indexOf(maxHealthMinionCard)));
        } else {
            if (table.get(0).size() == 5) {
                return 1;
            }
            table.get(0).addLast(table.get(3).remove(table.get(3).indexOf(maxHealthMinionCard)));
        }
        // if it got here, the card was moved accordingly, no error
        return 0;
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
    private int health;

    public HeroCard() {

    }
    public HeroCard(CardInput rawCard) {
        super(rawCard.getName(), rawCard.getMana(), rawCard.getDescription(), rawCard.getColors());
        this.setHealth(30);
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public String toString() {
        return "{"
                +  "mana="
                + this.getMana()
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