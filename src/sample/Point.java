package sample;

class Point extends java.awt.Point {
    public double x, y;
    int isConnected = 0;
    Point (double x, double y) {
        super((int)x, (int)y);
        this.x = super.getX();
        this.y = super.getY();
    }

    void updateConnexion(){
        isConnected += 1;
    }

    boolean connexionIsPossible(){
        return isConnected < 2;
    }

    boolean isAlreadyConnected(){
        return isConnected == 1;
    }


    void draw(){
        Main.gc.fillOval(this.x,this.y,10,10);
    }
}
