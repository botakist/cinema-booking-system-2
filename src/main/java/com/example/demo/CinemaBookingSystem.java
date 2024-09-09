package com.example.demo;

import com.example.demo.validators.Validator;
import com.example.demo.workflows.Workflows;

public class CinemaBookingSystem {
    public static void main(String[] args) {
        startApp();
    }

    public static void startApp() {
        Workflows.initialiseSettings();
        while (true) {
            String optionSelected = Workflows.displayMainMenu();
            int option = Validator.isValidIntegerInput(optionSelected, 3);
            if (option < 0) {
                // repeat main menu printing due to invalid option entered.
                continue;
            }
            if (optionSelected.equals("3")) {
                System.out.println("Thank you for using GIC Cinemas system. Bye!");
                break;
            }
            if (optionSelected.equals("1")) {
                Workflows.ticketBookingFlow();
            }
            if (optionSelected.equals("2")) {
                Workflows.checkBookingsFlow();
            }
        }
    }
}
