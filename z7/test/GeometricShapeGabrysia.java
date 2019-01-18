import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
class GeometricShape implements GeometricShapeInterface {
 
    public List<Point> allPoints = new ArrayList<>();   // aktualne punkty w figurze
    public List<List<Point>> allPosition = new ArrayList<>();   // listy przechowujace wszystkie punkty
    public List<List<Point>> history = new ArrayList<>();
    GeometricShape() {
    }
 
    @Override
    public void add(Point point) {  // dodawanie punktu na koniec kolekcji
        allPoints = new ArrayList<>(allPoints);
        allPoints.add(point);
        allPosition.add(allPoints);
    }
 
    @Override
    public boolean remove(Point point) {    // usuwanie punktu z kolekcji (jesli jest i pierwszy)
        if (allPoints.contains(point)) {    // jesli punkt znajduje sie w kolekcji
            allPoints = new ArrayList<>(allPoints);
            allPoints.remove(point);
            allPosition.add(allPoints);
            return true;
        } else return false;
    }
 
    @Override
    public boolean addBefore(Point point, Point beforePoint) {      // dodaje punkt przed punktem before
        if (allPoints.contains(beforePoint)) {
            allPoints = new ArrayList<>(allPoints);
            int index = allPoints.indexOf(beforePoint);     // indeks punktu before
            allPoints.add(index, point);    // wstawiamy punkt na miejsce before, reszta punktow przesuwa sie
            allPosition.add(allPoints);
            return true;
        } else return false;
    }
 
    @Override
    public boolean addAfter(Point point, Point afterPoint) {    // dodaje punkt za punktem after
        if (allPoints.contains(afterPoint)) {   // jezeli taki punkt istnieje
            allPoints = new ArrayList<>(allPoints);
            int index = allPoints.lastIndexOf(afterPoint) + 1;
            allPoints.add(index, point);
            allPosition.add(allPoints);
            return true;
        } else return false;
    }
 
    @Override
    public Point removeBefore(Point beforePoint) {  // usuwa punkt przed punktem before
        if (allPoints.contains(beforePoint)) {
            allPoints = new ArrayList<>(allPoints);
            int index = allPoints.indexOf(beforePoint);
            if (index <= 0) return null;
            else {
                Point remember = allPoints.get(index - 1);
                allPoints.remove(index - 1);
                allPosition.add(allPoints);
                return remember;
            }
        } else return null;
    }
 
    @Override
    public Point removeAfter(Point afterPoint) {    // usuwa punkt za punktem after
        if (allPoints.contains(afterPoint)) {
            allPoints = new ArrayList<>(allPoints);
            int index = allPoints.lastIndexOf(afterPoint);
            if (index >= allPoints.size() - 1) return null;
            else {
                Point remember = allPoints.get(index + 1);
                allPoints.remove(index + 1);
                allPosition.add(allPoints);
                return remember;
            }
        } else return null;
    }
 
    @Override
    public boolean undo() {
        if (allPosition.isEmpty()) {
            history.clear();
            return false;
        } else {
	    history = new ArrayList<>(allPosition);
            int index = allPosition.size() - 1;
            allPosition.remove(index);
            return true;
        }
    }
 
    @Override
    public boolean redo() {
        if (history.isEmpty()) return false;
        else {
	    allPosition = new ArrayList<>(history);
            return true;
        }
    }
 
    @Override
    public List<Point> get() {
        int index = allPosition.size();
	 return allPosition.get(index - 1);
    }
 
    @Override
    public List<Point> getUniq() {
        int index = allPosition.size();
        List<Point> uniq = allPosition.get(index - 1);
 
        Point r = uniq.get(0);
        for (int i = 1; i < uniq.size(); i++) {
            if (r.equals(uniq.get(i))) {
                uniq.remove(i);
                i--;
            } else {
                r = uniq.get(i);
            }
        }
        return uniq;
    }
 
    @Override
    public Map<Point, Integer> getPointsAsMap() {
        Map<Point, Integer> numberOfPoints = new HashMap<>();
 
        int index = allPosition.size();
        List<Point> points = allPosition.get(index - 1);
 
        for (int i = 0; i < points.size(); i++) {
            if (numberOfPoints.containsKey(points.get(i))) {
                int value = numberOfPoints.get(points.get(i));
                numberOfPoints.replace(points.get(i), value + 1);
            } else {
                numberOfPoints.put(points.get(i), 1);
            }
        }
        return numberOfPoints;
    }
 
    public static void main(String[] args) {
        GeometricShape g = new GeometricShape();
        Point p = new Point();
        Point p1 = new Point();
        Point p2 = new Point();
        Point p3 = new Point();
        g.add(p);
        g.add(p1);
        g.add(p2);
        g.add(p3);
        System.out.println(g.removeBefore(p));
    }
}
 