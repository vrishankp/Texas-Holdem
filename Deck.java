import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Deck {
    Card[] deck;

    public Deck(){
        this.deck = new Card[52];
        int count = 0;

        char[] suits = {'C', 'D', 'H', 'S'};

        for (int i = 1; i < 14; i++){
            for (char j : suits){
                deck[count] = new Card(i, j);
                count ++;
            }
        }
    }

    public Card[] getDeck() {
        return this.deck;
    }

    public String toString(){
        String toReturn = "";
        for (Card card : this.deck){
            toReturn += card.toString() + "\n";
        }
        return toReturn;
    }

    public void shuffle(){
        List<Card> toList = Arrays.asList(this.deck);
        Collections.shuffle(toList);
        toList.toArray(this.deck);
    }
}
