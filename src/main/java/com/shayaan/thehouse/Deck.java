package com.shayaan.thehouse;

import java.util.*;

public class Deck {
	private List<Card> deck;
	private static final String[] RANKS = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};

	public Deck() {
		deck = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			for (String rank : RANKS) {
				deck.add(new Card(rank));
			}
		}
		Collections.shuffle(deck);
	}

	public Deck(List<Card> deck) {
		this.deck = deck;
	}

	public Card dealCard() {
		if (deck.isEmpty()) {
			return null;
		}
		return deck.remove(deck.size() - 1);
	}

	public List<Card> getDeck() {
		return deck;
	}

	public int remainingCards() {
		return deck.size();
	}

	@Override
	public String toString() {
		return "Deck{" +
				deck +
				'}';
	}

	public Hand dealHand() {
		ArrayList<Card> hand = new ArrayList<>();
		hand.add(dealCard());
		hand.add(dealCard());
		return new Hand(hand);
	}
}

