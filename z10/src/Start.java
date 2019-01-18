import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.zip.GZIPInputStream;

class MainApp extends JPanel {

    private Map<Integer, List<Coordinates>> animation;
    private List<Coordinates> coordinatesList;
    private int speed = 1;
    private double maxX = 0, maxY = 0, minY = 0, minX = 0;

    int getSpeed() {
        return speed;
    }
    void setSpeed(int speed) {
        this.speed = speed;
    }

    MainApp(Map<Integer, List<Coordinates>> animation ) {
        this.animation = animation;

        animation.forEach((k,v) -> {
            v.forEach(vv -> {
                this.maxX = Math.max(vv.getX(), maxX);
                this.minX = Math.min(vv.getX(), minX);
                this.maxY = Math.max(vv.getY(), maxY);
                this.minY = Math.min(vv.getY(), minY);
            });
        });
        coordinatesList = animation.get(0);
        repaint();
    }
    void runAnimation() {
        final int[] i = {0};
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if(i[0] > animation.size()-1) i[0] = 0;
                coordinatesList = animation.get(i[0]);
                repaint();
                i[0]+= getSpeed();
            }
        };
        timer.schedule(timerTask, 100, 100);
    }
    public void paint(Graphics gg) {
        Graphics2D g = (Graphics2D) gg;
        super.paint(g);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        try {
            for (Coordinates coordinates : coordinatesList) {
                g.draw(new Ellipse2D.Double((coordinates.getX() * (getWidth()-10) / ((maxX + minX))), (getHeight()-8) - (coordinates.getY() * (getHeight()-10) / ((maxY+minY))), 7, 7));
            }
        } catch (ConcurrentModificationException e) {}

        g.dispose();
    }
}

class Window extends JPanel implements ActionListener {
    private MainApp mainApp;
    private JButton slowButton;
    private JButton fastButton;
    private JLabel getInfoSpeed;

    private Map<Integer, List<Coordinates>> animation;

    Window(Map<Integer, List<Coordinates>> animation) {
        this.animation = animation;

        mainApp = new MainApp(animation);
        setLayout(new BorderLayout(5,5));
        JPanel left = new JPanel(new BorderLayout());
        JPanel right = new JPanel(new BorderLayout());
        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel();
        add(top, BorderLayout.PAGE_START);
        add(left, BorderLayout.WEST);
        add(right, BorderLayout.EAST);
        add(center, BorderLayout.CENTER);

        slowButton = new JButton("slower");
        fastButton = new JButton("faster");
        JLabel infoSpeed = new JLabel("Speed: ");
        getInfoSpeed = new JLabel(Integer.toString(mainApp.getSpeed()));

        slowButton.addActionListener(this);
        fastButton.addActionListener(this);

        top.add(infoSpeed);
        top.add(getInfoSpeed);
        left.add(slowButton);
        right.add(fastButton);
        center.add(mainApp);

        mainApp.runAnimation();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == slowButton) {
            int speed = mainApp.getSpeed();
            if(speed == 0) mainApp.setSpeed(speed);
            else mainApp.setSpeed(speed-1);
            getInfoSpeed.setText(Integer.toString(mainApp.getSpeed()));
        }
        if(e.getSource() == fastButton) {
            int speed = mainApp.getSpeed();
            if(speed == animation.size()-1) mainApp.setSpeed(speed);
            else mainApp.setSpeed(speed+1);
            getInfoSpeed.setText(Integer.toString(mainApp.getSpeed()));
        }
    }
}

class FileChosen extends JPanel implements ActionListener {
    private Map<Integer, List<Coordinates>> animation = new HashMap<>();
    private JFrame aplication2;
    FileChosen(JFrame aplication2) {
        this.aplication2 = aplication2;
        JButton openB = new JButton("Open");
        openB.addActionListener(this);
        add(openB);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                readFile(file);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            new Start().runMainApp(animation);
            aplication2.setVisible(false);
        }
    }

    public void readFile(File plik) throws IOException {
        BufferedReader odczyt = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(plik))));

        String line = odczyt.readLine();
        line = odczyt.readLine();
        int cMap = 0;
        while((line = odczyt.readLine()) != null){
            List<Coordinates> coordinates = new ArrayList<>();
            String[] split = line.split("\\s");
            for (int i = -1; i < split.length; i+=2) {
                if (i > -1) {
                    double x = Double.parseDouble(split[i]);
                    double y = Double.parseDouble(split[i+1]);
                    coordinates.add(new Coordinates(x,y));
                }
            }
            animation.put(cMap, coordinates);
            cMap++;
        }
    }
}

class Coordinates {
    private double x;
    private double y;

    Coordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}

public class Start {
    void runMainApp(Map<Integer, List<Coordinates>> animation) {
        JFrame aplication = new JFrame("Animations");
        JPanel panel = new Window(animation);
        panel.setPreferredSize(new Dimension(700, 700));
        aplication.setContentPane(panel);
        aplication.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        aplication.pack();
        aplication.setVisible(true);
    }

    public static void main(String args[]) {
        JFrame aplication2 = new JFrame("Animations");
        FileChosen file = new FileChosen(aplication2);
        aplication2.setContentPane(file);
        aplication2.setSize(300, 100);
        aplication2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        aplication2.setVisible(true);
    }

}
