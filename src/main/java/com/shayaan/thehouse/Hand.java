package com.shayaan.thehouse;

import java.util.List;

public class Hand {
	private List<Card> hand;

	public Hand(List<Card> hand) {
		this.hand = hand;
	}

	public int getTotal() {
		int total = 0;
		for (Card card : hand) {
			total += card.getValue();
		}
		return total;
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

	@Override
	public String toString() {
		Iterable<String> cards = hand.stream().map(Card::getRank).toList();
		return String.join(", ", cards);
	}
}
