import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashSet;
import java.util.PriorityQueue;

public class EightPuzzle{

	private Scanner kb = new Scanner(System.in);
	private boolean heuristicIsMisplacedTiles;
	private List<Integer> digits = new ArrayList<>();

	public static void main(String[] args){
		new EightPuzzle(args);
	}

	public EightPuzzle(String[] args){
		if(args.length == 1){
			printData(args[0]);
			System.exit(0);
		}
		int input = 0;
		for(int i = 0; i < 9; i++){
			digits.add(i);
		}
		System.out.println("Eight Puzzle\n" +
						   "------------\n" +
						   "1) Misplaced tiles\n" +
						   "2) Manhattan distance\n");
		input = Integer.parseInt(kb.nextLine());
		heuristicIsMisplacedTiles = input == 1;
		String header = heuristicIsMisplacedTiles?
						"A* with the Misplaced Tiles Heuristic":
						"A* with the Manhattan Distance Heuristic";
		while(input != 3){
			System.out.println(header + "\n" +
							   "------------\n" +
							   "1) Random puzzles\n" +
							   "2) Custom puzzle\n" +
							   "3) Exit");
			input = Integer.parseInt(kb.nextLine());
			switch(input){
				case 1:
/*					System.out.println("Depth?");
					int depth = Integer.parseInt(kb.nextLine());
					if(depth != 0){
						System.out.println("How many random puzzles?");
						int numOfRands = Integer.parseInt(kb.nextLine());
						String[] randomPuzzles = new String[numOfRands];
						for(int i = 0; i < numOfRands; i ++){
							String r = randomPuzzle(depth);
							randomPuzzles[i] = r;
							print(r);
							System.out.println("-------------");
						}
						solve(randomPuzzles);
						break;
					}*/
					System.out.println("How many random puzzles?");
					int numOfRands = Integer.parseInt(kb.nextLine());
					String[] randomPuzzles = new String[numOfRands];
					for(int i = 0; i < numOfRands; i ++){
						String r = randomPuzzle();
						randomPuzzles[i] = r;
						print(r);
						System.out.println("-------------");
					}
					solve(randomPuzzles);
					break;
				case 2:
					System.out.println("On a single line, " +
									   "please enter a solvable puzzle.");
					String customPuzzle = kb.nextLine();
					if(solvable(customPuzzle)){
						solve(customPuzzle);
					}
					else{
						System.out.println("Not a solvable puzzle!");
					}
					break;
				case 3:
					System.out.println("Goodbye!");
					break;
				default:
					System.out.println("Invalid input!");
			}
		}
	}

	private void printData(String state){
		System.out.println("Misplaced tiles: " + misplacedTiles(state));
		System.out.println("Manhattan Distance: " + manhattanDistance(state));
		System.out.println("Proper format: ");
		print(state);
		System.out.println("Is it solvable?: ");
		System.out.println(solvable(state));
	}

	private void solve(String... states){
		for(String state : states){
			Tree tree = new Tree(state, null, heuristicIsMisplacedTiles);
			PriorityQueue<Tree> frontier = new PriorityQueue<>();
			HashSet<String> visited = new HashSet<>();
			frontier.add(tree);
			visited.add(tree.getData());
			Tree current = tree;
			while(!visited.contains("012345678")){
				current = frontier.poll();
				List<String> children = expandToAllKids(current.getData());
				List<Tree> realKids = new ArrayList<>();
				for(String child : children){
					if(!visited.contains(child)){
						visited.add(child);
						frontier.add(new Tree(child, current, heuristicIsMisplacedTiles));
					}
				}
				current.setChildren(realKids);
			}
			current = frontier.poll();
			print(current.getData());
			System.out.println("Total moves: " + current.getPathCost());
			if(states.length == 1){
				printPath(current);
			}
		}
	}

	private List<String> expandToAllKids(String parent){
		List<String> allChildren = new ArrayList<>();
		int index = parent.indexOf("0");
		char[] right, left, top, bottom;
		switch(index%3){ //checking columns
			case 0:	//left
				right = parent.toCharArray();
				swap(right, index, index+1);
				allChildren.add(String.valueOf(right));
				break;
			case 1: //center
				right = parent.toCharArray();
				left = parent.toCharArray();
				swap(right, index, index+1);
				swap(left, index, index-1);
				allChildren.add(String.valueOf(right));
				allChildren.add(String.valueOf(left));
				break;
			case 2: //right
				left = parent.toCharArray();
				swap(left, index, index-1);
				allChildren.add(String.valueOf(left));
				break;
		}
		switch(index/3){ //checking rows
			case 0: //top
				bottom = parent.toCharArray();
				swap(bottom, index, index+3);
				allChildren.add(String.valueOf(bottom));
				break;
			case 1:	//center
				bottom = parent.toCharArray();
				top = parent.toCharArray();
				swap(bottom, index, index+3);
				swap(top, index, index-3);
				allChildren.add(String.valueOf(bottom));
				allChildren.add(String.valueOf(top));
				break;
			case 2: //bottom
				top = parent.toCharArray();
				swap(top, index, index-3);
				allChildren.add(String.valueOf(top));
				break;
		}
		return allChildren;
	}
	
