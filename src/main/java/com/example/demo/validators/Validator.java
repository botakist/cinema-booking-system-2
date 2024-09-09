package com.example.demo.validators;

import java.util.HashSet;
import java.util.regex.Pattern;

public class Validator {

    public static boolean isBlank(String input) {
        return input == null || input.isEmpty() || Pattern.matches("^\\s*$", input);
    }

    public static boolean isValidSeatSelection(String seatSelection, int maximumRowAllowed, int seatsPerRow) {
        char row = getRowPositionAlphabet(seatSelection);
        if (row < 'A') {
            // input row cannot be "smaller" than the first row ('A')
            System.out.printf("Invalid seat selection %s row %c. Please enter a valid row.", seatSelection, row);
            return false;
        }
        // input row cannot be larger than the initial row settings
        if (row > maximumRowAllowed) {
            System.out.printf("Invalid seat selection %s row %c. Row input cannot exceed %c.%n", seatSelection, row, maximumRowAllowed);
            return false;
        }
        // Trim any leading '0' characters
        int seatNumberInt = getSeatPosition(seatSelection);
        if (seatNumberInt == 0) { // invalid seat number, since it starts from 1 onwards.
            return false;
        }
        if (seatNumberInt > seatsPerRow) {
            System.out.printf("Invalid seat selection %s seat number %d. Seat number cannot be more than the maximum allowed %d.%n", seatSelection, seatNumberInt, seatsPerRow);
            return false;
        }
        return true;
    }
    public static char getRowPositionAlphabet(String seatSelection) {
        return seatSelection.charAt(0);
    }
    public static int getSeatPosition(String seatSelection) {
        String trimmedStr = seatSelection.substring(1).replaceFirst("^0+", "");
        try {
            return Integer.parseInt(trimmedStr);
        } catch (NumberFormatException e) {
            System.out.printf("Invalid seat selection %s seat number: %s. Please enter a valid seat number.%n", seatSelection, trimmedStr);
        }
        return 0;
    }


    public static int isValidIntegerInput(String userInput, int max) {
        if (Validator.isBlank(userInput)) {
            System.out.println("Invalid selection. Please select a valid option.");
            return 0;
        }
        int input = 0;
        try {
            input = Integer.parseInt(userInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid option selected. Please select a valid option.");
            return 0;
        }
        if (input < 1) {
            System.out.printf("%d is not a valid option. Please enter positive numbers only.%n", input);
            return 0;
        }
        if (max == 0) {
            // use a different validation message
            return input;
        }
        if (input > max) {
            System.out.printf("%d is not a valid option. Please select options between 1 and %d only.%n", input, max);
            return 0;
        }
        return input;
    }

    public static boolean isValidBookingId(String input, HashSet<String> activeBookingIds) {
        return activeBookingIds.contains(input);
    }
}
