module UnoProjekat {
	requires javafx.controls;
	requires javafx.graphics;
	
	opens client_server to javafx.graphics, javafx.fxml;
}
