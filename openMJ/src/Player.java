/*
 * Joueur.java
 *
 * Created on 17/06/2007. Copyright Raphael (synthaxerrors@gmail.com
 *
 * Classe repr廥entant un joueur (ordi ou pas)
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

import javax.swing.JLabel;

public class Player{

	/* Partie graphique */
	JLabel labelCover[] = new JLabel[14];
	JLabel labelExpose[] = new JLabel[24];
	JLabel labelWind;
	int TileSelect;	//num廨o de la Tile s幨嶰tionn嶪
	
	/* Part functional */
	Game mainPlayer;	//main player
	Game mainExpose;	//combinaison expos嶪s du joueur
	
	int 風;
	int numero;
	int score;
	String name;
	int typeIA;

	Player(int i){
		mainPlayer = new Game(14);
		mainExpose = new Game(24);
		
		for(int j=0;j<14;j++){
			labelCover[j]=new JLabel();
		}
		for(int j=0;j<24;j++){
			labelExpose[j]=new JLabel();
		}
		
		風 = i;
		labelWind = new JLabel();
		numero = i;
		TileSelect = -1;
		score = 0;
		typeIA=0;
		name = "";
	}

	/**
	 * Retourne le num廨o de la figure qui est un kong cach�
	 * -1 si pas de kong
	 */
	public int 槓(){
		for(int i=0; i<14; i++){								//pour chaque figure de la main cach嶪
			if(mainPlayer.figures[i].type == Main.typeFig.GUNG){		//si la figure est un kong
				return i;
			}
		}
		return -1;
	}

	/**
	 * Retourne le num廨o de la figure qui est un kong cach�
	 * -1 si pas de kong
	 */
	public int 碰(){//碰
		for(int i=0; i<14; i++){								//pour chaque figure de la main cach嶪
			if(mainPlayer.figures[i].type == Main.typeFig.PONG){		//si la figure est un pung
				return i;
			}
		}
		return -1;
	}

	/**
	 * retourne le nombre de Tile t dans le jeu du joueur  (cach嶪s+expos�
	 */
	public int getNbTile(Tile t){
		return mainPlayer.getNbTile(t)+mainExpose.getNbTile(t);
	}

	/**
	 * Retourne le numero de la figure contenant une fleur ou saison (-1 si pas de fleur)
	 */
	public int a字牌(){
		for(int i=0; i<14; i++){
			if(mainPlayer.figures[i].is字牌()){
				return i;
			}
		}
		return -1; //pas d'honneur trouv嶪
	}
	
	/**
	 * Retourne TRUE si le joueur peut faire Mahjong
	 * Pour faire mahjong, il faut avoir 4 combinaison et une paire
	 * Une autre fa蔞n est 3 kong cach廥 non expos廥 et une paire
	 */
	public boolean aMahjong(){
		int cpt=0;
		boolean result = false;;
		
		// test les mahjong speciaux (juste pour le joueur humain)
		if(this.numero==0 && aMahjongSpecial()>0) result =true;
		
		else{
			cpt+=mainExpose.getNbPongGung();
			cpt+=mainPlayer.getNbPongGung();

			if(cpt==4 && mainPlayer.a對子()){
				result = true;
			}
			else if(mainPlayer.getNbGung()==3 && mainPlayer.a對子()){
				result = true;
			}
		}
		return result;
	}

	/**
	 * Retourne TRUE si le joueur peut faire Mahjong avec la Tile t
	 */
	public boolean canMahjong(Tile t){
		boolean result = false;
		int i = 0;

		if((i=mainPlayer.addTile(t))>=0){
			if(aMahjong()){
				result = true;
			}
			else{
				result = false;
			}
			mainPlayer.retireTile(i);
		}
		return result;
	}

	/**
	 * retourne true si le joueur peut faire un pung ou kong ou s'il fait Mahjong avec cette Tile
	 */
	public boolean canPrendre(Tile t){
		if(t.name.length() != 0 ){
			if(mainPlayer.aPair(t)){
				return true;
			}
			if(canMahjong(t)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Petit tri �bulle des mains cach嶪 et expos嶪s
	 */
	private void trierGame(){
		mainPlayer.triTile();
		mainExpose.triTile();
	}

	/**
	 * Met �jour et affiche tous les label d'un joueur 
	 */
	public void affiche(){
		trierGame();
		afficheMainCache();
		afficheMainExpose();
	}

	/**
	 * Affiche la mainCache d'un joueur
	 */
	public void afficheMainCache(){
		int cpt=0;
		/* r嶯nitialise l'affichage */
		for(int i=0; i<14; i++){
			labelCover[i].setIcon(null);
		}
		/* affiche la main chach嶪 */
		for(int i=0; i<14; i++){
			for(int j=0; j<mainPlayer.figures[i].nbTile; j++){
				
				switch(this.numero){
				case(0):
					labelCover[cpt].setIcon(mainPlayer.figures[i].donneIcon(0, false));
					cpt++;
				break;
				case(1):
					if(Main.montreJeu){
						labelCover[cpt].setIcon(mainPlayer.figures[i].donneIcon(-90, false));
					}
					else{
						labelCover[cpt].setIcon(Tile.donneFond270());
					}
					cpt++;
				break;
				case(2):
					if(Main.montreJeu){
						labelCover[cpt].setIcon(mainPlayer.figures[i].donneIcon(0, false));
					}
					else{
						labelCover[cpt].setIcon(Tile.donneFond());
					}
					cpt++;
				break;
				case(3):
					if(Main.montreJeu){
						labelCover[cpt].setIcon(mainPlayer.figures[i].donneIcon(90, false));
					}
					else{
						labelCover[cpt].setIcon(Tile.donneFond90());
					}
					cpt++;
				break;
				}
			}
		}	
	}
	
	/**
	 * Affiche les combinaisons d'un joueur
	 */
	public void afficheMainExpose(){
		int cpt=0;
		for(int i=0; i<24; i++){
			labelExpose[i].setIcon(null);
		}
		for(int i=0; i<24; i++){
			for(int j=0; j<mainExpose.figures[i].nbTile; j++){
				boolean grayed = false;
				if(mainExpose.figures[i].type == Main.typeFig.GUNG){
					if(mainExpose.figures[i].isCover && j==0){
						grayed = true;
					}
					if(j==3) grayed = true;
				}
	
				switch(numero){
				case(0):
					labelExpose[cpt].setIcon(mainExpose.figures[i].donneIcon(0, grayed));
				break;
				case(1):
					labelExpose[cpt].setIcon(mainExpose.figures[i].donneIcon(-90, grayed));
				break;
				case(2):
					labelExpose[cpt].setIcon(mainExpose.figures[i].donneIcon(0, grayed));
				break;
				case(3):
					labelExpose[cpt].setIcon(mainExpose.figures[i].donneIcon(90, grayed));
				break;
				}
				cpt++;
			}
		}
	}

//	public static int tzu=0;
//	public static Tile[] mmm = {
//		new Tile(1,'d'),new Tile(1,'d'),new Tile(1,'d'),
//		new Tile(1,'d'),new Tile(2,'d'),new Tile(2,'d'),
//		new Tile(2,'d'),new Tile(2,'d'),new Tile(3,'d'),
//		new Tile(3,'d'),new Tile(3,'d'),new Tile(3,'d'),
//		new Tile(4,'v'),new Tile(2,'v')
//	};
	/**
	 * Ajoute la Tile t dans la mainCache du joueur
	 * Renvoi la position de la Tile (-1 si le joueur �d嶴�14 Tiles)
	 */
	public int addsTile(Tile t){
//		if(numero==0 && tzu<14)t = mmm[tzu++];
		System.out.print("J"+(this.numero+1)+" add Tile:"+t.name+"\n");
		int i = mainPlayer.addTile(t);
		return i;
	}

	/**
	 * Retire la Tile �la position absolue i de la mainCache 
	 * Retourne la Tile retir嶪
	 */
	public Tile retireTile(int i){
		Tile t = new Tile(mainPlayer.retireTile(i));
		System.out.print("J"+(this.numero+1)+" retire Tile:"+t.name+"\n");
		return t;
	}

	

	/**
	 * Permet de faire d嶰lar��un joueur les fleurs et les saison qu'il a dans sa mainCache
	 * Place ces fleurs et saison dans la zone de mainExpose
	 * Renvoi true si un honneur a 彋�d嶰lar�	 */
	public boolean declareHonor(){
		int index =0;
		int nb_Tile = 0;

		index=a字牌();
		if(index>=0){
			if(this.numero == 0){
				Main.displayText("Vous d嶰larez la "+mainPlayer.figures[index].tile.name);
			}else{
				Main.displayText(this.name+"(J"+(this.numero+1)+") d嶰lare la "+mainPlayer.figures[index].tile.name);
			}
			mainExpose.addsFigure(mainPlayer.figures[index], false);
			mainPlayer.retireFigure(index);
			nb_Tile++;
		}
		return (index!=-1);
	}

	public void declareFigure(Tile t, boolean estCache){
		int i = mainPlayer.getFigFromTile(t);
		String message;
		
		if(this.numero == 0){
			message = "Vous d嶰larez ";
		}else{
			message = this.name+"((J"+(this.numero+1)+") d嶰lare ";
		}
		
		if(mainPlayer.figures[i].nbTile == 4){
			message += "un Kong de "+mainPlayer.figures[i].tile.name;
		}else if(mainPlayer.figures[i].nbTile == 3){
			message += "un Pung de "+mainPlayer.figures[i].tile.name;	
		}else if(mainPlayer.figures[i].nbTile == 2){
			message += "Mahjong avec une paire de "+mainPlayer.figures[i].nbTile;
		}else{
			message += "1 Tiles de "+mainPlayer.figures[i].nbTile;
		}
		
		if(estCache){ message += " cach�";}
		
		Main.displayText(message);
		mainExpose.addsFigure(mainPlayer.figures[i], estCache);
		mainPlayer.retireFigure(i);
	}
	/**
	 * fonction qui permet d'ajouter une Tile de la main cach嶪 �	 * un pung d嶴�d嶰lar�	 * @return true si un nouveau kong a 彋�d嶰lar�	 */
	public boolean declareKong(){
		for(int i=0;i<mainPlayer.sizeMax; i++){
			if(mainPlayer.figures[i].nbTile==1){
				for(int j=0; j<mainExpose.sizeMax; j++){
					if(mainExpose.figures[j].type == Main.typeFig.PONG 
							&& mainExpose.figures[j].name().compareTo(mainPlayer.figures[i].name())==0){
						//System.out.print("Un nouveau kong de "+mainExpose.figures[j].nom()+"vient d'皻re d嶰lar�);
						if(this.numero == 0){
							Main.displayText("Vous transformez un Pung de "+mainPlayer.figures[i].name()+" en Kong!");
						}else{
							Main.displayText(this.name+"("+this.numero+") transforme un Pung de "+mainPlayer.figures[i].name()+" en Kong!");
						}
						mainExpose.figures[j].nbTile++;
						mainExpose.figures[j].type = Main.typeFig.GUNG;
						mainPlayer.retireFigure(i);
						return true;
					}
				}
			}
		}
		return false;
	}


	/**
	 * Renvoie le nombre de points d'un joueur sans les multiplicateur
	 * estGanant: TRUE si le joueur a gagn�	 * vent est le vent du joueur (1~4)
	 */
	public int valeurMains(boolean estGagnant, int vent){
		int result = 0;
		result = mainPlayer.getValue(estGagnant, vent) + mainExpose.getValue(estGagnant, vent);
		return result;
	}
	
	public int multiMains(int vent){
		int result = 0;
		result = mainPlayer.getMulti(vent) + mainExpose.getMulti(vent);
		return result;
	}
	
	public int multiBonus(boolean gagnant){
		int result = 0;
		// Verifie si le jeu est pur 
		if(gagnant && mainPlayer.isPure() && mainExpose.isPure()){// si les 2 mains sont pur
			if( (mainPlayer.colour() == mainExpose.colour())//et de mm couleur 
					|| mainPlayer.colour()==0 || mainExpose.colour() == 0){ // ou si l'une ne contient que des honneurs  
				result += 3;												//Le jeu est pur => 3 doubles
				if(mainPlayer.colour() == 0 && mainExpose.colour() == 0){	//si ce ne sont que des honneurs
					result++;					//1 double de plus
				}
			}
			// un double pour une jeu sans honneur (vent/dragon)
			else if(mainPlayer.aHonor() == false && mainExpose.aHonor() == false){
				result++;							
			}
		}
		
		// 1 double si les 4 combinaisons sont des brelans ou carr廥 de Tiles majeures
		if(gagnant && mainPlayer.onlyTileMaj() && mainExpose.onlyTileMaj()){
			result++;
		}
		
		return result;
	}

	public int aMahjongSpecial()
	{
		int result = 0;
		Game mainComplete = mainPlayer.combineGames(mainExpose);
		
		if(mainComplete.test1PongWindy()) 					result = 1;
		else{if(mainComplete.test2PairsWindy()) 			result = 2;
		else{if(mainComplete.test13PetiteMainVerte()) 			result = 13;	//prioritaire
		else{if(mainComplete.test14GrandeMainVerte()) 			result = 14;	//prioritaire
		else{if(mainComplete.test3MainOfJade()) 				result = 3;
		else{if(mainComplete.test16PetiteMainRouge()) 			result = 16;	//prioritaire
		else{if(mainComplete.test4MainOfCoral()) 				result = 4;
		else{if(mainComplete.test19PetiteMainBlanche()) 		result = 19;
		else{if(mainComplete.test5MaindOpaline()) 				result = 5;
		else{if(mainComplete.test6PairsOfShozum()) 			result = 6;
		else{if(mainComplete.test7LanternWonderful())		result = 7;
		else{if(mainComplete.test10TriangleEternel()) 			result = 10;	//prioritaire
		else{if(mainComplete.test22Mandarin()) 					result = 22;	//prioritaire
		else{if(mainComplete.test23MahjongImperial(this.風)) 	result = 23;	//prioritaire
		else{if(mainComplete.test8_SevenPairs()) 		result = 8;
		else{if(mainComplete.test9BonheursDomestiques()) 		result = 9;
		else{if(mainComplete.test11Tempete()) 					result = 11;
		else{if(mainComplete.test12SouffleDuDragon()) 			result = 12;
		else{if(mainComplete.test15MainVerteEtRouge()) 			result = 15;
		else{if(mainComplete.test17GrandeMainRouge()) 			result = 17;
		else{if(mainComplete.test18MainRougeEtBlanche()) 		result = 18;
		else{if(mainComplete.test20GrandeMainBlanche()) 		result = 20;
		else{if(mainComplete.test21MainBlancheEtVerte()) 		result = 21;
		
		}}}}}}}}}}}}}}}}}}}}}}
		
		return result;
		
	}
	

}
