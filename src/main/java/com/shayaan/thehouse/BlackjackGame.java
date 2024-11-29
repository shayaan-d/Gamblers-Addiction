package com.shayaan.thehouse;


import com.fasterxml.jackson.annotation.JsonGetter;

public class BlackjackGame {

	private Deck deck;
	private Hand dealerHand;
	private Hand playerHand;
	private int bet;

	public BlackjackGame(Deck deck, Hand dealerHand, Hand playerHand, int bet) {
		this.deck = deck;
		this.dealerHand = dealerHand;
		this.playerHand = playerHand;
		this.bet = bet;
	}

	public static boolean isBust(Hand hand) {
		int total = 0;
		for (Card c : hand.getHand()) {
			total += c.getValue();
		}
		return total > 21;
	}

	@JsonGetter
	public Deck getDeck() {
		return deck;
	}
	@JsonGetter
	public Hand getDealerHand() {
		return dealerHand;
	}

	@JsonGetter
	public int getBet() {
		return bet;
	}

	@JsonGetter
	public Hand getPlayerHand() {
		return playerHand;
	}
}
