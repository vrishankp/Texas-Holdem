import java.util.NoSuchElementException;
import java.util.*;

public class Game {
    Deck deckOfCards;
    int numPlayers;
    Card[][] players;
    Card[] comCards;
    int winningHand;

    public Game(int numPlayers){
        if (numPlayers < 2 || numPlayers > 10){
            throw new NoSuchElementException("Only 2 - 10 players allowed");
        }
        this.deckOfCards = new Deck();
        this.deckOfCards.shuffle();

        this.numPlayers = numPlayers;
        this.players = new Card[numPlayers][2];

        int topCard = 0;
        for (int i = 0; i < numPlayers; i++){
            for (int j = 0; j < 2; j++){
                players[i][j] = deckOfCards.deck[topCard];
                topCard++;
            }
        }

        this.comCards = new Card[5];

        for (int i = 0; i < 5; i++){
            this.comCards[i] = deckOfCards.deck[topCard];
            topCard++;
        }
    }

    /**
    public static void main(String args[]){
       Game game = new Game(4);

       for (int i = 0; i < 4; i++){
           System.out.println("Player " + (i + 1) + " hand:");
           System.out.println(game.players[i][0] + ", " + game.players[i][1]);
           System.out.println();
       }

       System.out.println("Community Cards: ");
       System.out.println(game.comCards[0] + ", " + game.comCards[1] + ", " + game.comCards[2] + ", " + game.comCards[3] + ", " + game.comCards[4]);
       System.out.println();

       System.out.println("Winner(s): ");
       //Card[][] winners = game.winner(game.players, game.comCards);
       Card[] p1 = {new Card(8, 'S'), new Card(2, 'H')};
       Card[] p2 = {new Card(7, 'C'), new Card(7, 'H')};
       Card[] p3 = {new Card(4, 'C'), new Card(2, 'D')};
       Card[] p4 = {new Card(10, 'S'), new Card(9, 'C')};

       Card[] commCards = {new Card(8, 'C'), new Card(10, 'H'), new Card(13, 'H'), new Card(2, 'S'), new Card(9, 'D')};
       Card[][] players = {p1, p2, p3, p4};

       Card[][] winners = game.winner(game.players, game.comCards);
       for (Card[] cards: winners){
           System.out.println(cards[0] + ", " + cards[1] + ", " + cards[2] + ", " + cards[3] + ", " + cards[4]);
       }

       System.out.println("Winning hand rank: " + game.winningHand);

    }
    */

    public Card[][] winner(Card[][] hands, Card[] comCards){
        Card[][] bestHands = new Card[hands.length][5];
        // Keeps track of best hand name for each player
        // High card = 1, 1 pair = 2, ... , Straight Flush = 9
        int[] playerScore = new int[hands.length];

        // Step 1: Check for Straights
        for (int i = 0; i < hands.length; i++){
            Card[] straightHand = checkStraight(hands[i], comCards);
            if (straightHand != null){
                // 5 means straight
                playerScore[i] = 5;
                bestHands[i] = straightHand;
            }
        }

        // Step 2: Check for Flush and Straight Flush
        for (int i = 0; i < playerScore.length; i++){

            // If Hand already has a straight, they might have a straight flush
            if (playerScore[i] == 5){
                if (checkStraightFlush(bestHands[i]) != null){
                    // Straight Flush has a score of 9 (the highest)
                    playerScore[i] = 9;
                }
            } else {
                // Check for regular flush
                Card[] flushHand = checkFlush(hands[i], comCards);
                if (flushHand != null){
                    playerScore[i] = 6; // Flush has a score of 6
                    bestHands[i] = flushHand;
                }
            }
        }

        // Step 3: Check for groups (pairs, trips, quads, and full house)
        for (int i = 0; i < playerScore.length; i++) {
            if (playerScore[i] != 0){
                continue;
            }

            Object[] result = checkGroup(hands[i], comCards);
            String handRating = (String) result[0];
            Card[] hand = (Card[]) result[1];

            // If no pair, best is High Card
            if (handRating == null){
                playerScore[i] = 1;
                bestHands[i] = Arrays.copyOfRange(sort(combine(hands[i], comCards)), 2, 7);
            } else if (handRating.equals("Pair")){
                playerScore[i] = 2;
                bestHands[i] = hand;
            } else if (handRating.equals("Two Pair")){
                playerScore[i] = 3;
                bestHands[i] = hand;
            } else if (handRating.equals("Trips")){
                playerScore[i] = 4;
                bestHands[i] = hand;
            } else if (handRating.equals("Full House")){
                playerScore[i] = 7;
                bestHands[i] = hand;
            } else if (handRating.equals("Quads")){
                playerScore[i] = 8;
                bestHands[i] = hand;
            }

        }

        // Determine the player with the highest score
        int maxScore = 0;
        ArrayList<Card[]> maxScorePlayers = new ArrayList<>();

        for (int i = 0; i < playerScore.length; i++){
            if (playerScore[i] > maxScore){
                maxScore = playerScore[i];
                maxScorePlayers = new ArrayList<>();
                maxScorePlayers.add(bestHands[i]);
            } else if (playerScore[i] == maxScore){
                maxScorePlayers.add(bestHands[i]);
            }
        }
        Card[][] toReturn = new Card[maxScorePlayers.size()][];
        int count = 0;
        for (Card[] cards: maxScorePlayers){
            toReturn[count] = cards;
            count++;
        }

        this.winningHand = maxScore;
        if (toReturn.length == 1){
            return toReturn;
        } else {
            // Check for tiebreaks
            return compareHands(toReturn);
        }
    }

