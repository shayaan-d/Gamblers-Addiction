package com.shayaan.thehouse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static void log(String message) {
		System.out.println(formatter.format(LocalDateTime.now()) + "| MSG | " + message);
	}

	public static void log(String message, Object... args) {
		System.out.printf(formatter.format(LocalDateTime.now()) + " | MSG | " + message, args);
	}

	public static void logSuccess(String message) {
		System.out.println(formatter.format(LocalDateTime.now()) + "| SUS | " + message);
	}

	public static void logSuccess(String message, Object... args) {
		System.out.printf(formatter.format(LocalDateTime.now()) + " | SUS | " + message , args);
	}

	public static void logWarn(String message) {
		System.out.println(formatter.format(LocalDateTime.now()) + " | WRN | " + message);
	}

	public static void logWarn(String message, Object... args) {
		System.out.printf(formatter.format(LocalDateTime.now()) + "| WRN | " + message , args);
	}

	public static void logError(String message) {
		System.err.println(formatter.format(LocalDateTime.now()) + " | ERR | " + message);
	}

	public static void logError(String message, Object... args) {
		System.err.printf(formatter.format(LocalDateTime.now()) + "| ERR |" + message, args);
	}
}
