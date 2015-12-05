import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/* Do not modify. */

/** A Graph whose vertices are non-negative Integers and whose edges
 *  are pairs of Integers. The vertices may be labeled with values of
 *  type VLABEL and the edges with values of type ELABEL.  Until set,
 *  all labels are null.
 *  @author P. N. Hilfinger
 */
public interface Graph<VLabel, ELabel> {

    /** Add vertex V to this Graph, if it not already present. V >= 0.
     *  Optional operation. */
    void add(Integer v);

    /** Add edge (V1, V2) to this Graph, adding vertices V1 and V2 if
     *  they are not present.  V1, V2 >= 0.  Optional operation. */
    void add(Integer v1, Integer v2);

    /** Return true iff V is one of my vertices. */
    boolean contains(Integer v);

    /** Return true iff I contain the edge (V1, V2). */
    boolean contains(Integer v1, Integer v2);

    /** Return the successors of vertex V. */
    List<Integer> successors(Integer v);

    /** Return the predecessors of vertex V. */
    List<Integer> predecessors(Integer v);

    /** Set the label of vertex V to LABEL, adding V if it is not
     *  currently present.  Optional operation. */
    void addLabel(Integer v, VLabel label);

    /** Set the label of the edge (V1, V2) to LABEL, adding those
     *  vertices and edge if not present. Optional operation. */
    void addLabel(Integer v1, Integer v2, ELabel label);

    /** Return the label of vertex V. */
    VLabel getLabel(Integer v);

    /** Return the label of the edge (V1, V2). */
    ELabel getLabel(Integer v1, Integer v2);
    
    /** Applys a function to every label. */
    void apply(VertexConsumer<VLabel> func);

}
