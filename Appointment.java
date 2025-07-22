package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Appointment {
    private Connection connection;
    private Scanner scnnner;

    public Appointment(Connection connection, Scanner scanner){
        this.connection = connection;
        this.scanner = scanner;
    }

    public void viewAppointments() {
        // Join appointments with patients and doctors to fetch the required data
        String query = "SELECT a.appointment_id AS appointment_id, " +
                "a.patient_id AS patient_id, p.patient_name AS patient_name, " +
                "a.doctor_id AS doctor_id, d.doctor_name AS doctor_name, " +
                "a.appointment_date AS appointment_date " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.id " +  // Join with patients table
                "JOIN doctors d ON a.doctor_id = d.id";     // Join with doctors table


        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println("Appointments: ");
            System.out.println("+----------------+----------- +-------------------+-----------+-----------------+-----------------------+");
            System.out.println("| Appointment ID | Patient ID | Patient Name      | Doctor ID | Doctor Name     | Appointment Date      |");
            System.out.println("+----------------+------------+-------------------+-----------+-----------------+-----------------------+");

            while(resultSet.next()){
                int appointment_id = resultSet.getInt("appointment_id");
                int patient_id = resultSet.getInt("patient_id");
                String patient_name = resultSet.getString("patient_name");
                int doctor_id = resultSet.getInt("doctor_id");
                String doctor_name = resultSet.getString("doctor_name");
                String appointment_date = resultSet.getTimestamp("appointment_date").toString();
                System.out.printf("| %-10d     | %-10d | %-15s   | %-10d| %-15s | %-19s |\n", appointment_id, patient_id, patient_name, doctor_id, doctor_name, appointment_date);
                System.out.println("+----------------+------------+-------------------+-----------+-----------------+-----------------------+");
            }
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
