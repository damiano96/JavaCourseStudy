/**
 * Klasa Punkt reprezentuje punkt w n-wymiarowej przestrzeni.
 */
public class Point {
    private int dimensions;
    private double[] position;

    /**
     * Metoda pozwala na ustawienie liczby wymiarĂłw.
     * Efektem ubocznym jest ustawienie wszystkich wspĂłĹrzÄdnych
     * na 0. Metoda ta musi zostaÄ wywoĹana przed setPosition
     * oraz getPosition.
     * @param dimensions liczba wymiarĂłw
     */
    public void setNumberOfDimensions( int dimensions ) {
        this.dimensions = dimensions;
        this.position = new double[ dimensions ];
    }

    /**
     * Metoda zwraca liczbe wymiarĂłw.
     *
     * @return liczba wymiarĂłw
     */
    public int getNumberOfDimensions() {
        return dimensions;
    }


    /**
     * Metoda pozwala na ustawienie podanej wspĂłĹrzÄdnej
     * na okreĹlonÄ wartoĹÄ.
     *
     * @param dimension numer wymiaru. Dowolone sÄ liczby
     *                  od 0 do liczba wymiarĂłw - 1
     * @param value nowa wartoĹÄ dla wspĂłĹrzÄdnej dimention
     */
    public void setPosition( int dimension, double value ) {
        position[ dimension ] = value;
    }

    /**
     * Za pomocÄ tej metody moĹźliwe jest pobranie informacji o
     * wartoĹci okreĹlonej wspĂłĹrzÄdnej.
     * @param dimension numer wymiaru. Dozwolone sÄ liczby
     *                  od 0 do liczba wymiarĂłw - 1
     * @return wartoĹÄ wspĂłĹrzÄdnej
     */
    public double getPosition( int dimension ) {
        return position[ dimension ];
    }
}