	private void swap(char[] array, int first, int second){
		char temp = array[first];
		array[first] = array[second];
		array[second] = temp;
	}

	private boolean isSolution(String state){
		return misplacedTiles(state) == 0;
	}

	public static int misplacedTiles(String state){
		int result = 0;
		for(int i = 0; i < 9; i++){
			if(Character.getNumericValue(state.charAt(i)) != i){
				result++;
			}
		}
		return result;
	}

	private	boolean solvable(String state){
		int numOfInversions = 0;
		for(int i = 0; i < 8; i++){
			for(int j = i+1; j < 9; j++){
				if(state.charAt(i) != '0' && state.charAt(j) != '0'){
					if(Character.getNumericValue(state.charAt(i)) >
					   Character.getNumericValue(state.charAt(j)) ){
						numOfInversions++;
					}
				}
			}
		}
		return (numOfInversions & 1) == 0; //isEven
	}

	public static int manhattanDistance(String state){
		int result = 0;
		for(int i = 0; i < 9; i++){
			if(Character.getNumericValue(state.charAt(i)) != i){
				int whereIs = state.indexOf(String.valueOf(i));
				result += Math.abs(i % 3 - whereIs % 3);
				result += Math.abs(i / 3 - whereIs / 3);
			}
		}
		return result;
	}

	private void printPath(Tree state){
		if(state.getParent() != null){
			printPath(state.getParent());
		}
		print(state.getData());
		System.out.println();
	}

	private void print(String state){
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				if(j != 0){
					System.out.print(" ");
				}
				System.out.print(state.charAt(3 * i + j));
			}
			System.out.println();
		}
	}

	private String randomPuzzle(){
		String result = "021345678";
		do{
			Collections.shuffle(digits);
			StringBuilder puzzle = new StringBuilder();
			for(Integer digit : digits){
				puzzle.append(digit);
			}
			result = puzzle.toString();
		}while(!solvable(result));
		return result;
	}

	private String randomPuzzle(int depth){
		String solution = "012345678";
		HashSet<String> visited = new HashSet<>();
		Tree tree = new Tree(solution, null, heuristicIsMisplacedTiles);
		Tree result = tree;
		while(result.getPathCost() != depth){
			List<String> allChildren = expandToAllKids(result.getData());
			List<String> realKids = new ArrayList<>();
			for(String child: allChildren){
				if(!visited.contains(child)){	
					realKids.add(child);
				}
				visited.add(child);
			}
			result = new Tree(realKids.get((int)(Math.random()*realKids.size())), result, heuristicIsMisplacedTiles);
		}
		return result.getData();
	}

}

class Tree implements Comparable<Tree>{
	private Tree parent = null;
	private String state;
	private int pathCost = -1;
    private List<Tree> children = null;
	private Boolean heuristicIsMisplacedTiles = null;

	public Tree(String state, Tree parent, boolean heuristicIsMisplacedTiles) {
		this.parent = parent;
	    this.state = state;
		this.heuristicIsMisplacedTiles = heuristicIsMisplacedTiles;
	}

	public int getPathCost(){
		if(parent == null){
			pathCost = 0;
		}
		else if(pathCost == -1){
			pathCost = parent.getPathCost() + 1;
		}
		else{}
		return pathCost;
	}

	public String getState(){
		return state;
	}

	public boolean getHeuristicIsMisplacedTiles(){
		if(heuristicIsMisplacedTiles == null){
			heuristicIsMisplacedTiles = parent.getHeuristicIsMisplacedTiles();
		}
		return heuristicIsMisplacedTiles;
	}

	public String getData(){
		return state;
	}

	public void setChildren(List<Tree> children){
		this.children = children;
	}

	public List<Tree> getChildren(){
		return children;
	}

	public void setParent(Tree parent){
		this.parent = parent;
	}

	public Tree getParent(){
		return parent;
	}

	//this calculates the evaluation function f(n) = g(n) + h(n)
	private int evaluate(){
		int result = getPathCost();
		result += (getHeuristicIsMisplacedTiles())? EightPuzzle.misplacedTiles(state) :
													EightPuzzle.manhattanDistance(state);
		return result;
	}

	@Override
	public int compareTo(Tree that){
		return this.evaluate() - that.evaluate();
	}

	@Override
	public boolean equals(Object other){
	    Tree that = (Tree)other;
		return this.state.equals(that.getData());
	}

}
