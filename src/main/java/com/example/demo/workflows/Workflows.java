package com.example.demo.workflows;

import com.example.demo.constants.Constants;
import com.example.demo.exception.NotEnoughSeatsAvailableException;
import com.example.demo.map.CinemaMap;
import com.example.demo.validators.Validator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Workflows {
    public static HashMap<String, Object> settings = new HashMap<>();
    public static Scanner scanner = new Scanner(System.in);

    public static String getMovieTitle() {
        return settings.get(Constants.TITLE).toString();
    }

    public static int getTotalSeatsAvailable() {
        return (int) settings.get(Constants.TOTAL_SEATS_AVAILABLE);
    }

    public static int checkTotalSeatsAvailableAfterReservation(int amountToDecrease) {
        int amountLeft = getTotalSeatsAvailable() - amountToDecrease;
        if (amountLeft < 0) {
            throw new NotEnoughSeatsAvailableException(String.format("Sorry, there are only %d seats available.", getTotalSeatsAvailable()));
        }
        return amountLeft;
    }

    public static void setTotalSeatsAvailable(int amountToSet) {
        settings.put(Constants.TOTAL_SEATS_AVAILABLE, amountToSet);
    }

    public static int getId() {
        return (int) settings.get(Constants.ID);
    }

    public static void increaseId() {
        settings.put(Constants.ID, getId() + 1);
    }

    public static int getRows() {
        return (int) settings.get(Constants.ROWS);
    }

    public static int getSeatsPerRow() {
        return (int) settings.get(Constants.SEATS_PER_ROW);
    }

    public static void setCurrentBookingId(String currentBookingId) {
        settings.put(Constants.CURRENT_BOOKING_ID, currentBookingId);
    }

    public static String getCurrentBookingId() {
        return (String) settings.get(Constants.CURRENT_BOOKING_ID);
    }

    public static void setMaximumRowAllowed() {
        char maxRowChar = Constants.ALPHABETS[getRows() - 1].charAt(0);
        settings.put(Constants.MAXIMUM_ROW_ALLOWED, maxRowChar);
    }

    public static char getMaximumRowAllowed() {
        return (char) settings.get(Constants.MAXIMUM_ROW_ALLOWED);
    }

    public static String[][] getCinemaMap() {
        return (String[][]) settings.get(Constants.CINEMA_MAP);
    }

    public static void setCinemaMap(String[][] cinemaMap) {
        settings.put(Constants.CINEMA_MAP, cinemaMap);
    }

    public static int getMiddleMostCol() {
        return (int) settings.get(Constants.MIDDLEMOST_COL);
    }

    public static void setMiddleMostCol(int col) {
        settings.put(Constants.MIDDLEMOST_COL, col);
    }

    public static HashSet<String> getActiveBookingIds() {
        return (HashSet<String>) settings.get(Constants.ACTIVE_BOOKING_IDS);
    }

    public static void addActiveBookingIds(String newBookingId) {
        getActiveBookingIds().add(newBookingId);
    }

    public static void initialiseSettings() {
        while (true) {
            System.out.println(Constants.DEFINE_TITLE_AND_SEATING_MAP);
            String userInput = scanner.nextLine();
            // clean up input
            if (Validator.isBlank(userInput)) {
                System.out.println("Invalid input. Input is not in [Title] [Row] [SeatsPerRow] format.");
                continue;
            }
            userInput = userInput.trim();
            String[] parts = userInput.split("\\s+");
            // Validate input length
            if (parts.length < 3) {
                System.out.println("Input must contain a movie title followed by at least two numbers.");
                continue;
            }

            int rows = 0;
            int seatsPerRow = 0;
            try {
                seatsPerRow = Integer.parseInt(parts[parts.length - 1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid SeatsPerRow input. [SeatsPerRow] must be in number format.");
            }
            try {
                rows = Integer.parseInt(parts[parts.length - 2]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid Row input. [Row] must be in number format.");
                continue;
            }

            if (rows == 0 || rows > 26) {
                System.out.println("Invalid Row input. [Row] must be between 1 and 26.");
                continue;
            }

            if (seatsPerRow == 0 || seatsPerRow > 50) {
                System.out.println("Invalid SeatsPerRow input. [SeatsPerRow] must be between 1 and 50");
                continue;
            }

            StringBuilder titleBuilder = new StringBuilder();
            for (int i = 0; i < parts.length - 2; i++) {
                if (i > 0) {
                    titleBuilder.append(" ");
                }
                titleBuilder.append(parts[i]);
            }
            settings.put(Constants.TITLE, titleBuilder);
            settings.put(Constants.ROWS, rows);
            settings.put(Constants.SEATS_PER_ROW, seatsPerRow);
            settings.put(Constants.TOTAL_SEATS_AVAILABLE, rows * seatsPerRow);
            settings.put(Constants.CINEMA_MAP, new String[rows][seatsPerRow]);
            settings.put(Constants.ID, 1);
            setMiddleMostCol(deriveMiddleMostCol(seatsPerRow));
            setMaximumRowAllowed();
            settings.put(Constants.ACTIVE_BOOKING_IDS, new HashSet<String>());
            CinemaMap.generateEmptyCinemaMap();
            break;
        }
    }

    public static int deriveMiddleMostCol(int seatsPerRow) {
        if (seatsPerRow % 2 == 0) {
            return seatsPerRow / 2;
        } else {
            return (seatsPerRow / 2) + 1;
        }
    }


    public static String displayMainMenu() {
        System.out.println(Constants.WELCOME);
        System.out.printf((Constants.OPTION_ONE) + "%n", getMovieTitle(), getTotalSeatsAvailable());
        System.out.println(Constants.OPTION_TWO);
        System.out.println(Constants.OPTION_THREE);
        System.out.println(Constants.PLS_ENTER_SELECTION);
        System.out.print(Constants.POINT_RIGHT_WITH_SPACE);
        return scanner.nextLine();
    }

    public static void ticketBookingFlow() {
        generateBookingId();
        while (true) {
            System.out.println("Enter number of tickets to book, or enter blank to go back to main menu:");
            String input = scanner.nextLine();
            if (Validator.isBlank(input)) {
                break;
            }
            int noOfSeatsToBook = Validator.isValidIntegerInput(input, 0);
            if (noOfSeatsToBook == 0) {
                continue;
            }
            int seatsLeft = 0;
            try {
                seatsLeft = checkTotalSeatsAvailableAfterReservation(noOfSeatsToBook);
            } catch (NotEnoughSeatsAvailableException e) {
                System.out.println(e.getMessage());
                continue;
            }

            System.out.printf("Successfully reserved %d %s tickets.%n", noOfSeatsToBook, getMovieTitle());
            System.out.printf("Booking id: %s%n", getCurrentBookingId());
            System.out.println("Selected seats:");
            CinemaMap.setReservationForCurrentUser(noOfSeatsToBook, null);
            // this while loop is for when user wants a new seat position
            while (true) {
                CinemaMap.display(getCurrentBookingId());
                System.out.println("Enter blank to accept seat selection, or enter a new seating position:");
                System.out.print(Constants.POINT_RIGHT_WITH_SPACE);
                String seatSelection = scanner.nextLine();
                if (Validator.isBlank(seatSelection)) {
                    updateSettings(seatsLeft);
                    break;
                }
                if (!Validator.isValidSeatSelection(seatSelection, getMaximumRowAllowed(), getSeatsPerRow())) {
                    continue;
                }
                if (seatTaken(seatSelection)) {
                    System.out.printf("Seat %s has already been taken. Please select another seat.%n%n", seatSelection);
                    continue;
                }
                CinemaMap.clearExistingReservationForBookingId(getCurrentBookingId());
                CinemaMap.setReservationForCurrentUser(noOfSeatsToBook, seatSelection);
            }
            break;
        }
    }

    public static boolean seatTaken(String seatSelection) {
        String[] row = (String[]) settings.get((String.valueOf(seatSelection.charAt(0))));
        String trimmedStr = seatSelection.substring(1).replaceFirst("^0+", "");
        int seatNumberInt = Integer.parseInt(trimmedStr);
        return !Validator.isBlank(row[seatNumberInt - 1]);
    }

    public static String generateBookingId() {
        int length = 4;
        char padChar = '0';

        StringBuilder sb = new StringBuilder();
        sb.append(Constants.GIC);
        int paddingLength = length - String.valueOf(getId()).length();

        for (int i = 0; i < paddingLength; i++) {
            sb.append(padChar);
        }
        sb.append(getId());
        setCurrentBookingId(sb.toString());
        return sb.toString();
    }

    public static void updateSettings(int amountLeft) {
        System.out.printf("Booking id: %s confirmed.%n", getCurrentBookingId());
        // set active list of booking ids
        addActiveBookingIds(getCurrentBookingId());
        // only set available seats left after booking confirmed.
        setTotalSeatsAvailable(amountLeft);
        // increment id only after booking confirmed
        increaseId();
    }

    public static void checkBookingsFlow() {
        while (true) {
            System.out.println("Enter booking id, or enter blank to go back to main menu:");
            System.out.print(Constants.POINT_RIGHT_WITH_SPACE);
            String input = scanner.nextLine();
            if (Validator.isBlank(input)) {
                break;
            }
            if (!Validator.isValidBookingId(input, getActiveBookingIds())) {
                System.out.printf("Booking id %s does not exist. Please enter a valid booking id.%n%n", input);
                continue;
            }
            CinemaMap.display(input);
            System.out.println();
        }
    }
}

