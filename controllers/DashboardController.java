package controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.database;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.roomData;
import models.customerData;
import models.getData;


public class DashboardController implements Initializable {
	
	@FXML
    private AnchorPane main_form;
	
	@FXML
    private Button availableRooms_addBtn;

    @FXML
    private Button availableRooms_btn;

    @FXML
    private Button availableRooms_checkInBtn;

    @FXML
    private Button availableRooms_clearBtn;
    
    @FXML
    private TableView<roomData> availableRooms_tableView;

    @FXML
    private TableColumn<roomData, Double> availableRooms_col_price;

    @FXML
    private TableColumn<roomData, Integer> availableRooms_col_roomNumber;

    @FXML
    private TableColumn<roomData, String> availableRooms_col_roomType;

    @FXML
    private TableColumn<roomData, String> availableRooms_col_status;

    @FXML
    private Button availableRooms_deleteBtn;

    @FXML
    private AnchorPane availableRooms_form;

    @FXML
    private TextField availableRooms_price;

    @FXML
    private TextField availableRooms_roomNumber;

    @FXML
    private ComboBox<?> availableRooms_roomType;

    @FXML
    private TextField availableRooms_search;

    @FXML
    private ComboBox<?> availableRooms_status;

    @FXML
    private Button availableRooms_updateBtn;

    @FXML
    private Button closeBtn;

    @FXML
    private AnchorPane customers_Form;

    @FXML
    private Button customers_btn;

    @FXML
    private TableColumn<customerData, String> customers_checkedIn;

    @FXML
    private TableColumn<customerData, String> customers_checkedOut;

    @FXML
    private TableColumn<customerData, String> customers_customerNumber;

    @FXML
    private TableColumn<customerData, String> customers_firstName;

    @FXML
    private TableColumn<customerData, String> customers_lastName;

    @FXML
    private TableColumn<customerData, String> customers_phoneNumber;

    @FXML
    private TextField customers_search;

    @FXML
    private TableView<customerData> customers_tableView;

    @FXML
    private TableColumn<customerData, String> customers_totalPayment;

    @FXML
    private AreaChart<?, ?> dashboard_areaChart;

    @FXML
    private Button dashboard_btn;

    @FXML
    private AnchorPane dashboard_form;

    @FXML
    private Label dashboard_incomeToday;

    @FXML
    private Label dashboard_totalIncome;

    @FXML
    private Label dashboartd_bookToday;

    @FXML
    private Button logout_btn;

    @FXML
    private Button minimizeBtn;

    @FXML
    private Label username;
    
    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;
    
    private int count = 0;
    
    public void dashboardCountBookToday() {
    	
    	Date date = new Date();
    	java.sql.Date sqlDate = new java.sql.Date(date.getTime());
    	
    	String sql = "SELECT COUNT(id) FROM customer WHERE checkIn = '"+sqlDate+"'";
    	
    	connect = database.connectDb();
    	
    	count = 0;
    	
    	try {
    		
    		prepare = connect.prepareStatement(sql);
    		result = prepare.executeQuery();
    		
    		while(result.next()) {
    			
    			count = result.getInt("COUNT(id)");
    			
    		}
    		    		
    	}catch(Exception e) {e.printStackTrace();}
    	
    }
    
    public void dashboardDisplayBookToday() {
    	
    	dashboardCountBookToday();
    	
    	dashboartd_bookToday.setText(String.valueOf(count));
    }
    
    private double sumToday = 0;
    
    public void dashboardSumIncomeToday() {
    	
    	Date date = new Date();
    	java.sql.Date sqlDate = new java.sql.Date(date.getTime());
    	
    	String sql = "SELECT SUM(total) FROM customer_receipt WHERE date ='"+sqlDate+"'";
    	
    	connect = database.connectDb();
    	
    	try {
    		
    		prepare = connect.prepareStatement(sql);
    		result = prepare.executeQuery();
    		
    		while(result.next()) {
    			
    			sumToday = result.getDouble("SUM(total)");
    			
    		}
    		
    	}catch(Exception e) {e.printStackTrace();}
    	
    }
    
    public void dashboardDisplayIncomeToday() {
    	
    	dashboardSumIncomeToday();
    	
    	dashboard_incomeToday.setText("$"+String.valueOf(sumToday));
    	
    }
    
private double overall = 0;
    
