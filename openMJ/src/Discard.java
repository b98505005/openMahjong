/*
 * Discard.java
 *
 * Created on 10/08/2007. Copyright Raphael (synthaxerrors@gmail.com)
 *
 * Classe permettant de repértorier les tuiles jetées
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
import java.util.Random;

import javax.swing.JLabel;


public class Discard {

	public static int X1 = 140;
	public static int X2 = 730;
	public static int Y1 = 140;
	public static int Y2 = 490;
	
	int tableau[][] = new int[9][6];		// tableau pour compter les tuiles jetées (9 numéros sur 6 couleurs)
	JLabel[] cptTuile = new JLabel[14];	// text label pour l'affichage du nb de tuiles jetée pour le joueur
	Tuile discardedTile = new Tuile();
	JLabel discardedTileLabl = new JLabel();
	JLabel[] oldTilesLabl = new JLabel[Main.NB_TUILES];
	int nbOldTile = 0;
	
	public Discard() {
		for(int i=0; i<9; i++){
			for(int j=0;j<6;j++){
				tableau[i][j] = 0;
			}
		}
		
		for(int i=0; i<cptTuile.length; i++){
			cptTuile[i] = new JLabel();
			cptTuile[i].setBounds(Main.X+(i*37)+15,Main.Y+52,10,10);	// placé sous les tuiles du joueur
			cptTuile[i].setText("");
		}
		
		Random randint = new Random();
		int posX, posY;
		for(int i=0; i<oldTilesLabl.length; i++){
			oldTilesLabl[i] = new JLabel();
			do{
				posX=randint.nextInt(X2-X1)+X1;
			//}while(posX<X1 || posX>X2);
			//do{
				posY=randint.nextInt(Y2-Y1)+Y1;
			}while(!isPosValid(posX, posY));
			oldTilesLabl[i].setBounds(posX, posY, 37, 49);
			oldTilesLabl[i].setIcon(null);
		}
		discardedTileLabl.setBounds(410, 330, 37, 49);
		discardedTileLabl.setIcon(null);
	}
	
	/**
	 * RaZ du tableau
	 */
	public void init(){
		for(int i=0; i<9; i++){
			for(int j=0;j<6;j++){
				tableau[i][j] = 0;
			}
		}
		for(int i=0; i<Main.NB_TUILES; i++){
			oldTilesLabl[i].setIcon(null);
		}
		discardedTile = new Tuile();
		nbOldTile = 0;
		discardedTileLabl.setIcon(null);
	}

	public boolean isPosValid(int x, int y){
		boolean result = true;
		
		if(((x+37)>410 && x<447) && ((y+47>330) && y<379)) result = false;
		if(((x+37)>600 && x<880) && ((y+47>420) && y<570)) result = false;
		return result;
	}

	/**
	 * ajoute nb fois la tuile t au tableau
	 */
	public void declare(Tuile t, int nb){
		tableau[t.chiffre-1][t.valeurCouleur()-1] +=nb;
		

	}
	
	/**
	 * affiche la nouvelle tuile jetée au milieu du plateau 
	 */
	public void discardTile(Tuile t){
		discardedTile = t;
		discardedTileLabl.setIcon(t.donneIcon(false));
	}
	
	/**
	 * Quand personne ne prend la tuile jetée
	 */
	public void flushDiscardedTile(){
		if(!discardedTile.isEmpty()){
			declare(discardedTile, 1);
			//TODO
//			oldTilesLabl[nbOldTile].setIcon(discardedTile.donneIcon(true));		
			nbOldTile++;
		}
		discardedTile = new Tuile();
		discardedTileLabl.setIcon(null);
	}
	/**
	 * Quand qqun prend la tuile jetée
	 */
	public void takeDiscardedTile(){
		discardedTile = new Tuile();
		discardedTileLabl.setIcon(null);
	}
	
	public Tuile getDiscard(){
		return discardedTile;
	}
	

	/**
	 * affiche le nombre de tuile jetée pour chaque tuile de la mainCache
	 * si l'option est activée
	 */
	public void affiche(Jeu main){
		int cpt=0;
		for(int i=0; i<14; i++){
			cptTuile[i].setText("");
		}
		if(Main.montreDiscard){
			for(int i=0; i<14; i++){
				for(int j=0; j<main.figures[i].nbTuile; j++){
					cptTuile[cpt].setText(""+nbJet(main.figures[i].tuile));
					cpt++;
				}
			}
		}
	}
	
	/**
	 * renvoie le nombre de fois que la tuile t a été jetée
	 */
	public int nbJet(Tuile t){
		int valeur = t.valeurCouleur();
		if(valeur>0 && valeur<7) return tableau[t.chiffre-1][t.valeurCouleur()-1];
		else return 0;
	}

}
