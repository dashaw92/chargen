package me.daniel.chargen;

import java.util.ArrayList;
import java.util.List;

public final class Chargen {
	
	//Found on the Wikipedia page for chargen
	public static final String DEFAULT_CHARS = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|} ";
	//Found on the Wikipedia page for chargen
	public static final int DEFAULT_WIDTH = 72;
	
	//This code will store all permutations of a set of characters
	//This should improve performance overall by reducing string manipulations
	
	//List of unique states, ordered from start to end
	private List<String> states = new ArrayList<>();
	//Has the chargen finished looping through the set of characters?
	private boolean states_full = false;
	//Current index in the list of states
	private int current_index = 0; 
	//Disable state checking for very large strings
	private boolean should_check_states = true;
	
	
	private String chars; //The charset
	private final int width; //How wide the output should be
	
	public Chargen() { this(DEFAULT_CHARS, DEFAULT_WIDTH); }
	public Chargen(String chars) { this(chars, DEFAULT_WIDTH); }
	public Chargen(int width) { this(DEFAULT_CHARS, width); }
	public Chargen(String chars, int width) {
		//sanity check, ensure arguments are valid
		if(chars.trim().length() < 1) chars = DEFAULT_CHARS;
		if(width < 16) width = DEFAULT_WIDTH;
		if(width > DEFAULT_CHARS.length()) width = DEFAULT_CHARS.length() / 2;
		
		this.chars = chars;
		this.width = width;
		
		//Disable saving permuations for strings longer than 499 characters
		should_check_states = chars.length() < 500;
	}
	
	//Mutates the state or returns the next index in the states list if
	//the chargen has finished collecting states for the charset
	public String next() {
		next_state(chars); //Save the current state
		
		//Branch A: Mutate the current chars
		if(!should_check_states || !states_full) {
			String old = chars;
			chars = chars.substring(1) + chars.substring(0, 1);
			return format(old);
		}
		
		//Branch B: Return the next state and increment the internal counter
		current_index %= states.size();
		return format(states.get(current_index++));
	}
	
	//Saves the passed state to the state list if should_check_states is true
	private void next_state(String state) {
		if(!should_check_states) return;
		
		//The list contains the passed state so we notify the
		//instance that we are finished finding new states.
		if(states.contains(state)) {
			states_full = true;
			return;
		}
		
		states.add(state);
	}
	
	//Helper method to format the next line so it's ready 
	//to be sent to the client
	private String format(String line) {
		return line.substring(0, width) + "\r\n";
	}
}
