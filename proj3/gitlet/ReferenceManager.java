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
    
    public Reference add(ReferenceType type, String fileName, Reference ref){
        try{
            this.add(type.getBaseDir() + fileName, ref);
        }
        catch(IllegalStateException e){
            throw new IllegalArgumentException("A " + type.toString().toLowerCase()
                    + " with that name already exists.");
        }
        return ref;
    }
    
    public Reference add(ReferenceType type, Reference ref){
        return this.add(type, type.toString(), ref);
    }
    
    public Reference get(ReferenceType type, String fileName){
         Reference ref = this.get(Reference.class, type.getBaseDir() + fileName);
         if(ref == null)
             throw new IllegalStateException("No such " 
                     + type.toString().toLowerCase() + " exists.");
         return ref;
             
    }
    
    public Reference get(ReferenceType type){
        return this.get(type, type.toString());
    }
    
    public String resolve(ReferenceType type, String fileName){
        Reference cur =  this.get(Reference.class, type.getBaseDir() + fileName);
        while(cur.targetIsReference()){
            cur = this.get(Reference.class, cur.target());
        }
        
        return cur.target();
        
    }
    
    public String resolve(ReferenceType type){
        return this.resolve(type, type.toString());
    }
    
    public void remove(ReferenceType type, String fileName){
        try{
            this.remove(Reference.class, type.getBaseDir() + fileName);
        } catch(IllegalArgumentException e){
            throw new IllegalArgumentException("A "
                    + type.toString().toLowerCase()
                    + "with that name does not exist.");
        }
    }

}
