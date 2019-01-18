import java.util.List;

public class Main {
    static void printPoint(List<Point> pointToPrint) {
        for(int i=0; i<pointToPrint.size(); i++) {
            System.out.print(pointToPrint.get(i) + " ");
        }
        System.out.println();
    }
    public static void main(String args[]){

        GeometricShape geometricShape = new GeometricShape();
        Point p1 = new Point();
        p1.setPosition(1, 2);

        Point p2 = new Point();
        p2.setPosition(1, 1);

        Point p3 = new Point();
        p3.setPosition(2, 3);

        Point p4 = new Point();
        p4.setPosition(0, 2);

        Point p5 = new Point();
        p5.setPosition(1, 5);

        geometricShape.add(p1);
        geometricShape.add(p2);
        geometricShape.add(p3);
        geometricShape.add(p4);

        printPoint(geometricShape.get());

        geometricShape.addBefore(p5, p4);

        printPoint(geometricShape.get());

        geometricShape.undo();
        geometricShape.undo();

        printPoint(geometricShape.get());

        geometricShape.redo();
        geometricShape.redo();

        printPoint(geometricShape.get());
        Point removeBefore = geometricShape.removeBefore(p1);
        printPoint(geometricShape.get());


    }
}
