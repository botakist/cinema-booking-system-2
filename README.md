# Cinema Booking System
- created Java 8 maven project
- [CinemaBookingSystem](src/main/java/com/example/demo/CinemaBookingSystem.java) is the main class to run when starting this program

## thought process
- I decided to try using key-value maps for storing booking reservations.
  - for example: key = 'A', value = the seats array for row 'A' in String[] data type.
- all user inputs are validated. This includes:
  - entered '4' when there are only 3 options.
  - entered a non-existing row during seat selection. (entered 'D01', but there is only rows A,B,C)
  - entered a seat position input that exceeds the maximum seats per row. (entered 'A06', but there are only 5 seats per row)
- For each successful booking, I coded it such that the booking id is stored as the String contained in the seats array.
  - reason for this is that so I can utilise the seats array for the `Check bookings` workflow, where I can simply loop through all the available rows and display the booking based on the booking id.
## challenges
- I am stuck at figuring out the algorithm for determining default seat positions for the following scenarios:
  - when the seats booked for a row overflows to the next, how to determine where the seat position of the next row

## conclusion
- initialise movie title and seating map settings
- displayed reserved seating map based on initial map settings
- trouble with display custom reserved seating map based on given seatRowPosition input
- test cases not completed