/**
 * 
 */
package gitlet;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author william
 *
 */
public class CommandManager {
	private HashMap<String,Command> commandMap;


	public CommandManager(){
		this.commandMap = new HashMap<>();
	}
	
	
	public void add(String trigger, Command newCommand){
		this.commandMap.put(trigger, newCommand);
	}
	
	public void process(Repository localRepo, String args[]) throws Exception{
	    if(args == null || args.length == 0){
	        throw new IllegalStateException("Please enter a command.");
	    }
	    
		String trigger = args[0];
		Command command = this.commandMap.get(trigger);
		
		if(command == null){
		    throw new IllegalStateException("No command with that name exists.");
		}
		
		if(command.requiresRepo() && !localRepo.isOpen())
		    throw new IllegalStateException("Not in an initialized gitlet directory.");
		
		command.run(localRepo, Arrays.copyOfRange(args, 1, args.length));
	}
}
