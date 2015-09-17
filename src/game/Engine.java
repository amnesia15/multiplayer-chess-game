package game;

import geometry.Point;
import java.util.ArrayList;
import javafx.scene.image.*;
import exceptions.Checkmate;
import exceptions.Draw;
import exceptions.Promotion;
import figure.*;
import bot.Bot;

public class Engine implements GameConstants{

	private Figure[][] board;
	private int onMove;
	private ArrayList<Move> moves = new ArrayList<>();
	private Point selFigure;
	private int typeOfGame;
	private int team;
	private Bot bot = null;

	private ArrayList<Point> movFigures = new ArrayList<Point>();
	private ArrayList<Point> attackers = new ArrayList<Point>();
	
	public Engine(int typeOfGame,int team){
		newGame();
		this.typeOfGame = typeOfGame;
		this.team = team;
		if(typeOfGame == GAME_MODE_BOTS)
			bot = new Bot(this);
	}

	//Kreira novu tablu\\
	private void initializeBoard() {
		board = new Figure[8][8];
		
		for (int i = 2; i < 6; i++)
			for (int j = 0; j < 8; j++)
				board[i][j] = null;
		
		for (int i = 0; i < 8; i++) {
			board[1][i] = new Pawn(-1);
			board[6][i] = new Pawn(1);		
		}
		
		board[0][0] = new Rook(-1);
		board[0][7] = new Rook(-1);
		board[7][0] = new Rook(1);
		board[7][7] = new Rook(1);
		
		
		board[0][1] = new Knight(-1);
		board[0][6] = new Knight(-1);
		board[7][1] = new Knight(1);
		board[7][6] = new Knight(1);
		
		board[0][2] = new Bishop(-1);
		board[0][5] = new Bishop(-1);
		board[7][2] = new Bishop(1);
		board[7][5] = new Bishop(1);
		
		board[0][3] = new Queen(-1);
		board[7][3] = new Queen(1);
		board[0][4] = new King(-1);
		board[7][4] = new King(1);
		
		board[0][0].setEngine(this);
		selFigure = null;
		moves = new ArrayList<Move>();
	}

	//Kreira novu igru(Inicijalizuje novu tablu i resetuje ko je na potezu)\\
	public void newGame(){
		initializeBoard();
		onMove = 1;
	}

	public void invertTeam(){
		team *= -1;
	}
	
	//Klonira figuru za potez\\
	private Figure cloneFigure(Figure figure){
		Figure clonedFigure = null;
		
		if(figure instanceof Bishop)
			clonedFigure = new Bishop(figure.getTeam());
		else if(figure instanceof King)
			clonedFigure = new King(figure.getTeam());
		else if(figure instanceof Knight)
			clonedFigure = new Knight(figure.getTeam());
		else if(figure instanceof Pawn)
			clonedFigure = new Pawn(figure.getTeam());
		else if(figure instanceof Queen)
			clonedFigure = new Queen(figure.getTeam());
		else if(figure instanceof Rook)
			clonedFigure = new Rook(figure.getTeam());
		else 
			clonedFigure = null;
		
		return clonedFigure;
	}
	
	//Da li je figura pomerana od pocetka partije\\
	private boolean isMoved(Point figPosition){
		for (int i = 0; i < moves.size(); i++) {
			if(moves.get(i).getFrom().getI() == figPosition.getI() && moves.get(i).getFrom().getJ() == figPosition.getJ())
				return true;
		}
		return false;
	}
	
