package controller;

import db.DBconnection;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import tm.ToDoTM;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ToDoListFormController {
    public Text lblWElcomeText;
    public Label lblUserId;
    public AnchorPane root;
    public Pane subroot;
    public TextField txtDescription;
    public DatePicker datePiicker;
    public ColorPicker colorPicker;
    public static String selectedColor;
    public Label lblError;
    public Label lblSuccessful;
    public Label descriptionError;
    public Label dateError;
    public Rectangle lblGradient;
    public ListView<ToDoTM> lstToDo;
    public TextField txtToDo;
    public Button btnUpdate;
    public Button btnDelete;
    public String selectedID;
    public Label txtDateAndTime;

    public void initialize(){

        lblWElcomeText.setText("Hi " + LoginFormController.loginUsername + "! Welcome to the To-Do List");

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(8), event -> {
            lblWElcomeText.setText("Your To-Do List Needs To Be Focused On You");
        }));
        timeline.play();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                txtDateAndTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
        };
        timer.start();

        lblUserId.setText(LoginFormController.loginId);

        subroot.setVisible(false);
        descriptionError.setVisible(false);
        dateError.setVisible(false);
        lblError.setVisible(false);
        lblSuccessful.setVisible(false);

        loadList();

        setDisableCommon(true);

        lstToDo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoTM>() {
            @Override
            public void changed(ObservableValue<? extends ToDoTM> observable, ToDoTM oldValue, ToDoTM newValue) {
                setDisableCommon(false);
                subroot.setVisible(false);

                ToDoTM selectedItem = lstToDo.getSelectionModel().getSelectedItem();

                if (selectedItem == null) {
                    return;
                }
                txtToDo.setText(selectedItem.getDescription());
                selectedID = selectedItem.getId();
            }
        });
    }

    public void setDisableCommon(boolean isDisable){
        txtToDo.setDisable(isDisable);
        btnDelete.setDisable(isDisable);
        btnUpdate.setDisable(isDisable);
    }

    public void btnLogOutOnAction(ActionEvent actionEvent) throws IOException {

        CreateNewAccountFormController obj = new CreateNewAccountFormController();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you sure want to Logout!!", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){
            Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));
            Scene scene = new Scene(parent);

            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.centerOnScreen();
        }
    }

    public void btnAddNewTodoOnAction(ActionEvent actionEvent) {

        lstToDo.getSelectionModel().clearSelection();

        subroot.setVisible(true);
        txtDescription.requestFocus();
        setDisableCommon(true);
        txtToDo.clear();

    }

    public void btnAddToListOnAction(ActionEvent actionEvent) {

        if(txtDescription.getText().equals("") || (datePiicker.getValue()==null)){
            lblError.setVisible(true);
            lblError.setText("Fields cannot be empty !!");
            lblWElcomeText.setVisible(false);
            lblGradient.setVisible(false);

            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
                lblError.setVisible(false);
                lblWElcomeText.setVisible(true);
                lblGradient.setVisible(true);
            }));
            timeline.play();

        } else {
            addToDoList();
        }
    }

    public String autoGenerateTodoId(){

        Connection connection = DBconnection.getInstance().getConnection();
        String newTodoId=null;

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT id FROM todo ORDER BY id DESC LIMIT 1");

            boolean isTodoIdExist = resultSet.next();

            if(isTodoIdExist){

                String prevTodoId = resultSet.getString(1);
                String todoId = prevTodoId.substring(1, prevTodoId.length());

                int intTodoId = Integer.parseInt(todoId);
                intTodoId++;

                if(intTodoId<10){
                    newTodoId = "T000"+ intTodoId;

                } else if(intTodoId<100){
                    newTodoId = "T00" + intTodoId;

                } else if(intTodoId<1000){
                    newTodoId = "T0" + intTodoId;
                } else {
                    newTodoId = "T" + intTodoId;
                }

            } else {
                newTodoId="T0001";
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        return newTodoId;
    }

    public void txtDescriptionOnAction(ActionEvent actionEvent) {

        if(txtDescription.getText().equals("")){
            txtDescription.setStyle("-fx-border-color: red; -fx-border-radius: 30;-fx-background-radius: 30;-fx-border-width: 2");
            descriptionError.setVisible(true);
            descriptionError.setText("Description is required");
            txtDescription.requestFocus();

        } else {
            txtDescription.setStyle("-fx-border-color: transparent; -fx-border-radius: 30;-fx-background-radius: 30");
            descriptionError.setVisible(false);
            datePiicker.requestFocus();
        }
    }

    public void datePickerOnAction(ActionEvent actionEvent) {

        if(datePiicker.getValue()==null){
            dateError.setVisible(true);
            dateError.setText("Date is required");
            datePiicker.requestFocus();
        } else {
            dateError.setVisible(false);
            colorPicker.requestFocus();
        }
    }

    public void colorPickerOnAction(ActionEvent actionEvent) {

    }

    public void addToDoList(){

        String id = autoGenerateTodoId();
        String description = txtDescription.getText();
        String userId = lblUserId.getText();
        LocalDate date = datePiicker.getValue();
        String color = colorPicker.getValue().toString();
        color = "#" + color.substring(2, color.length());
        selectedColor=color;

        Connection connection = DBconnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO todo VALUES(?,?,?,?,?)");
            preparedStatement.setObject(1, id);
            preparedStatement.setObject(2, description);
            preparedStatement.setObject(3, userId);
            preparedStatement.setObject(4, color);
            preparedStatement.setObject(5, date);

            int i = preparedStatement.executeUpdate();

//            if(i>0){
//                lblSuccessful.setVisible(true);
//                lblSuccessful.setText("Your To-Do added successfully");
//                lblWElcomeText.setVisible(false);
//                lblGradient.setVisible(false);
//
//                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4),event -> {
//                    lblSuccessful.setVisible(false);
//                    lblWElcomeText.setVisible(true);
//                    lblGradient.setVisible(true);
//                }));
//                timeline.play();
//
//            } else {
//                System.out.println("Not added");
//            }

            loadList();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        subroot.setStyle("-fx-border-color: "+ selectedColor + "; -fx-border-width: 5");

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4),event -> {
            subroot.setStyle("-fx-background-color: transparent; -fx-border-color:  #576574; -fx-border-width: 2");
            subroot.setVisible(false);
        }));
        timeline.play();

        txtDescription.clear();
        txtDescription.requestFocus();
        datePiicker.getEditor().clear();
    }

    public void loadList(){
        ObservableList<ToDoTM> items = lstToDo.getItems();
        items.clear();

        Connection connection = DBconnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM todo WHERE user_id=?");
            preparedStatement.setObject(1, lblUserId.getText());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                String id = resultSet.getString(1);
                String description = resultSet.getString(2);
                String userId = resultSet.getString(3);
                String color = resultSet.getString(4);
                String date = resultSet.getString(5);

                items.add(new ToDoTM(id,description,userId,color,date));
            }

            lstToDo.setCellFactory(lv -> new ListCell<ToDoTM>() {
                private Circle circle = new Circle(15);
                private HBox hBox = new HBox(10);
                private Label descLabel = new Label();
                private Label dateLabel = new Label();

                {
                    dateLabel.setTextFill(Color.ROYALBLUE);
                    descLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold");
                    dateLabel.setStyle("-fx-font-size: 16px;");
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    hBox.getChildren().addAll(circle, descLabel, dateLabel);
                }

                @Override
                protected void updateItem(ToDoTM item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // Set the texts for the labels
                        descLabel.setText(item.getDescription());
                        dateLabel.setText(" - " + item.getDate().substring(5));
                        circle.setFill(Color.web(item.getColor()));

                        setGraphic(hBox); // Set the HBox as the graphic of the cell
                        setPrefHeight(70); // Set preferred height of the row
                    }
                }
            });

            lstToDo.refresh();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {

        Connection connection = DBconnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE todo SET description=? WHERE id=?");
            preparedStatement.setObject(1, txtToDo.getText());
            preparedStatement.setObject(2, selectedID);

            preparedStatement.executeUpdate();
            txtToDo.clear();
            loadList();
            setDisableCommon(true);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete this to do?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){
            Connection connection = DBconnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM todo WHERE id=?");
                preparedStatement.setObject(1, selectedID);

                preparedStatement.executeUpdate();
                txtToDo.clear();
                loadList();
                setDisableCommon(true);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
