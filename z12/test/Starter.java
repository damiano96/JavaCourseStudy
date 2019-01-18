import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class Starter implements Consumer<String> {

    private MethodToStart findStartMethod(List<Annotation> annotationList) {
        final MethodToStart[] methodToStart = {null};
        annotationList.forEach(v-> {
            if (v instanceof MethodToStart) {
                methodToStart[0] = (MethodToStart) v;
            }
        });
        return methodToStart[0];
    }

    private StringParameter findStringParametr(List<Annotation> annotationList) {
        final StringParameter[] stringParameter = {null};
        annotationList.forEach(v-> {
            if (v instanceof StringParameter) {
                stringParameter[0] = (StringParameter) v;
            }
        });
        return stringParameter[0];
    }
    boolean chceckIfNotDisabled(List<Annotation> annotations) {
        boolean booleanTesting = false;
        for (Annotation a : annotations) {
            if (!(a instanceof MethodDisabled)) {
                booleanTesting = true;
            } else {
                return false;
            }
        }
        return booleanTesting;
    }

    @Override
    public void accept(String s) {
        try {
            Class newObject = Class.forName(s);
            Method[] ms = newObject.getMethods();

            for(Method m: ms) {
                List<Annotation> annotationList = new ArrayList<>(Arrays.asList(m.getAnnotations()));


                if(chceckIfNotDisabled(annotationList)) {
                    if(annotationList.size() == 1) {
                        if (annotationList.get(0) instanceof MethodToStart) {
                            MethodToStart methodToStart = findStartMethod(annotationList);
                            @SuppressWarnings("unchecked")
                            Constructor constructor = newObject.getConstructor();
                            Object n = constructor.newInstance();
                            for (int i = 0; i < methodToStart.value(); i++) m.invoke(n);
                        }
                    } else if(annotationList.size() == 2){
                        MethodToStart methodToStart = findStartMethod(annotationList);
                        StringParameter stringParameter = findStringParametr(annotationList);
                        @SuppressWarnings("unchecked")
                        Constructor<?> constructor = newObject.getConstructor();
                        Object n = constructor.newInstance();
                        for(int i = 0; i< Objects.requireNonNull(methodToStart).value(); i++) {
                            m.invoke(n, stringParameter.value());
                        }
                    }

                }



            }

        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
            e.printStackTrace();
        }

    }

    public static void main(String args[]) {

    }
}