    public static Card[] checkStraight(Card[] player, Card[] comCards){
        boolean hasStraight = false;
        Card[] bestHand = null;

        Card[] totalHand = combine(player, comCards);
        totalHand = sort(totalHand);
        // Three variations for possible straight:
        // Cards 0-4:
        Card[] firstArray = Arrays.copyOfRange(totalHand, 0, 5);
        if (isStraight(firstArray)){
            hasStraight = true;
            bestHand = firstArray;
        }

        // Cards 1-5:
        Card[] secondArray = Arrays.copyOfRange(totalHand, 1, 6);
        if (isStraight(secondArray)){
            hasStraight = true;
            bestHand = secondArray;
        }

        // Cards 2-6:
        Card[] thirdArray = Arrays.copyOfRange(totalHand, 2, 7);
        if (isStraight(thirdArray)){
            hasStraight = true;
            bestHand = thirdArray;
        }

        // Now have to check with Aces = 14 (max)
        totalHand = sort(convertAces(totalHand, true));

        // Cards 0-4:
        firstArray = Arrays.copyOfRange(totalHand, 0, 5);
        if (isStraight(firstArray)){
            hasStraight = true;
            bestHand = firstArray;
        }

        // Cards 1-5:
        secondArray = Arrays.copyOfRange(totalHand, 1, 6);
        if (isStraight(secondArray)){
            hasStraight = true;
            bestHand = secondArray;
        }

        // Cards 2-6:
        thirdArray = Arrays.copyOfRange(totalHand, 2, 7);
        if (isStraight(thirdArray)){
            hasStraight = true;
            bestHand = thirdArray;
        }



        return bestHand;
    }

    public static Card[] combine(Card[] player, Card[] comCards){
        Card[] toReturn = new Card[player.length + comCards.length];
        List<Card> toList = Arrays.asList(comCards);
        toList = new ArrayList<>(toList);
        Collections.addAll(toList, player);
        toList.toArray(toReturn);
        return toReturn;
    }

    public static Card[] sort(Card[] cards){
        List<Card> toList = Arrays.asList(cards);
        Collections.sort(toList);
        toList.toArray(cards);
        return cards;
    }

    public static boolean isStraight(Card[] hand){
        int totalDiff = 0;
        for (int i = 0; i < hand.length - 1; i++){
            if ((hand[i].rank + 1) != hand[i + 1].rank){
                return false;
            }
        }
        return true;


    }

    public static Card[] convertAces(Card[] hand, boolean to14){
        if (to14){
            for (Card card: hand){
                if (card.rank == 1){
                    card.rank = 14;
                }
            }
        } else {
            for (Card card: hand){
                if (card.rank == 14){
                    card.rank = 1;
                }
            }
        }
        return hand;
    }

