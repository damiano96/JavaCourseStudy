public class PMO_TestBaseClass {
    protected static int counter;
    protected static String argument;

    public static boolean getExecutionFlag() {
        return counter>0;
    }

    public static int getCounter() {
        return counter;
    }

    public static String getArgument() {
        return argument;
    }

    public static void clear() {
        counter = 0;
        argument = null;
    }
}
