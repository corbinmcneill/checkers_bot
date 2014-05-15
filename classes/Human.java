// ALL CORBIN, AARON CONTRIBUTED TO inputMove

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;

public class Human extends Player {
	//stores the Scanner object for taking input
	private Scanner in;
	
	public Human (String startColor, boolean startsOnZeroSide, Robot startGameRobot, AIEngine startBrain) {
		//calls the Player constructor with the same arguments
		super(startColor, startsOnZeroSide, startGameRobot, startBrain);
	}

	public Human (String startXO, boolean startOnZeroSide, AIEngine startBrain) {
		//calls the Player constructor with the same arguments
		super(startXO, startOnZeroSide, startBrain);
		//creates a Scanner object to take input from the command line
		this.in = new Scanner(System.in);
	}

	public Move takeTurn(Game g) {
		//gets the last move made by the human player
		Move m = this.inputMove(g);
		//performs the move on the game board
		super.performMove(m, g.getGameBoard());
		//returns the move that was made
		return m;
	}

	public Move inputMove(Game g) {
		// THIS DOES NOT APPEAR TO DIFFERENTIATE BETWEEN DIFFERENT MOVES WITH SAME START AND ENDPOINTS
		if (this.getRobot()!=null) {
			final Robot r = this.getRobot();
			//starts concurrent thread waiting for button press
			Thread t = new Thread() {
				public void run() {
					r.waitForSensorPress();
				}
			};
			t.start();

			//creates pair of lists to hold scanned values
			ArrayList<int[]> scannedLocations = new ArrayList<int[]>();
			ArrayList<String> locationValues = new ArrayList<String>();
			//gets list of moves from best to worst
			Move[] possibleMoves = this.getBrain().rankBestMove(this.getAllMoves(g), g, this, 1);
			
			//waits for button thread to finish
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			//iterates over all possible moves
			for (Move m : possibleMoves) {
				System.out.print("\n");
				//gets all waypoints of the move
				int[][] waypoints = m.getWaypoints();
				//declares pointColor variable
				String pointColor;
				
				//sets origin waypoint of piece
				int[] waypoint = waypoints[0];
				if (scannedLocations.contains(waypoint)) {
				System.out.println("Examining: "+waypoint[0]+","+waypoint[1]+"\n");
					//sets pointColor to the previously stored value
					System.out.println("Already know color: "+locationValues.get(scannedLocations.indexOf(waypoint))+"\n");
					pointColor = locationValues.get(scannedLocations.indexOf(waypoint));
				} else {
					//scans the board to get pointColor
					pointColor = this.getRobot().examineLocation(waypoint);
					System.out.println("Color found: "+pointColor+"\n");
					//stores the color in the pair of arrays holding the scanned locations and values
					scannedLocations.add(waypoint);
					locationValues.add(pointColor);
				}
				//breaks and continues to the next move if the square is not empty
				if (!pointColor.equals(Robot.BOARD_COLOR)) {
					System.out.println("The piece was NOT moved. Moving on to next move\n");
					continue;
				}

				System.out.println("Piece WAS moved. Checking potential endpoint");
				
				//sets last waypoint
				waypoint = waypoints[waypoints.length-1];
				//checks the color of the last waypoint
				System.out.println("Examining: "+waypoint[0]+","+waypoint[1]+"\n");
				if (scannedLocations.contains(waypoint)) {
					//sets pointColor to the previously stored value
					pointColor = locationValues.get(scannedLocations.indexOf(waypoint));
					System.out.println("Already know color: "+pointColor+"\n");
				} else {
					//scans the board to get pointColor
					pointColor = r.examineLocation(waypoint);
					System.out.println("Color found: "+pointColor+"\n");
					//stores the color in the pair of arrays holding the scanned locations and values
					scannedLocations.add(waypoint);
					locationValues.add(pointColor);
				}
				//returns the move if the final square matches the color of this player's pieces
				if (pointColor.equals(this.getColor())) {
					if (waypoints.length!=2) {
						for (Move like_m : possibleMoves) {
							boolean is_correct = true;
							int[][] m_waypoints = m.getWaypoints();
							Integer[] first_m = Human.convert(m_waypoints[0]);
							Integer[] last_m = Human.convert(m_waypoints[m_waypoints.length-1]);
							int[][] like_m_waypoints = like_m.getWaypoints();
							Integer[] first_like_m = Human.convert(like_m_waypoints[0]);
							Integer[] last_like_m = Human.convert(like_m_waypoints[like_m_waypoints.length - 1]);
							if (Arrays.deepEquals(first_m, first_like_m) && Arrays.deepEquals(last_m, last_like_m)) {
								for (Piece jumped_piece : like_m.calculatePiecesToJump()) {
									String piece_color;
									System.out.println("Examining: "+waypoint[0]+","+waypoint[1]+"\n");
									if (scannedLocations.contains(waypoint)) {
										//sets pointColor to the previously stored value
										piece_color = locationValues.get(scannedLocations.indexOf(waypoint));
										System.out.println("Already know color: "+piece_color+"\n");
									} else {
										//scans the board to get pointColor
										piece_color = r.examineLocation(waypoints[waypoints.length-1]);
										System.out.println("Color found: "+piece_color+"\n");
										//stores the color in the pair of arrays holding the scanned locations and values
										scannedLocations.add(waypoint);
										locationValues.add(piece_color);
									}
						
									if (!piece_color.equals(Robot.BOARD_COLOR)) {
										is_correct = false;
										break;
									} 
								}
								if (is_correct) {
									return like_m;
								}
							}
						}
						System.out.println("Start and end points were correct, but no moves matched");
						return null;
					} else {
						return m;
					}
				}
				System.out.println("Endpoint is not the Human's color. Checking next move...\n");
			}
			
			//failed to find the right move
			return null;
		
		} else {
			// super.getBoard().printBoard();
			
			boolean moveEntered = false;
			Move inputtedMove = null;
			while (!moveEntered) {
				System.out.println("Enter Move:");
				String inputLine = this.in.nextLine();
				if ((inputLine.length()+1)%3==0) {
					inputLine = inputLine + " ";
				}
				if (inputLine.length()%3==0 && inputLine.length()>=5) {

					int numberOfWaypoints = inputLine.length()/3;
					String[] waypointStrings = new String[numberOfWaypoints];
					int[][] allWaypoints = new int[numberOfWaypoints][];
					for (int i=0; i<numberOfWaypoints; i++) {
						waypointStrings[i] = inputLine.substring(3*i,3*(i+1));
					}
				
					for (int i=0; i<numberOfWaypoints; i++) {
						allWaypoints[i] = new int[] {Integer.parseInt(inputLine.substring(3*i, 3*i+1)),Integer.parseInt(inputLine.substring(3*i+1, 3*i+2))};
					}
					inputtedMove = new Move(this.getBoard().getPieceAtLocation(allWaypoints[0]), allWaypoints);
					if (inputtedMove.getMovePiece()!=null && inputtedMove.getMovePiece().getPlayer()==this && inputtedMove.calculateIsValid(g)) {
						moveEntered = true;
					}
				}
			}
			return inputtedMove;
		}
	}

	public static Integer[] convert(int[] old) {
		Integer[] result = new Integer[old.length];
		for (int i = 0; i<old.length; i++) {
			result[i] = Integer.valueOf(old[i]);
		}
		return result;
	}
}