    public static Card[] checkFlush(Card[] player, Card[] comCards){
        Card[] bestHand = null;
        Card[] totalHand = combine(player, comCards);
        char flushSuit = isFlush(totalHand);

        // Return the highest 5 of the suit
        if (flushSuit != '\u0000'){
            ArrayList<Card> cardOfFlush = new ArrayList<>();
            for (Card x: totalHand){
                if (x.suit == flushSuit){
                    cardOfFlush.add(x);
                }
            }
            Card[] cardOfFlushArray = new Card[cardOfFlush.size()];
            cardOfFlush.toArray(cardOfFlushArray);

            cardOfFlushArray = sort(cardOfFlushArray);
            bestHand = Arrays.copyOfRange(cardOfFlushArray, cardOfFlushArray.length - 5,  cardOfFlushArray.length);
        }
        if (bestHand != null){
            bestHand = sort(convertAces(bestHand, true));
        }
        return bestHand;
    }

    public static Card[] checkStraightFlush(Card[] hand){
        if (isFlush(hand) != '\u0000'){
            return hand;
        }

        return null;
    }

    public static char isFlush(Card[] hand){
        int numClubs = 0;
        int numDiamonds = 0;
        int numHearts = 0;
        int numSpades = 0;
        char flushSuit = '\u0000';

        for (Card card: hand){
            switch(card.suit){
                case 'C':
                    numClubs ++;
                    if (numClubs >= 5){
                        flushSuit = 'C';
                    }
                    break;
                case 'D':
                    numDiamonds ++;
                    if (numDiamonds >= 5){
                        flushSuit = 'D';
                    }
                    break;
                case 'H':
                    numHearts ++;
                    if (numHearts >= 5){
                        flushSuit = 'H';
                    }
                    break;
                case 'S':
                    numSpades ++;
                    if (numSpades >= 5){
                        flushSuit = 'S';
                    }
                    break;

            }
        }

        return flushSuit;
    }

