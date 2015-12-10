/**
 * 
 */
package gitlet;
import static gitlet.ReferenceType.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * @author william
 *
 */
public class PushCommand implements Command {

    /* (non-Javadoc)
     * @see gitlet.Command#run(gitlet.Repository, java.lang.String[])
     */
    @Override
    public void run(Repository repo, String[] args) {
        Reference remoteRef = repo.refs().get(REMOTE, args[0]);
        Path remoteDir = repo.getWorkingDir().resolve(remoteRef.target());
        if(!Files.exists(remoteDir))
            throw new IllegalStateException("Remote directory not found.");
        
        Repository remote = new Repository(remoteDir.toAbsolutePath().toString());
        
        String head = repo.refs().resolve(HEAD);
        Collection<String> localHistory = MergeCommand.getHistory(repo, head, new HashSet<String>());
        

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
        return args.length == 2;
    }

}
