package gitlet;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a Gitlet repository.
 * @author william
 *
 */
public class Repository {

    private boolean open = false;
    private HashMap<String, Serializable> loadedObjects;
    private Path workingDir;
    private Path gitletDir;

    private static final String COMMIT_DIR = "objects/commits/";
    private static final String BLOB_DIR = "objects/blobs/";
    private static final String REFHEAD_DIR = "refs/heads/";
    private static final String INDEX = "index";
    private static final String HEAD = "HEAD";
    
    
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

        try {
            Files.createDirectory(gitletDir);

            String initialCommit = this.addCommit(new Commit("initial commit", new Date()));
            
            
            open = true;
        } catch (IOException e) {
            System.out.println("Something went wrong while initializing the repository!");
            e.printStackTrace();
        }
        
    }
    
    
    public String addCommit(Commit commit) {
        String hash = commit.sha1();
        this.loadedObjects.put(COMMIT_DIR + hash, commit);
        return hash;
    }
    
    public Commit getCommit(String hash){
       return (Commit)this.load(COMMIT_DIR + hash);
    }
    
    public List<Commit> getAllCommits(){
        Path
    }

    private void addBranch(String name, Commit commit){
        
    }

    
    
    /**
     * Loads an object into the lazy cache.
     * @param file
     * @return
     */
    private Serializable load(String file) {
        Path filePath = gitletDir.resolve(file);
        try{
            InputStream fin = Files.newInputStream(filePath);
            ObjectInputStream oin = new ObjectInputStream(fin);
            Serializable loaded = (Serializable)oin.readObject();
            oin.close();
            fin.close();
            
            this.loadedObjects.put(file, loaded);
            return loaded;
        }catch(IOException i){
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }
    
    private void save(String file, Serializable object){
        Path filePath = gitletDir.resolve(file);
        try{
            if(!Files.exists(filePath.getParent()))
                Files.createDirectories(filePath.getParent());
            OutputStream fin = Files.newOutputStream(filePath);
            ObjectOutputStream oin = new ObjectOutputStream(fin);
            oin.writeObject(object);
            oin.close();
            fin.close();

        }catch(IOException i){
            i.printStackTrace();
        }
    }

    
    /**
     * Opens a repository if the repository failed to open in the first place.
     */
    public void open(){
        if(isOpen())
            throw new IllegalStateException("Close repository before opening a new instance.");
        this.open = true;
    }
    
    /**
     * Closes a repository and serializes every loaded object.
     */
    public void close() {
        if(this.isOpen()){
            this.open = false;
            
            for(Map.Entry<String, Serializable> loaded
                    : loadedObjects.entrySet()){
                this.save(loaded.getKey(), loaded.getValue());
            }
        }    
    }
    
    public boolean isOpen() {
        return open;
    }

    

    public Path getWorkingDir() {
        return workingDir;
    }

    public Path getGitletDir() {
        return gitletDir;
    }


}
