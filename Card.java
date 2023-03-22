public class Card implements Comparable<Card>{
    public int rank;
    public char suit;
    public String name;

    public Card(int rank, char suit){
        this.rank = rank;
        this.suit = suit;

        if (rank > 1 && rank < 11){
            this.name = "" + rank;
        } else {
            if (rank == 11){
                name = "Jack";
            } else if (rank == 12){
                name = "Queen";
            } else if(rank == 13){
                name = "King";
            } else {
                name = "Ace";
            }
        }
    }

    public int getRank(){
        return this.rank;
    }

    public int getSuit(){
        return this.suit;
    }

    public String toString(){
        String toReturn = "" + name + " of ";
        switch(suit){
            case 'C':
                toReturn += "Clubs";
                break;
            case 'D':
                toReturn += "Diamonds";
                break;
            case 'H':
                toReturn += "Hearts";
                break;
            case 'S':
                toReturn += "Spades";
                break;

        }
        return toReturn;
    }

    public int compareTo(Card o) {
        if (o.rank > this.rank){
            return -1;
        } else if (o.rank < this.rank){
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object o){
        if (this.compareTo((Card) o) == 0){
            return true;
        } else {
            return false;
        }
    }

    public boolean superEqual(Card o){
        if (this.rank == o.rank && this.suit == o.suit){
            return true;
        }
        return false;
    }
}
