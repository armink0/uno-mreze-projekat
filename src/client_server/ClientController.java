package client_server;

import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ClientController extends Application {
	private Client client;

	@Override
	public void start(Stage primaryStage) {
		VBox root = new VBox(10);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(25));

		Label prikazTrenutne = new Label("");

		ListView<String> listView = new ListView<>();
		listView.setMaxSize(200, 200);

		Label stanje = new Label("");

		Button povuciKartu = new Button("povuci kartu");
		povuciKartu.setDisable(true);

		Button odigrajKartu = new Button("odigraj kartu");
		odigrajKartu.setDisable(true);

		Button preskoci = new Button("preskoci potez");
		preskoci.setDisable(true);

		Button refresuj = new Button("refresuj tabelu");

		try {
			client = new Client(new Socket(Server.LOCALHOST, Server.DEFAULT_PORT));
			System.out.println("Uspjesna konekcija");

			client.toServer("0").thenApply(res -> {
				Platform.runLater(() -> prikazTrenutne.setText(res));
				return res;
			}).thenRun(() -> {
				for (int i = 0; i < Server.POCETNO; i++) {
					client.toServer("1").thenApply(res -> {
						Platform.runLater(() -> {
							listView.getItems().clear();

							Client.addRuka(res);

							for (String karta : Client.getRuka()) {
								listView.getItems().add(karta);
							}
						});

						return res;
					});
				}
			});

		} catch (Exception e) {
			// ignorisi
		}

		povuciKartu.setOnAction(e -> {
			povuciKartu.setDisable(true);
			refresuj.setDisable(true);

			String s = prikazTrenutne.getText();

			if (s.contains("na potezu")) {
				System.out.println("aaaa");
				client.toServer("2").thenApply(res -> {
					Platform.runLater(() -> {
						listView.getItems().clear();

						Client.addRuka(res);

						for (String karta : Client.getRuka()) {
							listView.getItems().add(karta);
						}
					});

					return res;
				});
			}
		});

		odigrajKartu.setOnAction(e ->

		{
			String s = listView.getSelectionModel().getSelectedItem();

			if (s != null) {
				Client.trenutnaKarta = s;
				odigrajKartu.setDisable(true);
				povuciKartu.setDisable(true);
				preskoci.setDisable(true);
				refresuj.setDisable(false);

				client.toServer("3").thenApply(res -> {
					System.out.println(Client.naPotezu);
					Platform.runLater(() -> {
						prikazTrenutne.setText(res);

						Client.updateRuka(res);

						listView.getItems().clear();

						if (Client.getGotovo().equals("gotovo")) {
							Client.setGotovo("pobjeda");
							stanje.setText("pobjeda");
							stanje.setTextFill(Color.GREEN);
							povuciKartu.setDisable(true);
							odigrajKartu.setDisable(true);
							refresuj.setDisable(true);
						}

						for (String karta : Client.getRuka()) {
							listView.getItems().add(karta);
						}

					});

					return res;
				});
			}
		});

		preskoci.setOnAction(e -> {
			povuciKartu.setDisable(true);
			odigrajKartu.setDisable(true);
			preskoci.setDisable(true);
			refresuj.setDisable(false);

			client.toServer("5").thenApply(res -> {
				Platform.runLater(() -> {
					prikazTrenutne.setText(res);
				});

				return res;
			});
		});

		refresuj.setOnAction(e -> {
			client.toServer("4").thenApply(res -> {
				Platform.runLater(() -> {
					prikazTrenutne.setText(res + Client.naPotezu);
				});

				return res;
			}).thenRun(() -> {
				client.toServer("-1").thenApply(res -> {
					Platform.runLater(() -> {
						if (Client.getGotovo().equals("gotovo")) {
							if (Client.getRuka().size() != 0) {
								stanje.setText("poraz");
								stanje.setTextFill(Color.RED);
								povuciKartu.setDisable(true);
								odigrajKartu.setDisable(true);
								refresuj.setDisable(true);
							}
						} else {
							odigrajKartu.setDisable(false);
							povuciKartu.setDisable(false);
							preskoci.setDisable(false);
						}
					});

					return res;
				});
			});
		});

		root.getChildren().addAll(listView, prikazTrenutne, stanje, povuciKartu, preskoci, odigrajKartu, refresuj);
		Scene scene = new Scene(root, 400, 400);

		primaryStage.setScene(scene);
		primaryStage.setTitle("Klijent");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
