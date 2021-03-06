/*
 * [MhmwAssignFinal.java]
 * Monster Hunter Mini-World Assignment
 * Albert Quon
 * 2018/04/17
 */


import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;
import java.io.PrintWriter;

class MhmwAssignFinal {
  
  // global variables
  public static ArrayList<Integer>trapList = new ArrayList<Integer>(); //stores coordinates of the traps
  public static int longestTrap = 0; // stores the longest path
  public static int trapTime = Integer.MAX_VALUE; // stores the shortest path
  
  /** 
   * Main method
   * @param arguments
   * @throws e
   */
  public static void main(String[] args) throws Exception{
    Scanner keyInput = new Scanner(System.in);
    String fileName;
    String[][] maze, completeMaze;
    int trapCount = 0; 
    System.out.println("Welcome to the Monster Mini-World Hunter!\n");
    System.out.println("Enter the file name (without extensions)");
    fileName = keyInput.nextLine();
    keyInput.close();
    
    maze = mazeCreator(fileName); // maze from text file
    travel(1,1,maze, trapList); // determines trap coordinates by adding onto the global ArrayList
    completeMaze = trapSetter(maze); // generates completed maze
    captureSteps(1,1,completeMaze, 0); // modifies the global integer variable to determine the worst case scenario
    
    for (int i = 0; i < completeMaze[0].length; i++) { // determines the amount of traps in the complete maze
      for (int j = 0; j < completeMaze[0].length; j++) {
        if (completeMaze[i][j].equals("T")) {
          trapCount += 1;
        }
      }
    }
    
    layTrapTime(trapList.get(0),trapList.get(1), completeMaze, trapCount, 0); // modifies the global integer variable to determine steps to lay down traps
    
    solution(completeMaze); //outputs solution onto seperate text file
  }
  
  /** 
   * This method reads a text file then returns the maze as a 2-D array
   * @param the text file name
   * @throws e
   * @return a 2-D array of the maze 
   */
  public static String[][] mazeCreator(String fileName) throws Exception{
    String[][] map;
    File mapFile = new File(fileName + ".txt");
    String mazeLength;
    Scanner fileInput = new Scanner(mapFile);
    mazeLength = fileInput.nextLine();
    
    map = new String[mazeLength.length()][mazeLength.length()]; //map length determined from one line
    for (int i = 0; i < mazeLength.length(); i++) { //for loop that inputs the first line that was prev. read for length
      map[0][i] = mazeLength.substring(i,i+1); 
    }
    
    while (fileInput.hasNext()) { // reads from the file
      for (int j = 1; j < mazeLength.length(); j++) {
        mazeLength = fileInput.nextLine();
        for (int i = 0; i < mazeLength.length(); i++) {
          map[j][i] = mazeLength.substring(i,i+1);
        }
      }
    }
    fileInput.close();
    
    return map;
  }
  
  /** 
   * This method determines coordinates of all "P" along all paths to the "F" in a maze
   * Uses recursion and adds on to the global variable during the runtime of the algorithm, ends when it reaches "F"
   * @param start indexes of first and second array, a 2-D array of the maze, ArrayList of the traps
   * @throws e
   */
  public static void travel(int y, int x, String[][] mazeB, ArrayList<Integer> currentTraps) throws Exception{
    
    ArrayList<Integer>trapCopy = new ArrayList<Integer>(); // create a copy of the traps passed through so far
    trapCopy.addAll(currentTraps); // add the current traps passed through, unique to the method
    
    String[][] maze = new String[mazeB[0].length][mazeB[0].length];
    for (int i = 0; i < mazeB[0].length; i++) { // create a copy of the maze entered
      for (int j = 0; j < mazeB[0].length; j++) 
      {
        maze[i][j] = mazeB[i][j];
      }
    }
    
    if (maze[y][x].equals("F")) { // base case, finishes when it reaches F
      trapList.add(currentTraps.get(currentTraps.size()-2)); // add the last trap coordinate of the second array
      trapList.add(currentTraps.get(currentTraps.size()-1)); // add the last trap coordinate of the first array
      // eventually, the direct paths to F will reach one "T", making it the minimum number of traps needed
      return;
    }
    
    if (maze[y][x].equals("P")) {
      maze[y][x] = "T";
      trapCopy.add(y); //add to the copied ArrayList 
      trapCopy.add(x); //add to the copied ArrayList 
    } else {
      maze[y][x] = "."; // prevent backtracking in maze
    }
    
    //recursion
    if (maze[y][x-1].equals(" ") || maze[y][x-1].equals("P") || maze[y][x-1].equals("F")) {
      travel(y , x-1, maze, trapCopy);
    }
    if (maze[y+1][x].equals(" ") || maze[y+1][x].equals("P") || maze[y+1][x].equals("F")) {
      travel(y+1, x, maze, trapCopy);
    }
    if (maze[y][x+1].equals(" ") || maze[y][x+1].equals("P")|| maze[y][x+1].equals("F")) {
      travel(y, x+1, maze, trapCopy);
    }
    if (maze[y-1][x].equals(" ") || maze[y-1][x].equals("P") || maze[y-1][x].equals("F")) {
      travel(y-1, x, maze, trapCopy);
    }
    
  }
  
  /** 
   * Replaces "P" in the 2-D array with "T" with the indexes from an ArrayList
   * @param the 2-D array of the maze
   * @return the maze with the traps laid down
   */
  public static String[][] trapSetter(String[][] maze) {
    for (int i = trapList.size(); i > 1; i-=2) { //goes through the list by 2 at a time to use pairs of coordinates
      maze[trapList.get(i-2)][trapList.get(i-1)] = "T"; 
    }
    return maze;
  }
  
