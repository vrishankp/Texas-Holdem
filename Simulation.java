import java.util.*;
import java.lang.*;
import java.io.*;

public class Simulation {
    Game game;
    int numIter;
    int[] result;

    public Simulation(int numIter){
        result = new int[10];
        this.numIter = numIter;
        for (int i = 0; i < numIter; i++){
            this.game = new Game(6);
            game.winner(game.players, game.comCards);

            int winningHandRank = game.winningHand;
            int newCount = result[winningHandRank] + 1;
            result[winningHandRank] = newCount;
            System.out.print("Loading: " + (i + 1) + "/" + numIter + " (" + Math.round(((float) i + 1)/numIter * 100 * 100.0)/100.0 + "%)" + "\r");
        }
        int[] output = this.result;
        System.out.println();

        for (int i = 1; i < output.length; i++){
            System.out.println(i + ": " + ((float) output[i]  * 100)/ numIter);
        }

    }

    public static void main(String args[]){
        System.out.print("Press 1 for simulation, 2 for winning chances, or 3 to simulate a live game: ");
        Scanner scan = new Scanner(System.in);
        int num = scan.nextInt();
        if (num == 1){
            runSimul();
        } else if (num == 2) {
            chance(scan);
        } else {
            fullGame(scan);
        }
    }

    public static void runSimul(){
        System.out.print("Enter Number of Simulations: ");
        Scanner scan = new Scanner(System.in);
        int num = scan.nextInt();
        Simulation sim = new Simulation(num);
    }

    public static void chance(Scanner scan1){
        Scanner scan = scan1;
        Card[] comCards = new Card[5];
        Card[] playerHand = new Card[2];
        Deck deck = new Deck();
        Card[] remainingCards = deck.getDeck();

        for (int i = 1; i < 6; i++){
            System.out.print("Enter Community Card " + i + "(ex: 12 C, Aces are 1): ");
            int rank = scan.nextInt();
            char suit = scan.next().charAt(0);
            Card newCard = new Card(rank, suit);
            comCards[i - 1] = newCard;
            System.out.println("Added " + newCard);
            remainingCards = remove(remainingCards, newCard);
        }

        for (int i = 1; i < 3; i++){
            System.out.print("Enter your hand " + i + "(ex: 12 C, Aces are 1): ");
            int rank = scan.nextInt();
            char suit = scan.next().charAt(0);
            Card newCard = new Card(rank, suit);
            playerHand[i - 1] = newCard;
            System.out.println("Added " + newCard);
            remainingCards = remove(remainingCards, newCard);
        }
        int handsBetter = 0;
        int handsWorse = 0;
        int handsEqual = 0;
        int count = 0;
        ArrayList<Card> cardsThatBeat1 = new ArrayList<>();
        ArrayList<Card> cardsThatBeat2 = new ArrayList<>();
        ArrayList<Integer> reason = new ArrayList<>();

        for (int i = 0; i < remainingCards.length; i ++){
            for (int j = i + 1; j < remainingCards.length; j ++){
                Card[] otherHand = {remainingCards[i], remainingCards[j]};
                Card[][] players = {playerHand, otherHand};
                Game game = new Game(2);
                game.players = players;
                game.comCards = comCards;
                Card[][] result = game.winner(game.players, game.comCards);
                Card[] winningHand;
                if (result.length > 1){
                    handsEqual++;
                    continue;
                } else {
                    winningHand = result[0];
                }

                if (compareCardArray(winningHand, otherHand, comCards)){
                    handsBetter++;
                    cardsThatBeat1.add(otherHand[0]);
                    cardsThatBeat2.add(otherHand[1]);
                    reason.add(game.winningHand);
                } else if(compareCardArray(winningHand, playerHand, comCards)){
                    handsWorse++;
                }
                count++;
            }
        }
        double winningChance = (float) handsWorse / (float) count;
        double winningPercent = Math.round(winningChance * 100.0 * 100.0) / 100.0;
        System.out.println("You have a " + winningPercent + "% of winning!");
        System.out.println("There are " + handsEqual + " hands that will tie with you");
        writeToCSV(cardsThatBeat1, cardsThatBeat2, reason);
    }

