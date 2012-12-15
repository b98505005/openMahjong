/*
 * Jeu.java
 *
 * Created on 22/08/2007. Copyright Raphael (synthaxerrors@gmail.com
 *
 * Classe rep廥entant un jeu de Tiles compos�de figures
 * 
 * This file is part of Open Mahjong.
 * 
 * Open Mahjong is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Open Mahjong is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Open Mahjong.  If not, see <http://www.gnu.org/licenses/>.
 */


public class Game {

	Figure figures[];
	int sizeMax;
	
	Game(int nb){
		figures = new Figure[nb];
		for(int i=0; i<nb; i++){
			figures[i] = new Figure();
		}
		sizeMax = nb;
	}
	
	Game(Game j){
		figures = new Figure[j.sizeMax];
		for(int i=0; i<j.sizeMax; i++){
			figures[i] = new Figure(j.figures[i]);
		}
		sizeMax = j.sizeMax;
	}
	/**
	 * ajoute la Tile t dans le jeu
	 * renvoie la position absolue de la Tile (-1 si pas d'emplacement trouv�
	 */
	public int addTile(Tile t){
		int index=0;
		
		if((index=aTile(t))<0){ //le joueur ne possede pas d嶴�cette Tile
			index=0;
			while(figures[index].nbTile>0 && index<13){	//cherche la premi鋨e figure libre
				index++;
			}
			if(figures[index].nbTile>0 && index==13){
				return -1;
			}
		}
		figures[index].addTile(t);	//ajoute la Tile t dans la figure index
		triTile();
		return getPosFromTile(t);
	}
	
	/**
	 * retire la Tile correspondant �la position absolue i dans le jeu
	 * retourne la Tile retir嶪
	 */
	public Tile retireTile(int i){
		Tile t = getTileFromPos(i);
		if(t.name.length() != 0){ //la Tile est dans le jeu
			int index = getFigFromTile(t);
			figures[index].removedTile();
		}
		triTile();
		return t;
	}
	
	public void addsFigure(Figure f, boolean cover){
		int index=0;
		while(figures[index].nbTile>0){
			index++;
		}
		figures[index] = new Figure(f);
		if(cover == false){
			figures[index].expose();
		}
		triTile();
	}
	
	public void retireFigure(int i){
		figures[i] = new Figure();
		triTile();
	}
	
	/**
	 * Combine 2 jeux et retourne le resultat
	 */
	public Game combineGames(Game game)
	{
		Game result = new Game(this.sizeMax+game.sizeMax);
		
		result.figures = new Figure[this.sizeMax+game.sizeMax];
		
		for(int i=0; i< this.sizeMax; i++){
			result.figures[i] = new Figure(this.figures[i]);
		}
		for(int i=0; i< game.sizeMax; i++){
			result.figures[i+this.sizeMax] = new Figure(game.figures[i]);
		}
		
		return result;
	}
	
	/**
	 * retourne le nombre de pung et de kong dans le jeu
	 */
	public int getNbPongGung(){
		int cpt=0;
		for(int i=0; i<sizeMax; i++){
			if(figures[i].type == Main.typeFig.GUNG || figures[i].type == Main.typeFig.PONG){
				cpt++;
			}
		}
		return cpt;
	}
	
	/**
	 * retourne le nombre de kong dans le jeu
	 */
	public int getNbGung(){
		int cpt=0;
		for(int i=0; i<sizeMax; i++){
			if(figures[i].type == Main.typeFig.GUNG){
				cpt++;
			}
		}
		return cpt;
	}
	
	/**
	 * retourne le nombre de kong cach廥 de la couleur demand嶪 dans le jeu
	 */
	public int getNbGungCover(char color){
		int cpt=0;
		for(int i=0; i<sizeMax; i++){
			if(figures[i].type == Main.typeFig.GUNG && figures[i].isCover==true
					&& figures[i].tile.牌面種類 == color){
				cpt++;
			}
		}
		return cpt;
	}
	
	/**
	 * retourne le nombre de Tile t dans le jeu
	 */
	public int getNbTile(Tile t){
		for(int i=0; i<sizeMax; i++){
			if(figures[i].tile.name.compareTo(t.name)==0){
				return figures[i].nbTile;
			}
		}
		return 0; 
	}
	