  /** 
   * Determines the longest path to a trap by recursion, through the use of the parameters of
   * the starting indexes of the 2-D array, and the initial amount of steps taken
   * Uses recursion to determine the longest path to a "T" by recording all direct paths to it and comparing it to the 
   * global variable
   * @param start indexes of first and second array, the 2-D array of the maze, and the initial steps taken (default 0)
   */
  public static void captureSteps(int y, int x, String[][] maze, int steps) { 
    String[][] mazeCopy = new String[maze[0].length][maze[0].length];
    for (int i = 0; i < maze[0].length; i++) { // generate a copy of the map
      for (int j = 0; j < maze[0].length; j++) 
      {
        mazeCopy[i][j] = maze[i][j];
      }
    }
    
    int stepsCopy = steps + 1; //add a step
    if (mazeCopy[y][x].equals("T")) { // base case, the first "T" as the finish, path is complete
      if (stepsCopy > longestTrap) { // determines if the path taken is greater than the answer
        longestTrap = stepsCopy; //replaces global variable
      }
      return;
    }   
    mazeCopy[y][x] = "."; // prevent backtracking in maze
    
    //recursion
    if (maze[y][x-1].equals(" ") || maze[y][x-1].equals("P") || maze[y][x-1].equals("T")) {
      captureSteps(y, x-1, mazeCopy, stepsCopy);
    }
    if (maze[y+1][x].equals(" ") || maze[y+1][x].equals("P") || maze[y+1][x].equals("T"))  {
      captureSteps(y+1, x, mazeCopy, stepsCopy);
    }
    if (maze[y][x+1].equals(" ") || maze[y][x+1].equals("P") || maze[y][x+1].equals("T")) {
      captureSteps(y, x+1, mazeCopy, stepsCopy);
    }
    if (maze[y-1][x].equals(" ") || maze[y-1][x].equals("P") || maze[y-1][x].equals("T")) {
      captureSteps(y-1, x, mazeCopy, stepsCopy);
    }
    
  }
  
  /** 
   * This method uses the starting indexes of the 2-D array with a integer with the number
   * of traps to modify a global integer variable, uses recursion to find all paths that go through all traps but 
   * will only change the global variable if the current path is smaller than it
   * @param start indexes of first and second array, the 2-D array of the maze, the numbers of traps in the map, and the initial amount of steps
   * taken (default 0)
   */
  public static void layTrapTime(int y, int x, String[][] maze, int trapCount, int trapSteps) {
    String[][] mazeCopy = new String[maze[0].length][maze[0].length];
    for (int i = 0; i < maze[0].length; i++) { // generate a copy of the maze
      for (int j = 0; j < maze[0].length; j++) 
      {
        mazeCopy[i][j] = maze[i][j];
      }
    }
    int trapCountCopy = trapCount; // generate a copy of the parameter to make it unique for the method
    int trapStepsCopy = trapSteps; // generate a copy of the parameter to make it unique for the method
    
    if (trapCountCopy <= 0) { // base case, when there's no more traps to travel to, path is complete
      if (trapStepsCopy < trapTime) { // determines if the path taken is smaller than current solution
        trapTime = trapSteps - 1; //deduct the extra step taken from the start and replace the global variable
      }
      return;
    }
    if (mazeCopy[y][x].equals("T")) { //reduce the amount of traps needed to find if one is found
      trapCountCopy -= 1;
    } 
    mazeCopy[y][x] = ".";// prevent back tracking in maze
    
    //recursion
    if (maze[y][x-1].equals(" ") || maze[y][x-1].equals("P") || maze[y][x-1].equals("T")) {
      layTrapTime(y , x-1, mazeCopy, trapCountCopy, trapStepsCopy+1);
    }
    if (maze[y+1][x].equals(" ") || maze[y+1][x].equals("P") || maze[y+1][x].equals("T")) {
      layTrapTime(y+1, x, mazeCopy, trapCountCopy, trapStepsCopy+1);
    }
    if (maze[y][x+1].equals(" ") || maze[y][x+1].equals("P")|| maze[y][x+1].equals("T")) {
      layTrapTime(y, x+1, mazeCopy, trapCountCopy, trapStepsCopy+1);
    }
    if (maze[y-1][x].equals(" ") || maze[y-1][x].equals("P") || maze[y-1][x].equals("T")) {
      layTrapTime(y-1, x, mazeCopy, trapCountCopy, trapStepsCopy+1);
    }
    
  }
  
  /** 
   * This method outputs the solution of the maze on to a separate text file by using the
   * parameters of the 2-D array of the maze, the capture time, and the trap time
   * @param 2-D array of the maze
   * @throws e
   */
  public static void solution(String[][] mapSolution) throws Exception{
    File solution = new File("solution.txt");
    PrintWriter output = new PrintWriter(solution);
    
    for (int i = 0; i < mapSolution[0].length; i++) { // output the map onto a text file
      for (int j = 0; j < mapSolution[0].length; j++) {
        output.print(mapSolution[i][j]);
      }
      output.println("");
    }
    
    output.println("Steps to capture monster: " + longestTrap);
    output.println("Steps to set traps: " + trapTime);
    output.close();
    System.out.println("Solution printed in solution.txt!");
  }
  
}
