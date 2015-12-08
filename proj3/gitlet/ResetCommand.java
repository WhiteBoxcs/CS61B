/**
 * 
 */
package gitlet;

/**
 * @author william
 *
 */
public class ResetCommand implements Command {

    /* (non-Javadoc)
     * @see gitlet.Command#run(gitlet.Repository, java.lang.String[])
     */
    @Override
    public void run(Repository repo, String[] args) {
        String commitHash = args[0];
        Commit toCheck;
        if(commitHash.length() == 40)
            toCheck = repo.getCommit(commitHash);
        else
            toCheck = repo.firstCommitWhere(x -> x.startsWith(commitHash));
        
        if(toCheck == null)
            throw new IllegalArgumentException("No commit with that id exists.");
        
        repo.checkout(toCheck);
        repo.setHead(commitHash);
    }

    /* (non-Javadoc)
     * @see gitlet.Command#requiresRepo()
     */
    @Override
    public boolean requiresRepo() {
        return true;
    }

    /* (non-Javadoc)
     * @see gitlet.Command#checkOperands(java.lang.String[])
     */
    @Override
    public boolean checkOperands(String[] args) {
        return args.length == 1;
    }

}
