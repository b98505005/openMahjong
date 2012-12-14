/*
 * Jeu.java
 *
 * Created on 22/08/2007. Copyright Raphael (synthaxerrors@gmail.com
 *
 * Classe repésentant un jeu de tuiles composé de figures
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


public class Jeu {

	Figure figures[];
	int tailleMax;
	
	Jeu(int nb){
		figures = new Figure[nb];
		for(int i=0; i<nb; i++){
			figures[i] = new Figure();
		}
		tailleMax = nb;
	}
	
	Jeu(Jeu j){
		figures = new Figure[j.tailleMax];
		for(int i=0; i<j.tailleMax; i++){
			figures[i] = new Figure(j.figures[i]);
		}
		tailleMax = j.tailleMax;
	}
	/**
	 * ajoute la tuile t dans le jeu
	 * renvoie la position absolue de la tuile (-1 si pas d'emplacement trouvé)
	 */
	public int ajouteTuile(Tuile t){
		int index=0;
		
		if((index=aTuile(t))<0){ //le joueur ne possede pas déjà cette tuile
			index=0;
			while(figures[index].nbTuile>0 && index<13){	//cherche la première figure libre
				index++;
			}
			if(figures[index].nbTuile>0 && index==13){
				return -1;
			}
		}
		figures[index].ajoutTuile(t);	//ajoute la tuile t dans la figure index
		triTuile();
		return getPosFromTuile(t);
	}
	
	/**
	 * retire la tuile correspondant à la position absolue i dans le jeu
	 * retourne la tuile retirée
	 */
	public Tuile retireTuile(int i){
		Tuile t = getTuileFromPos(i);
		if(t.nom.length() != 0){ //la tuile est dans le jeu
			int index = getFigFromTuile(t);
			figures[index].enleveTuile();
		}
		triTuile();
		return t;
	}
	
	public void ajouteFigure(Figure f, boolean cache){
		int index=0;
		while(figures[index].nbTuile>0){
			index++;
		}
		figures[index] = new Figure(f);
		if(cache == false){
			figures[index].expose();
		}
		triTuile();
	}
	
	public void retireFigure(int i){
		figures[i] = new Figure();
		triTuile();
	}
	
	/**
	 * Combine 2 jeux et retourne le resultat
	 */
	public Jeu combineJeux(Jeu jeu)
	{
		Jeu result = new Jeu(this.tailleMax+jeu.tailleMax);
		
		result.figures = new Figure[this.tailleMax+jeu.tailleMax];
		
		for(int i=0; i< this.tailleMax; i++){
			result.figures[i] = new Figure(this.figures[i]);
		}
		for(int i=0; i< jeu.tailleMax; i++){
			result.figures[i+this.tailleMax] = new Figure(jeu.figures[i]);
		}
		
		return result;
	}
	
	/**
	 * retourne le nombre de pung et de kong dans le jeu
	 */
	public int getNbPungKong(){
		int cpt=0;
		for(int i=0; i<tailleMax; i++){
			if(figures[i].type == Main.typeFig.KONG || figures[i].type == Main.typeFig.PUNG){
				cpt++;
			}
		}
		return cpt;
	}
	
	/**
	 * retourne le nombre de kong dans le jeu
	 */
	public int getNbKong(){
		int cpt=0;
		for(int i=0; i<tailleMax; i++){
			if(figures[i].type == Main.typeFig.KONG){
				cpt++;
			}
		}
		return cpt;
	}
	
	/**
	 * retourne le nombre de kong cachés de la couleur demandée dans le jeu
	 */
	public int getNbKongCaches(char couleur){
		int cpt=0;
		for(int i=0; i<tailleMax; i++){
			if(figures[i].type == Main.typeFig.KONG && figures[i].estCache==true
					&& figures[i].tuile.couleur == couleur){
				cpt++;
			}
		}
		return cpt;
	}
	
	/**
	 * retourne le nombre de tuile t dans le jeu
	 */
	public int getNbTuile(Tuile t){
		for(int i=0; i<tailleMax; i++){
			if(figures[i].tuile.nom.compareTo(t.nom)==0){
				return figures[i].nbTuile;
			}
		}
		return 0; 
	}
	
	/**
	 * retourne le nombre de tuile dans le jeu
	 */
	public int getNbTuile(){
		int nbTuile = 0;
		for(int i=0; i<tailleMax; i++){
			nbTuile += figures[i].nbTuile;
		}
		return nbTuile; 
	}
	
	public int getValeur(boolean estGagnant, int vent){
		int result=0;
		for(int i=0; i<tailleMax; i++){
			result+=figures[i].getValeur(estGagnant, vent);
		}
		return result;
	}
	
	public int getMulti(int vent){
		int result=0;
		for(int i=0; i<tailleMax; i++){
			result+=figures[i].getMulti(vent);
		}
		return result;
	}
	
	/**
	 * retourne la position absolue de la tuile t
	 */
	public int getPosFromTuile(Tuile t){
		int result = 0;
		for(int i=0; i<tailleMax; i++){
			if(figures[i].nom().compareTo(t.nom) == 0){
				break;
			}
			else{
				result+=figures[i].nbTuile;
				if(i==(tailleMax-1)){
					return -1;
				}
			}
		}
		return result;
	}
	
	/**
	 * retourne la tuile correspondant à la position absolue pos
	 */
	public Tuile getTuileFromPos(int pos){
		for(int i=0; i<tailleMax; i++){
			if(pos<figures[i].nbTuile){
				return figures[i].tuile;
			}
			else{
				pos -= figures[i].nbTuile;
			}
		}
		return new Tuile();
	}
	
	/**
	 * retourne le numero de la figure contenant la tuile t
	 */
	public int getFigFromTuile(Tuile t){
		if(t.nom.length() != 0){
			for(int i=0; i<tailleMax; i++){
				if(figures[i].tuile.nom.compareTo(t.nom) == 0){
					return i;
				}
			}
		}
		return -1;
	}
	
	public int getFigFromPos(int i){
		return getFigFromTuile(getTuileFromPos(i));
	}
	
	/**
	 * retourne le numero de la première figure dont le nombre
	 * de tuile est égale à nb. -1 s'il n'y en a pas
	 */
	public int getFirstFig(int nb, boolean sens){
		int cpt = -1;
		if(nb<0) return -1;
		for(int i=0; i<tailleMax; i++){
			if((figures[i].nbTuile >= nb && sens == Main.DESCENDANT)
					|| (figures[i].nbTuile <= nb && sens == Main.ASCENDANT)){
				cpt++;
			}
		}
		return cpt;
	}
	
	public boolean aPaire(){
		for(int i=0; i<tailleMax; i++){
			if(figures[i].type == Main.typeFig.PAIRE){
				return true;
			}
		}
		return false;
	}
	
	public boolean aPaire(Tuile t){
		for(int i=0; i<tailleMax; i++){
			if(figures[i].tuile.nom.compareTo(t.nom)==0 && (figures[i].type == Main.typeFig.PAIRE ||figures[i].type == Main.typeFig.PUNG)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * fontion qui dit si le joueur possede déjà une figure avec la tuile t
	 * renvoi le numero de la figure ou -1 si le joueur ne la possede pas 
	 */
	public int aTuile(Tuile t){
		int result = -1;
		for(int i=0; i<13; i++){
			if(figures[i].nom().compareTo(t.nom) == 0){
				result = i;
			}
		}
		return result;
	}
	
	/**
	 * fonction qui retourne true si le jeu possède au moins une tuile
	 * de type dragon/vent
	 */
	public boolean aHonneur(){
		for(int i=0; i<tailleMax; i++){
			if(figures[i].couleur()=='v' || figures[i].couleur()=='d'){
				return true;
			}
		}
		return false;
	}
	
	public boolean estPur(){
		char coul = 0;
		boolean pur = true;
		
		for(int i=0; i<tailleMax; i++){
			if(figures[i].nbTuile>0 && figures[i].estNormal()){
				if(coul == 0){
					coul = figures[i].couleur();
				}else if(coul != figures[i].couleur()){
					pur = false;
					break;
				}
			}
		}
		return pur;
	}
	
	public boolean estVide(int i){
		if(getFigFromPos(i)>=0){
			if(figures[getFigFromPos(i)].estLibre()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Petit tri à bulle qui classe en fonction des tuiles
	 */
	public void triTuile(){
		Figure temp = new Figure();
		int i = 0;
		
		/* tri de la main cachée */
		while(i<tailleMax-1){
//			if(((figures[i].estLibre()) && (!figures[i+1].estLibre()))	// pas de tuile
//					|| (figures[i].couleur()<figures[i+1].couleur()) 	// ou couleur supérieure à la suivante 
//					|| (figures[i].couleur()==figures[i+1].couleur() && figures[i].chiffre()<figures[i+1].chiffre())) 	// ou couleur identique et chiffre supérieur
			if(figures[i].estInferieure(figures[i+1]))
			{
				// intervertion des tuiles
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
	 * Tri à bulle qui tri les figures en fonction du nombre 
	 * de tuile de chaque figure
	 */
	public void triNumerique(boolean ordre){
		Figure temp = new Figure();
		int i = 0;
		
		/* tri de la main cachée */
		while(i<tailleMax-1){
			if(figures[i].nbTuile<figures[i+1].nbTuile && ordre == Main.DESCENDANT
					|| figures[i].nbTuile>figures[i+1].nbTuile && ordre == Main.ASCENDANT)
			{
				// intervertion des tuiles
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
	 * retourne la couleur de la première tuile trouvée (non honneur)
	 * 0 sinon (i.e. pur honneurs). Si le jeu est pur (à checker avant), 
	 * donne la couleur du jeu 
	 */
	public char couleur(){
		char coul = 0;
//		if(estPur()){
			for(int i=0; i<tailleMax; i++){
				if(figures[i].nbTuile>0 && figures[i].estNormal()){
					coul =  figures[i].couleur();
					break;
				}
			}
//		}
		return coul;
	}
	
	/* retourne true si le jeu n'est composé que de Pung/Kong de tuiles 
	 * majeures (1/9 rond/bambou/caractère)*/
	public boolean seulmentTuileMaj(){
		boolean result = true;
		
		for(int i=0; i<tailleMax; i++){
			if(figures[i].type == Main.typeFig.KONG ||  figures[i].type == Main.typeFig.PUNG){
				if(!figures[i].estNormal() || (figures[i].chiffre()!=1) || (figures[i].chiffre()!=9)){
					result = false;
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * retourne le nombre de figure de la couleur demandée
	 */
	public int getNbFigure(Main.typeFig figure, char couleur, boolean kongEgal2Paire){
		int result = 0;
		
		for(int i=0; i<tailleMax; i++){
			if(figures[i].tuile.couleur == couleur){
				if(figures[i].type == figure){
					result ++;
				}//un kong caché peut compter comme 2 paires
				else if(kongEgal2Paire){ 
					if(figure == Main.typeFig.PAIRE && figures[i].type==Main.typeFig.KONG && figures[i].estCache){
						result+=2;
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * retourne le nombre de figure correspondant au chiffre et à la couleur demandés
	 */
	public int getNbFigure(Main.typeFig figure, int chiffre,char couleur, boolean kongEgal2Paire){
		int result = 0;
		
		for(int i=0; i<tailleMax; i++){
			if(figures[i].tuile.couleur == couleur && figures[i].tuile.chiffre == chiffre){
				if(figures[i].type == figure){
					result ++;
				}//un kong caché peut compter comme 2 paires
				else if(kongEgal2Paire){ 
					if(figure == Main.typeFig.PAIRE && figures[i].type==Main.typeFig.KONG && figures[i].estCache){
						result+=2;
					}
				}
			}
		}
		return result;
	}
	/**
	 * retourne le nombre de figure demandée
	 */
	public int getNbFigure(Main.typeFig figure, boolean kongEgal2Paire){
		int result = 0;
		
		for(int i=0; i<tailleMax; i++){
			if(figures[i].type == figure){
				result ++;
			}//un kong caché peut compter comme 2 paires
			else if(kongEgal2Paire){ 
				if(figure == Main.typeFig.PAIRE && figures[i].type==Main.typeFig.KONG && figures[i].estCache){
					result+=2;
				}
			}
		}
		return result;
	}
	
	/**
	 * retourne le nombre de Pung/Kong de la couleur demandée
	 */
	public int getNbPungKong(char couleur){
		int result = 0;
		
		for(int i=0; i<tailleMax; i++){
			if(figures[i].tuile.couleur == couleur){
				if(figures[i].type == Main.typeFig.KONG || figures[i].type == Main.typeFig.PUNG){
					result ++;
				}
			}
		}
		return result;
	}
	
	/**
	 * retourne le nombre de Pung/Kong de la couleur et du chiffre demandée
	 */
	public int getNbPungKong(int chiffre, char couleur){
		int result = 0;
		
		for(int i=0; i<tailleMax; i++){
			if(figures[i].tuile.couleur == couleur && figures[i].tuile.chiffre == chiffre){
				if(figures[i].type == Main.typeFig.KONG || figures[i].type == Main.typeFig.PUNG){
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
	public boolean test1PungVenteux(){
		boolean result = false;
		
		if(this.getNbPungKong('c') == 1 
				&& this.getNbPungKong('r') == 1
				&& this.getNbPungKong('b') == 1){
			if(this.getNbTuile(new Tuile(1,'v')) >0 
					&& this.getNbTuile(new Tuile(2,'v'))>0
					&& this.getNbTuile(new Tuile(3,'v'))>0
					&& this.getNbTuile(new Tuile(4,'v'))>0){
				if(this.getNbTuile(new Tuile(1,'v')) == 2  
						|| this.getNbTuile(new Tuile(2,'v'))== 2
						|| this.getNbTuile(new Tuile(3,'v'))== 2
						|| this.getNbTuile(new Tuile(4,'v'))== 2){
					result = true;
				}
			}
		}
		return result;
	}
	/**
	 * Test le Mahjong special "Les Paires Venteuses":
	 * 	- 1 paire de chaque vent
	 * 	- 1 paire de chaque séries
	 */
	public boolean test2PairesVenteuses(){
		boolean result = false;
		
		if(this.getNbFigure(Main.typeFig.PAIRE, 1,'v', false)==1
				&& 	this.getNbFigure(Main.typeFig.PAIRE, 2,'v', false)==1
				&& 	this.getNbFigure(Main.typeFig.PAIRE, 3,'v', false)==1
				&& 	this.getNbFigure(Main.typeFig.PAIRE, 4,'v', false)==1
				&& 	this.getNbFigure(Main.typeFig.PAIRE, 'b', false)==1
				&& 	this.getNbFigure(Main.typeFig.PAIRE, 'r', false)==1
				&& 	this.getNbFigure(Main.typeFig.PAIRE, 'c', false)==1){
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
	public boolean test3MainDeJade(){
		boolean result = false;
		
		if(this.getNbPungKong('b')==3
				&& 	this.getNbFigure(Main.typeFig.PAIRE, 'b', false)==1
				&& 	this.getNbPungKong(3,'d')==1){
			result = true;
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La Main de Corail":
	 * 	- 3 pung de caractère
	 *  - 1 paire de caractère
	 *  - 1 pung de dragon rouge
	 */
	public boolean test4MainDeCorail(){
		boolean result = false;
		
		if(this.getNbPungKong('c')==3
				&& 	this.getNbFigure(Main.typeFig.PAIRE, 'c', false)==1
				&& 	this.getNbPungKong(1,'d')==1){
			result = true;
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La Main d'Opaline":
	 * 	- 3 pung de cercle
	 *  - 1 paire de cercle
	 *  - 1 pung de dragon blanc
	 */
	public boolean test5MaindOpaline(){
		boolean result = false;
		
		if(this.getNbPungKong('r')==3
				&& 	this.getNbFigure(Main.typeFig.PAIRE, 'r', false)==1
				&& 	this.getNbPungKong(2,'d')==1){
			result = true;
		}
		return result;
	}
	/**
	 * Test le Mahjong special "Les Paires de Shozum":
	 * 	- 6 paires de chaque série
	 *  - 1 paire du dragon associé à la série
	 */
	public boolean test6PairesDeShozum(){
		boolean result = false;
		
		if(this.getNbFigure(Main.typeFig.PAIRE, 'c', true)==6
				&& 	this.getNbFigure(Main.typeFig.PAIRE, 1, 'd', false)==1){
			result = true;
		}
		if(this.getNbFigure(Main.typeFig.PAIRE, 'r', true)==6
				&& 	this.getNbFigure(Main.typeFig.PAIRE, 2, 'd', false)==1){
			result = true;
		}
		if(this.getNbFigure(Main.typeFig.PAIRE, 'b', true)==6
				&& 	this.getNbFigure(Main.typeFig.PAIRE, 3, 'd', false)==1){
			result = true;
		}
		return result;
	}
	/**
	 * Test le Mahjong special "Les 13 Lanternes Merveilleuses":
	 * 	- Les 1 et les 9 des 3 séries 
	 *  - Les 4 vents et 3 dragons
	 *  - 1 Honneur
	 */
	public boolean test7LanternesMerveilleuses(){
		boolean result = false;

		if(this.getNbFigure(Main.typeFig.SIMPLE, 1,'r', false)==1
				&& this.getNbFigure(Main.typeFig.SIMPLE, 9,'r', false)==1
				&& this.getNbFigure(Main.typeFig.SIMPLE, 1,'c', false)==1
				&& this.getNbFigure(Main.typeFig.SIMPLE, 9,'c', false)==1
				&& this.getNbFigure(Main.typeFig.SIMPLE, 1,'b', false)==1
				&& this.getNbFigure(Main.typeFig.SIMPLE, 9,'b', false)==1){
			if(this.getNbTuile(new Tuile(1, 'v')) > 0
					&& this.getNbTuile(new Tuile(2, 'v')) > 0
					&& this.getNbTuile(new Tuile(3, 'v')) > 0
					&& this.getNbTuile(new Tuile(4, 'v')) > 0
					&& this.getNbTuile(new Tuile(1, 'd')) > 0
					&& this.getNbTuile(new Tuile(2, 'd')) > 0
					&& this.getNbTuile(new Tuile(3, 'd')) > 0){
				if(this.getNbTuile(new Tuile(1, 'v')) == 2
						|| this.getNbTuile(new Tuile(2, 'v')) == 2
						|| this.getNbTuile(new Tuile(3, 'v')) == 2
						|| this.getNbTuile(new Tuile(4, 'v')) == 2
						|| this.getNbTuile(new Tuile(1, 'd')) == 2
						|| this.getNbTuile(new Tuile(2, 'd')) == 2
						|| this.getNbTuile(new Tuile(3, 'd')) == 2){
					result = true;
				}
			}
		}
		return result;
	}
	/**
	 * Test le Mahjong special "Les 7 muses du poète chinois":
	 * 	- 7 paires d'honneurs ou majeur 
	 */
	public boolean test8MusesDuPoeteChinois(){
		boolean result = true;

		if(this.getNbFigure(Main.typeFig.PAIRE, true)==7)	{
			// aucune des paires ne doit être mineure
			for(int i=2; i<8; i++){
				if(this.getNbTuile(new Tuile(i, 'b')) >0
						|| this.getNbTuile(new Tuile(i, 'c')) >0
						|| this.getNbTuile(new Tuile(i, 'r')) >0){
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

		if(this.getNbPungKong('v')==4
				&& this.getNbFigure(Main.typeFig.PAIRE, false)==1)	{
			result = true;			
		}
		return result;
	}
	/**
	 * Test le Mahjong special "Le triangle éternel":
	 * 	- 4 paires de vent
	 *  - 3 paires de dragon 
	 */
	public boolean test10TriangleEternel(){
		boolean result = false;

		if(this.getNbFigure(Main.typeFig.PAIRE,'v', true)==4
				&& this.getNbFigure(Main.typeFig.PAIRE, 'd', true)==3)	{
			result = true;			
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La tempête":
	 * 	- 2 pung de dragons
	 *  - 1 paire de chaque vent 
	 */
	public boolean test11Tempete(){
		boolean result = false;

		if(this.getNbFigure(Main.typeFig.PAIRE,'v', false)==4
				&& this.getNbPungKong('d')==2)	{
			result = true;			
		}
		return result;
	}
	/**
	 * Test le Mahjong special "Le souffle du dragon":
	 * 	- 3 pung de dragons 
	 *  - 1 vents de chaque série
	 *  - 1 vent quelconque
	 */
	public boolean test12SouffleDuDragon(){
		boolean result = false;

		if(this.getNbPungKong('d')==3
				&& this.getNbTuile(new Tuile(1,'v'))>0
				&& this.getNbTuile(new Tuile(2,'v'))>0
				&& this.getNbTuile(new Tuile(3,'v'))>0
				&& this.getNbTuile(new Tuile(4,'v'))>0
				)	{
			if(this.getNbTuile(new Tuile(1,'v')) == 2  
					|| this.getNbTuile(new Tuile(2,'v'))== 2
					|| this.getNbTuile(new Tuile(3,'v'))== 2
					|| this.getNbTuile(new Tuile(4,'v'))== 2){
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

		if(this.getNbPungKong(3,'b')==1
				&& this.getNbPungKong(3,'d')==1){
			int nbPungPair = this.getNbPungKong(2,'b') + this.getNbPungKong(4,'b') + this.getNbPungKong(6,'b') + this.getNbPungKong(8,'b');
			if(nbPungPair == 2){
				if(this.getNbFigure(Main.typeFig.PAIRE, 2, 'b', false)==1
						|| this.getNbFigure(Main.typeFig.PAIRE, 4, 'b', false)==1
						|| this.getNbFigure(Main.typeFig.PAIRE, 6, 'b', false)==1
						|| this.getNbFigure(Main.typeFig.PAIRE, 8, 'b', false)==1){
					result = true;
				}
			}
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La grande main verte":
	 * 	- 1 pung de n°2, n°4, n°6 bambou
	 *  - 1 pung de dragon vert
	 *  - 1 paire de n°1 bambou
	 */
	public boolean test14GrandeMainVerte(){
		boolean result = false;

		if(this.getNbPungKong(2,'b')==1
				&& this.getNbPungKong(4,'b')==1
				&& this.getNbPungKong(6,'b')==1
				&& this.getNbPungKong(3,'d')==1
				&& this.getNbFigure(Main.typeFig.PAIRE, 1, 'b', false)==1){
			result = true;
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La main verte et rouge":
	 * 	- 1 pung de n°1, n°5, n°7, n°9 bambou
	 *  - 1 dragon vert
	 *  - 1 dragon rouge
	 */
	public boolean test15MainVerteEtRouge(){
		boolean result = false;

		if(this.getNbPungKong(1,'b')==1
				&& this.getNbPungKong(5,'b')==1
				&& this.getNbPungKong(7,'b')==1
				&& this.getNbPungKong(9,'b')==1
				&& this.getNbFigure(Main.typeFig.SIMPLE, 3, 'd', false)==1
				&& this.getNbFigure(Main.typeFig.SIMPLE, 1, 'd', false)==1){
			result = true;
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La petite main rouge":
	 * 	- 1 pung de 3 caractère et 2 pung de nb pairs caractère
	 *  - 1 pung de dragon rouge
	 *  - 1 paire de nb pair caractère
	 */
	public boolean test16PetiteMainRouge(){
		boolean result = false;

		if(this.getNbPungKong(3,'c')==1
				&& this.getNbPungKong(1,'d')==1){
			int nbPungPair = this.getNbPungKong(2,'c') + this.getNbPungKong(4,'c') + this.getNbPungKong(6,'c') + this.getNbPungKong(8,'c');
			if(nbPungPair == 2){
				if(this.getNbFigure(Main.typeFig.PAIRE, 2, 'c', false)==1
						|| this.getNbFigure(Main.typeFig.PAIRE, 4, 'c', false)==1
						|| this.getNbFigure(Main.typeFig.PAIRE, 6, 'c', false)==1
						|| this.getNbFigure(Main.typeFig.PAIRE, 8, 'c', false)==1){
					result = true;
				}
			}
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La grande main rouge":
	 * 	- 1 pung de n°2, n°4, n°6 caractère
	 *  - 1 pung de dragon rouge
	 *  - 1 paire de n°1 caractère
	 */
	public boolean test17GrandeMainRouge(){
		boolean result = false;

		if(this.getNbPungKong(2,'c')==1
				&& this.getNbPungKong(4,'c')==1
				&& this.getNbPungKong(6,'c')==1
				&& this.getNbPungKong(1,'d')==1
				&& this.getNbFigure(Main.typeFig.PAIRE, 1, 'c', false)==1){
			result = true;
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La main rouge et blanche":
	 * 	- 1 pung de n°1, n°5, n°7, n°9 caractère
	 *  - 1 dragon rouge
	 *  - 1 dragon blanc
	 */
	public boolean test18MainRougeEtBlanche(){
		boolean result = false;

		if(this.getNbPungKong(1,'c')==1
				&& this.getNbPungKong(5,'c')==1
				&& this.getNbPungKong(7,'c')==1
				&& this.getNbPungKong(9,'c')==1
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

		if(this.getNbPungKong(3,'r')==1
				&& this.getNbPungKong(2,'d')==1){
			int nbPungPair = this.getNbPungKong(2,'r') + this.getNbPungKong(4,'r') + this.getNbPungKong(6,'r') + this.getNbPungKong(8,'r');
			if(nbPungPair == 2){
				if(this.getNbFigure(Main.typeFig.PAIRE, 2, 'r', false)==1
						|| this.getNbFigure(Main.typeFig.PAIRE, 4, 'r', false)==1
						|| this.getNbFigure(Main.typeFig.PAIRE, 6, 'r', false)==1
						|| this.getNbFigure(Main.typeFig.PAIRE, 8, 'r', false)==1){
					result = true;
				}
			}
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La grande main blanche":
	 * 	- 1 pung de n°2, n°4, n°6 rond
	 *  - 1 pung de dragon blanc
	 *  - 1 paire de n°1 rond
	 */
	public boolean test20GrandeMainBlanche(){
		boolean result = false;

		if(this.getNbPungKong(2,'r')==1
				&& this.getNbPungKong(4,'r')==1
				&& this.getNbPungKong(6,'r')==1
				&& this.getNbPungKong(2,'d')==1
				&& this.getNbFigure(Main.typeFig.PAIRE, 1, 'r', false)==1){
			result = true;
		}
		return result;
	}
	/**
	 * Test le Mahjong special "La main blanche et verte":
	 * 	- 1 pung de n°1, n°5, n°7, n°9 rond
	 *  - 1 dragon blanc
	 *  - 1 dragon vert
	 */
	public boolean test21MainBlancheEtVerte(){
		boolean result = false;

		if(this.getNbPungKong(1,'r')==1
				&& this.getNbPungKong(5,'r')==1
				&& this.getNbPungKong(7,'r')==1
				&& this.getNbPungKong(9,'r')==1
				&& this.getNbFigure(Main.typeFig.SIMPLE, 2, 'd', false)==1
				&& this.getNbFigure(Main.typeFig.SIMPLE, 3, 'd', false)==1){
			result = true;
		}
		return result;
	}
	/**
	 * Test le Mahjong special "Le mandarin":
	 * 	- 3 kong caché de vent
	 *  - 1 paire de vent
	 */
	public boolean test22Mandarin(){
		boolean result = false;

		if(this.getNbKongCaches('v')==3
				&& this.getNbFigure(Main.typeFig.PAIRE, 'v', false)==1){
			result = true;
		}
		return result;
	}
	/**
	 * Test le Mahjong special "Le mahjong impérial":
	 * 	- 3 kong caché de dragon
	 *  - 1 paire de vent du joueur
	 */
	public boolean test23MahjongImperial(int ventDuJoueur){
		boolean result = false;

		if(this.getNbKongCaches('d')==3
				&& this.getNbFigure(Main.typeFig.PAIRE, ventDuJoueur,'v', false)==1){
			result = true;
		}
		return result;
	}

}
