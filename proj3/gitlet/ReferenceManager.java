/**
 * 
 */
package gitlet;

import java.nio.file.Path;

/**
 * @author william
 *
 */
public class ReferenceManager extends LazySerialManager<Reference> {
    
    public ReferenceManager(Path base) {
        super(base);
    }
    
    public String add(ReferenceType type, String fileName, String target){
        this.add(type.getBaseDir() + fileName, new Reference(target));
        return target;
    }
    
    public String add(ReferenceType type, String target){
        return this.add(type, type.toString(), target);
    }
    
    public String get(ReferenceType type, String fileName, String target){
        return null;
    }
    

}
