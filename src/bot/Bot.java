package bot;

import java.util.ArrayList;

import exceptions.Checkmate;
import exceptions.Draw;
import exceptions.Promotion;
import game.Engine;
import game.GameConstants;
import geometry.Point;

public class Bot {
	
	Engine engine;
	
	public Bot(Engine engine){
		this.engine = engine;
	}
	
	//Daje random broj
	private int randomize(int max){
		return ((int) Math.floor(Math.random()*max));
	}
	
	//Odigra potez//
	public void play() throws Draw, Checkmate {
		Point move = null;
		ArrayList<Point> moveableFigures;
		Point figura = null;
		try {
			moveableFigures = engine.getMovableFigures();//Uzima neku figuru koja moze da se pomera//
			if(moveableFigures == null || moveableFigures.size() ==0)
				System.out.println("0 ili null");
			figura = moveableFigures.get(randomize(moveableFigures.size()));
			if(figura == null)
				System.out.println("figura null");
			ArrayList<Point> posibleMoves = engine.getPossibleMoves(figura.getI(), figura.getJ());
			if(posibleMoves == null || posibleMoves.size() == 0){
				System.out.println("pos figure null || 0");
				if(posibleMoves == null)
					System.out.println("null");
				else 
					System.out.println("0");
			}
			//Uzima neku od mogucih pozicija
			move = posibleMoves.get(randomize(posibleMoves.size()));
			if(move == null)
				System.out.println("move null");
			try {
				engine.playMove(move);
			} catch (Promotion e) {
				int rand = randomize(4);
				switch (rand) {
				case 0:
					rand = GameConstants.FIGURE_BISHOP;
					break;
				case 1:
					rand = GameConstants.FIGURE_KING;
					break;
				case 2:
					rand = GameConstants.FIGURE_QUEEN;
					break;
				case 3:
					rand = GameConstants.FIGURE_ROOK;
					break;
				}
				engine.zamena(move.getI(), move.getJ(), rand);
			}
		} catch (Draw e1) {
			throw new Draw();
		} catch (Checkmate e1) {
			// TODO Auto-generated catch block
			throw new Checkmate();
		}
		
	}

	
}
