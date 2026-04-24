import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hotel_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "jojo@321";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public static void initialize() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found!");
            System.exit(1);
        }

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // Create rooms table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS rooms (" +
                "  room_id INT AUTO_INCREMENT PRIMARY KEY," +
                "  room_number VARCHAR(10) UNIQUE NOT NULL," +
                "  room_type VARCHAR(20) NOT NULL," +
                "  ac_type VARCHAR(10) NOT NULL," +
                "  price_per_day DOUBLE NOT NULL," +
                "  max_occupancy INT NOT NULL," +
                "  status VARCHAR(20) NOT NULL DEFAULT 'Available'" +
                ")"
            );

            // Create bookings table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS bookings (" +
                "  booking_id INT AUTO_INCREMENT PRIMARY KEY," +
                "  room_id INT NOT NULL," +
                "  customer_name VARCHAR(100) NOT NULL," +
                "  id_proof VARCHAR(50) NOT NULL," +
                "  address VARCHAR(255) NOT NULL," +
                "  num_persons INT NOT NULL," +
                "  num_days INT NOT NULL," +
                "  check_in_date VARCHAR(20)," +
                "  check_out_date VARCHAR(20)," +
                "  booking_date VARCHAR(20) NOT NULL," +
                "  status VARCHAR(20) NOT NULL DEFAULT 'Active'," +
                "  FOREIGN KEY (room_id) REFERENCES rooms(room_id)" +
                ")"
            );

            // Create billing table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS billing (" +
                "  bill_id INT AUTO_INCREMENT PRIMARY KEY," +
                "  booking_id INT NOT NULL," +
                "  room_charges DOUBLE NOT NULL," +
                "  extra_charges DOUBLE NOT NULL DEFAULT 0," +
                "  total_amount DOUBLE NOT NULL," +
                "  payment_date VARCHAR(20) NOT NULL," +
                "  FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)" +
                ")"
            );

            // Create services table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS services (" +
                "  service_id INT AUTO_INCREMENT PRIMARY KEY," +
                "  booking_id INT NOT NULL," +
                "  service_name VARCHAR(50) NOT NULL," +
                "  charge DOUBLE NOT NULL," +
                "  service_date VARCHAR(20) NOT NULL," +
                "  FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)" +
                ")"
            );

            // Seed rooms if empty
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM rooms");
            if (rs.next() && rs.getInt(1) == 0) {
                seedRooms(conn);
            }
            rs.close();

            System.out.println("Database initialized successfully with MySQL.");
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void seedRooms(Connection conn) throws SQLException {
        String[][] rooms = {
            {"101", "Single", "AC", "1500", "1"},
            {"102", "Single", "AC", "1500", "1"},
            {"103", "Single", "Non-AC", "800", "1"},
            {"104", "Single", "Non-AC", "800", "1"},
            {"201", "Double", "AC", "2500", "2"},
            {"202", "Double", "AC", "2500", "2"},
            {"203", "Double", "Non-AC", "1500", "2"},
            {"204", "Double", "Non-AC", "1500", "2"},
            {"301", "Triple", "AC", "3500", "3"},
            {"302", "Triple", "AC", "3500", "3"},
            {"303", "Triple", "Non-AC", "2200", "3"},
            {"401", "Suite", "AC", "5000", "4"},
            {"402", "Suite", "AC", "5000", "4"},
            {"501", "Deluxe", "AC", "7000", "2"},
            {"502", "Deluxe", "AC", "7000", "2"},
        };

        String sql = "INSERT INTO rooms (room_number, room_type, ac_type, price_per_day, max_occupancy, status) VALUES (?, ?, ?, ?, ?, 'Available')";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String[] room : rooms) {
                pstmt.setString(1, room[0]);
                pstmt.setString(2, room[1]);
                pstmt.setString(3, room[2]);
                pstmt.setDouble(4, Double.parseDouble(room[3]));
                pstmt.setInt(5, Integer.parseInt(room[4]));
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
        System.out.println("Seeded 15 rooms into MySQL.");
    }

    // ===================== ROOM OPERATIONS =====================

    public static ResultSet getAllRooms() throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("SELECT * FROM rooms ORDER BY room_number");
    }

    public static ResultSet getAvailableRooms() throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("SELECT * FROM rooms WHERE status='Available' ORDER BY room_number");
    }

    public static ResultSet getBookedRooms() throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("SELECT * FROM rooms WHERE status='Booked' ORDER BY room_number");
    }

    public static ResultSet getOccupiedRooms() throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("SELECT * FROM rooms WHERE status='Occupied' ORDER BY room_number");
    }

    public static void addRoom(String roomNumber, String roomType, String acType, double price, int maxOccupancy) throws SQLException {
        String sql = "INSERT INTO rooms (room_number, room_type, ac_type, price_per_day, max_occupancy, status) VALUES (?, ?, ?, ?, ?, 'Available')";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomNumber);
            pstmt.setString(2, roomType);
            pstmt.setString(3, acType);
            pstmt.setDouble(4, price);
            pstmt.setInt(5, maxOccupancy);
            pstmt.executeUpdate();
        }
    }

    public static void updateRoom(int roomId, String roomNumber, String roomType, String acType, double price, int maxOccupancy) throws SQLException {
        String sql = "UPDATE rooms SET room_number=?, room_type=?, ac_type=?, price_per_day=?, max_occupancy=? WHERE room_id=?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomNumber);
            pstmt.setString(2, roomType);
            pstmt.setString(3, acType);
            pstmt.setDouble(4, price);
            pstmt.setInt(5, maxOccupancy);
            pstmt.setInt(6, roomId);
            pstmt.executeUpdate();
        }
    }

    public static void deleteRoom(int roomId) throws SQLException {
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement("DELETE FROM rooms WHERE room_id=? AND status='Available'")) {
            pstmt.setInt(1, roomId);
            pstmt.executeUpdate();
        }
    }

    public static void updateRoomStatus(int roomId, String status) throws SQLException {
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement("UPDATE rooms SET status=? WHERE room_id=?")) {
            pstmt.setString(1, status);
            pstmt.setInt(2, roomId);
            pstmt.executeUpdate();
        }
    }

    public static int getRoomCount(String status) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM rooms WHERE status=?")) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getTotalRoomCount() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM rooms")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ===================== BOOKING OPERATIONS =====================

    public static int createBooking(int roomId, String customerName, String idProof, String address,
                                     int numPersons, int numDays, String bookingDate) throws SQLException {
        String sql = "INSERT INTO bookings (room_id, customer_name, id_proof, address, num_persons, num_days, booking_date, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, 'Active')";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, roomId);
            pstmt.setString(2, customerName);
            pstmt.setString(3, idProof);
            pstmt.setString(4, address);
            pstmt.setInt(5, numPersons);
            pstmt.setInt(6, numDays);
            pstmt.setString(7, bookingDate);
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            int bookingId = keys.next() ? keys.getInt(1) : -1;

            // Update room status to Booked
            updateRoomStatus(roomId, "Booked");
            return bookingId;
        }
    }

    public static ResultSet getActiveBookings() throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(
            "SELECT b.booking_id, r.room_number, r.room_type, r.ac_type, b.customer_name, " +
            "b.id_proof, b.num_persons, b.num_days, b.booking_date, b.status " +
            "FROM bookings b JOIN rooms r ON b.room_id = r.room_id " +
            "WHERE b.status='Active' ORDER BY b.booking_date DESC"
        );
    }

    public static ResultSet getCheckedInBookings() throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(
            "SELECT b.booking_id, r.room_number, r.room_type, r.ac_type, r.price_per_day, " +
            "b.customer_name, b.num_persons, b.num_days, b.check_in_date, b.booking_date " +
            "FROM bookings b JOIN rooms r ON b.room_id = r.room_id " +
            "WHERE b.status='CheckedIn' ORDER BY b.check_in_date DESC"
        );
    }

    public static void checkIn(int bookingId, String checkInDate) throws SQLException {
        try (Connection conn = getConnection()) {
            // Update booking status
            PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE bookings SET status='CheckedIn', check_in_date=? WHERE booking_id=?"
            );
            pstmt.setString(1, checkInDate);
            pstmt.setInt(2, bookingId);
            pstmt.executeUpdate();
            pstmt.close();

            // Get room_id for this booking
            PreparedStatement pstmt2 = conn.prepareStatement(
                "SELECT room_id FROM bookings WHERE booking_id=?"
            );
            pstmt2.setInt(1, bookingId);
            ResultSet rs = pstmt2.executeQuery();
            if (rs.next()) {
                int roomId = rs.getInt("room_id");
                updateRoomStatus(roomId, "Occupied");
            }
            rs.close();
            pstmt2.close();
        }
    }

    public static void checkOut(int bookingId, String checkOutDate, double roomCharges, double extraCharges, double totalAmount) throws SQLException {
        try (Connection conn = getConnection()) {
            // Update booking status
            PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE bookings SET status='CheckedOut', check_out_date=? WHERE booking_id=?"
            );
            pstmt.setString(1, checkOutDate);
            pstmt.setInt(2, bookingId);
            pstmt.executeUpdate();
            pstmt.close();

            // Create billing record
            PreparedStatement pstmt2 = conn.prepareStatement(
                "INSERT INTO billing (booking_id, room_charges, extra_charges, total_amount, payment_date) VALUES (?, ?, ?, ?, ?)"
            );
            pstmt2.setInt(1, bookingId);
            pstmt2.setDouble(2, roomCharges);
            pstmt2.setDouble(3, extraCharges);
            pstmt2.setDouble(4, totalAmount);
            pstmt2.setString(5, checkOutDate);
            pstmt2.executeUpdate();
            pstmt2.close();

            // Get room_id and update status to Available
            PreparedStatement pstmt3 = conn.prepareStatement(
                "SELECT room_id FROM bookings WHERE booking_id=?"
            );
            pstmt3.setInt(1, bookingId);
            ResultSet rs = pstmt3.executeQuery();
            if (rs.next()) {
                updateRoomStatus(rs.getInt("room_id"), "Available");
            }
            rs.close();
            pstmt3.close();
        }
    }

    // ===================== SERVICE OPERATIONS =====================

    public static void addService(int bookingId, String serviceName, double charge, String serviceDate) throws SQLException {
        String sql = "INSERT INTO services (booking_id, service_name, charge, service_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            pstmt.setString(2, serviceName);
            pstmt.setDouble(3, charge);
            pstmt.setString(4, serviceDate);
            pstmt.executeUpdate();
        }
    }

    public static ResultSet getServicesByBooking(int bookingId) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(
            "SELECT * FROM services WHERE booking_id=? ORDER BY service_date"
        );
        pstmt.setInt(1, bookingId);
        return pstmt.executeQuery();
    }

    public static double getTotalExtraCharges(int bookingId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT COALESCE(SUM(charge), 0) FROM services WHERE booking_id=?")) {
            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }

    // ===================== REPORT OPERATIONS =====================

    public static ResultSet getAllBookings() throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(
            "SELECT b.booking_id, r.room_number, r.room_type, b.customer_name, " +
            "b.num_persons, b.num_days, b.booking_date, b.check_in_date, b.check_out_date, b.status " +
            "FROM bookings b JOIN rooms r ON b.room_id = r.room_id " +
            "ORDER BY b.booking_id DESC"
        );
    }

    public static ResultSet getAllBillingRecords() throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(
            "SELECT bi.bill_id, r.room_number, b.customer_name, bi.room_charges, " +
            "bi.extra_charges, bi.total_amount, bi.payment_date " +
            "FROM billing bi JOIN bookings b ON bi.booking_id = b.booking_id " +
            "JOIN rooms r ON b.room_id = r.room_id " +
            "ORDER BY bi.bill_id DESC"
        );
    }

    public static double getTotalRevenue() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COALESCE(SUM(total_amount), 0) FROM billing")) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
