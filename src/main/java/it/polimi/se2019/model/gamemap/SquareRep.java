package it.polimi.se2019.model.gamemap;

import it.polimi.se2019.model.cards.Card;

import java.io.Serializable;
import java.util.ArrayList;

public class SquareRep implements Serializable {

	private int roomID;
	private ArrayList<Card> cards;
	private Coordinates coordinates;

	public SquareRep(Square squareToRepresent) {
		this.roomID = squareToRepresent.getRoomID();
		this.cards = new ArrayList<>(squareToRepresent.getCardList());
		this.coordinates = squareToRepresent.getCoordinates();
	}

	public int getRoomID() {
		return roomID;
	}

	public ArrayList<Card> getCards() {
		return cards;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}
}