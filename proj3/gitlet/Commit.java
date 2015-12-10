/**
 *
 */
package gitlet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * @author william Represents a GIT commit/flat tree..
 */
public class Commit extends GitletObject implements Map<String, String> {

    /**
     * The serial version ID for the commit class.
     */
    private static final long serialVersionUID = 7879186830461498380L;

    /** the parent. */
    private String parent;
    /** the message. */
    private String message;
    /** the date. */
    private LocalDateTime date;
    /** The hash map of filename-sha1. */
    private HashMap<String, String> blobs;

    /**
     * Creates a commit with a set of blobs.
     * @param messages
     *            The commti message.
     * @param datee
     *            The date time.
     * @param parentt
     *            The parent commit.
     * @param blobs
     *            The blobs involved in the commit.
     */
    public Commit(String messages, LocalDateTime datee, String parentt,
            HashMap<String, String> blobs) {
        if (messages == null || messages.isEmpty() || messages.equals("")) {
            throw new IllegalArgumentException(
                    "Please enter a commit message.");
        }
        this.parent = parentt;
        this.message = messages;
        this.date = datee;
        this.blobs = blobs;
    }

    /**
     * Creates an initial commit.
     * @param message
     *            The initial message.
     * @param currentDate
     *            The date.
     */
    public Commit(String message, LocalDateTime currentDate) {
        this(message, currentDate, "", new HashMap<String, String>());
    }

    /**
     * @return the parent
     */
    public String getParent() {
        return this.parent;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @return the date
     */
    public LocalDateTime getDate() {
        return this.date;
    }

    /**
     * @return the blobs filename-sha1
     */
    public HashMap<String, String> getBlobs() {
        return this.blobs;
    }

    /**
     * Gets the toString of the commit.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String dateStr = this.date.format(formatter).replace('T', ' ');
        int nanoIndex = dateStr.indexOf('.');
        return "===\n" + "Commit " + this.sha1() + "\n"
                + dateStr.substring(0, nanoIndex) + "\n" + this.message + "\n";
    }

    /**
     * Determines the size of the blobs.
     */
    @Override
    public int size() {
        return this.blobs.size();
    }

    /**
     * Determines if a commit is empty.
     */
    @Override
    public boolean isEmpty() {
        return this.blobs.isEmpty();
    }

    /**
     * Determines if the commit references a file.
     */
    @Override
    public boolean containsKey(Object fileName) {
        return this.blobs.containsKey(fileName);
    }

    /**
     * Determines if the commti contains a blob hash.
     */
    @Override
    public boolean containsValue(Object hash) {
        return this.blobs.containsValue(hash);
    }

    /**
     * Gets a blob hash from a file name.
     */
    @Override
    public String get(Object fileName) {
        return this.blobs.get(fileName);
    }

    /**
     * Puts an element in the blob set.
     */
    @Override
    public String put(String fileName, String hash) {
        return this.blobs.put(fileName, hash);
    }

    /**
     * Removes an element from the blob set.
     */
    @Override
    public String remove(Object fileName) {
        return this.blobs.remove(fileName);
    }

    /**
     * Puts a bunch of blobs in the commit.
     */
    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        this.blobs.putAll(m);

    }

    /**
     * Clears the blobs.
     */
    @Override
    public void clear() {
        this.blobs.clear();
    }

    /**
     * Gets the blob keyset.
     */
    @Override
    public Set<String> keySet() {
        return this.blobs.keySet();
    }

    /**
     * Gets the blobs values.
     */
    @Override
    public Collection<String> values() {
        return this.blobs.values();
    }

    /**
     * Gets the blobs entry set.
     */
    @Override
    public Set<java.util.Map.Entry<String, String>> entrySet() {
        return this.blobs.entrySet();
    }

    /**
     * Iterates over the blobs.
     * @param action
     *            The action.
     */
    @Override
    public void forEach(BiConsumer<? super String, ? super String> action) {
        this.blobs.forEach(action);
    }

}
