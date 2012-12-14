/*
 * Discard.java
 *
 * Created on 10/08/2007. Copyright Raphael (synthaxerrors@gmail.com)
 *
 * Classe permettant de rep撱系orier les Tiles jet撊泅
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
	
	int table[][] = new int[9][6];		// tableau pour compter les Tiles jet撊泅 (9 num撱矣s sur 6 couleurs)
	JLabel[] cptTile = new JLabel[14];	// text label pour l'affichage du nb de Tiles jet撊�pour le joueur
	Tile discarded = new Tile();
	JLabel discardedTileLabl = new JLabel();
	JLabel[] oldTilesLabl = new JLabel[Main.NB_Tiles];
	int nbOld = 0;
	
	public Discard() {
		for(int i=0; i<9; i++){
			for(int j=0;j<6;j++){
				table[i][j] = 0;
			}
		}
		
		for(int i=0; i<cptTile.length; i++){
			cptTile[i] = new JLabel();
			cptTile[i].setBounds(Main.X+(i*37)+15,Main.Y+52,10,10);	// plac嚙編ous les Tiles du joueur
			cptTile[i].setText("");
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
				table[i][j] = 0;
			}
		}
		for(int i=0; i<Main.NB_Tiles; i++){
			oldTilesLabl[i].setIcon(null);
		}
		discarded = new Tile();
		nbOld = 0;
		discardedTileLabl.setIcon(null);
	}

	public boolean isPosValid(int x, int y){
		boolean result = true;
		
		if(((x+37)>410 && x<447) && ((y+47>330) && y<379)) result = false;
		if(((x+37)>600 && x<880) && ((y+47>420) && y<570)) result = false;
		return result;
	}

	/**
	 * ajoute nb fois la Tile t au tableau
	 */
	public void declare(Tile t, int nb){
		table[t.牌面-1][t.valeurCouleur()-1] +=nb;
		

	}
	
	/**
	 * affiche la nouvelle Tile jet撊�au milieu du plateau 
	 */
	public void discardTile(Tile t){
		discarded = t;
		discardedTileLabl.setIcon(t.donneIcon(false));
	}
	
	/**
	 * Quand personne ne prend la Tile jet撊�	 */
	public void flushDiscardedTile(){
		if(!discarded.isEmpty()){
			declare(discarded, 1);
			//TODO
//			oldTilesLabl[nbOld].setIcon(discarded.donneIcon(true));		
			nbOld++;
		}
		discarded = new Tile();
		discardedTileLabl.setIcon(null);
	}
	/**
	 * Quand qqun prend la Tile jet撊�	 */
	public void takeDiscardedTile(){
		discarded = new Tile();
		discardedTileLabl.setIcon(null);
	}
	
	public Tile getDiscard(){
		return discarded;
	}
	

	/**
	 * affiche le nombre de Tile jet撊�pour chaque Tile de la mainCache
	 * si l'option est activ撊�	 */
	public void affiche(Game main){
		int cpt=0;
		for(int i=0; i<14; i++){
			cptTile[i].setText("");
		}
		if(Main.montreDiscard){
			for(int i=0; i<14; i++){
				for(int j=0; j<main.figures[i].nbTile; j++){
					cptTile[cpt].setText(""+nbJet(main.figures[i].tile));
					cpt++;
				}
			}
		}
	}
	
	/**
	 * renvoie le nombre de fois que la Tile t a 敶蕭jet撊�	 */
	public int nbJet(Tile t){
		int valeur = t.valeurCouleur();
		if(valeur>0 && valeur<7) return table[t.牌面-1][t.valeurCouleur()-1];
		else return 0;
	}

}
