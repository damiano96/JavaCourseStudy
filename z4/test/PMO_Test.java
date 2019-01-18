import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class PMO_Test {
    //////////////////////////////////////////////////////////////////////////
    private static final Map<String, Double> tariff = new HashMap<>();

    static {
        tariff.put("initialStateTest", 1.0 );
        tariff.put("lockResultTest", 0.2 );
        tariff.put("lockLevelResultTest", 0.2 );
        tariff.put("unlockResultTest", 0.2 );
        tariff.put("unlockWrongPasswordResultTest", 0.2 );
        tariff.put("simpleSetResultTest", 1.0 );
        tariff.put("simpleSetGetTest", 1.0 );
        tariff.put("lockedUnlockedSetResultTest", 0.2 );
        tariff.put("lockedUnlockedSetGetTest", 1.0 );
        tariff.put("lockedUnlockedSetGetTest2", 1.0 );
        tariff.put("lockedNotUnlockedSetResultTest", 1.0 );
        tariff.put("lockedNotUnlockedSetGetTest", 1.0 );
        tariff.put("lockedNotUnlockedSetGetTest2", 1.0 );
        tariff.put("simpleMoveResultTest", 1.0 );
        tariff.put("simpleSetMoveGetTest", 1.0 );
        tariff.put("simpleSetLockedUnlockedMoveGetTest", 1.0 );
        tariff.put("simpleMove0ResultTest", 0.1 );
    }

    private BetterPoint betterPoint;
    private static final String PASSWORD_01 = "AlaMaKota";
    private static final String PASSWORD_02 = "AlaNieMaKota";
    private static final String PASSWORD_03 = "AlaMiałaKota";
    private static final String PASSWORD_04 = "AlaKupiłaKota";
    private static final String PASSWORD_05 = "AlaMaKoty";

    //////////////////////////////////////////////////////////////////////////

    public static double getTariff(String testName) {
        return tariff.get(testName);
    }

    private static void showException(Exception e, String txt) {
        e.printStackTrace();
        fail("W trakcie pracy metody " + txt + " doszło do wyjątku " + e.toString());
    }

    @BeforeEach
    public void createBetterPoint() {
        betterPoint = new BetterPoint();
    }

    private void setDimensions( int dims ) {
        try {
            betterPoint.setDimensions( dims );
        } catch ( Exception e ) {
            showException( e, "setDimensions" );
        }
    }

    private boolean executeSet( int dim, double value ) {
        boolean result = false;

        try {
            result = assertTimeoutPreemptively(Duration.ofMillis(100),() ->
            {
                return betterPoint.set(dim,value);
            });
        } catch ( Exception e ) {
            showException( e, "set(" + dim + ", " + value + ")");
        }

        return result;
    }

    private boolean executeMove( int dim, double value ) {
        boolean result = false;

        try {
            result = assertTimeoutPreemptively(Duration.ofMillis(100),() ->
            {
                return betterPoint.move(dim,value);
            });
        } catch ( Exception e ) {
            showException( e, "move(" + dim + ", " + value + ")");
        }

        return result;
    }


    private double executeGet( int dim ) {
        double result = 0.0;

        try {
            result = assertTimeoutPreemptively(Duration.ofMillis(100),() ->
            {
                return betterPoint.get(dim);
            });
        } catch ( Exception e ) {
            showException( e, "get(" + dim + ")" );
        }

        return result;
    }

    private double executeLockLevel() {
        int result = 0;

        try {
            result = assertTimeoutPreemptively(Duration.ofMillis(100),() ->
            {
                return betterPoint.lockLevel();
            });
        } catch ( Exception e ) {
            showException( e, "lockLevel" );
        }

        return result;
    }

    private int executeLock( String password ) {
        int result = 0;

        try {
            result = assertTimeoutPreemptively(Duration.ofMillis(100),() ->
            {
                return betterPoint.lock(password);
            });
        } catch ( Exception e ) {
            showException( e, "lock(" + password + ")" );
        }

        return result;
    }

    private int executeUnlock( String password ) {
        int result = 0;

        try {
            result = assertTimeoutPreemptively(Duration.ofMillis(100),() ->
            {
                return betterPoint.unlock(password);
            });
        } catch ( Exception e ) {
            showException( e, "unlock(" + password + ")" );
        }

        return result;
    }

    private void testSetGet( double[] values2set, double[] expectedGet, boolean modificationExpected,
                             boolean setResultTestOn ) {
        boolean setResult;
        double valueGet;
        for ( int i = 0; i < values2set.length; i++ ) {
            setResult = executeSet( i, values2set[ i ] );
            if ( setResultTestOn )
                assertEquals( modificationExpected, setResult, "Błędny wynik metody set dla set(" + i + ", " + values2set[i] + ")" ) ;
            valueGet = executeGet( i );
            if ( expectedGet != null )
                assertEquals( expectedGet[ i ], valueGet, 0.001, "Metoda get(" + i +
                        ") zwróciła inną wartość niż uczekiwano");
        }
    }

    private void testMoveGet( double[] values2set, double[] expectedGet, boolean modificationExpected,
                             boolean setResultTestOn ) {
        boolean setResult;
        double valueGet;
        for ( int i = 0; i < values2set.length; i++ ) {
            setResult = executeMove( i, values2set[ i ] );
            if ( setResultTestOn )
                assertEquals( modificationExpected, setResult, "Błędny wynik metody move dla set(" + i + ", " + values2set[i] + ")" ) ;
            valueGet = executeGet( i );
            if ( expectedGet != null )
                assertEquals( expectedGet[ i ], valueGet, 0.001, "Metoda get(" + i +
                        ") zwróciła inną wartość niż uczekiwano");
        }
    }

    @Test
    @DisplayName( "Test stanu początkowego")
    public void initialStateTest() {
        setDimensions( 123 );
        assertEquals(0, executeLockLevel(), "Oczekiwano, że nowy obiekt nie ma ochrony. lockLevel powinien wynosić 0" );
    }

    @Test
    @DisplayName( "Test wyniku zwracanego przez lock")
    public void lockResultTest() {
        setDimensions( 10 );
        assertEquals( 1, executeLock( PASSWORD_01 ) );
        assertEquals( 2, executeLock( PASSWORD_02 ) );
        assertEquals( 3, executeLock( PASSWORD_03 ) );
    }

    @Test
    @DisplayName( "Test wyniku zwracanego przez lockLevel")
    public void lockLevelResultTest() {
        setDimensions( 10 );
        executeLock( PASSWORD_01 );
        assertEquals(1, executeLockLevel() );
        executeLock( PASSWORD_02 );
        assertEquals(2, executeLockLevel() );
        executeLock( PASSWORD_03 );
        assertEquals(3, executeLockLevel() );
    }

    @Test
    @DisplayName( "Test wyniku zwracanego przez unlock")
    public void unlockResultTest() {
        setDimensions( 10 );
        executeLock( PASSWORD_01 );
        executeLock( PASSWORD_02 );
        executeLock( PASSWORD_03 );
        assertEquals(2, executeUnlock( PASSWORD_03) );
        assertEquals(1, executeUnlock( PASSWORD_02) );
        assertEquals(0, executeUnlock( PASSWORD_01) );
    }

    @Test
    @DisplayName( "Test wyniku zwracanego przez unlock przy błędnym haśle")
    public void unlockWrongPasswordResultTest() {
        setDimensions( 10 );
        executeLock( PASSWORD_01 );
        executeLock( PASSWORD_02 );
        executeLock( PASSWORD_03 );
        assertEquals(3, executeUnlock( PASSWORD_04) );
        assertEquals(3, executeUnlock( PASSWORD_05 ) );
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    @ParameterizedTest
    @MethodSource( "simpleSetGetTestDataProvider" )
    @DisplayName( "Test wyniku niezablokowanej metody set")
    public void simpleSetResultTest( double[] values2set, double[] values2set2  ) {
        setDimensions( values2set.length );
        testSetGet( values2set, null, true, true );
    }

    @ParameterizedTest
    @MethodSource( "simpleSetGetTestDataProvider" )
    @DisplayName( "Test poprawności działania set/get")
    public void simpleSetGetTest( double[] values2set, double[] values2set2 ) {
        setDimensions( values2set.length );
        testSetGet( values2set, values2set, true, false );
    }

    @ParameterizedTest
    @MethodSource( "simpleSetGetTestDataProvider" )
    @DisplayName( "Test wyniku zwracanego przez zablokowaną/odblokowaną metodę set")
    public void lockedUnlockedSetResultTest( double[] values2set, double[] values2set2 ) {
        setDimensions( values2set.length );
        testSetGet( values2set, null, true, true );
        executeLock( PASSWORD_04 );
        testSetGet( values2set2, null, false, true );
        executeUnlock( PASSWORD_04 );
        testSetGet( values2set2, null, true, true );
    }

    @ParameterizedTest
    @MethodSource( "simpleSetGetTestDataProvider" )
    @DisplayName( "Test poprawności działania zablokowanego/odblokowanego set/get")
    public void lockedUnlockedSetGetTest( double[] values2set, double[] values2set2 ) {
        setDimensions( values2set.length );
        testSetGet( values2set, values2set, true, false );
        executeLock( PASSWORD_05 );
        testSetGet( values2set2, values2set, false, false );
        executeUnlock( PASSWORD_05 );
        testSetGet( values2set2, values2set2, true, false );
    }

    @ParameterizedTest
    @MethodSource( "simpleSetGetTestDataProvider" )
    @DisplayName( "Test poprawności działania zablokowanego/odblokowanego set/get")
    public void lockedUnlockedSetGetTest2( double[] values2set, double[] values2set2 ) {
        setDimensions( values2set.length );
        testSetGet( values2set, values2set, true, false );
        executeLock( PASSWORD_01 );
        executeLock( PASSWORD_02 );
        executeLock( PASSWORD_02 );
        executeLock( PASSWORD_03 );
        testSetGet( values2set2, values2set, false, false );
        executeUnlock( PASSWORD_03 );
        testSetGet( values2set2, values2set, false, false );
        executeUnlock( PASSWORD_02 );
        testSetGet( values2set2, values2set, false, false );
        executeUnlock( PASSWORD_02 );
        testSetGet( values2set2, values2set, false, false );
        executeUnlock( PASSWORD_01 );
        testSetGet( values2set2, values2set2, true, false );
    }

    @ParameterizedTest
    @MethodSource( "simpleSetGetTestDataProvider" )
    @DisplayName( "Test wyniku zwracanego niepopranie odblokowaną metodę set")
    public void lockedNotUnlockedSetResultTest( double[] values2set, double[] values2set2 ) {
        setDimensions( values2set.length );
        testSetGet( values2set, null, true, true );
        executeLock( PASSWORD_04 );
        testSetGet( values2set2, null, false, true );
        executeUnlock( PASSWORD_05 );
        testSetGet( values2set2, null, false, true );
    }

    @ParameterizedTest
    @MethodSource( "simpleSetGetTestDataProvider" )
    @DisplayName( "Test poprawności działania niepoprawnie odblokowanego set")
    public void lockedNotUnlockedSetGetTest( double[] values2set, double[] values2set2 ) {
        setDimensions( values2set.length );
        testSetGet( values2set, values2set, true, false );
        executeLock( PASSWORD_05 );
        testSetGet( values2set2, values2set, false, false );
        executeUnlock( PASSWORD_04 );
        testSetGet( values2set2, values2set, true, false );
    }

    @ParameterizedTest
    @MethodSource( "simpleSetGetTestDataProvider" )
    @DisplayName( "Test poprawności działania niepoprawnie odblokowanego set")
    public void lockedNotUnlockedSetGetTest2( double[] values2set, double[] values2set2 ) {
        setDimensions( values2set.length );
        testSetGet( values2set, values2set, true, false );
        executeLock( PASSWORD_01 );
        executeLock( PASSWORD_02 );
        executeLock( PASSWORD_03 );
        testSetGet( values2set2, values2set, false, false );
        executeUnlock( PASSWORD_01 );
        executeUnlock( PASSWORD_02 );
        executeUnlock( PASSWORD_03 );
        testSetGet( values2set2, values2set, true, false );
    }

    static  Stream<Arguments> simpleSetGetTestDataProvider() {
        return Stream.of( Arguments.of( new double[] {1,2,3,4,3,2,1}, new double[] {1,2,3,8,9,22,11} ),
                Arguments.of( new double[] { 9, 10, 11, 23, 2, 4, 2, -2 }, new double[] { 9, -10, -11, -23, 2, 4, 2, -2 } ));
    }
    /////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @MethodSource( "simpleSetMoveGetTestDataProvider" )
    @DisplayName( "Test wyniku niezablokowanej metody move")
    public void simpleMoveResultTest( double[] values2set, double[] values2move, double[] expectedGet ) {
        setDimensions( values2set.length );
        testMoveGet( values2move, null, true, true );
    }

    @Test
    @DisplayName( "Test wyniku niezablokowanej metody move")
    public void simpleMove0ResultTest( ) {
        setDimensions( 3 );
        testMoveGet( new double[] {0,0,0}, null, false, true );
    }

    @ParameterizedTest
    @MethodSource( "simpleSetMoveGetTestDataProvider" )
    @DisplayName( "Test poprawności działania niezablokowanego set/move/get")
    public void simpleSetMoveGetTest( double[] values2set, double[] values2move, double[] expectedGet ) {
        simpleSetGetTest( values2set, values2set );
        // jeśli set nie zadziała, to sprawdzanie move nie ma sensu
        testMoveGet( values2move, expectedGet, true, false );
    }

    @ParameterizedTest
    @MethodSource( "simpleSetMoveGetTestDataProvider" )
    @DisplayName( "Test poprawności działania zablokowanego move")
    public void simpleSetLockedUnlockedMoveGetTest( double[] values2set, double[] values2move, double[] expectedGet ) {
        simpleSetGetTest( values2set, values2set );
        // jeśli set nie zadziała, to sprawdzanie move nie ma sensu
        executeLock( PASSWORD_04 );
        testMoveGet( values2move, values2set, true, false );
        executeUnlock( PASSWORD_04 );
        testMoveGet( values2move, expectedGet, true, false );
    }


    static Stream<Arguments> simpleSetMoveGetTestDataProvider() {
        return Stream.of( Arguments.of( new double[] {1,2,3,4,3,2,1},
                                        new double[] {2,4,5,-2,3,3,-1},
                                        new double[] {3,6,8,2,6,5,0} ),
                Arguments.of( new double[] { 9,  10, 11,  23, 2, 4, 2, -2 },
                              new double[] { -2, 10, 15, -23, 7, 4, -10, -1 },
                              new double[] { 7,  20, 26, 0,   9, 8, -8, -3 }));
    }
    /////////////////////////////////////////////////////////////////////////////////////////////

}
