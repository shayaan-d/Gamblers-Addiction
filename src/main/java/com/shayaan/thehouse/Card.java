package com.shayaan.thehouse;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Card {
	private String rank;

	public Card(String rank) {
		this.rank = rank;
	}

	@JsonGetter
	public String getRank() {
		return rank;
	}

	@JsonIgnore
	public int getValue() {
		switch (rank) {
			case "Jack":
			case "Queen":
			case "King":
				return 10;
			case "Ace":
				return 1;
			default:
				return Integer.parseInt(rank);
		}
	}

	@Override
	public String toString() {
		return rank;
	}
}
