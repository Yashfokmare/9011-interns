
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class flightbooking {
    public static void main(String[] args) {
        FlightBookingSystem system = new FlightBookingSystem();
        system.run();
    }
}

class Flight {
    private String flightCode;
    private String airline;
    private String source;
    private String destination;
    private int availableSeats;
    private double baseFare;

    public Flight(String flightCode, String airline, String source,
                  String destination, int availableSeats, double baseFare) {
        this.flightCode = flightCode;
        this.airline = airline;
        this.source = source;
        this.destination = destination;
        this.availableSeats = availableSeats;
        this.baseFare = baseFare;
    }

    public String getFlightCode() { return flightCode; }
    public String getAirline() { return airline; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public int getAvailableSeats() { return availableSeats; }
    public double getBaseFare() { return baseFare; }

    public boolean hasSeats(int tickets) {
        return tickets > 0 && tickets <= availableSeats;
    }

    public void reserveSeats(int tickets) {
        if (!hasSeats(tickets)) {
            throw new IllegalArgumentException("Not enough seats available.");
        }
        availableSeats -= tickets;
    }

    public void displayDetails() {
        System.out.printf(
                "%-8s %-15s %-15s %-15s %-8d Rs. %.2f%n",
                flightCode, airline, source, destination,
                availableSeats, baseFare
        );
    }
}

abstract class Passenger {
    private String name;
    private int age;

    public Passenger(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() { return name; }
    public int getAge() { return age; }

    public abstract double getDiscountPercentage();

    public abstract String getPassengerType();
}

class RegularPassenger extends Passenger {
    public RegularPassenger(String name, int age) {
        super(name, age);
    }

    @Override
    public double getDiscountPercentage() {
        return 0;
    }

    @Override
    public String getPassengerType() {
        return "Regular";
    }
}

class StudentPassenger extends Passenger {
    public StudentPassenger(String name, int age) {
        super(name, age);
    }

    @Override
    public double getDiscountPercentage() {
        return 10;
    }

    @Override
    public String getPassengerType() {
        return "Student";
    }
}

class Booking {
    private String bookingId;
    private Flight flight;
    private Passenger passenger;
    private int ticketCount;
    private double subtotal;
    private double discount;
    private double finalAmount;

    public Booking(String bookingId, Flight flight,
                   Passenger passenger, int ticketCount) {
        this.bookingId = bookingId;
        this.flight = flight;
        this.passenger = passenger;
        this.ticketCount = ticketCount;

        subtotal = flight.getBaseFare() * ticketCount;
        discount = subtotal * passenger.getDiscountPercentage() / 100;
        finalAmount = subtotal - discount;
    }

    public void displaySummary() {
        System.out.println("\n========== BOOKING SUMMARY ==========");
        System.out.println("Booking ID: " + bookingId);
        System.out.println("Flight: " + flight.getFlightCode());
        System.out.println("Airline: " + flight.getAirline());
        System.out.println("Route: " + flight.getSource()
                + " to " + flight.getDestination());
        System.out.println("Passenger: " + passenger.getName());
        System.out.println("Age: " + passenger.getAge());
        System.out.println("Passenger type: "
                + passenger.getPassengerType());
        System.out.println("Tickets: " + ticketCount);

        System.out.printf("Base fare per ticket: Rs. %.2f%n",
                flight.getBaseFare());
        System.out.printf("Subtotal: Rs. %.2f%n", subtotal);
        System.out.printf("%s discount (%.0f%%): Rs. %.2f%n",
                passenger.getPassengerType(),
                passenger.getDiscountPercentage(), discount);
        System.out.printf("Final amount: Rs. %.2f%n", finalAmount);
        System.out.println("=====================================");
    }
}

class FlightBookingSystem {
    private List<Flight> flights = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);
    private int nextBookingNumber = 1001;

