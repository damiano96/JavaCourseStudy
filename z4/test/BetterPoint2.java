import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class BetterPoint extends AbstractBetterPoint {
    public int dimensions;
    public List<String> passwords = new ArrayList<>();
    public Map<Integer, Double> coordinates = new HashMap<>();

    BetterPoint(){}

    @Override
    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
        for(int i=0; i<dimensions; i++) {
            coordinates.put(i, 0.0);
        }
    }

    @Override
    public int lockLevel() {
        return passwords.size();
    }

    @Override
    public int lock(String password) {
        if(password == null) return passwords.size();
        else {
            passwords.add(password);
            return passwords.size();
        }
    }

    @Override
    public int unlock(String password) {
        if(password == null) return passwords.size();
        if(!password.equals(passwords.get(passwords.size()-1))) return passwords.size();
        else {
            passwords.remove(password);
            return passwords.size();
        }
    }

    @Override
    public boolean move(int dimension, double delta) {
        if(delta == 0) return false;
        if(this.lockLevel() != 0) return false;
        if(dimension >= dimensions) return false;
        else {
            coordinates.replace(dimension, coordinates.get(dimension)+delta);
            return true;
        }
    }

    @Override
    public boolean set(int dimension, double value) {
        if(this.lockLevel() != 0) return false;
        if(dimension >= dimensions) return false;
        else {
            coordinates.put(dimension,value);
            return true;
        }
    }

    @Override
    public double get(int dimension) {
        return coordinates.get(dimension);
    }

    public static void main(String args[]) {
        BetterPoint b = new BetterPoint();
        b.setDimensions(3);

        b.set(0,7.0);
        b.move(0,4);

        System.out.println(b.get(0));

    }
}
