import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.InputStreamReader;

public class BadPatterns {

    static final Pattern 
        itemPatn = Pattern.compile("\\s*'(.*)':'(.*)'"),
        startPatn = Pattern.compile("\\s*\\["),
        sepPatn = Pattern.compile("\\s*,"),
        endPatn = Pattern.compile("\\s*\\]");

    static List<String> readList(Scanner inp) throws IOException {
        
        ArrayList<String> results = new ArrayList<String>();

        if (inp.findWithinHorizon(startPatn, 0) == null)
            throw new IOException("list not found");
        if (inp.findWithinHorizon(itemPatn, 0) != null) {
            while (true) {
                results.add(inp.match().group(1));
                results.add(inp.match().group(2));
                if (inp.findWithinHorizon(sepPatn, 0) == null)
                    break;
                if (inp.findWithinHorizon(itemPatn, 0) == null)
                    throw new IOException("bad list");
            }
        }
        if (inp.findWithinHorizon(endPatn, 0) == null)
            throw new IOException("bad list");
        return results;

    }
    
    public static void main(String[] args) {
        Scanner inp = new Scanner(new InputStreamReader(System.in));
        try {
            List<String> items = readList(inp);
            System.out.println(items);
        } catch (IOException e) {
            System.err.printf("Something wrong: %s%n", e.getMessage());
        }
    }

}