    public FlightBookingSystem() {
        flights.add(new Flight("PN101", "Kiran Air",
                "Pune", "Delhi", 20, 4000));
        flights.add(new Flight("MB202", "Sky India",
                "Mumbai", "Bengaluru", 15, 3500));
        flights.add(new Flight("PN303", "Deccan Air",
                "Pune", "Hyderabad", 12, 3000));
    }

    public void run() {
        System.out.println("Welcome to Kiran Air Booking");

        while (true) {
            System.out.println("\n1. View Flights");
            System.out.println("2. Book Flight");
            System.out.println("3. View Booking");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    displayFlights();
                    break;
                case "2":
                    bookFlight();
                    break;
                case "3":
                    displayLatestBooking();
                    break;
                case "4":
                    System.out.println(
                            "Thank you for using Kiran Air Booking!"
                    );
                    scanner.close();
                    return;
                default:
                    System.out.println(
                            "Invalid choice. Please enter 1, 2, 3, or 4."
                    );
            }
        }
    }

    public void displayFlights() {
        System.out.println("\nAvailable Flights");
        System.out.printf("%-8s %-15s %-15s %-15s %-8s %s%n",
                "Code", "Airline", "Source", "Destination",
                "Seats", "Base Fare");

        for (Flight flight : flights) {
            flight.displayDetails();
        }
    }

    public Flight findFlightByCode(String code) {
        for (Flight flight : flights) {
            if (flight.getFlightCode().equalsIgnoreCase(code.trim())) {
                return flight;
            }
        }
        return null;
    }

    private boolean validateBookingDetails(
            Flight flight, String name, int age,
            int tickets, String passengerType) {

        if (flight == null) {
            System.out.println("Booking failed: Flight code does not exist.");
            return false;
        }

        if (name == null || name.trim().isEmpty()) {
            System.out.println("Booking failed: Passenger name cannot be blank.");
            return false;
        }

        if (age <= 0) {
            System.out.println("Booking failed: Age must be greater than 0.");
            return false;
        }

        if (tickets <= 0) {
            System.out.println("Booking failed: Ticket count must be greater than 0.");
            return false;
        }

        if (!"regular".equalsIgnoreCase(passengerType)
                && !"student".equalsIgnoreCase(passengerType)) {
            System.out.println(
                    "Booking failed: Enter Regular or Student."
            );
            return false;
        }

        if (!flight.hasSeats(tickets)) {
            System.out.println("Booking failed: Not enough seats available.");
            return false;
        }

        return true;
    }

    private void bookFlight() {
        System.out.print("Enter flight code: ");
        String code = scanner.nextLine().trim();

        System.out.print("Enter passenger name: ");
        String name = scanner.nextLine();

        int age = readInteger("Enter age: ");

        System.out.print("Passenger type (Regular/Student): ");
        String type = scanner.nextLine().trim();

        int tickets = readInteger("Enter number of tickets: ");

        Flight flight = findFlightByCode(code);

        if (!validateBookingDetails(
                flight, name, age, tickets, type)) {
            System.out.println(
                    "No booking was created. Available seats remain unchanged."
            );
            return;
        }

        Passenger passenger;

        if (type.equalsIgnoreCase("student")) {
            passenger = new StudentPassenger(name.trim(), age);
        } else {
            passenger = new RegularPassenger(name.trim(), age);
        }

        Booking booking = new Booking(
                "BK" + nextBookingNumber, flight, passenger, tickets
        );

        // Update seats and save only after validation succeeds.
        flight.reserveSeats(tickets);
        bookings.add(booking);
        nextBookingNumber++;

        System.out.println("\nBooking confirmed!");
        booking.displaySummary();
    }

    private int readInteger(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(
                        "Invalid input. Please enter a whole number."
                );
            }
        }
    }

    private void displayLatestBooking() {
        if (bookings.isEmpty()) {
            System.out.println("No confirmed booking exists yet.");
            return;
        }

        bookings.get(bookings.size() - 1).displaySummary();
    }
}

