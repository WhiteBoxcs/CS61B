/**
 * 
 */
package gitlet;

import java.util.HashMap;

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
    
    
    private HashMap<String, String> modified;
    private HashMap<String, String> added;
    private HashMap<String, String> removed;
    /**
     * Creates a gitlet index.
     */
    public Index(){
        blobs = new HashMap<String, String>();
        removed = new HashMap<>();
        added = new HashMap<>();
        modified = new HashMap<>();
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
        this.changed = true;
        this.blobs.put(fileName, hash);
        
    }
    
    /**
     * Untracks a file.
     * @param fileName
     */
    public void remove(String fileName){
        if(!this.blobs.containsKey(fileName))
            throw new IllegalStateException("No reason to remove the file.");
        
        
        this.changed = true;
        this.removed.put(fileName, this.blobs.get(fileName));
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
    public HashMap<String, String> getModified() {
        return modified;
    }

    /**
     * @return the removed
     */
    public HashMap<String, String> getRemoved() {
        return removed;
    }
    
    

}
