package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "Myhome@4321";

    public static void main(String[] args) {

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);

        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);
            Appointment appointment = new Appointment(connection, scanner);

            while(true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointments");
                System.out.println("5. View Appointments");
                System.out.println("6. Delete Appointments");
                System.out.println("7. Exit");
                System.out.println("Enter your choice: ");

                int choice;
                try {
                    choice = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                    continue;
                }

                switch(choice){
                    case 1:
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3:
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        bookAppointment(patient, doctor, connection, scanner);
                        System.out.println();
                        break;
                    case 5:
                        appointment.viewAppointments();
                        System.out.println();
                        break;
                    case 6:
                        deleteAppointment(connection, scanner);
                        System.out.println();
                        break;
                    case 7:
                        System.out.println("THANK YOU! FOR USING HOSPITAL MANAGEMENT SYSTEM!!");
                        return;
                    default:
                        System.out.println("Enter valid choice!!!");
                        break;
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static String getPatientNameById(int patientId, Connection connection){
        String query = "Select patient_name from patients where id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setInt(1, patientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getString("patient_name");
            }
            else {
                return null;
            }
        } catch (SQLException e) {
           e.printStackTrace();
           return null;
        }
    }

    public static String getDoctorNameById(int doctorId, Connection connection){
        String query = "Select doctor_name from doctors where id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setInt(1, doctorId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getString("doctor_name");
            }
            else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner){

        System.out.print("Enter Patient Id: ");
        int patientId = scanner.nextInt();

        System.out.print("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();

        System.out.print("Enter appointment date: ");
        String appointmentDateInput = scanner.next();

        LocalDate formattedDate;
        String appointmentDate;

        try{
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            formattedDate = LocalDate.parse(appointmentDateInput, inputFormatter);
            appointmentDate = formattedDate.toString();
        }
        catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use DD-MM-YYYY.");
            return;
        }

        if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)){

            if(checkDoctorAvailability(doctorId, appointmentDate, connection)){

                String appointmentQuery = "INSERT INTO appointments(patient_id, patient_name, doctor_id, doctor_name, appointment_date) VALUES(?, ?, ?, ?, ?)";

                try {
                    String patientName = getPatientNameById(patientId, connection);
                    String doctorName = getDoctorNameById(doctorId, connection);

                    if (patientName == null || doctorName == null) {
                        System.out.println("Could not retrieve patient or doctor details.");
                        return;
                    }

                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setString(2, patientName);
                    preparedStatement.setInt(3, doctorId);
                    preparedStatement.setString(4, doctorName);
                    preparedStatement.setString(5, appointmentDate);

                    int rowsAffected = preparedStatement.executeUpdate();

                    if(rowsAffected>0){
                        System.out.println("Appointment Booked!");
                    }
                    else{
                        System.out.println("Failed to Book Appointment!");
                    }
                }
                catch (SQLException e) {
                    System.out.println("Error booking the appointment: " + e.getMessage());
                    e.printStackTrace();
                }

            }
            else{
                System.out.println("Doctor not available on this date!!");
            }
        }
        else{
            System.out.println("Either doctor or patient doesn't exist!!!");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection){

        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count==0){
                    return true;
                }
                else{
                    return false;
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    private static void deleteAppointment(Connection connection, Scanner scanner){
        try{
            System.out.println("Enter Appointment Id to delete: ");
            int appointmentId = scanner.nextInt();

            if (!appointmentExists(connection, appointmentId)){
                System.out.println("Appointment doesn't exist!!!");
                return;

            }
            String sql = "DELETE FROM appointments WHERE appointment_id = " + appointmentId;
            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Appointment deleted successfully !!");
                }
                else {
                    System.out.println("Appointment deletion failed !!");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean appointmentExists(Connection connection, int appointmentId) {
        try {
            String sql = "SELECT appointment_id FROM appointments WHERE appointment_id = " + appointmentId;

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)){
                return resultSet.next(); // If there's a result, the reservation exists
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle database errors as needed
        }
    }
}