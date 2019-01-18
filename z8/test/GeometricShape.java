import java.util.*;
import java.util.stream.IntStream;

class GeometricShape implements GeometricShapeInterface {
    private List<Point> pointList = new ArrayList<>();
    private List<Point> pointListAll = new ArrayList<>();


    private int getDimension(Point point) {
        int dimensions = 0;
        try {
            for(int i=0; i<Integer.MAX_VALUE; i++) {
                point.getPosition(i);
                dimensions++;
            }
        } catch (IndexOutOfBoundsException e) {
            return dimensions;
        }
        return dimensions;
    }

    @Override
    public void add(Point point) throws WrongNumberOfDimensionsException {
        if(pointList.size() == 0) {
            pointList.add(point);
            pointListAll.add(point);
        } else {
            int dimensionsOfFirstPoint = getDimension(pointList.get(0));
            int dimensionsOfPoint = getDimension(point);
            if(dimensionsOfFirstPoint != dimensionsOfPoint) {
                throw new WrongNumberOfDimensionsException(dimensionsOfFirstPoint, dimensionsOfPoint);
            } else {
                pointList.add(point);
                pointListAll.add(point);
            }
        }
    }

    @Override
    public void remove(Point point) throws WrongArgumentException {
        if(pointList.contains(point)) {
            pointList.remove(point);
        } else {
            throw new WrongArgumentException(point);
        }
    }

    @Override
    public void addBefore(Point point, Point beforePoint) throws WrongArgumentException, WrongNumberOfDimensionsException {
        int dimensionsOfFirstPoint = getDimension(pointList.get(0));
        int dimensionOfBeforePoint = getDimension(beforePoint);
        int dimensionsOfPoint = getDimension(point);
        if(dimensionOfBeforePoint == dimensionsOfFirstPoint) {
            if(pointList.contains(beforePoint)) {
                if(dimensionsOfPoint == dimensionsOfFirstPoint) {
                    int indexPointToAdd = pointList.indexOf(beforePoint);
                    pointList.add(indexPointToAdd, point);
                    pointListAll.add(point);
                } else {
                    throw new WrongNumberOfDimensionsException(dimensionsOfFirstPoint, dimensionsOfPoint);
                }
            } else {
                throw new WrongArgumentException(beforePoint);
            }
        } else {
            throw new WrongNumberOfDimensionsException(dimensionsOfFirstPoint, dimensionOfBeforePoint);

        }
    }

    @Override
    public void addAfter(Point point, Point afterPoint) throws WrongNumberOfDimensionsException, WrongArgumentException {
        int dimensionsOfFirstPoint = getDimension(pointList.get(0));
        int dimensionOfBeforePoint = getDimension(afterPoint);
        int dimensionsOfPoint = getDimension(point);
        if(dimensionOfBeforePoint == dimensionsOfFirstPoint) {
            if(pointList.contains(afterPoint)) {
                if(dimensionsOfPoint == dimensionsOfFirstPoint) {
                    int indexPointToAdd = pointList.lastIndexOf(afterPoint)+1;
                    pointList.add(indexPointToAdd, point);
                    pointListAll.add(point);
                } else {
                    throw new WrongNumberOfDimensionsException(dimensionsOfFirstPoint, dimensionsOfPoint);
                }
            } else {
                throw new WrongArgumentException(afterPoint);
            }
        } else {
            throw new WrongNumberOfDimensionsException(dimensionsOfFirstPoint, dimensionOfBeforePoint);
        }
    }

    @Override
    public Point removeBefore(Point beforePoint) throws NoSuchPointException, WrongNumberOfDimensionsException, WrongArgumentException {
        int dimensionsOfFirstPoint = getDimension(pointList.get(0));
        int dimensionsOfPoint = getDimension(beforePoint);
        if(dimensionsOfPoint == dimensionsOfFirstPoint) {
            if(pointList.contains(beforePoint)) {
                int indexOfPointToRemove = pointList.indexOf(beforePoint)-1;
                if(indexOfPointToRemove >= 0) {
                    Point removePoint = pointList.get(indexOfPointToRemove);
                    pointList.remove(indexOfPointToRemove);
                    return removePoint;
                } else {
                    throw new NoSuchPointException(beforePoint);
                }
            } else {
                throw new WrongArgumentException(beforePoint);
            }
        } else {
            throw new WrongNumberOfDimensionsException(dimensionsOfFirstPoint, dimensionsOfPoint);
        }
    }

    @Override
    public Point removeAfter(Point afterPoint) throws NoSuchPointException, WrongNumberOfDimensionsException, WrongArgumentException {
        int dimensionsOfFirstPoint = getDimension(pointList.get(0));
        int dimensionsOfPoint = getDimension(afterPoint);
        if(dimensionsOfPoint == dimensionsOfFirstPoint) {
            if(pointList.contains(afterPoint)) {
                int indexOfPointToRemove = pointList.lastIndexOf(afterPoint)+1;
                if(indexOfPointToRemove < pointList.size()) {
                    Point removePoint = pointList.get(indexOfPointToRemove);
                    pointList.remove(indexOfPointToRemove);
                    return removePoint;
                } else {
                    throw new NoSuchPointException(afterPoint);
                }
            } else {
                throw new WrongArgumentException(afterPoint);
            }
        } else {
            throw new WrongNumberOfDimensionsException(dimensionsOfFirstPoint, dimensionsOfPoint);
        }
    }

    @Override
    public List<Point> get() {
        return pointList;
    }

    @Override
    public Set<Point> getSetOfPoints() {
        return new HashSet<>(pointList);
    }

    @Override
    public Optional<Point> getByPosition(List<Integer> positions) throws WrongNumberOfDimensionsException {
        int dimensionsOfFirstPoint = getDimension(pointList.get(0));
        Optional<Point> optionalPoint;

        if(positions.size() != dimensionsOfFirstPoint) {
            throw new WrongNumberOfDimensionsException(getDimension(pointList.get(0)), positions.size());
        } else {
            Point pointTest = new Point(positions.size());
            IntStream.range(0, positions.size()).forEach(i -> pointTest.setPosition(i, positions.get(i)));
            optionalPoint = IntStream.iterate(pointListAll.size() - 1, i -> i >= 0, i -> i - 1).mapToObj(i -> pointListAll.get(i)).filter(aPointList -> aPointList.hashCode() == pointTest.hashCode()).findFirst();
            return optionalPoint;
        }
    }
}