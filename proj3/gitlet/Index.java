/**
 *
 */
package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * @author william
 */
public class Index implements Serializable {

    /**
     * Serialization ID for gitlet.
     */
    private static final long serialVersionUID = -8630621570840209428L;

    /**
     * Gets a list of blobs.
     */
    private HashMap<String, String> blobs;

    /**
     * A tree map of the staged files in the index.
     */
    private TreeMap<String, String> staged;

    /**
     * A tree map of the removed (staged) files in the index.
     */
    private TreeMap<String, String> removed;

    /**
     * Creates a gitlet index.
     */
    public Index() {
        this.blobs = new HashMap<String, String>();
        this.removed = new TreeMap<>();
        this.staged = new TreeMap<>();
    }

    /**
     * Gets the blob from the stage and clears the staging area.
     * @return A hashmap of filenames to blobs.
     */
    public HashMap<String, String> blobsFromStage() {
        if (!this.isChanged()) {
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
        if (this.removed.containsKey(fileName)) {
            String removedHash = this.removed.remove(fileName);
            this.blobs.put(fileName, removedHash);
        } else {
            if (!this.blobs.containsKey(fileName)
                    || !this.blobs.get(fileName).equals(hash)) {
                this.staged.put(fileName, hash);
            }
            this.blobs.put(fileName, hash);
        }

    }

    /**
     * Untracks a file.
     * @param fileName
     */
    public void remove(String fileName, boolean fromLastCommit) {
        if (!this.blobs.containsKey(fileName)) {
            throw new IllegalStateException("No reason to remove the file.");
        }
        if (fromLastCommit) {
            this.removed.put(fileName, this.blobs.get(fileName));
        }

        this.staged.remove(fileName);
        this.blobs.remove(fileName);
    }

    public void unstage(String fileName) {
        if (!this.blobs.containsKey(fileName)) {
            throw new IllegalStateException("No reason to remove the file.");
        }

        this.staged.remove(fileName);
    }

    /**
     * Clears the stage.
     */
    private void clearStage() {
        this.removed.clear();
        this.staged.clear();
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

    /**
     * Determines if the staging area has changed.
     * @return If it has changed.
     */
    public boolean isChanged() {
        // TODO Auto-generated method stub
        return this.removed.size() + this.staged.size() != 0;
    }

}
