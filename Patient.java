package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;
import java.sql.SQLException;

public class Patient {
    private Connection connection;
    private Scanner scanner;

    public Patient(Connection connection, Scanner scanner){
        this.connection = connection;
        this.scanner = scanner;
    }

    public void addPatient(){

        int id = getValidatedIntegerInput("Patient ID: ");
        //System.out.print("Enter Patient Name: ");
        String patient_name = getValidatedStringInput("Patient Name: ");
        //System.out.print("Enter Patient Age: ");
        int age = getValidatedIntegerInput("Patient Age: ");
        //System.out.println("Enter Patient Phone Number: ");
        String phone_number = getValidatedStringInput("Patient Phone Number: ");
        //System.out.print("Enter Patient Gender: ");
        String gender = getValidatedStringInput("Patient Gender: ");
        //System.out.println("Enter Patient Address: ");
        String address = getValidatedStringInput("Patient Address: ");

        String query = "INSERT INTO patients(id, patient_name, age, phone_number, gender, address) VALUES(?, ?, ?, ?, ?, ?)";

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, patient_name);
            preparedStatement.setInt(3, age);
            preparedStatement.setString(4, phone_number);
            preparedStatement.setString(5, gender);
            preparedStatement.setString(6, address);

            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows>0){
                System.out.println("Patient Added Successfully!!");
            }
            else{
                System.out.println("Failed to add Patient!!");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    private int getValidatedIntegerInput (String prompt) {
        while (true) {
            System.out.println(prompt);
            try {
                String input = scanner.nextLine().trim();
                if (!input.isEmpty()) {
                    return Integer.parseInt(input);
                }
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private String getValidatedStringInput (String prompt) {
        while (true) {
            System.out.println(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            else {
                System.out.println("Invalid input. Please enter a valid value.");
            }
        }
    }

    public void viewPatients(){
        String query = "select * from patients";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println("Patients: ");
            System.out.println("+------------+-----------------+-----+--------------+--------+--------------------+");
            System.out.println("| Patient Id | Patient Name    | Age | Phone Number | Gender |      Address       |");
            System.out.println("+------------+-----------------+-----+--------------+--------+--------------------+");

            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String patient_name = resultSet.getString("patient_name");
                int age = resultSet.getInt("age");
                String phone_number = resultSet.getString("phone_number");
                String gender = resultSet.getString("gender");
                String address = resultSet.getString("address");
                System.out.printf("| %-10d | %-15s | %-3d | %-12s |   %-2s   | %-15s    |\n", id, patient_name, age, phone_number, gender, address);
                System.out.println("+------------+-----------------+-----+--------------+--------+--------------------+");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean getPatientById(int id){
        String query = "SELECT * FROM patients WHERE id = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }else{
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}