	/**
	 * retourne le nombre de Tile dans le jeu
	 */
	public int getNbTile(){
		int nbTile = 0;
		for(int i=0; i<sizeMax; i++){
			nbTile += figures[i].nbTile;
		}
		return nbTile; 
	}
	
	public int getValue(boolean isWinner, int wind){
		int result=0;
		for(int i=0; i<sizeMax; i++){
			result+=figures[i].getValue(isWinner, wind);
		}
		return result;
	}
	
	public int getMulti(int vent){
		int result=0;
		for(int i=0; i<sizeMax; i++){
			result+=figures[i].getMulti(vent);
		}
		return result;
	}
	
	/**
	 * retourne la position absolue de la Tile t
	 */
	public int getPosFromTile(Tile t){
		int result = 0;
		for(int i=0; i<sizeMax; i++){
			if(figures[i].name().compareTo(t.name) == 0){
				break;
			}
			else{
				result+=figures[i].nbTile;
				if(i==(sizeMax-1)){
					return -1;
				}
			}
		}
		return result;
	}
	
	/**
	 * retourne la Tile correspondant �la position absolue pos
	 */
	public Tile getTileFromPos(int pos){
		for(int i=0; i<sizeMax; i++){
			if(pos<figures[i].nbTile){
				return figures[i].tile;
			}
			else{
				pos -= figures[i].nbTile;
			}
		}
		return new Tile();
	}
	
	/**
	 * retourne le numero de la figure contenant la Tile t
	 */
	public int getFigFromTile(Tile t){
		if(t.name.length() != 0){
			for(int i=0; i<sizeMax; i++){
				if(figures[i].tile.name.compareTo(t.name) == 0){
					return i;
				}
			}
		}
		return -1;
	}
	
	public int getFigFromPos(int i){
		return getFigFromTile(getTileFromPos(i));
	}
	
	/**
	 * retourne le numero de la premi鋨e figure dont le nombre
	 * de Tile est 嶲ale �nb. -1 s'il n'y en a pas
	 */
	public int getFirstFig(int nb, boolean sens){
		int cpt = -1;
		if(nb<0) return -1;
		for(int i=0; i<sizeMax; i++){
			if((figures[i].nbTile >= nb && sens == Main.DESCENDANT)
					|| (figures[i].nbTile <= nb && sens == Main.ASCENDANT)){
				cpt++;
			}
		}
		return cpt;
	}
	
	public boolean a對子(){
		for(int i=0; i<sizeMax; i++){
			if(figures[i].type == Main.typeFig.PAIR){
				return true;
			}
		}
		return false;
	}
	