    public void dashboardSumTotalIncome() {
    	
    	String sql = "SELECT SUM(total) FROM customer_receipt";
    	
    	connect = database.connectDb();
    	
    	try {
    		
    		prepare = connect.prepareStatement(sql);
    		result = prepare.executeQuery();
    		
    		while(result.next()) {
    			
    			overall = result.getDouble("SUM(total)");
    			
    		}
    		
    	}catch(Exception e) {e.printStackTrace();}
    	
    }
    
    public void dashboardTotalIncome() {
    	
    	dashboardSumTotalIncome();
    	
    	dashboard_totalIncome.setText("$"+String.valueOf(overall));
    	
    }
    
    public void dashboardChart() {
    	
    	dashboard_areaChart.getData().clear();
    	
    	String sql = "SELECT date, total FROM customer_receipt GROUP BY date ORDER BY TIMESTAMP(date) ASC LIMIT 8";
    	
    	connect = database.connectDb();
    	
    	XYChart.Series chart = new XYChart.Series();
    	
    	try {
    		
    		prepare = connect.prepareStatement(sql);
    		result = prepare.executeQuery();
    		
    		while(result.next()) {
    			
    			chart.getData().add(new XYChart.Data(result.getString(1), result.getInt(2)));
    			
    		}
    		
    		dashboard_areaChart.getData().add(chart);
    		
    	}catch(Exception e) {e.printStackTrace();}
    	
    }
    
    public ObservableList<roomData> availableRoomsListData(){
    	ObservableList<roomData> listData = FXCollections.observableArrayList();
    	
    	String sql = "SELECT * FROM room";
    	
    	connect = database.connectDb();
    	
    	try {
    		
    		roomData  roomD;
    		
    		prepare = connect.prepareStatement(sql);
    		result = prepare.executeQuery();
    		
    		while(result.next()) {
    			roomD = new roomData(result.getInt("roomNumber"), result.getString("type"), result.getString("status"), result.getDouble("price"));
    			
    			listData.add(roomD);
    		}
    		
    	}catch(Exception e) {e.printStackTrace();}
    	
    	return listData;
    }
    
    private ObservableList<roomData> roomDataList;
    
    public void availableRoomsShowData() {
    	
    	roomDataList = availableRoomsListData();
    	availableRooms_col_roomNumber.setCellValueFactory(new PropertyValueFactory<roomData, Integer>("roomNumber"));
    	availableRooms_col_roomType.setCellValueFactory(new PropertyValueFactory<roomData, String>("roomType"));
    	availableRooms_col_status.setCellValueFactory(new PropertyValueFactory<roomData, String>("status"));
    	availableRooms_col_price.setCellValueFactory(new PropertyValueFactory<roomData, Double>("price"));
    	
    	availableRooms_tableView.setItems(roomDataList);
    }
    
    
    public void availableRoomsSelectData() {
    	roomData roomD = availableRooms_tableView.getSelectionModel().getSelectedItem();
    	int num = availableRooms_tableView.getSelectionModel().getSelectedIndex();
    	
    	
    	if((num -1) < -1) {
    		return;
    	}
    	
    	availableRooms_roomNumber.setText(String.valueOf(roomD.getRoomNumber()));
    	availableRooms_price.setText(String.valueOf(roomD.getPrice()));
    	
    }
    
    public void availableRoomsSearch() {
    	
    	FilteredList<roomData> filter = new FilteredList<>(roomDataList, e -> true);
    	
    	availableRooms_search.textProperty().addListener((Observable, oldValue, newValue) ->{
    		
    		filter.setPredicate(predicateRoomData ->{
    			
    			if(newValue == null && newValue.isEmpty()) {
    				return true;
    			}
    			
    			String searchKey = newValue.toLowerCase();
    			
    			if(predicateRoomData.getRoomNumber().toString().contains(searchKey)) {
    				return true;
    			}else if(predicateRoomData.getRoomType().toLowerCase().contains(searchKey)) {
    				return true;
    			}else if(predicateRoomData.getPrice().toString().contains(searchKey)) {
    				return true;
    			}else if(predicateRoomData.getStatus().toLowerCase().contains(searchKey)) {
    				return true;
    			}else {
    				return false;
    			}
    			
    		});
    		
    	});
    	
    	SortedList<roomData> sortList = new SortedList<>(filter);
    	sortList.comparatorProperty().bind(availableRooms_tableView.comparatorProperty());
    	availableRooms_tableView.setItems(sortList);
    	
    }
    
