package main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fileio.CardInput;

import java.util.ArrayList;
import java.util.LinkedList;

public abstract class Card {

    private String description;

    private ArrayList<String> colors;

    private String name = null;

    private int mana = -1;

    public Card(final Card cardToCopy) {
        this.mana = cardToCopy.mana;
        this.colors = new ArrayList<>(cardToCopy.getColors());
        this.description = cardToCopy.description;
        this.name = cardToCopy.name;
    }

    Card(final String name, final int mana, final String description,
         final ArrayList<String> colors) {
        this.name = name;
        this.colors = new ArrayList<>(colors);
        this.mana = mana;
        this.description = description;
    }

    public Card() {
    }

    /**
     * Returns the description of the card.
     * @return      card description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the card.
     * @param description   card description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Returns the colors of the card.
     * @return      colors of the card
     */
    public ArrayList<String> getColors() {
        return colors;
    }

    /**
     * Sets colors of the card.
     * @param colors    colors of the card.
     */
    public void setColors(final ArrayList<String> colors) {
        this.colors = new ArrayList<>(colors);
    }

    /**
     * Returns card name.
     * @return      card name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets card name.
     * @param name  card name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns mana cost of the card.
     * @return      mana cost
     */
    public int getMana() {
        return mana;
    }

    /**
     * Sets mana cost of the card.
     * @param mana  mana cost
     */
    public void setMana(final int mana) {
        this.mana = mana;
    }

    /**
     * Returns whether a card needs to be placed on the front row, back row or is
     * an environment card.
     * @param card  card to be reviewed
     * @return      corresponding row: 1 = row card, -1 = back row card, 0 = environment card
     */
    public static int correspondingRow(final Card card) {
        if (card.getName().equals("Goliath") || card.getName().equals("Warden")
                || card.getName().equals("The Ripper")
                || card.getName().equals("Miraj")) {
            // front row
            return 1;
        } else if (card.getName().equals("Disciple") || card.getName().equals("Sentinel")
                || card.getName().equals("Berserker")
                || card.getName().equals("The Cursed One")) {
            // back row
            return -1;
        } else {
            // environment card
            return 0;
        }
    }

    /**
     * Returns a deep copied LinkedList of a given card LinkedList. Each card is
     * deep-copied, then added to a new list.
     * @param initialCardList   Card LinkedList to be copied
     * @return                  Card LinkedList deep copy
     */
    public static LinkedList<Card> getCardListCopy(final LinkedList<Card> initialCardList) {
        LinkedList<Card> cardListCopy = new LinkedList<>();
        // make a copy of each card, depending on its type
        for (Card card : initialCardList) {
            if (Card.correspondingRow(card) != 0) {
                // is minion card
                MinionCard minionCardCopy = new MinionCard((MinionCard) card);
                cardListCopy.add(minionCardCopy);
            } else {
                if (card.getName().equals("Winterfell")) {
                    // is winterfell card
                    WinterfellEnvironmentCard cardToCopy;
                    cardToCopy = new WinterfellEnvironmentCard((WinterfellEnvironmentCard) card);
                    cardListCopy.add(cardToCopy);
                } else if (card.getName().equals("Firestorm")) {
                    // is firestorm card
                    FirestormEnvironmentCard cardToCopy;
                    cardToCopy = new FirestormEnvironmentCard((FirestormEnvironmentCard) card);
                    cardListCopy.add(cardToCopy);
                } else {
                    // is heart hound card
                    HeartHoundEnvironmentCard cardToCopy;
                    cardToCopy = new HeartHoundEnvironmentCard((HeartHoundEnvironmentCard) card);
                    cardListCopy.add(cardToCopy);
                }
            }
        }
        return cardListCopy;
    }

