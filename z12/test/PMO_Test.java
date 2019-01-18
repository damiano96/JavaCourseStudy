import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.ThrowingSupplier;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public class PMO_Test {
    //////////////////////////////////////////////////////////////////////////
    private static final Map<String, Double> tariff = new HashMap<>();

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Tariff {
        double value();
    }

    static {
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

    private static <T> T tryToExecute(ThrowingSupplier<T> run, String txt, long timeLimit) {
        try {
            return assertTimeoutPreemptively(Duration.ofMillis(timeLimit), run::get, "Przekroczono limit czasu " + txt);
        } catch (Exception e) {
            showException(e, txt);
        }
        return null;
    }

    private static void tryToExecute(Runnable run, String txt, long timeLimit) {
        try {
            assertTimeoutPreemptively(Duration.ofMillis(timeLimit), run::run, "Przekroczono limit czasu " + txt);
        } catch (Exception e) {
            showException(e, txt);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////////

    private Consumer<String> starter;
    private PMO_Easy easy = new PMO_Easy();
    private PMO_EasyParameter easyParameter = new PMO_EasyParameter();
    private PMO_Null nullClass = new PMO_Null();
    private PMO_MultipleMethods multipleMethods = new PMO_MultipleMethods();
    // powyższe klasy nie są przez test używane, ale wpisanie ich powyżej ułatwia kompilację kodu

    @BeforeEach
    public void creator() {
        starter = new Starter();
        PMO_TestBaseClass.clear();
    }

    private void test( String className, boolean executionExpected, int counterExpected ) {
        test( className, executionExpected, counterExpected, null );
    }

    private void test( String className, boolean executionExpected, int counterExpected, String argument ) {
        starter.accept( className );

        if ( executionExpected ) {
            assertTrue( PMO_TestBaseClass.getExecutionFlag(), "Oczekiwano, że co najmniej jedna metoda z klasy " + className +
                    " zostanie uruchomiona" );
            assertEquals( counterExpected, PMO_TestBaseClass.getCounter(), "Metod(y) nie wykonano odpowiednią ilość razy" );
            if ( argument != null ) {
                assertEquals(argument, PMO_TestBaseClass.getArgument(), "Oczekiwano innego argumentu wywołania metody");
            }
        } else {
            assertFalse( PMO_TestBaseClass.getExecutionFlag(), "Oczekiwano, że ŻADNA metoda z klasy " + className +
                    " NIE zostanie uruchomiona" );
        }
    }

    @DisplayName("Uruchomienie metody bezparametrowej")
    @Test
    @Tariff(1.0)
    public void easyStart() {
        test("PMO_Easy", true, 2 );
    }

    @DisplayName("Uruchomienie metody z parametrem String")
    @Test
    @Tariff(1.0)
    public void easyStartParameter() {
        test("PMO_EasyParameter", true, 2, "Pyra" );
    }

    @DisplayName("Klasa z wieloma metodami do uruchomienia")
    @Test
    @Tariff(0.5)
    public void manyMethodsToStart() {
        test("PMO_MultipleMethods", true, 30, "nicCiekawego" );
    }

    @DisplayName("Klasa z wieloma metodami, ale żadnej nie powinno się uruchamiać")
    @Test
    @Tariff(1.0)
    public void nothingToStart() {
        test("PMO_Null", false, 0 );
    }
}
