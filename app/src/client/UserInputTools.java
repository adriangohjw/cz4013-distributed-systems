package client;

import java.util.Scanner;
import java.util.HashSet;
import java.time.LocalTime;

/**
 * Handles user input for various types of data
 */
public class UserInputTools {
	private static Scanner scanner = new Scanner(System.in);
	
	
	/** 
	 * Checks if user input is a valid integer and returns an integer
	 * @return int
	 */
	public static int inputInt() {
		try {
			//DO NOT use nextint because it does not read \n
			return Integer.parseInt(scanner.nextLine());
		} catch (Exception e) {
			System.out.println("Invalid input - please enter an integer!");
			return inputInt();
		}
	}
	
	
	/** 
	 * Checks if user input is a valid non-zero length string and returns a string
	 * @return String
	 */
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
	
	
	/** 
	 * Checks if user input is a valid day of the week and returns a string of values for the days of the week
	 * @return String Numeric values of days of the week separated by commas
	 */
	public static String inputDayOfWeek() {
		HashSet<Integer> days = new HashSet<Integer>();
		String dayOfWeekPrompt = "Please select a day.\n" +
				"Select an option from [1-7]:\n" +
				"1. Monday\n" +
				"2. Tuesday\n" +
				"3. Wednesday\n" +
				"4. Thursday\n" +
				"5. Friday\n" +
				"6. Saturday\n" +
				"7. Sunday\n";
		System.out.print(dayOfWeekPrompt);

		int dayOfWeekChoice = inputInt();
		
		while (dayOfWeekChoice < 1 | dayOfWeekChoice > 7) {
			System.out.println("Invalid input - please enter an integer from 1-7!");
			dayOfWeekChoice = inputInt();
		}
		
		days.add(dayOfWeekChoice);
		while (true) {
			System.out.println("Please enter other days you would also like to check.");
			System.out.println("If you are done, please enter 0.");
			dayOfWeekChoice = inputInt();
			while (dayOfWeekChoice < 0 | dayOfWeekChoice > 7) {
				System.out.println("Invalid input - please enter an integer from 1-7, or 0 to quit!");
				dayOfWeekChoice = inputInt();
			}
			if (dayOfWeekChoice == 0) {
				break;
			} else {
				days.add(dayOfWeekChoice);
			}
		}
		
		String daysString = days.toString(); //includes square brackets and spaces
		
		return daysString.substring(1,daysString.length() - 1).replaceAll("\\s", "");
	}
	
	
	/** 
	 * Checks if user input is a valid time and returns LocalTime
	 * @return LocalTime
	 */
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
	
	/**
	 * Closes the current instance of Scanner
	 */
	public static void closeScanner() {
		scanner.close();
	}
}