	//Da li polju preti neka figura\\
	private boolean isAttacked(Point poistion){
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				if(board[i][j] != null && board[i][j].getTeam() != onMove && Point.exists(poistion,board[i][j].possibleMoves(i, j)))
						return true;
			}
		}		
		return false;
	}

	//Proverava da li moze da se odigra mala rokada, ako ne moze vraca NULL\\
	private Point mozeMalaRokada(){
		Point a = null;
		if(onMove == 1){//Beli igrac
			if(isAttacked(new Point(7, 4)))
				return null;
			if(!isMoved(new Point(7, 4)) && !isMoved(new Point(7, 7))){
				for(int j=5;j<7;j++)
					if(board[7][j]!=null || isAttacked(new Point(7, j)))
						return null;
					
				a = new Point(7, 6);
			}
		} else {//Crni igrac
			if(isAttacked(new Point(0, 4)))
				return null;
			if(!isMoved(new Point(0, 4)) && !isMoved(new Point(0, 7))){
				for(int j=5;j<7;j++)
					if(board[0][j] !=null || isAttacked(new Point(0, j)))
						return null;
					
				a = new Point(0, 6);
			}
		}
		
		return a;
	}

	//Proverava da li moze da se odigra velika rokada, ako ne moze vraca NULL\\
	private Point mozeVelikaRokada(){
		Point a = null;
		if(onMove == 1){
			if(isAttacked(new Point(7, 4)))
				return null;
			if(!isMoved(new Point(7, 4)) && !isMoved(new Point(7, 0))){
				for(int j=1;j<4;j++)
					if(board[7][j]!=null || isAttacked(new Point(7, j)))
						return null;
					
				a = new Point(7, 2);
			}
		} else {
			if(isAttacked(new Point(0, 4)))
				return null;
			if(!isMoved(new Point(0, 4)) && !isMoved(new Point(0, 0))){
				for(int j=1;j<4;j++)
					if(board[0][j] !=null || isAttacked(new Point(0, j)))
						return null;
					
				a = new Point(0, 2);
			}
		}
		
		return a;
	}

	//Proverava da li moze da se odigra potez EnPasant, ako ne moze vraca NULL\\
	private Point mozeEnPassant(int i, int j){
		if ((board[i][j].getTeam() == 1 && i == 3) || (board[i][j].getTeam()==-1 && i == 4)){
			if((j-1 > -1 && board[i][j-1] instanceof Pawn) || (j+1<8 && board[i][j+1] instanceof Pawn)){
				Point from = getLastMove().getFrom();
				Point to = getLastMove().getTo();
				if(board[to.getI()][to.getJ()] instanceof Pawn && Math.abs(from.getI() - to.getI()) == 2)
						if(Math.abs(to.getJ()-j)==1)//ako je skocio dva polja i to je pored naseg pijuna
							return	new Point(to.getI()-board[i][j].getTeam(), to.getJ());
				
			}
		} 
		return null;
	}
	
	//Fu-ja koja u zavisnosti od polozaja koji su joj prosledjeni izvodi rokadu\\
	private void playRokada(int toi,int toj){
		if(toj==2) {//Velika rokada
			//Top
			board[toi][3] = board[toi][0];
			board[toi][0] = null;
			//kralj
			board[toi][2] = board[toi][4];
			board[toi][4] = null;
			moves.add(new Move(new Point(toi, 0), new Point(toi, 3), new Rook(onMove), new Rook(onMove)));
			moves.add(new Move(new Point(toi, 4), new Point(toi, 2), new King(onMove), new King(onMove)));
		} else if(toj==6){//Mala rokada
			board[toi][5] = board[toi][7];
			board[toi][7] = null;
			board[toi][6] = board[toi][4];
			board[toi][4] = null;
			moves.add(new Move(new Point(toi, 7), new Point(toi, 5), new Rook(onMove), new Rook(onMove)));
			moves.add(new Move(new Point(toi, 4), new Point(toi, 6), new King(onMove), new King(onMove)));
		}
		//Ovde uneti za move kao specijalni potez//
	}

	private void makeMove(Point from,Point to) throws Promotion {
		int fromi = from.getI(),fromj = from.getJ();
		int toi = to.getI(), toj = to.getJ();
		if (board[fromi][fromj] instanceof King && Math.abs(fromj-toj)==2) {
			playRokada(toi,toj);
		} else {
			//Provera za zamenu, da li je pijun stigao do kraja ;)
			if(board[fromi][fromj] instanceof Pawn){
				if(onMove == 1 && (toi == 0)){
						throw new Promotion();				
				} else if(onMove == -1 && (toi == 7)){
						throw new Promotion();
				}
				else {//Za EnPasant
					Point p = mozeEnPassant(fromi, fromj);
					if(p!=null){
						board[toi+onMove][toj] = null;
					}
				}
			}
			//kraj provere za zamenu
				board[toi][toj] = board[fromi][fromj];
				board[fromi][fromj] = null;
				moves.add(new Move(new Point(fromi, fromj), new Point(toi, toj),cloneFigure(board[fromi][fromj]),cloneFigure(board[toi][toj])));
			
		}
		attackers.clear();
		onMove *= -1;
	}
	
	//Menja figuru kada pijun stigne do kraja table\\
	public void zamena(int i, int j,int figura){
		Figure fig = null;
		switch(figura){
		case GameConstants.FIGURE_BISHOP://Bishop
			fig = new Bishop(onMove);
			break;
		case GameConstants.FIGURE_KNIGHT://Knight
			fig = new Knight(onMove);
			break;
		case GameConstants.FIGURE_QUEEN://queen
			fig = new Queen(onMove);
			break;
		case GameConstants.FIGURE_ROOK://Rook
			fig = new Rook(onMove);
			break;
		}

		board[i][j] = fig;
		board[selFigure.getI()][selFigure.getJ()] = null;

		moves.add(new Move(new Point(selFigure.getI(), selFigure.getJ()), new Point(i, j), new Pawn(onMove), cloneFigure(board[i][j])));
		attackers.clear();
		onMove *= -1;
	}
	
	public void playMove(Point position) throws Promotion{
		if(selFigure != null)
			makeMove(new Point(selFigure.getI(), selFigure.getJ()), position);
	}
	
	public void interpretMove(Move move){
		Point from = move.getFrom();
		Point to = move.getTo();
		Figure toFig = move.getToFig();
		try {
			makeMove(new Point(from.getI(), from.getJ()), new Point(to.getI(), to.getJ()));
		} catch (Promotion e) {
			if(toFig instanceof Bishop)
				zamena(to.getI(), to.getJ(), 1);
			else if(toFig instanceof Knight)
				zamena(to.getI(), to.getJ(), 2);
			else if(toFig instanceof Queen)
				zamena(to.getI(), to.getJ(), 3);
			else if(toFig instanceof Rook)
				zamena(to.getI(), to.getJ(), 4);
			board[from.getI()][from.getJ()] = null;
		}
	}
	
	//-----------Geteri---------------\\
	
	//Vraca celu tablu\\
	public Figure[][] getBoard(){
		return board;
	}
	
	//Daje sliku figure cije su koordinate prosledjene
	public Image getFigureImage(int i,int j){
		if(board[i][j] != null)
			return (new Image(board[i][j].getImage()));
		else 
			return null;
	}
	
	//Daje ko je na potezu ( 1 - beli igrac | -1 crni igrac )\\
	public int getOnMove(){
		return onMove;
	}

	public Move getLastMove(){
		return moves.get(moves.size()-1);
	}
	
	public int getTypeOfGame() {
		return typeOfGame;
	}

	public int getTeam() {
		return team;
	}

	public Bot getBot() {
		return bot;
	}
	
	//Vraca putanjom kojom figura napada drugu figuru\\
	private ArrayList<Point> getAttackingPath(Point attackerPosition,Point figurePosition){
		ArrayList<Point> attackPath = new ArrayList<>();
		//Dodajemo poziciju napadaca
		attackPath.add(attackerPosition);
		if(!(board[attackerPosition.getI()][attackerPosition.getJ()] instanceof Knight)){//Ako nije konj odma ima jos neko polje na koje moze da se stane, a da se blokira sah
			if(attackerPosition.getI()==figurePosition.getI()){//Napada Horizontalno
				if(attackerPosition.getJ()<figurePosition.getJ()){//napada sa leva na desno 
					for (int i = attackerPosition.getJ(); i < figurePosition.getJ(); i++) {
						attackPath.add(new Point(attackerPosition.getI(), i));
					}
				} else {//napada sa desna na levo
					for (int i = figurePosition.getJ(); i < attackerPosition.getJ(); i++) {
						attackPath.add(new Point(attackerPosition.getI(), i));
					}
				}
			} else if(attackerPosition.getJ()==figurePosition.getJ()){//Napada Vertikalno
				if(attackerPosition.getI()<figurePosition.getI()){//Napada na dole
					for (int i = attackerPosition.getJ(); i < figurePosition.getJ(); i++) {
						attackPath.add(new Point(i, attackerPosition.getJ()));
					}
				} else {//Napada na gore
					for (int i = figurePosition.getJ(); i < attackerPosition.getJ(); i++) {
						attackPath.add(new Point(i, attackerPosition.getJ()));
					}
				}
			} else {//Napada Dijagnoalno
				if(attackerPosition.getI()<figurePosition.getI()){//Napada na dole				
					if(attackerPosition.getJ()<figurePosition.getJ()){
						for (int i = 0; i < (figurePosition.getI()-attackerPosition.getJ()); i++) {//Napada Desno 
							attackPath.add(new Point(attackerPosition.getI()+i, attackerPosition.getJ()+i));
						}
					} else {
						for (int i = 0; i < (attackerPosition.getJ()-figurePosition.getI()); i++) {//Napada Levo
							attackPath.add(new Point(attackerPosition.getI()+i, attackerPosition.getJ()-i));
						}
					}
				} else {//Npada na gore
					if(attackerPosition.getJ()<figurePosition.getJ()){
						for (int i = 0; i < (figurePosition.getI()-attackerPosition.getJ()); i++) {//Napada Desno
							attackPath.add(new Point(attackerPosition.getI()-i, attackerPosition.getJ()+i));
						}
					} else {
						for (int i = 0; i < (attackerPosition.getJ()-figurePosition.getI()); i++) {//Napada Levo
							attackPath.add(new Point(attackerPosition.getI()-i, attackerPosition.getJ()-i));
						}
					}
				}
			}
		}
		return attackPath;
	}
		
	//Proverava da li bi se figurinim pomeranjem stvorio sah\\
	private boolean proveri(Point kingPos,Point sPos, Point nPos,ArrayList<Point> drugeFigure){
		boolean ind = false;
		Figure fig = board[nPos.getI()][nPos.getJ()];
		board[nPos.getI()][nPos.getJ()] = board[sPos.getI()][sPos.getJ()];
		board[sPos.getI()][sPos.getJ()] = null;
		//Ako kralj nema napdaca posle pomeranja figure, figura moze da stane na to mesto
		if(getAttackers(kingPos,drugeFigure).size() == 0) 
			ind = true;
		board[sPos.getI()][sPos.getJ()] = board[nPos.getI()][nPos.getJ()];
		board[nPos.getI()][nPos.getJ()] = fig;	
		return ind;
	}
	
	//Uzima sve figure koje napadaju prosledjenu figuru\\
	private ArrayList<Point> getAttackers(Point figurePostition,ArrayList<Point> FigureList){
		ArrayList<Point> attackers = new ArrayList<Point>();	
		//provera da li je na udaru neke figure i ako jeste uzmi njenu poziciju//
		for (int i = 0; i < FigureList.size(); i++) {
			if(Point.exists(figurePostition,board[FigureList.get(i).getI()][FigureList.get(i).getJ()].possibleMoves(FigureList.get(i).getI(),FigureList.get(i).getJ())))
				attackers.add(new Point(FigureList.get(i).getI(),FigureList.get(i).getJ()));
		}
		return attackers;
	}
	
	//Daje sve figure koje igrac moze da pomera, u zavisnosti da li je sah ili nije\\
	public ArrayList<Point> getMovableFigures() throws Draw, Checkmate{
		if((typeOfGame == GAME_MODE_ONLINE && onMove == team) || typeOfGame != GAME_MODE_ONLINE){
			Point king = new Point(0, 0);
			ArrayList<Point> mojeFigure = new ArrayList<>();
			ArrayList<Point> drugeFigure = new ArrayList<>();
			//Raspored Figura
			for (int i = 0; i < board.length; i++) 
				for (int j = 0; j < board.length; j++) 
					if(board[i][j] != null){
						if(board[i][j].getTeam()==onMove){
							if(board[i][j] instanceof King )
								king = new Point(i,j);
							else 
								mojeFigure.add(new Point(i, j));
						} else 
							drugeFigure.add(new Point(i, j));
					}
			
			attackers = new ArrayList<Point>();
			attackers = getAttackers(king,drugeFigure);
			//Ako oba igraca imaju samo kraljeve nereseon je
			movFigures = new ArrayList<Point>();
			//Ako ja imam samo kralja i drugi igrac ima samo kralja onda je nereseno\\
			if(mojeFigure.size()==0 && drugeFigure.size() == 1){
				throw new Draw();
			}
			
			if(board[king.getI()][king.getJ()].possibleMoves(king.getI(), king.getJ()).size()==0){
				if(attackers.size()>1)
					throw new Checkmate();
			} else {
				//Dodajemo kralja jer ima gde da se pomeri :)
				movFigures.add(new Point(king.getI(), king.getJ()));
			}
			
			if(attackers.size()==1){//Ako ga 1 figura napada, proveri da li moze neka da se ispreci ili pojede tu figuru
				ArrayList<Point> attackingPath = getAttackingPath(attackers.get(0), king);
				//Kreiram novu listu figura koje smem da pomeram
				//movFigures = new ArrayList<Point>();
				ArrayList<Point> posMov;
				for (int i = 0; i < mojeFigure.size(); i++) {
					//Uzimam sve moguce pozicije na koje moja figura moze da stane
					posMov = board[mojeFigure.get(i).getI()][mojeFigure.get(i).getJ()].possibleMoves(mojeFigure.get(i).getI(), mojeFigure.get(i).getJ());
					//Dodajem samo figure koje mogu da se isprece, ili pojedu figuru koja napada kralja\\
					//a njihovim pomeranjem se ne ugrozava kralj sa druge strane\\
					for (int j = 0; j < attackingPath.size(); j++) {
						//Ako se pozicija nalazu i nizu mogucih poteza dodaj figuru u listu mogucih figura\\
						if(Point.exists(attackingPath.get(j), posMov)){
							movFigures.add(new Point(mojeFigure.get(i).getI(), mojeFigure.get(i).getJ()));
							break;
						}	
					}
				}
				//ako on ne moze da se pomeri kralj, i ni jedna figura ne moze da spreci sah baca se exception sahMat
				if(board[king.getI()][king.getJ()].possibleMoves(king.getI(), king.getJ()).size()==0 && movFigures.size()==0)
						throw new Checkmate();
				
			} else if(attackers.size()==0){
				//Ako ni 1 figura ne napada kralja, dodaj samo figure koje mogu da se pomeraju i njihovim pomeranjem ne moze da se ugrozi kralj
								
				//proveri da li pomeranje na bilo koju poziciju kralj ostaje zasticen

				//movFigures = new ArrayList<Point>();
				
				for (int i = 0; i < mojeFigure.size(); i++) {
					ArrayList<Point> pos = board[mojeFigure.get(i).getI()][mojeFigure.get(i).getJ()].possibleMoves(mojeFigure.get(i).getI(), mojeFigure.get(i).getJ());
					if(pos.size()>0){
						//Ako bar jedno pomeranje ne izaziva sah, dodaj figuru u listu pomerajucih figura\\
						for (int j = 0; j < pos.size(); j++) {
							if(proveri(king, new Point(mojeFigure.get(i).getI(),mojeFigure.get(i).getJ()), pos.get(j),drugeFigure)){
									movFigures.add(new Point(mojeFigure.get(i).getI(),mojeFigure.get(i).getJ()));
									break;
							}
						}
					}
				}
				//Ako kralj nema gde da se pomeri i ni 1 figura ne moze da se pomeri, a nije sah, onda je nereseno
				if(board[king.getI()][king.getJ()].possibleMoves(king.getI(), king.getJ()).size()==0 && movFigures.size()==0)
						throw new Draw();
				
			} else{//ako ga napada vise od 1 figure
				if(movFigures.size()==0)//Ako kralj ne moze da se pomeri shamat je
					throw new Checkmate();
			}
			//Ako kralj ima vise napadaca, i moze da se pomeri ovim uslovima primoravamo da samo kraljem moze igrac da igra :)
			return movFigures;
		} else // ako nije na tebe red, onda vrati prazan  
			return new ArrayList<Point>();
	}
		
	//Daje gde moze da skoci  figura cije su koordinate prosledjene\\
	public ArrayList<Point> getPossibleMoves(int i, int j){
		if((typeOfGame == GAME_MODE_ONLINE && onMove == team) || typeOfGame != GAME_MODE_ONLINE)
		if(board[i][j] != null && board[i][j].getTeam() == onMove){//Izabiramo figuru igraca koji je na potezu i vracamo njene moguce poteze
			selFigure = new Point(i, j);
			ArrayList<Point> lista = board[i][j].possibleMoves(i, j);
			if(board[i][j] instanceof Pawn){//Dodajemo EnPasant
				Point p = mozeEnPassant(i, j);
				if(p!=null)
					lista.add(p);
			}
			ArrayList<Point> possibleMovies = new ArrayList<Point>(); 
			
			if(board[i][j] instanceof King){
				//Ako smo kliknuli na kralja, samo uzimamo njegove moguce poteze i dodajemo rokade ako su moguce
				Point malaRokada = mozeMalaRokada();
				if(malaRokada!=null)
					lista.add(malaRokada);
				
				Point velikaRokada = mozeVelikaRokada();
				if(velikaRokada!=null)
					lista.add(velikaRokada);
				
				return lista;
			} else {//Ako je odabrana druga figura uzeti njene poteze\\
				
				//Uzimamo mesto kralja( i protivnicke figure?)\\
				Point king = null;
				ArrayList<Point> drugeFigure = new ArrayList<>();
				for (int k = 0; k < board.length; k++) {
					for (int k2 = 0; k2 < board.length; k2++) {
						
						if(board[k][k2] != null && board[k][k2].getTeam()==onMove && board[k][k2] instanceof King){
							king = new Point(k, k2);
						} else if(board[k][k2] != null && board[k][k2].getTeam()!=onMove)
							drugeFigure.add(new Point(k, k2));
							
					}
				}
				
				if(attackers != null && attackers.size()==1) {//Ako je kralj napadnut, daj moguce poteze koji ce ga spasiti\\
					
					//Uzimamo put kojim je kralj napadnut\\
					ArrayList<Point> path = getAttackingPath(attackers.get(0), king);
					//proci kroz sve moguce potexe figure i odabrati samo one koji se nalaze na putanji napada
					for (int k = 0; k < path.size(); k++) {
						if(Point.exists(path.get(k),lista) && proveri(king,new Point(i, j),path.get(k),drugeFigure))
							possibleMovies.add(path.get(k));
					}

				} else { //ako kralj nije napadnut samo dodaj poteze koji ga nece ugroziti\\
					//Proci kroz sve poteze figure i odabrati one koje ne ugrozavaju kralja\\
					for (int k = 0; k < lista.size(); k++) {
						if(proveri(king, new Point(i, j),lista.get(k) , drugeFigure))
							possibleMovies.add(lista.get(k));
					}
				}
				
				return possibleMovies;											
			}
		} else //Figura koju smo odabrali nije ispravna \\
			return null;
		else return new ArrayList<Point>();
	}
	
}