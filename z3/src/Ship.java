import java.util.*;

class Ship {
    private boolean shipwreck;
    private int size;
    private static ShipSizeLimit limit;
    private static Map<Integer, Integer> ships;

    private Ship(int size) {
        this.size = size;
    }

    public static void setLimit(ShipSizeLimit limit) {
        Ship.limit = limit;
        ships = new HashMap<>();
        for (int i=0; i<=limit.getNumberOfSizes(); i++) {
            ships.put(i, limit.getLimit(i));
        }
    }

    public static Ship getShip(int size) {
        if(limit == null) return null;
        if(size < 0 || size > limit.getNumberOfSizes()) return null;

        int shipsSize = ships.get(size);
        if(ships.get(size) > 0) {
            ships.replace(size, --shipsSize);
            return new Ship(size);
        } else {
            return null;
        }
    }

    public void shipwreck() {
        shipwreck = true;
        int shipsSize = ships.get(size);
        ships.replace(size, ++shipsSize);
    }

    public boolean isShipwreck() {
        return shipwreck;
    }

    public static void main(String args[]) {
    }
}
