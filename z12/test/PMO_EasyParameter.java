public class PMO_EasyParameter extends PMO_TestBaseClass {

    @MethodToStart(2)
    @StringParameter("Pyra")
    public void pomidor( String arg ) {
        counter++;
        argument = arg;
    }
}
