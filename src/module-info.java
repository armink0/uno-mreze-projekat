module UnoProjekat {
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.base;
	
	opens client_server to javafx.graphics, javafx.fxml;
}
