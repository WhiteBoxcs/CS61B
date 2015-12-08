/**
 * 
 */
package gitlet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author william
 * 
 */
public class RemoveCommand implements Command {

    /* (non-Javadoc)
     * @see gitlet.Command#run(gitlet.Repository, java.lang.String[])
     */
    @Override
    public void run(Repository repo, String[] args) {
        String file = args[0];  
        Index index = repo.getIndex();
        
        
        Commit cur = repo.getCommit(repo.getHead());
        if(cur.getBlobs().containsKey(file))
        {
            try {
                Files.delete(repo.getWorkingDir().resolve(file));
                index.remove(file, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        index.remove(file, false);
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
