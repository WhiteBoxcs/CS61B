/**
 * 
 */
package gitlet;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;


/**
 * @author william
 * Represents a GIT commit/flat tree..
 */
public class Commit extends GitlitObject {

    /**
     * The serial version ID for the commit class.
     */
    private static final long serialVersionUID = 7879186830461498380L;
    
    private String parent;
    private String message;
    private OffsetDateTime date;
    /** The hash map of sha1-filename. */
    private HashMap<String, String> blobs;


    /**
     * Creates a commit with a set of blobs.
     * @param message The commti message.
     * @param date The date time.
     * @param parent The parent commit.
     * @param blobs The blobs involved in the commit.
     */
    public Commit(String message, OffsetDateTime date, String parent,  HashMap<String,String> blobs) {
        this.parent = parent;
        this.message = message;
        this.date = date;
        this.blobs = blobs;
    }


    /**
     * @return the parent
     */
    public String getParent() {
        return parent;
    }


    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }


    /**
     * @return the date
     */
    public OffsetDateTime getDate() {
        return date;
    }


    /**
     * @return the blobs
     */
    public HashMap<String,String> getBlobs() {
        return blobs;
    }

}