    /**
     * Returns a deep copied LinkedList of a given minion card LinkedList. Each card is
     * deep-copied, then added to a new list.
     * @param initialCardList   MinionCard LinkedList to be copied
     * @return                  MinionCard LinkedList deep copy
     */
    public static LinkedList<MinionCard> getMinionCardListCopy(final LinkedList<MinionCard>
                                                                       initialCardList) {
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

    MinionCard() {

    }

    MinionCard(final CardInput rawCard) {
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

    MinionCard(final MinionCard minionCardToCopy) {
        super(minionCardToCopy);
        this.health = minionCardToCopy.health;
        this.isTank = minionCardToCopy.isTank;
        this.ableToAttack = minionCardToCopy.isAbleToAttack();
        this.attackDamage = minionCardToCopy.attackDamage;
        this.isFrozen = minionCardToCopy.isFrozen;
    }

    public void loseHealth(final int lostHealth) {
        this.health = this.health - lostHealth;
    }

    @JsonIgnore
    public boolean isDead() {
        return this.health <= 0;
    }

    @JsonIgnore
    public boolean isTank() {
        return isTank;
    }

    @JsonIgnore
    public void setTank(final boolean tank) {
        isTank = tank;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(final int health) {
        this.health = health;
    }

    @JsonIgnore
    public boolean isFrozen() {
        return isFrozen;
    }

    @JsonIgnore
    public void setFrozen(final boolean frozen) {
        isFrozen = frozen;
    }

    public boolean isAbleToAttack() {
        return ableToAttack;
    }

    public void setAbleToAttack(final boolean ableToAttack) {
        this.ableToAttack = ableToAttack;
    }

    @Override
    public String toString() {
        return "{"
                + "mana="
                + this.getMana()
                + ", attackDamage="
                + attackDamage
                + ", health="
                + health
                + ", description='"
                + this.getDescription()
                + '\''
                + ", colors="
                + this.getColors()
                + ", name='"
                + ""
                + this.getName()
                + '\''
                + '}';
    }
}

abstract class SpecialMinionCard extends MinionCard {
    SpecialMinionCard(final CardInput rawCard) {
        super(rawCard);
    }

    void specialAbility(final MinionCard attackedCard) {
    }
}

class Ripper extends SpecialMinionCard {
    Ripper(final CardInput rawCard) {
        super(rawCard);
    }

    @Override
    void specialAbility(final MinionCard attackedCard) {
        // lower attack damage with 2 points or until it reaches 0
        int prevAttackDamage = attackedCard.getAttackDamage();
        attackedCard.setAttackDamage(prevAttackDamage - 2);
        if (attackedCard.getAttackDamage() < 0) {
            attackedCard.setAttackDamage(0);
        }
    }

}

class Miraj extends SpecialMinionCard {
    Miraj(final CardInput rawCard) {
        super(rawCard);
    }

    @Override
    void specialAbility(final MinionCard attackedCard) {
        // switch health between Miraj and attacked card
        int swapAux = this.getHealth();
        this.setHealth(attackedCard.getHealth());
        attackedCard.setHealth(swapAux);
    }
}

class CursedOne extends SpecialMinionCard {
    CursedOne(final CardInput rawCard) {
        super(rawCard);
    }

    @Override
    void specialAbility(final MinionCard attackedCard) {
        // switch the attack and health of an enemy minion
        int swapAux = attackedCard.getHealth();
        attackedCard.setHealth(attackedCard.getAttackDamage());
        attackedCard.setAttackDamage(swapAux);
    }
}

class Disciple extends SpecialMinionCard {
    Disciple(final CardInput rawCard) {
        super(rawCard);
    }

    @Override
    void specialAbility(final MinionCard attackedCard) {
        // heal allied card with 2 hp
        int prevHealth = attackedCard.getHealth();
        attackedCard.setHealth(prevHealth + 2);
    }
}

class FirestormEnvironmentCard extends Card {
    FirestormEnvironmentCard(final CardInput rawCard) {
        super(rawCard.getName(), rawCard.getMana(), rawCard.getDescription(), rawCard.getColors());
    }

    FirestormEnvironmentCard(final FirestormEnvironmentCard firestormEnvironmentCard) {
        super(firestormEnvironmentCard);
    }

    void firestormEffect(final LinkedList<MinionCard> cards) {
        // deal damage to all cards
        LinkedList<MinionCard> deadCards = new LinkedList<>();
        // deal damage to all minions on row, marks dead minions
        for (MinionCard card : cards) {
            card.loseHealth(Global.FIRESTORM_DAMAGE);
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
                + "mana="
                + this.getMana()
                + ", description='"
                + this.getDescription()
                + '\''
                + ", colors="
                + this.getColors()
                + ", name='"
                + ""
                + this.getName()
                + '\''
                + '}';
    }
}

class WinterfellEnvironmentCard extends Card {
    WinterfellEnvironmentCard(final CardInput rawCard) {
        super(rawCard.getName(), rawCard.getMana(), rawCard.getDescription(), rawCard.getColors());
    }

    WinterfellEnvironmentCard(final WinterfellEnvironmentCard winterfellEnvironmentCard) {
        super(winterfellEnvironmentCard);
    }

    void winterfellEffect(final LinkedList<MinionCard> cards) {
        // take ability to attack (will be reset next round)
        for (MinionCard card : cards) {
            card.setFrozen(true);
        }
    }

    @Override
    public String toString() {
        return "{"
                + "mana="
                + this.getMana()
                + ", description='"
                + this.getDescription()
                + '\''
                + ", colors="
                + this.getColors()
                + ", name='"
                + ""
                + this.getName()
                + '\''
                + '}';
    }
}

class HeartHoundEnvironmentCard extends Card {
    HeartHoundEnvironmentCard(final CardInput rawCard) {
        super(rawCard.getName(), rawCard.getMana(), rawCard.getDescription(), rawCard.getColors());
    }

    HeartHoundEnvironmentCard(final HeartHoundEnvironmentCard heartHoundEnvironmentCard) {
        super(heartHoundEnvironmentCard);
    }

    int heartHoundEffect(final LinkedList<MinionCard> cards,
                         final ArrayList<LinkedList<MinionCard>> table, final int playerTurn) {
        // initially null; if row is empty, will stay null
        MinionCard maxHealthMinionCard = null;

        // get max health minion
        if (cards.size() == 1) {
            // if only one card, it is max hp card
            maxHealthMinionCard = cards.getFirst();
        } else if (cards.size() > 1) {
            // if multiple, get max hp card
            maxHealthMinionCard = cards.getFirst();

            for (int i = 1; i < cards.size(); i++) {
                if (cards.get(i).getHealth() > maxHealthMinionCard.getHealth()) {
                    maxHealthMinionCard = cards.get(i);
                }
            }
        }

        if (maxHealthMinionCard == null) {
            return -1;
        }

        // first player used card
        if (playerTurn == 1 && MinionCard.correspondingRow(maxHealthMinionCard) == 1) {
            // destination row is full
            if (table.get(2).size() == Global.MAX_ROW_SIZE) {
                return 1;
            }
            // remove the card at index of max health minion, place it at row of current player
            table.get(2).addLast(table.get(1).remove(table.get(1).indexOf(maxHealthMinionCard)));
        } else if (playerTurn == 1 && MinionCard.correspondingRow(maxHealthMinionCard) == -1) {
            // user's row at max capacity
            if (table.get(Global.PLAYER_ONE_BACK_ROW).size() == Global.MAX_ROW_SIZE) {
                return 1;
            }
            MinionCard movedCard = table.get(0).remove(table.get(0).indexOf(maxHealthMinionCard));
            table.get(Global.PLAYER_ONE_BACK_ROW).addLast(movedCard);
            // second player used card
        } else if (playerTurn == 2 && MinionCard.correspondingRow(maxHealthMinionCard) == 1) {
            // user's row at max capacity
            if (table.get(1).size() == Global.MAX_ROW_SIZE) {
                return 1;
            }
            table.get(1).addLast(table.get(2).remove(table.get(2).indexOf(maxHealthMinionCard)));
        } else {
            // user's row at max capacity
            if (table.get(0).size() == Global.MAX_ROW_SIZE) {
                return 1;
            }
            int indexOfCard = table.get(Global.PLAYER_ONE_BACK_ROW).indexOf(maxHealthMinionCard);
            table.get(0).addLast(table.get(Global.PLAYER_ONE_BACK_ROW).remove(indexOfCard));
        }
        // if it got here, the card was moved accordingly, no error
        return 0;
    }

    @Override
    public String toString() {
        return "{"
                + "mana="
                + this.getMana()
                + ", description='"
                + this.getDescription()
                + '\''
                + ", colors="
                + this.getColors()
                + ", name='"
                + ""
                + this.getName()
                + '\''
                + '}';
    }
}

class HeroCard extends Card {
    private int health;
    @JsonIgnore
    private boolean ableToAttack;
    HeroCard() {

    }

    HeroCard(final CardInput rawCard) {
        super(rawCard.getName(), rawCard.getMana(), rawCard.getDescription(),
                rawCard.getColors());
        this.setHealth(Global.DEFAULT_HERO_HEALTH);
    }

    HeroCard(final HeroCard heroCardToCopy) {
        super(heroCardToCopy.getName(), heroCardToCopy.getMana(), heroCardToCopy.getDescription(),
                heroCardToCopy.getColors());
        this.setHealth(heroCardToCopy.health);
        this.ableToAttack = heroCardToCopy.isAbleToAttack();
    }

    public void loseHealth(final int lostHealth) {
        int prevHealth = this.health;
        this.setHealth(prevHealth - lostHealth);
    }

    public void useHeroAbility(final LinkedList<MinionCard> affectedRow) {

    }

    @JsonIgnore
    public boolean isDead() {
        return this.health <= 0;
    }

    @JsonIgnore
    public boolean isAbleToAttack() {
        return ableToAttack;
    }

    @JsonIgnore
    public void setAbleToAttack(final boolean ableToAttack) {
        this.ableToAttack = ableToAttack;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(final int health) {
        this.health = health;
    }

    @Override
    public String toString() {
        return "{"
                + "mana="
                + this.getMana()
                + ", health="
                + health
                + ", description='"
                + this.getDescription()
                + '\''
                + ", colors="
                + this.getColors()
                + ", name='"
                + ""
                + this.getName()
                + '\''
                + '}';
    }
}

class LordRoyce extends HeroCard {
    LordRoyce(final CardInput rawCard) {
        super(rawCard);
    }

    @Override
    public void useHeroAbility(final LinkedList<MinionCard> affectedRow) {
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

            for (int i = 1; i < affectedRow.size(); i++) {
                if (affectedRow.get(i).getHealth() > maxAttackMinionCard.getAttackDamage()) {
                    maxAttackMinionCard = affectedRow.get(i);
                }
            }
        }

        // set card to frozen
        maxAttackMinionCard.setFrozen(true);
    }
}

class EmpressThorina extends HeroCard {
    EmpressThorina(final CardInput rawCard) {
        super(rawCard);
    }

    @Override
    public void useHeroAbility(final LinkedList<MinionCard> affectedRow) {
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

            for (int i = 1; i < affectedRow.size(); i++) {
                if (affectedRow.get(i).getHealth() > maxHealthMinionCard.getHealth()) {
                    maxHealthMinionCard = affectedRow.get(i);
                }
            }
        }

        // kill minion
        affectedRow.removeFirstOccurrence(maxHealthMinionCard);
    }
}

class KingMudface extends HeroCard {
    KingMudface(final CardInput rawCard) {
        super(rawCard);
    }

    @Override
    public void useHeroAbility(final LinkedList<MinionCard> affectedRow) {
        // increase health with 1 point for each card on row
        for (MinionCard card : affectedRow) {
            card.setHealth(card.getHealth() + 1);
        }
    }
}

class GeneralKocioraw extends HeroCard {
    GeneralKocioraw(final CardInput rawCard) {
        super(rawCard);
    }

    @Override
    public void useHeroAbility(final LinkedList<MinionCard> affectedRow) {
        // increase attack damage with 1 point for each card on row
        for (MinionCard card : affectedRow) {
            card.setAttackDamage(card.getAttackDamage() + 1);
        }
    }
}
