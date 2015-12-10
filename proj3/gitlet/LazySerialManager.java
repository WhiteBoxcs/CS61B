/**
 *
 */
package gitlet;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    private HashMap<String, Serializable> loadedObjects;
    /**
     * List of all objects that could possibly be loaded.
     */
    protected HashMap<Class<?>, Set<String>> tracker;
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
        this.loadedObjects = new HashMap<>();
        this.tracker = new LinkedHashMap<>();
    }

    /**
     * Gets a serial object.
     * @param file
     *            The file name of the object.
     * @return The object.
     */
    public <S extends T> S get(Class<S> type, String file) {
        try {
            Serializable obj = this.loadedObjects.get(file);
            if (obj == null) {
                return this.load(type, file);
            } else {
                return type.cast(obj);
            }
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

        if (this.loadedObjects.get(file) != null
                || this.loadUnsafe(file) != null) {
            throw new IllegalStateException(toAdd.getClass().getSimpleName()
                    + " as specified already exists.");
        }
        this.loadedObjects.put(file, toAdd);
        Set<String> tracked = this.tracker.get(toAdd.getClass());
        if (tracked == null) {
            tracked = new LinkedHashSet<String>();
            this.tracker.put(toAdd.getClass(), tracked);
        }
        tracked.add(file);
    }

    /**
     * Determines if the lazy serial manager contains a file.
     * @param file
     *            The file to check.
     * @return If ti does contain the file.
     */
    public boolean contains(String file) {
        for (Class<?> type : this.tracker.keySet()) {
            if (this.contains(type, file)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the serial manager contains a given file.
     * @param type
     *            The type to check./
     * @param file
     *            The fuile name.
     * @return If it does.
     */
    public <S extends T> boolean contains(Class<?> type, String file) {
        Set<String> files = this.tracker.get(type);
        if (files == null) {
            return false;
        }

        return files.contains(file);
    }

    /**
     * Removes a file from the Lazy Serial manager.
     * @param type
     *            The tpe.
     * @param file
     *            The file.
     */
    public <S extends T> void remove(Class<S> type, String file) {
        try {
            Path filePath = this.baseDirectory.resolve(file);

            if (!this.tracker.containsKey(type)
                    || !this.tracker.get(type).contains(file)) {
                throw new IllegalArgumentException(type.getSimpleName()
                        + " as specified does not exist.");
            }

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            this.tracker.get(type).remove(file);
            this.loadedObjects.remove(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract boolean niceSerialization();

    /**
     * Opens a lazy serial manager.
     */
    public void open() {
        this.open = true;

        if (!Files.exists(this.getBaseDirectory())) {
            try {
                Files.createDirectories(this.getBaseDirectory());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @SuppressWarnings("unchecked")
        HashMap<Class<?>, Set<String>> trck =
                (HashMap<Class<?>, Set<String>>) this.loadUnsafe(DB_NAME);

        if (trck == null) {
            this.loadedObjects.put(DB_NAME, this.tracker);
        } else {
            this.tracker = trck;
        }

    }

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
            this.save(DB_NAME, this.tracker);
            this.loadedObjects.forEach((file, obj) -> this.save(file, obj));

        }
    }

    /**
     * @return the baseDirectory
     */
    public Path getBaseDirectory() {
        return this.baseDirectory;
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
        if (!this.tracker.containsKey(type)) {
            String name = type.getSimpleName();
            throw new IllegalStateException(
                    "No " + name.toLowerCase() + "s exist.");
        }
        this.tracker.get(type).forEach(action);
    }

    /**
     * Performs a for each on an object of a certain type.
     * @param type
     *            The type of the object.
     * @param action
     *            The action.
     */
    public <S extends T> void forEach(Class<S> type,
            final BiConsumer<? super String, ? super S> action) {
        if (!this.tracker.containsKey(type)) {
            String name = type.getSimpleName();
            throw new IllegalStateException(
                    "No " + name.toLowerCase() + "s exist.");
        }
        this.tracker.get(type)
                .forEach(file -> action.accept(file, this.load(type, file)));
    }

    /**
     * Gets an iterator for the lazy serial file manager.
     */
    @Override
    public Iterator<T> iterator() {
        List<Iterator<String>> iterators = new ArrayList<Iterator<String>>();
        this.tracker.forEach((type, files) -> iterators.add(files.iterator()));

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
            return this.fileNameIter.hasNext();
        }

        /*
         * (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        @Override
        public T next() {
            return LazySerialManager.this.loadUnsafe(this.fileNameIter.next());
        }

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

            Object unsafe;
            if (this.niceSerialization()) {
                XMLDecoder e = new XMLDecoder(oin);
                unsafe = e.readObject();
                e.close();
            } else {
                unsafe = oin.readObject();
            }

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

            Object unsafe;
            if (this.niceSerialization()) {
                XMLDecoder e = new XMLDecoder(oin);
                unsafe = e.readObject();
                e.close();
            } else {
                unsafe = oin.readObject();
            }

            T loaded = (T) unsafe;

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
            if (this.niceSerialization()) {
                XMLEncoder e = new XMLEncoder(oin);
                e.writeObject(object);
                e.close();
            } else {
                oin.writeObject(object);
            }
            oin.close();
            fin.close();

        } catch (IOException i) {
            i.printStackTrace();
        }
    }

}
