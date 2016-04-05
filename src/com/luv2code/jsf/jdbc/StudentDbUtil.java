package com.luv2code.jsf.jdbc;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

class StudentDbUtil {
    private static StudentDbUtil instance;
    private DataSource dataSource;
    private String jndiName = "java:comp/env/student_tracker";
    private Connection connection;

    public static StudentDbUtil getInstance() throws Exception{
        if (instance == null) {
            instance = new StudentDbUtil();
        }
        return instance;
    }
    
    private StudentDbUtil() throws Exception{
        dataSource = getDataSource();
    }

    private DataSource getDataSource() throws NamingException{
        Context context = new InitialContext();
        return (DataSource) context.lookup(jndiName);
    }

    List<Student> getStudents() throws Exception {
        List<Student> students = new ArrayList<>();
        Connection myConn = null;
        Statement myStmt = null;
        ResultSet myRs = null;

        try {
            myConn = getConnection();
            String sql = "SELECT * FROM student ORDER BY last_name";
            myStmt = myConn.createStatement();
            myRs = myStmt.executeQuery(sql);

            // Process result set
            while(myRs.next()){

                // Retrieve data from result set row
                int id = myRs.getInt("id");
                String firstName = myRs.getString("first_name");
                String lastName = myRs.getString("last_name");
                String email = myRs.getString("email");

                // Create new Student object
                Student tempStudent = new Student(id, firstName, lastName, email);

                // Add it to the list of students
                students.add(tempStudent);
            }
        }catch (SQLException sqlE){
            System.out.println(sqlE.getMessage());
        }finally {
            close(myConn, myStmt, myRs);
        }
        return students;
    }

    private void close(Connection myConn, Statement myStmt, ResultSet myRs) {
        try {
            myConn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            myStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            myRs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() {
        return connection;
    }
}