    public static void fullGame(Scanner scan){
        System.out.print("Enter how many players are in this game: ");
        int numPlayers = scan.nextInt();
        System.out.println();

        System.out.print("Enter how many cards are part of the community cards: ");
        int numComCards = scan.nextInt();
        System.out.println();

        Card[] comCards = new Card[numComCards];
        Card[][] players = new Card[numPlayers][];
        for (int i = 0; i < players.length; i++){
            players[i] = new Card[2];
        }
        Deck deck = new Deck();
        Card[] remainingCards = deck.getDeck();

        for (int i = 1; i < numComCards + 1; i++){
            System.out.print("Enter Community Card " + i + "(ex: 12 C, Aces are 1): ");
            int rank = scan.nextInt();
            char suit = scan.next().charAt(0);
            Card newCard = new Card(rank, suit);
            comCards[i - 1] = newCard;
            System.out.println("Added " + newCard);
            remainingCards = remove(remainingCards, newCard);
        }

        for (int i = 1; i < numPlayers + 1; i++){
            for (int j = 0; j < 2; j++){
                System.out.print("Enter player " + i + "'s Card " + j + "(ex: 12 C, Aces are 1): ");
                int rank = scan.nextInt();
                char suit = scan.next().charAt(0);
                Card newCard = new Card(rank, suit);
                players[i - 1][j] = newCard;
                System.out.println("Added " + newCard);
                remainingCards = remove(remainingCards, newCard);
            }
        }

        int comLeft = 5 - numComCards;

        int count = 0;
        int[] winningCount = new int[numPlayers];
        ArrayList<Card> test1 = new ArrayList<>();
        ArrayList<Card> test2 = new ArrayList<>();
        ArrayList<Integer> stringTest = new ArrayList<>();
        if (comLeft == 2){
            for (int i = 0; i < remainingCards.length; i ++) {
                for (int j = i + 1; j < remainingCards.length; j++) {

                    Game game = new Game(numPlayers);
                    Card[] completionCards = {remainingCards[i], remainingCards[j]};

                    if (completionCards[0].rank == 9 && completionCards[1].rank == 10){
                        System.out.print("");
                    }
                    Card[] fullComCards = Game.combine(comCards, completionCards);
                    game.players = players;
                    game.comCards = fullComCards;

                    boolean test = false;
                    Card[][] result = game.winner(game.players, game.comCards);
                    Card[] winningHand;

                    if (result.length > 1){
                        for (int l = 0; l < players.length; l++){
                            for (int k = 0; k < result.length; k++) {
                                if (compareCardArray(result[k], players[l], fullComCards)) {
                                    winningCount[l]++;
                                    test = true;
                                    break;
                                }
                            }
                        }
                        continue;
                    } else {
                        winningHand = result[0];
                    }

                    for (int l = 0; l < players.length; l++){
                        if (compareCardArray(winningHand, players[l], fullComCards)){
                            winningCount[l]++;
                            test = true;
                            break;
                        }
                    }

                    test1.add(remainingCards[i]);
                    test2.add(remainingCards[j]);
                    stringTest.add(1);
                }
            }

            for (int element: winningCount){
                count += element;
            }
            System.out.println(count);
            for (int i = 1; i < winningCount.length + 1; i++){;
                System.out.println("Player " + i + "'s chance of winning: "  + winningCount[i-1]/ (float) count * 100.0 + "%");
            }
            writeToCSV(test1, test2, stringTest);
        } else if (comLeft == 1){

        }


    }

    public static Card[] remove(Card[] array, Card card){
        Card[] newArray = new Card[array.length - 1];
        int count = 0;
        for (int i = 0; i < array.length; i++){
            if (array[i].superEqual(card)){

            } else {
                newArray[count] = array[i];
                count++;
            }
        }
        return newArray;
    }

    public static String displayCardArray(Card[] array){
        String toReturn = "";
        for (int i = 0; i < array.length; i++){
            toReturn += array[i].toString();
            toReturn += ", ";
        }
        toReturn += " of length ";
        toReturn += array.length;
        return toReturn;
    }

    public static boolean compareCardArray(Card[] returnHands, Card[] playerHands, Card[] com){
        Card[] fullHand = Game.combine(playerHands, com);
        for (int i = 0; i < returnHands.length; i++){
            boolean matchFound = false;
            for (int j = 0; j < fullHand.length; j ++){
                if (returnHands[i].superEqual(fullHand[j])){
                    matchFound = true;
                    break;
                }
            }
            if (!matchFound){
                return false;
            }
        }
        return true;
    }

    public static void writeToCSV(ArrayList card1, ArrayList card2, ArrayList reason){
        reason = convertHandRank(reason);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.csv"));
            String header = "First Card,Second Card,Hand Formed";
            writer.write(header);


            for (int i = 0; i < card1.size(); i++){
                writer.newLine();
                String toWrite = "";
                toWrite += card1.get(i) + ",";
                toWrite += card2.get(i) + ",";
                toWrite += reason.get(i);
                writer.write(toWrite);
            }
            writer.close();
        } catch (Exception e){

        }
    }

    public static ArrayList<String> convertHandRank(ArrayList<Integer> reason){
        ArrayList<String> toReturn = new ArrayList<>();
        for (Integer rank: reason){
            switch(rank){
                case 1:
                    toReturn.add("High Card");
                    break;
                case 2:
                    toReturn.add("One Pair");
                    break;
                case 3:
                    toReturn.add("Two Pair");
                    break;
                case 4:
                    toReturn.add("Triple");
                    break;
                case 5:
                    toReturn.add("Straight");
                    break;
                case 6:
                    toReturn.add("Flush");
                    break;
                case 7:
                    toReturn.add("Full House");
                    break;
                case 8:
                    toReturn.add("Quadruple");
                    break;
                case 9:
                    toReturn.add("Straight Flush");
                    break;
            }
        }
        return toReturn;
    }
}
