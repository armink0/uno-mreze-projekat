package client_server;

import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
		prikazTrenutne.setVisible(false);

		ListView<String> listView = new ListView<>();
		listView.setMaxSize(200, 200);
		listView.setVisible(false);

		Label stanje = new Label("");

		Button povuciKartu = new Button("Povuci kartu");
		povuciKartu.setDisable(true);
		povuciKartu.setVisible(false);

		Button odigrajKartu = new Button("Odigraj kartu");
		odigrajKartu.setDisable(true);
		odigrajKartu.setVisible(false);

		Button preskoci = new Button("Preskoci potez");
		preskoci.setDisable(true);
		preskoci.setVisible(false);

		Button refresuj = new Button("Refresuj tabelu");
		refresuj.setVisible(false);

		Button zapocniPartiju = new Button("Pocni partiju");

		try {
			client = new Client(new Socket(Server.LOCALHOST, Server.DEFAULT_PORT));
			System.out.println("Uspjesna konekcija");

			client.toServer("-2").thenApply(res -> {
				Platform.runLater(() -> {
					zapocniPartiju.setOnAction(e -> {
						if (Client.getBroj() > 2) {
							System.out.println("Vec ima dovoljan broj igraca");

							primaryStage.close();
						} else {
							root.getChildren().remove(zapocniPartiju);
							prikazTrenutne.setVisible(true);
							listView.setVisible(true);
							povuciKartu.setVisible(true);
							odigrajKartu.setVisible(true);
							preskoci.setVisible(true);
							refresuj.setVisible(true);
						}
					});
				});

				return res;
			}).thenRun(() -> {
				client.toServer("0").thenApply(res -> {
					Platform.runLater(() -> {
						prikazTrenutne.setText(res);
					});

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
			});

		} catch (Exception e) {
			// ignorisi
		}

		povuciKartu.setOnAction(e -> {
			povuciKartu.setDisable(true);
			refresuj.setDisable(true);

			String s = prikazTrenutne.getText();

			if (s.contains("na potezu")) {
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

		odigrajKartu.setOnAction(e -> {
			String getTrenutna = listView.getSelectionModel().getSelectedItem();

			if (getTrenutna != null) {
				String[] trenutnaKarta = getTrenutna.split(", ");
				odigrajKartu.setDisable(true);
				refresuj.setDisable(false);

				String[] izabranaKarta = prikazTrenutne.getText().split(", ");

				boolean dopustiTrenutna = trenutnaKarta[0].equals("+2") || trenutnaKarta[0].equals("+4");
				boolean dopustiIzabrana = izabranaKarta[0].equals("+2") || izabranaKarta[0].equals("+4");

				boolean dopusti = (dopustiTrenutna || dopustiIzabrana || trenutnaKarta[0].equals(izabranaKarta[0])
						|| trenutnaKarta[1].equals(izabranaKarta[1]));

				if (trenutnaKarta != null && prikazTrenutne.getText().contains("na potezu") && dopusti) {
					Client.trenutnaKarta = trenutnaKarta[0] + ", " + trenutnaKarta[1];
					povuciKartu.setDisable(true);
					preskoci.setDisable(true);

					client.toServer("3").thenApply(res -> {
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

					int n = 0;

					String getTrenutna = prikazTrenutne.getText();

					if (getTrenutna != null) {
						String[] trenutnaKarta = getTrenutna.split(", ");

						if (trenutnaKarta[0].equals("+2")) {
							n = 2;
						} else if (trenutnaKarta[0].equals("+4")) {
							n = 4;
						}

						if (prikazTrenutne.getText().contains("na potezu")) {
							for (int i = 0; i < n; i++) {
								EventHandler<ActionEvent> onActionHandler = povuciKartu.getOnAction();

								if (onActionHandler != null) {
									onActionHandler.handle(new ActionEvent());
								}
							}
						}
					}
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

						String trenutnaKarta = prikazTrenutne.getText();

						if ((trenutnaKarta.contains("+2") || trenutnaKarta.equals("+4"))
								&& trenutnaKarta.contains("na potezu")) {
							preskoci.setDisable(true);
						}
					});

					return res;
				});
			});
		});

		root.getChildren().addAll(listView, zapocniPartiju, prikazTrenutne, stanje, povuciKartu, preskoci, odigrajKartu,
				refresuj);
		Scene scene = new Scene(root, 400, 400);

		primaryStage.setScene(scene);
		primaryStage.setTitle("Korisnik");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