    public void availableRoomsdAdd() {
    	String sql = "INSERT INTO room (roomNumber, type, status, price) VALUES (?,?,?,?)";
    	
    	connect = database.connectDb();
    	
    	try {
    		
    		String roomNumber = availableRooms_roomNumber.getText();
    		String type = (String)availableRooms_roomType.getSelectionModel().getSelectedItem();
    		String status = (String)availableRooms_status.getSelectionModel().getSelectedItem();
    		String price = availableRooms_price.getText();
    		
    		Alert alert;
    		
    		if (roomNumber == null || type == null || status == null || price == null) {
    			alert = new Alert(AlertType.ERROR);
    			alert.setTitle("Error Message");
    			alert.setHeaderText(null);
    			alert.setContentText("Please fill all blank fields");
    			alert.showAndWait();
    		} else {
    			
    			String check = "SELECT roomNumber FROM room WHERE roomNumber = '"+roomNumber+"'";
    			
    			prepare = connect.prepareStatement(check);
    			result = prepare.executeQuery();
    			
    			if (result.next()) {
    				alert = new Alert(AlertType.ERROR);
        			alert.setTitle("Error Message");
        			alert.setHeaderText(null);
        			alert.setContentText("Room #"+roomNumber+" was already exist!");
        			alert.showAndWait();
    			}else {
    				prepare = connect.prepareStatement(sql);
	    			prepare.setString(1, roomNumber);
	    			prepare.setString(2, type);
	    			prepare.setString(3, status);
	    			prepare.setString(4, price);
	    			
	    			prepare.executeUpdate();
	    			
	    			alert = new Alert(AlertType.INFORMATION);
	    			alert.setTitle("Information Message");
	    			alert.setHeaderText(null);
	    			alert.setContentText("Successfully added!");
	    			alert.showAndWait();
	    			
	    			availableRoomsShowData();
	    			
	    			availableRoomsClear();
    			}
    		}
    	}catch(Exception e) {e.printStackTrace();}
    }
    
    public void availableRoomsUpdate() {
    	
    	String type1 = (String)availableRooms_roomType.getSelectionModel().getSelectedItem();
    	String status1 = (String)availableRooms_status.getSelectionModel().getSelectedItem();
    	String price1 = availableRooms_price.getText();
    	String roomNum = availableRooms_roomNumber.getText();
    	
    	String sql = "UPDATE room SET type = '"+type1+"', status = '"+status1+"', price = '"+price1+"' WHERE roomNumber = '"+roomNum+"'";
    	
    	connect = database.connectDb();
    	
    	try {
    		
    		Alert alert;
    		
    		if(type1 == null || status1 == null || price1 == null || roomNum == null) {
    			alert = new Alert(AlertType.ERROR);
    			alert.setTitle("Error Message");
    			alert.setHeaderText(null);
    			alert.setContentText("Please select the data first");
    			alert.showAndWait();
    		}else {
    			
    			prepare = connect.prepareStatement(sql);
    			prepare.executeUpdate();
    			
    			alert = new Alert(AlertType.INFORMATION);
    			alert.setTitle("Information Message");
    			alert.setHeaderText(null);
    			alert.setContentText("Successfully Updated!");
    			alert.showAndWait();
    			
    			availableRoomsShowData();
    			
    			availableRoomsClear();
    		}
 
    	}catch(Exception e) {e.printStackTrace();}
    	
    }
    
