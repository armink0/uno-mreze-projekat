package client_server;

import java.net.Socket;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ClientController extends Application {
	private Client client;

	@Override
	public void start(Stage primaryStage) {
		try {
			client = new Client(new Socket(Server.LOCALHOST, Server.DEFAULT_PORT));
			System.out.println("Uspjesna konekcija");
		} catch (Exception e) {
			e.printStackTrace();
		}

		VBox root = new VBox(10);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(25));

		TextField ta = new TextField();

		Button b = new Button("dodaj text");

		b.setOnAction(e -> {
			if (!ta.getText().isEmpty()) {
				synchronized (this) {
					client.posaljiPorukuServeru(ta.getText());
					client.vratiPorukuOdServera();
				}
			}

			ta.clear();
		});


		root.getChildren().addAll(ta, b);
		Scene scene = new Scene(root, 400, 400);

		primaryStage.setScene(scene);
		primaryStage.setTitle("Klijent");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
