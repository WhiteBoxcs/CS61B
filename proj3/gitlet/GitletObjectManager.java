/**
 *
 */
package gitlet;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * @author william
 */
public class GitletObjectManager extends LazySerialManager<GitletObject> {

    private static final int DIR_DELIM = 2;
    private static final String OBJ_DIR = "objects/";

    public GitletObjectManager(Path base) {
        super(base);
    }

    @Override
    public <S extends GitletObject> S get(Class<S> type, String hash) {
        return super.get(type, hashToFile(hash));
    }

    /**
     * Gets the object directly.
     * @param type
     *            The type of object.
     * @param file
     *            The file.
     * @return The boject.
     */
    public <S extends GitletObject> S getDirect(Class<S> type, String file) {
        return super.get(type, file);
    }

    /**
     * Performs a foreach on a hashed object type.
     * @param type The type of hashed object.
     * @param action The action.
     */
    @Override
    public <S extends GitletObject> void forEach(Class<S> type,
            BiConsumer<? super String, ? super S> action) {
        BiConsumer<? super String, ? super S> hashedAction =
                (file, com) -> {
                    if(file.startsWith(OBJ_DIR))
                        action.accept(fileToHash(file), com);
                    else
                        action.accept(file, com);
                };
        super.forEach(type, hashedAction);
    };

    /**
     * Adds a gitlet object.
     * @param obj
     *            The object to add.
     * @return The hash of the object.
     */
    public String add(GitletObject obj) {
        String hash = obj.sha1();
        this.add(hashToFile(hash), obj);
        return hash;
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

        Path base = this.getBaseDirectory().resolve(OBJ_DIR);
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
                            if (contents.contains(OBJ_DIR + directoryName + "/" + fileName)) {
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
        return OBJ_DIR + hash.substring(0, DIR_DELIM) + "/"
                + hash.substring(DIR_DELIM, hash.length());
    }

    /**
     * Converts a file name to a hash value.
     * @param file
     * @return
     */
    private static String fileToHash(String file) {
        file = file.replaceFirst(OBJ_DIR, "");
        return file.replaceFirst("/", "");
    }
}
