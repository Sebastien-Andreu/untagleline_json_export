package sample;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.filechooser.FileSystemView;

class JSON {
    private ArrayList<Form> listForm;
    private String title, difficulty;
    private String path = Paths.get(".").toAbsolutePath().normalize().toString();
    JSON (ArrayList<Form> listForm, String title, String difficulty){
        this.listForm = listForm;
        this.title = title;
        this.difficulty = difficulty;
    }

    void createJson() throws JSONException, IOException {
        JSONObject title = new JSONObject();
        JSONArray formes = new JSONArray();
        for (Form f : this.listForm){
            JSONObject object = new JSONObject();
            JSONArray points = new JSONArray();
            for (Point p : f.list){
                JSONObject point = new JSONObject();
                point.put("x", p.x);
                point.put("y", p.y);
                points.put(point);
            }
            object.put("points", points);
            formes.put(object);
        }
        title.put("name", this.title);
        title.put("difficulty", this.difficulty);
        title.put("numberForm", this.listForm.size());
        title.put("formes", formes);


        File file = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath()+ "\\untagleline\\json\\" + this.title + ".json");
        Files.write(Paths.get(file.getPath()), title.toString().getBytes());
    }
}
