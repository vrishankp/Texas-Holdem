import java.util.*;
public class Simulation {
    Game game;
    int numIter;
    int[] result;

    public Simulation(int numIter){
        result = new int[10];
        this.numIter = numIter;
        for (int i = 0; i < numIter; i++){
            this.game = new Game(4);
            game.winner(game.players, game.comCards);

            int winningHandRank = game.winningHand;
            int newCount = result[winningHandRank] + 1;
            result[winningHandRank] = newCount;
            System.out.print("Loading: " + (i + 1) + "/" + numIter + " (" + Math.round(((float) i + 1)/numIter * 100 * 100.0)/100.0 + "%)" + "\r");
        }
        int[] output = this.result;
        System.out.println();

        for (int i = 0; i < output.length; i++){
            System.out.println(i + ": " + ((float) output[i]  * 100)/ numIter);
        }

    }

    public static void main(String args[]){
        System.out.print("Enter Number of Simulations: ");
        Scanner scan = new Scanner(System.in);
        int num = scan.nextInt();
        Simulation sim = new Simulation(num);
    }
}
