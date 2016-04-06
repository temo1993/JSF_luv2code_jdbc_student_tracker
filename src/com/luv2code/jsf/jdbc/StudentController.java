package com.luv2code.jsf.jdbc;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedBean
@SessionScoped
public class StudentController {
    private List<Student> students;
    private StudentDbUtil studentDbUtil;
    private Logger logger = Logger.getLogger(getClass().getName());

    public StudentController() throws Exception{
        students = new ArrayList<>();
        studentDbUtil = StudentDbUtil.getInstance();
    }

    public List<Student> getStudents() {
        return students;
    }

    public void loadStudent(){
        logger.info("Loading Students");
        students.clear();

        try{
            // Get all students from database
            students = studentDbUtil.getStudents();
        } catch (Exception e) {
            // Send this to server logs
            logger.log(Level.SEVERE, "Error loading students", e);

            // Add error message for JSF Page
            addErrorMessage(e);
        }
    }

    public String loadStudent(int studentId){
        logger.info("Loading Student: " + studentId);

        try{
            // Get student from database
            Student theStudent = studentDbUtil.getStudent(studentId);

            // Put in the request attribute ... so we can use it on the form page
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

            Map<String, Object> requestMap = externalContext.getRequestMap();
            requestMap.put("student", theStudent);
        } catch (Exception e) {
            // Send this to server logs
            logger.log(Level.SEVERE, "Error loading student id: " + studentId, e);

            // Add error message for JSF Page
            addErrorMessage(e);

            return null;
        }
        return "update-student-form.xhtml";
    }

    private void addErrorMessage(Exception e) {
        FacesMessage message = new FacesMessage("Error: " + e.getMessage());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

//    public StudentDbUtil getStudentDbUtil() {
//        return studentDbUtil;
//    }
//
//    public void setStudentDbUtil(StudentDbUtil studentDbUtil) {
//        this.studentDbUtil = studentDbUtil;
//    }
//
//    public Logger getLogger() {
//        return logger;
//    }
//
//    public void setLogger(Logger logger) {
//        this.logger = logger;
//    }

    public String addStudent(Student theStudent) {
        logger.info("Adding Student: " + theStudent);
        try{
            // Add Student to the database
            studentDbUtil.addStudent(theStudent);
        }catch (Exception exc){
            // Send this to server logs
            logger.log(Level.SEVERE, "Error adding students", exc);

            // Add error message for JSF page
            addErrorMessage(exc);
        }return theStudent.toString();
    }

    public String updateStudent(Student theStudent) {
        logger.info("updating student: " + theStudent);

        try{
            // Update student in the database
            studentDbUtil.updateStudent(theStudent);
        }catch (Exception exc){
            // Send this to server logs
            logger.log(Level.SEVERE, "Error updating student: " + theStudent, exc);

            // Add error message for JSF page
            addErrorMessage(exc);

            return null;
        }
        return "list-students?faces-redirect=true";
    }

    public String deleteStudent(int studentId) {
        logger.info("Deleting student id: " + studentId);

        try{
            // Delete the student from the database
            studentDbUtil.deleteStudent(studentId);
        } catch (Exception exc){
            // Send this to server logs
            logger.log(Level.SEVERE, "Error deleting student id: " + studentId, exc);

            // Add error message for JSF page
            addErrorMessage(exc);
            return null;
        }
        return "list-students";
    }
}
