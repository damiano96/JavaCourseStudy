public class PMO_Null extends PMO_TestBaseClass {

    // metoda nie jest publiczna
    @MethodToStart(12)
    void no() {
        counter++;
    };

    // metoda przyjmuje int i String
    @MethodToStart(12)
    @StringParameter( "Bombelek")
    void no2( int i, String arg ) {
        counter++;
    };

    // metoda zablokowana
    @MethodToStart(12)
    @MethodDisabled
    public void no2() {
        counter++;
    };

    // brak adnotacji MethodToStart
    @StringParameter("zielenina")
    public void no2(String str) {
        counter++;
    };

}
