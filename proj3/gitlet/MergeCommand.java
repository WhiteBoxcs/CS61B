/**
 * 
 */
package gitlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
        merge(repo, args[0]);
    }
    
    /**
     * Merges two commits from different repositories.
     * @param The repo.
     * @param commitB The commit from which the content will be merged into TO.
     */
    public static void merge(Repository repo, String branch){
        if(branch.equals(repo.getBranch()))
            throw new IllegalArgumentException("Cannot merge a branch with itself.");
        if(repo.getIndex().isChanged())
            throw new IllegalStateException("You have uncommitted changes.");
        
        String otherHash = repo.getBranchHead(branch);
        String headHash = repo.getHead();
        
        String splitHash = getSplitPoint(repo, headHash, otherHash);
        
        
        if(splitHash.equals(otherHash))
            throw new IllegalStateException("Given branch is an ancestor of the current branch.");
        if(splitHash.equals(headHash)){
            ResetCommand.reset(repo, otherHash);
            System.out.println("Current branch fast-forwarded.");
        }
        if(splitHash.isEmpty()){
            splitHash = repo.initialCommit();
        }
        
        Commit split = repo.getCommit(splitHash);
        Commit head = repo.getCommit(headHash);
        Commit other = repo.getCommit(otherHash);
        
        mergeCompare(repo, head, other, split);
    }

    /**
     * Handles the actual checkout of the merge.
     * @param repo The repository.
     * @param head The head.
     * @param other The other.
     * @param split The split.
     */
    private static void mergeCompare(Repository repo, Commit head, Commit other, Commit split){
        
        List<String> toCheckout = new ArrayList<String>();
        List<String> toRemove = new ArrayList<String>();
        List<String> inConflict = new ArrayList<String>();
        
        //TODO: REFACTOR
        
        other.getBlobs().forEach((file,hash)-> {
            String splitFileHash = split.getBlobs().get(file);
            if(splitFileHash != null){ //present at splitpoint
                if(!splitFileHash.equals(hash)){
                    if(head.getBlobs().containsKey(file)){
                        if(splitFileHash.equals(head.getBlobs().get(file))){
                            toCheckout.add(file); 
                        }
                        else{
                            inConflict.add(file);
                        }
                    }
                }
                else{
                    if(!head.getBlobs().containsKey(file));
                }
                        
            }
            else{
                if(!other.getBlobs().containsKey(file))
                    toCheckout.add(file);
            }
        });
        
        
        head.getBlobs().forEach((file, hash) -> {
            String splitFileHash = split.getBlobs().get(file);
            if(splitFileHash != null){
                if(!splitFileHash.equals(hash)){
                    if(other.getBlobs().containsKey(file) 
                            && splitFileHash.equals(other.getBlobs().get(file)));
                }
                else{
                    if(!other.getBlobs().containsKey(file))
                        toRemove.add(file);
                }
            }
            else if(!other.getBlobs().containsKey(file));
            if(head.getBlobs().containsKey(file) &&
                    !other.getBlobs().containsKey(file))
                ; 
        });
        
        //TODO check if some files in the way!
        mergeCheckout(repo, head, other, split, toCheckout);
        mergeRemove(repo, head, other, split, toRemove);
        mergeConflict(repo, head, other, split, inConflict);
    }
    
    private static void mergeCheckout(Repository repo, Commit head,
            Commit other, Commit split, Collection<String> toCheckout) {
        for(String file : toCheckout){
            
        }
        
    }

    
    private static void mergeConflict(Repository repo, Commit head,
            Commit other, Commit split, List<String> inConflict) {
        // TODO Auto-generated method stub
        
    }

    private static void mergeRemove(Repository repo, Commit head, Commit other,
            Commit split, List<String> toRemove) {
        // TODO Auto-generated method stub
        
    }


    /**
     * Gets the split point.
     * @param repo The repository.
     * @param a The first commit.
     * @param b The second commmit.
     * @return The splitpoint commit.
     */
    public static String getSplitPoint(Repository repoA, String a, String b){
        if(b.equals(a))
            return a;
        
        List<String> aHistory = (List<String>)getHistory(repoA, a, new ArrayList<String>());
        Collection<String> bHistory = getHistory(repoA, b, new HashSet<String>());
        
        aHistory.retainAll(bHistory);
        if(aHistory.isEmpty())
            return "";
        else
            return aHistory.get(0);
    }
    
   
    /**
     * Gets the commit history from a starting point.
     * @param repo
     * @param start
     * @param history
     * @return
     */
    private static Collection<String> getHistory(Repository repo, String start,
            Collection<String> history) {
        String cur = start;
        while(!cur.isEmpty())
        {
            history.add(cur);
            cur = repo.getCommit(cur).getParent();
        }
        return history;
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
