/*
 * Tile.java
 *
 * Created on 17/06/2007. Copyright Raphael (synthaxerrors@gmail.com
 *
 * Classe repr�sentant une Tile
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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Tile {
	int �P�� ;
	char color;
	String name ;

	public Tile(){
		�P�� = 0;
		color = 0;
		name = new String("");
	}
	public Tile(Tile t){
		�P�� = t.�P��;
		color = t.color;
		name = new String(t.name);
	}

	public Tile ( int c , char f){
		�P�� = c ;
		color = f ;
		
		switch(color){
		case('c'):
			name = new String(c+" caract�re");
			break;
		case('b'):
			name = new String(c+" bambou");
			break;
		case('r'):
			name = new String(c+" rond");
			break;
		case('d'):
			switch(�P��){
			case(1):
				name = new String("dragon rouge");
				break;
			case(2):
				name = new String("dragon blanc");
				break;
			case(3):
				name = new String("dragon vert");
				break;
			}
		break;
		case('v'):
			switch(�P��){
			case(1):
				name = new String("�F��");
				break;
			case(2):
				name = new String("�_��");
				break;
			case(3):
				name = new String("�護");
				break;
			case(4):
				name = new String("�n��");
				break;
			}
		break;
		case('s'):
			name = new String("saison "+�P��);
			break;
		case('f'):
			name = new String("fleur "+�P��);
			break;
		}
	}
	

	public boolean isBottom(Tile Tile)
	{
		boolean resultat = false;
		
		if(this.valeurCouleur() < Tile.valeurCouleur()){
			resultat = true;
		}else if(this.valeurCouleur() == Tile.valeurCouleur()){
			if(this.�P�� < Tile.�P��){
				resultat = true;
			}
		}
		return resultat;
	}
	
	public int valeurCouleur()
	{
		int resultat = 0;	//valeur pour Tile 'null'
		switch(color){
		case('b'):
			resultat = 1; 
			break;
		case('c'):
			resultat = 2; 
			break;
		case('r'):
			resultat = 3; 
			break;
		case('v'):
			resultat = 4; 
			break;
		case('d'):
			resultat = 5; 
			break;
		case('f'):
			resultat = 6; 
			break;
		case('s'):
			resultat = 7; 
			break;
		}
		return resultat;
	}
	
	/** 
	 * Donne l'image de fond des cartes
	 */
	public static ImageIcon donneFond(){
		return new ImageIcon("images/fond.jpg"); 
	}
	public static ImageIcon donneFond90(){
		return new ImageIcon("images/fond90.jpg"); 
	}
	public static ImageIcon donneFond270(){
		return new ImageIcon("images/fond270.jpg"); 
	}
	public ImageIcon donneIcon(boolean isGrayed){
		ImageIcon result;

		if(isGrayed){
			BufferedImage image = null; 
			File file = new File("images/"+�P��+color+".jpg");
	        try {
	            image = ImageIO.read(file);
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        } 
	        
	        image = Main.convertToGrayscale(image);
	        result = new ImageIcon(image);
		}else{
			result = new ImageIcon("images/"+�P��+color+".jpg");
		}
		
		return result;
	}
	
	public boolean egale(Tile t){
		return (this.�P�� == t.�P�� && this.color == t.color);
	}

	public boolean isEmpty(){
		if(this.�P�� == 0 || this.color==0)
			return true;
		else
			return false;
	}
}