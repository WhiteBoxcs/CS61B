import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 * Graph searching utlity.
 * @author
 */
public class Search {

    /**
     * Return a shortest path from vertex START to vertex END in G. The edge
     * labels of G are the lengths of the edges (all non-negative). The vertex
     * labels are 3-element arrays whose first 2 elements contain the
     * coordinates of the vertices. The third element in each vertex label is
     * unused (and may be modified by shortestPath) The Euclidean distance
     * between vertices joined by an edge is guaranteed to be less than or
     * equal to the length of the edge. The path is returned as a list of the
     * vertices along the path, with the first being START and the last being
     * LAST.
     */
    public static List<Integer> shortestPath(final Graph<double[], Double> G,
            int start, int end) {

        // Set up:
        final int INFINITY = Integer.MAX_VALUE;// The infinity value

        if (!G.contains(start) || !G.contains(end))
            throw new UnsupportedOperationException(
                    "The graph does not contain either the starting vertex or the goal vertex");

        // Set evertything to 0.
        Stack<Integer> vStack = new Stack<Integer>();
        PriorityQueue<Integer> verts = new PriorityQueue<Integer>((x,
                y) -> (int) Math.signum(G.getLabel(y)[2] - G.getLabel(x)[2]));
        HashSet<Integer> visited = new HashSet<Integer>();
        LinkedList<Integer> shortestPath = new LinkedList<Integer>();

        G.apply((vert, label) -> {
            label[2] = Double.MAX_VALUE;
            verts.add(vert);
        });

        G.getLabel(start)[2] = 0;
        verts.add((start));

        while (!verts.isEmpty()) {
            Integer cur = verts.poll();
            double[] curLabel = G.getLabel(cur);
            if (curLabel[2] == Double.MAX_VALUE)
                continue;

            for (Integer suc : G.successors(cur)) {
                if (!verts.contains(suc)) {
                    double[] sucLabel = G.getLabel(suc);
                    double edgeLabel = G.getLabel(cur, suc);

                    double dist = edgeLabel + curLabel[2];
                    if (sucLabel[2] > dist) {
                        verts.remove(suc);
                        sucLabel[2] = dist;
                        verts.add(suc);
                    }
                }
            }
        }

    }

}
