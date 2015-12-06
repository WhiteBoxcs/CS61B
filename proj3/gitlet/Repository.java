package gitlet;

public class Repository {

    private boolean open;

    public Repository() {
        this.open = false;
    }

    public Repository(String workingDir) {
        // TODO Auto-generated constructor stub
    }

    
    public void open(String workingDir){
        if(isOpen())
            throw new IllegalStateException("Close repository before opening a new instance.");
    }
    
    public void close() {
        if(this.isOpen()){
            this.open = false;
            
            
        }    
    }
    
    

 
    
    public boolean isOpen() {
        // TODO Auto-generated method stub
        return open;
    }


}
