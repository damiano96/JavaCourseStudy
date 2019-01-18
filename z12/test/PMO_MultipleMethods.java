public class PMO_MultipleMethods extends PMO_TestBaseClass {

    @MethodToStart(10)
    public void nothing() {
        counter++;
    }

    @StringParameter("nicCiekawego")
    @MethodToStart(20)
    public void nothing2( String arg ) {
        argument = arg;
        counter++;
    }

}
