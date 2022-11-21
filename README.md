<div align="center"><img src="https://tenor.com/view/witcher3-gif-9340436.gif" width="500px" alt="Gwent Image"></div>

<b><font size=6 ><center>GWENTSTONE</center></div></font></b>

## Description
A fast-paced, turn-based strategy card game, in which two opponents use their limited
mana in order to summon different types of minions or cast environment spells,
affecting multiple cards. The minions can be used to attack the opponent's minions or hero.
The game ends when the opponent's hero dies.
opponent's minions or hero.

## Gameplay

### Preparing the Players

The first step into playing the game is preparing the players. Before the games between two
players begin, input is given about the two players: each player gets a set of decks with
the same number of cards in each deck. A player can only use the cards in a deck chosen
before the game begins.

### Preparing the Game

Along with the players' deck, input is given about each game: the chosen deck for each player,
the starting player and the actions performed by each player during the game.

### Playing Table

The game is played on a table. Each player has a front row and a back row. Cards are placed on the
first row or on the back row according to their type. The maximum number of cards placed on a table
is 5.

### Turn-Based Round System

#### Cards in Hand

When the game begins, each player starts with one card grabbed from his deck. Every round,
that is every time both players use their turns, another card is grabbed from the deck.

#### Mana Gain

Both players begin with 1 mana and their mana gain per round increases by 1 each round.
At the beginning of the first round, players start with 1 mana, at the beginning of the second
round players gain 2 mana, and so on. Maximum mana gain per round is 10 mana.

#### Attack System and Freeze

A minion card placed on the table can only attack once every round, during the player's turn.

A minion card can be frozen by certain abilities. Frozen cards cannot attack enemy cards
for one turn and freezes do not stack - freezing the same card twice will make it stay frozen for
one round only.

### Heroes

Each player starts with a hero, that must be eliminated in order for the game to end.
Each hero starts with 30 health points and has a special ability that affects a row.

- Lord Royce - Ability: Sub-Zero - freezes the card with the highest attack damage on the targeted row
- King Mudface - Ability: Earth Born - increases health points of all cards on targeted allied row by 1
- General Kocioraw - Ability: Blood Thirst - increases attack damage of all cards on targeted allied row by 1
- Empress Thorina - Ability: Low Blow - destroys the card with the highest health on the targeted row

### Playable Card Types

#### Standard Minion Cards

The most basic type of minion there is in the game. They can be placed and they can attack enemy
minion cards and the opposing hero.

- Sentinel
- Berserker

#### Tank Minion Cards

Certain minion cards have the 'tank' status, meaning that whenever they are placed on the table,
they must be the first ones attack. A player cannot attack other minions as long as the opponent
still has tank minions on his side.

- Warden
- Goliath

#### Special Minion Cards

Certain minion cards are ranked as 'special' or 'premium'. Special minions also have, besides
their normal attack, a special ability that can be targeted and used.

- The Cursed One - Ability: Shapeshift - swaps the attack damage and health points of a targeted enemy minion 
- Disciple - Ability: God's Plan - increases health points of a targeted allied minion card by 2 points
- The Ripper - Ability: Weak Knees - reduces the attack damage of an enemy minion card by 2 points 
- Miraj - Ability: Skyjack - swaps his health points with a targeted enemy minion's health points

#### Environment Cards

Environment cards can only be used once, and they affect an entire row. They consume mana.

- Firestorm - lowers the health of all minions on the row by 1
- Winterfell - freezes all minions on the row
- Heart Hound - steals the minion with the highest health on the targeted row, placing it on the
current player's side

### Win Condition

A player wins when the opponent's hero is eliminated.

### Commands

#### Player Commands

There are multiple commands a player can use:

- placeCard: places card from the player's hand at a given index on the table; consumes the card's mana cost
- useEnvironmentCard: uses environment card from player's hand at given index, affecting targeted row on the table;
consumes card's mana cost
- cardUsesAttack: uses a minion card placed on the table to attack an opponent's minion
- cardUsesAbility: uses the ability of a special minion card placed on the table on an opponent's minion
- useAttackHero: uses a minion placed on the table attack the enemy hero; if the hero dies, the game ends
- useHeroAbility: uses the ability of the player's hero on a specified row
- endPlayerTurn: ends the current player's turn; at two turns, the round ends

The command is given by the current player. If it is first player's turn, he places the card,
for example.

#### Debugging Commands

There are also commands that help us visualize the board, the current game information and
the cards in each player's hand, deck.

- getPlayerDeck: displays the cards in the deck of a specified player
- getCardsInHand: displays the cards in the hand of a specified player
- getPlayerHero: displays the hero of a specified player
- getPlayerTurn: displays the current player's turn
- getPlayerMana: displays the available mana of a specified player
- getCardsOnTable: displays the cards placed on the table
- getFrozenCardsOnTable: displays the frozen cards placed on the table
- getEnvironmentCardsInHand: displays the environment cards in the hand of a specified player
- getCardAtPosition: displays the card placed at the given position

#### Statistics Commands

- getTotalGamesPlayer: displays the number of games played
- getPlayerOneWins: displays the number of games won by player one
- getPlayerTwoWins: displays the number of games won by player two

## Class Structure
<center>The Class Structure used for input</center>

<center>The Class and Method Structure used for commands</center>

<center>The Class Structure used for cards</center>

## Implementation

The main principle of the implementation can be summarised in a few simple steps:

- receive input
- set up players' decks
- iterate through given games
  - set up game
  - set up each players' chosen decks
  - iterate through each command
    - apply given command
  - determine game outcome
- end of program

## Possible Improvements

- better code structure - less if..else, shorter methods
- better class structure - a more detailed and logical class implementation; I encountered issues
with a more logical approach regarding implementation
- exchange card.getName().equals() with something else - for example, enums

## Comments

- A large project, compared to previous University projects received
- Approachable theme because of my familiarity with its inspiration
- Could have received some more information about using json files, something live and practical,
instead of documentation links; similar situation with the code skeleton - it requires a more
detailed description about what it does

## Conclusion

This project helped me better understand OOP principles and Java code writing. It was interesting,
but also very stressful because of the little to no explaining done for the code skeleton.

Copyright 2022 - 2023 Omer Tarik Ilhan 324CA
