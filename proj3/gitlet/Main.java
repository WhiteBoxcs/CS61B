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
    	processor.add("log", new LogCommand());
    	processor.add("global-log", new GlobalLogCommand());
    	processor.add("find", new FindCommand());
    	processor.add("add", new AddCommand());
    	processor.add("rm", new RemoveCommand());
    	processor.add("status", new StatusCommand());
    	
    	Repository repo = new Repository(System.getProperty("user.dir"));
    	
    	try {
            processor.process(repo, args);
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    	
    	
    	repo.close();
    }

}
