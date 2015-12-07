/**
 * 
 */
package gitlet;

import java.util.Date;
import java.util.HashMap;


/**
 * @author william
 * Represents a GIT commit/flat tree..
 */
public class Commit extends GitletObject {

    /**
     * The serial version ID for the commit class.
     */
    private static final long serialVersionUID = 7879186830461498380L;
    
    private String parent;
    private String message;
    private Date date;
    /** The hash map of filename-sha1. */
    private HashMap<String, String> blobs;


    /**
     * Creates a commit with a set of blobs.
     * @param message The commti message.
     * @param date The date time.
     * @param parent The parent commit.
     * @param blobs The blobs involved in the commit.
     */
    public Commit(String message, Date date, String parent,  HashMap<String,String> blobs) {
        this.parent = parent;
        this.message = message;
        this.date = date;
        this.blobs = blobs;
    }

    /**
     * Creates an initial commit.
     * @param message The initial message.
     * @param currentDate The date.
     */
    public Commit(String message, Date currentDate) {
        this(message, currentDate, "", new HashMap<String,String>());
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
    public Date getDate() {
        return date;
    }


    /**
     * @return the blobs
     */
    public HashMap<String,String> getBlobs() {
        return blobs;
    }
    
    @Override
    public String toString(){
        String repr = "===\n";
        repr += "Commit "+ this.sha1();
        
    }

}
