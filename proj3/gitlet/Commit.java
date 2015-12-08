/**
 * 
 */
package gitlet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private LocalDateTime date;
    /** The hash map of filename-sha1. */
    private HashMap<String, String> blobs;


    /**
     * Creates a commit with a set of blobs.
     * @param message The commti message.
     * @param date The date time.
     * @param parent The parent commit.
     * @param blobs The blobs involved in the commit.
     */
    public Commit(String message, LocalDateTime date, String parent,  HashMap<String,String> blobs) {
        if(message == "")
            throw new IllegalArgumentException("Please enter a commit message.");
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
    public Commit(String message, LocalDateTime currentDate) {
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
    public LocalDateTime getDate() {
        return date;
    }


    /**
     * @return the blobs filename-sha1
     */
    public HashMap<String,String> getBlobs() {
        return blobs;
    }
    
    /**
     * Gets the toString of the commit.
     */
    @Override
    public String toString(){
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String dateStr = date.format(formatter).replace('T', ' ');
        int nanoIndex = dateStr.indexOf('.');
        return  "===\n"
                + "Commit "+ this.sha1() +"\n"
                +  dateStr.substring(0, nanoIndex)+ "\n"
                + message + "\n";
    }

}
