/**
 * 
 */
package gitlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author william
 * Represents a general file object manager.
 */
public abstract class LazySerialManager<T extends Serializable> implements Iterable<T> {
    /**
     * All loaded objects.
     */
    private HashMap<String, T> loadedObjects;
    
    /** Base file object directory */
    private Path baseDirectory;

    /**
     * If the Lazy serial manager is open.
     */
    private boolean open;
   
    /**
     * Builds a lazy serial manager.
     * @param base The base directory.
     */
    public LazySerialManager(Path base){
        this.baseDirectory = base;
        this.open = false;
    }
    
    /**
     * Gets a serial object.
     * @param file The file name of the object.
     * @return The object.
     */
    public <S extends T> S get(Class<S> type, String file){
        try{
            S obj = type.cast(loadedObjects.get(file));
            if(obj == null)
                return load(type, file);
            else
                return obj;
        } catch(ClassCastException e){
            e.printStackTrace();
            return null;
        }
    }
    
    
    /**
     * Loads an object into the lazy cache.
     * @param file
     * @return
     */
    private <S extends T> S load(Class<S> type, String file) {
        Path filePath = this.baseDirectory.resolve(file);
        try {
            InputStream fin = Files.newInputStream(filePath);
            ObjectInputStream oin = new ObjectInputStream(fin);
            @SuppressWarnings("unchecked")
            Object unsafe;
           
            unsafe = oin.readObject();
            
            
            S loaded = type.cast(unsafe);

            oin.close();
            fin.close();

            this.loadedObjects.put(file, loaded);
            return loaded;
            
        } catch (IOException i) {
            return null;
        } catch (ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Saves a serilizable object object.
     * @param file
     *            The file name/relative path.
     * @param object
     *            The object to save.
     */
    private void save(String file, T object) {
        Path filePath = this.baseDirectory.resolve(file);
        try {
            if (!Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }
            OutputStream fin = Files.newOutputStream(filePath);
            ObjectOutputStream oin = new ObjectOutputStream(fin);
            oin.writeObject(object);
            oin.close();
            fin.close();

        } catch (IOException i) {
            i.printStackTrace();
        }
    }   

    /**
     * Opens a lazy serial manager.
     */
    public void open() {
        this.open = true;
    }

    /** Returns if the repository has been opened. */
    public boolean isOpen() {
        return this.open;
    }
    
    /**
     * Closes a repository and serializes every loaded object.
     */
    public void close() {
        if (this.isOpen()) {
            this.open = false;
            
            loadedObjects.forEach((file, obj) -> save(file,obj));
        }
    }
    
}
