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
import javafx.util.Duration;

import javax.imageio.IIOException;
import java.io.IOException;
import java.security.Key;
import java.sql.*;

public class LoginFormController {
    public AnchorPane root;
    public TextField txtUsername;
    public PasswordField txtPassword;
    public Label lblSuccessfull;
    public Label lblError;
    public Label lblUsernameRequired;
    public Label lblPasswordRequired;
    public Button btnLogin;

    public static String loginUsername;
    public static String loginId;

    public void initialize(){

        lblError.setVisible(false);
        lblSuccessfull.setVisible(false);
        lblUsernameRequired.setVisible(false);
        lblPasswordRequired.setVisible(false);
    }

    public void lblCreateNewAccountOnClicked(MouseEvent mouseEvent) throws IOException {

        Parent parent = FXMLLoader.load(this.getClass().getResource("../view/CreateNewAccountForm.fxml"));

        Scene scene = new Scene(parent);

        Stage stage = (Stage)root.getScene().getWindow();

        stage.setScene(scene);
        stage.setTitle("Create New Account");
        stage.centerOnScreen();
    }

    public void txtUsernameOnAction(ActionEvent actionEvent) {

        if(txtUsername.getText().equals("")){
            lblUsernameRequired.setVisible(true);
            lblUsernameRequired.setText("Username is Required");
            txtUsername.requestFocus();
            txtUsername.setStyle("-fx-border-color: red; -fx-border-radius: 25; -fx-border-width: 2; -fx-background-radius: 25");
        } else {

            lblUsernameRequired.setVisible(false);
            txtUsername.setStyle("-fx-border-color: transparent; -fx-border-radius: 25; -fx-border-width: 2; -fx-background-radius: 25");
            txtPassword.requestFocus();
        }
    }

    public void txtPasswordOnAction(ActionEvent actionEvent) {

        if(txtPassword.getText().isEmpty()){
            lblPasswordRequired.setVisible(true);
            lblPasswordRequired.setText("Password is Required");
            txtPassword.requestFocus();
            txtPassword.setStyle("-fx-border-color: red; -fx-border-radius: 25; -fx-border-width: 2; -fx-background-radius: 25");
            lblError.setText("Fields cannot be empty");
            errorMsgDisplay();

        } else {
            lblPasswordRequired.setVisible(false);
            txtPassword.setStyle("-fx-border-color: transparent; -fx-border-radius: 25; -fx-border-width: 2; -fx-background-radius: 25");
            Login();
        }
    }

    public void btnLoginOnAction(ActionEvent actionEvent) {
        Login();
    }

    public void Login(){

        if(txtUsername.getText().isEmpty() || txtPassword.getText().isEmpty()){
            lblError.setText("Fields cannot be empty");
            errorMsgDisplay();

        } else {
            String username = txtUsername.getText();
            String password = txtPassword.getText();

            Connection connection = DBconnection.getInstance().getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE name=? AND password=?");
                preparedStatement.setObject(1, username);
                preparedStatement.setObject(2, password);

                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next()){
                    lblSuccessfull.setVisible(true);
                    lblSuccessfull.setText("Log in Successfully");

                    loginUsername = resultSet.getString(2);
                    loginId = resultSet.getString(1);

                    Parent parent = FXMLLoader.load(this.getClass().getResource("../view/ToDoListForm.fxml"));
                    Scene scene = new Scene(parent);
                    Stage stage = (Stage) root.getScene().getWindow();

                    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
                        lblSuccessfull.setVisible(false);
                        stage.setScene(scene);
                        stage.setTitle("To Do Form");
                        stage.centerOnScreen();
                    }));
                    timeline.play();

                } else {
                    lblSuccessfull.setVisible(false);
                    txtUsername.clear();
                    txtPassword.clear();
                    txtUsername.requestFocus();
                    lblError.setText("Invalid Username of Password");
                    errorMsgDisplay();
                }

            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
            }

        }

    }

    public void errorMsgDisplay(){

        lblError.setVisible(true);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(8), event -> {
            lblError.setVisible(false);
        }));
        timeline.play();
    }


}
