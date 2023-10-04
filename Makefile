run: Card.class Game.class Simulation.class Deck.class
	java Simulation
Card.class: Card.java
	javac Card.java
Game.class: Game.java
	javac Game.java
Simulation.class: Simulation.java
	javac Simulation.java
Deck.class: Deck.java
	javac Deck.java
clean:
	rm *.class
