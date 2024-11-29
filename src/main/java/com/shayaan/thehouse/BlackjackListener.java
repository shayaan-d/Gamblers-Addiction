package com.shayaan.thehouse;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.ButtonStyle;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.util.ArrayList;
import java.util.Base64;

public class BlackjackListener implements SlashCommandCreateListener, MessageComponentCreateListener {
	@Override
	public void onSlashCommandCreate(SlashCommandCreateEvent event) {
		Logger.log(event.getSlashCommandInteraction().getUser().getName() + " used \"Blackjack\" command");
		if (event.getSlashCommandInteraction().getCommandName().equals("blackjack")) {
			Deck deck = new Deck();
			Hand playerHand = deck.dealHand();
			Hand dealerHand = deck.dealHand();
			long bet = event.getSlashCommandInteraction().getOptions().getFirst().getLongValue().get(); // we know there should be a bet otherwise discord is crashing out somehow
			EmbedBuilder embedBuilder = new EmbedBuilder();
			embedBuilder.setTitle("Blackjack w/ " + event.getSlashCommandInteraction().getUser().getName());
			embedBuilder.addField("Dealer's Hand", dealerHand.getCard(0).getRank() + ", ?", false);
			embedBuilder.addField("Your Hand", playerHand.getCard(0).getRank() + ", " + playerHand.getCard(1).getRank(), false);
			embedBuilder.addField("Your Total", Integer.toString(playerHand.getCard(0).getValue() + playerHand.getCard(1).getValue()), true);
			embedBuilder.addField("Bet", ":coin: " + bet, true);
			MessageBuilder messageBuilder = new MessageBuilder();

			StringBuilder dataBuilder = new StringBuilder();
			for (Card c : deck.getDeck()) {
				dataBuilder.append(c.getValue()).append(" ");
			}

			dataBuilder.append("| " + dealerHand.getCard(1).getValue());

			String data = dataBuilder.toString();

			String encodedData = Base64.getEncoder().encodeToString(data.getBytes());
			embedBuilder.setFooter(encodedData);

			Logger.log("Sent (Encoded) Data: " + encodedData);
			Logger.log("Sent Data: " + data);

			messageBuilder.addEmbed(embedBuilder);
			messageBuilder.addComponents(
				ActionRow.of(
					Button.create("hit", ButtonStyle.PRIMARY, "Hit"),
					Button.create("stand", ButtonStyle.PRIMARY, "Stand"),
					Button.create("dd", ButtonStyle.PRIMARY, "Double Down")

				)
			);
			event.getSlashCommandInteraction().createImmediateResponder().respond();
			messageBuilder.send(event.getSlashCommandInteraction().getChannel().get());
		}
	}

	@Override
	public void onComponentCreate(MessageComponentCreateEvent event) {
		String customId = event.getMessageComponentInteraction().getCustomId();
		Logger.log("Button clicked: " + event.getMessageComponentInteraction().getUser().getName() + " clicked " + customId);
		event.getMessageComponentInteraction().acknowledge();

		Embed embed = event.getMessageComponentInteraction().getMessage().getEmbeds().getFirst();
		String encodedData = embed.getFooter().get().getText().get();

		String data = new String(Base64.getDecoder().decode(encodedData));

		Logger.log("Received (encoded) Data: " + encodedData);
		Logger.log("Received Data: " + data);

		// split data into dealer hand and deck;

		String deckData = data.substring(0, data.indexOf("|"));
		String dealerHandData = data.substring(data.indexOf("|") + 1);
		// deal w/ deck stuff

		// skib

		ArrayList<Card> deckList = new ArrayList<>();
		for (String s : deckData.split(" ")) {
			deckList.add(new Card(s));
		}

		Deck deck = new Deck(deckList);

		Logger.log("Deck: \"" + deck + "\"");

		// deal w/ hand stuff

		ArrayList<Card> dealerHandList = new ArrayList<>();
		dealerHandData = dealerHandData.trim();
		Logger.log("Dealer Hand Data: \"" + dealerHandData + "\"");

		// into a gam

		switch (customId) {
			case "hit":
				break;
			case "stand":
				break;
			case "dd":
				break;
			default:
				Logger.logError("Button not recognized! Button ID: " + customId);
		}
	}
}
