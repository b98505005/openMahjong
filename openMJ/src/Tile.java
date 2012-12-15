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
	int �P���j�p ;
	char �P������;
	String name ;

	public Tile(){
		�P���j�p = 0;
		�P������ = 0;
		name = new String("");
	}
	public Tile(Tile t){
		�P���j�p = t.�P���j�p;
		�P������ = t.�P������;
		name = new String(t.name);
	}

	public Tile ( int c , char f){
		�P���j�p = c ;
		�P������ = f ;
		
		switch(�P������){
		case('c'):
			name = new String(c+" �U�r");
			break;
		case('b'):
			name = new String(c+" ���l");
			break;
		case('r'):
			name = new String(c+" ��l");
			break;
		case('d'):
			switch(�P���j�p){
			case(1):
				name = new String("dragon ����");
				break;
			case(2):
				name = new String("dragon �ժO");
				break;
			case(3):
				name = new String("dragon �o");
				break;
			}
		break;
		case('v'):
			switch(�P���j�p){
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
			name = new String("season "+�P���j�p);
			break;
		case('f'):
			name = new String("flower "+�P���j�p);
			break;
		}
	}
	

	public boolean isBottom(Tile Tile)
	{
		boolean resultat = false;
		
		if(this.valueColor() < Tile.valueColor()){
			resultat = true;
		}else if(this.valueColor() == Tile.valueColor()){
			if(this.�P���j�p < Tile.�P���j�p){
				resultat = true;
			}
		}
		return resultat;
	}
	
	public int valueColor()
	{
		int resultat = 0;	//judge dragon, �r���, windCard
		switch(�P������){
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
	 * Covered tile's image
	 */
	public static ImageIcon giveBackground(){
		return new ImageIcon("images/fond.jpg"); 
	}
	public static ImageIcon giveBackground90(){
		return new ImageIcon("images/fond90.jpg"); 
	}
	public static ImageIcon giveBackground270(){
		return new ImageIcon("images/fond270.jpg"); 
	}
	public ImageIcon donneIcon(boolean isGrayed){
		ImageIcon result;

		if(isGrayed){
			BufferedImage image = null; 
			File file = new File("images/"+�P���j�p+�P������+".jpg");
	        try {
	            image = ImageIO.read(file);
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        } 
	        
	        image = Main.convertToGrayscale(image);
	        result = new ImageIcon(image);
		}else{
			result = new ImageIcon("images/"+�P���j�p+�P������+".jpg");
		}
		
		return result;
	}
	
	public boolean equal(Tile t){
		return (this.�P���j�p == t.�P���j�p && this.�P������ == t.�P������);
	}

	public boolean isEmpty(){
		if(this.�P���j�p == 0 || this.�P������==0)
			return true;
		else
			return false;
	}
}