package com.shayaan.thehouse;

import java.util.List;

public class Hand {
	private List<Card> hand;

	public Hand(List<Card> hand) {
		this.hand = hand;
	}

	public List<Card> getHand() {
		return hand;
	}

	public Card getCard(int index) {
		return hand.get(index);
	}

	public void dealCard(Card c) {
		hand.add(c);
	}
}
