import com.sun.deploy.util.StringUtils;
import javafx.application.Application;
import javafx.event.EventHandler;
import java.io.IOException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.swing.*;

public class Main extends Application{
    private Scene scene;
    private BorderPane border;
    private Stage stage;
    private TextField folder;
    private String selectedPath;
    private TextField ip;
    private TextField port;
    private TextField fileName;
    private TextField result;
    private GridPane gPane;


    /**
     * Initialise graphical items
     * @param primaryStage the first stage of the application
     * @throws Exception Java fx exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setResizable(false);
        gPane = new GridPane();
        gPane.setAlignment(Pos.CENTER_LEFT);
        gPane.setPadding(new Insets(15,15,10,10));
        gPane.setHgap(10);
        gPane.setVgap(10);
        border = new BorderPane();

        Label labelIP = new Label("Serveur :");
        ip = new TextField ();
        port = new TextField();
        port.setMaxWidth(50);

        gPane.add(labelIP, 0, 0);
        gPane.add(ip, 1, 0);
        gPane.add(port, 2, 0);

        Label labelParcourir = new Label("Dossier dest :");
        folder = new TextField ();
        Button btnParcourir = new Button("Parcourir");
        btnParcourir.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("../"));
                chooser.setDialogTitle("Dossier de destination");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    selectedPath = chooser.getCurrentDirectory().getPath();
                    folder.setText(selectedPath);
                } else {
                    folder.setText("");
                }
            }
        });
        Button btnGet = new Button("Télécharger");
        btnGet.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String msg = "Remplir le formulaire !";
                if(selectedPath != null && !StringUtils.trimWhitespace(ip.getText()).equals("")
                        && !StringUtils.trimWhitespace(port.getText()).equals("")
                        && !StringUtils.trimWhitespace(fileName.getText()).equals("")) {
                    try {
                        msg = Client.recevoirFichier(ip.getText(), port.getText(), selectedPath, fileName.getText());
                    } catch (IOException ex) {
                        msg = "Aucune réponse du serveur ...";
                    }
                }
                result.setText(msg);
            }
        });

        gPane.add(labelParcourir, 0, 1);
        gPane.add(folder, 1, 1);
        gPane.add(btnParcourir, 2, 1);

        Label labelFile = new Label("Nom fichier :");
        fileName = new TextField();

        gPane.add(labelFile, 0, 2);
        gPane.add(fileName, 1, 2);

        gPane.add(btnGet, 0, 3);

        result = new TextField();
        result.setPadding(new Insets(10,10,10,10));

        border.setCenter(gPane);
        border.setTop(result);
        scene = new Scene(border);
        primaryStage.setTitle("ARSIR RECEPTION");
        primaryStage.setScene(scene);
        primaryStage.show();
        stage = primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
