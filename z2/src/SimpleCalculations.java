class SimpleCalculations {

    public Point[] equidistantPoints(Point firstPoint, Point secondPoint, int points) {
        if(firstPoint == null || secondPoint == null) return null;
        Point[] returnPoints = new Point[points];
        Point point;

        int dimension = firstPoint.getNumberOfDimensions();
        double a;
        for(int i=1; i<points+1; i++) {
           point = new Point();
           point.setNumberOfDimensions(dimension);

            for(int j=0; j<dimension; j++) {
                if((Math.min(firstPoint.getPosition(j), secondPoint.getPosition(j)) == secondPoint.getPosition(j))) {
                    a = -Math.abs(firstPoint.getPosition(j) - secondPoint.getPosition(j)) * (i) / (points + 1) + Math.max(firstPoint.getPosition(j), secondPoint.getPosition(j));
                } else {
                    a = Math.abs(firstPoint.getPosition(j) - secondPoint.getPosition(j)) * (i) / (points + 1) + Math.min(firstPoint.getPosition(j), secondPoint.getPosition(j));
                }
                point.setPosition(j, a);
                returnPoints[i-1] = point;
            }
        }
        return returnPoints;
    }

    public Point geometricCenter(Point[] points) {
        if(points == null || points.length == 0) return null;

        int dimension = points[0].getNumberOfDimensions();
        Point p = new Point();
        p.setNumberOfDimensions(dimension);

        for (int i=0; i<dimension; i++) {
            double w = 0;
            for (Point point : points) {
                w += point.getPosition(i);
            }
            w = w/points.length;
            p.setPosition(i, w);
        }
        return p;
    }

    public Point next(Point firstPoint, Point secondPoint, double distance) {
        if(firstPoint == null || secondPoint == null) return null;

        Point point = new Point();
        double result;

        int dimension = firstPoint.getNumberOfDimensions();
        point.setNumberOfDimensions(dimension);

        int changeVariable = 0;
        for(int i=0; i<dimension; i++) {
            if(firstPoint.getPosition(i) != secondPoint.getPosition(i)) {
                changeVariable = i;
                break;
            }
        }
        double distanceHelp = Math.sqrt((firstPoint.getPosition(changeVariable) - secondPoint.getPosition(changeVariable)) * (firstPoint.getPosition(changeVariable) - secondPoint.getPosition(changeVariable)));

        if(firstPoint.getPosition(changeVariable) < secondPoint.getPosition(changeVariable)) {
            result = distanceHelp+distance+firstPoint.getPosition(changeVariable);
        } else {
            result = firstPoint.getPosition(changeVariable)-distanceHelp-distance;
        }
        for(int i=0; i<dimension; i++) {
            if(i == changeVariable) {
                point.setPosition(i, result);
            } else {
                point.setPosition(i, firstPoint.getPosition(i));
            }
        }
        return point;
    }


    public static void main(String args[]) {
    }
}
