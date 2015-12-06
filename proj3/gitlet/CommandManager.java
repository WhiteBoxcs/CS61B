/**
 * 
 */
package gitlet;

import java.util.HashMap;

/**
 * @author william
 *
 */
public class CommandManager {
	private HashMap<String,Command> commandMap;


	public void CommandManager(){
		this.commandMap = new HashMap<>();
	}
	
	
	public void add(String trigger, Command newCommand){
		this.commandMap.put(trigger, newCommand);
	}
	
	public void process(String args[]) throws Exception{
		String command = args[0];
		if(!commandMap.containsKey(key)){
			
		}
	}
}
