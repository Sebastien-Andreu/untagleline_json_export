package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONException;

import javax.swing.filechooser.FileSystemView;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main extends Application {
    static GraphicsContext gc;
    private Canvas canvas;

    private ArrayList<Point> listPoint = new ArrayList<>();
    private ArrayList<Form> listForm = new ArrayList<>();

    private Form form;
    private boolean isFirst = true, isPoint = true, isLine = false;

    private Point mainPoint = null, firstPoint = null, secondPoint = null;

    private Button btn, btn2, btn3;
    private Label level, difficulty;
    private TextField textLabel;
    private ComboBox<String> comboDifficulty;
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        primaryStage.setTitle("Untagleline Export Json");
        primaryStage.setScene(new Scene(root, 500, 800));

        StackPane holder = new StackPane();
        canvas = new Canvas(500, 700);
        gc = canvas.getGraphicsContext2D();
//        gc.setStroke(Paint.valueOf(String.valueOf(Color.DARKGRAY)));
        holder.getChildren().add(canvas);

        holder.setStyle("-fx-background-color: grey , white ;\n" +
                        "    -fx-background-insets:0 , 10 ;\n" +
                        "    -fx-padding: 1 ;");


        createButton();
        createInput();
        setEventClickCanvas();
        setEventReleaseClickCanvas();
        setEventDrawLine();

        HBox hBox = new HBox();
        hBox.setSpacing(20);
        hBox.getChildren().addAll(btn, btn2, btn3);
        hBox.setAlignment(Pos.CENTER);

        HBox hBoxInput = new HBox();
        hBoxInput.setSpacing(20);
        hBoxInput.getChildren().addAll(level, textLabel, difficulty, comboDifficulty);

        VBox vBox = new VBox();
        vBox.setSpacing(20);
        vBox.getChildren().addAll(holder, hBox, hBoxInput);

        root.setTop(vBox);
        primaryStage.show();
    }

    private void createInput(){
        level = new Label("Name of level");
        difficulty = new Label("Choose difficulty");
        comboDifficulty = new ComboBox<>();
        comboDifficulty.setItems(FXCollections.observableArrayList("easy", "medium", "hard"));
        comboDifficulty.getSelectionModel().select(0);
        textLabel = new TextField();
    }

    private void createButton(){
        btn = new Button("create point");
        btn.setOnAction(e -> {
            isPoint = true;
            isLine = false;
        });
        btn2 = new Button("create form");
        btn2.setOnAction(e -> {
            isPoint = false;
            isLine = true;
        });
        btn3 = new Button("export JSON");
        btn3.setOnAction(e -> {
            try {
                JSON json = new JSON(listForm, textLabel.getText(), comboDifficulty.getValue());
                json.createJson();
                gc.clearRect(0, 0, 500, 700);
                textLabel.clear();
                comboDifficulty.getSelectionModel().select(0);
            } catch (IOException | JSONException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void setEventDrawLine(){
        canvas.setOnMouseDragged(e -> {
            if (isLine){
                setFirstPoint(new Point(e.getX(), e.getY()));
                if (mainPoint == null && firstPoint.connexionIsPossible()){
                    form = new Form();
                    form.addPoint(firstPoint);
                    mainPoint = firstPoint;
                }
            }
        });
    }

    private void setEventClickCanvas(){
        canvas.setOnMousePressed(e -> {
            if (isPoint){
                Point point = new Point(e.getX(), e.getY());
                if (listPoint.size() != 0) {
                    if (drawIsPossible(point) && isNotBorder(point)){
                        point.draw();
                        listPoint.add(point);
                    }
                } else {
                    if (isNotBorder(point)){
                        firstPoint = point;
                        point.draw();
                        listPoint.add(point);
                    }
                }
            }
        });
    }

    private void setEventReleaseClickCanvas(){
        canvas.setOnMouseReleased(e -> {
            isFirst = true;
            for (Point p : listPoint) {
                if (p.distance(e.getX(),e.getY()) < 50 && firstPoint.distance(e.getX(), e.getY()) > 50){
                    secondPoint = p;
                    if (mainPoint != null){
                        if (secondPoint.distance(mainPoint) < 10){
                            updatePoint(firstPoint, secondPoint);
                            gc.strokeLine(firstPoint.x+5, firstPoint.y+5, secondPoint.x+5, secondPoint.y+5);
                            listForm.add(form);
                            mainPoint = null;
                        } else if ((firstPoint == mainPoint || firstPoint.isAlreadyConnected()) && secondPoint.connexionIsPossible()) {
                            updatePoint(firstPoint, secondPoint);
                            form.addPoint(secondPoint);
                            gc.strokeLine(firstPoint.x+5, firstPoint.y+5, secondPoint.x+5, secondPoint.y+5);
                        }
                    }
                }
            }
        });
    }

    private void updatePoint(Point p, Point p2){
        p.updateConnexion();
        p2.updateConnexion();
    }

    private void setFirstPoint(Point point){
        if (isFirst){
            for (Point p : listPoint)
                if (p.distance(point.x,point.y) < 50)
                    firstPoint = p;
            isFirst = false;
        }
    }

    private boolean drawIsPossible(Point point){
        for (Point p : listPoint) {
            if ((p.distance(point.getX(),point.getY()) < 50) && !p.equals(point))
                return false;
        }
        return true;
    }

    private boolean isNotBorder(Point point){
        return (point.x >= 40 && point.x < 450 && point.y > 35 && point.y < 655);
    }

    public static void main(String[] args) {
        try {
            Path path = Paths.get(FileSystemView.getFileSystemView().getDefaultDirectory().getPath()+ "\\untagleline\\json");
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        launch(args);
    }
}