	public boolean aPair(Tile t){
		for(int i=0; i<sizeMax; i++){
			if(figures[i].tile.name.compareTo(t.name)==0 && (figures[i].type == Main.typeFig.PAIR ||figures[i].type == Main.typeFig.PONG)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * fontion qui dit si le joueur possede d嶴�une figure avec la Tile t
	 * renvoi le numero de la figure ou -1 si le joueur ne la possede pas 
	 */
	public int aTile(Tile t){
		int result = -1;
		for(int i=0; i<13; i++){
			if(figures[i].name().compareTo(t.name) == 0){
				result = i;
			}
		}
		return result;
	}
	
	/**
	 * fonction qui retourne true si le jeu poss鋄e au moins une Tile
	 * de type dragon/vent
	 */
	public boolean aHonor(){
		for(int i=0; i<sizeMax; i++){
			if(figures[i].get_colour()=='v' || figures[i].get_colour()=='d'){
				return true;
			}
		}
		return false;
	}
	
	public boolean isPure(){
		char coul = 0;
		boolean pure = true;
		
		for(int i=0; i<sizeMax; i++){
			if(figures[i].nbTile>0 && figures[i].isNormal()){
				if(coul == 0){
					coul = figures[i].get_colour();
				}else if(coul != figures[i].get_colour()){
					pure = false;
					break;
				}
			}
		}
		return pure;
	}
	
	public boolean isEmpty(int i){
		if(getFigFromPos(i)>=0){
			if(figures[getFigFromPos(i)].isFree()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Petit tri �bulle qui classe en fonction des Tiles
	 */
	public void triTile(){
		Figure temp = new Figure();
		int i = 0;
		
		/* tri de la main cach嶪 */
		while(i<sizeMax-1){
//			if(((figures[i].estLibre()) && (!figures[i+1].estLibre()))	// pas de Tile
//					|| (figures[i].couleur()<figures[i+1].couleur()) 	// ou couleur sup廨ieure �la suivante 
//					|| (figures[i].couleur()==figures[i+1].couleur() && figures[i].chiffre()<figures[i+1].chiffre())) 	// ou couleur identique et chiffre sup廨ieur
			if(figures[i].isBottom(figures[i+1]))
			{
				// intervertion des Tiles
				temp = figures[i];
				figures[i] = figures[i+1];
				figures[i+1] = temp;
				i=0;
			}
			else{
				i++;
			}
		}
	}
	
	/**
	 * Tri �bulle qui tri les figures en fonction du nombre 
	 * de Tile de chaque figure
	 */
	public void triDigit(boolean order){
		Figure temp = new Figure();
		int i = 0;
		
		/* tri de la main cach嶪 */
		while(i<sizeMax-1){
			if(figures[i].nbTile<figures[i+1].nbTile && order == Main.DESCENDANT
					|| figures[i].nbTile>figures[i+1].nbTile && order == Main.ASCENDANT)
			{
				// intervertion des Tiles
				temp = figures[i];
				figures[i] = figures[i+1];
				figures[i+1] = temp;
				i=0;
			}
			else{
				i++;
			}
		}
	}
	
	/*
	 * retourne la couleur de la premi鋨e Tile trouv嶪 (non honneur)
	 * 0 sinon (i.e. pur honneurs). Si le jeu est pur (�checker avant), 
	 * donne la couleur du jeu 
	 */
	public char colour(){
		char coul = 0;
//		if(estPur()){
			for(int i=0; i<sizeMax; i++){
				if(figures[i].nbTile>0 && figures[i].isNormal()){
					coul =  figures[i].get_colour();
					break;
				}
			}
//		}
		return coul;
	}
	
	/* retourne true si le jeu n'est compos�que de Pung/Kong de Tiles 
	 * majeures (1/9 rond/bambou/caract鋨e)*/
	public boolean onlyTileMaj(){
		boolean result = true;
		
		for(int i=0; i<sizeMax; i++){
			if(figures[i].type == Main.typeFig.GUNG ||  figures[i].type == Main.typeFig.PONG){
				if(!figures[i].isNormal() || (figures[i].get_牌面()!=1) || (figures[i].get_牌面()!=9)){
					result = false;
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * retourne le nombre de figure de la couleur demand嶪
	 */
	public int getNbFigure(Main.typeFig figure, char couleur, boolean kongEgal2Paire){
		int result = 0;
		
		for(int i=0; i<sizeMax; i++){
			if(figures[i].tile.牌面種類 == couleur){
				if(figures[i].type == figure){
					result ++;
				}//un kong cach�peut compter comme 2 paires
				else if(kongEgal2Paire){ 
					if(figure == Main.typeFig.PAIR && figures[i].type==Main.typeFig.GUNG && figures[i].isCover){
						result+=2;
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * retourne le nombre de figure correspondant au chiffre et �la couleur demand廥
	 */
	public int getNbFigure(Main.typeFig figure, int 牌面,char couleur, boolean kongEgal2Paire){
		int result = 0;
		
		for(int i=0; i<sizeMax; i++){
			if(figures[i].tile.牌面種類 == couleur && figures[i].tile.牌面大小 == 牌面){
				if(figures[i].type == figure){
					result ++;
				}//un kong cach�peut compter comme 2 paires
				else if(kongEgal2Paire){ 
					if(figure == Main.typeFig.PAIR && figures[i].type==Main.typeFig.GUNG && figures[i].isCover){
						result+=2;
					}
				}
			}
		}
		return result;
	}
	/**
	 * retourne le nombre de figure demand嶪
	 */
	public int getNbFigure(Main.typeFig figure, boolean gungEqual2Pair){
		int result = 0;
		
		for(int i=0; i<sizeMax; i++){
			if(figures[i].type == figure){
				result ++;
			}//un kong cach�peut compter comme 2 paires
			else if(gungEqual2Pair){ 
				if(figure == Main.typeFig.PAIR && figures[i].type==Main.typeFig.GUNG && figures[i].isCover){
					result+=2;
				}
			}
		}
		return result;
	}
	
	/**
	 * retourne le nombre de Pung/Kong de la couleur demand嶪
	 */
	public int getNbPongGung(char colour){
		int result = 0;
		
		for(int i=0; i<sizeMax; i++){
			if(figures[i].tile.牌面種類 == colour){
				if(figures[i].type == Main.typeFig.GUNG || figures[i].type == Main.typeFig.PONG){
					result ++;
				}
			}
		}
		return result;
	}
	
	/**
	 * retourne le nombre de Pung/Kong de la couleur et du chiffre demand嶪
	 */
	public int getNbPongGung(int chiffre, char couleur){
		int result = 0;
		
		for(int i=0; i<sizeMax; i++){
			if(figures[i].tile.牌面種類 == couleur && figures[i].tile.牌面大小 == chiffre){
				if(figures[i].type == Main.typeFig.GUNG || figures[i].type == Main.typeFig.PONG){
					result ++;
				}
			}
		}
		return result;
	}

	
	/**
	 * Test le Mahjong special "Le Pung Venteux":
	 * 	- 1 pung/kong de chaque serie
	 * 	- 4 vents
	 *  - 1 vent quelconque
	 */
	public boolean test1PongWindy(){
		boolean result = false;
		
		if(this.getNbPongGung('c') == 1 
				&& this.getNbPongGung('r') == 1
				&& this.getNbPongGung('b') == 1){
			if(this.getNbTile(new Tile(1,'v')) >0 
					&& this.getNbTile(new Tile(2,'v'))>0
					&& this.getNbTile(new Tile(3,'v'))>0
					&& this.getNbTile(new Tile(4,'v'))>0){
				if(this.getNbTile(new Tile(1,'v')) == 2  
						|| this.getNbTile(new Tile(2,'v'))== 2
						|| this.getNbTile(new Tile(3,'v'))== 2
						|| this.getNbTile(new Tile(4,'v'))== 2){
					result = true;
				}
			}
		}
		return result;
	}
	/**
	 * Test le Mahjong special "Les Paires Venteuses":
	 * 	- 1 paire de chaque vent
	 * 	- 1 paire de chaque s廨ies
	 */
	public boolean test2PairsWindy(){
		boolean result = false;
		
		if(this.getNbFigure(Main.typeFig.PAIR, 1,'v', false)==1
				&& 	this.getNbFigure(Main.typeFig.PAIR, 2,'v', false)==1
				&& 	this.getNbFigure(Main.typeFig.PAIR, 3,'v', false)==1
				&& 	this.getNbFigure(Main.typeFig.PAIR, 4,'v', false)==1
				&& 	this.getNbFigure(Main.typeFig.PAIR, 'b', false)==1
				&& 	this.getNbFigure(Main.typeFig.PAIR, 'r', false)==1
				&& 	this.getNbFigure(Main.typeFig.PAIR, 'c', false)==1){
			result = true;
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La Main de Jade":
	 * 	- 3 pung de bambou 
	 *  - 1 paire de bambou
	 *  - 1 pung de dragon vert
	 */
	public boolean test3MainOfJade(){
		boolean result = false;
		
		if(this.getNbPongGung('b')==3
				&& 	this.getNbFigure(Main.typeFig.PAIR, 'b', false)==1
				&& 	this.getNbPongGung(3,'d')==1){
			result = true;
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La Main de Corail":
	 * 	- 3 pung de caract鋨e
	 *  - 1 paire de caract鋨e
	 *  - 1 pung de dragon rouge
	 */
	public boolean test4MainOfCoral(){
		boolean result = false;
		
		if(this.getNbPongGung('c')==3
				&& 	this.getNbFigure(Main.typeFig.PAIR, 'c', false)==1
				&& 	this.getNbPongGung(1,'d')==1){
			result = true;
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La Main d'Opaline": Hand of xxx
	 * 	- 3 pung de cercle
	 *  - 1 paire de cercle
	 *  - 1 pung de dragon blanc
	 */
	public boolean test5MaindOpaline(){
		boolean result = false;
		
		if(this.getNbPongGung('r')==3
				&& 	this.getNbFigure(Main.typeFig.PAIR, 'r', false)==1
				&& 	this.getNbPongGung(2,'d')==1){
			result = true;
		}
		return result;
	}
	/**
	 * Test le Mahjong special "Les Paires de Shozum":
	 * 	- 6 paires de chaque s廨ie
	 *  - 1 paire du dragon associ��la s廨ie
	 */
	public boolean test6PairsOfShozum(){
		boolean result = false;
		
		if(this.getNbFigure(Main.typeFig.PAIR, 'c', true)==6
				&& 	this.getNbFigure(Main.typeFig.PAIR, 1, 'd', false)==1){
			result = true;
		}
		if(this.getNbFigure(Main.typeFig.PAIR, 'r', true)==6
				&& 	this.getNbFigure(Main.typeFig.PAIR, 2, 'd', false)==1){
			result = true;
		}
		if(this.getNbFigure(Main.typeFig.PAIR, 'b', true)==6
				&& 	this.getNbFigure(Main.typeFig.PAIR, 3, 'd', false)==1){
			result = true;
		}
		return result;
	}
	/**
	 * Test le Mahjong special "Les 13 Lanternes Merveilleuses":
	 * 	- Les 1 et les 9 des 3 s廨ies 
	 *  - Les 4 vents et 3 dragons
	 *  - 1 Honneur
	 */
	public boolean test7LanternWonderful(){
		boolean result = false;

		if(this.getNbFigure(Main.typeFig.SIMPLE, 1,'r', false)==1
				&& this.getNbFigure(Main.typeFig.SIMPLE, 9,'r', false)==1
				&& this.getNbFigure(Main.typeFig.SIMPLE, 1,'c', false)==1
				&& this.getNbFigure(Main.typeFig.SIMPLE, 9,'c', false)==1
				&& this.getNbFigure(Main.typeFig.SIMPLE, 1,'b', false)==1
				&& this.getNbFigure(Main.typeFig.SIMPLE, 9,'b', false)==1){
			if(this.getNbTile(new Tile(1, 'v')) > 0
					&& this.getNbTile(new Tile(2, 'v')) > 0
					&& this.getNbTile(new Tile(3, 'v')) > 0
					&& this.getNbTile(new Tile(4, 'v')) > 0
					&& this.getNbTile(new Tile(1, 'd')) > 0
					&& this.getNbTile(new Tile(2, 'd')) > 0
					&& this.getNbTile(new Tile(3, 'd')) > 0){
				if(this.getNbTile(new Tile(1, 'v')) == 2
						|| this.getNbTile(new Tile(2, 'v')) == 2
						|| this.getNbTile(new Tile(3, 'v')) == 2
						|| this.getNbTile(new Tile(4, 'v')) == 2
						|| this.getNbTile(new Tile(1, 'd')) == 2
						|| this.getNbTile(new Tile(2, 'd')) == 2
						|| this.getNbTile(new Tile(3, 'd')) == 2){
					result = true;
				}
			}
		}
		return result;
	}
	/**
	 * Test le Mahjong special "Les 7 muses du po鋈e chinois":
	 * 	- 7 paires d'honneurs ou majeur 
	 */
	public boolean test8_SevenPairs(){
		boolean result = true;

		if(this.getNbFigure(Main.typeFig.PAIR, true)==7)	{
			// aucune des paires ne doit 皻re mineure
			for(int i=2; i<8; i++){
				if(this.getNbTile(new Tile(i, 'b')) >0
						|| this.getNbTile(new Tile(i, 'c')) >0
						|| this.getNbTile(new Tile(i, 'r')) >0){
					result = false;
					break;
				}
			}
		}
		else result = false;
		return result;
	}
	/**
	 * Test le Mahjong special "Les 4 bonheurs domestiques":
	 * 	- 1 pung de chaque vent
	 *  - 1 paire quelconque 
	 */
	public boolean test9BonheursDomestiques(){
		boolean result = false;

		if(this.getNbPongGung('v')==4
				&& this.getNbFigure(Main.typeFig.PAIR, false)==1)	{
			result = true;			
		}
		return result;
	}
	/**
	 * Test le Mahjong special "Le triangle 彋ernel":
	 * 	- 4 paires de vent
	 *  - 3 paires de dragon 
	 */
	public boolean test10TriangleEternel(){
		boolean result = false;

		if(this.getNbFigure(Main.typeFig.PAIR,'v', true)==4
				&& this.getNbFigure(Main.typeFig.PAIR, 'd', true)==3)	{
			result = true;			
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La temp皻e":
	 * 	- 2 pung de dragons
	 *  - 1 paire de chaque vent 
	 */
	public boolean test11Tempete(){
		boolean result = false;

		if(this.getNbFigure(Main.typeFig.PAIR,'v', false)==4
				&& this.getNbPongGung('d')==2)	{
			result = true;			
		}
		return result;
	}
	/**
	 * Test le Mahjong special "Le souffle du dragon":
	 * 	- 3 pung de dragons 
	 *  - 1 vents de chaque s廨ie
	 *  - 1 vent quelconque
	 */
	public boolean test12SouffleDuDragon(){
		boolean result = false;

		if(this.getNbPongGung('d')==3
				&& this.getNbTile(new Tile(1,'v'))>0
				&& this.getNbTile(new Tile(2,'v'))>0
				&& this.getNbTile(new Tile(3,'v'))>0
				&& this.getNbTile(new Tile(4,'v'))>0
				)	{
			if(this.getNbTile(new Tile(1,'v')) == 2  
					|| this.getNbTile(new Tile(2,'v'))== 2
					|| this.getNbTile(new Tile(3,'v'))== 2
					|| this.getNbTile(new Tile(4,'v'))== 2){
				result = true;
			}
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La petite main verte":
	 * 	- 1 pung de 3 bambou et 2 pung de nb pairs bambou
	 *  - 1 pung de dragon vert
	 *  - 1 paire de nb pair bambou
	 */
	public boolean test13PetiteMainVerte(){
		boolean result = false;

		if(this.getNbPongGung(3,'b')==1
				&& this.getNbPongGung(3,'d')==1){
			int nbPungPair = this.getNbPongGung(2,'b') + this.getNbPongGung(4,'b') + this.getNbPongGung(6,'b') + this.getNbPongGung(8,'b');
			if(nbPungPair == 2){
				if(this.getNbFigure(Main.typeFig.PAIR, 2, 'b', false)==1
						|| this.getNbFigure(Main.typeFig.PAIR, 4, 'b', false)==1
						|| this.getNbFigure(Main.typeFig.PAIR, 6, 'b', false)==1
						|| this.getNbFigure(Main.typeFig.PAIR, 8, 'b', false)==1){
					result = true;
				}
			}
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La grande main verte":
	 * 	- 1 pung de n�, n�, n� bambou
	 *  - 1 pung de dragon vert
	 *  - 1 paire de n� bambou
	 */
	public boolean test14GrandeMainVerte(){
		boolean result = false;

		if(this.getNbPongGung(2,'b')==1
				&& this.getNbPongGung(4,'b')==1
				&& this.getNbPongGung(6,'b')==1
				&& this.getNbPongGung(3,'d')==1
				&& this.getNbFigure(Main.typeFig.PAIR, 1, 'b', false)==1){
			result = true;
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La main verte et rouge":
	 * 	- 1 pung de n�, n�, n�, n� bambou
	 *  - 1 dragon vert
	 *  - 1 dragon rouge
	 */
	public boolean test15MainVerteEtRouge(){
		boolean result = false;

		if(this.getNbPongGung(1,'b')==1
				&& this.getNbPongGung(5,'b')==1
				&& this.getNbPongGung(7,'b')==1
				&& this.getNbPongGung(9,'b')==1
				&& this.getNbFigure(Main.typeFig.SIMPLE, 3, 'd', false)==1
				&& this.getNbFigure(Main.typeFig.SIMPLE, 1, 'd', false)==1){
			result = true;
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La petite main rouge":
	 * 	- 1 pung de 3 caract鋨e et 2 pung de nb pairs caract鋨e
	 *  - 1 pung de dragon rouge
	 *  - 1 paire de nb pair caract鋨e
	 */
	public boolean test16PetiteMainRouge(){
		boolean result = false;

		if(this.getNbPongGung(3,'c')==1
				&& this.getNbPongGung(1,'d')==1){
			int nbPungPair = this.getNbPongGung(2,'c') + this.getNbPongGung(4,'c') + this.getNbPongGung(6,'c') + this.getNbPongGung(8,'c');
			if(nbPungPair == 2){
				if(this.getNbFigure(Main.typeFig.PAIR, 2, 'c', false)==1
						|| this.getNbFigure(Main.typeFig.PAIR, 4, 'c', false)==1
						|| this.getNbFigure(Main.typeFig.PAIR, 6, 'c', false)==1
						|| this.getNbFigure(Main.typeFig.PAIR, 8, 'c', false)==1){
					result = true;
				}
			}
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La grande main rouge":
	 * 	- 1 pung de n�, n�, n� caract鋨e
	 *  - 1 pung de dragon rouge
	 *  - 1 paire de n� caract鋨e
	 */
	public boolean test17GrandeMainRouge(){
		boolean result = false;

		if(this.getNbPongGung(2,'c')==1
				&& this.getNbPongGung(4,'c')==1
				&& this.getNbPongGung(6,'c')==1
				&& this.getNbPongGung(1,'d')==1
				&& this.getNbFigure(Main.typeFig.PAIR, 1, 'c', false)==1){
			result = true;
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La main rouge et blanche":
	 * 	- 1 pung de n�, n�, n�, n� caract鋨e
	 *  - 1 dragon rouge
	 *  - 1 dragon blanc
	 */
	public boolean test18MainRougeEtBlanche(){
		boolean result = false;

		if(this.getNbPongGung(1,'c')==1
				&& this.getNbPongGung(5,'c')==1
				&& this.getNbPongGung(7,'c')==1
				&& this.getNbPongGung(9,'c')==1
				&& this.getNbFigure(Main.typeFig.SIMPLE, 1, 'd', false)==1
				&& this.getNbFigure(Main.typeFig.SIMPLE, 2, 'd', false)==1){
			result = true;
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La petite main blanche":
	 * 	- 1 pung de 3 rond et 2 pung de nb pairs rond
	 *  - 1 pung de dragon blanc
	 *  - 1 paire de nb pair rond
	 */
	public boolean test19PetiteMainBlanche(){
		boolean result = false;

		if(this.getNbPongGung(3,'r')==1
				&& this.getNbPongGung(2,'d')==1){
			int nbPungPair = this.getNbPongGung(2,'r') + this.getNbPongGung(4,'r') + this.getNbPongGung(6,'r') + this.getNbPongGung(8,'r');
			if(nbPungPair == 2){
				if(this.getNbFigure(Main.typeFig.PAIR, 2, 'r', false)==1
						|| this.getNbFigure(Main.typeFig.PAIR, 4, 'r', false)==1
						|| this.getNbFigure(Main.typeFig.PAIR, 6, 'r', false)==1
						|| this.getNbFigure(Main.typeFig.PAIR, 8, 'r', false)==1){
					result = true;
				}
			}
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La grande main blanche":
	 * 	- 1 pung de n�, n�, n� rond
	 *  - 1 pung de dragon blanc
	 *  - 1 paire de n� rond
	 */
	public boolean test20GrandeMainBlanche(){
		boolean result = false;

		if(this.getNbPongGung(2,'r')==1
				&& this.getNbPongGung(4,'r')==1
				&& this.getNbPongGung(6,'r')==1
				&& this.getNbPongGung(2,'d')==1
				&& this.getNbFigure(Main.typeFig.PAIR, 1, 'r', false)==1){
			result = true;
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La main blanche et verte":
	 * 	- 1 pung de n�, n�, n�, n� rond
	 *  - 1 dragon blanc
	 *  - 1 dragon vert
	 */
	public boolean test21MainBlancheEtVerte(){
		boolean result = false;

		if(this.getNbPongGung(1,'r')==1
				&& this.getNbPongGung(5,'r')==1
				&& this.getNbPongGung(7,'r')==1
				&& this.getNbPongGung(9,'r')==1
				&& this.getNbFigure(Main.typeFig.SIMPLE, 2, 'd', false)==1
				&& this.getNbFigure(Main.typeFig.SIMPLE, 3, 'd', false)==1){
			result = true;
		}
		return result;
	}
	/**
	 * Test le Mahjong special "Le mandarin":
	 * 	- 3 kong cach�de vent
	 *  - 1 paire de vent
	 */
	public boolean test22Mandarin(){
		boolean result = false;

		if(this.getNbGungCover('v')==3
				&& this.getNbFigure(Main.typeFig.PAIR, 'v', false)==1){
			result = true;
		}
		return result;
	}
	/**
	 * Test le Mahjong special "Le mahjong imp廨ial":
	 * 	- 3 kong cach�de dragon
	 *  - 1 paire de vent du joueur
	 */
	public boolean test23MahjongImperial(int ventDuJoueur){
		boolean result = false;

		if(this.getNbGungCover('d')==3
				&& this.getNbFigure(Main.typeFig.PAIR, ventDuJoueur,'v', false)==1){
			result = true;
		}
		return result;
	}

}
