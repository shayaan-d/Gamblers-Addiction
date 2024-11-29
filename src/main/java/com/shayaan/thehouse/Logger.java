package com.shayaan.thehouse;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Scanner;

public class Logger {

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static final File file = new File("log.txt");

	static {
		if (file.delete()) {
			try {
				file.createNewFile();
				System.out.println(ANSI_GREEN + formatter.format(LocalDateTime.now()) + "| SUS | Created log file!"+ ANSI_RESET);
			} catch (IOException e) {
				throw new RuntimeException("Failed to create log file", e);
			}
		}
		logSuccess("Logger initialized");
	}

	public static void log(String message) {
		try {
			Files.write(file.toPath(), (formatter.format(LocalDateTime.now()) + "| MSG | " + message + "\n").getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.println(formatter.format(LocalDateTime.now()) + "| MSG | " + message);
	}

	public static void log(String message, Object... args) {
		try {
			Files.write(file.toPath(), String.format(formatter.format(LocalDateTime.now()) + "| MSG | " + message + "\n", args).getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.printf(formatter.format(LocalDateTime.now()) + " | MSG | " + message, args);
	}

	public static void logSuccess(String message) {
		try {
			Files.write(file.toPath(), (formatter.format(LocalDateTime.now()) + "| SUS | " + message + "\n").getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.println(ANSI_GREEN + formatter.format(LocalDateTime.now()) + "| SUS | " + message + ANSI_RESET);
	}

	public static void logSuccess(String message, Object... args) {
		try {
			Files.write(file.toPath(), String.format(formatter.format(LocalDateTime.now()) + "| SUS | " + message + "\n", args).getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.printf(ANSI_GREEN + formatter.format(LocalDateTime.now()) + " | SUS | " + message + ANSI_RESET, args);
	}

	public static void logWarn(String message) {
		try {
			Files.write(file.toPath(), (formatter.format(LocalDateTime.now()) + "| WRN | " + message + "\n").getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.println(ANSI_YELLOW + formatter.format(LocalDateTime.now()) + " | WRN | " + message + ANSI_RESET);
	}

	public static void logWarn(String message, Object... args) {
		try {
			Files.write(file.toPath(), String.format(formatter.format(LocalDateTime.now()) + "| WRN | " + message + "\n", args).getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.printf(ANSI_YELLOW + formatter.format(LocalDateTime.now()) + "| WRN | " + message + ANSI_RESET, args);
	}

	public static void logError(String message) {
		try {
			Files.write(file.toPath(), (formatter.format(LocalDateTime.now()) + "| ERR | " + message + "\n").getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.err.println(formatter.format(LocalDateTime.now()) + " | ERR | " + message);
	}

	public static void logError(String message, Object... args) {
		try {
			Files.write(file.toPath(), String.format(formatter.format(LocalDateTime.now()) + "| ERR | " + message + "\n", args).getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.err.printf(formatter.format(LocalDateTime.now()) + "| ERR |" + message, args);
	}
}
