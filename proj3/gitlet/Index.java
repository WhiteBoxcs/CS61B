/**
 *
 */
package gitlet;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * @author william
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
    public Index() {
        this.blobs = new HashMap<String, String>();
        this.removed = new TreeMap<>();
        this.added = new TreeMap<>();
        this.modified = new TreeMap<>();
        this.staged = new TreeMap<>();
    }

    /**
     * Gets the blob from the stage and clears the staging area.
     * @return A hashmap of filenames to blobs.
     */
    public HashMap<String, String> blobsFromStage() {
        if (!this.changed) {
            throw new IllegalStateException("No changes added to the commit.");
        }
        this.clearStage();

        return this.blobs;
    }

    /**
     * Non destructiveley gets the tracked blobs.
     * @return
     */
    public HashMap<String, String> getBlobs() {
        return this.blobs;
    }

    /**
     * Checks out a particular file.
     * @param filename
     *            The file to checkout.
     * @param hash
     *            The hash of the file.
     */
    public void checkout(String filename, String hash, boolean stage) {

        if (stage) {
            this.add(filename, hash);
        } else {
            this.blobs.put(filename, hash);
            this.staged.remove(filename);
            this.removed.remove(filename);
            this.added.remove(filename);
            this.modified.remove(filename);
            if (this.staged.size() + this.removed.size() == 0) {
                this.changed = false;
            }
        }
    }

    public void checkout(Commit commit) {
        this.clearStage();
        this.blobs = commit.getBlobs();
    }

    /**
     * Adds a blob to the staging area.
     * @param fileName
     *            The file name of the blob.
     * @param hash
     *            The hash of the blob.
     */
    public void add(String fileName, String hash) {
        if (this.blobs.containsKey(fileName)) {
            if (!this.blobs.get(fileName).equals(hash)) {
                this.modified.put(fileName, hash);
            } else {
                return;
            }
        } else {
            this.added.put(fileName, hash);
        }

        this.staged.put(fileName, hash);

        this.changed = true;
        this.blobs.put(fileName, hash);

    }

    /**
     * Untracks a file.
     * @param fileName
     */
    public void remove(String fileName, boolean fromLastCommit) {
        if (!this.blobs.containsKey(fileName)) {
            throw new IllegalStateException("No reason to remove the file.");
        }

        this.changed = true;
        if (fromLastCommit) {
            this.removed.put(fileName, this.blobs.get(fileName));
        }

        this.staged.remove(fileName);
        this.added.remove(fileName);
        this.modified.remove(fileName);
        this.blobs.remove(fileName);
    }

    /**
     * Clears the stage.
     */
    private void clearStage() {
        this.changed = false;
        this.modified.clear();
        this.removed.clear();
        this.added.clear();
        this.staged.clear();
    }

    /**
     * @return the changed
     */
    public boolean isChanged() {
        return this.changed;
    }

    /**
     * @return the modified
     */
    public TreeMap<String, String> getModified() {
        return this.modified;
    }

    /**
     * @return the removed
     */
    public TreeMap<String, String> getRemoved() {
        return this.removed;
    }

    /**
     * @return the union of removed and modified.
     */
    public TreeMap<String, String> getStaged() {
        return this.staged;
    }

}
