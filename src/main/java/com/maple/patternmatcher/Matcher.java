package com.maple.patternmatcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maple.patternmatcher.pattern.Field;

public class Matcher {

	private static final String NO_MATCH = "NO MATCH";
	private static final String SLASH = "/";
	private static final String COMMA = ",";
	
	private static List<String> patterns = new ArrayList<String>();
	private static List<String> paths = new ArrayList<String>();
	private static Map<String, Field> patternMap = new HashMap<String, Field>();
	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Must send two arguments, the input file name and output file name");
			return;
		}
		
		if (!parseInput(args[0])) {
			return;
		}
		buildPatternMap();
		matchPaths(args[1]);
	}
	
	private static boolean parseInput(String inputFile) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(inputFile));
			int numPatterns = Integer.parseInt(in.readLine());
			for (int i = 0; i < numPatterns; i++) {
				patterns.add(in.readLine());
			}
			int numPaths = Integer.parseInt(in.readLine());
			for (int i = 0; i < numPaths; i++) {
				paths.add(in.readLine());
			}
			
			in.close();
			return true;
		} catch (FileNotFoundException e) {
			System.err.println("Could not find file " + inputFile);
		} catch (NumberFormatException e) {
			System.err.println("Could not parse integer in file " + inputFile);
		} catch (IOException e) {
			System.err.println("Could not close file " + inputFile);
		}
		
		return false;
	}

	/**
	 * This builds out a map of the possible patterns. Essentially,
	 * this maps roughly to a pattern tree where each field in a pattern
	 * represents a level in the pattern tree. Each distinct first pattern
	 * would be a root node, with the possible sub-patterns carried within each node.
	 * 
	 * Each valid path has a TERMINATOR node, which represents that the tree route to
	 * that node is a valid path. This helps assist with situations where a sub-path
	 * for a pattern is itself a valid pattern, such as a,b being a valid sub-pattern
	 * of a,b,c
	 */
	private static void buildPatternMap() {
		for (String pattern : patterns) {
			String[] patternSplit = pattern.split(COMMA);

			if (!patternMap.containsKey(patternSplit[0])) {
				patternMap.put(patternSplit[0], new Field(patternSplit[0]));
			}
			Field node = patternMap.get(patternSplit[0]);
			for (int i = 1; i < patternSplit.length; i++) {
				if (!node.hasField(patternSplit[i])) {
					node.addField(patternSplit[i]);
				}
				node = node.getField(patternSplit[i]);
			}
			node.addField(Field.TERMINATOR);
		}
	}
	
	private static void matchPaths(String output) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(output));
			
			for (String path : paths) {
				String pattern = matchPath(path);
				if (pattern == null) {
					pattern = NO_MATCH;
				}
				out.write(pattern);
				out.newLine();
				System.out.println(pattern);
			}
			
			out.close();
		} catch (FileNotFoundException e) {
			System.err.println("Could not find file " + output);
		} catch (NumberFormatException e) {
			System.err.println("Could not parse integer in file " + output);
		} catch (IOException e) {
			System.err.println("Could not close file " + output);
		}
	}

	/**
	 * Returns the pattern that matches a given path, or null if there is
	 * no match.
	 * 
	 * @param path
	 * @return
	 */
	private static String matchPath(String path) {
		StringBuilder pattern = new StringBuilder();
		String[] pathSplit = path.split(SLASH);
		Map<String, Field> map = patternMap;
		
		for (String segment : pathSplit) {
			if (map.containsKey(segment)) {
				pattern.append(segment).append(',');
				map = map.get(segment).getFields();
			} else if (map.containsKey(Field.WILDCARD)) {
				pattern.append(Field.WILDCARD).append(',');
				map = map.get(Field.WILDCARD).getFields();
			} else {
				return null;
			}
		}
		
		if (map.containsKey(Field.TERMINATOR)) {
			// remove trailing comma and return
			return pattern.deleteCharAt(pattern.length() - 1).toString();
		} else {
			return null;
		}
	}
}
