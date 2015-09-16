import java.util.Arrays;
import java.awt.Color;

/** 
 *  @author Josh Hug
 */

public class ImageRescaler {
    private Picture pic;

    public ImageRescaler(Picture pic) {
        this.pic = new Picture(pic);
    }

    public int width() {
        return pic.width();
    }

    public int height() {
        return pic.height();
    }

    private double energy(int x, int y) {
        if ((x < 0) || (x >= pic.width()) || (y < 0) || (y > pic.height())) {
            return Double.POSITIVE_INFINITY;
        }

        if ((x == 0) || (x == pic.width() - 1) 
         || (y == 0) || (y == pic.height() - 1)) {
            return 1000000; 
    }

    Color left  = pic.get(x - 1, y);
    Color right = pic.get(x + 1, y);
    Color up    = pic.get(x, y + 1);
    Color down  = pic.get(x, y - 1);

    return calculateGradient(left, right) + calculateGradient(up, down);

}

    // Calculates the square of the (R,G,B) gradient between two pixels (p1,p2)
private static double calculateGradient(Color p1, Color p2) {
    double r = p1.getRed() - p2.getRed();
    double g = p1.getGreen() - p2.getGreen();
    double b = p1.getBlue() - p2.getBlue();

    return r*r + g*g + b*b;
}



public double[][] energyMatrix() {
    double[][] energyMatrix = new double[height()][width()];
    for (int c = 0; c < width(); c++)
        for (int r = 0; r < height(); r++)
            energyMatrix[r][c] = energy(c, r);

        return energyMatrix;
    }

    private static double get(double[][] m, int r, int c) {
        int width = m[0].length;
        int height = m.length;
        if ((c < 0) || (c >= width) || (r < 0) || (r >= height))
            return Double.POSITIVE_INFINITY;
        return m[r][c];
    }

    public void removeRow() {
        double[][] em = energyMatrix();
        double[][] cem = MatrixUtils.accumulate(em, 
           MatrixUtils.Orientation.HORIZONTAL);
        int[] hseam = MatrixUtils.findSeam(cem, 
          MatrixUtils.Orientation.HORIZONTAL);
        removeHorizontalSeam(hseam);

    }

    public void removeColumn() {
        double[][] em = energyMatrix();
        double[][] cem = MatrixUtils.accumulate(em, 
           MatrixUtils.Orientation.VERTICAL);
        int[] hseam = MatrixUtils.findSeam(cem, 
          MatrixUtils.Orientation.VERTICAL);
        removeVerticalSeam(hseam);
    }

    /* Remove specified vertical SEAM from picture */
    public void removeVerticalSeam(int[] seam)   
    {
        // Get rid of the pixels with coordinates (seam[r], r)
        Picture resizedPic = new Picture(width()-1, height());
        for (int r = 0; r < height(); r++)
        {
            int k = 0;
            for (int c = 0; c < width(); c++)
            {
                if (c == seam[r])
                    continue;

                resizedPic.set(k++, r, pic.get(c, r));
            }
        }

        pic = resizedPic;
    }

    /** Transposes the image inside this ImageRescaler */
    private void transpose() {
        Picture transposed = new Picture(pic.height(), pic.width());

        for (int c = 0; c < pic.width(); c++)
        {
            for (int r = 0; r < pic.height(); r++)
                transposed.set(r, c, pic.get(c, r));
        }

        pic = transposed;
    }


    /* Remove specified horizontal SEAM from picture */

    public void removeHorizontalSeam(int[] seam)  
    {
        this.transpose();
        removeVerticalSeam(seam);
        this.transpose();
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage:\njava ImageRescaler [image filename] [num rows to remove] [num columns to remove]");
            return;
        }

        Picture inputImg = new Picture(args[0]);
        inputImg.show();
        int removeColumns = Integer.parseInt(args[1]);
        int removeRows = Integer.parseInt(args[2]); 

        System.out.printf("Original image is %d columns by %d rows\n", 
           inputImg.width(), inputImg.height());

        ImageRescaler sc = new ImageRescaler(inputImg);

        for (int i = 0; i < removeColumns; i++) {
            sc.removeColumn();
        }

        for (int i = 0; i < removeRows; i++) {
            sc.removeRow();
        }

        System.out.printf("New image size is %d columns by %d rows\n", 
            sc.width(), sc.height());


        inputImg.show();
        sc.pic.show();    
    }
} 