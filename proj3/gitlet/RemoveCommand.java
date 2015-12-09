/**
 * 
 */
package gitlet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author william
 */
public class RemoveCommand implements Command {

    /*
     * (non-Javadoc)
     * @see gitlet.Command#run(gitlet.Repository, java.lang.String[])
     */
    @Override
    public void run(Repository repo, String[] args) {
        remove(repo, args[0], repo.getCommit(repo.getHead()));
    }
    
    
    public static void remove(Repository repo, String file, Commit head){
        Index index = repo.getIndex();

        if (head.getBlobs().containsKey(file)) {
            try {
                index.remove(file, true);
                if (Files.exists(repo.getWorkingDir().resolve(file)))
                    Files.delete(repo.getWorkingDir().resolve(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            index.remove(file, false);
        }
    }

    /*
     * (non-Javadoc)
     * @see gitlet.Command#requiresRepo()
     */
    @Override
    public boolean requiresRepo() {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see gitlet.Command#checkOperands(java.lang.String[])
     */
    @Override
    public boolean checkOperands(String[] args) {
        return args.length == 1;
    }

}