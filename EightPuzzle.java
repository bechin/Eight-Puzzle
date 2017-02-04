import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
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
					System.out.println("How many random puzzles?");
					int numOfRands = Integer.parseInt(kb.nextLine());
					for(int i = 0; i < numOfRands; i ++){
						print(randomPuzzle());
						System.out.println();
					}
					break;
				case 2:
					System.out.println("on a single line, " +
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
			Tree tree = new Tree(state, null);
			PriorityQueue<Tree> frontier = new PriorityQueue<>();
			HashSet<Tree> visited = new HashSet<>();
			frontier.add(tree);
			visited.add(tree);
			visited.addAll(expand(frontier.poll()), visited);
			
		}
	}

	private ArrayList<Tree> expand(Tree state, HashSet<Tree> visited){
		List<Tree> children = new ArrayList<>();
		
	}

	private boolean isSolution(String state){
		return misplacedTiles(state) == 0;
	}

	private int misplacedTiles(String state){
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

	private int manhattanDistance(String state){
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

	public boolean getHeuristicIsMisplacedTiles(){
		if(heuristicIsMisplacedTiles == null){
			heuristicIsMisplacedTiles = parent.getHeuristicIsMisplacedTiles();
		}
		return heuristicIsMisplacedTiles;
	}

	public String getData(){
		return state;
	}

	public void setChildren(ArrayList<Tree> children){
		this.children = children;
	}

	private int evaluate(){
		int result = getPathCost();
		result += (getHeuristicIsMisplacedTiles())? EightPuzzle.misplacedTiles(state) :
													EightPuzzle.manhattanDistance(state);
		return result;
	}

	@Override //this calculates the evaluation function f(n) = g(n) + h(n)
	public int compareTo(Tree that){
		return this.evaluate() - that.evaluate();
	}

	@Override
	public boolean equals(Object other){
	    Tree that = (Tree)other;
		return this.state.equals(that.getData());
	}

}
