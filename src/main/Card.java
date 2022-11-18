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
    public static LinkedList<Card> getCardListCopy(LinkedList<Card> initialCardList) {
        LinkedList<Card> cardListCopy = new LinkedList<>();
        // make a copy of each card, depending on its type
        for (Card card : initialCardList) {
            if (Card.correspondingRow(card) != 0) {
                // is minion card
                MinionCard minionCardCopy = new MinionCard((MinionCard)card);
                cardListCopy.add(minionCardCopy);
            } else {
                if (card.getName().equals("Winterfell")) {
                    // is winterfell card
                    WinterfellEnvironmentCard cardToCopy = new WinterfellEnvironmentCard((WinterfellEnvironmentCard) card);
                    cardListCopy.add(cardToCopy);
                } else if (card.getName().equals("Firestorm")) {
                    // is firestorm card
                    FirestormEnvironmentCard cardToCopy = new FirestormEnvironmentCard((FirestormEnvironmentCard) card);
                    cardListCopy.add(cardToCopy);
                } else {
                    // is heart hound card
                    HeartHoundEnvironmentCard cardToCopy = new HeartHoundEnvironmentCard((HeartHoundEnvironmentCard) card);
                    cardListCopy.add(cardToCopy);
                }
            }
        }
        return cardListCopy;
    }

    public static LinkedList<MinionCard> getMinionCardListCopy(LinkedList<MinionCard> initialCardList) {
        LinkedList<MinionCard> cardListCopy = new LinkedList<>();
        // make copy for each card
        for (MinionCard card : initialCardList) {
            MinionCard minionCardCopy = new MinionCard(card);
            cardListCopy.add(minionCardCopy);
        }
        return cardListCopy;
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
        this.isFrozen = false;
        this.ableToAttack = true;
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
}

abstract class SpecialMinionCard extends MinionCard {
    public SpecialMinionCard(CardInput rawCard) { super(rawCard); }

    void specialAbility(MinionCard attackedCard) {
    }
}

class Ripper extends SpecialMinionCard {
    public Ripper(CardInput rawCard) {
        super(rawCard);
    }
    @Override
    void specialAbility(MinionCard attackedCard) {
        // lower attack damage with 2 points or until it reaches 0
        int prevAttackDamage = attackedCard.getAttackDamage();
        attackedCard.setAttackDamage(prevAttackDamage - 2);
        if (attackedCard.getAttackDamage() < 0)
            attackedCard.setAttackDamage(0);
    }

}

class Miraj extends SpecialMinionCard {
    public Miraj(CardInput rawCard) {
        super(rawCard);
    }
    @Override
    void specialAbility(MinionCard attackedCard) {
        // switch health between Miraj and attacked card
        int swapAux = this  .getHealth();
        this.setHealth(attackedCard.getHealth());
        attackedCard.setHealth(swapAux);
    }
}

class CursedOne extends SpecialMinionCard {
    public CursedOne(CardInput rawCard) {
        super(rawCard);
    }
    @Override
    void specialAbility(MinionCard attackedCard) {
        // switch the attack and health of an enemy minion
        int swapAux = attackedCard.getHealth();
        attackedCard.setHealth(attackedCard.getAttackDamage());
        attackedCard.setAttackDamage(swapAux);
    }
}
class Disciple extends SpecialMinionCard {
    public Disciple(CardInput rawCard) {
        super(rawCard);
    }
    @Override
    void specialAbility(MinionCard attackedCard) {
        // heal allied card with 2 hp
        int prevHealth = attackedCard.getHealth();
        attackedCard.setHealth(prevHealth + 2);
    }
}

class FirestormEnvironmentCard extends Card {
    public FirestormEnvironmentCard(CardInput rawCard) {
        super(rawCard.getName(), rawCard.getMana(), rawCard.getDescription(), rawCard.getColors());
    }

