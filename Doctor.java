package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Doctor {
    private Connection connection;

    public Doctor(Connection connection){
        this.connection = connection;
    }

    public void viewDoctors(){
        String query = "SELECT id, doctor_name, specialization, `experience_in_year` FROM doctors";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println("Doctors: ");
            System.out.println("+-----------+----------------+-----------------------+----------------------+");
            System.out.println("| Doctor Id | Doctor Name    | Specialization        | Experience in Year   |");
            System.out.println("+-----------+----------------+-----------------------+----------------------+");

            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String doctor_name = resultSet.getString("doctor_name");
                String specialization = resultSet.getString("specialization");
                int experience_in_year = resultSet.getInt("experience_in_year");
                System.out.printf("| %-10d| %-15s| %-22s| %-21d|\n", id, doctor_name, specialization, experience_in_year);
                System.out.println("+-----------+----------------+-----------------------+----------------------+");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean getDoctorById(int id){
        String query = "SELECT * FROM doctors WHERE id = ?";
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