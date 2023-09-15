package client_server;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Pocetak extends Application {
	private int brojKorisnika = 0;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		Label label = new Label("Uno");

		Label dodanKorisnik = new Label("");

		Button button = new Button("Pocni");

		button.setOnAction(e -> {
			this.brojKorisnika++;

			if (brojKorisnika <= 2) {
				ClientController noviKorisnik = new ClientController();
				Stage stage = new Stage();
				noviKorisnik.start(stage);

				dodanKorisnik.setText("Dodan korisnik!");
				dodanKorisnik.setTextFill(Color.GREEN);
			} else {
				dodanKorisnik.setText("Vec imaju 2 korisnika!");
				dodanKorisnik.setTextFill(Color.RED);
			}
		});

		VBox vbox = new VBox(5);
		vbox.setAlignment(Pos.CENTER);

		vbox.getChildren().addAll(label, button, dodanKorisnik);

		Scene scene = new Scene(vbox, 200, 200);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Uno");
		primaryStage.show();
	}
}
