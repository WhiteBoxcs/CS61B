/**
 * The accumulator class.
 *
 * @author MadcowD
 *
 */
public class Accumulator implements IntUnaryFunction {
  private int sum = 0;

  public Accumulator(int initial) {
    this.sum = initial;
  }

  @Override
  public int apply(int x) {
    this.sum += x;
    // TODO Auto-generated method stub
    return x;
  }

  public int result() {
    return this.sum;
  }
}