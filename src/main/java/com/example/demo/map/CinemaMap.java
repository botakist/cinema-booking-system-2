package com.example.demo.map;

import com.example.demo.constants.Constants;
import com.example.demo.validators.Validator;
import com.example.demo.workflows.Workflows;

import java.util.ArrayList;

import static com.example.demo.constants.Constants.ALPHABETS;

public class CinemaMap {

    public static String[] getCurrentRow(int i) {
        return (String[]) Workflows.settings.get(ALPHABETS[i - 1]);
    }

    public static int getCurrentRowIndex(String seatPosition) {
        if (Validator.isBlank(seatPosition)) {
            return 1;
        }
        String letter = String.valueOf(seatPosition.charAt(0));
        for (int i = 0; i < ALPHABETS.length; i++) {
            if (ALPHABETS[i].equals(letter)) {
                return i + 1;
            }
        }
        return -1;
    }

    public static void generateEmptyCinemaMap() {
        for (int i = Workflows.getRows(); i > 0; i--) {
            Workflows.settings.put(ALPHABETS[i - 1], new String[Workflows.getSeatsPerRow()]);
        }
    }

    public static void display(String bookingId) {
        System.out.println(Constants.PRINT_SCREEN_MESSAGE);
        for (int i = Workflows.getRows(); i > 0; i--) {
            StringBuilder sb = new StringBuilder();
            String rowInAlphabet = ALPHABETS[i - 1];
            sb.append(" " + rowInAlphabet + " "); // display the row in descending order
            String[] rowArr = (String[]) Workflows.settings.get(rowInAlphabet);
            for (String row : rowArr) {
                sb.append(" " + deriveCharToDisplay(row, bookingId) + " "); // display the seats for the row in asc order (1, 2, ..., X)
            }
            System.out.println(sb);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("   ");
        for (int i = 0; i < Workflows.getSeatsPerRow(); i++) {
            sb.append(" ").append(i + 1).append(" ");
        }
        System.out.println(sb);
    }

    private static char deriveCharToDisplay(String rowStr, String bookingIdToDisplay) {
        if (Validator.isBlank(bookingIdToDisplay)) {
            bookingIdToDisplay = Workflows.getCurrentBookingId();
        }
        if (Validator.isBlank(rowStr)) {
            return '.';
        }
        if (rowStr.contains(bookingIdToDisplay)) {
            return 'o';
        } else {
            return '#';
        }
    }

    public static void setReservationForCurrentUser(int noOfSeatsToBook, String seatPosition) {
        int noOfSeatsLeft = noOfSeatsToBook;
        // derive available position for current user to sit
        int seatsPerRow = Workflows.getSeatsPerRow(); // use for overflow to next row
        int totalRows = Workflows.getRows();
        int alphabetIndex = getCurrentRowIndex(seatPosition);
        for (int aIndex = alphabetIndex; aIndex <= totalRows; aIndex++) {
            ArrayList<Integer> reserved;
            String[] currentRowArr = getCurrentRow(aIndex);
            reserved = deriveAvailableSeatsOnCurrentRow(noOfSeatsLeft, seatPosition, currentRowArr);
            for (Integer seatNo : reserved) {
                currentRowArr[seatNo - 1] = Workflows.getCurrentBookingId();
                noOfSeatsLeft--;
            }
            if (noOfSeatsLeft <= 0) {
                break;
            }
        }
    }

    public static ArrayList<Integer> deriveAvailableSeatsOnCurrentRow(int seatsToBook, final String rowSeatPositionStr, String[] currentRow) {
        int middleMostCol = Workflows.getMiddleMostCol();
        // derive row and seat number
        int seatStartPos;
        if (!Validator.isBlank(rowSeatPositionStr)) {
            seatStartPos = Validator.getSeatPosition(rowSeatPositionStr);
        } else {
            seatStartPos = 1;
        }
        int availableSeatsInCurrentRow = 0;
        // check available seats left in current row
        for (int i = seatStartPos; i <= currentRow.length; i++) {
            if (Validator.isBlank(currentRow[seatStartPos - 1])) {
                availableSeatsInCurrentRow++;
            }
        }

        if (Validator.isBlank(rowSeatPositionStr)) {
            return generateReservedCurrentRow(availableSeatsInCurrentRow, seatsToBook, middleMostCol, currentRow);
        }
        // TODO: figure out how to determine which reserved array to return
        if (seatsToBook <= availableSeatsInCurrentRow && Validator.isBlank(rowSeatPositionStr)) {
            return generateReservedCurrentRow(availableSeatsInCurrentRow, seatsToBook, middleMostCol, currentRow);
        }
        // TODO:
        return generateCustomReservedCurrentRow(availableSeatsInCurrentRow, seatsToBook, seatStartPos, currentRow);
    }


    public static ArrayList<Integer> generateReservedCurrentRow(int availableSeatsThisRow, int seatsToBookThisRow, final int middleCol, String[] currentRow) {
        // generate default array of reserved seats that is in the middle
        ArrayList<Integer> reserved = new ArrayList<>();
        reserved.add(middleCol);
        availableSeatsThisRow--;
        seatsToBookThisRow--;
        boolean polarity = true; // set right-side first
        int forward = 1;
        int backward = 1;
        while (availableSeatsThisRow > 0 && seatsToBookThisRow > 0) { // exclude middle seat
            if (polarity) {
                // check if this position already taken, if taken dont add, but add forward to find one that is free.
                int rightSeat = middleCol + forward;
                if (Validator.isBlank(currentRow[rightSeat - 1])) {
                    reserved.add(rightSeat);
                    availableSeatsThisRow--;
                    seatsToBookThisRow--;
                }
                forward++;
            } else {
                // check if this position already taken, if taken dont add, but add backward to find one that is free.
                int leftSeat = middleCol - backward;
                if (Validator.isBlank(currentRow[leftSeat - 1])) {
                    reserved.add(0, leftSeat);
                    availableSeatsThisRow--;
                    seatsToBookThisRow--;
                }
                backward++;
            }
            polarity = !polarity;
        }
        return reserved;
    }

    public static ArrayList<Integer> generateCustomReservedCurrentRow(int availableSeatsInCurrentRow, int seatsToBookThisRow, int startPos, String[] currentRow) {
        // asc order only.
        ArrayList<Integer> reserved = new ArrayList<>();
        int forward = startPos;
        while (availableSeatsInCurrentRow > 0 && seatsToBookThisRow > 0) {
            if (forward - 1 > currentRow.length) {
                break;
            }
            if (Validator.isBlank(currentRow[forward - 1])) {
                reserved.add(forward);
                availableSeatsInCurrentRow--;
                seatsToBookThisRow--;
            }
            forward++;
        }
        return reserved;
    }

    public static void clearExistingReservationForBookingId(String currentBookingId) {
        for (String alphabet : ALPHABETS) {
            String[] row = (String[]) Workflows.settings.get(alphabet);
            if (row != null) {
                for (int i = 0; i < row.length; i++) {
                    if (!Validator.isBlank(row[i]) && row[i].equals(currentBookingId)) {
                        row[i] = null;
                    }
                }
            }
        }
    }
}
