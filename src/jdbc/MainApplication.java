package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class MainApplication {

    public static void main(String[] args) {

        String url =
                "jdbc:mysql://localhost:3306/bus_reservation_db";

        String username = "root";
        String password = "Mokshitha@123";

        try {

            // Load JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create connection only once
            Connection con =
                    DriverManager.getConnection(
                            url,
                            username,
                            password
                    );

            System.out.println(
                    "Database connection successful!"
            );

            // Create tables
            DatabaseOperations.createTables(con);

            // Add sample buses
            DatabaseOperations.addSampleBuses(con);

            Scanner sc = new Scanner(System.in);

            boolean running = true;

            while (running) {

                System.out.println();
                System.out.println("========================================");
                System.out.println("        BUS RESERVATION SYSTEM");
                System.out.println("========================================");
                System.out.println("1. Search Buses");
                System.out.println("2. Seat Booking");
                System.out.println("3. Ticket Cancellation");
                System.out.println("4. Passenger Details");
                System.out.println("5. Ticket Details");
                System.out.println("6. Exit");
                System.out.println("========================================");

                System.out.print("Enter your choice: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {

                    case 1:

                        DatabaseOperations.searchBuses(
                                con,
                                sc
                        );

                        break;

                    case 2:

                        int passengerId =
                                DatabaseOperations.registerPassenger(
                                        con,
                                        sc
                                );

                        if (passengerId != -1) {

                            DatabaseOperations.bookSeat(
                                    con,
                                    sc
                            );
                        }

                        break;

                    case 3:

                        DatabaseOperations.cancelTicket(
                                con,
                                sc
                        );

                        break;

                    case 4:

                        DatabaseOperations.viewPassengers(
                                con
                        );

                        break;

                    case 5:

                        DatabaseOperations.viewBooking(
                                con,
                                sc
                        );

                        break;

                    case 6:

                        running = false;

                        System.out.println(
                                "Thank you for using Bus Reservation System."
                        );

                        break;

                    default:

                        System.out.println(
                                "Invalid choice. Please try again."
                        );
                }
            }

            sc.close();
            con.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}