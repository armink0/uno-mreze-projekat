package client_server;

import java.net.Socket;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
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

		Button vratiKartu = new Button("povuci kartu");

		Button odigrajKartu = new Button("odigraj kartu");
		odigrajKartu.setDisable(true);

		Button preskoci = new Button("preskoci potez");

		Button refresuj = new Button("refresuj tabelu");

		try {
			client = new Client(new Socket(Server.LOCALHOST, Server.DEFAULT_PORT));
			System.out.println("Uspjesna konekcija");

			client.toServer("0").thenApply(res -> {
				Platform.runLater(() -> prikazTrenutne.setText(res + ", " + Client.getBroj()));
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

		vratiKartu.setOnAction(e -> {
			vratiKartu.setDisable(true);
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
		});

		odigrajKartu.setOnAction(e -> {
			String s = listView.getSelectionModel().getSelectedItem();

			if (s != null) {
				Client.trenutnaKarta = s;
				odigrajKartu.setDisable(true);
				vratiKartu.setDisable(true);

				client.toServer("3").thenApply(res -> {
					Platform.runLater(() -> {
						prikazTrenutne.setText(res);

						Client.updateRuka(res);

						listView.getItems().clear();

						if (Client.getGotovo().equals("gotovo")) {
							Client.setGotovo("pobjeda");
							stanje.setText("pobjeda");
							stanje.setTextFill(Color.GREEN);
							vratiKartu.setDisable(true);
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
								vratiKartu.setDisable(true);
								odigrajKartu.setDisable(true);
								refresuj.setDisable(true);
							}
						} else {
							odigrajKartu.setDisable(false);
							vratiKartu.setDisable(false);
						}
					});

					return res;
				});
			});
		});

		preskoci.setOnAction(e -> {
			vratiKartu.setDisable(true);
			odigrajKartu.setDisable(true);
			client.toServer("5").thenApply(res -> {
				Platform.runLater(() -> {
					prikazTrenutne.setText(res + Client.naPotezu);
				});

				return res;
			});
		});

		root.getChildren().addAll(listView, prikazTrenutne, stanje, vratiKartu, preskoci, odigrajKartu, refresuj);
		Scene scene = new Scene(root, 400, 400);

		primaryStage.setScene(scene);
		primaryStage.setTitle("Klijent");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
