/**
 * 
 */
package gitlet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author william
 *
 */
public class Reference implements Serializable {

    /**
     * The serial reference ID.
     */
    private static final long serialVersionUID = -1972946612858325631L;
    
    /**
     * The target of the reference.
     */
    private String target;
    
    /**
     * Constructs a reference with a target.
     * @param target The target.
     */
    public Reference(String target){
        this.target = target;
    }
    
    /**
     * Gets the target of this reference.
     * @return The target.
     */
    public String getTarget(){
        return target;
    }

    /**
     * Overloads serial write object.
     * @param out The output strea,/
     * @throws IOException An IO Exception.
     */
    private void writeObject(ObjectOutputStream out) throws IOException{
        out.writeObject(target.getBytes());
    }
    
    /**
     * Overloads the serializable read object.
     * @param in The input stream.
     * @throws IOException The IOException.
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException{
        this.target = in.readUTF();
        
    }
}
