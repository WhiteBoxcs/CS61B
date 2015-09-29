/** Functions to increment and sum the elements of a WeirdList. */
class WeirdListClient {

  /** Return the result of adding N to each element of L. */
  static WeirdList add(WeirdList L, int n) {
    return L.map(x -> x + n);
  }

  /** Return the sum of the elements in L. */
  static int sum(WeirdList L) {
    Accumulator sum = new Accumulator(0);

    L.map(sum);
    return sum.result();
  }

  /*
   * As with WeirdList, you'll need to add an additional class or perhaps more for WeirdListClient
   * to work. Again, you may put those classes either inside WeirdListClient as private static
   * classes, or in their own separate files.
   * 
   * You are still forbidden to use any of the following: if, switch, while, for, do, try, or the ?:
   * operator.
   */

}
