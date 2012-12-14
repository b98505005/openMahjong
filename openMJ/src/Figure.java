/*
 * Figure.java
 *
 * Created on 22/08/2007. Copyright Raphael (synthaxerrors@gmail.com
 *
 * Classe rep撱占ntant un groupe de tile faisant un figure (seule, paire, pung, kong, cach嚙箴xpos嚙�* 
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

import javax.swing.ImageIcon;

public class Figure {
	
	
	Tile tile;
	int nbTile;
	Main.typeFig type;
	boolean isCover;
	
	Figure(){
		tile = new Tile();
		nbTile = 0;
		type = Main.typeFig.SIMPLE;
		isCover = true;
	}
	
	Figure(Tile t, int nb){
		
		tile = new Tile(t);
		nbTile = nb;
		majType();
		isCover = true;
	}
	
	Figure(Figure f){
		tile = new Tile(f.tile);
		nbTile = f.nbTile;
		type = f.type;
		isCover = f.isCover;
	}
	
	private void majType(){
		switch(nbTile){
		case 2: 
			type = Main.typeFig.PAIR;
			break;
		case 3:
			type = Main.typeFig.PONG;
			break;
		case 4:
			type = Main.typeFig.GUNG;
			break;
		case 0:
		case 1:
		default:
			type = Main.typeFig.SIMPLE;
		break;
		}
	}

	public void expose(){
		isCover = false;
	}
	
	public boolean addTile(Tile t){
		if(nbTile<4){		//il doit y avoir moins de 4 tile ds la figure
			if(nbTile==0){		//si la figure est libre
				tile = new Tile(t);	//on met 嚙篌our la tile de la figure
			}
			else if(t.name.compareTo(tile.name)!=0){	//sinon la tile doit �裸e la m�庸 que celle de la figure
				return false;
			}
			nbTile++;
			majType();
			return true;
		}
		else{return false;}
	}
	
	public boolean removedTile(){
		if(nbTile>0){
			nbTile--;
			majType();
			if(nbTile == 0){
				tile = new Tile();
			}
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Retourne le nombre de point de la figure
	 * gagnant vaut true si le joueur fait mahjong
	 * vent est le vent du joueur (1~4)
	 */
	public int getValue(boolean winner, int vent){
		
		int score = 0;
		switch (tile.color){
		case 'f':
		case 's':
			score = 4;	//fleur/saison : 4pts
			break;
		case 'v':
		case 'd':
			if(type == Main.typeFig.PONG){
				score = 8; //pung dragon/vent : 8pts
			}
			else if(type == Main.typeFig.GUNG){
				score = 16;	//kong dragon/vent : 16pts
			}
			else if(winner &&((tile.color == 'd' || (tile.color =='v' && (tile.牌面==Main.ventDominant || tile.牌面==(vent)))))){
				score = 2;	// une paire du vent dominant ou du joueur ou de dragon vaut 2 point si le joueur est gagnant
			}
			break;
		case 'c':
		case 'r':
		case 'b':
			if(tile.牌面 == 1 || tile.牌面 == 9){
				if(type == Main.typeFig.PONG){
					score = 8;	// pung de 1/9 rond/bamboo/caract�牠s : 8pts
				}
				else if(type == Main.typeFig.GUNG){
					score = 16;	// kong de 1/9 rond/bamboo/caract�牠s : 16pts
				}
			}
			else{
				if(type == Main.typeFig.PONG){
					score = 4;	// pung de autre rond/bamboo/caract�牠s : 4pts
				}
				else if(type == Main.typeFig.GUNG){
					score = 8;	// kong de autre rond/bamboo/caract�牠s : 8pts
				}
			}
			break;
		default:
			break;
		}
		if(isCover == true && ((type == Main.typeFig.PONG) ||(type == Main.typeFig.GUNG))){
			score*=2;		//double le score si combi cach撊�		
		}
		return score;
	}
	/**
	 *	Retoune le nombre de multiplicateur par 2 de la combi
	 * vent est le vent du joueur (1~4) 
	 */
	public int getMulti(int wind){
		int multi = 0;
		if((type == Main.typeFig.PONG) ||(type == Main.typeFig.GUNG)){
			if(tile.color == 'd'){	// un double pour une combi de dragon
				multi += 1;
			}
			if((tile.color == 'v') && tile.牌面 == (wind+1)){
				multi +=1;		// un double une combi du vent du joueur 
			}
			if( tile.color == 'v' && tile.牌面 == Main.ventDominant){
				multi +=1;		// un double pour une combi du vent dominant 
			}
		}else if((tile.color == 'f' || tile.color == 's') && tile.牌面 == (wind+1)){
			multi +=1;		// un double pour la saison/fleur du joueur
		}
		
		return multi;
	}
	
	
	
	public boolean isBottom(Figure fig)
	{
		return this.tile.isBottom(fig.tile);
	}
	
	public boolean isFree(){
		return tile.name.length()==0;
	}
	
	public boolean is字牌(){
		return (tile.color == 's') || (tile.color == 'f'); 
	}
	
	public boolean isNormal(){
		return (tile.color == 'c') || (tile.color == 'r')|| (tile.color == 'b'); 
	}
	
	public String name(){
		return tile.name;
	}
	
	public char get_colour(){
		return tile.color;
	}
	
	public int get_牌面(){
		return tile.牌面;
	}
	
	public ImageIcon donneIcon(int angle, boolean isGrayed){
		ImageIcon result;
		
		if(angle != 0){
			result = Main.rotationIcon(tile.donneIcon(isGrayed),angle); 
		}
		else{
			result = tile.donneIcon(isGrayed); 
		}
		return result;
	}
}
