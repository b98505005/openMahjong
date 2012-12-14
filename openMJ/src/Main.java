/*
 * Main.java
 *
 * Created on 17/06/2007. Copyright Raphael (synthaxerrors@gmail.com
 *
 * Classe principale, elle s'occuppe des menus et de faire les liaisions entre les objets
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


import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;

public class Main extends JFrame {

	public static enum typeFig {SIMPLE, PAIRE, PUNG, KONG};

	public static String[][] nomIA  = new String[][]{
		{"Philippe Risoli", "Bruce Willis", "Goethe", "Mao Tse Tung"},
		{"Dieu", "Mon chat","Batman","Anonyme"},
		{"JC Van Damme", "George Bush","Naboleon","Kim Jong-Il"}
	};
	
	/*Scores des Mahjong sp嶰iaux (idx0 ne compte pas)
	 * 
	 */
	public static int[] SCORE_MAHJONG_SPE = {0, 800, 1000, 1300, 1300, 1300, 1300, 1600, 1700, 2000, 2500, 
		3000, 4000, 3200, 3600, 3400, 3200, 3600, 3400, 3200, 3600, 3400, 5000, 	};
	
	public static String[] NOM_MAHJONG_SPE = {"", "Les Pung Venteux", "Les paires venteuses",
		"La main de jade", "La main de corail", "La main d'opaline", "Les paires de shozum",
		"Les 13 lanternes merveilleuses", "Les 7 muses du po鋈e chinois", "Les 4 bonheurs domestiques",
		"Le triangle 彋ernel", "La temp皻e", "Le souffle du dragon", "La petite main verte", 
		"La grande main verte", "La main verte et rouge", "La petite main rouge", "La grande main rouge", 
		"La main rouge et blanche", "La petite main blanche", "La grande main blanche", 
		"La main blanche et verte", "Le mandarin", "Le mahjong imp廨ial"};

	/* CONSTANTES GLOBALES */
	private static final long serialVersionUID = 1L;
	public static final int X = 180 ;
	public static final int Y = 650;
	public static final int EST = 0;
	public static final int NORD = 1;
	public static final int OUEST = 2;
	public static final int SUD = 3;
	public static final int NB_IA = 4;
	public static final int NB_TUILES = 144;
	public static final boolean ASCENDANT = true;
	public static final boolean DESCENDANT = false;

	/* VARIABLES GLOBALES */
	public static boolean montreJeu = false ;
	public static boolean montreDiscard = true ;
	public static int ventDominant;

	/* ATTRIBUTS */
	public Container conteneur = null;
	Tuile pioche[] = new Tuile[NB_TUILES];
	int indexPioche;
	Joueur[] joueurs = new Joueur[4];
	JLabel labelReste = new JLabel();
	JLabel labelVentDom = new JLabel();
	Timer timer;
	int timerCpt = 0;
	int aQuiDeJouer = 0;
	Discard poubelle = new Discard();
	int[] highScore = new int[5];
	int bestScore = 0;
	int worstScore = 0;
	File file = new File("highScore.dat");


	/* Menu */
	JMenuBar menuBarre = new JMenuBar();
	JMenu fich = new JMenu("Fichier");
	JMenu info = new JMenu("?");
	JMenuItem nouv = new JMenuItem("Nouveau");
	JMenuItem option = new JMenuItem("Options");
	JMenuItem jHighScore = new JMenuItem("High Scores");
	JMenuItem quit = new JMenuItem("Quitter");
	JMenuItem regle = new JMenuItem("R銶les");
	JMenuItem aPropos = new JMenuItem("�propos...");

	/* Boutons*/
	JButton jouer = new JButton("Jouer");
	JButton declar = new JButton("D嶰larer");
	JButton prendre = new JButton("Prendre");

	/* log textbox */
	static JTextArea textBox = new JTextArea();
	JScrollPane areaScrollPane = new JScrollPane(textBox);



	/** Cr嶪 un nouvelle espace graphique */
	public Main() {
		/* Propri彋�de Base */
		this.setResizable(false);
		this.setSize(new Dimension (1024,768));
		this.setJMenuBar(menuBarre);
		this.setTitle("Mahjong");

		/* Container */
		conteneur = this.getContentPane();
		conteneur.setLayout(null);
		conteneur.setBackground(new Color(20,140,20));

		ActionListener actionTimer = new ActionListener (){
			// Methode appelee a chaque tic du timer
			public void actionPerformed (ActionEvent event)
			{
				Tuile discard = poubelle.getDiscard();
				// fin "naturelle" du timer
				if(timerCpt==0 && timer.isRunning()){
					timer.stop();
					// v廨ifie si un des ordi peut prendre la tuile du milieu pour faire un Mahjong
					for(int i=1; i<4; i++){
						if(joueurs[i].peutMahjong(discard)){
							joueurs[i].ajouteTuile(discard);	//r嶰up鋨e la tuile
							joueurs[i].declareFigure(discard, false);
							aQuiDeJouer = i;
							poubelle.takeDiscardedTile();
							finPartie(i);	
							return;
						}
					}
					for(int i=1; i<4; i++){ 	 //un ordi peut-il prendre la tuile pour faire une mainExpose ? 
						if(joueurs[i].peutPrendre(discard) && aQuiDeJouer!=i){  //ce joueur peut-il prendre ? 
							int cpt = 0;

							System.out.print(joueurs[i].nom+" prend la tuile\n");

							joueurs[i].ajouteTuile(discard);
							cpt = joueurs[i].getNbTuile(discard);
							joueurs[i].declareFigure(discard,false);
							poubelle.takeDiscardedTile();
							poubelle.declare(discard, cpt-1); //ajoute les tuiles �la poubelle

							joueurs[aQuiDeJouer].labelVent.setForeground(new Color(0,0,0));	//remet en noir le joueur pr嶰edent
							aQuiDeJouer = i;	//donne la mainCache au joueur qui prend
							
							poubelle.affiche(joueurs[0].mainCache);
							if(cpt==4){
								//displayText(joueurs[i].nom+"("+joueurs[i].numero+") d嶰lare un kong");
								partie(true);
							}
							else{
								//displayText(joueurs[i].nom+"("+joueurs[i].numero+") d嶰lare un pung");
								partie(false);
							}
							return;		 //retour car ce prog est fait n'importe comment... 
						}
					}
					// personne ne prendre la tuile jet嶪
					poubelle.flushDiscardedTile();
					jouer.setText("Jouer");
					aQuiDeJouer = (aQuiDeJouer+1)%4;	//joueur suivant
					partie(true);
				}
				else if(timerCpt>0){	 //le timer est toujours actif 

					jouer.setEnabled(true);
					jouer.setText("Passer "+(timerCpt));	// maj du label du bouton
					timerCpt--;
				}
			}
		};

		/* d嶰laration du timer d'1s*/
		timer = new Timer(1000,actionTimer);

		/* Menu */
		menuBarre.add(fich);
		nouv.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				nouveauJeu();
			}
		});
		fich.add(nouv);

		option.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				options();
			}
		});
		fich.add(option);

		jHighScore.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				showHighScore();
			}
		});
		fich.add(jHighScore);
		
		fich.addSeparator();
		quit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				System.exit(0);
			}
		});
		fich.add(quit);
		menuBarre.add(info);

		/* affiche le ficher regles.html */
		regle.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				Properties sys = System.getProperties();
				String os = sys.getProperty("os.name");
				Runtime r = Runtime.getRuntime();
				System.out.println(os);
				try
				{
					if (os.endsWith("NT")||os.endsWith("2000")||os.endsWith("XP")||os.endsWith("7"))
						r.exec("cmd /c start .\\regles.html");
					else
						r.exec("start .\\regles.html");
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				} 
			}
		});
		info.add(regle);

		aPropos.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				JOptionPane.showMessageDialog(null,
						"Open Mahjong\n"+
						"Ce programme est sous license GPL V3. Vous pouvez librement le redistribuer et/ou le modifier.\n"+
						"http://openmahjong.sourceforge.net"+
						"Raphael(synthaxerrors@gmail.com)","A propos ...",JOptionPane.INFORMATION_MESSAGE);
			}
		});
		info.add(aPropos);

		/* Labels */
		labelVentDom.setBounds(720, 20, 150, 10);
		conteneur.add(labelVentDom);

		labelReste.setBounds(720, 50, 150, 10);
		conteneur.add(labelReste);
		for(int i=0; i<14; i++){
			conteneur.add(poubelle.cptTuile[i]);
		}
		for(int i=(Main.NB_TUILES-1); i>=0; i--){
			conteneur.add(poubelle.oldTilesLabl[i]);
			conteneur.setComponentZOrder(poubelle.oldTilesLabl[i], Main.NB_TUILES-i);
		}
		conteneur.add(poubelle.discardedTileLabl);
		
		for(int i=0; i<4; i++){
			joueurs[i] = new Joueur(i);
		}
		/* initialisation des label de la mainCache et des combi des joueurs */
		for (int i=0; i<14; i++){
			joueurs[0].labelCache[i] = new JLabel(Tuile.donneFond());
			joueurs[1].labelCache[i] = new JLabel(Tuile.donneFond270());
			joueurs[2].labelCache[i] = new JLabel(Tuile.donneFond());
			joueurs[3].labelCache[i] = new JLabel(Tuile.donneFond90());

			joueurs[0].labelCache[i].setBounds(X+(i*37),Y,37,49);
			joueurs[1].labelCache[i].setBounds(X+750,Y-550+(i*37),49,37);
			joueurs[2].labelCache[i].setBounds(X+(i*37),Y-630,37,49);
			joueurs[3].labelCache[i].setBounds(X-150,Y-550+(i*37),49,37);

			conteneur.add(joueurs[0].labelCache[i]);
			conteneur.add(joueurs[1].labelCache[i]);
			conteneur.add(joueurs[2].labelCache[i]);
			conteneur.add(joueurs[3].labelCache[i]);
		}

		for (int i=0; i<14; i++){	// premi鋨e ligne de combi
			joueurs[0].labelExpose[i] = new JLabel();
			joueurs[1].labelExpose[i] = new JLabel();
			joueurs[2].labelExpose[i] = new JLabel();
			joueurs[3].labelExpose[i] = new JLabel();

			joueurs[0].labelExpose[i].setBounds(X+(i*37),Y-55,37,49);
			joueurs[1].labelExpose[i].setBounds(X+750-55,Y-550+(i*37),49,37);
			joueurs[2].labelExpose[i].setBounds(X+(i*37),Y-630+55,37,49);
			joueurs[3].labelExpose[i].setBounds(X-150+55,Y-550+(i*37),49,37);

			conteneur.add(joueurs[0].labelExpose[i]);            
			conteneur.add(joueurs[1].labelExpose[i]);
			conteneur.add(joueurs[2].labelExpose[i]);
			conteneur.add(joueurs[3].labelExpose[i]);
		}
		for (int i=14; i<24; i++){	// deuxi鋗e ligne
			joueurs[0].labelExpose[i] = new JLabel();
			joueurs[1].labelExpose[i] = new JLabel();
			joueurs[2].labelExpose[i] = new JLabel();
			joueurs[3].labelExpose[i] = new JLabel();

			joueurs[0].labelExpose[i].setBounds(X+((i-14)*37),Y-108,37,49);
			joueurs[1].labelExpose[i].setBounds(X+750-108,Y-550+((i-14)*37),49,37);
			joueurs[2].labelExpose[i].setBounds(X+((i-14)*37),Y-630+108,37,49);
			joueurs[3].labelExpose[i].setBounds(X-150+108,Y-550+((i-14)*37),49,37);

			conteneur.add(joueurs[0].labelExpose[i]);
			conteneur.add(joueurs[1].labelExpose[i]);
			conteneur.add(joueurs[2].labelExpose[i]);
			conteneur.add(joueurs[3].labelExpose[i]);
		}
		/* initialisation des label pour le vent des joueurs */
		joueurs[0].labelVent.setBounds(740, 650, 100, 49);
		joueurs[1].labelVent.setBounds(930, 20, 49, 70);
		joueurs[2].labelVent.setBounds(70, 20, 100, 49);
		joueurs[3].labelVent.setBounds(30, 640, 49, 70);
		joueurs[1].labelVent.setVerticalTextPosition(SwingConstants.TOP);
		joueurs[1].labelVent.setHorizontalTextPosition(SwingConstants.CENTER);
		joueurs[2].labelVent.setHorizontalTextPosition(SwingConstants.LEFT);
		joueurs[3].labelVent.setVerticalTextPosition(SwingConstants.BOTTOM);
		joueurs[3].labelVent.setHorizontalTextPosition(SwingConstants.CENTER);
		conteneur.add(joueurs[0].labelVent);
		conteneur.add(joueurs[1].labelVent);
		conteneur.add(joueurs[2].labelVent);
		conteneur.add(joueurs[3].labelVent);


		/* Boutons */
		jouer.setText("Jouer");
		jouer.setEnabled(false);
		jouer.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				jouer();
			}
		});
		jouer.setBounds(870,630, 100, 25);
		conteneur.add(jouer);

		declar.setText("D嶰larer");
		declar.setEnabled(false);
		declar.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				declarer();
			}
		});
		declar.setBounds(870,655, 100, 25);
		conteneur.add(declar);

		prendre.setText("Prendre");
		prendre.setEnabled(false);
		prendre.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				prendre();
			}
		});
		prendre.setBounds(870,680, 100, 25);
		conteneur.add(prendre);

		textBox.setEditable(false);
		areaScrollPane.setBounds(600, 420, 260, 150);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		conteneur.add(areaScrollPane);

		displayText("Welcome");

		/* Listener & Event */
		WindowAdapter win = new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		};
		MouseInputAdapter mou = new MouseInputAdapter(){
			public void mouseClicked(MouseEvent evt){
				mouse_clic(evt);
			}
		};
		/* Ajout des Listener */
		this.addWindowListener(win);
		this.addMouseListener(mou);
		
		initHighScore();
	}

	/** 
	 * Fonction principale, elle affiche l'espace graphique
	 */
	public static void main(String[] args) {
		new Main().setVisible(true);
	}

	/** 
	 * G鋨e les clics de la souris
	 */
	void mouse_clic(MouseEvent evt){
		int x = evt.getX();
		int y = evt.getY();
		int temp = joueurs[0].tuileSelect;

		if(aQuiDeJouer == 0){ //clique pris en compte uniquement si c'est au joueur de jouer
			for (int i=0; i<14; i++){
				//le clique est-il sur une tuile? 
				if ((x>(X+i*37))&&(x<(X+(i+1)*37))&&(y>(Y+50))&&(y<(Y+99))){
					if(joueurs[0].mainCache.estVide(i) == false) {	// test si la tuile i existe
						if(temp < 0){	// aucune tuile n'avait 彋�s幨嶰tion�avant 
							joueurs[0].labelCache[i].setBounds(X+37*i, Y-10, 37, 49);	//d嶰alage vers le haut
							joueurs[0].tuileSelect = i;									//m幦orisation de la tuile
						}
						else{	//une tuile avait d嶴�彋�s幨嶰tion嶪
							joueurs[0].labelCache[temp].setBounds(X+37*temp, Y, 37, 49);	//on remet la tuile d'avant en place

							if(joueurs[0].tuileSelect != i){	//la tuile s幨嶰tion嶪 n'est pas la m瘱e qu'avant
								joueurs[0].labelCache[i].setBounds(X+37*i, Y-10, 37, 49);	//d嶰alage vers le haut
								joueurs[0].tuileSelect = i;									//m幦orisation de la tuile
							}
							else{								//la m瘱e tuile est s幨嶰tion嶪
								joueurs[0].tuileSelect = -1;		//aucune tuile n'est s幨嶰tion嶪
							}
						}
					}
					break;
				}
			}
		}
	}

	/**
	 * G鋨e l'affichage et la mise en oeuvre des options de jeu
	 */
	void options(){
		Checkbox montreJeux = new Checkbox("Montrer tous les jeux");
		Checkbox montreDisc = new Checkbox("Montrer les tuiles jet嶪s");

		// on arr皻e le timer pendant le r嶲lage des options
		if(timerCpt>0){
			timer.stop();
		}

		if(montreJeu == true){
			montreJeux.setState(true);
		}
		else{
			montreJeux.setState(false);
		}
		if(montreDiscard == true){
			montreDisc.setState(true);
		}
		else{
			montreDisc.setState(false);
		}
		Object[] obj = {"OPTIONS:", montreJeux, montreDisc};
		JOptionPane.showMessageDialog(null,obj,"Options",JOptionPane.INFORMATION_MESSAGE);

		/* v廨ifie si l'option a chang嶪 */
		if(montreJeux.getState() != montreJeu){
			montreJeu = montreJeux.getState();
			for(int i=0; i<4; i++){
				joueurs[i].affiche();
			}
		}

		montreDiscard = montreDisc.getState();

		//on red幦arre le timer si necessaire
		if(timerCpt>0){
			timer.start();
		}

	}

	/**
	 * Cr嶪 un nouveau jeu (les scores sont remis �0)
	 */
	void nouveauJeu(){
		boolean temp;
		Random rand = new Random(); 
		JTextField jTF1;
		JComboBox jCB1, jCB2, jCB3;


		timer.stop();
		poubelle.init();

		ventDominant = rand.nextInt(4)+1;
		switch(ventDominant){
		case(EST+1):
			labelVentDom.setText("Vent Dominant: Est");
		break;
		case(NORD+1):
			labelVentDom.setText("Vent Dominant: Nord");
		break;
		case(OUEST+1):
			labelVentDom.setText("Vent Dominant: Ouest");
		break;
		case(SUD+1):
			labelVentDom.setText("Vent Dominant: Sud");
		break;
		default: 
			labelVentDom.setText("");
		break;
		}

		joueurs[0].nom = "Moi";
		jTF1 = new JTextField(joueurs[0].nom);

		jCB1 = new JComboBox(nomIA[0]);
		jCB2 = new JComboBox(nomIA[1]);
		jCB3 = new JComboBox(nomIA[2]);
		jCB1.setSelectedIndex(rand.nextInt(NB_IA));
		jCB2.setSelectedIndex(rand.nextInt(NB_IA));
		jCB3.setSelectedIndex(rand.nextInt(NB_IA));

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4,2));
		panel.add(new Label("Joueur1: "));
		panel.add(jTF1);

		panel.add(new Label("Joueur2: "));

		panel.add(jCB1);
		panel.add(new Label("Joueur3: "));

		panel.add(jCB2);
		panel.add(new Label("Joueur4: "));

		panel.add(jCB3);


		JOptionPane.showMessageDialog(null,panel,"Nom",JOptionPane.INFORMATION_MESSAGE);

		//maj des noms des joueurs
		if((jTF1.getText().compareTo(joueurs[0].nom)!=0)	&& (jTF1.getText().length()<20)	&& !jTF1.getText().isEmpty()){
			joueurs[0].nom = jTF1.getText();
		}

		//maj des type d'IA
		joueurs[1].nom = nomIA[0][jCB1.getSelectedIndex()];
		joueurs[2].nom = nomIA[1][jCB2.getSelectedIndex()];
		joueurs[3].nom = nomIA[2][jCB3.getSelectedIndex()];
		joueurs[1].typeIA = jCB1.getSelectedIndex();
		joueurs[2].typeIA = jCB2.getSelectedIndex();
		joueurs[3].typeIA = jCB3.getSelectedIndex();

		aQuiDeJouer = rand.nextInt(4);

		/* Texte, couleur correspondant au vent du joueur */
		joueurs[aQuiDeJouer].vent = EST;
		joueurs[aQuiDeJouer].labelVent.setText("EST(1)");
		joueurs[aQuiDeJouer].labelVent.setForeground(new Color(220,40,40));

		joueurs[(aQuiDeJouer+1)%4].vent = NORD;
		joueurs[(aQuiDeJouer+1)%4].labelVent.setText("NORD(2)");
		joueurs[(aQuiDeJouer+1)%4].labelVent.setForeground(new Color(0,0,0));

		joueurs[(aQuiDeJouer+2)%4].vent = OUEST;
		joueurs[(aQuiDeJouer+2)%4].labelVent.setText("OUEST(3)");
		joueurs[(aQuiDeJouer+2)%4].labelVent.setForeground(new Color(0,0,0));

		joueurs[(aQuiDeJouer+3)%4].vent = SUD;
		joueurs[(aQuiDeJouer+3)%4].labelVent.setText("SUD(4)");
		joueurs[(aQuiDeJouer+3)%4].labelVent.setForeground(new Color(0,0,0));

		/* met la bonne image et la bonne orientation des vents */
		switch(aQuiDeJouer){
		case 0:
			joueurs[aQuiDeJouer].labelVent.setIcon(new ImageIcon("images/1v.jpg"));
			joueurs[(aQuiDeJouer+1)%4].labelVent.setIcon(Main.rotationIcon(new ImageIcon("images/2v.jpg"),-90));
			joueurs[(aQuiDeJouer+2)%4].labelVent.setIcon(new ImageIcon("images/3v.jpg"));
			joueurs[(aQuiDeJouer+3)%4].labelVent.setIcon(Main.rotationIcon(new ImageIcon("images/4v.jpg"),90));
			break;
		case 1:
			joueurs[aQuiDeJouer].labelVent.setIcon(Main.rotationIcon(new ImageIcon("images/1v.jpg"),-90));
			joueurs[(aQuiDeJouer+1)%4].labelVent.setIcon(new ImageIcon("images/2v.jpg"));
			joueurs[(aQuiDeJouer+2)%4].labelVent.setIcon(Main.rotationIcon(new ImageIcon("images/3v.jpg"),90));
			joueurs[(aQuiDeJouer+3)%4].labelVent.setIcon(new ImageIcon("images/4v.jpg"));
			break;
		case 2:
			joueurs[aQuiDeJouer].labelVent.setIcon(new ImageIcon("images/1v.jpg"));
			joueurs[(aQuiDeJouer+1)%4].labelVent.setIcon(Main.rotationIcon(new ImageIcon("images/2v.jpg"),90));
			joueurs[(aQuiDeJouer+2)%4].labelVent.setIcon(new ImageIcon("images/3v.jpg"));
			joueurs[(aQuiDeJouer+3)%4].labelVent.setIcon(Main.rotationIcon(new ImageIcon("images/4v.jpg"),-90));
			break;
		case 3:
			joueurs[aQuiDeJouer].labelVent.setIcon(Main.rotationIcon(new ImageIcon("images/1v.jpg"),90));
			joueurs[(aQuiDeJouer+1)%4].labelVent.setIcon(new ImageIcon("images/2v.jpg"));
			joueurs[(aQuiDeJouer+2)%4].labelVent.setIcon(Main.rotationIcon(new ImageIcon("images/3v.jpg"),-90));
			joueurs[(aQuiDeJouer+3)%4].labelVent.setIcon(new ImageIcon("images/4v.jpg"));
			break;
		}

		/* Initialise */
		initPioche();
		initJoueurs();


		/* initialisation des jeux */
		for(int i=0; i<4; i++){
			do{
				temp = joueurs[i].declareHonneur();
				if(temp) piocheTuiles(joueurs[i]);
			}while(temp);
			joueurs[i].affiche();
		}
		if(joueurs[0].tuileSelect>=0){
			joueurs[0].labelCache[joueurs[0].tuileSelect].setBounds(X+37*joueurs[0].tuileSelect, Y, 37, 49);
			joueurs[0].tuileSelect = -1;
		}
		jouer.setText("Jouer");

		if(aQuiDeJouer == 0){ // c'est au joueur de jouer
			if(joueurs[0].aKong()>=0 || joueurs[0].aMahjong()){
				declar.setEnabled(true);	//le joueur peut d嶰larer une combi ou un mahjong
			}
			else{
				declar.setEnabled(false);
			}
			jouer.setEnabled(true);
			prendre.setEnabled(false);
		}
		else{	/* les ordinateurs jouent */
			declar.setEnabled(false);
			jouer.setEnabled(false);
			prendre.setEnabled(false);
			IAJoue(aQuiDeJouer);
		}
	}

	/**
	 * Cr嶪 une nouvelle partie (score non remis �0)
	 * @param estGagne : vrai si le vent d'est a gagn�(les vents ne tournent pas)
	 */
	void nouvellePartie(boolean estGagne){
		Random rand = new Random(); 

		textBox.setText("Nouvelle Partie\n");
		
		initPioche();
		poubelle.init();

		ventDominant = rand.nextInt(4)+1;
		switch(ventDominant){
		case(EST):
			labelVentDom.setText("Vent Dominant: Est");
		break;
		case(NORD):
			labelVentDom.setText("Vent Dominant: Nord");
		break;
		case(OUEST):
			labelVentDom.setText("Vent Dominant: Ouest");
		break;
		case(SUD):
			labelVentDom.setText("Vent Dominant: Sud");
		break;
		}
		displayText(labelVentDom.getText());
		//init de chaque joueur
		for(int j = 0; j<4;j++){
			joueurs[j].mainExpose = new Jeu(24);
			joueurs[j].mainCache = new Jeu(14);

			//r嶯nitialise les combinaisons
			for(int i=0; i<24; i++){
				joueurs[j].labelExpose[i].setIcon(null);
			}
			//fait tourner les vents si le vent d'est n'a pas gagner
			if(estGagne==false){	
				joueurs[j].vent = (joueurs[j].vent+3)%4;
			}
			//13 tuiles par joueur
			for(int i=0; i<13; i++){
				joueurs[j].ajouteTuile(pioche[indexPioche++]);
			}
			//une tuile de plus pour le joueur de l'est
			if(joueurs[j].vent==EST){
				joueurs[j].ajouteTuile(pioche[indexPioche++]);
				aQuiDeJouer = j;
			}
		}
		if(estGagne==false){
			displayText("Le vent tourne...");
		}
		//Texte, couleur correspondant au vent du joueur
		joueurs[aQuiDeJouer].labelVent.setText("EST(1)");
		joueurs[aQuiDeJouer].labelVent.setForeground(new Color(220,40,40));

		joueurs[(aQuiDeJouer+1)%4].labelVent.setText("NORD(2)");
		joueurs[(aQuiDeJouer+1)%4].labelVent.setForeground(new Color(0,0,0));

		joueurs[(aQuiDeJouer+2)%4].labelVent.setText("OUEST(3)");
		joueurs[(aQuiDeJouer+2)%4].labelVent.setForeground(new Color(0,0,0));

		joueurs[(aQuiDeJouer+3)%4].labelVent.setText("SUD(4)");
		joueurs[(aQuiDeJouer+3)%4].labelVent.setForeground(new Color(0,0,0));

		//met la bonne image et la bonne orientation des vents
		switch(aQuiDeJouer){
		case 0:
			joueurs[aQuiDeJouer].labelVent.setIcon(new ImageIcon("images/1v.jpg"));
			joueurs[(aQuiDeJouer+1)%4].labelVent.setIcon(Main.rotationIcon(new ImageIcon("images/2v.jpg"),-90));
			joueurs[(aQuiDeJouer+2)%4].labelVent.setIcon(new ImageIcon("images/3v.jpg"));
			joueurs[(aQuiDeJouer+3)%4].labelVent.setIcon(Main.rotationIcon(new ImageIcon("images/4v.jpg"),90));
			break;
		case 1:
			joueurs[aQuiDeJouer].labelVent.setIcon(Main.rotationIcon(new ImageIcon("images/1v.jpg"),-90));
			joueurs[(aQuiDeJouer+1)%4].labelVent.setIcon(new ImageIcon("images/2v.jpg"));
			joueurs[(aQuiDeJouer+2)%4].labelVent.setIcon(Main.rotationIcon(new ImageIcon("images/3v.jpg"),90));
			joueurs[(aQuiDeJouer+3)%4].labelVent.setIcon(new ImageIcon("images/4v.jpg"));
			break;
		case 2:
			joueurs[aQuiDeJouer].labelVent.setIcon(new ImageIcon("images/1v.jpg"));
			joueurs[(aQuiDeJouer+1)%4].labelVent.setIcon(Main.rotationIcon(new ImageIcon("images/2v.jpg"),90));
			joueurs[(aQuiDeJouer+2)%4].labelVent.setIcon(new ImageIcon("images/3v.jpg"));
			joueurs[(aQuiDeJouer+3)%4].labelVent.setIcon(Main.rotationIcon(new ImageIcon("images/4v.jpg"),-90));
			break;
		case 3:
			joueurs[aQuiDeJouer].labelVent.setIcon(Main.rotationIcon(new ImageIcon("images/1v.jpg"),90));
			joueurs[(aQuiDeJouer+1)%4].labelVent.setIcon(new ImageIcon("images/2v.jpg"));
			joueurs[(aQuiDeJouer+2)%4].labelVent.setIcon(Main.rotationIcon(new ImageIcon("images/3v.jpg"),-90));
			joueurs[(aQuiDeJouer+3)%4].labelVent.setIcon(new ImageIcon("images/4v.jpg"));
			break;
		}

		boolean temp = false;
		//chaque joueur d嶰lare les fleurs et les saisons
		for(int i=0; i<4; i++){
			do{
				temp = joueurs[i].declareHonneur();
				if(temp) piocheTuiles(joueurs[i]);
			}while(temp);
			joueurs[i].affiche();
		}
		poubelle.affiche(joueurs[0].mainCache);
		//remet les tuiles du joueur au bon endroit
		if(joueurs[0].tuileSelect>=0){
			joueurs[0].labelCache[joueurs[0].tuileSelect].setBounds(X+37*joueurs[0].tuileSelect, Y, 37, 49);
			joueurs[0].tuileSelect = -1;
		}
		jouer.setText("Jouer");
		if(aQuiDeJouer == 0){ //c'est au joueur joue
			if(joueurs[0].aKong()>=0 || joueurs[0].aMahjong()){
				declar.setEnabled(true);	//le joueur peut d嶰larer une combi ou un mahjong
			}
			else{
				declar.setEnabled(false);
			}
			jouer.setEnabled(true);
			prendre.setEnabled(false);
		}
		else{	// les ordinateurs jouent 
			declar.setEnabled(false);
			jouer.setEnabled(false);
			prendre.setEnabled(false);
			IAJoue(aQuiDeJouer);
		}
	}

	/**
	 * Initialise la pioche avec les 144 tuiles
	 */
	void initPioche(){
		int cpt = 0;
		//initialise les icones des tuiles
		for(int i=0; i<4; i++){
			for(int j=0; j<9;j++){
				pioche[cpt++] = new Tuile(j+1,'c');	//chiffres
				pioche[cpt++] = new Tuile(j+1,'b'); 	//bamboos
				pioche[cpt++] = new Tuile(j+1,'r');	//ronds
			}
			for(int j=0; j<3;j++){
				pioche[cpt++] = new Tuile(j+1,'d');	//dragons
			}
			for(int j=0; j<4;j++){
				pioche[cpt++] = new Tuile(j+1,'v');	//vents
			}
			pioche[cpt++] = new Tuile(i+1,'f');		//fleurs
			pioche[cpt++] = new Tuile(i+1,'s');		//saisons
		}

		//m幨ange de la pioche
		Random melange = new Random();    	
		Tuile temp = new Tuile();
		int x,y;

		//inverse 300 fois 2 tuiles al嶧toires
		for(int i=0; i<300; i++){
			x = melange.nextInt(NB_TUILES);
			y = melange.nextInt(NB_TUILES);
			temp = pioche[x];
			pioche[x] = pioche[y];
			pioche[y] = temp;
		}
		indexPioche=0;
		labelReste.setText("Tuiles restantes: "+(NB_TUILES-indexPioche));
	}

	/**
	 * Initialise les joueurs
	 */
	void initJoueurs(){    	
		//reinitialisation des joueurs
		for(int j=0; j<4; j++){
			joueurs[j].mainCache = new Jeu(14);
			joueurs[j].mainExpose = new Jeu(24);

			for(int i=0; i<14; i++){
				joueurs[j].labelCache[i].setIcon(null);
			}
			for(int i=0; i<24; i++){
				joueurs[j].labelExpose[i].setIcon(null);
			}
			joueurs[j].numero = j;
			joueurs[j].score = 0;
			joueurs[j].typeIA = 1;
		}
		//13 tuiles par joueur
		for(int i=0; i<13; i++){
			for(int j=0; j<4; j++){
				joueurs[j].ajouteTuile(pioche[indexPioche++]);
			}
		}
		//une tuile de plus pour le joueur de l'est
		for(int i=0; i<4; i++){
			if(joueurs[i].vent==EST){
				joueurs[i].ajouteTuile(pioche[indexPioche++]);
			}
		}
		poubelle.affiche(joueurs[0].mainCache);
	}


	/**
	 * Fait piocher 1 tuiles au joueur j
	 * Retourne la position de la tuile dans la mainCache
	 */
	int piocheTuiles(Joueur j){
		int t = -1;
		if(indexPioche<NB_TUILES){
			
			//Test mahjong speciaux
//			if(j.numero==0){
//				Tuile tui = new Tuile(1, 'r');
//				t = j.ajouteTuile(mmm[tzu++]);
//			}
//			else
			t = j.ajouteTuile(pioche[indexPioche++]);
		
			if(j.numero==0){
				displayText("Vous piochez le "+pioche[indexPioche-1].nom);
			}
			System.out.print(j.nom+" pioche le "+pioche[indexPioche-1].nom+"\n");
			labelReste.setText("Tuiles restantes: "+(NB_TUILES-indexPioche));
		}
		return t;
	}

	/**
	 * Le joueur a appuy�sur le bouton D嶰larer
	 * Permet de d憝larer des kong cach�ou un Mahjong 
	 */
	void declarer()
	{
		int reponse = -1, nb;
		boolean temp;

		if(joueurs[0].aMahjong()){
			reponse=JOptionPane.showConfirmDialog(null, "Vous avez un Mahjong!\nVoulez vous le d嶰larer?","MAHJONG!!!",  JOptionPane.YES_NO_OPTION);
			if(reponse == 0){	// le joueur d嶰lare un Mahjong
				finPartie(0);
				return;
			}
		}

		if(reponse!=0){
			nb = joueurs[0].aKong();
			if(nb>=0){
				reponse=JOptionPane.showConfirmDialog(null, "Vous avez un kong de "+joueurs[0].mainCache.figures[nb].nom()+".\nVoulez vous le d嶰larer?","D幨aration",  JOptionPane.YES_NO_OPTION);
				if(reponse == 0){	// d嶰laration du kong
					// ajoute toutes les tuiles correspondantes dans la poubelle
					poubelle.declare(joueurs[0].mainCache.figures[nb].tuile,4);

					// declare le kong comme 彋ant cach�					joueurs[0].declareFigure(joueurs[0].mainCache.figures[nb].tuile,true);

					poubelle.affiche(joueurs[0].mainCache);

					if(indexPioche<NB_TUILES){	// y a-t-il assez de tuiles?
						piocheTuiles(joueurs[0]);
						do{
							temp = joueurs[0].declareHonneur();
							if(temp) piocheTuiles(joueurs[0]);
						}while(temp);
					}
					if(indexPioche>=NB_TUILES){
						if(joueurs[0].aMahjong()){
							finPartie(0);
						}else{
							finPartie(-1);	// plus de tuile donc fin de la partie
						}
						return;
					}
				}
			}
		}
		joueurs[0].affiche();
		// v廨ifie si le joueur peut encore d嶰larer
		if(joueurs[0].aKong()>=0 || joueurs[0].aMahjong()){
			declar.setEnabled(true);
		}
		else{
			declar.setEnabled(false);
		}
	}

	/**
	 * Le joueur a appuy�sur le bouton jouer/passer
	 */
	void jouer(){
		Tuile discard = poubelle.getDiscard();
		if(timer.isRunning()){	// le timer tourne dc le joueur a demand�de passer au joueur suivant 
			timer.stop();
			jouer.setText("Jouer");
			jouer.setEnabled(true);
			prendre.setEnabled(false);

			// v廨ifie si les ordi peuvent prendre la tuile jet嶪 pour faire mahjong
			for(int i=1; i<4; i++){
				if(joueurs[i].peutMahjong(discard)){
					joueurs[i].ajouteTuile(discard);
					joueurs[i].declareFigure(discard, false);
					aQuiDeJouer = i;
					poubelle.takeDiscardedTile();
					finPartie(i);
					return;
				}
			}
			// v廨ifie si les ordi peuvent prendre la tuile jet嶪 pour faire une combi
			for(int i=1; i<4; i++){
				if(joueurs[i].peutPrendre(discard) && aQuiDeJouer!=i){ // ce joueur peut prendre ?
					int cpt = 0;

					System.out.print(joueurs[i].nom+"prend la tuile\n");

					joueurs[i].ajouteTuile(discard);
					cpt = joueurs[i].getNbTuile(discard);
					joueurs[i].declareFigure(discard, false);

					poubelle.declare(discard, cpt-1); //ajoute les tuiles �la poubelle
					poubelle.takeDiscardedTile();
					joueurs[aQuiDeJouer].labelVent.setForeground(new Color(0,0,0));	//remet en noir le joueur precedent
					aQuiDeJouer = i;
					if(cpt==4){// si le joueur d嶰lare un kong, il doit piocher
						partie(true);
					}
					else{
						partie(false);
					}
					return;		// retour car ce prog est fait n'importe comment...
				}
			}
			// personne ne peut prendre la tuile
			poubelle.flushDiscardedTile();
			aQuiDeJouer = (aQuiDeJouer+1)%4;	//joueur suivant
			partie(true);
		}
		else{ // le joueur joue 
			Tuile t = new Tuile();
			int i = joueurs[0].tuileSelect;

			if(joueurs[0].tuileSelect != -1){	//il faut avoir s幨嶰tionner une tuile pour jouer
				t = joueurs[0].retireTuile(i);
				joueurs[0].labelCache[i].setBounds(X+37*i, Y, 37, 49);
				joueurs[0].tuileSelect = -1;
				poubelle.discardTile(t);
				jouer.setEnabled(false);
				declar.setEnabled(false);
				prendre.setEnabled(false);

				//poubelle.add(t, 1);
				poubelle.affiche(joueurs[0].mainCache);

				displayText("Vous jettez le "+ t.nom);
				
				joueurs[0].affiche();

				timerCpt = 2;	//lance le timer pour 2s 
				timer.start();
			}
			else{
				// pas de tuile s幨嶰tionn嶪
			}
		}
	}

	/**
	 * Le joueur a appuy�sur le bouton Prendre 
	 */
	void prendre(){
		int cpt = 0;
		Tuile discard = poubelle.getDiscard();

		timer.stop();
		jouer.setText("Jouer");
		jouer.setEnabled(true);
		prendre.setEnabled(false);

		System.out.print("Vous prenez la tuile\n");
		// Si le jouer peut faire Mahjong avec la tuile
		if(joueurs[0].peutMahjong(discard)){
			joueurs[0].ajouteTuile(discard);
			joueurs[0].declareFigure(discard, false);
			poubelle.takeDiscardedTile();
			finPartie(0);
		}
		else{ // sinon c'est juste une combi

			joueurs[0].ajouteTuile(discard);
			cpt = joueurs[0].getNbTuile(discard);
			joueurs[0].declareFigure(discard, false);
			poubelle.takeDiscardedTile();
			poubelle.declare(discard, cpt-1); //ajoute les tuiles �la poubelle

			joueurs[aQuiDeJouer].labelVent.setForeground(new Color(0,0,0));	//remet en noir le joueur precedent
			aQuiDeJouer = 0;
			poubelle.affiche(joueurs[0].mainCache);
			if(cpt == 4){	// c t un kong don le joueur peut piocher
				partie(true);
			}
			else{
				partie(false);
			}
		}
	}

	/**
	 * Fonction qui fait jouer chaque joueur
	 * doitPiocher : TRUE si le joueur a le droit de piocher
	 */
	void partie(boolean doitPiocher){
		boolean temp;
		int temp2 = -1; //temp2 est la position de la derni鋨e tuile pioch嶪
		boolean declarKong;

		//pour debug: detecte incoherence du nb de tuile dans un jeu
		if(doitPiocher==true){
			if((joueurs[aQuiDeJouer].mainCache.getNbTuile()%3)!=1){
				Object[] obj = {"BUG: "+joueurs[aQuiDeJouer].nom+" a "+joueurs[aQuiDeJouer].mainCache.getNbTuile()+" tuiles cach嶪 (prb1).\nPr憝enir Raf :-D"};
				JOptionPane.showMessageDialog(null,obj,"Bug Report",JOptionPane.ERROR_MESSAGE);
			}
		}
		else{
			//le joueur n'a pas �piocher
			if((joueurs[aQuiDeJouer].mainCache.getNbTuile()%3)!=2){
				Object[] obj = {"BUG: "+joueurs[aQuiDeJouer].nom+" a "+joueurs[aQuiDeJouer].mainCache.getNbTuile()+" tuiles cach嶪 (prb2).\nPr憝enir Raf :-D"};
				JOptionPane.showMessageDialog(null,obj,"Bug Report",JOptionPane.ERROR_MESSAGE);
			}
		}


		joueurs[aQuiDeJouer].labelVent.setForeground(new Color(220,40,40));
		joueurs[(aQuiDeJouer+3)%4].labelVent.setForeground(new Color(0,0,0));

		if(doitPiocher == true){
			// le joueur pioche une tuile
			if(indexPioche<NB_TUILES){
				temp2 = piocheTuiles(joueurs[aQuiDeJouer]);
				
				// d嶰laration des fleurs et saisons et des nouveau kong
				do{
					temp = joueurs[aQuiDeJouer].declareHonneur();
					declarKong = joueurs[aQuiDeJouer].declareKong();
					
					if(temp) temp2 = piocheTuiles(joueurs[aQuiDeJouer]);
					if(declarKong) temp2 = piocheTuiles(joueurs[aQuiDeJouer]);
				}while(temp || declarKong);
			}
		}
		poubelle.affiche(joueurs[0].mainCache);

		if(indexPioche>=NB_TUILES){ // plus de tuile
			//check si la derni鋨e tuile �fait Mahjong
			if(joueurs[aQuiDeJouer].aMahjong()){
				finPartie(aQuiDeJouer);
			}
			else{
				finPartie(-1);
			}
			return;
		}
		else{
			// c'est au joueur de jouer
			if(aQuiDeJouer == 0){
				if(temp2 >=0){	// si le joueur a pioch�une tuile, on la sureleve
					joueurs[0].labelCache[temp2].setBounds(X+37*temp2, Y-10, 37, 49);	//d嶰alage vers le haut
					joueurs[0].tuileSelect = temp2;									//m幦orisation de la tuile
				}

				if(joueurs[0].aKong()>=0 || joueurs[0].aMahjong()){
					declar.setEnabled(true);
				}
				jouer.setText("Jouer");
				jouer.setEnabled(true);
				
				//pour debug: detecte incoherence du nb de tuile dans un jeu
				if((joueurs[aQuiDeJouer].mainCache.getNbTuile()%3)!=2){
					Object[] obj = {"BUG: "+joueurs[aQuiDeJouer].nom+" a "+joueurs[aQuiDeJouer].mainCache.getNbTuile()+" tuiles cach嶪 (prb3).\nPr憝enir Raf :-D"};
					JOptionPane.showMessageDialog(null,obj,"Bug Report",JOptionPane.ERROR_MESSAGE);
				}
				joueurs[aQuiDeJouer].affiche();	// rafraichissement de l'affichage
			}
			// les ordinateurs jouent
			else{	
				if(joueurs[aQuiDeJouer].aMahjong()){
					finPartie(aQuiDeJouer);
					return;
				}
				else{
					//d嶰laration des kong cach廥
					int nb = joueurs[aQuiDeJouer].aKong();
					while(nb>=0){
						System.out.print(joueurs[aQuiDeJouer].nom+" d嶰lare un kong de "+joueurs[aQuiDeJouer].mainCache.figures[nb].nom()+"\n");

						poubelle.declare(joueurs[aQuiDeJouer].mainCache.figures[nb].tuile,4);
						joueurs[aQuiDeJouer].declareFigure(joueurs[aQuiDeJouer].mainCache.figures[nb].tuile,true);
						poubelle.affiche(joueurs[0].mainCache);
						if(indexPioche<NB_TUILES){
							piocheTuiles(joueurs[aQuiDeJouer]);
							// d嶰laration des fleurs et saisons
							do{
								temp = joueurs[aQuiDeJouer].declareHonneur();
								if(temp) piocheTuiles(joueurs[aQuiDeJouer]);
							}while(temp);
						}
						if(indexPioche>=NB_TUILES){
							if(joueurs[aQuiDeJouer].aMahjong()){
								finPartie(aQuiDeJouer);
							}else{
								finPartie(-1);
							}
							return;
						}
						nb = joueurs[aQuiDeJouer].aKong();
					}
					jouer.setEnabled(false);
					prendre.setEnabled(false);
					IAJoue(aQuiDeJouer);
					
					//pour debug: detecte incoherence du nb de tuile dans un jeu
					if((joueurs[aQuiDeJouer].mainCache.getNbTuile()%3)!=1){
						Object[] obj = {"BUG: "+joueurs[aQuiDeJouer].nom+" a "+joueurs[aQuiDeJouer].mainCache.getNbTuile()+" tuiles cach嶪 (prb4).\nPr憝enir Raf :-D"};
						JOptionPane.showMessageDialog(null,obj,"Bug Report",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}

	/**
	 * G鋨e la fin de la partie
	 * gagnant : -1 si personne n'a gagn� num廨o du joueur gagnant sinon
	 */
	void finPartie(int gagnant){
		int reponse;

		timer.stop();
		poubelle.flushDiscardedTile();

		// affiche les jeux des ordinateurs
		boolean temp = montreJeu;
		montreJeu = true;
		for(int i=0; i<4; i++){
			joueurs[i].affiche();
		}
		montreJeu = temp;

		if(gagnant<0){ //pas de gagnant
			reponse=JOptionPane.showConfirmDialog(null, "La partie s'est finie sans mahjong.\n On Continue?","Fin de partie",  JOptionPane.YES_NO_OPTION);
			if(reponse == 0){
				nouvellePartie(false);
			}
			else{
				System.exit(0);
			}
		}
		else{	// on a un gagnant
			if(gagnant == 0){
				if(joueurs[0].aMahjongSpecial()>0){
					Object[] obj = {"Vous venez de r嶧liser un Mahjong Sp嶰ial!\n      ----\""+NOM_MAHJONG_SPE[joueurs[0].aMahjongSpecial()]+"\"----\n BIEN JOUER!"};
					JOptionPane.showMessageDialog(null,obj,"Fin de la partie",JOptionPane.INFORMATION_MESSAGE);					
				}
				else{
					Object[] obj = {"Bien jou� Vous avez gagn�la partie"};
					JOptionPane.showMessageDialog(null,obj,"Fin de la partie",JOptionPane.INFORMATION_MESSAGE);
				}
			}
			else{
				Object[] obj = {"Dommage, c'est "+joueurs[gagnant].nom+"(J"+(joueurs[gagnant].numero+1)+") qui a gagn�la partie"};
				JOptionPane.showMessageDialog(null,obj,"Fin de la partie",JOptionPane.INFORMATION_MESSAGE);
			}

			calculScore(gagnant);	//calcul des scores

			reponse=JOptionPane.showConfirmDialog(null, "Partie suivante?","Fin de partie",  JOptionPane.YES_NO_OPTION);
			if(reponse == 0){
				if(joueurs[gagnant].vent == EST){
					nouvellePartie(true);
				}
				else{
					nouvellePartie(false);
				}
			}
			else{
				System.exit(0);
			}
		}
	}

	/**
	 * Fait jouer les ordinateur
	 */
	void IAJoue(int j){
		Tuile t;
		int choix;

		choix = IAChoixTuile(joueurs[j]);

		t = joueurs[j].retireTuile(choix);
		poubelle.discardTile(t);
		poubelle.affiche(joueurs[0].mainCache);

		displayText(joueurs[aQuiDeJouer].nom+"(J"+(joueurs[aQuiDeJouer].numero+1)+") jette le "+ t.nom);

		joueurs[j].affiche();
		
		if((joueurs[0].peutPrendre(t))){  
			prendre.setEnabled(true);	 //si le joueur peut perndre la tuile, on active le bouton Prendre 
		}
		timerCpt = 10;
		timer.start();
	}

	/**
	 * Fonction qui choisi qu'elle tuile va jouer l'ordi en fct de l'IA
	 * @return le num廨o de la figure i choisie
	 */
	int IAChoixTuile(Joueur j){
		Random rand = new Random();
		int figChoisie = 0,cpt =0;
		Tuile t;
		Jeu jeuTemp = new Jeu(j.mainCache);

		switch(j.typeIA){
		case 0:
		default:
			do{ // on jete une tuile au hasard sauf si l'ordi poss鋄e plus d'1 exemplaire 
				figChoisie = rand.nextInt(14);
				if(cpt++>14 && j.mainCache.figures[figChoisie].estLibre() == false){
					break;
				}
				cpt++;
			}while(j.mainCache.figures[figChoisie].estLibre() == true 
					|| j.peutPrendre(j.mainCache.figures[figChoisie].tuile));
		break;

		case 1:
			for(int i=0; i<jeuTemp.tailleMax; i++){
				if(jeuTemp.figures[i].estLibre() == false){
					jeuTemp.figures[i].nbTuile = poubelle.nbJet(jeuTemp.figures[i].tuile);
				}
			}
			jeuTemp.triNumerique(DESCENDANT);	//tri du plus grand au plus petit

			if(jeuTemp.getFirstFig(3,DESCENDANT)>=0){		//s'il y a des tuiles jet嶪s plus de 3 fois
				t = new Tuile(jeuTemp.figures[rand.nextInt(jeuTemp.getFirstFig(3,DESCENDANT)+1)].tuile);	//on en prend une au hasard
				figChoisie = j.mainCache.getFigFromTuile(t);							//et on retrouve la figure qui y correspond dans la main

			}
			else{ // il n'y a que des tuiles qui ont 彋�j彋�une fois ou jamais
				do{ // on jete une tuile au hasard sauf si l'ordi poss鋄e plus d'1 exemplaire 
					figChoisie = rand.nextInt(14);
					if(cpt++>14 && j.mainCache.figures[figChoisie].estLibre() == false){
						break;
					}
					cpt++;
				}while(j.mainCache.figures[figChoisie].estLibre() == true 
						|| j.peutPrendre(j.mainCache.figures[figChoisie].tuile));
			}
			break;

		case 2:
			for(int i=0; i<jeuTemp.tailleMax; i++){
				if(jeuTemp.figures[i].estLibre() == false){
					jeuTemp.figures[i].nbTuile = poubelle.nbJet(jeuTemp.figures[i].tuile);
				}
			}
			jeuTemp.triNumerique(DESCENDANT);	//tri du plus grand au plus petit

			if(jeuTemp.getFirstFig(3,DESCENDANT)>=0){		//s'il y a des tuiles jet嶪s plus de 3 fois
				t = new Tuile(jeuTemp.figures[rand.nextInt(jeuTemp.getFirstFig(3,DESCENDANT)+1)].tuile);	//on en prend une au hasard
				figChoisie = j.mainCache.getFigFromTuile(t);							//et on retrouve la figure qui y correspond dans la main

			}
			else if(jeuTemp.getFirstFig(2,DESCENDANT)>=0){	//s'il y a des tuiles jet嶪s plus de 2 fois
				t = new Tuile(jeuTemp.figures[rand.nextInt(jeuTemp.getFirstFig(2,DESCENDANT)+1)].tuile);	//on en prend une au hasard
				figChoisie = j.mainCache.getFigFromTuile(t);							//on en prend une au hasard
			}
			else{ // il n'y a que des tuiles qui ont 彋�j彋�une fois ou jamais
				do{ // on jete une tuile au hasard sauf si l'ordi poss鋄e plus d'1 exemplaire 
					figChoisie = rand.nextInt(14);
					if(cpt++>14 && j.mainCache.figures[figChoisie].estLibre() == false){
						break;
					}
					cpt++;
				}while(j.mainCache.figures[figChoisie].estLibre() == true 
						|| j.peutPrendre(j.mainCache.figures[figChoisie].tuile));
			}
			break;

		case 3:
			Figure figTemp = new Figure();
			figTemp.nbTuile = 3;
			figTemp.type = Main.typeFig.PUNG;
			figTemp.estCache = false;

			for(int i=0; i<jeuTemp.tailleMax; i++){
				if(jeuTemp.figures[i].estLibre() == false){
					figTemp.tuile = jeuTemp.figures[i].tuile;
					jeuTemp.figures[i].nbTuile = (int) (figTemp.getValeur(true, j.vent) * Math.pow(2, figTemp.getMulti(j.vent)) * Math.pow(jeuTemp.figures[i].nbTuile,3))/4; 
				}
			}
			jeuTemp.triNumerique(ASCENDANT); //tri du plus petit au plus grand

			int index = jeuTemp.getFirstFig(0,ASCENDANT);	//index pour eviter les figures vides
			cpt=1;
			while(jeuTemp.getFirstFig(cpt,ASCENDANT)<=index ){
				cpt++;
			}
			t = new Tuile(jeuTemp.figures[rand.nextInt(jeuTemp.getFirstFig(cpt,ASCENDANT)-index)+index+1].tuile);	//on en prend une au hasard
			figChoisie = j.mainCache.getFigFromTuile(t);
			break;
		}
		return figChoisie;
	}

	/**
	 * Calcul et affiche les scores des joueurs
	 * gagnant: num廨o du joueur gagnant
	 */
	void calculScore(int gagnant){
		int[] valeurMain = new int[4];
		int[] bonus = new int[4];
		int[] multiMain = new int[4];
		int[] scoreInter = new int[4];
		int[] scoreFinal = new int[4];
		int mahjongSpe = joueurs[0].aMahjongSpecial();

		// calcul des scores des jeux (score intermediaire)
		for(int i =0; i<4; i++){
			// Score pour nahjong sp嶰iaux
			if(i==0 && gagnant==0 && mahjongSpe>0){
				valeurMain[i] = SCORE_MAHJONG_SPE[mahjongSpe];
				bonus[i]=0;
				multiMain[i] =0;
			}
			// Score pour mahjong normaux
			else{
				if(i == gagnant){
					valeurMain[i] = joueurs[i].valeurMains(true,joueurs[i].vent);
					bonus[i] = 20;
				}
				else{
					valeurMain[i] = joueurs[i].valeurMains(false,joueurs[i].vent);
					bonus[i] = 0;
				}
				multiMain[i] = joueurs[i].multiMains(joueurs[i].vent);
				multiMain[i] += joueurs[i].multiBonus(i==gagnant);
			}
			scoreInter[i] = (int)((valeurMain[i]+bonus[i])*Math.pow(2,multiMain[i]));
			scoreFinal[i]=0;
		}

		JPanel tabScore = new JPanel();
		tabScore.setLayout(new GridLayout(5, 5));    
		tabScore.add(new Label("",Label.CENTER)); 
		tabScore.add(new Label(joueurs[0].nom,Label.CENTER));
		tabScore.add(new Label(joueurs[1].nom,Label.CENTER));
		tabScore.add(new Label(joueurs[2].nom,Label.CENTER));
		tabScore.add(new Label(joueurs[3].nom,Label.CENTER));
		tabScore.add(new Label("Valeur Main (pts)"));
		tabScore.add(new Label(String.valueOf(valeurMain[0]),Label.CENTER));
		tabScore.add(new Label(String.valueOf(valeurMain[1]),Label.CENTER));
		tabScore.add(new Label(String.valueOf(valeurMain[2]),Label.CENTER));
		tabScore.add(new Label(String.valueOf(valeurMain[3]),Label.CENTER));
		tabScore.add(new Label("Bonus (pts)"));
		tabScore.add(new Label(String.valueOf(bonus[0]),Label.CENTER));
		tabScore.add(new Label(String.valueOf(bonus[1]),Label.CENTER));
		tabScore.add(new Label(String.valueOf(bonus[2]),Label.CENTER));
		tabScore.add(new Label(String.valueOf(bonus[3]),Label.CENTER));
		tabScore.add(new Label("Multiple (*2)"));
		tabScore.add(new Label(String.valueOf(multiMain[0]),Label.CENTER));
		tabScore.add(new Label(String.valueOf(multiMain[1]),Label.CENTER));
		tabScore.add(new Label(String.valueOf(multiMain[2]),Label.CENTER));
		tabScore.add(new Label(String.valueOf(multiMain[3]),Label.CENTER));
		tabScore.add(new Label("Total (pts)"));
		tabScore.add(new Label(String.valueOf(scoreInter[0]),Label.CENTER));
		tabScore.add(new Label(String.valueOf(scoreInter[1]),Label.CENTER));
		tabScore.add(new Label(String.valueOf(scoreInter[2]),Label.CENTER));
		tabScore.add(new Label(String.valueOf(scoreInter[3]),Label.CENTER));

		JOptionPane.showMessageDialog(null,tabScore,"Score de la partie",JOptionPane.INFORMATION_MESSAGE);


		// calcul des scores de la partie
		for(int i = 0;i<4;i++){
			for(int j = 0; j<4; j++){
				if(i!=j){	// ce ne sont pas les mm joueurs
					if(i == gagnant){		// je suis gagnant
						if(scoreInter[i]<scoreInter[j] && joueurs[i].vent==EST){	//l'autre a un score plus grand que moi et je suis EST
							scoreFinal[i]-= 4*(scoreInter[j]-scoreInter[i]);	// je lui donne 4* la diff de nos score
						}
						else{										// conditions normales
							if(joueurs[i].vent==EST || joueurs[j].vent==EST){	// si je suis EST ou que l'aute est EST
								scoreFinal[i]+= 2*scoreInter[i];					// je recoit 2* mon score
							}
							else{
								scoreFinal[i]+= scoreInter[i];						// une seule fois sinon
							}
						}
					}
					else{	// j'ai perdu
						if(j == gagnant){	// l'autre a gagn�							
							if(scoreInter[i]>scoreInter[j] && joueurs[j].vent==EST){	//j'ai un score plus grand et le gagnant est EST
								scoreFinal[i]+= 4*(scoreInter[i]-scoreInter[j]);	// je re蔞is 4* la diff de nos score
							}
							else{
								if(joueurs[i].vent==EST || joueurs[j].vent==EST){	// si je suis EST ou que l'aute est EST
									scoreFinal[i]-= 2*scoreInter[j];					// je donne 2* son score �l'autre
								}
								else{
									scoreFinal[i]-= scoreInter[j];						// une seule fois sinon
								}
							}
						}
						else{	// nous avons tout les 2 perdus
							if(joueurs[i].vent==EST){	// si je suis EST 
								scoreFinal[i]+= 2*scoreInter[i]-scoreInter[j];	// mon score compte double
							}
							else if(joueurs[j].vent==EST){ //l'autre est EST
								scoreFinal[i]+= scoreInter[i]- 2*scoreInter[j];	// son score compte double
							}
							else{
								scoreFinal[i]+= scoreInter[i]-scoreInter[j];	// sinon diff廨ence normale
							}
						}
					}
				}
				else{
					//ce sont les mm joueurs
				}
			}
		}
		if((scoreFinal[0]+scoreFinal[1]+scoreFinal[2]+scoreFinal[3]) !=0){
			Object[] obj = {"BUG: La somme des scores de la partie n'est pas nulle.\nPr憝enir Raf :-D"};
			JOptionPane.showMessageDialog(null,obj,"Bug Report",JOptionPane.ERROR_MESSAGE);
			
			calculScore(gagnant);
		}
		JPanel tabScore2 = new JPanel();
		tabScore2.setLayout(new GridLayout(5, 5));    
		tabScore2.add(new Label("",Label.CENTER)); 
		tabScore2.add(new Label(joueurs[0].nom,Label.CENTER));
		tabScore2.add(new Label(joueurs[1].nom,Label.CENTER));
		tabScore2.add(new Label(joueurs[2].nom,Label.CENTER));
		tabScore2.add(new Label(joueurs[3].nom,Label.CENTER));
		tabScore2.add(new Label("Score partie"));
		tabScore2.add(new Label(String.valueOf(scoreInter[0]),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(scoreInter[1]),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(scoreInter[2]),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(scoreInter[3]),Label.CENTER));
		tabScore2.add(new Label("Ancien score"));
		tabScore2.add(new Label(String.valueOf(joueurs[0].score),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(joueurs[1].score),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(joueurs[2].score),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(joueurs[3].score),Label.CENTER));
		tabScore2.add(new Label("Gain/Perte"));
		tabScore2.add(new Label(String.valueOf(scoreFinal[0]),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(scoreFinal[1]),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(scoreFinal[2]),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(scoreFinal[3]),Label.CENTER));

		// maj des scores
		for(int i=0; i<4; i++){
			joueurs[i].score += scoreFinal[i];
		}

		tabScore2.add(new Label("Nouveau Score"));
		tabScore2.add(new Label(String.valueOf(joueurs[0].score),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(joueurs[1].score),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(joueurs[2].score),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(joueurs[3].score),Label.CENTER));

		JOptionPane.showMessageDialog(null,tabScore2,"TOTAUX",JOptionPane.INFORMATION_MESSAGE);

		
		/* maj des High scores */
		if(gagnant == 0){
			int highScoreIdx = 5;
			boolean isBetterScore = false;
			
			for(int i=4; i>=0; i--){
				if(scoreFinal[0]>highScore[i]){
					highScoreIdx = i;
					isBetterScore = true;
				}
			}
			if(isBetterScore){
				for(int i= highScoreIdx; i<4;i++){
					highScore[i+1] = highScore[i];
				}
				highScore[highScoreIdx] = scoreFinal[0];
				
				String message = "Bien jou� Vous 皻es class�dans les high scores ("+scoreFinal[0]+" pts)";
				JOptionPane.showMessageDialog(null,message,"High score!",JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if(joueurs[0].score>bestScore){
			bestScore = joueurs[0].score;
		}
		if(joueurs[0].score<worstScore || worstScore==0){
			worstScore = joueurs[0].score;
		}
		saveHighScore();
		
	}
	
	void showHighScore(){
		JPanel tabScore = new JPanel();
		
		tabScore.setLayout(new GridLayout(6, 2));
		
		tabScore.add(new Label("Meilleur Mahjong")); 
		tabScore.add(new Label("Meilleur Score Final"));
		tabScore.add(new Label(""+highScore[0],Label.CENTER));
		tabScore.add(new Label(""+bestScore,Label.CENTER));
		tabScore.add(new Label(""+highScore[1],Label.CENTER));
		tabScore.add(new Label("",Label.CENTER));
		tabScore.add(new Label(""+highScore[2],Label.CENTER));
		tabScore.add(new Label("Plus Mauvais Score Final",Label.CENTER));
		tabScore.add(new Label(""+highScore[3],Label.CENTER));
		tabScore.add(new Label(""+worstScore,Label.CENTER));
		tabScore.add(new Label(""+highScore[4],Label.CENTER));
		tabScore.add(new Label("",Label.CENTER));
		

		JOptionPane.showMessageDialog(null,tabScore,"High Scores",JOptionPane.INFORMATION_MESSAGE);
	}
	
	void initHighScore(){
		BufferedReader reader = null;
		String line = null;
		
		Arrays.fill(highScore, 0);
		bestScore = 0;
		worstScore = 0;

		if(file.exists()){
			try {
				reader = new BufferedReader(new FileReader(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try{
				line = reader.readLine();
				if(line!=null && (line.trim().length() != 0)){
					StringTokenizer t = new StringTokenizer(line);
					if(t.countTokens() >= 7){
						for(int i=0; i<5; i++){
							highScore[i] = Integer.parseInt(t.nextToken());
						}
						bestScore = Integer.parseInt(t.nextToken());
						worstScore = Integer.parseInt(t.nextToken());
					}

				}
			}
			catch (IOException ioe) {/*rien*/}
		}
	}
	
	void saveHighScore(){
		BufferedWriter out = null;
		/* Cr嶪 le fichier si necessaire */
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		file.setWritable(true);
		
		try {
			out = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			for(int i=0; i<5; i++){
				out.write(highScore[i]+" ");
			}
			out.write(bestScore+" ");
			out.write(worstScore+" ");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void displayText(String s){
		textBox.append(s+"\n");
		textBox.setCaretPosition(textBox.getText().length());
		System.out.print(s+"\n");
	}


	/**
	 * Fait tourner l'image icon de deg degr鋊 et la retoune
	 */
	public static ImageIcon rotationIcon(ImageIcon icon, int deg) {

		int w = icon.getIconWidth();
		int h = icon.getIconHeight();
		int type = BufferedImage.TYPE_INT_RGB;
		double x = (h - w)/2.0;
		double y = (w - h)/2.0;

		BufferedImage image = new BufferedImage(h, w, type);
		Graphics2D g2 = image.createGraphics();
		AffineTransform at = AffineTransform.getTranslateInstance(x, y);
		at.rotate(Math.toRadians(deg), w/2.0, h/2.0);
		g2.drawImage(icon.getImage(), at, null);
		g2.dispose();

		return new ImageIcon(image);
	}

	public static BufferedImage convertToGrayscale(BufferedImage source) { 
		BufferedImageOp op = new ColorConvertOp(
				ColorSpace.getInstance(ColorSpace.CS_GRAY), null); 
		return op.filter(source, null);
	}
	

}
