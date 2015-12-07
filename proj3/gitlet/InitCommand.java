/**
 * 
 */
package gitlet;

/**
 * @author william
 *
 */
public class InitCommand implements Command {

    /**
     * 
     */
    public InitCommand() {
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see gitlet.Command#run(java.lang.String[])
     */
    @Override
    public void run(Repository repo, String[] args) {
        repo.init();
    }

    @Override
    public boolean requiresRepo() {
        return false;
    }

}
