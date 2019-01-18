import java.util.*;

class HistoryOfPoints {
    private List<Point> pointListHistory;

    HistoryOfPoints(List<Point> pointListHistory) {
        this.pointListHistory = new ArrayList<>(pointListHistory);
    }

    List<Point> getPointListHistory() {
        return pointListHistory;
    }
}

public class GeometricShape implements GeometricShapeInterface {
    private List<Point> pointList = new ArrayList<>();
    private List<HistoryOfPoints> historyOfPoints = new ArrayList<>();
    private int undoCommands = 0;

    GeometricShape() {}

    private int getIndexOfPoint(Point point, List<Point> list) {
        int h = System.identityHashCode(point);
        int indexToReturn = 0;

        for(int i=0; i<list.size(); i++) {
            if (System.identityHashCode(list.get(i)) == h) {
                indexToReturn = i;
                break;
            }
        }
        return indexToReturn;
    }

    private boolean isPointInList(Point point, List<Point> list) {
        int h = System.identityHashCode(point);
        boolean valueToReturn = false;

        for (Point k : list) {
            if(System.identityHashCode(k) == h)
                valueToReturn = true;
        }
        return valueToReturn;
    }

    @Override
    public void add(Point point) {
        if(pointList.isEmpty()) historyOfPoints.add(new HistoryOfPoints(pointList));
        pointList.add(point);
        historyOfPoints.add(new HistoryOfPoints(pointList));
        undoCommands = 0;
    }

    @Override
    public boolean remove(Point point) {
        if(isPointInList(point, pointList)) {
            int index = getIndexOfPoint(point, pointList);
            pointList.remove(index);
            historyOfPoints.add(new HistoryOfPoints(pointList));
            undoCommands = 0;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addBefore(Point point, Point beforePoint) {
        if(isPointInList(beforePoint, pointList)) {
            int index = getIndexOfPoint(beforePoint, pointList);
            pointList.add(index, point);
            historyOfPoints.add(new HistoryOfPoints(pointList));
            undoCommands = 0;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addAfter(Point point, Point afterPoint) {
        if(pointList.contains(afterPoint)) {
            int indexPointToAdd = pointList.lastIndexOf(afterPoint)+1;
            pointList.add(indexPointToAdd, point);
            historyOfPoints.add(new HistoryOfPoints(pointList));
            undoCommands = 0;
            return true;
        } else {
            return false;
        }
    }

    private Point pointToRemove(int indexOfPointToRemove) {
        Point removePoint = pointList.get(indexOfPointToRemove);
        pointList.remove(indexOfPointToRemove);
        historyOfPoints.add(new HistoryOfPoints(pointList));
        undoCommands = 0;
        return removePoint;
    }

    @Override
    public Point removeBefore(Point beforePoint) {
        if(pointList.contains(beforePoint)) {
            int indexOfPointToRemove = pointList.indexOf(beforePoint)-1;
            if(indexOfPointToRemove >= 0) {
                return pointToRemove(indexOfPointToRemove);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public Point removeAfter(Point afterPoint) {
        if(pointList.contains(afterPoint)) {
            int indexOfPointToRemove = pointList.lastIndexOf(afterPoint)+1;
            if(pointList.size() > indexOfPointToRemove) {
                return pointToRemove(indexOfPointToRemove);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean undo() {
        if(historyOfPoints.size()-undoCommands-2 >= 0) {
            pointList = new ArrayList<>(historyOfPoints.get(historyOfPoints.size() - undoCommands - 2).getPointListHistory());
            undoCommands++;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean redo() {
        if(undoCommands > 0) {
            pointList = new ArrayList<>(historyOfPoints.get(historyOfPoints.size() - undoCommands + 2 - 2).getPointListHistory());
            undoCommands--;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Point> get() {
        return pointList;
    }

    @Override
    public List<Point> getUniq() {
        List<Point> pointListToReturn = new ArrayList<>();
        pointListToReturn.add(pointList.get(0));
        for(int i=1; i<pointList.size(); i++) {
            if(!pointList.get(i).equals(pointList.get(i-1))){
                pointListToReturn.add(pointList.get(i));
            }
        }
        return pointListToReturn;
    }

    @Override
    public Map<Point, Integer> getPointsAsMap() {

        Map<Point,Integer> counterMap = new HashMap<>();
        for (Point aPointList : pointList) {
            if (counterMap.containsKey(aPointList)) {
                counterMap.put(aPointList, counterMap.get(aPointList) + 1);
            } else {
                counterMap.put(aPointList, 1);
            }
        }
        return counterMap;
    }
}
