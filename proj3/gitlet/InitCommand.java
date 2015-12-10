/**
 *
 */
package gitlet;

/**
 * @author william
 */
public class InitCommand implements Command {

    /*
     * (non-Javadoc)
     * @see gitlet.Command#run(java.lang.String[])
     */
    @Override
    public void run(Repository repo, String[] args) {
        repo.init();
    }

    /*
     * (non-Javadoc)
     * @see gitlet.Command#requiresRepo()
     */
    @Override
    public boolean requiresRepo() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see gitlet.Command#checkOperands()
     */
    @Override
    public boolean checkOperands(String[] args) {
        return args.length == 0;
    }

}
