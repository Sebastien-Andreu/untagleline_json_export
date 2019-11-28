package sample;

class Point extends java.awt.Point{
    double x, y;
    private boolean isInForm = false;
    private Point connectedPoint;
    private int nbConnection = 0;
    Point (double x, double y) {
        super((int)x, (int)y);
        this.x = super.getX();
        this.y = super.getY();
    }

    boolean isInForm (){
        return this.isInForm;
    }

    void setInForm() {
        this.isInForm = true;
    }

    void connectedPoint(Point p){
        this.connectedPoint = p;
        nbConnection += 1;
    }

    boolean connectionIsPossible(){
        return nbConnection < 2;
    }

    Point getConnectedPoint(){
        return this.connectedPoint;
    }

    void draw(){
        Main.gc.fillOval(this.x,this.y,10,10);
    }
}
