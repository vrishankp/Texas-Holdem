import java.util.*;
import java.lang.*;
import java.io.*;

public class Simulation {
    Game game;
    int numIter;
    int[] result;

    public void standardSimulation(int numIter){
        this.result = new int[10];
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
        Simulation sim = new Simulation();
        sim.standardSimulation(num);
    }
    public static Card getCard(Scanner scan){
        String next = scan.next();
        int rank;
        if (next.charAt(0) == 'A'){
            rank = 1;
        } else if (next.charAt(0) == 'K'){
            rank = 13;
        } else if (next.charAt(0) == 'Q'){
            rank = 12;
        } else if (next.charAt(0) == 'J'){
            rank = 11;
        } else {
            rank = Integer.parseInt(next);
        }
        char suit = scan.next().toUpperCase().charAt(0);
        Card newCard = new Card(rank, suit);
        return newCard;
    }
    public static void chance(Scanner scan1){
        Scanner scan = scan1;
        Card[] comCards = new Card[5];
        Card[] playerHand = new Card[2];
        Deck deck = new Deck();
        Card[] remainingCards = deck.getDeck();

        for (int i = 1; i < 6; i++){
            System.out.print("Enter Community Card " + i + " (ex: \"A C\"): ");
            Card newCard = getCard(scan);
            comCards[i - 1] = newCard;
            System.out.println("Added " + newCard);
            remainingCards = remove(remainingCards, newCard);
        }

        for (int i = 1; i < 3; i++){
            System.out.print("Enter your hand " + i + " (ex: \"A C\"): ");
            Card newCard = getCard(scan);
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
        System.out.print("Enter how many cards are part of the community cards: ");
        int numComCards = scan.nextInt();

        Card[] comCards = new Card[numComCards];
        Card[][] players = new Card[numPlayers][];
        for (int i = 0; i < players.length; i++){
            players[i] = new Card[2];
        }
        Deck deck = new Deck();
        Card[] remainingCards = deck.getDeck();

        for (int i = 1; i < numComCards + 1; i++){
            System.out.print("Enter Community Card " + i + " (ex: \"A C\"): ");
            Card newCard = getCard(scan);
            comCards[i - 1] = newCard;
            System.out.println("Added " + newCard);
            remainingCards = remove(remainingCards, newCard);
        }

        for (int i = 1; i < numPlayers + 1; i++){
            for (int j = 0; j < 2; j++){
                System.out.print("Enter player " + i + "'s Card " + (j + 1) + " (ex: \"A C\"): ");
                Card newCard = getCard(scan);
                players[i - 1][j] = newCard;
                System.out.println("Added " + newCard);
                remainingCards = remove(remainingCards, newCard);
            }
        }

        int comLeft = 5 - numComCards;

        int count = 0;
        int[] winningCount = new int[numPlayers];
        int[] tieCount = new int[numPlayers];
        if (comLeft == 2){
            for (int i = 0; i < remainingCards.length; i ++) {
                for (int j = i + 1; j < remainingCards.length; j++) {

                    Game game = new Game(numPlayers);
                    Card[] completionCards = {remainingCards[i], remainingCards[j]};
                    Card[] fullComCards = Game.combine(comCards, completionCards);
                    game.players = players;
                    game.comCards = fullComCards;

                    Card[][] result = game.winner(game.players, game.comCards);
                    Card[] winningHand;

                    if (result.length > 1){
                        for (int l = 0; l < players.length; l++){
                            for (int k = 0; k < result.length; k++) {
                                if (compareCardArray(result[k], players[l], fullComCards)) {
                                    tieCount[l]++;
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
                            break;
                        }
                    }
                }
            }

            for (int element: winningCount){
                count += element;
            }

            for (int element: tieCount){
                count += element;
            }

            System.out.println("\n------ Results ------");
            for (int i = 1; i < winningCount.length + 1; i++){
                double winningPercent = winningCount[i-1]/ (float) count * 100.0;
                double tiePercent = tieCount[i-1]/ (float) count * 100.0;
                double totalPercent =  winningPercent + tiePercent;
                System.out.println("Player " + i + " has a " + round(winningPercent) + "% chance of winning and a " + round(tiePercent) + "% chance of tying, for a total of " + round(totalPercent) + "%");
            }

            System.out.println("\n------ Next Card ------");
            System.out.println("Next Community Card: ");
            Card newCard = getCard(scan);
            System.out.println("Added " + newCard);
            Card[] newComCards = Game.combine(comCards, new Card[]{newCard});
            Card[] newRemainingCards = remove(remainingCards, newCard);

            fullGame4(scan, newRemainingCards, newComCards, players);

        } else if (comLeft == 1){
            fullGame4(scan, remainingCards, comCards, players);
        } else {

        }


    }

    public static void fullGame4(Scanner scan, Card[] remainingCards, Card[] comCards, Card[][] players){
        int[] winningCount = new int[players.length];
        int[] tieCount = new int[players.length];
        for (int i = 0; i < remainingCards.length; i++){
            Game game = new Game(players.length);
            Card[] completionCards = {remainingCards[i]};
            Card[] fullComCards = Game.combine(comCards, completionCards);
            game.players = players;
            game.comCards = fullComCards;

            Card[][] result = game.winner(game.players, game.comCards);
            Card[] winningHand;

            if (result.length > 1){
                for (int l = 0; l < players.length; l++){
                    for (int k = 0; k < result.length; k++) {
                        if (compareCardArray(result[k], players[l], fullComCards)) {
                            tieCount[l]++;
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
                    break;
                }
            }
        }
        int count = 0;
        for (int element: winningCount){
            count += element;
        }
        System.out.println("\n------ Results ------");
        for (int i = 1; i < winningCount.length + 1; i++){
            double winningPercent = winningCount[i-1]/ (float) count * 100.0;
            double tiePercent = tieCount[i-1]/ (float) count * 100.0;
            double totalPercent = winningPercent + tiePercent;
            System.out.println("Player " + i + " has a " + round(winningPercent) + "% chance of winning and a " + round(tiePercent) + "% chance of tying, for a total of " + round(totalPercent) + "%");
        }

        System.out.println("\n------ Next Card ------");
        System.out.println("Next Community Card: ");
        Card newCard = getCard(scan);
        System.out.println("Added " + newCard);
        Card[] newComCards = Game.combine(comCards, new Card[]{newCard});

        Game game = new Game(players.length);
        game.players = players;
        game.comCards = newComCards;
        Card[][] winners =  game.winner(game.players, game.comCards);

        if (winners.length == 1){
            System.out.print("The winner is: ");
            for (int i = 0; i < players.length; i++){
                if (compareCardArray(winners[0], players[i], newComCards)){
                    System.out.println("Player " + (i + 1));
                    break;
                }
            }
        } else {
            System.out.println("There is a tie between: ");
            for (int i = 0; i < players.length; i++){
                for (int j = 0; j < winners.length; j++){
                    if (compareCardArray(winners[j], players[i], newComCards)){
                        System.out.println("Player " + (i + 1));
                        break;
                    }
                }
            }
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

    public static double round(double value) {
        return Math.round(value * 100.00) / 100.00;
    }
}
