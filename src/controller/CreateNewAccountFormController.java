package controller;

import db.DBconnection;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.IOException;
import java.net.ConnectException;
import java.security.Key;
import java.sql.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateNewAccountFormController {
    public PasswordField txtPassword;
    public PasswordField txtConfirmPassword;
    public Label lblErrorAlert;
    public TextField txtUsername;
    public TextField txtEmail;
    public Button btnAddNewUser;
    public Label lblUserId;
    public Label lblUsernameRequiired;
    public Label lblEmailRequired;
    public Label lblPasswordRequiire;
    public Label lblSuccessfull;
    public AnchorPane root;
    public static String userId;

    public void initialize(){

        lblErrorAlert.setVisible(false);
        lblUserId.setVisible(false);

        lblUsernameRequiired.setVisible(false);
        lblEmailRequired.setVisible(false);
        lblPasswordRequiire.setVisible(false);
        lblSuccessfull.setVisible(false);

        setDisableCommon(true);
    }

    public void setDisableCommon(boolean isDisable){
        txtUsername.setDisable(isDisable);
        txtEmail.setDisable(isDisable);
        txtPassword.setDisable(isDisable);
        txtConfirmPassword.setDisable(isDisable);
    }

    public void btnRegisterOnAction(ActionEvent actionEvent) {

        if(txtUsername.getText().equals("") ||
           txtEmail.getText().equals("") ||
           txtConfirmPassword.getText().equals("")){

            lblErrorAlert.setVisible(true);
            lblErrorAlert.setText("Fields cannot be empty");

            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(6),event -> {
                lblErrorAlert.setVisible(false);
            }));
            timeline.play();

        } else {
            registeredEmail();
        }

    }

    public void registeredEmail(){

        Connection connection = DBconnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE email=?");
            preparedStatement.setObject(1, txtEmail.getText());

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean isEmailFound = resultSet.next();

            if(isEmailFound){
                lblErrorAlert.setVisible(true);
                lblErrorAlert.setText("The email is already registered!");

                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5),event -> {
                    lblErrorAlert.setVisible(false);
                }));
                timeline.play();
            } else {
                isPasswordMatched();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    public void isPasswordMatched(){

        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();


        if(password.equals(confirmPassword)){

            setBorderColorAndRadius("transparent");
            lblErrorAlert.setVisible(false);

            String id = lblUserId.getText().substring(10,lblUserId.getText().length());
            String username = txtUsername.getText();
            String email = txtEmail.getText();

            Connection connection = DBconnection.getInstance().getConnection();
//
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO user VALUES(?,?,?,?)");
                preparedStatement.setObject(1,id);
                preparedStatement.setObject(2, username);
                preparedStatement.setObject(3, email);
                preparedStatement.setObject(4, confirmPassword);

                int i = preparedStatement.executeUpdate();

                if(i>0){

                    lblSuccessfull.setVisible(true);
                    lblSuccessfull.setText("Registered Successfully");

                    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
                        lblSuccessfull.setVisible(false);

                        try {
                            loadLoginForm();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }));
                    timeline.play();
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }


        } else {

            txtPassword.clear();
            txtConfirmPassword.clear();

            txtPassword.requestFocus();

            setBorderColorAndRadius("red");

            lblErrorAlert.setVisible(true);

            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(6), event -> {
                lblErrorAlert.setVisible(false);
            }));
            timeline.play();

            lblErrorAlert.setText("Password doesn't matched");
        }
    }


    public void setBorderColorAndRadius(String color){
        txtPassword.setStyle("-fx-border-color: " + color + "; -fx-border-radius: 25px; -fx-background-radius: 25px");
        txtConfirmPassword.setStyle("-fx-border-color: "+ color + "; -fx-border-radius: 25px; -fx-background-radius: 25px");
    }

    public void btnAddNewUserOnAction(ActionEvent actionEvent) {
        setDisableCommon(false);

        btnAddNewUser.setVisible(false);

        autoGeneratedId();
    }

    public void autoGeneratedId(){

        lblUserId.setVisible(true);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(6), event -> {
            lblUserId.setVisible(false);
        }));
        timeline.play();

        Connection connection = DBconnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT id FROM user ORDER BY id DESC LIMIT 1");

            boolean isExist = resultSet.next();

            if(isExist) {
                String userId = resultSet.getString(1);

                String substring = userId.substring(1, userId.length());

                int intUserId = Integer.parseInt(substring);
                intUserId++;

                if(intUserId<10){
                    userId = "U000" + intUserId;

                } else if (intUserId<100){
                    userId = "U00" + intUserId;

                } else if(intUserId<1000){
                    userId = "U0" + intUserId;
                    
                } else {
                    userId = "U" + intUserId;
                }
                lblUserId.setText("USER ID - " + userId);



            } else {

                lblUserId.setText("USER ID - U0001");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void txtUernameOnAction(ActionEvent actionEvent) {

        String regex = "^[A-Za-z]\\w{5,29}$";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(txtUsername.getText());
        boolean isMatch = matcher.matches();

        if(txtUsername.getText().equals("")) {
            lblUsernameRequiired.setVisible(true);
            lblUsernameRequiired.setText("Username is required");
            txtUsername.setStyle("-fx-border-color: red; -fx-border-width: 2; -fx-border-radius: 25; -fx-background-radius: 25");
            txtUsername.requestFocus();

        } else if(isMatch==false){

            lblUsernameRequiired.setVisible(true);
            lblUsernameRequiired.setText("Enter a valid name");
            txtUsername.setStyle("-fx-border-color: red; -fx-border-width: 2; -fx-border-radius: 25; -fx-background-radius: 25");
            txtUsername.requestFocus();

        } else {
            lblUsernameRequiired.setVisible(false);
            txtUsername.setStyle("-fx-border-color: transparent; -fx-border-width: 1; -fx-border-radius: 25; -fx-background-radius: 25");
            txtEmail.requestFocus();
        }
    }

    public void txtNewPasswordOnAction(ActionEvent actionEvent) {

        if(txtPassword.getText().equals("")) {
            lblPasswordRequiire.setVisible(true);
            lblPasswordRequiire.setText("Password is Required");
            txtPassword.setStyle("-fx-border-color: red; -fx-border-width: 2; -fx-border-radius: 25; -fx-background-radius: 25");
            txtPassword.requestFocus();

        } else {
            lblPasswordRequiire.setVisible(false);
            txtPassword.setStyle("-fx-border-color: transparent; -fx-border-width: 1; -fx-border-radius: 25; -fx-background-radius: 25");
            txtConfirmPassword.requestFocus();
        }

    }

    public void txtEmailOnAction(ActionEvent actionEvent) {

        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(txtEmail.getText());
        boolean isMatch = matcher.matches();

        if(txtEmail.getText().equals("")) {
            lblEmailRequired.setVisible(true);
            lblEmailRequired.setText("Email is Required");
            txtEmail.setStyle("-fx-border-color: red; -fx-border-width: 2; -fx-border-radius: 25; -fx-background-radius: 25");
            txtEmail.requestFocus();

        } else if(isMatch==false){
            lblEmailRequired.setVisible(true);
            lblEmailRequired.setText("Enter a valid email");
            txtEmail.setStyle("-fx-border-color: red; -fx-border-width: 2; -fx-border-radius: 25; -fx-background-radius: 25");
            txtEmail.requestFocus();

        } else {
            lblEmailRequired.setVisible(false);
            txtEmail.setStyle("-fx-border-color: transparent; -fx-border-width: 1; -fx-border-radius: 25; -fx-background-radius: 25");
            txtPassword.requestFocus();
        }
    }

    public void txtConfirmPasswordOnAction(ActionEvent actionEvent) {
        registeredEmail();
    }


    public void lblLoginMouseClicked(MouseEvent mouseEvent) throws IOException {
        loadLoginForm();
    }

    public void loadLoginForm() throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.centerOnScreen();
    }

}
