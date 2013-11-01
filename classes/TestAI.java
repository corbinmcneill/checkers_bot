public class TestAI extends AIEngine {

	public Move[] rankBestMove(Move[] unrankedMoves, Game g, Player p, int recursionDepth) {
		//creates array to hold all possible moves
		Move[] unrankedMoves = this.getAllMoves(g.getGameBoard());

		//creates array to hold values of boards
		double[] boardValues = new double[unrankedMoves.length];

		//if recursionDepth is one, calculate direct values of moves
		if (recursionDepth==1) {
			//iterates over all moves and calculates values to put in boardValues
			for (int i = 0; i<unrankedMoves.length; i++) {
				boardValues[i] = (new Board(g.getGameBoard(), unrankedMoves[i])).calculateValue(p);
			}

		//recursionDepth must be greater than one, so get values of the best opponent moves for each possible move
		} else {
			//iterates over all moves and calculates value based on best opponent move
			for (int i = 0; i<unrankedMoves.length; i++) {
				boardValues[i] = this.valueOfMoves(new Game(g, unrankedMoves[i]), recursionDepth-1, true, p);
			}
		}

		//creates array to hold sorted values from lowest to highest
		double[] boardValuesSorted = Arrays.copyOf(boardValues, boardValues.length);
		Arrays.sort(boardValuesSorted);

		//creates array to hold final list of moves
		Move[] sortedMoves = new Move[unrankedMoves.length];
		//creates variable to hold index of move in original list
		int index = 0;
		//iterates over all values of boardValuesSorted
		for (int i = 0; i<boardValuesSorted.length; i++) {
			//finds the index of the current values in the original move list
			index = ArraysHelper.find(boardValues, boardValuesSorted[i]);
			//sets the value at index to very large so that the same move is not used again, even if multiple moves have equal values
			boardValues[index] = Board.maxBoardValue*1000;

			//puts the correct move in the correct position in the final array
			sortedMoves[unrankedMoves.length-i-1] = unrankedMoves[index];
		}

		//logs values for debugging
		// g.getGameBoard().printBoard();
		// for (Move m : sortedMoves) {
		// 	System.out.println(Arrays.deepToString(m.getWaypoints()));
		// }
		// System.out.print("" + recursionDepth + " ");
		// System.out.println(Arrays.toString(boardValuesSorted));

		return sortedMoves;
	}

	private double valueOfMoves(Game g, int recursionDepth, boolean testOpponentMoves, Player p) {
		if (g.isDraw()) {
			// System.out.println(Arrays.deepToString(g.getLastFewMoves()));
			// System.out.println("Detected possible draw");
			return 0;
		}

		//creates array to hold all possible moves
		Move[] moves;
		if (testOpponentMoves) {
			moves = g.getOtherPlayer(p).getAllMoves(g.getGameBoard());
		} else {
			moves = this.getAllMoves(g.getGameBoard());
		}

		if (moves.length==0) {
			if (testOpponentMoves) {
				return 2*Board.maxBoardValue;
			} else {
				return -2*Board.maxBoardValue;
			}
		}

		//creates array to hold values of boards
		double[] boardValues = new double[moves.length];

		//if recursionDepth is one, calculate direct values of moves
		if (recursionDepth==1) {
			//iterates over all moves and calculates values to put in boardValues
			for (int i = 0; i<moves.length; i++) {
				boardValues[i] = (new Board(g.getGameBoard(), moves[i])).calculateValue(p);
			}
		
		//recursionDepth must be greater than one, so get values of the best opponent moves for each possible move
		} else {
			//iterates over all moves and calculates value based on best opponent move
			for (int i = 0; i<moves.length; i++) {
				boardValues[i] = p.valueOfMoves(new Game(g, moves[i]), recursionDepth-1, !testOpponentMoves);
			}
		}

		//creates array to hold sorted values from lowest to highest
		double[] boardValuesSorted = Arrays.copyOf(boardValues, boardValues.length);
		Arrays.sort(boardValuesSorted);

		//creates variable to hold result value
		double result;
		if (testOpponentMoves) {
			result = boardValuesSorted[0];
		} else {
			result = boardValuesSorted[boardValuesSorted.length-1];
		}

		//logs the values for debugging
		// g.getGameBoard().printBoard();
		// Move[] sortedMoves = new Move[moves.length];
		// int index = 0;
		// for (int i = 0; i<boardValuesSorted.length; i++) {
		// 	index = ArraysHelper.find(boardValues, boardValuesSorted[i]);
		// 	boardValues[index] = Board.maxBoardValue*1000;
		// 	sortedMoves[moves.length-i-1] = moves[index];
		// }
		// for (Move m : sortedMoves) {
		// 	System.out.println(Arrays.deepToString(m.getWaypoints()));
		// }
		// System.out.print("" + recursionDepth + " ");
		// System.out.println(Arrays.toString(boardValuesSorted));
		// System.out.println(result / 10);

		return result;
	}

}