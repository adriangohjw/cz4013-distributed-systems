package cz4013.facilitybooking.client;

import java.util.Scanner;
import java.time.LocalTime;

public class UserInputTools {
	private static Scanner scanner = new Scanner(System.in);
	
	public static int inputInt() {
		try {
			//DO NOT use nextint because it does not read \n
			return Integer.parseInt(scanner.nextLine());
		} catch (Exception e) {
			System.out.println("Invalid input - please enter an integer!");
			return inputInt();
		}
	}
	
	public static String inputString() {
		try {
			String userInput = scanner.nextLine();
			while (userInput.equals("")) {
				System.out.println("Empty input. Please try again.");
				userInput = scanner.nextLine();
			}
			return userInput;
		} catch (Exception e) {
			System.out.println("Invalid input! Please try again.");
			return inputString();
		}
	}
	
	public static LocalTime inputTime() {
		try {
			String userInput = scanner.nextLine();
			String hourStr = userInput.substring(0,2);
			String minStr = userInput.substring(2,4);
			return LocalTime.of(Integer.parseInt(hourStr), Integer.parseInt(minStr));
		} catch (Exception e) {
			System.out.println("Invalid input! Please enter the time in the format specified.");
			return inputTime();
		}
	}
	
	public static void closeScanner() {
		scanner.close();
	}
}
