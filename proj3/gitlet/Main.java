package gitlet;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
    	CommandManager processor = new CommandManager();
    	processor.add("init", new InitCommand());
    	
    	
    	
    	Repository repo = new Repository(System.getProperty("user.dir"));
    	try {
            processor.process(repo, args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    	repo.close();
    }

}
