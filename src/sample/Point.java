package sample;

import java.util.Objects;

public class Point extends Object {
    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Point point) {
       if(point.x-this.x>=-6 && point.x-this.x<=6 &&
               point.y-this.y>=-6 && point.y-this.y <=6)
           return true;

       return false;
    }


}