    public FirestormEnvironmentCard(FirestormEnvironmentCard firestormEnvironmentCard) {
        super(firestormEnvironmentCard);
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

    public WinterfellEnvironmentCard(WinterfellEnvironmentCard winterfellEnvironmentCard) {
        super(winterfellEnvironmentCard);
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

    public HeartHoundEnvironmentCard(HeartHoundEnvironmentCard heartHoundEnvironmentCard) {
        super(heartHoundEnvironmentCard);
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

    @JsonIgnore
    private boolean isDead;
    @JsonIgnore
    private boolean ableToAttack;

    public HeroCard() {

    }
    public HeroCard(CardInput rawCard) {
        super(rawCard.getName(), rawCard.getMana(), rawCard.getDescription(), rawCard.getColors());
        this.setHealth(30);
    }

    public HeroCard(HeroCard heroCardToCopy) {
        super(heroCardToCopy.getName(), heroCardToCopy.getMana(), heroCardToCopy.getDescription(),
                heroCardToCopy.getColors());
        this.setHealth(heroCardToCopy.health);
    }
    public void loseHealth(int lostHealth) {
        int prevHealth = this.health;
        this.setHealth(prevHealth - lostHealth);
    }

    public void useHeroAbility(LinkedList<MinionCard> affectedRow) {

    }

    @JsonIgnore
    public boolean isDead() {
        if (this.health <= 0) {
            return true;
        } else {
            return false;
        }
    }
    @JsonIgnore
    public boolean isAbleToAttack() {
        return ableToAttack;
    }
    @JsonIgnore
    public void setAbleToAttack(boolean ableToAttack) {
        this.ableToAttack = ableToAttack;
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
    @Override
    public void useHeroAbility(LinkedList<MinionCard> affectedRow) {
        // freeze card with the highest attack damage on row

        // initially null; if row is empty, will stay null
        MinionCard maxAttackMinionCard = null;

        // get max attack damage minion
        if (affectedRow.size() == 1) {
            // if only one card, it is max attack damage card
            maxAttackMinionCard = affectedRow.getFirst();
        } else if (affectedRow.size() > 1) {
            // if multiple, get max attack damage card
            maxAttackMinionCard = affectedRow.getFirst();

            for (int i = 1; i < affectedRow.size(); i++)
                if (affectedRow.get(i).getHealth() > maxAttackMinionCard.getAttackDamage())
                    maxAttackMinionCard = affectedRow.get(i);
        }

        // set card to frozen
        maxAttackMinionCard.setFrozen(true);
    }
}

class EmpressThorina extends HeroCard {
    public EmpressThorina(CardInput rawCard) {
        super(rawCard);
    }

    @Override
    public void useHeroAbility(LinkedList<MinionCard> affectedRow) {
        // destroys card with most hp on row
        // initially null; if row is empty, will stay null
        MinionCard maxHealthMinionCard = null;

        // get max health minion
        if (affectedRow.size() == 1) {
            // if only one card, it is max hp card
            maxHealthMinionCard = affectedRow.getFirst();
        } else if (affectedRow.size() > 1) {
            // if multiple, get max hp card
            maxHealthMinionCard = affectedRow.getFirst();

            for (int i = 1; i < affectedRow.size(); i++)
                if (affectedRow.get(i).getHealth() > maxHealthMinionCard.getHealth())
                    maxHealthMinionCard = affectedRow.get(i);
        }

        // kill minion
        affectedRow.removeFirstOccurrence(maxHealthMinionCard);
    }
}

class KingMudface extends HeroCard {
    public KingMudface(CardInput rawCard) {
        super(rawCard);
    }

    @Override
    public void useHeroAbility(LinkedList<MinionCard> affectedRow) {
        // increase health with 1 point for each card on row
        for (MinionCard card : affectedRow) {
            card.setHealth(card.getHealth() + 1);
        }
    }
}

class GeneralKocioraw extends HeroCard {
    public GeneralKocioraw(CardInput rawCard) {
        super(rawCard);
    }

    @Override
    public void useHeroAbility(LinkedList<MinionCard> affectedRow) {
        // increase attack damage with 1 point for each card on row
        for (MinionCard card : affectedRow) {
            card.setAttackDamage(card.getAttackDamage() + 1);
        }
    }
}