    public static Object[] checkGroup(Card[] player, Card[] comCards){
        Card[] totalHand = convertAces(combine(player, comCards), true);
        Card[] bestHand = null;
        String handRating = null;

        ArrayList<Integer> matches = new ArrayList<>();
        ArrayList<Integer> numMatches = new ArrayList<>();

        for (int i = 0; i < totalHand.length - 1; i++ ){
            if (matches.indexOf(totalHand[i].rank) != -1){
                continue;
            }
            for (int j = i + 1; j < totalHand.length; j++){
                if (totalHand[i].equals(totalHand[j])){
                    int indexOfMatch = matches.indexOf(totalHand[i].rank);

                    // Not in the matches
                    if (indexOfMatch == -1){
                        matches.add(totalHand[i].rank);
                        numMatches.add(2);
                        continue;
                    }

                    // Already in matches ,so add 1
                    //System.out.println("Index of Match: " + indexOfMatch);
                    numMatches.set(indexOfMatch, numMatches.get(indexOfMatch) + 1);
                }
            }
        }
        // If at least 1 group
        if (numMatches.size() >= 1){
            int numInGroup = Collections.max(numMatches);
            int groupRank = matches.get(numMatches.indexOf(numInGroup));
            // If only 1 group, then it can be a pair, triple, or quads
            if (numMatches.size() == 1 || numInGroup == 4){
                ArrayList<Card> listOfGroup = new ArrayList<>();
                ArrayList<Card> rest = new ArrayList<>();
                for (Card card: totalHand){
                    if (card.rank == groupRank){
                        listOfGroup.add(card);
                    } else {
                        rest.add(card);
                    }
                }

                Card[] groupArray = new Card[listOfGroup.size()];
                listOfGroup.toArray(groupArray);

                Card[] restArray = new Card[rest.size()];
                rest.toArray(restArray);

                restArray = sort(restArray);

                bestHand = combine(groupArray, Arrays.copyOfRange(restArray, restArray.length - (5 - numInGroup), restArray.length));
                if (numInGroup == 4){
                    handRating = "Quads";
                } else if (numInGroup == 3){
                    handRating = "Trips";
                } else if (numInGroup == 2){
                    handRating = "Pair";
                }
                //System.out.println(bestHand);


            } else {
                // If multiple groups, then it can be 2 pair or full house
                // 2 pair
                if (numInGroup == 2){
                    ArrayList<Integer> matchesSorted = matches;
                    Collections.sort(matchesSorted);

                    int pairOne = matchesSorted.get(matchesSorted.size() - 1);
                    int pairTwo = matchesSorted.get(matchesSorted.size() - 2);

                    Card[] topPair = {null, null};
                    Card[] secondPair = {null, null};

                    for (Card card: totalHand){
                        if (card.rank == pairOne){
                            if (topPair[0] == null){
                                topPair[0] = card;
                            } else {
                                topPair[1] = card;
                            }
                        }
                        if (card.rank == pairTwo){
                            if (secondPair[0] == null){
                                secondPair[0] = card;
                            } else {
                                secondPair[1] = card;
                            }
                        }
                    }

                    Card highestNonPair;
                    totalHand = sort(totalHand);

                    List<Card> totalHandList = Arrays.asList(totalHand);
                    ArrayList<Card> totalArrayList = new ArrayList<>(totalHandList);

                    totalArrayList.remove(topPair[0]);
                    totalArrayList.remove(topPair[1]);

                    totalArrayList.remove(secondPair[0]);
                    totalArrayList.remove(secondPair[1]);

                    highestNonPair = totalArrayList.get(totalArrayList.size() - 1);

                    Card[] toCombine = {highestNonPair};
                    bestHand = combine(topPair, combine(secondPair, toCombine));
                    handRating = "Two Pair";
                } else {
                    // Full House
                    int indexOfTriple = numMatches.indexOf(3);
                    int tripleRank = matches.get(indexOfTriple);
                    // If more than 2 groups, then there are 2 pairs
                    int indexOfPair;
                    int pairRank;
                    if (numInGroup > 2){

                        // Remove the triple
                        matches.remove(indexOfTriple);
                        pairRank = Collections.max(matches);
                    } else {
                        indexOfPair = numMatches.indexOf(2);
                        pairRank = matches.indexOf(indexOfPair);
                    }

                    Card[] triple = {null, null, null};
                    Card[] pair = {null, null};

                    for (Card card: totalHand){
                        if (card.rank == tripleRank){
                            if (triple[0] == null){
                                triple[0] = card;
                            } else {
                                if (triple[1] == null) {
                                    triple[1] = card;
                                } else {
                                    triple[2] = card;
                                }
                            }
                        }
                        if (card.rank == pairRank){
                            if (pair[0] == null){
                                pair[0] = card;
                            } else {
                                pair[1] = card;
                            }
                        }
                    }
                    bestHand = combine(triple, pair);
                    handRating = "Full House";
                }
            }
        } else {
            // No pair, aka high card
        }

        Object[] toReturn = {handRating, bestHand};
        return toReturn;
    }

    public static Card[][] compareHands(Card[][] hands){
        ArrayList<Card[]> handsList = new ArrayList<>();
        for (Card[] cards : hands){
            handsList.add(cards);
        }
        for (int j = handsList.get(0).length - 1; j >= 0 ; j--){
            if (handsList.size() == 1){
                break;
            }
            int maxRank = 0;
            ArrayList<Card[]> maxRankHand = new ArrayList<>();
            for (int i = 0; i < handsList.size(); i++){
                if (handsList.get(i)[j].rank > maxRank){
                    maxRank = handsList.get(i)[j].rank;
                    Card[] currentHand = handsList.get(i);
                    // Remove all hands in maxRankHand from handsList as they aren't the highest anymore
                    for (Card[] cards: maxRankHand){
                        handsList.remove(cards);
                    }
                    // Create a new maxRankHand
                    maxRankHand = new ArrayList<>();
                    maxRankHand.add(currentHand);
                } else if (handsList.get(i)[j].rank == maxRank){
                    maxRankHand.add(handsList.get(i));
                } else {
                    handsList.remove(handsList.get(i));
                }
            }
        }

        Card[][] toReturn = new Card[handsList.size()][];
        int count = 0;
        for (Card[] cards: handsList){
            toReturn[count] = cards;
            count++;
        }

        return toReturn;

    }
}
