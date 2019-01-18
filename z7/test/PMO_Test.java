import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class PMO_Test {
    //////////////////////////////////////////////////////////////////////////
    private static final Map<String, Double> tariff = new HashMap<>();

    static {
        tariff.put("addGet", 1.6);
        tariff.put("addUniq", 1.6);
        tariff.put("addMap", 1.6);

        tariff.put("remove", 0.5);
        tariff.put("removeWrong", 0.5);
        tariff.put("removeResult", 0.1);
        tariff.put("removeMultiple", 0.2);

        tariff.put("addAfter", 0.5);
        tariff.put("addAfterWrong", 0.5);
        tariff.put("addAfterResult", 0.1);
        tariff.put("addAfterMultiple", 0.2);

        tariff.put("addBefore", 0.5);
        tariff.put("addBeforeWrong", 0.5);
        tariff.put("addBeforeResult", 0.1);
        tariff.put("addBeforeMultiple", 0.2);

        tariff.put("removeBefore", 0.5);
        tariff.put("removeBeforeMultiple", 0.2);
        tariff.put("removeBeforeResult", 0.1);

        tariff.put("removeAfter", 0.5);
        tariff.put("removeAfterMultiple", 0.2);
        tariff.put("removeAfterResult", 0.1);

        tariff.put("addUndo", 0.3);
        tariff.put("addUndoMultiple", 0.3);
        tariff.put("addBeforeUndo", 0.3);
        tariff.put("addAfterUndo", 0.3);
        tariff.put("addBeforeUndoWrong", 0.3);
        tariff.put("addAfterUndoWrong", 0.3);

        tariff.put("addUndoRedo", 0.2);
        tariff.put("addBeforeUndoRedo", 0.2);
        tariff.put("addAfterUndoRedo", 0.2);

        tariff.put("removeAfterUndo", 0.2);
        tariff.put("removeAfterUndoWrong", 0.2);
        tariff.put("removeAfterUndoRedo", 0.2);

        tariff.put("removeBeforeUndo", 0.2);
        tariff.put("removeBeforeUndoWrong", 0.2);
        tariff.put("removeBeforeUndoRedo", 0.2);
    }

    public static double getTariff(String testName) {
        return tariff.get(testName);
    }

    //////////////////////////////////////////////////////////////////////////

    private static void showException(Exception e, String txt) {
        e.printStackTrace();
        fail("W trakcie pracy metody " + txt + " doszło do wyjątku " + e.toString());
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

    ////////////////////////////////////////////////////////////////////////////////

    private GeometricShapeInterface gs;

    @BeforeEach
    public void creator() {
        gs = new GeometricShape();
    }

    private void add(Point point) {
        tryToExecute(() -> gs.add(point), "add");
    }

    private boolean addAfter(Point point, Point afterPoint) {
        return tryToExecute(() -> gs.addAfter(point, afterPoint), "addAfter");
    }

    public boolean remove(Point point) {
        return tryToExecute(() -> gs.remove(point), "remove");
    }

    public boolean addBefore(Point point, Point beforePoint) {
        return tryToExecute(() -> gs.addBefore(point, beforePoint), "addBefore");
    }

    public Point removeBefore(Point beforePoint) {
        return tryToExecute(() -> gs.removeBefore(beforePoint), "removeBefore");
    }

    public Point removeAfter(Point afterPoint) {
        return tryToExecute(() -> gs.removeAfter(afterPoint), "removeAfter");
    }

    public boolean undo() {
        return tryToExecute(() -> gs.undo(), "undo");
    }

    public boolean redo() {
        return tryToExecute(() -> gs.redo(), "redo");
    }

    public List<Point> get() {
        return tryToExecute(() -> gs.get(), "get");
    }

    public List<Point> getUniq() {
        return tryToExecute(() -> gs.getUniq(), "getUniq");
    }

    public Map<Point, Integer> getPointsAsMap() {
        return tryToExecute(() -> gs.getPointsAsMap(), "getPointsAsMap");
    }

    ///////////////////////////////////////////////////////////////////////////////

    private static Point generate(int p1, int p2, int p3) {
        Point result = new Point();
        result.setPosition(0, p1);
        result.setPosition(1, p2);
        result.setPosition(2, p3);
        return result;
    }

    private static Point clone(Point source) {
        return generate(source.getPosition(0), source.getPosition(1), source.getPosition(2));
    }

    private Point p1 = generate(0, 0, 1);
    private Point p2 = generate(0, 0, 2);
    private Point p3 = generate(0, 3, 0);
    private Point p4 = generate(4, 0, 0);
    private Point p5 = generate(5, 0, 1);
    private Point p6 = generate(0, 6, 1);
    private Point p7 = generate(1, 1, 7);
    private Point p1c = clone(p1);
    private Point p2c = clone(p2);
    private Point p3c = clone(p3);
    private Point p4c = clone(p4);
    private Point p5c = clone(p5);
    private Point p6c = clone(p6);
    private Point p7c = clone(p7);

    private void add(List<Point> points) {
        points.forEach(this::add);
    }

    private void testGet(List<Point> expected) {
        List<Point> result = get();
        assertNotNull(result, "Wynikiem metody get nie może być null");
        assertIterableEquals(expected, result, "Niezgodność wyniku get z oczekiwanym");
    }

    private void testUniq(List<Point> expected) {
        List<Point> result = getUniq();
        assertNotNull(result, "Wynikiem metody getUniq nie może być null");
        assertIterableEquals(expected, result, "Niezgodność wyniku getUniq z oczekiwanym");
    }

    private void testMap(Map<Point, Integer> expected) {
        Map<Point, Integer> result = getPointsAsMap();
        assertNotNull(result, "Wynikiem metody getPointsAsMap nie może być null");

        assertEquals(expected.size(), result.size(), "Oczekiwano innej liczby elementów w mapie getPointsAsMap");

        expected.entrySet().forEach(e -> {
            assertTrue(result.containsKey(e.getKey()), "Oczekiwano, że w mapie jest klucz " + e.getKey());
            assertEquals(e.getValue(), result.get(e.getKey()), "Oczekiwano innej wartości dla klucza " + e.getKey());
        });
    }

    @Test
    @DisplayName("Podstawowy test add/get")
    public void addGet() {
        add(List.of(p1, p2, p3));
        testGet(List.of(p1c, p2c, p3c));
        add(List.of(p1, p2, p4));
        testGet(List.of(p1c, p2c, p3c, p1c, p2c, p4c));
    }

    @Test
    @DisplayName("Podstawowy test add/getUniq")
    public void addUniq() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        testUniq(List.of(p1c, p2c, p1c, p3c, p4c, p3c, p6c));
        add(List.of(p1, p2, p5, p5, p5, p5, p6));
        testUniq(List.of(p1c, p2c, p1c, p3c, p4c, p3c, p6c, p1c, p2c, p5c, p6c));
    }

    @Test
    @DisplayName("Podstawowy test add/getPointsAsMap")
    public void addMap() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        testMap(Map.of(p1c, 3, p2c, 1, p3c, 3, p4c, 1, p6c, 1));
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6, p7));
        testMap(Map.of(p1c, 6, p2c, 2, p3c, 6, p4c, 2, p6c, 2, p7c, 1));
    }

    @Test
    @DisplayName("Podstawowy test remove")
    public void remove() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        remove(p6);
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p4c, p3c, p3c));
    }

    @Test
    @DisplayName("Test błędnego remove")
    public void removeWrong() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        remove(p7);
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p4c, p3c, p3c, p6c));
    }

    @Test
    @DisplayName("Test rezultatu remove")
    public void removeResult() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        assertTrue(remove(p6), "Usunięcie istniejącego punktu powinno zwrócić true");
        assertFalse(remove(p7), "Usunięcie nieistniejącego punktu powinno zwrócić false");
    }

    @Test
    @DisplayName("Test remove - powtórzenia elementów")
    public void removeMultiple() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        remove(p6);
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p4c, p3c, p3c));
        remove(p1);
        testGet(List.of(p2c, p1c, p1c, p3c, p4c, p3c, p3c));
        remove(p4);
        testGet(List.of(p2c, p1c, p1c, p3c, p3c, p3c));
        remove(p3);
        testGet(List.of(p2c, p1c, p1c, p3c, p3c));
        remove(p1);
        testGet(List.of(p2c, p1c, p3c, p3c));
    }

    @Test
    @DisplayName("Podstawowy test addAfter")
    public void addAfter() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        addAfter(p1, p6);
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p4c, p3c, p3c, p6c, p1c));
    }

    @Test
    @DisplayName("Test błędnego wywołania addAfter")
    public void addAfterWrong() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        addAfter(p1, p5);
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p4c, p3c, p3c, p6c));
    }

    @Test
    @DisplayName("Test rezultatu addAfter")
    public void addAfterResult() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        assertTrue(addAfter(p1, p6), "Prawidłowe wywołanie addAfter powinno zwrócić true");
        assertFalse(addAfter(p1, p5), "Błędne wywołanie addAfter powinno zwrócić false");
    }

    @Test
    @DisplayName("Test addAfter z powtórzeniami")
    public void addAfterMultiple() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        addAfter(p7, p1);
        testGet(List.of(p1c, p2c, p1c, p1c, p7c, p3c, p4c, p3c, p3c, p6c));
        addAfter(p1, p3);
        testGet(List.of(p1c, p2c, p1c, p1c, p7c, p3c, p4c, p3c, p3c, p1c, p6c));
    }

    @Test
    @DisplayName("Podstawowy test addBefore")
    public void addBefore() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        addBefore(p1, p6);
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p4c, p3c, p3c, p1c, p6c));
    }

    @Test
    @DisplayName("Test błędnego wywołania addBefore")
    public void addBeforeWrong() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        addBefore(p1, p7);
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p4c, p3c, p3c, p6c));
    }

    @Test
    @DisplayName("Test rezultatu addBefore")
    public void addBeforeResult() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        assertTrue(addAfter(p1, p6), "Prawidłowe wywołanie addBefore powinno zwrócić true");
        assertFalse(addAfter(p1, p5), "Błędne wywołanie addBefore powinno zwrócić false");
    }

    @Test
    @DisplayName("Test addBefore z powtórzeniami")
    public void addBeforeMultiple() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        addBefore(p7, p1);
        testGet(List.of(p7c, p1c, p2c, p1c, p1c, p3c, p4c, p3c, p3c, p6c));
        addBefore(p1, p3);
        testGet(List.of(p7c, p1c, p2c, p1c, p1c, p1c, p3c, p4c, p3c, p3c, p6c));
    }

    @Test
    @DisplayName("Podstawowy test removeBefore")
    public void removeBefore() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        removeBefore(p6);
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p4c, p3c, p6c));
    }

    @Test
    @DisplayName("Test removeBefore z powtórzeniami")
    public void removeBeforeMultiple() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        removeBefore(p3);
        testGet(List.of(p1c, p2c, p1c, p3c, p4c, p3c, p3c, p6c));
        removeBefore(p3);
        testGet(List.of(p1c, p2c, p3c, p4c, p3c, p3c, p6c));
        removeBefore(p6);
        testGet(List.of(p1c, p2c, p3c, p4c, p3c, p6c));
    }

    @Test
    @DisplayName("Test rezultatu removeBefore")
    public void removeBeforeResult() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        Point result = removeBefore(p6);
        assertNotNull(result, "Przy prawidłowych danych removeBefore nie może zwrócić NULL");
        assertEquals(p3, result, "Prawidłowe dane - oczekiwano innego wyniku removeBefore");

        result = removeBefore(p7);
        assertNull(result, "removeBefore - wskazano nieistniejący punkt - wynikiem powinien być NULL");
        result = removeBefore(p1);
        assertNull(result, "removeBefore - przed wskazanym punktem nie punktu do usunięcia - wynikiem ma być NULL");
    }

    @Test
    @DisplayName("Podstawowy test removeAfter")
    public void removeAfter() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        removeAfter(p2);
        testGet(List.of(p1c, p2c, p1c, p3c, p4c, p3c, p3c, p6c));
    }

    @Test
    @DisplayName("Test removeAfter z powtórzeniami")
    public void removeAfterMultiple() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        removeAfter(p3);
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p4c, p3c, p3c));
        removeAfter(p1);
        testGet(List.of(p1c, p2c, p1c, p1c, p4c, p3c, p3c));
        removeAfter(p1);
        testGet(List.of(p1c, p2c, p1c, p1c, p3c, p3c));
    }

    @Test
    @DisplayName("Test rezultatu removeAfter")
    public void removeAfterResult() {
        add(List.of(p1, p2, p1, p1, p3, p4, p3, p3, p6));
        Point result = removeAfter(p4);
        assertNotNull(result, "Przy prawidłowych danych removeAfter nie może zwrócić NULL");
        assertEquals(p3, result, "Prawidłowe dane - oczekiwano innego wyniku removeAfter");

        result = removeAfter(p5);
        assertNull(result, "removeAfter - wskazano nieistniejący punkt - wynikiem powinien być NULL");
        result = removeAfter(p6);
        assertNull(result, "removeAfter - za wskazanym punktem nie punktu do usunięcia - wynikiem ma być NULL");
    }

    @Test
    @DisplayName("Test add + undo")
    public void addUndo() {
        add(List.of(p1, p2));
        add(List.of(p3));
        testGet(List.of(p1c, p2c, p3c));
        undo();
        testGet(List.of(p1c, p2c));
    }

    @Test
    @DisplayName("Test add + undo")
    public void addUndoMultiple() {
        add(List.of(p1, p2));
        add(List.of(p3));
        addAfter(p4, p2);
        addBefore(p5, p2);
        testGet(List.of(p1c, p5c, p2c, p4c, p3c));
        undo();
        undo();
        undo();
        testGet(List.of(p1c, p2c));
    }

    @Test
    @DisplayName("Test add + undo + redo")
    public void addUndoRedo() {
        add(List.of(p1, p2));
        add(List.of(p3));
        testGet(List.of(p1c, p2c, p3c));
        undo();
        testGet(List.of(p1c, p2c));
        redo();
        testGet(List.of(p1c, p2c, p3c));
    }

    @Test
    @DisplayName("Test addBefore + undo")
    public void addBeforeUndo() {
        add(List.of(p1, p2));
        addBefore(p3, p2);
        testGet(List.of(p1c, p3c, p2c));
        undo();
        testGet(List.of(p1c, p2c));
    }

    @Test
    @DisplayName("Test addBefore + undo - błędne addBefore")
    public void addBeforeUndoWrong() {
        add(List.of(p1, p2));
        addBefore(p3, p3);
        testGet(List.of(p1c, p2c));
        undo();
        testGet(List.of(p1c));
    }

    @Test
    @DisplayName("Test addBefore + undo + redo")
    public void addBeforeUndoRedo() {
        add(List.of(p1, p2));
        addBefore(p3, p2);
        testGet(List.of(p1c, p3c, p2c));
        undo();
        testGet(List.of(p1c, p2c));
        redo();
        testGet(List.of(p1c, p3c, p2c));
    }

    @Test
    @DisplayName("Test addAfter + undo")
    public void addAfterUndo() {
        add(List.of(p1, p2));
        addAfter(p3, p2);
        testGet(List.of(p1c, p2c, p3c));
        undo();
        testGet(List.of(p1c, p2c));
    }

    @Test
    @DisplayName("Test addAfter + undo")
    public void addAfterUndoWrong() {
        add(List.of(p1, p2));
        addAfter(p3, p4);
        testGet(List.of(p1c, p2c));
        undo();
        testGet(List.of(p1c));
    }

    @Test
    @DisplayName("Test addAfter + undo + redo")
    public void addAfterUndoRedo() {
        add(List.of(p1, p2));
        addAfter(p3, p2);
        testGet(List.of(p1c, p2c, p3c));
        undo();
        testGet(List.of(p1c, p2c));
        redo();
        testGet(List.of(p1c, p2c, p3c));
    }

    @Test
    @DisplayName("Test removeAfter + undo")
    public void removeAfterUndo() {
        add(List.of(p1, p2, p3));
        removeAfter(p2);
        testGet(List.of(p1c, p2c));
        undo();
        testGet(List.of(p1c, p2c, p3c));
    }

    @Test
    @DisplayName("Test removeAfter + undo")
    public void removeAfterUndoWrong() {
        add(List.of(p1, p2, p3));
        removeAfter(p3);
        removeAfter(p4);
        testGet(List.of(p1c, p2c, p3c));
        undo();
        testGet(List.of(p1c, p2c));
    }

    @Test
    @DisplayName("Test removeAfter + undo + redo")
    public void removeAfterUndoRedo() {
        add(List.of(p1, p2, p3));
        removeAfter(p2);
        testGet(List.of(p1c, p2c));
        undo();
        testGet(List.of(p1c, p2c, p3c));
        redo();
        testGet(List.of(p1c, p2c));
    }

    @Test
    @DisplayName("Test removeBefore + undo")
    public void removeBeforeUndo() {
        add(List.of(p1, p2, p3));
        removeBefore(p2);
        testGet(List.of(p2c, p3c));
        undo();
        testGet(List.of(p1c, p2c, p3c));
    }

    @Test
    @DisplayName("Test błędnego removeBefore + undo")
    public void removeBeforeUndoWrong() {
        add(List.of(p1, p2, p3));
        removeBefore(p1);
        removeBefore(p4);
        testGet(List.of(p1c, p2c, p3c));
        undo();
        testGet(List.of(p1c, p2c));
    }

    @Test
    @DisplayName("Test removeBefore + undo + redo")
    public void removeBeforeUndoRedo() {
        add(List.of(p1, p2, p3));
        removeBefore(p2);
        testGet(List.of(p2c, p3c));
        undo();
        testGet(List.of(p1c, p2c, p3c));
        redo();
        testGet(List.of(p2c, p3c));
    }
}
