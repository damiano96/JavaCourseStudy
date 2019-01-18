import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.Duration;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class PMO_Test {
    //////////////////////////////////////////////////////////////////////////
    private static final Map<String, Double> tariff = new HashMap<>();

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Tariff {
        double value();
    }

    static {
        tariff.put("addGet", 1.6);
        tariff.put("addException", 0.5 );

        tariff.put("remove", 0.1);
        tariff.put("removeWrong", 0.1);
        tariff.put("removeMultiple", 0.1);

        tariff.put("removeException", 0.5);

        tariff.put("addAfter", 0.1);
        tariff.put("addAfterMultiple", 0.1);

        tariff.put("addAfterArgumentException", 0.4);
        tariff.put("addAfterDimensionsExceptionF", 0.4);
        tariff.put("addAfterDimensionsExceptionS", 0.4);

        tariff.put("addBefore", 0.1);
        tariff.put("addBeforeMultiple", 0.1);

        tariff.put("addBeforeArgumentException", 0.4);
        tariff.put("addBeforeDimensionsExceptionF", 0.4);
        tariff.put("addBeforeDimensionsExceptionS", 0.4);

        tariff.put("removeBefore", 0.1);
        tariff.put("removeBeforeMultiple", 0.1);

        tariff.put("removeAfter", 0.1);
        tariff.put("removeAfterMultiple", 0.1);

        tariff.put("removeBeforeNoSuchPointException", 0.4);
        tariff.put("removeAfterNoSuchPointException", 0.4);
        tariff.put("removeBeforeWrongNumberOfDimensionsException", 0.4);
        tariff.put("removeAfterWrongNumberOfDimensionsException", 0.4);
        tariff.put("removeBeforeWrongArgumentException", 0.4);
        tariff.put("removeAfterWrongArgumentException", 0.4);

        // UWAGA - z uwagi na rosnącą liczbę linii kodu testu i samych testów
        // część metod ma taryfę umieszczoną bezpośrednio przed
        // nazwą metody.
    }

    public static double getTariff(String testName) {
        return tariff.get(testName);
    }

    //////////////////////////////////////////////////////////////////////////

    private static void showException(Throwable e, String txt) {
        e.printStackTrace();
        fail("W trakcie pracy metody " + txt + " doszło do wyjątku " + e.toString());
    }

    ////////////////////////////////////////////////////////////////////////////////

    private GeometricShapeInterface gs;
    private static Map<Class<? extends Throwable>, String> exceptionName =
            Map.of( WrongArgumentException.class, "WrongArgumentException",
                    NoSuchPointException.class, "NoSuchPointException",
                    WrongNumberOfDimensionsException.class, "WrongNumberOfDimensionsException" );

    @BeforeEach
    public void creator() {
        gs = new GeometricShape();
    }

    // Tak... poniżej sporo powielonego kodu. Problem da się rozwiązać w bardziej
    // cywilizowany sposób, ale ten jest łatwiejszy do zrozumienia.
    // Osobom zainteresowanym problemem wyjątków w wyrażeniach lambda
    // polecam taką stronę: https://www.baeldung.com/java-lambda-exceptions

    private void add(Point point, boolean exceptionExpected ) {
        if (!exceptionExpected) {
            try {
                gs.add(point);
            } catch (Exception e) {
                showException(e, "add");
            }
        } else {
            assertThrows(WrongNumberOfDimensionsException.class, () -> gs.add(point), "Oczekiwano wyjątku WrongNumberOfDimensionsException");
        }
    }

    private void remove(Point point, boolean exceptionExpected ) {
        if (!exceptionExpected) {
            try {
                gs.remove(point);
            } catch (Exception e) {
                showException(e, "remove");
            }
        } else {
            assertThrows(WrongArgumentException.class, () -> gs.remove(point), "Oczekiwano wyjątku WrongArgumentException" );
        }
    }

    private <T extends Throwable> void addAfter(Point point, Point secondPoint, Class< T >  expected ) {
        if (expected == null) {
            try {
                gs.addAfter(point, secondPoint);
            } catch (Exception e) {
                showException(e, "addAfter");
            }
        } else {
            assertThrows(expected, () -> gs.addAfter(point,secondPoint),
                    "Oczekiwano wyjątku " + exceptionName.get(expected));
        }
    }

    private <T extends Throwable> void addBefore(Point point, Point secondPoint, Class< T >  expected ) {
        if (expected == null) {
            try {
                gs.addBefore(point, secondPoint);
            } catch (Exception e) {
                showException(e, "addBefore");
            }
        } else {
            assertThrows(expected, () -> gs.addBefore(point,secondPoint),
                    "Oczekiwano wyjątku " + exceptionName.get(expected));
        }
    }

    private <T extends Throwable> void removeAfter(Point point , Class< T >  expected ) {
        if (expected == null) {
            try {
                gs.removeAfter(point);
            } catch (Exception e) {
                showException(e, "removeAfter");
            }
        } else {
            assertThrows(expected, () -> gs.removeAfter(point),
                    "Oczekiwano wyjątku " + exceptionName.get(expected));
        }
    }

    private <T extends Throwable> void removeBefore(Point point, Class< T >  expected ) {
        if (expected == null) {
            try {
                gs.removeBefore(point);
            } catch (Exception e) {
                showException(e, "removeBefore");
            }
        } else {
            assertThrows(expected, () -> gs.removeBefore(point),
                    "Oczekiwano wyjątku " + exceptionName.get(expected));
        }
    }

    private static void tryToExecute(Runnable run, String txt) {
        try {
            assertTimeoutPreemptively(Duration.ofMillis(100), run::run, "Przekroczono limit czasu " + txt);
        } catch (Exception e) {
            showException(e, txt);
        }
    }

    private static <T> T tryToExecute(ThrowingSupplier<T> run, String txt) {
        try {
            return assertTimeoutPreemptively(Duration.ofMillis(100), run::get, "Przekroczono limit czasu " + txt);
        } catch (Exception e) {
            showException(e, txt);
        }
        return null;
    }

    public List<Point> getList() {
        return tryToExecute(() -> gs.get(), "get");
    }

    public Set<Point> getSet() {
        return tryToExecute(() -> gs.getSetOfPoints(), "getSetOfPoints");
    }

    public void getByPosition( java.util.List<java.lang.Integer> positions, Point expected, boolean exceptionExpected ) {
        if ( exceptionExpected ) {
            assertThrows(WrongNumberOfDimensionsException.class, () -> gs.getByPosition(positions),
                    "Oczekiwano wyjątku WrongNumberOfDimensionsException");
        } else {
            try {
                Optional<Point> result = gs.getByPosition(positions);
                assertNotNull( result, "Wynikiem getByPosition nigdy nie może być null");
                if ( expected == null ) {
                    assertFalse( result.isPresent(), "Oczekiwano, że punkt nie zostanie zwrócony");
                } else {
                    assertTrue( result.isPresent(), "Oczekiwano zwrócenia punktu, a jest null");
                    assertEquals( expected, result.get(), "Oczekiwano zwrocenia innego punktu");
                    assertTrue( expected==result.get(), "Oczekiwano, że zostanie zwrócony punkt ostatnio dodany");
                }
            } catch (Exception e) {
                showException(e, "getByPosition");
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////

    private static Point generate( int ... positions ) {
        Point result = new Point( positions.length );
        IntStream.range(0, positions.length).forEach( i -> result.setPosition(i, positions[i]));
        return result;
    }

    private static Point clone(int dims, Point source) {
        Point result = new Point( dims );
        IntStream.range(0, dims).forEach( i -> result.setPosition(i, source.getPosition(i)));
        return result;
    }

    private Point p3_1 = generate(0, 0, 1);
    private Point p3_2 = generate(0, 0, 2);
    private Point p3_3 = generate(0, 3, 0);
    private Point p3_4 = generate(4, 0, 0);
    private Point p3_5 = generate(5, 0, 1);

    private Point p4_1 = generate( 0,0,0,1 );
    private Point p2_1 = generate( 0,0,0,1 );

    private Point p3_1c = clone( 3, p3_1 );
    private Point p3_2c = clone( 3, p3_2 );
    private Point p3_3c = clone( 3, p3_3 );
    private Point p3_4c = clone( 3, p3_4 );
    private Point p3_5c = clone( 3, p3_5 );

    private Point p1 = clone(3, p3_1 );
    private Point p2 = clone(3, p3_2 );
    private Point p3 = clone(3, p3_3 );
    private Point p4 = clone(3, p3_4 );
    private Point p5 = clone(3, p3_5 );
    private Point p6 = generate( 6,6,6 );
    private Point p7 = generate( 7,7,7 );

    private Point p1c = clone(3,p1);
    private Point p2c = clone(3,p2);
    private Point p3c = clone(3,p3);
    private Point p4c = clone(3,p4);
    private Point p5c = clone(3,p5);
    private Point p6c = clone(3,p6);
    private Point p7c = clone(3,p7);

    private void add(List<Point> points) {
        points.forEach( p -> add(p, false ));
    }

    private void testGet(List<Point> expected) {
        List<Point> result = getList();
        assertNotNull(result, "Wynikiem metody get nie może być null");
        assertIterableEquals(expected, result, "Niezgodność wyniku get z oczekiwanym");
    }

    @Test
    @DisplayName("Podstawowy test add/get")
    public void addGet() {
        add(List.of(p3_1, p3_2, p3_3));
        testGet(List.of(p3_1c, p3_2c, p3_3c));
        add(List.of(p3_1, p3_2, p3_4));
        testGet(List.of(p3_1c, p3_2c, p3_3c, p3_1c, p3_2c, p3_4c));
    }

    @Test
    @DisplayName("Test wyjątku w add")
    public void addException() {
        add(List.of(p3_1, p3_2, p3_3));
        add( p4_1, true );
        add( p2_1, true );
    }

    @Test
    @DisplayName("Podstawowy test remove")
    public void remove() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        remove(p6, false );
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p4c, p3c, p3c));
    }

    @Test
    @DisplayName("Test błędnego remove")
    public void removeException() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        remove(p7,true );
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p4c, p3c, p3c, p6c));
    }

    @Test
    @DisplayName("Podstawowy test addAfter")
    public void addAfter() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        addAfter(p1, p6, null);
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p4c, p3c, p3c, p6c, p1c));
    }

    @Test
    @DisplayName("Test addAfter z powtórzeniami")
    public void addAfterMultiple() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        addAfter(p7, p1, null);
        testGet(List.of(p1c, p2c, p1c, p1c, p7c, p3c, p4c, p3c, p3c, p6c));
        addAfter(p1, p3, null);
        testGet(List.of(p1c, p2c, p1c, p1c, p7c, p3c, p4c, p3c, p3c, p1c, p6c));
    }

    @Test
    @DisplayName("Test addAfter - wyjątek WrongArgumentException")
    public void addAfterArgumentException() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        addAfter(p1, p7, WrongArgumentException.class );
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p4c, p3c, p3c, p6c));
    }

    @Test
    @DisplayName("Test addAfter - wyjątek WrongNumberOfDimensionsException")
    public void addAfterDimensionsExceptionF() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        addAfter(p4_1, p1, WrongNumberOfDimensionsException.class );
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p4c, p3c, p3c, p6c));
    }

    @Test
    @DisplayName("Test addAfter - wyjątek WrongNumberOfDimensionsException")
    public void addAfterDimensionsExceptionS() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        addAfter(p1c, p2_1, WrongNumberOfDimensionsException.class );
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p4c, p3c, p3c, p6c));
    }

    @Test
    @DisplayName("Podstawowy test addBefore")
    public void addBefore() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        addBefore(p1, p6, null);
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p4c, p3c, p3c, p1c, p6c));
    }

    @Test
    @DisplayName("Test addBefore z powtórzeniami")
    public void addBeforeMultiple() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        addBefore(p7, p1, null);
        testGet(List.of(p7c, p1c, p2c, p1c, p1c, p3c, p4c, p3c, p3c, p6c));
        addBefore(p1, p3, null );
        testGet(List.of(p7c, p1c, p2c, p1c, p1c, p1c, p3c, p4c, p3c, p3c, p6c));
    }

    @Test
    @DisplayName("Test addBefore - wyjątek WrongArgumentException")
    public void addBeforeArgumentException() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        addBefore(p1, p7, WrongArgumentException.class );
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p4c, p3c, p3c, p6c));
    }

    @Test
    @DisplayName("Test addBefore - wyjątek WrongNumberOfDimensionsException")
    public void addBeforeDimensionsExceptionF() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        addBefore(p4_1, p2c, WrongNumberOfDimensionsException.class );
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p4c, p3c, p3c, p6c));
    }

    @Test
    @DisplayName("Test addBefore - wyjątek WrongNumberOfDimensionsException")
    public void addBeforeDimensionsExceptionS() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        addBefore(p3c, p2_1, WrongNumberOfDimensionsException.class );
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p4c, p3c, p3c, p6c));
    }

    @Test
    @DisplayName("Podstawowy test removeBefore")
    public void removeBefore() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        removeBefore(p6,null);
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p4c, p3c, p6c));
    }

    @Test
    @DisplayName("Test removeBefore z powtórzeniami")
    public void removeBeforeMultiple() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        removeBefore(p3,null);
        testGet(List.of(p1c, p2c, p1c, p3c, p4c, p3c, p3c, p6c));
        removeBefore(p3, null);
        testGet(List.of(p1c, p2c, p3c, p4c, p3c, p3c, p6c));
        removeBefore(p6, null);
        testGet(List.of(p1c, p2c, p3c, p4c, p3c, p6c));
    }


    @Test
    @DisplayName("Podstawowy test removeBefore - NoSuchPointException")
    @Tariff( 0.4 )
    public void removeBeforeNoSuchPointException() {
        add(List.of(p7, p2, p1, p1, p3, p4, p3, p6));
        removeBefore(p7c, NoSuchPointException.class );
        testGet(List.of(p7c, p2c, p1c, p1c, p3c, p4c, p3c, p6c));
    }

    @Test
    @DisplayName("Podstawowy test removeAfter - NoSuchPointException")
    @Tariff( 0.4 )
    public void removeAfterNoSuchPointException() {
        add(List.of(p7, p2, p1, p1, p3, p4, p3, p6));
        removeAfter(p6c, NoSuchPointException.class );
        testGet(List.of(p7c, p2c, p1c, p1c, p3c, p4c, p3c, p6c));
    }

    @Test
    @DisplayName("Podstawowy test removeBefore - WrongNumberOfDimensionsException")
    @Tariff( 0.4 )
    public void removeBeforeWrongNumberOfDimensionsException() {
        add(List.of(p7, p2, p1, p1, p3, p4, p3, p6));
        removeBefore(p2_1, WrongNumberOfDimensionsException.class );
        removeBefore(p4_1, WrongNumberOfDimensionsException.class );
        testGet(List.of(p7c, p2c, p1c, p1c, p3c, p4c, p3c, p6c));
    }

    @Test
    @DisplayName("Podstawowy test removeAfter - WrongNumberOfDimensionsException")
    @Tariff( 0.4 )
    public void removeAfterWrongNumberOfDimensionsException() {
        add(List.of(p7, p2, p1, p1, p3, p4, p3, p6));
        removeAfter(p2_1, WrongNumberOfDimensionsException.class );
        removeAfter(p4_1, WrongNumberOfDimensionsException.class );
        testGet(List.of(p7c, p2c, p1c, p1c, p3c, p4c, p3c, p6c));
    }

    @Test
    @DisplayName("Podstawowy test removeBefore - WrongArgumentException")
    @Tariff( 0.4 )
    public void removeBeforeWrongArgumentException() {
        add(List.of(p7, p2, p1, p1, p3, p4, p3, p6));
        removeBefore(p5c, WrongArgumentException.class );
        testGet(List.of(p7c, p2c, p1c, p1c, p3c, p4c, p3c, p6c));
    }

    @Test
    @DisplayName("Podstawowy test removeAfter - WrongArgumentException")
    @Tariff( 0.4 )
    public void removeAfterWrongArgumentException() {
        add(List.of(p7, p2, p1, p1, p3, p4, p3, p6));
        removeAfter(p5c, WrongArgumentException.class );
        testGet(List.of(p7c, p2c, p1c, p1c, p3c, p4c, p3c, p6c));
    }

    @Test
    @DisplayName("Podstawowy test removeAfter")
    public void removeAfter() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        removeAfter(p2,null);
        testGet(List.of(p1c, p2c, p1c, p3c, p4c, p3c, p3c, p6c));
    }

    @Test
    @DisplayName("Test removeAfter z powtórzeniami")
    public void removeAfterMultiple() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        removeAfter(p3,null);
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p4c, p3c, p3c));
        removeAfter(p1,null);
        testGet(List.of(p1c, p2c, p1c, p1c, p4c, p3c, p3c));
        removeAfter(p1, null);
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p3c));
    }

    private static class PointsComparator implements Comparator<Point> {
        @Override
        public int compare(Point point, Point t1) {

            int[] tbl1 = new int[] { point.getPosition(0), point.getPosition(1),
                point.getPosition(2) };

            int[] tbl2 = new int[] { t1.getPosition(0), t1.getPosition(1),
                    t1.getPosition(2) };

            return Arrays.compare( tbl1, tbl2 );
        }
    }

    @Test
    @DisplayName("Test getSetTest")
    @Tariff( 0.7 )
    public void getSetTest() {
        List<Point> points = List.of(p1, p7, p1, p2, p2, p1, p1, p3, p4, p3, p3, p6);
        Set<Point> expected = new TreeSet<>( new PointsComparator() );
        expected.addAll( points );
        add(points);

        Set<Point> result = new TreeSet<>( new PointsComparator() );
        result.addAll( getSet());

        assertIterableEquals( expected, result, "Oczekiwano innej zawartości zbioru");
    }

    @Test
    @DisplayName("Test metody getByPosition - empty Optional")
    @Tariff( 1.2 )
    public void emptyOptional() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p7, p3, p6));

        getByPosition( List.of( 0,0,0 ), null, false );
    }

    @Test
    @DisplayName("Test metody getByPosition - OK Optional")
    @Tariff( 0.7 )
    public void okOptional() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p7, p3, p6));

        getByPosition( List.of( 7,7,7 ), p7, false );
    }

    @Test
    @DisplayName("Test metody getByPosition - OK Optional - wiele do wyboru")
    @Tariff( 0.5 )
    public void okOptionalMultiple() {
        add(List.of(p1, p2, p1, p1, p3, p5, p4, p3, p7, p3, p6));
        add(List.of(p5c));
        getByPosition( List.of( 5,0,1 ), p5c, false );
    }

    @Test
    @DisplayName("Test metody getByPosition - wyjatek WrongNumberOfDimensionsException")
    @Tariff( 0.4 )
    public void optionalException() {
        add(List.of(p1, p2, p1, p1, p3, p5, p4, p3, p7, p3, p6));
        add(List.of(p5c));
        getByPosition( List.of( 5,0,1,1 ), null, true );
    }
}
