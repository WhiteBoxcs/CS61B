import java.io.Reader;

import com.sun.tools.javac.util.Assert;

import java.io.IOException;

/** Translating Reader: a stream that is a translation of an
 *  existing reader.
 *  @author
 */
public class TrReader extends Reader {
    private Reader source;
<<<<<<< HEAD
=======
	private String from;
	private String to;
>>>>>>> c6077f71cdda17424bc27078aa5f547b2c0b3730

	/** A new TrReader that produces the stream of characters produced
     *  by STR, converting all characters that occur in FROM to the
     *  corresponding characters in TO.  That is, change occurrences of
     *  FROM.charAt(0) to TO.charAt(0), etc., leaving other characters
     *  unchanged.  FROM and TO must have the same length. */
    public TrReader(Reader str, String from, String to) {
    	Assert.check(from != null);
    	Assert.check(to != null);
    	Assert.check(from.length() == to.length());
    	
        // FILL IN
    	this.source = str;
    	this.from = from;
    	this.to = to;
    }

	@Override
	public void close() throws IOException {
		source.close();
		
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int readStatus = source.read(cbuf, off, len);

		for(int i = off; i < len; i++){
		
			cbuf[i-off] = map(cbuf[i-off]);
		}
		
		return Math.min(len, readStatus);
	}

	/**
	 * Translates a given character.
	 * @param origin
	 * @return
	 */
	private char map(char origin){
		int fromIndex = from.indexOf(origin);
		if(fromIndex == -1)
			return origin;
		else
			return to.charAt(fromIndex);

		
	}
}


