package canfield;

import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

/** Various static utility functions.
 *  @author P. N. Hilfinger
 */
public class Utils {

    /** Location of resources. */
    private static final File RESOURCES = new File("canfield", "resources");

    /** There are no objects of type Utils.  By making its constructor
     *  private, there is no way to create a Utils outside the class. */
    private Utils() {
    }

    /** Return the contents of an existing file NAME as an InputStream, or null
     *  if NAME is null, has bad syntax or does not exist.
     *  Tries to read file directly, and if that fails, tries to read
     *  the resource canfield/resources/NAME.
     */
    static InputStream getFileStream(String name) {
        File file;
        if (name == null) {
            return null;
        }
        file = new File(name);
        try {
            if (file.canRead()) {
                return new FileInputStream(file);
            }
            file = new File(RESOURCES, name);
            if (file.canRead()) {
                return new FileInputStream(file);
            }
        } catch (IOException excp) {
            return null;
        }
        try {
            URL url = Utils.class.getClassLoader().getResource(file.toString());
            if (url != null) {
                return url.openStream();
            } else {
                return null;
            }
        } catch (IOException excp) {
            return null;
        }
    }

    /** Returns an IllegalArgumentException with specified message.  Arguments
     *  MSG and ARGS are as for String.format. */
    static IllegalArgumentException err(String msg, Object... args) {
        return new IllegalArgumentException(String.format(msg, args));
    }

}

