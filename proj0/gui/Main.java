package gui;

/** Display a sample GUI displaying points (as images) and lines between them,
 *  allowing user creation of points and lines and movement of points.
 *  @author P. N. Hilfinger */
public class Main {

    /** Display GUI and wait for termination. */
    public static void main(String... ignored) {
        SampleData data = new SampleData();
        SampleGUI gui = new SampleGUI("Bears and Lines", data);
    }

}
