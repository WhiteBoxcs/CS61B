/**
 * 
 */
package gitlet;

/**
 * @author william
 *
 */
public class MergeCommand implements Command {

    /* (non-Javadoc)
     * @see gitlet.Command#run(gitlet.Repository, java.lang.String[])
     */
    @Override
    public void run(Repository repo, String[] args) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see gitlet.Command#requiresRepo()
     */
    @Override
    public boolean requiresRepo() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see gitlet.Command#checkOperands(java.lang.String[])
     */
    @Override
    public boolean checkOperands(String[] args) {
        // TODO Auto-generated method stub
        return false;
    }

}
