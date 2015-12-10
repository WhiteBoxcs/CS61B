/**
 *
 */
package gitlet;

import static gitlet.ReferenceType.BRANCH;
import static gitlet.ReferenceType.REMOTE;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author william
 */
public class FetchCommand implements Command {

    /*
     * (non-Javadoc)
     * @see gitlet.Command#run(gitlet.Repository, java.lang.String[])
     */
    @Override
    public void run(Repository repo, String[] args) {
        fetch(repo, args[0], args[1]);

    }

    /**
     * Fetches a remote repository.
     * @param repo Tj.
     * @param remoteName rj
     * @param remoteBranch rb
     */
    public static void fetch(Repository repo, String remoteName,
            String remoteBranch) {
        Reference remoteRef = repo.refs().get(REMOTE, remoteName);

        Path remoteDir =
                repo.getWorkingDir().resolve(remoteRef.target()).normalize();

        if (!Files.exists(remoteDir)) {
            throw new IllegalStateException("Remote directory not found.");
        }

        Repository remote =
                new Repository(remoteDir.resolve("..").normalize().toString());
        if (!remote.isOpen()) {
            throw new IllegalStateException("Remote directory not found.");
        }

        String remoteHead = remote.refs().resolve(BRANCH, remoteBranch);

        pullObjects(repo, remote, remoteHead);
        String localBranch = remoteName + "/" + remoteBranch;
        if (!repo.refs().contains(BRANCH, localBranch)) {
            repo.refs().add(BRANCH, localBranch, new Reference(remoteHead));
        } else {
            repo.refs().get(BRANCH, localBranch).setTarget(remoteHead);
        }

    }

    /**
     * Pulls objs.
     * @param repo a 
     * @param remote  b
     * @param remoteHead c
     */
    private static void pullObjects(Repository repo, Repository remote,
            String remoteHead) {
        Collection<GitletObject> incoming = new HashSet<GitletObject>();

        for (String hash : MergeCommand.getHistory(remote, remoteHead,
                new ArrayList<String>())) {

            Commit c = remote.objects().get(Commit.class, hash);
            List<GitletObject> blobs = c.values().stream()
                    .filter(x -> !repo.objects().contains(Blob.class, x))
                    .map((x) -> {
                        return remote.objects().get(Blob.class, x);
                    }).collect(Collectors.toList());
            if (!repo.objects().contains(Commit.class, hash)) {
                incoming.add(c);
            }

            incoming.addAll(blobs);
        }
        repo.objects().putAll(incoming);

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
        return args.length == 2;
    }

}
