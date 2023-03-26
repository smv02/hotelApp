package controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import application.database;

import java.sql.*;
import java.text.SimpleDateFormat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.getData;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class CheckInController implements Initializable {

	@FXML
    private DatePicker checkIn_date;

    @FXML
    private AnchorPane checkIn_form;

    @FXML
    private DatePicker checkOut_date;

    @FXML
    private Button close;

    @FXML
    private Label customerNumber;

    @FXML
    private TextField emailAddress;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField phoneNumber;
    
    @FXML
    private Label total;

    @FXML
    private Label totalDays;
    
    @FXML
    private ComboBox<?> roomNumber;

    @FXML
    private ComboBox<?> roomType;
    
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    private Statement statement;
    
    public void customerCheckIn() {
    	
    	String insertCustomerData = "INSERT INTO customer (customer_id, total, roomType, roomNumber, firstName, lastName, phoneNumber, email, checkIn, checkOut) VALUES(?,?,?,?,?,?,?,?,?,?)";
    	
    	connect = database.connectDb();
    	
    	try {
    		
    		String customerNum = customerNumber.getText();
    		String roomT = (String)roomType.getSelectionModel().getSelectedItem();
    		String roomN = (String)roomNumber.getSelectionModel().getSelectedItem();
    		String firstN = firstName.getText();
    		String lastN = lastName.getText();
    		String phoneNum = phoneNumber.getText();
    		String email = emailAddress.getText();
    		String checkInDate = String.valueOf(checkIn_date.getValue());
    		String checkOutDate = String.valueOf(checkOut_date.getValue());
    		
    		Alert alert;
    		
    		if(customerNum == null || firstN == null || lastN == null || phoneNum == null || email == null || checkInDate == null || checkOutDate == null) {
    			alert = new Alert(AlertType.ERROR);
    			alert.setTitle("Error Message");
    			alert.setHeaderText(null);
    			alert.setContentText("Please fill all blank fields");
    			alert.showAndWait();
    		}else {
    			    			
    			alert = new Alert(AlertType.CONFIRMATION);
    			alert.setTitle("Confirmation Message");
    			alert.setHeaderText(null);
    			alert.setContentText("Are you sure?");
    			
    			Optional<ButtonType> optional = alert.showAndWait();
    			
    			if(optional.get().equals(ButtonType.OK)){
    				String totalC = String.valueOf(totalP);
    				
    				prepare = connect.prepareStatement(insertCustomerData);
	    			prepare.setString(1, customerNum);
	    			prepare.setString(2, totalC);
	    			prepare.setString(3, roomT);
	    			prepare.setString(4, roomN);
	    			prepare.setString(5, firstN);
	    			prepare.setString(6, lastN);
	    			prepare.setString(7, phoneNum);
	    			prepare.setString(8, email);
	    			prepare.setString(9, checkInDate);
	    			prepare.setString(10, checkOutDate);
	    			
	    			prepare.executeUpdate();
	    			
	    			String date = String.valueOf(checkIn_date.getValue());
	    			String customerN = customerNumber.getText();
	    			
	    			String customerReceipt = "INSERT INTO customer_receipt (customer_num, total, date) VALUES(?,?,?)";
	    			
	    			prepare = connect.prepareStatement(customerReceipt);
	    			prepare.setString(1, customerN);
	    			prepare.setString(2, totalC);
	    			prepare.setString(3, date);
	    			
	    			prepare.execute();
	    			
	    			String sqlEditStatus = "UPDATE room SET status = 'Occupied' WHERE roomNumber = '"+roomN+"'";
	    			
	    			statement = connect.createStatement();
	    			statement.executeUpdate(sqlEditStatus);
	    			
	    			alert = new Alert(AlertType.INFORMATION);
	    			alert.setTitle("Information Message");
	    			alert.setHeaderText(null);
	    			alert.setContentText("Successfully checked-In!");
	    			alert.showAndWait();
	    			
	    			reset();
    				
    			}else {
    				return;
    			}
    		}
    		
    		
    		
    		
    	}catch(Exception e) {e.printStackTrace();}
    	
    }
    
    public void reset() {
    	firstName.setText("");
    	lastName.setText("");
    	phoneNumber.setText("");
    	emailAddress.setText("");
    	roomType.getSelectionModel().clearSelection();
    	roomNumber.getSelectionModel().clearSelection();
    	totalDays.setText("---");
    	total.setText("$0.0");
    }
    
    public void totalDays() {
    	
    	Alert alert;
    	
    	if(checkOut_date.getValue().isAfter(checkIn_date.getValue())) {
    		
    		getData.totalDays =  checkOut_date.getValue().compareTo(checkIn_date.getValue());
    		    		
    	}else {
    		alert = new Alert(AlertType.ERROR);
    		alert.setTitle("ERROR Message");
			alert.setHeaderText(null);
			alert.setContentText("Invalid checked-out date");
			alert.showAndWait();
    	}
    	
    }
    
    private double totalP = 0;
    
    public void displayTotal() {
    	
    	totalDays();
    	
    	String totalD = String.valueOf(getData.totalDays);
    	
    	totalDays.setText(totalD);
    	
    	String selectItem = (String) roomNumber.getSelectionModel().getSelectedItem();
    	
    	String sql = "SELECT * FROM room WHERE roomNumber = '"+selectItem+"'";
    	
    	double priceData = 0;
    	
    	connect = database.connectDb();
    	
    	try {
    		
    		prepare = connect.prepareStatement(sql);
    		result = prepare.executeQuery();
    		
    		while(result.next()) {
    			
    			priceData = result.getDouble("price");
    			
    		}
    		
    		totalP = (float)((priceData)*getData.totalDays);
    		System.out.println("Total payment: " + totalP);
    		System.out.println("priceDatat: " + priceData);
    		total.setText("$" + String.valueOf(totalP));    		
    		
    	}catch(Exception e) {e.printStackTrace();}
    	
    }
    
    
    public void customerNumber() {
    	
    	String custonerNum = "SELECT customer_id FROM customer";
    	
    	connect = database.connectDb();
    	
    	try {
    		
    		prepare = connect.prepareStatement(custonerNum);
    		result = prepare.executeQuery();
    		
    		while(result.next()) {
    			getData.customerNum = result.getInt("customer_id");
    		}
    		
    	}catch(Exception e) {e.printStackTrace();}
    	
    }
    
    public void roomTypeList() {
    	
    	String listType = "SELECT * FROM room WHERE status = 'Available' GROUP BY type ORDER BY type ASC";
    	
    	connect = database.connectDb();
    	
    	try {
    		
    		ObservableList listData = FXCollections.observableArrayList();
    		prepare = connect.prepareStatement(listType);
    		result = prepare.executeQuery();
    		
    		while(result.next()) {
    			listData.add(result.getString("type"));
    		}
    		
    		roomType.setItems(listData);
    		
    		roomNumberList();
    		    		
    	}catch(Exception e) {e.printStackTrace();}
    	
    }
    
    
    
    public void roomNumberList() {
    	
    	String item = (String)roomType.getSelectionModel().getSelectedItem();
    	
    	String availableRoomNumber = "SELECT * FROM room WHERE type = '"+item+"' and status = 'Available' ORDER BY roomNumber ASC";
    	connect = database.connectDb();
    	
    	try {
    		
    		ObservableList listData = FXCollections.observableArrayList();
    		prepare = connect.prepareStatement(availableRoomNumber);
    		result = prepare.executeQuery();
    		
    		while(result.next()) {
    			listData.add(result.getString("roomNumber"));
    		}
    		
    		roomNumber.setItems(listData);
    		
    	}catch(Exception e) {e.printStackTrace();}
    	
    }
    
    public void displayCustomerNumber() {
    	customerNumber();
    	customerNumber.setText(String.valueOf(getData.customerNum + 1));
    }
    
    public void receipt() {
    	
    	Document doc = new Document();
    	try {
			PdfWriter.getInstance(doc, new FileOutputStream("receipt.pdf"));
			doc.open();
			com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance("C:\\Users\\kolas\\Downloads\\hotel_logo.png");
			img.setAlignment(com.itextpdf.text.Image.ALIGN_CENTER);
			doc.add(img);
			String format = "dd/mm/yy hh:mm";
			
			SimpleDateFormat formater = new SimpleDateFormat(format);
			java.util.Date date = new java.util.Date();
			doc.add(new Paragraph("Hotel #", FontFactory.getFont(FontFactory.TIMES_ROMAN, 28, Font.BOLD, BaseColor.BLACK)));
			doc.add(new Paragraph("Customer #: " + (String)customerNumber.getText(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.NORMAL, BaseColor.BLACK)));
			doc.add(new Paragraph("First Name: " + (String)firstName.getText(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.NORMAL, BaseColor.BLACK)));
			doc.add(new Paragraph("Last Name: " + (String)lastName.getText(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.NORMAL, BaseColor.BLACK)));
			doc.add(new Paragraph("Room Type: " + (String)roomType.getSelectionModel().getSelectedItem(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.NORMAL, BaseColor.BLACK)));
			doc.add(new Paragraph("Room #: " + (String)roomNumber.getSelectionModel().getSelectedItem(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.NORMAL, BaseColor.BLACK)));
			doc.add(new Paragraph("Checked-In: " + String.valueOf(checkIn_date.getValue()), FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.NORMAL, BaseColor.BLACK)));
			doc.add(new Paragraph("Checked-Out: " + String.valueOf(checkOut_date.getValue()), FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.NORMAL, BaseColor.BLACK)));
			doc.add(new Paragraph(" ", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.NORMAL, BaseColor.BLACK)));
			doc.add(new Paragraph(" ", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.NORMAL, BaseColor.BLACK)));
			doc.add(new Paragraph("Total Payment: " + total.getText(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 18, Font.NORMAL, BaseColor.BLACK)));
			doc.add(new Paragraph(" ", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.NORMAL, BaseColor.BLACK)));
			doc.add(new Paragraph("Signatures", FontFactory.getFont(FontFactory.TIMES_ROMAN, 16, Font.NORMAL, BaseColor.BLACK)));
			doc.add(new Paragraph(" ", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.NORMAL, BaseColor.BLACK)));
			doc.add(new Paragraph("Customer", FontFactory.getFont(FontFactory.TIMES_ROMAN, 16, Font.NORMAL, BaseColor.BLACK)));
			doc.add(new Paragraph(" ", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.NORMAL, BaseColor.BLACK)));
			doc.add(new Paragraph("__________________", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.NORMAL, BaseColor.BLACK)));
			doc.add(new Paragraph(" ", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.NORMAL, BaseColor.BLACK)));
			doc.add(new Paragraph(" ", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.NORMAL, BaseColor.BLACK)));
			doc.add(new Paragraph("Manager Hotel", FontFactory.getFont(FontFactory.TIMES_ROMAN, 16, Font.NORMAL, BaseColor.BLACK)));
			doc.add(new Paragraph(" ", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.NORMAL, BaseColor.BLACK)));
			doc.add(new Paragraph("__________________", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.NORMAL, BaseColor.BLACK)));
			doc.close();
			Desktop.getDesktop().open(new File("receipt.pdf"));
    	} catch (FileNotFoundException | DocumentException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @Override
    public void initialize(URL arg0, ResourceBundle argl) {
    	
    	displayCustomerNumber();
    	
    	roomTypeList();
    	roomNumberList();
    	
    }
}