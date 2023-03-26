package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import application.database;

import java.sql.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.getData;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class SignInController implements Initializable {

	@FXML
    private Button close;

    @FXML
    private Button loginBtn;

    @FXML
    private AnchorPane main_form;

    @FXML
    private PasswordField password;

    @FXML
    private StackPane stack_form;

    @FXML
    private TextField username;
    
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    
    public void login() {
    	String user = username.getText();
    	String pass = password.getText();
    	
    	String sql = "SELECT * FROM admin WHERE username = ? and password = ?";
    	
    	connect = database.connectDb();
    	
    	try{
    		prepare = connect.prepareStatement(sql);
    		prepare.setString(1, user);
    		prepare.setString(2, pass);
    		
    		result = prepare.executeQuery();
    		
    		Alert alert;
    		
    		if (user.isEmpty() || pass.isEmpty()) {
    			alert = new Alert(AlertType.ERROR);
    			alert.setTitle("Error Message");
    			alert.setHeaderText(null);
    			alert.setContentText("Please fill all blank fields");
    			alert.showAndWait();
    		} else {
    		
	    		if (result.next()) {
	    			
	    			getData.username = username.getText();
	    			
	    			alert = new Alert(AlertType.INFORMATION);
	    			alert.setTitle("Information Message");
	    			alert.setHeaderText(null);
	    			alert.setContentText("Successfuly login!");
	    			alert.showAndWait();
	    			
	    			loginBtn.getScene().getWindow().hide();
	    			
	    			Parent root = FXMLLoader.load(getClass().getResource("/interfaces/dashboard.fxml"));
	    			
	    			Stage stage = new Stage();
	    			Scene scene = new Scene(root);
	    			
	    			scene.setFill(Color.TRANSPARENT);
	    			stage.setScene(scene);
	    			stage.initStyle(StageStyle.TRANSPARENT);
	    			stage.show();
	    		} else {
	    			alert = new Alert(AlertType.ERROR);
	    			alert.setTitle("Error Message");
	    			alert.setHeaderText(null);
	    			alert.setContentText("Wrong Username/Password!");
	    			alert.showAndWait();
	    		}
    		}
    	}catch(Exception e) {e.printStackTrace();}
    	
    }
    
    
    
    public void exit() {
    	System.exit(0);
    }
    
    @Override
    public void initialize(URL arg0, ResourceBundle argl) {
    	
    }
}