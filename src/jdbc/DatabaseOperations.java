package jdbc;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
import java.sql.SQLIntegrityConstraintViolationException;


public class DatabaseOperations {

    public static void createTables(Connection con) {

        try {

            Statement stmt = con.createStatement();

            // Buses table
            String busesTable = """
                    CREATE TABLE IF NOT EXISTS buses (
                        bus_id INT PRIMARY KEY AUTO_INCREMENT,
                        bus_number VARCHAR(20) NOT NULL UNIQUE,
                        bus_name VARCHAR(100) NOT NULL,
                        source VARCHAR(100) NOT NULL,
                        destination VARCHAR(100) NOT NULL,
                        total_seats INT NOT NULL CHECK (total_seats > 0),
                        fare DECIMAL(10,2) NOT NULL CHECK (fare > 0)
                    )
                    """;

            stmt.executeUpdate(busesTable);

            // Passengers table
            String passengersTable = """
                    CREATE TABLE IF NOT EXISTS passengers (
                        passenger_id INT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(100) NOT NULL,
                        email VARCHAR(100) NOT NULL UNIQUE,
                        phone VARCHAR(10) NOT NULL
                    )
                    """;

            stmt.executeUpdate(passengersTable);

            // Bookings table
            String bookingsTable = """
                    CREATE TABLE IF NOT EXISTS bookings (
                        booking_id INT PRIMARY KEY AUTO_INCREMENT,
                        passenger_id INT NOT NULL,
                        bus_id INT NOT NULL,
                        seat_number INT NOT NULL,
                        booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        fare DECIMAL(10,2) NOT NULL,
                        status VARCHAR(20) DEFAULT 'CONFIRMED',

                        FOREIGN KEY (passenger_id)
                            REFERENCES passengers(passenger_id),

                        FOREIGN KEY (bus_id)
                            REFERENCES buses(bus_id),

                        UNIQUE (bus_id, seat_number)
                    )
                    """;

            stmt.executeUpdate(bookingsTable);

            System.out.println("Tables created successfully!");

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    public static void searchBuses(Connection con, Scanner sc) {

        try {

            System.out.print("Enter source: ");
            String source = sc.nextLine();

            System.out.print("Enter destination: ");
            String destination = sc.nextLine();

            if (!Validation.isNotEmpty(source) ||
                !Validation.isNotEmpty(destination)) {

                System.out.println("Source and destination cannot be empty.");
                return;
            }

            String sql =
                    "SELECT * FROM buses " +
                    "WHERE source = ? AND destination = ?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, source);
            ps.setString(2, destination);

            ResultSet rs = ps.executeQuery();

            boolean found = false;

            while (rs.next()) {

                found = true;

                displayBus(rs);
            }

            if (!found) {

                System.out.println(
                        "No buses found for this route."
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    public static void displayBus(ResultSet rs) {

        try {

            System.out.println("========================================");

            System.out.println(
                    "Bus ID        : " + rs.getInt("bus_id")
            );

            System.out.println(
                    "Bus Number    : " + rs.getString("bus_number")
            );

            System.out.println(
                    "Bus Name      : " + rs.getString("bus_name")
            );

            System.out.println(
                    "Source        : " + rs.getString("source")
            );

            System.out.println(
                    "Destination   : " + rs.getString("destination")
            );

            System.out.println(
                    "Total Seats   : " + rs.getInt("total_seats")
            );

            System.out.println(
                    "Fare          : ₹" + rs.getDouble("fare")
            );

            System.out.println("========================================");

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    public static void addSampleBuses(Connection con) {

        try {

            String sql =
                    "INSERT IGNORE INTO buses " +
                    "(bus_number, bus_name, source, destination, total_seats, fare) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            // Bus 1
            ps.setString(1, "AP01AB1234");
            ps.setString(2, "Orange Travels");
            ps.setString(3, "Bangalore");
            ps.setString(4, "Hyderabad");
            ps.setInt(5, 40);
            ps.setDouble(6, 750);
            ps.addBatch();

            // Bus 2
            ps.setString(1, "KA05CD5678");
            ps.setString(2, "VRL Travels");
            ps.setString(3, "Bangalore");
            ps.setString(4, "Chennai");
            ps.setInt(5, 40);
            ps.setDouble(6, 650);
            ps.addBatch();

            // Bus 3
            ps.setString(1, "TS09EF9012");
            ps.setString(2, "SRS Travels");
            ps.setString(3, "Hyderabad");
            ps.setString(4, "Bangalore");
            ps.setInt(5, 40);
            ps.setDouble(6, 700);
            ps.addBatch();

            ps.executeBatch();

            System.out.println("Sample buses added successfully!");

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    public static int registerPassenger(Connection con, Scanner sc) {

        try {

            String name;

            while (true) {

                System.out.print("Enter passenger name: ");
                name = sc.nextLine();

                if (!Validation.isNotEmpty(name)) {
                    System.out.println("Name cannot be empty.");
                }
                else if (!Validation.containsOnlyAlphabets(name)) {
                    System.out.println(
                            "Name should contain alphabets and spaces only."
                    );
                }
                else {
                    break;
                }
            }

            String email;

            while (true) {

                System.out.print("Enter email: ");
                email = sc.nextLine();

                if (!Validation.isNotEmpty(email)) {
                    System.out.println("Email cannot be empty.");
                }
                else if (!Validation.isValidEmail(email)) {
                    System.out.println("Enter a valid email.");
                }
                else {
                    break;
                }
            }

            String phone;

            while (true) {

                System.out.print("Enter phone number: ");
                phone = sc.nextLine();

                if (!Validation.isNotEmpty(phone)) {
                    System.out.println("Phone number cannot be empty.");
                }
                else if (!Validation.isValidPhone(phone)) {
                    System.out.println(
                            "Phone number should contain 10 digits and start with 6-9."
                    );
                }
                else {
                    break;
                }
            }

            String sql =
                    "INSERT INTO passengers (name, email, phone) " +
                    "VALUES (?, ?, ?)";

            PreparedStatement ps =
                    con.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    );

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {

                int passengerId = rs.getInt(1);

                System.out.println();
                System.out.println("Passenger registered successfully!");
                System.out.println(
                        "Passenger ID: " + passengerId
                );

                return passengerId;
            }

            return -1;

        } catch (Exception e) {

            System.out.println("Passenger registration failed.");
            e.printStackTrace();
            return -1;
        }
    }
    public static void viewPassengers(Connection con) {

        try {

            String sql = "SELECT * FROM passengers";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            boolean found = false;

            while (rs.next()) {

                found = true;

                displayPassenger(rs);
            }

            if (!found) {
                System.out.println("No passenger details found.");
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    public static void displayPassenger(ResultSet rs) {

        try {

            System.out.println("========================================");

            System.out.println(
                    "Passenger ID : " + rs.getInt("passenger_id")
            );

            System.out.println(
                    "Name         : " + rs.getString("name")
            );

            System.out.println(
                    "Email        : " + rs.getString("email")
            );

            System.out.println(
                    "Phone        : " + rs.getString("phone")
            );

            System.out.println("========================================");

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    public static void bookSeat(Connection con, Scanner sc) {

        try {

            System.out.print("Enter passenger ID: ");
            int passengerId = sc.nextInt();
            sc.nextLine();

            System.out.print("Enter bus ID: ");
            int busId = sc.nextInt();
            sc.nextLine();

            System.out.print("Enter seat number: ");
            int seatNumber = sc.nextInt();
            sc.nextLine();

            if (seatNumber <= 0) {
                System.out.println("Seat number should be greater than 0.");
                return;
            }

            // Start transaction
            con.setAutoCommit(false);

            // Get bus details
            String busSql =
                    "SELECT total_seats, fare " +
                    "FROM buses " +
                    "WHERE bus_id = ? " +
                    "FOR UPDATE";

            PreparedStatement busPs =
                    con.prepareStatement(busSql);

            busPs.setInt(1, busId);

            ResultSet busRs = busPs.executeQuery();

            if (!busRs.next()) {

                System.out.println("Bus not found.");
                con.rollback();
                return;
            }

            int totalSeats =
                    busRs.getInt("total_seats");

            double fare =
                    busRs.getDouble("fare");

            // Validate seat number
            if (seatNumber > totalSeats) {

                System.out.println(
                        "Invalid seat number. Total seats are "
                        + totalSeats
                );

                con.rollback();
                return;
            }

            // Seat availability validation
            String seatCheckSql =
                    "SELECT booking_id " +
                    "FROM bookings " +
                    "WHERE bus_id = ? " +
                    "AND seat_number = ? " +
                    "AND status = 'CONFIRMED'";

            PreparedStatement seatCheckPs =
                    con.prepareStatement(seatCheckSql);

            seatCheckPs.setInt(1, busId);
            seatCheckPs.setInt(2, seatNumber);

            ResultSet seatRs =
                    seatCheckPs.executeQuery();

            if (seatRs.next()) {

                System.out.println(
                        "Seat " + seatNumber +
                        " is already booked."
                );

                con.rollback();
                return;
            }

            // Insert booking
            String bookingSql =
                    "INSERT INTO bookings " +
                    "(passenger_id, bus_id, seat_number, fare) " +
                    "VALUES (?, ?, ?, ?)";

            PreparedStatement bookingPs =
                    con.prepareStatement(
                            bookingSql,
                            Statement.RETURN_GENERATED_KEYS
                    );

            bookingPs.setInt(1, passengerId);
            bookingPs.setInt(2, busId);
            bookingPs.setInt(3, seatNumber);
            bookingPs.setDouble(4, fare);

            bookingPs.executeUpdate();

            ResultSet generatedKeys =
                    bookingPs.getGeneratedKeys();

            if (generatedKeys.next()) {

                int bookingId =
                        generatedKeys.getInt(1);

                con.commit();

                System.out.println();
                System.out.println("========================================");
                System.out.println("       TICKET BOOKED SUCCESSFULLY");
                System.out.println("========================================");
                System.out.println(
                        "Booking ID   : " + bookingId
                );
                System.out.println(
                        "Passenger ID : " + passengerId
                );
                System.out.println(
                        "Bus ID       : " + busId
                );
                System.out.println(
                        "Seat Number  : " + seatNumber
                );
                System.out.println(
                        "Fare         : ₹" + fare
                );
                System.out.println("Status       : CONFIRMED");
                System.out.println("========================================");
            }

        } catch (SQLIntegrityConstraintViolationException e) {

            try {
                con.rollback();
            } catch (Exception rollbackException) {
                rollbackException.printStackTrace();
            }

            System.out.println(
                    "Seat is already booked."
            );

        } catch (Exception e) {

            try {
                con.rollback();
            } catch (Exception rollbackException) {
                rollbackException.printStackTrace();
            }

            System.out.println(
                    "Booking failed. Transaction rolled back."
            );

            e.printStackTrace();

        } finally {

            try {
                con.setAutoCommit(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void cancelTicket(Connection con, Scanner sc) {

        try {

            System.out.print("Enter booking ID: ");
            int bookingId = sc.nextInt();
            sc.nextLine();

            // Start transaction
            con.setAutoCommit(false);

            String checkSql =
                    "SELECT booking_id " +
                    "FROM bookings " +
                    "WHERE booking_id = ? " +
                    "AND status = 'CONFIRMED' " +
                    "FOR UPDATE";

            PreparedStatement checkPs =
                    con.prepareStatement(checkSql);

            checkPs.setInt(1, bookingId);

            ResultSet rs = checkPs.executeQuery();

            if (!rs.next()) {

                System.out.println(
                        "Booking not found or ticket is already cancelled."
                );

                con.rollback();
                return;
            }

            String cancelSql =
                    "UPDATE bookings " +
                    "SET status = 'CANCELLED' " +
                    "WHERE booking_id = ?";

            PreparedStatement cancelPs =
                    con.prepareStatement(cancelSql);

            cancelPs.setInt(1, bookingId);

            int rows = cancelPs.executeUpdate();

            if (rows > 0) {

                con.commit();

                System.out.println(
                        "Ticket cancelled successfully!"
                );
            }
            else {

                con.rollback();

                System.out.println(
                        "Ticket cancellation failed."
                );
            }

        } catch (Exception e) {

            try {
                con.rollback();
            } catch (Exception rollbackException) {
                rollbackException.printStackTrace();
            }

            System.out.println(
                    "Cancellation failed. Transaction rolled back."
            );

            e.printStackTrace();

        } finally {

            try {
                con.setAutoCommit(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void viewBooking(Connection con, Scanner sc) {

        try {

            System.out.print("Enter booking ID: ");
            int bookingId = sc.nextInt();
            sc.nextLine();

            String sql =
                    "SELECT b.booking_id, " +
                    "p.name, " +
                    "p.phone, " +
                    "bus.bus_number, " +
                    "bus.bus_name, " +
                    "bus.source, " +
                    "bus.destination, " +
                    "b.seat_number, " +
                    "b.booking_date, " +
                    "b.fare, " +
                    "b.status " +
                    "FROM bookings b " +
                    "JOIN passengers p " +
                    "ON b.passenger_id = p.passenger_id " +
                    "JOIN buses bus " +
                    "ON b.bus_id = bus.bus_id " +
                    "WHERE b.booking_id = ?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setInt(1, bookingId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                System.out.println("========================================");
                System.out.println("             TICKET DETAILS");
                System.out.println("========================================");

                System.out.println(
                        "Booking ID    : " + rs.getInt("booking_id")
                );

                System.out.println(
                        "Passenger     : " + rs.getString("name")
                );

                System.out.println(
                        "Phone         : " + rs.getString("phone")
                );

                System.out.println(
                        "Bus Number    : " + rs.getString("bus_number")
                );

                System.out.println(
                        "Bus Name      : " + rs.getString("bus_name")
                );

                System.out.println(
                        "Route         : " +
                        rs.getString("source") +
                        " -> " +
                        rs.getString("destination")
                );

                System.out.println(
                        "Seat Number   : " + rs.getInt("seat_number")
                );

                System.out.println(
                        "Booking Date  : " + rs.getTimestamp("booking_date")
                );

                System.out.println(
                        "Fare          : ₹" + rs.getDouble("fare")
                );

                System.out.println(
                        "Status        : " + rs.getString("status")
                );

                System.out.println("========================================");

            } else {

                System.out.println("Booking not found.");
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}