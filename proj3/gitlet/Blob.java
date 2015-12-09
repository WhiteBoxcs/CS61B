package gitlet;

public class Blob extends GitletObject {

    /**
     * Thbe base serialization UID for the Blob object.
     */
    private static final long serialVersionUID = 4865064372716910861L;
    private byte[] contents;

    /**
     * Generates a Blob.
     * @param contents
     */
    public Blob(byte[] contents) {
        this.contents = contents;
    }

    /**
     * @return the contents
     */
    public byte[] getContents() {
        return this.contents;
    }

}
