package gitlet;

/**
 * Represents an set of reference types.
 * @author william
 */
public enum ReferenceType {
    HEAD(""), BRANCH("branches/"), REMOTE("remotes/"), TAG("tags/"), NONE("");

    /**
     * The base directory for this type of reference.
     */
    private String baseDir;

    /**
     * @param dir
     */
    ReferenceType(String dir) {
        this.baseDir = dir;
    }

    /**
     * Gets the base directory of references of this type.
     * @return The directory.
     */
    public String getBaseDir() {
        return this.baseDir;
    }

}
