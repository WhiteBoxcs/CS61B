/**
 *
 */
package gitlet;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * @author william
 */
public class GitletObjectManager extends LazySerialManager<GitletObject> {

    private static final int DIR_DELIM = 2;

    public GitletObjectManager(Path base) {
        super(base);
    }

    @Override
    public <S extends GitletObject> S get(Class<S> type, String hash) {
        return super.get(type, hashToFile(hash));
    }

    /**
     * Performs a foreach on a hashed object type.
     * @param type
     *            The type of hashed object.
     * @param action
     *            The action.
     */
    @Override
    public <S extends GitletObject> void forEach(Class<S> type,
            BiConsumer<? super String, ? super S> action) {
        BiConsumer<? super String, ? super S> hashedAction = (file, com) -> {
            action.accept(fileToHash(file), com);
        };
        super.forEach(type, hashedAction);
    };

    @Override
    public <S extends GitletObject> boolean contains(Class<?> type,
            String hash) {
        return super.contains(type, hashToFile(hash));
    }

    /**
     * Puts a gitlet object overwriting if necisarry.
     * @param obj
     *            The object to add.
     * @return The hash of the object.
     */
    public String put(GitletObject obj) {
        String hash = obj.sha1();
        if (!this.contains(hash)) {
            this.add(hashToFile(hash), obj);
        }
        return hash;
    }

    /**
     * Adds all gitlet objects of a certain type.
     * @param objs
     */
    public void putAll(Collection<? extends GitletObject> objs) {
        for (GitletObject q : objs) {
            this.put(q);
        }
    }

    /**
     * Removes a hash from the object store.
     */
    @Override
    public <S extends GitletObject> void remove(Class<S> type, String hash) {
        super.remove(type, hashToFile(hash));
    }

    /**
     * Uses the speed of the file system to load an object of a given type
     * satisfying the search.
     * @param type
     *            The type to search for.
     * @param search
     *            The delimiter with which to search.
     * @return The gitlet objecto find.
     */
    public <S extends GitletObject> S find(Class<S> type, String search) {
        Set<String> contents = this.tracker.get(type);
        if (contents == null || contents.isEmpty()) {
            return null;
        }

        String delim =
                search.substring(0, Math.min(search.length(), DIR_DELIM));

        String rest = "";
        if (search.length() > DIR_DELIM) {
            rest = search.substring(DIR_DELIM, search.length());
        }

        Path base = this.getBaseDirectory();
        try (DirectoryStream<Path> str =
                Files.newDirectoryStream(base, x -> Files.isDirectory(x))) {

            for (Path entry : str) {
                String directoryName = entry.getFileName().toString();

                if (directoryName.startsWith(delim)) {
                    DirectoryStream<Path> substr =
                            Files.newDirectoryStream(entry);

                    for (Path subEntry : substr) {
                        String fileName = subEntry.getFileName().toString();

                        if (fileName.startsWith(rest)) {
                            String targetHash = directoryName + fileName;
                            if (contents.contains(
                                    directoryName + "/" + fileName)) {
                                return this.get(type, targetHash);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converts a hash to a file path.
     * @param hash
     *            The hash to convert.
     * @return The file path.
     */
    private static String hashToFile(String hash) {
        return hash.substring(0, DIR_DELIM) + "/"
                + hash.substring(DIR_DELIM, hash.length());
    }

    /**
     * Converts a file name to a hash value.
     * @param file
     * @return
     */
    private static String fileToHash(String file) {
        return file.replaceFirst("/", "");
    }

    @Override
    protected boolean niceSerialization() {
        return false;
    }
}