    public void availableRoomsDelete() {
    	
    	String type1 = (String)availableRooms_roomType.getSelectionModel().getSelectedItem();
    	String status1 = (String)availableRooms_status.getSelectionModel().getSelectedItem();
    	String price1 = availableRooms_price.getText();
    	String roomNum = availableRooms_roomNumber.getText();
    	
    	String deleteData = "DELETE FROM room WHERE roomNumber = '"+roomNum+"'";
    	
    	connect = database.connectDb();
    	
    	try {
    		
    		Alert alert;
    		
    		if(type1 == null || status1 == null || price1 == null || roomNum == null) {
    			alert = new Alert(AlertType.ERROR);
    			alert.setTitle("Error Message");
    			alert.setHeaderText(null);
    			alert.setContentText("Please select the data first!");
    			alert.showAndWait();
    		}else {
    			
    			alert = new Alert(AlertType.CONFIRMATION);
    			alert.setTitle("Confirmation Message");
    			alert.setHeaderText(null);
    			alert.setContentText("Are you sure you want to delete Room #" + roomNum + "?");
    			
    			Optional<ButtonType> option = alert.showAndWait();
    			
    			if(option.get().equals(ButtonType.OK)) {
    				
    				statement = connect.createStatement();
    				statement.executeUpdate(deleteData);
    				
    				alert = new Alert(AlertType.INFORMATION);
        			alert.setTitle("Information Message");
        			alert.setHeaderText(null);
        			alert.setContentText("Successfully Deleted!");
        			alert.showAndWait();
    				
        			availableRoomsShowData();
        			
        			availableRoomsClear();
        			
    			}else {
    				return;
    			}
    			
    		}   		
    	}catch(Exception e) {e.printStackTrace();}
    	
    }
    
    public void availableRoomsClear() {
    	availableRooms_roomNumber.setText("");
    	availableRooms_roomType.getSelectionModel().clearSelection();
    	availableRooms_status.getSelectionModel().clearSelection();
    	availableRooms_price.setText("");
    }
    
    public void availableRoomsCheckIn() {
    	
    	try {
    		Parent root = FXMLLoader.load(getClass().getResource("/interfaces/checkIn.fxml"));
    		Stage stage = new Stage();
    		Scene scene = new Scene(root);
    		
    		stage.setMinHeight(473+35);
    		stage.setMinWidth(410+15);
    		
    		stage.initStyle(StageStyle.DECORATED);
    		stage.setScene(scene);
    		stage.show();
    	}catch(Exception e) {e.printStackTrace();}
    	
    }
    
    private String type[] = {"Single Room", "Double Room", "Triple Room", "Quad Room", "King Room"};
    
    public void availableRoomsRoomType() {
    	List<String> listData = new ArrayList<>();
    	
    	for(String data: type) {
    		listData.add(data);
    	}
    	
    	ObservableList list = FXCollections.observableArrayList(listData);
    	availableRooms_roomType.setItems(list);
    }
    
    private String status[] = {"Avaiblable", "Not avaiblable", "Occupied"};
    
    public void availableRoomsStatus() {
    	List<String> listData = new ArrayList<>();
    	
    	for(String data: status) {
    		listData.add(data);
    	}
    	
    	ObservableList list = FXCollections.observableArrayList(listData);
    	availableRooms_status.setItems(list);
    }
    
    public ObservableList<customerData> customerListData() {
    	
    	ObservableList<customerData> listData = FXCollections.observableArrayList();
    	
    	String sql = "SELECT * FROM customer";
    	
    	connect = database.connectDb();
    	
    	try {
    		
    		prepare = connect.prepareStatement(sql);
    		result = prepare.executeQuery();
    		
    		customerData custD;
    		
    		while(result.next()){
    			
    			custD = new customerData(result.getInt("customer_id"), result.getString("firstName"), result.getString("lastName")
    					, result.getString("phoneNumber"), result.getDouble("total"), result.getDate("checkIn"), result.getDate("checkOut"));
    			
    			listData.add(custD);
    			
    		}
    		
    	}catch(Exception e) {e.printStackTrace();}
    	
    	return listData;
    	
    }
    
    private ObservableList<customerData> listCustomerData;
    public void customersShowData() {
    	
    	listCustomerData = customerListData();
    	
    	customers_customerNumber.setCellValueFactory(new PropertyValueFactory<>("customerNum"));
    	customers_firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
    	customers_lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
    	customers_phoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
    	customers_totalPayment.setCellValueFactory(new PropertyValueFactory<>("total"));
    	customers_checkedIn.setCellValueFactory(new PropertyValueFactory<>("checkIn"));
    	customers_checkedOut.setCellValueFactory(new PropertyValueFactory<>("checkOut"));
    	
    	customers_tableView.setItems(listCustomerData);
    	
    }
    
