/**
 * 
 */
package gitlet;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * @author william
 *
 */
public class Index extends GitletObject {

    /**
     * Serialization ID for gitlet.
     */
    private static final long serialVersionUID = -8630621570840209428L;
    
    /**
     * A map of filename-sha1.
     */
    private boolean changed = false;
    
    /**
     * Gets a list of blobs.
     */
    private HashMap<String, String> blobs;
    
    
    private TreeMap<String, String> modified;
    private TreeMap<String, String> added;
    private TreeMap<String, String> staged;
    private TreeMap<String, String> removed;
    /**
     * Creates a gitlet index.
     */
    public Index(){
        blobs = new HashMap<String, String>();
        removed = new TreeMap<>();
        added = new TreeMap<>();
        modified = new TreeMap<>();
        staged = new TreeMap<>();
    }
    
    /**
     * Gets the blob from the stage and clears the staging area.
     * @return A hashmap of filenames to blobs.
     */
    public HashMap<String, String> blobsFromStage(){
        if(!changed)
            throw new IllegalStateException("No changes added to the commit.");
 
        changed = false;
        modified.clear();
        removed.clear();
        added.clear();
        staged.clear();
        
        
        return blobs;
    }
    
    /**
     * Non destructiveley gets the tracked blobs.
     * @return
     */
    public HashMap<String,String> getBlobs(){
        return blobs;
    }
    
    
    /**
     * Adds a blob to the staging area.
     * @param fileName The file name of the blob.
     * @param hash The hash of the blob.
     */
    public void add(String fileName, String hash){
        if(blobs.containsKey(fileName)){
            if(!this.blobs.get(fileName).equals(hash))
                this.modified.put(fileName, hash);
            else
                return;
        }
        else
            this.added.put(fileName, hash);
        
        this.staged.put(fileName, hash);
        
        this.changed = true;
        this.blobs.put(fileName, hash);
        
    }
    
    /**
     * Untracks a file.
     * @param fileName
     */
    public void remove(String fileName, boolean fromLastCommit){
        if(!this.blobs.containsKey(fileName))
            throw new IllegalStateException("No reason to remove the file.");
        
        
        this.changed = true;
        if(fromLastCommit)
            this.removed.put(fileName, this.blobs.get(fileName));
        this.staged.remove(fileName);
        this.added.remove(fileName);
        this.modified.remove(fileName);
        this.blobs.remove(fileName);
    }

    /**
     * @return the changed
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * @return the modified
     */
    public TreeMap<String, String> getModified() {
        return modified;
    }
   
    /**
     * @return the removed
     */
    public TreeMap<String, String> getRemoved() {
        return removed;
    }
    
    /**
     * @return the union of removed and modified.
     */
    public TreeMap<String, String> getStaged(){
        return staged;
    }
    
    
    

}
