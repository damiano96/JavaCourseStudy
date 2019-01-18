import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PMO_Test {
    //////////////////////////////////////////////////////////////////////////
    private static final Map<String, Double> tariff = new HashMap<>();

    static {
        tariff.put("privateConstructorTest", 1.0);
        tariff.put("testLimit", 1.6);
        tariff.put("testWrongSize", 0.3);
        tariff.put("testLimitSomeShipsDestroyed", 1.6);
        tariff.put("testLimitChange",0.7);
    }

    //////////////////////////////////////////////////////////////////////////

    private static void showException(Exception e, String txt) {
        e.printStackTrace();
        fail(txt + " " + e.toString());
    }

    public static double getTariff(String testName) {
        return tariff.get(testName);
    }

    private static Ship shipGenerator(int size) {
        Ship ship = null;

        try {
            ship = assertTimeoutPreemptively(Duration.ofMillis(100),
                    () -> {
                        return Ship.getShip(size);
                    });
        } catch (Exception e) {
            showException(e, "W trakcie pracy metody getShip doszło do błędu");
        }

        return ship;
    }

    private static void setLimits(ShipSizeLimit shipSizeLimit) {
        try {
            assertTimeoutPreemptively(Duration.ofMillis(100),
                    () -> {
                        Ship.setLimit(shipSizeLimit);
                    });
        } catch (Exception e) {
            showException(e, "W trakcie pracy metody setLimit doszło do błędu");
        }
    }

    private static Ship getShipTest(int size, boolean expected) {
        Ship ship = shipGenerator(size);

        if (expected) {
            assertNotNull(ship, "Oczekiwano, że okręt o rozmiarze " + size + " zostanie wygenerowany");
            assertFalse(ship.isShipwreck(), "Dostarczono okręt w stanie wrak!");
        } else {
            assertNull(ship, "Oczekiwano, że okręt nie zostanie stworzony");
        }

        return ship;
    }

    private static void sunkShip(Ship ship) {
        try {
            assertTimeoutPreemptively(Duration.ofMillis(100),
                    () -> {
                        ship.shipwreck();
                    });
        } catch (Exception e) {
            showException(e, "W trakcie pracy metody shipwreck doszło do błędu");
        }
    }

    @Test
    public void privateConstructorTest() {
        final Constructor<?>[] constructors = Ship.class.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            assertTrue(Modifier.isPrivate(constructor.getModifiers()),
                    "Znaleziono nieprywatny konstruktor" + constructor);
        }
    }

    @Test
    @DisplayName("Podstawowy test przestrzegania limitów")
    public void testLimit() {
        int[] limits = new int[]{5, 7, 10};
        ShipSizeLimit shipSizeLimit = new ShipSizeLimit(limits);

        setLimits(shipSizeLimit);
        getShips(limits);
    }

    @Test
    @DisplayName("Test działania dla rozmiarów spoza limitu")
    public void testWrongSize() {
        int[] limits = new int[]{5, 7, 10};
        ShipSizeLimit shipSizeLimit = new ShipSizeLimit(limits);
        setLimits(shipSizeLimit);

        getShipTest(0, false);
        getShipTest(-1, false);
        getShipTest(4, false);
    }

    @Test
    @DisplayName("Test zatapiania okrętów")
    public void testLimitSomeShipsDestroyed() {

        int[] limits = new int[]{5, 7, 10};
        ShipSizeLimit shipSizeLimit = new ShipSizeLimit(limits);
        setLimits(shipSizeLimit);

        List<Ship> shipsArray = new ArrayList<>();
        Ship ship;
        for (int size = 1; size <= limits.length; size++) {
            for (int ships = 0; ships < limits[size - 1]; ships++) {
                ship = getShipTest(size, true);
                shipsArray.add(ship);
            }
        }
        for (int size = 1; size <= limits.length; size++) {
            getShipTest(size, false);
        }

        shipsArray.forEach(PMO_Test::sunkShip);

        getShips(limits);
    }

    @Test
    @DisplayName("Test zmiany limitu")
    public void testLimitChange() {

        int[] limits = new int[]{5, 7, 10};
        ShipSizeLimit shipSizeLimit = new ShipSizeLimit(limits);
        setLimits(shipSizeLimit);

        getShips(limits);

        int[] limits2 = new int[]{1, 5, 7, 10, 11, 12};
        ShipSizeLimit shipSizeLimit2 = new ShipSizeLimit(limits2);
        setLimits(shipSizeLimit2);

        getShips(limits2);
    }

    private void getShips(int[] limits) {
        for (int size = 1; size <= limits.length; size++) {
            for (int ships = 0; ships < limits[size - 1]; ships++) {
                getShipTest(size, true);
            }
        }
        for (int size = 1; size <= limits.length; size++) {
            getShipTest(size, false);
        }
    }

}
