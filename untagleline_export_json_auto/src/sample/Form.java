package sample;


import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Form {
    ArrayList<Point> list = new ArrayList<>(), listOfPoint = new ArrayList<>();
    private ArrayList<Point> l1= new ArrayList<>(), l2=new ArrayList<>(), l3= new ArrayList<>(), l4 = new ArrayList<>();
    private Point mainPoint, centerPoint;
    private int nbPoint;
    private boolean isFirstPoint = true;


    @Override
    public String toString() {
        return "Form{ list=" + list + '}';
    }

    void generateForm(){
        setCenterPoint();

        for (int i = 0; i < nbPoint-1; i++){
            foundNextPoint();
            list.clear();
            list.addAll(l1);
            list.addAll(l2);
            list.addAll(l3);
            list.addAll(l4);

            if (isFirstPoint){
                mainPoint = list.get(0);
                listOfPoint.add(list.get(0));
                listOfPoint.get(listOfPoint.size()-1).connectedPoint(list.get(1));
                isFirstPoint = false;
            }
            if (list.get(0).connectionIsPossible() && list.get(1).connectionIsPossible()){
                connectPoint(list.get(0), list.get(1));
                listOfPoint.add(list.get(0));
                listOfPoint.get(listOfPoint.size()-1).connectedPoint(list.get(1));
                list.remove(0);
            }

        }
        listOfPoint.get(0).connectedPoint(list.get(0));
        connectPoint(mainPoint, list.get(0));
    }

    private void setCenterPoint(){
        double moyX = 0;
        double moyY = 0;
        for (Point p : list){
            moyX += p.x;
            moyY += p.y;
        }

        this.centerPoint = new Point(moyX / nbPoint, moyY / nbPoint);
    }

    private void foundNextPoint(){
        l1.clear();
        l2.clear();
        l3.clear();
        l4.clear();
        for (Point p : this.list) {
            if (p.x >= this.centerPoint.x && p.y >= this.centerPoint.y)
                l1.add(p);
            else if (p.x < this.centerPoint.x && p.y >= this.centerPoint.y)
                l2.add(p);
            else if (p.x < this.centerPoint.x && p.y < this.centerPoint.y)
                l3.add(p);
            else
                l4.add(p);
        }
        sortListOfPoint(l1);
        sortListOfPoint(l2);
        sortListOfPoint(l3);
        sortListOfPoint(l4);
    }

    private void sortListOfPoint(ArrayList<Point> l){
        l.sort((a, b) -> Double.compare(0, (((b.x - a.x) * (centerPoint.y - a.y)) - ((centerPoint.x - a.x) * (b.y - a.y))) / 2));
    }

    private void connectPoint(Point firstPoint, Point secondPoint){
        Main.gc.strokeLine(firstPoint.x+5, firstPoint.y+5, secondPoint.x+5, secondPoint.y+5);
    }

    void addListOfPoint(ArrayList<Point> listPoint, int rand){
        listPoint.sort((a,b) -> (int) (a.y- b.y)); // trie sur l'axe des Y
        this.mainPoint = listPoint.get(0); // on prend le plus haut
//        listOfPoint.add(this.mainPoint);
        Main.gc.fillOval(mainPoint.x,mainPoint.y,20,20);

        listPoint.sort((a,b) -> (int) (this.mainPoint.distance(a) - this.mainPoint.distance(b))); // on trie par distance au point le plus haut
        this.nbPoint = 4 + rand; // on définit le nombre de point dans la forme
        for (int i = 0; i < this.nbPoint; i++){
            this.list.add(listPoint.get(i));
            listPoint.get(i).setInForm();
        }
    }
}
