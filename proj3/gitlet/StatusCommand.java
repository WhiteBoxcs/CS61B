/**
 * 
 */
package gitlet;

import java.nio.file.Path;

/**
 * @author william
 *
 */
public class StatusCommand implements Command {

    /* (non-Javadoc)
     * @see gitlet.Command#run(gitlet.Repository, java.lang.String[])
     */
    @Override
    public void run(Repository repo, String[] args) {
        String currentBranch = repo.getBranch();
        System.out.println("=== Branches ===");
        repo.applyToBranches((branch) -> {
            if(branch.equals(currentBranch))
                System.out.print('*');
            System.out.println(branch);
        });
        
        Index index = repo.getIndex();
        Path workingDir = repo.getWorkingDir();
        
        System.out.println("\n=== Staged Files ===");
        index.getStaged().forEach(
                (name, hash) -> System.out.println(name));
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
        // TODO Auto-generated method stub
        return args.length == 0;
    }

}
