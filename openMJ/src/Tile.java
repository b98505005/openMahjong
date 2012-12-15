/*
 * Tile.java
 *
 * Created on 17/06/2007. Copyright Raphael (synthaxerrors@gmail.com
 *
 * Classe repr廥entant une Tile
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
	int 牌面大小 ;
	char 牌面種類;
	String name ;

	public Tile(){
		牌面大小 = 0;
		牌面種類 = 0;
		name = new String("");
	}
	public Tile(Tile t){
		牌面大小 = t.牌面大小;
		牌面種類 = t.牌面種類;
		name = new String(t.name);
	}

	public Tile ( int c , char f){
		牌面大小 = c ;
		牌面種類 = f ;
		
		switch(牌面種類){
		case('c'):
			name = new String(c+" 萬字");
			break;
		case('b'):
			name = new String(c+" 條子");
			break;
		case('r'):
			name = new String(c+" 桶子");
			break;
		case('d'):
			switch(牌面大小){
			case(1):
				name = new String("dragon 紅中");
				break;
			case(2):
				name = new String("dragon 白板");
				break;
			case(3):
				name = new String("dragon 發");
				break;
			}
		break;
		case('v'):
			switch(牌面大小){
			case(1):
				name = new String("東風");
				break;
			case(2):
				name = new String("北風");
				break;
			case(3):
				name = new String("西風");
				break;
			case(4):
				name = new String("南風");
				break;
			}
		break;
		case('s'):
			name = new String("season "+牌面大小);
			break;
		case('f'):
			name = new String("flower "+牌面大小);
			break;
		}
	}
	

	public boolean isBottom(Tile Tile)
	{
		boolean resultat = false;
		
		if(this.valueColor() < Tile.valueColor()){
			resultat = true;
		}else if(this.valueColor() == Tile.valueColor()){
			if(this.牌面大小 < Tile.牌面大小){
				resultat = true;
			}
		}
		return resultat;
	}
	
	public int valueColor()
	{
		int resultat = 0;	//judge dragon, 字桶條, windCard
		switch(牌面種類){
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
			File file = new File("images/"+牌面大小+牌面種類+".jpg");
	        try {
	            image = ImageIO.read(file);
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        } 
	        
	        image = Main.convertToGrayscale(image);
	        result = new ImageIcon(image);
		}else{
			result = new ImageIcon("images/"+牌面大小+牌面種類+".jpg");
		}
		
		return result;
	}
	
	public boolean equal(Tile t){
		return (this.牌面大小 == t.牌面大小 && this.牌面種類 == t.牌面種類);
	}

	public boolean isEmpty(){
		if(this.牌面大小 == 0 || this.牌面種類==0)
			return true;
		else
			return false;
	}
}