import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

/* Do NOT modify. */

/** A finite, mutable Graph that is stored in memory.
 *  @author P. N. Hilfinger
 */
public class SimpleGraph<VLabel, ELabel> implements Graph<VLabel, ELabel> {

    /** A Graph whose vertices are all <= MAXVERTEX.  If the graph is
     *  to have edge labels, MAXVERTEX**2 should be <=
     *  Integer.MAX_VALUE or the results may be undefined.  */
    SimpleGraph(int maxVertex) {
        _maxVertex = maxVertex;
    }

    @Override
    public void add(Integer v) {
        if (v < 0 || v > _maxVertex) {
            throw new IllegalArgumentException("bad vertex");
        }
        if (_successors.get(v) == null) {
            _successors.put(v, new ArrayList<>());
            _predecessors.put(v, new ArrayList<>());
        }
    }

    @Override
    public void add(Integer v1, Integer v2) {
        add(v1);
        add(v2);
        _successors.get(v1).add(v2);
        _predecessors.get(v2).add(v1);
    }

    @Override
    public boolean contains(Integer v) {
        return _predecessors.get(v) != null;
    }

    @Override
    public boolean contains(Integer v1, Integer v2) {
        return contains(v1) && successors(v1).contains(v2);
    }

    @Override
    public List<Integer> successors(Integer v) {
        return _successors.get(v);
    }

    @Override
    public List<Integer> predecessors(Integer v) {
        return _predecessors.get(v);
    }

    @Override
    public void addLabel(Integer v, VLabel label) {
        _vlabels.put(v, label);
    }

    @Override
    public void addLabel(Integer v1, Integer v2, ELabel label) {
        _elabels.put(v1 * (_maxVertex + 1) + v2, label);
    }

    @Override
    public VLabel getLabel(Integer v) {
        return _vlabels.get(v);
    }

    @Override
    public ELabel getLabel(Integer v1, Integer v2) {
        return _elabels.get(v1 * (_maxVertex + 1) + v2);
    }
    
    public void apply(VertexConsumer<VLabel> func){
        for(Entry<Integer, VLabel> label : _vlabels.entrySet())
            func.accept(label.getKey(), label.getValue());
    }

    /** Maximum vertex number. */
    private final int _maxVertex;
    /** Maps vertices to lists of predecessors. */
    private HashMap<Integer, ArrayList<Integer>> _predecessors
        = new HashMap<>();
    /** Maps vertices to lists of successors. */
    private HashMap<Integer, ArrayList<Integer>> _successors = new HashMap<>();
    /** Maps vertices to labels. */
    private HashMap<Integer, VLabel> _vlabels = new HashMap<>();
    /** Maps edges to labels. */
    private HashMap<Integer, ELabel> _elabels = new HashMap<>();

}
