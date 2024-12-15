package com.shayaan.thehouse;

import com.shayaan.logcryption.Logcryption;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.ButtonStyle;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.embed.EmbedField;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class BlackjackListener implements SlashCommandCreateListener, MessageComponentCreateListener {
	@Override
	public void onSlashCommandCreate(SlashCommandCreateEvent event) {
		try {
			SlashCommandInteraction interaction = event.getSlashCommandInteraction();
			String username = interaction.getUser().getName();
			Logger.log(username + " used \"Blackjack\" command");
			if (interaction.getCommandName().equals("blackjack")) {
				Deck deck = new Deck();
				Hand playerHand = deck.dealHand();
				Hand dealerHand = deck.dealHand();

				Optional<Long> betMaybe = interaction.getOptions().getFirst().getLongValue();
				if (betMaybe.isEmpty()) {
					Logger.logError("Oh No! A bet wasn't provided! This shouldn't happen! Discord is broken! The world is on fire and we're all going to die! User: " + username);
					interaction.createImmediateResponder().setFlags(MessageFlag.EPHEMERAL).setContent("Error! Bet not provided! PLEASE CONTACT THE DEV @whatthesigama. THIS IS AN EMERGENCY").respond();
					return;
				}

				long bet = betMaybe.get(); // we know there should be a bet otherwise discord is crashing out somehow
				EmbedBuilder embedBuilder = createEmbed(username, dealerHand, playerHand, bet, deck);
				MessageBuilder messageBuilder = new MessageBuilder();
				messageBuilder.addEmbed(embedBuilder);
				messageBuilder.addComponents(
						ActionRow.of(
								Button.create("hit", ButtonStyle.SUCCESS, "Hit"),
								Button.create("stand", ButtonStyle.PRIMARY, "Stand"),
								Button.create("dd", ButtonStyle.DANGER, "Double Down")

						)
				);
				String link = messageBuilder.send(interaction.getChannel().get()).join().getLink().toString();
				interaction.createImmediateResponder().setFlags(MessageFlag.EPHEMERAL, MessageFlag.LOADING)
						.setContent("[Blackjack Game Started!](" + link + ")").respond();
			}
		} catch (RuntimeException e) {
			Logger.logError("Error in Blackjack command! " + e.getMessage());
			event.getSlashCommandInteraction().createImmediateResponder().setFlags(MessageFlag.EPHEMERAL).setContent("Error! Please contact the dev @whatthesigama").respond();
		}
	}

	@Override
	public void onComponentCreate(MessageComponentCreateEvent event) {
		Deck deck;
		Hand playerHand;
		Hand dealerHand;
		long bet;
		String customId;
		String username;
		// Data collection
		{
			customId = event.getMessageComponentInteraction().getCustomId();
			String clickerName = event.getMessageComponentInteraction().getUser().getName();
			Logger.log("Button clicked: " + clickerName + " clicked " + customId);

			Embed embed;
			try{
				embed = event.getMessageComponentInteraction().getMessage().getEmbeds().getFirst();
			} catch (NoSuchElementException e) {
				event.getMessageComponentInteraction().createImmediateResponder().setFlags(MessageFlag.EPHEMERAL).setContent("Error! Has the embed closed?").respond();
				Logger.logError(clickerName + "clicked empty embed with buttons!");
				return;
			}

			username = embed.getTitle().get().substring(13);

			if (!clickerName.equals(username)) {
				event.getMessageComponentInteraction().createImmediateResponder().setFlags(MessageFlag.EPHEMERAL).setContent("It's not your turn!").respond();
				return;
			}

			String encodedData = embed.getFooter().get().getText().get();

			String data = Logcryption.decode(encodedData);

			// split data into dealer hand and deck;

			String deckData = data.substring(0, data.indexOf("|"));
			String dealerHandData = data.substring(data.indexOf("|") + 1);

			// Deck

			ArrayList<Card> deckList = new ArrayList<>();
			for (String s : deckData.split(" ")) {
				deckList.add(new Card(s));
			}

			deck = new Deck(deckList);

			// Dealer Hand

			ArrayList<Card> dealerHandList = new ArrayList<>();
			dealerHandData = dealerHandData.trim();

			List<EmbedField> fields = embed.getFields();
			EmbedField dealerHandField = fields.get(0);
			String dealerHandString = dealerHandField.getValue();

			dealerHandList.add(new Card(dealerHandData));
			dealerHandList.add(new Card(dealerHandString.substring(0, dealerHandString.indexOf(","))));

			dealerHand = new Hand(dealerHandList);

			// Player Hand

			ArrayList<Card> playerHandList = new ArrayList<>();

			EmbedField playerHandField = fields.get(1);
			String playerHandString = playerHandField.getValue();
			for(String s : playerHandString.split(", ")) {
				playerHandList.add(new Card(s));
			}

			playerHand = new Hand(playerHandList);

			// Bet

			EmbedField betField = fields.get(3);
			String betFieldValue = betField.getValue();
			bet = Long.parseLong(betFieldValue.substring(7));
		}

		switch (customId) {
			case "dd":
				bet *= 2;
				hit(event, playerHand, deck, dealerHand, username, bet);
				break;
			case "hit":
				hit(event, playerHand, deck, dealerHand, username, bet);
				break;
			case "stand":
				if (dealerHand.getTotal() < 17) {
					while (dealerHand.getTotal() < 17) {
						dealerHand.dealCard(deck.dealCard());
					}
				}
				if (winCheck(event, playerHand, dealerHand)) return;
				if (dealerHand.getTotal() > playerHand.getTotal()) {
					handleGameResult(event, "You Lose!", playerHand, dealerHand, Color.red);
				} else if (dealerHand.getTotal() == playerHand.getTotal()) {
					handleGameResult(event, "It's a Tie?!", playerHand, dealerHand, Color.yellow);
				} else {
					handleGameResult(event, "You Win!", playerHand, dealerHand, Color.green);
				}
				break;
			default:
				Logger.logError("Button not recognized! Button ID: " + customId);
		}
		event.getMessageComponentInteraction().acknowledge();
	}

	private void hit(MessageComponentCreateEvent event, Hand playerHand, Deck deck, Hand dealerHand, String username, long bet) {
		playerHand.dealCard(deck.dealCard());
		if (dealerHand.getTotal() < 17) {
			dealerHand.dealCard(deck.dealCard());
		}

		if (winCheck(event, playerHand, dealerHand)) return;
		event.getMessageComponentInteraction()
				.getMessage()
				.createUpdater()
				.removeAllComponents()
				.addEmbed(createEmbed(username, dealerHand, playerHand, bet, deck))
				.addComponents(
						ActionRow.of(
								Button.create("hit", ButtonStyle.SUCCESS, "Hit"),
								Button.create("stand", ButtonStyle.PRIMARY, "Stand")
						)
				)
				.applyChanges();
	}

	private void handleGameResult(MessageComponentCreateEvent event, String title, Hand playerHand, Hand dealerHand, Color color) {
		event.getMessageComponentInteraction()
				.getMessage()
				.createUpdater()
				.removeAllComponents()
				.addEmbed(new EmbedBuilder().setTitle(title)
						.addField("Your Hand", playerHand.toString())
						.addField("Your Total", "" + playerHand.getTotal())
						.addField("Dealer Hand", dealerHand.toString())
						.addField("Dealer Total", "" + dealerHand.getTotal())
						.setColor(color)
				).applyChanges();
		event.getMessageComponentInteraction().acknowledge();
	}

	private static boolean winCheck(MessageComponentCreateEvent event, Hand playerHand, Hand dealerHand) {
		if (playerHand.getTotal() > 21 || dealerHand.getTotal() == 21) {
			event.getMessageComponentInteraction()
					.getMessage()
					.createUpdater()
					.removeAllComponents()
					.addEmbed(new EmbedBuilder().setTitle("You Lose!")
							.addField("Your Hand", playerHand.toString())
							.addField("Your Total", "" + playerHand.getTotal())
							.addField("Dealer Hand", dealerHand.toString())
							.addField("Dealer Total", "" + dealerHand.getTotal())
							.setColor(Color.red)
					).applyChanges();
			event.getMessageComponentInteraction().acknowledge();
			return true;
		}

		if (playerHand.getTotal() == 21 || dealerHand.getTotal() > 21) {
			event.getMessageComponentInteraction()
					.getMessage()
					.createUpdater()
					.removeAllComponents()
					.addEmbed(new EmbedBuilder().setTitle("You Win!")
							.addField("Your Hand", playerHand.toString())
							.addField("Your Total", "" + playerHand.getTotal())
							.addField("Dealer Hand", dealerHand.toString())
							.addField("Dealer Total", "" + dealerHand.getTotal())
							.setColor(Color.green)
					).applyChanges();
			return true;
		}
		return false;
	}

	public EmbedBuilder createEmbed(String username, Hand dealerHand, Hand playerHand, long bet, Deck deck) {
		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setTitle("Blackjack w/ " + username);
		embedBuilder.addField("Dealer's Hand", dealerHand.getCard(0).getRank() + ", ?", false);
		embedBuilder.addField("Your Hand", playerHand.toString(), false);
		embedBuilder.addField("Your Total", String.valueOf(playerHand.getTotal()), true);
		embedBuilder.addField("Bet", ":coin: " + bet, true);

		StringBuilder dataBuilder = new StringBuilder();
		for (Card c : deck.getDeck()) {
			dataBuilder.append(c.getRank()).append(" ");
		}

		dataBuilder.append("| ").append(dealerHand.getCard(1).getRank());

		String data = dataBuilder.toString();

		String encodedData = Logcryption.encode(data);
		embedBuilder.setFooter(encodedData);
		return embedBuilder;
	}
}
