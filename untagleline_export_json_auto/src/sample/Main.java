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
import java.util.concurrent.ThreadLocalRandom;

public class Main extends Application {
    static GraphicsContext gc;
    private Canvas canvas;

    private ArrayList<Point> listOfAllPoint = new ArrayList<>();
    private ArrayList<Form> listForm = new ArrayList<>();
    private ArrayList<Point> listPoint = new ArrayList<>();

    private Button btn3;
    private Label level, difficulty;
    private TextField textLabel;
    private ComboBox<String> comboDifficulty;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        primaryStage.setTitle("Untagleline auto Export Json");
        primaryStage.setScene(new Scene(root, 500, 800));

        StackPane holder = new StackPane();
        canvas = new Canvas(500, 700);
        gc = canvas.getGraphicsContext2D();
        holder.getChildren().add(canvas);

        holder.setStyle("-fx-background-color: grey , white ;\n" +
                        "    -fx-background-insets:0 , 10 ;\n" +
                        "    -fx-padding: 1 ;");


        createButton();
        createInput();
        generateGame();


        HBox hBox = new HBox();
        hBox.setSpacing(20);
        hBox.getChildren().addAll(btn3);
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

    private void generateGame(){
        generatePoint();
        getNoneUsePoint();
        int aleaForm = ThreadLocalRandom.current().nextInt(1, 5);

        int nbRandom = listPoint.size() - (/*nbForm*/ aleaForm * 4);
        int rand;

        for (int i = 0; i < /*nbForm*/ aleaForm; i++){
            getNoneUsePoint();
            if (nbRandom > 0) {
                rand = ThreadLocalRandom.current().nextInt(0, nbRandom);
            } else {
                rand = 0;
            }
            if (i == /*nbForm -1 */ aleaForm-1){
                rand = nbRandom;
            }
            nbRandom -= rand;
            listForm.add(new Form());
            listForm.get(i).addListOfPoint(listPoint, rand);

            listForm.get(i).generateForm();
        }
//        System.out.println(listForm);
        boolean regenerate = false;
        for (Form f : listForm){
            for (Point p : f.listOfPoint){
                System.out.println(p + "      " + p.getConnectedPoint());
                if (!isUntagle(p, p.getConnectedPoint())){
                    regenerate = true;
                    break;
                }
            }
            System.out.println("-------------------------------");
        }
        if (regenerate){
            System.out.println("regenerate game !");
            gc.clearRect(0, 0, 500, 700);
            listOfAllPoint.clear();
            listPoint.clear();
            listForm.clear();
            generateGame();
        }
    }

    private boolean isUntagle(Point p1, Point p2){
        for (Form f : listForm) {
            for (Point p : f.listOfPoint) {
                if (intersectionSegmentSegment(p1, p2, p, p.getConnectedPoint())){
                    System.out.println("isUntagle false--------------------------");
                    return false;
                }
            }
        }
//        System.out.println("isUntagle true");
        return true;
    }

    private boolean intersectionSegmentSegment(Point A, Point B, Point C, Point D) {

        double c1 = (A.y-C.y)*(D.x-C.x)-(A.x-C.x)*(D.y-C.y);
        double c2 = (B.x-A.x)*(D.y-C.y)-(B.y-A.y)*(D.x-C.x);
        double c3 = (A.y-C.y)*(B.x-A.x)-(A.x-C.x)*(B.y-A.y);

        double r = c1/c2;
        double s = c3/c2;

        return (!(r <= 0) && !(r >= 1)) && (!(s <= 0) && !(s >= 1)) && c2 != 0;
    }

    private void getNoneUsePoint() {
        listPoint.clear();
        for (Point p : listOfAllPoint){
            if (!p.isInForm())
                listPoint.add(p);
        }
    }

    private void createInput(){
        level = new Label("Name of level");
        difficulty = new Label("Choose difficulty");
        comboDifficulty = new ComboBox<>();
        comboDifficulty.setItems(FXCollections.observableArrayList("easy", "medium", "hard"));
        comboDifficulty.getSelectionModel().select(0);
        textLabel = new TextField();
    }

    private void generatePoint(){
        int nbPointMax = 20;
        while (listOfAllPoint.size() < nbPointMax){
            double x = ThreadLocalRandom.current().nextInt(40, 450);
            double y = ThreadLocalRandom.current().nextInt(35, 655);
            Point p = new Point(x,y);
            if (drawIsPossible(p)){
                listOfAllPoint.add(p);
                p.draw();
            }
        }
        System.out.println(listOfAllPoint.size());
//
//        if (listOfAllPoint.size() < nbPointMax){
//            generatePoint();
//        }
    }

    private void createButton(){
        btn3 = new Button("reload");
        btn3.setOnAction(e -> {
            gc.clearRect(0, 0, 500, 700);
            listOfAllPoint.clear();
            listPoint.clear();
            listForm.clear();
            generateGame();
        });
    }

    private boolean drawIsPossible(Point point){
        for (Point p : listOfAllPoint) {
            if ((p.distance(point.getX(),point.getY()) < 75) && !p.equals(point))
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
