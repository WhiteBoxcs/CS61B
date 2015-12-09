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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import gitlet.LazySerialManager.LoadingIterator;

/**
 * @author william Represents a general file object manager.
 */
public abstract class LazySerialManager<T extends Serializable>
        implements Iterable<T> {

    /**
     * The database name.
     */
    private static final String DB_NAME = "SERIAL";

    /**
     * All loaded objects.
     */
    private HashMap<String, T> loadedObjects;

    /**
     * List of all objects that could possibly be loaded.
     */
    private HashMap<Class<?>, List<String>> tracker;

    /** Base file object directory */
    private Path baseDirectory;

    /**
     * If the Lazy serial manager is open.
     */
    private boolean open;

    /**
     * Builds a lazy serial manager.
     * @param base
     *            The base directory.
     */
    public LazySerialManager(Path base) {
        this.baseDirectory = base;
        this.open = false;
    }

    /**
     * Gets a serial object.
     * @param file
     *            The file name of the object.
     * @return The object.
     */
    public <S extends T> S get(Class<S> type, String file) {
        try {
            S obj = type.cast(loadedObjects.get(file));
            if (obj == null)
                return load(type, file);
            else
                return obj;
        } catch (ClassCastException e) {
            String name = type.getSimpleName();
            throw new IllegalArgumentException(
                    name + " as specified does not exist.");
        }
    }

    /**
     * Adds an object to the lazy serial store.
     * @param file
     * @param toAdd
     */
    public <S extends T> void add(String file, S toAdd) {
        this.loadedObjects.put(file, toAdd);
        List<String> tracked = this.tracker.get(toAdd.getClass());
        if (tracked == null) {
            tracked = new ArrayList<String>();
            this.tracker.put(toAdd.getClass(), tracked);
        }
        tracked.add(file);
    }

    /**
     * Loads an object into the lazy cache.
     * @param file
     * @return
     */
    private <S extends T> S load(Class<S> type, String file)
            throws ClassCastException {
        Path filePath = this.baseDirectory.resolve(file);
        try {
            InputStream fin = Files.newInputStream(filePath);
            ObjectInputStream oin = new ObjectInputStream(fin);

            Object unsafe = oin.readObject();

            S loaded = type.cast(unsafe);

            oin.close();
            fin.close();

            this.loadedObjects.put(file, loaded);
            return loaded;

        } catch (IOException i) {
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Loads a file unsafeley.
     * @param file
     *            The file to load.
     * @return The loaded gile.
     */
    @SuppressWarnings("unchecked")
    private T loadUnsafe(String file) {
        Path filePath = this.baseDirectory.resolve(file);
        try {
            InputStream fin = Files.newInputStream(filePath);
            ObjectInputStream oin = new ObjectInputStream(fin);

            Object unsafe = oin.readObject();

            T loaded = (T) (unsafe);

            oin.close();
            fin.close();

            this.loadedObjects.put(file, loaded);
            return loaded;

        } catch (IOException i) {
            return null;
        } catch (ClassNotFoundException e) {
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
    private void save(String file, Object object) {
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
    @SuppressWarnings("unchecked")
    public void open() {
        this.open = true;
        Path dbPath = baseDirectory.resolve(DB_NAME);
        if (Files.exists(dbPath)) {
            try {
                InputStream fin = Files.newInputStream(dbPath);
                ObjectInputStream oin = new ObjectInputStream(fin);

                Object unsafe = oin.readObject();
                
                this.tracker = (HashMap<Class<?>, List<String>>)
                        (unsafe);

                oin.close();
                fin.close();
                

            } catch (IOException i) {
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else
            this.tracker = new HashMap<>();

    }

    /** Returns if the repository has been opened. */
    /**
     * Returns if the serial manager is open.
     * @return If the manager is open.
     */
    public boolean isOpen() {
        return this.open;
    }

    /**
     * Closes a repository and serializes every loaded object.
     */
    public void close() {
        if (this.isOpen()) {
            this.open = false;
            save(DB_NAME, this.tracker);
            loadedObjects.forEach((file, obj) -> save(file, obj));

        }
    }

    /**
     * Performs an action for every file of object of type TYPE.
     * @param type
     *            The object type.
     * @param action
     *            The action.
     */
    public <S extends T> void lazyForEach(Class<S> type,
            Consumer<? super String> action) {
        if (!tracker.containsKey(type)) {
            String name = type.getSimpleName();
            throw new IllegalStateException(
                    "No " + name.toLowerCase() + "s exist.");
        }
        tracker.get(type).forEach(action);
    }

    @Override
    public Iterator<T> iterator() {
        List<Iterator<String>> iterators = new ArrayList<Iterator<String>>();
        tracker.forEach((type, files) -> iterators.add(files.iterator()));

        List<Iterator<T>> fileIterators =
                iterators.stream().map(x -> this.new LoadingIterator(x))
                        .collect(Collectors.toList());

        return new ConcatIterator<>(fileIterators);
    }

    /**
     * Represents an iterater which loads files as they occur.
     * @author william
     */
    public class LoadingIterator implements Iterator<T> {
        /**
         * The file name iterator over which the loading iterator iterates.
         */
        private Iterator<String> fileNameIter;

        /**
         * Creates a loading iterator.
         * @param x
         *            The file name iterator.
         */
        public LoadingIterator(Iterator<String> x) {
            this.fileNameIter = x;
        }

        /*
         * (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            return fileNameIter.hasNext();
        }

        /*
         * (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        @Override
        public T next() {
            return LazySerialManager.this.loadUnsafe(fileNameIter.next());
        }

    }

}
