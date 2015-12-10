/**
 *
 */
package gitlet;

import static gitlet.ReferenceType.BRANCH;
import static gitlet.ReferenceType.HEAD;
import static gitlet.ReferenceType.REMOTE;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author william
 */
public class PushCommand implements Command {

    /*
     * (non-Javadoc)
     * @see gitlet.Command#run(gitlet.Repository, java.lang.String[])
     */
    @Override
    public void run(final Repository repo, String[] args) {
        push(repo, args[0], args[1]);
    }

    /**
     * Pushes a branch locally to the remote.
     * @param repo
     *            The repo.
     * @param remoteName
     *            The remote path.
     * @param remoteBranch
     *            The remote branch.
     */
    public static void push(final Repository repo, String remoteName,
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

        String head = repo.refs().resolve(HEAD);

        String remoteHead = "";
        if (remote.refs().contains(BRANCH, remoteBranch)) {
            remoteHead = remote.refs().resolve(BRANCH, remoteBranch);
        } else {
            remote.refs().add(BRANCH, remoteBranch, new Reference(""));
        }

        if (head.equals(remoteHead)) {
            return;
        }

        Collection<String> intersecting =
                intersectBranches(repo, remote, head, remoteHead);
        pushCommits(repo, remote, intersecting);
        fastForward(remote, remoteBranch, head);

        remote.close();
    }

    /**
     * Intersects two branches.
     * @param repo
     *            The repo.
     * @param remote
     *            The remote.
     * @param head
     *            The head.
     * @param remoteHead
     *            The remote head.
     * @return The commits in the intersection.
     */
    private static Collection<String> intersectBranches(Repository repo,
            Repository remote, String head, String remoteHead) {
        Collection<String> localHistory = MergeCommand.getHistory(repo, head,
                new LinkedHashSet<String>());
        if (!localHistory.contains(remoteHead) && !remoteHead.isEmpty()) {
            remote.close();
            throw new IllegalStateException(
                    "Please pull down remote changes before pushing.");
        }
        localHistory.remove(MergeCommand.getHistory(remote, remoteHead,
                new LinkedHashSet<String>()));

        return localHistory;
    }

    /**
     * Feeds the remote forward,
     * @param remote
     *            the remiote.
     * @param remoteBranch
     *            the remote branch
     */
    private static void fastForward(Repository remote, String remoteBranch,
            String head) {
        if (remote.refs().get(HEAD).target().equals(remoteBranch)) {
            ResetCommand.reset(remote, head);
        } else {
            remote.refs().get(BRANCH, remoteBranch).setTarget(head);
        }
    }

    /**
     * Pushes objects in a collection of outgoing commits.
     * @param repo
     *            The repositoryl.
     * @param remote
     *            The remote.
     * @param outGoingCommits
     *            The outgoing commits.
     */
    private static void pushCommits(Repository repo, Repository remote,
            Collection<String> outGoingCommits) {
        Collection<GitletObject> outgoing = new HashSet<GitletObject>();

        for (String hash : outGoingCommits) {

            Commit c = repo.objects().get(Commit.class, hash);
            List<GitletObject> blobs = c.values().stream().map((x) -> {
                return repo.objects().get(Blob.class, x);
            }).collect(Collectors.toList());

            outgoing.add(c);
            outgoing.addAll(blobs);
        }
        remote.objects().putAll(outgoing);
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
