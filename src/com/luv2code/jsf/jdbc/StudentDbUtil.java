package com.luv2code.jsf.jdbc;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class StudentDbUtil {
    private static StudentDbUtil instance;
    private DataSource dataSource;

    static StudentDbUtil getInstance() throws Exception{
        if (instance == null) {
            instance = new StudentDbUtil();
        }
        return instance;
    }
    
    private StudentDbUtil() throws Exception{
        dataSource = getDataSource();
    }

    private DataSource getDataSource() throws NamingException{
        Context initContext = new InitialContext();
        Context envContext  = (Context)initContext.lookup("java:/comp/env");
        return  (DataSource)envContext.lookup("jdbc/student_tracker");

//        Context context = new InitialContext();
//        String jndiName = "java:comp/env/student_tracker";
//        return (DataSource) context.lookup(jndiName);
    }

    List<Student> getStudents() throws Exception {
        List<Student> students = new ArrayList<>();
        Connection myConn = null;
        Statement myStmt = null;
        ResultSet myRs = null;

        try {
            myConn = dataSource.getConnection();
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

    void addStudent(Student theStudent) throws Exception{
        Connection myConn = null;
        PreparedStatement myPrepStmt = null;

        try{
            myConn = dataSource.getConnection();
            String sql = "INSERT INTO student (first_name, last_name, email) values " +
                    "(?, ?, ?)";
            myPrepStmt = myConn.prepareStatement(sql);

            // Set params
            myPrepStmt.setString(1, theStudent.getFirstName());
            myPrepStmt.setString(2, theStudent.getLastName());
            myPrepStmt.setString(3, theStudent.getEmail());

            myPrepStmt.execute();
        } finally {
            if (myConn != null) {
                myConn.close();
            }
            if (myPrepStmt != null) {
                myPrepStmt.close();
            }
        }
    }

    Student getStudent(int studentId) throws Exception{
        Connection myConn = null;
        PreparedStatement prepStmt = null;
        ResultSet myRs = null;

        try{
            myConn = dataSource.getConnection();
            String sql = "SELECT * FROM student WHERE id=?";
            prepStmt = myConn.prepareStatement(sql);

            // Set params
            prepStmt.setInt(1, studentId);
            myRs = prepStmt.executeQuery();

            Student theStudent;

            // Retrieve data frm result set row
            if (myRs.next()) {
                int id = myRs.getInt("id");
                String firstName = myRs.getString("first_name");
                String lastName = myRs.getString("last_name");
                String email = myRs.getString("email");

                theStudent = new Student(id, firstName, lastName, email);
            } else {
                throw  new Exception("Could not find student id: " + studentId);
            }
            return theStudent;
        } finally {
            if (myConn != null) {
                myConn.close();
            }
            if (prepStmt != null) {
                prepStmt.close();
            }
            if (myRs != null) {
                myRs.close();
            }
        }
    }

    void updateStudent(Student theStudent) throws Exception{
        Connection myConn = null;
        PreparedStatement preparedStatement = null;

        try{
            myConn = dataSource.getConnection();

            String sql = "UPDATE student SET first_name=?, last_name=?, " +
                    "email=? WHERE id=?";
            preparedStatement = myConn.prepareStatement(sql);

            // Set params
            preparedStatement.setString(1, theStudent.getFirstName());
            preparedStatement.setString(2, theStudent.getLastName());
            preparedStatement.setString(3, theStudent.getEmail());
            preparedStatement.setInt(4, theStudent.getId());

            preparedStatement.execute();
        }finally {
            if (myConn != null) {
                myConn.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }
}
