package com.shayaan.thehouse;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.util.Arrays;

public class Main {

	public static DiscordApi api;

	public static void main(String[] args) throws InterruptedException {

		String token = System.getenv("DISCORD_TOKEN");
		api = new DiscordApiBuilder()
				.setToken(token)
				.setAllIntents()
				.login().join();

		Logger.logSuccess("Logged in as " + api.getYourself().getName());

		SlashCommand.with("blackjack", "Play a game of blackjack", Arrays.asList(
				SlashCommandOption.create(SlashCommandOptionType.LONG, "bet", "The amount of money to bet", true)
		)).createGlobal(api).join();
		Logger.logSuccess("Created blackjack command");
		BlackjackListener listener = new BlackjackListener();
		api.addSlashCommandCreateListener(listener);
		api.addMessageComponentCreateListener(listener);

	}
}
