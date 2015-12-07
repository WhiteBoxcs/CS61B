package gitlet;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Repository {

    private boolean open = false;
    private HashMap<String, Serializable> loadedObjects;
    private Path workingDir;
    private Path gitletDir;

    public Repository(String workingDir) {
        this.workingDir = Paths.get(workingDir);
        this.gitletDir = this.workingDir.resolve(".gitlet");
        
        if(Files.exists(gitletDir))
            open();
        
        loadedObjects = new HashMap<>();
    }

    /**
     * Initializes a repository if one does not already exist there.
     */
    public void init(){
        if(isOpen())
            throw new IllegalStateException("A gitlet version-control system already exists in the current directory.");
        
    }
    
    
    /**
     * Opens a repository if the repository failed to open in the first place.
     */
    public void open(){
        if(isOpen())
            throw new IllegalStateException("Close repository before opening a new instance.");
    }
    
    /**
     * Closes a repository and serializes every loaded object.
     */
    public void close() {
        if(this.isOpen()){
            this.open = false;
            
            for(Map.Entry<String, Serializable> loaded
                    : loadedObjects.entrySet()){
                
            }
        }    
    }
    
    public boolean isOpen() {
        // TODO Auto-generated method stub
        return open;
    }


}
