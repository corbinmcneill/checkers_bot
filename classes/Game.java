public class Game {

	private Board gameBoard;
	private Robot gameRobot;
	private Player player1;
	private Player player2;
	private Move[] lastFewMoves;

	public Game (Player p1, Player p2) {
		this.gameBoard = new Board(new Player[]{p1, p2});
		this.player1 = p1;
		this.player2 = p2;
		this.player1.setBoard(gameBoard);
		this.player2.setBoard(gameBoard);
	}
	
	public Game (Player p1, Player p2, Robot startGameRobot) {
		this(p1,p2);
		this.gameRobot = startGameRobot;
	}
	
	public Game (Game oldG, Board b) {
		this.player1 = oldG.getPlayers()[0];
		this.player2 = oldG.getPlayers()[1];
		this.gameBoard = b;
	}

	public Player play() {
		while (player1.getAllMoves(this.gameBoard).length>0 && player2.getAllMoves(this.gameBoard).length>0) {
			this.player1.takeTurn(this);
			// this.gameBoard.printBoard();
			if (player1.getAllMoves(this.gameBoard).length>0 && player2.getAllMoves(this.gameBoard).length>0) {
				this.player2.takeTurn(this);
				// this.gameBoard.printBoard();
			}
		}
		if (this.gameBoard.totalPiecesLeft(player1)>0) {
			return this.player1;
		}
		else {
			return this.player2;
		}
	}

	public Board getGameBoard () {
		return this.gameBoard;
	}

	public Player[] getPlayers () {
		return new Player[]{this.player1,this.player2};
	}

	public Player getOtherPlayer(Player p) {
		if (this.player1==p) {
			return this.player2;
		} else {
			return this.player1;
		}
	}
}