    public void customerSearch() {
    	
    	FilteredList<customerData> filter = new FilteredList<>(listCustomerData, e -> true);
    	
    	customers_search.textProperty().addListener((Observable, oldValue, newValue) ->{
    		
    		filter.setPredicate(predicateCustomer ->{
    			
    			if(newValue == null && newValue.isEmpty()) {
    				return true;
    			}
    			
    			String searchKey = newValue.toLowerCase();
    			
    			if(predicateCustomer.getCustomerNum().toString().contains(searchKey)) {
    				return true;
    			}else if(predicateCustomer.getFirstName().toLowerCase().contains(searchKey)){
    				return true;
    			}else if(predicateCustomer.getLastName().toLowerCase().contains(searchKey)) {
    				return true;
    			}else if(predicateCustomer.getTotal().toString().contains(searchKey)) {
    				return true;
    			}else if(predicateCustomer.getPhoneNumber().toLowerCase().contains(searchKey)) {
    				return true;
    			}else if(predicateCustomer.getCheckIn().toString().contains(searchKey)) {
    				return true;
    			}else if(predicateCustomer.getCheckOut().toString().contains(searchKey)) {
    				return true;
    			}else {
    				return false;
    			}
    		});
    		
    	});
    	
    	SortedList<customerData> sortList = new SortedList<>(filter);
    	sortList.comparatorProperty().bind(customers_tableView.comparatorProperty());
    	customers_tableView.setItems(sortList);
    	
    }
    
    public void switchForm(ActionEvent event) {
    	
    	if(event.getSource() == dashboard_btn) {
    		
    		dashboard_form.setVisible(true);
    		availableRooms_form.setVisible(false);
    		customers_Form.setVisible(false);
    		
    		dashboard_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #5068c9, #bc59e4)");
    		availableRooms_btn.setStyle("-fx-background-color:transparent");
    		customers_btn.setStyle("-fx-background-color:transparent");
    		
    		dashboardDisplayBookToday();
    		dashboardDisplayIncomeToday();
    		dashboardTotalIncome();
    		dashboardChart();
    		displayUsername();
    		
    	}else if(event.getSource() == availableRooms_btn) {
    		
    		dashboard_form.setVisible(false);
    		availableRooms_form.setVisible(true);
    		customers_Form.setVisible(false);
    		
    		dashboard_btn.setStyle("-fx-background-color:transparent");
    		availableRooms_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #5068c9, #bc59e4)");
    		customers_btn.setStyle("-fx-background-color:transparent");
    		
    		availableRoomsSearch();
    		availableRoomsShowData();
    		
    	}else if(event.getSource() == customers_btn) {
    		
    		dashboard_form.setVisible(false);
    		availableRooms_form.setVisible(false);
    		customers_Form.setVisible(true);
    		
    		dashboard_btn.setStyle("-fx-background-color:transparent");
    		availableRooms_btn.setStyle("-fx-background-color:transparent");
    		customers_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #5068c9, #bc59e4)t");
    		
    		customerSearch();
    		customersShowData();
    	}
    	
    }
    
    public void defaultNavBtn() {
    	
    	dashboard_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #5068c9, #bc59e4)");
    	
    }
    
    public void displayUsername() {
    	
    	username.setText(getData.username);
    	
    }
    
    public void logout() {
    	try {
    		Alert alert = new Alert(AlertType.CONFIRMATION);
    		alert.setTitle("Confirmation Message");
    		alert.setHeaderText(null);
    		alert.setContentText("Are you sure want to logout?");
    		
    		Optional<ButtonType> option = alert.showAndWait();
    		
    		if (option.get().equals(ButtonType.OK)) {
    			Parent root = FXMLLoader.load(getClass().getResource("/interfaces/login1.fxml"));
	    		Stage stage = new Stage();
	    		Scene scene = new Scene(root);
	    		
	    		stage.initStyle(StageStyle.TRANSPARENT);
	    		stage.setScene(scene);
	    		stage.show();
	    		
	    		logout_btn.getScene().getWindow().hide();
	    		
    		}else {
    			return;
    		}
    		
    		
    	}catch(Exception e) {e.printStackTrace();}
    }
    
    public void close() {
    	System.exit(0);
    }
    
    public void minimize() {
    	Stage stage = (Stage)main_form.getScene().getWindow();
    	stage.setIconified(true);
    }
    
	
    @Override
    public void initialize(URL location, ResourceBundle resources){
    	dashboardDisplayBookToday();
    	dashboardDisplayIncomeToday();
    	dashboardTotalIncome();
    	dashboardChart();
    	defaultNavBtn();
    	displayUsername();
    	
    	availableRoomsRoomType();
    	availableRoomsStatus();
    	
    	availableRoomsShowData();
    	
    	customersShowData();
    }
}