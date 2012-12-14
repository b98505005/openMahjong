/*
 * Tuile.java
 *
 * Created on 17/06/2007. Copyright Raphael (synthaxerrors@gmail.com
 *
 * Classe représentant une Tuile
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

public class Tuile {
	int chiffre ;
	char couleur;
	String nom ;

	public Tuile(){
		chiffre = 0;
		couleur = 0;
		nom = new String("");
	}
	public Tuile(Tuile t){
		chiffre = t.chiffre;
		couleur = t.couleur;
		nom = new String(t.nom);
	}

	public Tuile ( int c , char f){
		chiffre = c ;
		couleur = f ;
		
		switch(couleur){
		case('c'):
			nom = new String(c+" caractère");
			break;
		case('b'):
			nom = new String(c+" bambou");
			break;
		case('r'):
			nom = new String(c+" rond");
			break;
		case('d'):
			switch(chiffre){
			case(1):
				nom = new String("dragon rouge");
				break;
			case(2):
				nom = new String("dragon blanc");
				break;
			case(3):
				nom = new String("dragon vert");
				break;
			}
		break;
		case('v'):
			switch(chiffre){
			case(1):
				nom = new String("vent d'est");
				break;
			case(2):
				nom = new String("vent du nord");
				break;
			case(3):
				nom = new String("vent d'ouest");
				break;
			case(4):
				nom = new String("vent du sud");
				break;
			}
		break;
		case('s'):
			nom = new String("saison "+chiffre);
			break;
		case('f'):
			nom = new String("fleur "+chiffre);
			break;
		}
	}
	

	public boolean estInferieure(Tuile tuile)
	{
		boolean resultat = false;
		
		if(this.valeurCouleur() < tuile.valeurCouleur()){
			resultat = true;
		}else if(this.valeurCouleur() == tuile.valeurCouleur()){
			if(this.chiffre < tuile.chiffre){
				resultat = true;
			}
		}
		return resultat;
	}
	
	public int valeurCouleur()
	{
		int resultat = 0;	//valeur pour tuile 'null'
		switch(couleur){
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
			File file = new File("images/"+chiffre+couleur+".jpg");
	        try {
	            image = ImageIO.read(file);
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        } 
	        
	        image = Main.convertToGrayscale(image);
	        result = new ImageIcon(image);
		}else{
			result = new ImageIcon("images/"+chiffre+couleur+".jpg");
		}
		
		return result;
	}
	
	public boolean egale(Tuile t){
		return (this.chiffre == t.chiffre && this.couleur == t.couleur);
	}

	public boolean isEmpty(){
		if(this.chiffre == 0 || this.couleur==0)
			return true;
		else
			return false;
	}
}