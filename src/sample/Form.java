package sample;

import java.awt.*;
import java.util.ArrayList;

public class Form {
    public ArrayList<Point> list = new ArrayList<>();

    @Override
    public String toString() {
        return "Form{" +
                "list=" + list +
                '}';
    }

    void addPoint(Point p){
        list.add(p);
    }

    Point getMainPoint(){
        return list.get(0);
    }
}
