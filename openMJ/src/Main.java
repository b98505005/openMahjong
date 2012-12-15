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

	public static enum typeFig {SIMPLE, PAIR, PONG, GUNG};

	public static String[][] nameIA  = new String[][]{
		{"高鴨穩乃", "新子憧", "松實玄", "松實宥"},
		{"宮永照", "大星淡","亦野誠子","弘世堇"},
		{"園城寺怜", "清水谷龍華","船久保浩子","愛宕雅枝"}
	};
	
	/*Scores des Mahjong speciaux (idx0 ne compte pas)
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
	public static final int EAST = 0;
	public static final int NORTH = 1;
	public static final int WEST = 2;
	public static final int SOUTH = 3;
	public static final int NB_IA = 4;
	public static final int NB_Tiles = 144;
	public static final boolean ASCENDANT = true;
	public static final boolean DESCENDANT = false;

	/* VARIABLES GLOBALES */
	public static boolean montreJeu = false ;
	public static boolean montreDiscard = true ;
	public static int windDominant;

	/* ATTRIBUTS */
	public Container container = null;
	Tile pick[] = new Tile[NB_Tiles];
	int indexPick;
	Player[] players = new Player[4];
	JLabel labelRest = new JLabel();
	JLabel labelWindDom = new JLabel();
	Timer timer;
	int timerCpt = 0;
	int nowPlayer = 0;
	Discard trash_can = new Discard();
	int[] highScore = new int[5];
	int bestScore = 0;
	int worstScore = 0;
	File file = new File("highScore.dat");


	/* Menu */
	JMenuBar menuBar = new JMenuBar();
	JMenu fich = new JMenu("File");
	JMenu info = new JMenu("info");
	JMenuItem new_game = new JMenuItem("New");
	JMenuItem option = new JMenuItem("Options");
	JMenuItem jHighScore = new JMenuItem("High Scores");
	JMenuItem quit = new JMenuItem("Quit");
	JMenuItem rule = new JMenuItem("Rules");
	JMenuItem about = new JMenuItem("about");

	/* Boutons*/
	JButton play = new JButton("Play");
	JButton declare = new JButton("Declare");
	JButton takeOthersTile = new JButton("Prendre");

	/* log textbox */
	static JTextArea textBox = new JTextArea();
	JScrollPane areaScrollPane = new JScrollPane(textBox);



	/** Cr嶪 un nouvelle espace graphique */
	public Main() {
		/* Propri彋�de Base */
		this.setResizable(false);
		this.setSize(new Dimension (1024,768));
		this.setJMenuBar(menuBar);
		this.setTitle("麻雀");

		/* Container */
		container = this.getContentPane();
		container.setLayout(null);
		container.setBackground(new Color(20,140,20));

		ActionListener actionTimer = new ActionListener (){
			// Methode appelee a chaque tic du timer
			public void actionPerformed (ActionEvent event)
			{
				Tile discard = trash_can.getDiscard();
				// fin "naturelle" du timer
				if(timerCpt==0 && timer.isRunning()){
					timer.stop();
					// v廨ifie si un des ordi peut prendre la Tile du milieu pour faire un Mahjong
					for(int i=1; i<4; i++){
						if(players[i].canMahjong(discard)){
							players[i].addsTile(discard);	//recup鋨e la Tile
							players[i].declareFigure(discard, false);
							nowPlayer = i;
							trash_can.takeDiscardedTile();
							finPart(i);	
							return;
						}
					}
					for(int i=1; i<4; i++){ 	 //un ordi peut-il prendre la Tile pour faire une mainExpose ? 
						if(players[i].canTake(discard) && nowPlayer!=i){  //ce joueur peut-il prendre ? 
							int cpt = 0;

							System.out.print(players[i].name+" prend la Tile\n");

							players[i].addsTile(discard);
							cpt = players[i].getNbTile(discard);
							players[i].declareFigure(discard,false);
							trash_can.takeDiscardedTile();
							trash_can.declare(discard, cpt-1); //ajoute les Tiles �la poubelle

							players[nowPlayer].labelWind.setForeground(new Color(0,0,0));	//remet en noir le joueur precedent
							nowPlayer = i;	//donne la mainCache au joueur qui prend
							
							trash_can.affiche(players[0].mainPlayer);
							if(cpt==4){
								//displayText(joueurs[i].nom+"("+joueurs[i].numero+") declare un kong");
								part(true);
							}
							else{
								//displayText(joueurs[i].nom+"("+joueurs[i].numero+") declare un pung");
								part(false);
							}
							return;		 //retour car ce prog est fait n'importe comment... 
						}
					}
					// personne ne prendre la Tile jet嶪
					trash_can.flushDiscardedTile();
					play.setText("Jouer");
					nowPlayer = (nowPlayer+1)%4;	//joueur suivant
					part(true);
				}
				else if(timerCpt>0){	 //le timer est toujours actif 

					play.setEnabled(true);
					play.setText("剩下 "+(timerCpt) +"秒決定");	// maj du label du bouton
					timerCpt--;
				}
			}
		};

		/* declaration du timer d'1s*/
		timer = new Timer(1000,actionTimer);

		/* Menu */
		menuBar.add(fich);
		new_game.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				newGame();
			}
		});
		fich.add(new_game);

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
		menuBar.add(info);

		/* affiche le ficher regles.html */
		rule.addActionListener(new ActionListener(){
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
		info.add(rule);

		about.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				JOptionPane.showMessageDialog(null,
						"Open Mahjong\n"+
						"The program is sous license GPL V3. Code modified by NTU csie AI course Team \n"+
						"http://openmahjong.sourceforge.net"+
						"Raphael(synthaxerrors@gmail.com)","A propos ..."
						,JOptionPane.INFORMATION_MESSAGE);
			}
		});
		info.add(about);

		/* Labels */
		labelWindDom.setBounds(720, 20, 150, 10);
		container.add(labelWindDom);

		labelRest.setBounds(720, 50, 150, 10);
		container.add(labelRest);
		for(int i=0; i<14; i++){
			container.add(trash_can.handTile[i]);
		}
		for(int i=(Main.NB_Tiles-1); i>=0; i--){
			container.add(trash_can.oldTilesLabl[i]);
			container.setComponentZOrder(trash_can.oldTilesLabl[i], Main.NB_Tiles-i);
		}
		container.add(trash_can.discardedTileLabl);
		
		for(int i=0; i<4; i++){
			players[i] = new Player(i);
		}
		/* initialisation des label de la mainCache et des combi des joueurs */
		for (int i=0; i<14; i++){
			players[0].labelCover[i] = new JLabel(Tile.giveBackground());
			players[1].labelCover[i] = new JLabel(Tile.giveBackground270());
			players[2].labelCover[i] = new JLabel(Tile.giveBackground());
			players[3].labelCover[i] = new JLabel(Tile.giveBackground90());

			players[0].labelCover[i].setBounds(X+(i*37),Y,37,49);
			players[1].labelCover[i].setBounds(X+750,Y-550+(i*37),49,37);
			players[2].labelCover[i].setBounds(X+(i*37),Y-630,37,49);
			players[3].labelCover[i].setBounds(X-150,Y-550+(i*37),49,37);

			container.add(players[0].labelCover[i]);
			container.add(players[1].labelCover[i]);
			container.add(players[2].labelCover[i]);
			container.add(players[3].labelCover[i]);
		}

		for (int i=0; i<14; i++){	// premi鋨e ligne de combi
			players[0].labelExpose[i] = new JLabel();
			players[1].labelExpose[i] = new JLabel();
			players[2].labelExpose[i] = new JLabel();
			players[3].labelExpose[i] = new JLabel();

			players[0].labelExpose[i].setBounds(X+(i*37),Y-55,37,49);
			players[1].labelExpose[i].setBounds(X+750-55,Y-550+(i*37),49,37);
			players[2].labelExpose[i].setBounds(X+(i*37),Y-630+55,37,49);
			players[3].labelExpose[i].setBounds(X-150+55,Y-550+(i*37),49,37);

			container.add(players[0].labelExpose[i]);            
			container.add(players[1].labelExpose[i]);
			container.add(players[2].labelExpose[i]);
			container.add(players[3].labelExpose[i]);
		}
		for (int i=14; i<24; i++){	// deuxi鋗e ligne
			players[0].labelExpose[i] = new JLabel();
			players[1].labelExpose[i] = new JLabel();
			players[2].labelExpose[i] = new JLabel();
			players[3].labelExpose[i] = new JLabel();

			players[0].labelExpose[i].setBounds(X+((i-14)*37),Y-108,37,49);
			players[1].labelExpose[i].setBounds(X+750-108,Y-550+((i-14)*37),49,37);
			players[2].labelExpose[i].setBounds(X+((i-14)*37),Y-630+108,37,49);
			players[3].labelExpose[i].setBounds(X-150+108,Y-550+((i-14)*37),49,37);

			container.add(players[0].labelExpose[i]);
			container.add(players[1].labelExpose[i]);
			container.add(players[2].labelExpose[i]);
			container.add(players[3].labelExpose[i]);
		}
		/* initialisation des label pour le vent des joueurs */
		players[0].labelWind.setBounds(740, 650, 100, 49);
		players[1].labelWind.setBounds(930, 20, 49, 70);
		players[2].labelWind.setBounds(70, 20, 100, 49);
		players[3].labelWind.setBounds(30, 640, 49, 70);
		players[1].labelWind.setVerticalTextPosition(SwingConstants.TOP);
		players[1].labelWind.setHorizontalTextPosition(SwingConstants.CENTER);
		players[2].labelWind.setHorizontalTextPosition(SwingConstants.LEFT);
		players[3].labelWind.setVerticalTextPosition(SwingConstants.BOTTOM);
		players[3].labelWind.setHorizontalTextPosition(SwingConstants.CENTER);
		container.add(players[0].labelWind);
		container.add(players[1].labelWind);
		container.add(players[2].labelWind);
		container.add(players[3].labelWind);


		/* Boutons */
		play.setText("Play");
		play.setEnabled(false);
		play.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				play();
			}
		});
		play.setBounds(870,630, 100, 25);
		container.add(play);

		declare.setText("Declarer");
		declare.setEnabled(false);
		declare.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				declarer();
			}
		});
		declare.setBounds(870,655, 100, 25);
		container.add(declare);

		takeOthersTile.setText("吃/碰");
		takeOthersTile.setEnabled(false);
		takeOthersTile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				takeDisgard();
			}
		});
		takeOthersTile.setBounds(870,680, 100, 25);
		container.add(takeOthersTile);

		textBox.setEditable(false);
		areaScrollPane.setBounds(600, 420, 260, 150);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		container.add(areaScrollPane);

		displayText("Welcome");

		/* Listener & Event */
		WindowAdapter win = new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		};
		MouseInputAdapter mou = new MouseInputAdapter(){
			public void mouseClicked(MouseEvent evt){
				mouse_click(evt);
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
	void mouse_click(MouseEvent event){
		int x = event.getX();
		int y = event.getY();
		int temp = players[0].TileSelect;

		if(nowPlayer == 0){ //clique pris en compte uniquement si c'est au joueur de jouer
			for (int i=0; i<14; i++){
				//le clique est-il sur une Tile? 
				if ((x>(X+i*37))&&(x<(X+(i+1)*37))&&(y>(Y+50))&&(y<(Y+99))){
					if(players[0].mainPlayer.isEmpty(i) == false) {	// test si la Tile i existe
						if(temp < 0){	// aucune Tile n'avait 彋�s幨ection�avant 
							players[0].labelCover[i].setBounds(X+37*i, Y-10, 37, 49);	//decalage vers le haut
							players[0].TileSelect = i;									//m幦orisation de la Tile
						}
						else{	//une Tile avait d嶴�彋�s幨ection嶪
							players[0].labelCover[temp].setBounds(X+37*temp, Y, 37, 49);	//on remet la Tile d'avant en place

							if(players[0].TileSelect != i){	//la Tile s幨ection嶪 n'est pas la m瘱e qu'avant
								players[0].labelCover[i].setBounds(X+37*i, Y-10, 37, 49);	//decalage vers le haut
								players[0].TileSelect = i;									//m幦orisation de la Tile
							}
							else{								//la m瘱e Tile est s幨ection嶪
								players[0].TileSelect = -1;		//aucune Tile n'est s幨ection嶪
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
		Checkbox montreDisc = new Checkbox("Montrer les Tiles jet嶪s");

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
				players[i].poster();
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
	void newGame(){
		boolean temp;
		Random rand = new Random(); 
		JTextField jTF1;
		JComboBox jCB1, jCB2, jCB3;


		timer.stop();
		trash_can.init();

		windDominant = rand.nextInt(4)+1;
		switch(windDominant){
		case(EAST+1):
			labelWindDom.setText("場風: 東");
		break;
		case(NORTH+1):
			labelWindDom.setText("場風: 北");
		break;
		case(WEST+1):
			labelWindDom.setText("場風: 西");
		break;
		case(SOUTH+1):
			labelWindDom.setText("場風: 南");
		break;
		default: 
			labelWindDom.setText("");
		break;
		}

		players[0].name = "我";
		jTF1 = new JTextField(players[0].name);

		jCB1 = new JComboBox(nameIA[0]);
		jCB2 = new JComboBox(nameIA[1]);
		jCB3 = new JComboBox(nameIA[2]);
		jCB1.setSelectedIndex(rand.nextInt(NB_IA));
		jCB2.setSelectedIndex(rand.nextInt(NB_IA));
		jCB3.setSelectedIndex(rand.nextInt(NB_IA));

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4,2));
		panel.add(new Label("Player1: "));
		panel.add(jTF1);

		panel.add(new Label("Player2: "));

		panel.add(jCB1);
		panel.add(new Label("Player3: "));

		panel.add(jCB2);
		panel.add(new Label("Player4: "));

		panel.add(jCB3);


		JOptionPane.showMessageDialog(null,panel,"Nom",JOptionPane.INFORMATION_MESSAGE);

		//maj des noms des joueurs
		if((jTF1.getText().compareTo(players[0].name)!=0)	&& (jTF1.getText().length()<20)	&& !jTF1.getText().isEmpty()){
			players[0].name = jTF1.getText();
		}

		//maj des type d'IA
		players[1].name = nameIA[0][jCB1.getSelectedIndex()];
		players[2].name = nameIA[1][jCB2.getSelectedIndex()];
		players[3].name = nameIA[2][jCB3.getSelectedIndex()];
		players[1].typeIA = jCB1.getSelectedIndex();
		players[2].typeIA = jCB2.getSelectedIndex();
		players[3].typeIA = jCB3.getSelectedIndex();

		nowPlayer = rand.nextInt(4);

		/* Texte, couleur correspondant au vent du joueur */
		players[nowPlayer].風 = EAST;
		players[nowPlayer].labelWind.setText("東(1)"+players[nowPlayer].name);
		players[nowPlayer].labelWind.setForeground(new Color(220,40,40));

		players[(nowPlayer+1)%4].風 = NORTH;
		players[(nowPlayer+1)%4].labelWind.setText("北(2)"+players[(nowPlayer+1)%4].name);
		players[(nowPlayer+1)%4].labelWind.setForeground(new Color(0,0,0));

		players[(nowPlayer+2)%4].風 = WEST;
		players[(nowPlayer+2)%4].labelWind.setText("西(3)"+players[(nowPlayer+2)%4].name);
		players[(nowPlayer+2)%4].labelWind.setForeground(new Color(0,0,0));

		players[(nowPlayer+3)%4].風 = SOUTH;
		players[(nowPlayer+3)%4].labelWind.setText("南(4)"+players[(nowPlayer+3)%4].name);
		players[(nowPlayer+3)%4].labelWind.setForeground(new Color(0,0,0));

		/* met la bonne image et la bonne orientation des vents */
		switch(nowPlayer){
		case 0:
			players[nowPlayer].labelWind.setIcon(new ImageIcon("images/1v.jpg"));
			players[(nowPlayer+1)%4].labelWind.setIcon(Main.rotationIcon(new ImageIcon("images/2v.jpg"),-90));
			players[(nowPlayer+2)%4].labelWind.setIcon(new ImageIcon("images/3v.jpg"));
			players[(nowPlayer+3)%4].labelWind.setIcon(Main.rotationIcon(new ImageIcon("images/4v.jpg"),90));
			break;
		case 1:
			players[nowPlayer].labelWind.setIcon(Main.rotationIcon(new ImageIcon("images/1v.jpg"),-90));
			players[(nowPlayer+1)%4].labelWind.setIcon(new ImageIcon("images/2v.jpg"));
			players[(nowPlayer+2)%4].labelWind.setIcon(Main.rotationIcon(new ImageIcon("images/3v.jpg"),90));
			players[(nowPlayer+3)%4].labelWind.setIcon(new ImageIcon("images/4v.jpg"));
			break;
		case 2:
			players[nowPlayer].labelWind.setIcon(new ImageIcon("images/1v.jpg"));
			players[(nowPlayer+1)%4].labelWind.setIcon(Main.rotationIcon(new ImageIcon("images/2v.jpg"),90));
			players[(nowPlayer+2)%4].labelWind.setIcon(new ImageIcon("images/3v.jpg"));
			players[(nowPlayer+3)%4].labelWind.setIcon(Main.rotationIcon(new ImageIcon("images/4v.jpg"),-90));
			break;
		case 3:
			players[nowPlayer].labelWind.setIcon(Main.rotationIcon(new ImageIcon("images/1v.jpg"),90));
			players[(nowPlayer+1)%4].labelWind.setIcon(new ImageIcon("images/2v.jpg"));
			players[(nowPlayer+2)%4].labelWind.setIcon(Main.rotationIcon(new ImageIcon("images/3v.jpg"),-90));
			players[(nowPlayer+3)%4].labelWind.setIcon(new ImageIcon("images/4v.jpg"));
			break;
		}

		/* Initialise */
		initPick();
		initPlayers();


		/* initialisation des jeux */
		for(int i=0; i<4; i++){
			do{
				temp = players[i].declareHonor();
				if(temp) pickTiles(players[i]);
			}while(temp);
			players[i].poster();
		}
		if(players[0].TileSelect>=0){
			players[0].labelCover[players[0].TileSelect].setBounds(X+37*players[0].TileSelect, Y, 37, 49);
			players[0].TileSelect = -1;
		}
		play.setText("Play");

		if(nowPlayer == 0){ // c'est au joueur de jouer
			if(players[0].槓()>=0 || players[0].aMahjong()){
				declare.setEnabled(true);	//le joueur peut declarer une combi ou un mahjong
			}
			else{
				declare.setEnabled(false);
			}
			play.setEnabled(true);
			takeOthersTile.setEnabled(false);
		}
		else{	/* les ordinateurs jouent */
			declare.setEnabled(false);
			play.setEnabled(false);
			takeOthersTile.setEnabled(false);
			IAJoue(nowPlayer);
		}
	}

	/**
	 * Cr嶪 une nouvelle partie (score non remis �0)
	 * @param isWin : vrai si le vent d'est a gagn�(les vents ne tournent pas)
	 */
	void nouvellePartie(boolean isWin){
		Random rand = new Random(); 

		textBox.setText("Nouvelle Partie\n");
		
		initPick();
		trash_can.init();

		windDominant = rand.nextInt(4)+1;
		switch(windDominant){
		case(EAST):
			labelWindDom.setText("場風: 東");
		break;
		case(NORTH):
			labelWindDom.setText("場風: 北");
		break;
		case(WEST):
			labelWindDom.setText("場風: 西");
		break;
		case(SOUTH):
			labelWindDom.setText("場風: 南");
		break;
		}
		displayText(labelWindDom.getText());
		//init de chaque joueur
		for(int j = 0; j<4;j++){
			players[j].mainExpose = new Game(24);
			players[j].mainPlayer = new Game(14);

			//r嶯nitialise les combinaisons
			for(int i=0; i<24; i++){
				players[j].labelExpose[i].setIcon(null);
			}
			//fait tourner les vents si le vent d'est n'a pas gagner
			if(isWin==false){	
				players[j].風 = (players[j].風+3)%4;
			}
			//13 Tiles par joueur
			for(int i=0; i<13; i++){
				players[j].addsTile(pick[indexPick++]);
			}
			//une Tile de plus pour le joueur de l'est
			if(players[j].風==EAST){
				players[j].addsTile(pick[indexPick++]);
				nowPlayer = j;
			}
		}
		if(isWin==false){
			displayText("大風吹起, 轉換風向 ...");
		}
		//Texte, couleur correspondant au vent du joueur
		players[nowPlayer].labelWind.setText("EAST(1)");
		players[nowPlayer].labelWind.setForeground(new Color(220,40,40));

		players[(nowPlayer+1)%4].labelWind.setText("NORTH(2)");
		players[(nowPlayer+1)%4].labelWind.setForeground(new Color(0,0,0));

		players[(nowPlayer+2)%4].labelWind.setText("WEST(3)");
		players[(nowPlayer+2)%4].labelWind.setForeground(new Color(0,0,0));

		players[(nowPlayer+3)%4].labelWind.setText("SOUTH(4)");
		players[(nowPlayer+3)%4].labelWind.setForeground(new Color(0,0,0));

		//met la bonne image et la bonne orientation des vents
		switch(nowPlayer){
		case 0:
			players[nowPlayer].labelWind.setIcon(new ImageIcon("images/1v.jpg"));
			players[(nowPlayer+1)%4].labelWind.setIcon(Main.rotationIcon(new ImageIcon("images/2v.jpg"),-90));
			players[(nowPlayer+2)%4].labelWind.setIcon(new ImageIcon("images/3v.jpg"));
			players[(nowPlayer+3)%4].labelWind.setIcon(Main.rotationIcon(new ImageIcon("images/4v.jpg"),90));
			break;
		case 1:
			players[nowPlayer].labelWind.setIcon(Main.rotationIcon(new ImageIcon("images/1v.jpg"),-90));
			players[(nowPlayer+1)%4].labelWind.setIcon(new ImageIcon("images/2v.jpg"));
			players[(nowPlayer+2)%4].labelWind.setIcon(Main.rotationIcon(new ImageIcon("images/3v.jpg"),90));
			players[(nowPlayer+3)%4].labelWind.setIcon(new ImageIcon("images/4v.jpg"));
			break;
		case 2:
			players[nowPlayer].labelWind.setIcon(new ImageIcon("images/1v.jpg"));
			players[(nowPlayer+1)%4].labelWind.setIcon(Main.rotationIcon(new ImageIcon("images/2v.jpg"),90));
			players[(nowPlayer+2)%4].labelWind.setIcon(new ImageIcon("images/3v.jpg"));
			players[(nowPlayer+3)%4].labelWind.setIcon(Main.rotationIcon(new ImageIcon("images/4v.jpg"),-90));
			break;
		case 3:
			players[nowPlayer].labelWind.setIcon(Main.rotationIcon(new ImageIcon("images/1v.jpg"),90));
			players[(nowPlayer+1)%4].labelWind.setIcon(new ImageIcon("images/2v.jpg"));
			players[(nowPlayer+2)%4].labelWind.setIcon(Main.rotationIcon(new ImageIcon("images/3v.jpg"),-90));
			players[(nowPlayer+3)%4].labelWind.setIcon(new ImageIcon("images/4v.jpg"));
			break;
		}

		boolean temp = false;
		//chaque joueur declare les fleurs et les saisons
		for(int i=0; i<4; i++){
			do{
				temp = players[i].declareHonor();
				if(temp) pickTiles(players[i]);
			}while(temp);
			players[i].poster();
		}
		trash_can.affiche(players[0].mainPlayer);
		//remet les Tiles du joueur au bon endroit
		if(players[0].TileSelect>=0){
			players[0].labelCover[players[0].TileSelect].setBounds(X+37*players[0].TileSelect, Y, 37, 49);
			players[0].TileSelect = -1;
		}
		play.setText("Play");
		if(nowPlayer == 0){ //c'est au joueur joue
			if(players[0].槓()>=0 || players[0].aMahjong()){
				declare.setEnabled(true);	//le joueur peut declarer une combi ou un mahjong
			}
			else{
				declare.setEnabled(false);
			}
			play.setEnabled(true);
			takeOthersTile.setEnabled(false);
		}
		else{	// the computers player 
			declare.setEnabled(false);
			play.setEnabled(false);
			takeOthersTile.setEnabled(false);
			IAJoue(nowPlayer);
		}
	}

	/**
	 * Initialise la pioche avec les 144 Tiles
	 */
	void initPick(){
		int cpt = 0;
		//initialise les icones des Tiles
		for(int i=0; i<4; i++){
			for(int j=0; j<9;j++){
				pick[cpt++] = new Tile(j+1,'c');	//chiffres
				pick[cpt++] = new Tile(j+1,'b'); 	//bamboos
				pick[cpt++] = new Tile(j+1,'r');	//ronds
			}
			for(int j=0; j<3;j++){
				pick[cpt++] = new Tile(j+1,'d');	//dragons
			}
			for(int j=0; j<4;j++){
				pick[cpt++] = new Tile(j+1,'v');	//vents
			}
			pick[cpt++] = new Tile(i+1,'f');		//fleurs
			pick[cpt++] = new Tile(i+1,'s');		//saisons
		}

		//m幨ange de la pioche
		Random melange = new Random();    	
		Tile temp = new Tile();
		int x,y;

		//inverse 300 fois 2 Tiles al嶧toires
		for(int i=0; i<300; i++){
			x = melange.nextInt(NB_Tiles);
			y = melange.nextInt(NB_Tiles);
			temp = pick[x];
			pick[x] = pick[y];
			pick[y] = temp;
		}
		indexPick=0;
		labelRest.setText("Tiles restantes: "+(NB_Tiles-indexPick));
	}

	/**
	 * Initialise les joueurs
	 */
	void initPlayers(){    	
		//reinitialisation des joueurs
		for(int j=0; j<4; j++){
			players[j].mainPlayer = new Game(14);
			players[j].mainExpose = new Game(24);

			for(int i=0; i<14; i++){
				players[j].labelCover[i].setIcon(null);
			}
			for(int i=0; i<24; i++){
				players[j].labelExpose[i].setIcon(null);
			}
			players[j].numero = j;
			players[j].score = 0;
			players[j].typeIA = 1;
		}
		//13 Tiles par joueur
		for(int i=0; i<13; i++){
			for(int j=0; j<4; j++){
				players[j].addsTile(pick[indexPick++]);
			}
		}
		//une Tile de plus pour le joueur de l'est
		for(int i=0; i<4; i++){
			if(players[i].風==EAST){
				players[i].addsTile(pick[indexPick++]);
			}
		}
		trash_can.affiche(players[0].mainPlayer);
	}


	/**
	 * Fait piocher 1 Tiles au joueur j
	 * Retourne la position de la Tile dans la mainCache
	 */
	int pickTiles(Player j){
		int t = -1;
		if(indexPick<NB_Tiles){
			
			//Test mahjong speciaux
//			if(j.numero==0){
//				Tile tui = new Tile(1, 'r');
//				t = j.ajouteTile(mmm[tzu++]);
//			}
//			else
			t = j.addsTile(pick[indexPick++]);
		
			if(j.numero==0){
				displayText("你抽到了 "+pick[indexPick-1].name);
			}
			System.out.print(j.name+" pick the "+pick[indexPick-1].name+"\n");
			labelRest.setText("Tiles remaining: "+(NB_Tiles-indexPick));
		}
		return t;
	}

	/**
	 * Le joueur a appuy�sur le bouton Declarer
	 * Permet de d憝larer des kong cach�ou un Mahjong 
	 */
	void declarer()
	{
		int reponse = -1, nb;
		boolean temp;

		if(players[0].aMahjong()){
			reponse=JOptionPane.showConfirmDialog(null, "Vous avez un Mahjong!\nVoulez vous le declarer?","MAHJONG!!!",  JOptionPane.YES_NO_OPTION);
			if(reponse == 0){	// le joueur declare un Mahjong
				finPart(0);
				return;
			}
		}

		if(reponse!=0){
			nb = players[0].槓();
			if(nb>=0){
				reponse=JOptionPane.showConfirmDialog(null, "Vous avez un kong de "+players[0].mainPlayer.figures[nb].name()+".\nVoulez vous le declarer?","D幨aration",  JOptionPane.YES_NO_OPTION);
				if(reponse == 0){	// declaration du kong
					// ajoute toutes les Tiles correspondantes dans la poubelle
					trash_can.declare(players[0].mainPlayer.figures[nb].tile,4);

					// declare le kong comme 彋ant cach�					joueurs[0].declareFigure(joueurs[0].mainCache.figures[nb].Tile,true);

					trash_can.affiche(players[0].mainPlayer);

					if(indexPick<NB_Tiles){	// y a-t-il assez de Tiles?
						pickTiles(players[0]);
						do{
							temp = players[0].declareHonor();
							if(temp) pickTiles(players[0]);
						}while(temp);
					}
					if(indexPick>=NB_Tiles){
						if(players[0].aMahjong()){
							finPart(0);
						}else{
							finPart(-1);	// plus de Tile donc fin de la partie
						}
						return;
					}
				}
			}
		}
		players[0].poster();
		// v廨ifie si le joueur peut encore declarer
		if(players[0].槓()>=0 || players[0].aMahjong()){
			declare.setEnabled(true);
		}
		else{
			declare.setEnabled(false);
		}
	}

	/**
	 * Le joueur a appuy�sur le bouton jouer/passer
	 */
	void play(){
		Tile discard = trash_can.getDiscard();
		if(timer.isRunning()){	// le timer tourne dc le joueur a demand�de passer au joueur suivant 
			timer.stop();
			play.setText("Jouer");
			play.setEnabled(true);
			takeOthersTile.setEnabled(false);

			// v廨ifie si les ordi peuvent prendre la Tile jet嶪 pour faire mahjong
			for(int i=1; i<4; i++){
				if(players[i].canMahjong(discard)){
					players[i].addsTile(discard);
					players[i].declareFigure(discard, false);
					nowPlayer = i;
					trash_can.takeDiscardedTile();
					finPart(i);
					return;
				}
			}
			// v廨ifie si les ordi peuvent prendre la Tile jet嶪 pour faire une combi
			for(int i=1; i<4; i++){
				if(players[i].canTake(discard) && nowPlayer!=i){ // ce joueur peut prendre ?
					int cpt = 0;

					System.out.print(players[i].name+"prend la Tile\n");

					players[i].addsTile(discard);
					cpt = players[i].getNbTile(discard);
					players[i].declareFigure(discard, false);

					trash_can.declare(discard, cpt-1); //ajoute les Tiles �la poubelle
					trash_can.takeDiscardedTile();
					players[nowPlayer].labelWind.setForeground(new Color(0,0,0));	//remet en noir le joueur precedent
					nowPlayer = i;
					if(cpt==4){// si le joueur declare un kong, il doit piocher
						part(true);
					}
					else{
						part(false);
					}
					return;		// retour car ce prog est fait n'importe comment...
				}
			}
			// personne ne peut prendre la Tile
			trash_can.flushDiscardedTile();
			nowPlayer = (nowPlayer+1)%4;	//joueur suivant
			part(true);
		}
		else{ // le joueur joue 
			Tile t = new Tile();
			int i = players[0].TileSelect;

			if(players[0].TileSelect != -1){	//il faut avoir s幨ectionner une Tile pour jouer
				t = players[0].retireTile(i);
				players[0].labelCover[i].setBounds(X+37*i, Y, 37, 49);
				players[0].TileSelect = -1;
				trash_can.discardTile(t);
				play.setEnabled(false);
				declare.setEnabled(false);
				takeOthersTile.setEnabled(false);

				//poubelle.add(t, 1);
				trash_can.affiche(players[0].mainPlayer);

				displayText("你丟出了 "+ t.name);
				
				players[0].poster();

				timerCpt = 2;	//lance le timer pour 2s 
				timer.start();
			}
			else{
				// pas de Tile s幨ectionn嶪
			}
		}
	}

	/**
	 * Le joueur a appuy�sur le bouton Prendre 
	 */
	void takeDisgard(){
		int cpt = 0;
		Tile discard = trash_can.getDiscard();

		timer.stop();
		play.setText("Jouer");
		play.setEnabled(true);
		takeOthersTile.setEnabled(false);

		System.out.print("Vous prenez la Tile\n");
		// Si le jouer peut faire Mahjong avec la Tile
		if(players[0].canMahjong(discard)){
			players[0].addsTile(discard);
			players[0].declareFigure(discard, false);
			trash_can.takeDiscardedTile();
			finPart(0);
		}
		else{ // sinon c'est juste une combi

			players[0].addsTile(discard);
			cpt = players[0].getNbTile(discard);
			players[0].declareFigure(discard, false);
			trash_can.takeDiscardedTile();
			trash_can.declare(discard, cpt-1); //ajoute les Tiles �la poubelle

			players[nowPlayer].labelWind.setForeground(new Color(0,0,0));	//remet en noir le joueur precedent
			nowPlayer = 0;
			trash_can.affiche(players[0].mainPlayer);
			if(cpt == 4){	// c t un kong don le joueur peut piocher
				part(true);
			}
			else{
				part(false);
			}
		}
	}

	/**
	 * Fonction qui fait jouer chaque joueur
	 * doitPiocher : TRUE si le joueur a le droit de piocher
	 */
	void part(boolean mustDraw){
		boolean temp;
		int temp2 = -1; //temp2 est la position de la derni鋨e Tile pioch嶪
		boolean declarKong;

		//pour debug: detecte incoherence du nb de Tile dans un jeu
		if(mustDraw==true){
			if((players[nowPlayer].mainPlayer.getNbTile()%3)!=1){
				Object[] obj = {"BUG: "+players[nowPlayer].name+" a "+players[nowPlayer].mainPlayer.getNbTile()+" Tiles cach嶪 (prb1).\nPr憝enir Raf :-D"};
				JOptionPane.showMessageDialog(null,obj,"Bug Report",JOptionPane.ERROR_MESSAGE);
			}
		}
		else{
			//le joueur n'a pas �piocher
			if((players[nowPlayer].mainPlayer.getNbTile()%3)!=2){
				Object[] obj = {"BUG: "+players[nowPlayer].name+" a "+players[nowPlayer].mainPlayer.getNbTile()+" Tiles cach嶪 (prb2).\nPr憝enir Raf :-D"};
				JOptionPane.showMessageDialog(null,obj,"Bug Report",JOptionPane.ERROR_MESSAGE);
			}
		}


		players[nowPlayer].labelWind.setForeground(new Color(220,40,40));
		players[(nowPlayer+3)%4].labelWind.setForeground(new Color(0,0,0));

		if(mustDraw == true){
			// le joueur pioche une Tile
			if(indexPick<NB_Tiles){
				temp2 = pickTiles(players[nowPlayer]);
				
				// declaration des fleurs et saisons et des nouveau kong
				do{
					temp = players[nowPlayer].declareHonor();
					declarKong = players[nowPlayer].declareKong();
					
					if(temp) temp2 = pickTiles(players[nowPlayer]);
					if(declarKong) temp2 = pickTiles(players[nowPlayer]);
				}while(temp || declarKong);
			}
		}
		trash_can.affiche(players[0].mainPlayer);

		if(indexPick>=NB_Tiles){ // plus de Tile
			//check si la derni鋨e Tile �fait Mahjong
			if(players[nowPlayer].aMahjong()){
				finPart(nowPlayer);
			}
			else{
				finPart(-1);
			}
			return;
		}
		else{
			// c'est au joueur de jouer
			if(nowPlayer == 0){
				if(temp2 >=0){	// si le joueur a pioch�une Tile, on la sureleve
					players[0].labelCover[temp2].setBounds(X+37*temp2, Y-10, 37, 49);	//decalage vers le haut
					players[0].TileSelect = temp2;									//m幦orisation de la Tile
				}

				if(players[0].槓()>=0 || players[0].aMahjong()){
					declare.setEnabled(true);
				}
				play.setText("Jouer");
				play.setEnabled(true);
				
				//pour debug: detecte incoherence du nb de Tile dans un jeu
				if((players[nowPlayer].mainPlayer.getNbTile()%3)!=2){
					Object[] obj = {"BUG: "+players[nowPlayer].name+" a "+players[nowPlayer].mainPlayer.getNbTile()+" Tiles cach嶪 (prb3).\nPr憝enir Raf :-D"};
					JOptionPane.showMessageDialog(null,obj,"Bug Report",JOptionPane.ERROR_MESSAGE);
				}
				players[nowPlayer].poster();	// rafraichissement de l'affichage
			}
			// les ordinateurs jouent
			else{	
				if(players[nowPlayer].aMahjong()){
					finPart(nowPlayer);
					return;
				}
				else{
					//declaration des kong cach廥
					int nb = players[nowPlayer].槓();
					while(nb>=0){
						System.out.print(players[nowPlayer].name+" 決定槓  "+players[nowPlayer].mainPlayer.figures[nb].name()+"\n");

						trash_can.declare(players[nowPlayer].mainPlayer.figures[nb].tile,4);
						players[nowPlayer].declareFigure(players[nowPlayer].mainPlayer.figures[nb].tile,true);
						trash_can.affiche(players[0].mainPlayer);
						if(indexPick<NB_Tiles){
							pickTiles(players[nowPlayer]);
							// declaration des fleurs et saisons
							do{
								temp = players[nowPlayer].declareHonor();
								if(temp) pickTiles(players[nowPlayer]);
							}while(temp);
						}
						if(indexPick>=NB_Tiles){
							if(players[nowPlayer].aMahjong()){
								finPart(nowPlayer);
							}else{
								finPart(-1);
							}
							return;
						}
						nb = players[nowPlayer].槓();
					}
					play.setEnabled(false);
					takeOthersTile.setEnabled(false);
					IAJoue(nowPlayer);
					
					//pour debug: detecte incoherence du nb de Tile dans un jeu
					if((players[nowPlayer].mainPlayer.getNbTile()%3)!=1){
						Object[] obj = {"BUG: "+players[nowPlayer].name+" a "+players[nowPlayer].mainPlayer.getNbTile()+" Tiles cach嶪 (prb4).\nPr憝enir Raf :-D"};
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
	void finPart(int winner){
		int reponse;

		timer.stop();
		trash_can.flushDiscardedTile();

		// affiche les jeux des ordinateurs
		boolean temp = montreJeu;
		montreJeu = true;
		for(int i=0; i<4; i++){
			players[i].poster();
		}
		montreJeu = temp;

		if(winner<0){ //pas de gagnant
			reponse=JOptionPane.showConfirmDialog(null, "La partie s'est finie sans mahjong.\n On Continue?","Fin the turn",  JOptionPane.YES_NO_OPTION);
			if(reponse == 0){
				nouvellePartie(false);
			}
			else{
				System.exit(0);
			}
		}
		else{	// on a un gagnant
			if(winner == 0){
				if(players[0].aMahjongSpecial()>0){
					Object[] obj = {"Vous venez de r嶧liser un Mahjong Special!\n      ----\""+NOM_MAHJONG_SPE[players[0].aMahjongSpecial()]+"\"----\n BIEN JOUER!"};
					JOptionPane.showMessageDialog(null,obj,"Fin de la partie",JOptionPane.INFORMATION_MESSAGE);					
				}
				else{
					Object[] obj = {"Bien jou� Vous avez gagn�la partie"};
					JOptionPane.showMessageDialog(null,obj,"Fin de la partie",JOptionPane.INFORMATION_MESSAGE);
				}
			}
			else{
				Object[] obj = {"Dommage, c'est "+players[winner].name+"(J"+(players[winner].numero+1)+") qui a gagn�la partie"};
				JOptionPane.showMessageDialog(null,obj,"Fin de la partie",JOptionPane.INFORMATION_MESSAGE);
			}

			calculScore(winner);	//calcul des scores

			reponse=JOptionPane.showConfirmDialog(null, "Partie suivante?","Fin de partie",  JOptionPane.YES_NO_OPTION);
			if(reponse == 0){
				if(players[winner].風 == EAST){
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
		Tile t;
		int choix;

		choix = IAChoixTile(players[j]);

		t = players[j].retireTile(choix);
		trash_can.discardTile(t);
		trash_can.affiche(players[0].mainPlayer);

		displayText(players[nowPlayer].name+"(J"+(players[nowPlayer].numero+1)+") 丟出了 "+ t.name);

		players[j].poster();
		
		if((players[0].canTake(t))){  
			takeOthersTile.setEnabled(true);	 //si le joueur peut perndre la Tile, on active le bouton Prendre 
		}
		timerCpt = 10;
		timer.start();
	}

	/**
	 * Fonction qui choisi qu'elle Tile va jouer l'ordi en fct de l'IA
	 * @return le num廨o de la figure i choisie
	 */
	int IAChoixTile(Player j){
		Random rand = new Random();
		int figChoice = 0,cpt =0;
		Tile t;
		Game jeuTemp = new Game(j.mainPlayer);

		switch(j.typeIA){
		case 0:
		default:
			do{ // on jete une Tile au hasard sauf si l'ordi poss鋄e plus d'1 exemplaire 
				figChoice = rand.nextInt(14);
				if(cpt++>14 && j.mainPlayer.figures[figChoice].isFree() == false){
					break;
				}
				cpt++;
			}while(j.mainPlayer.figures[figChoice].isFree() == true 
					|| j.canTake(j.mainPlayer.figures[figChoice].tile));
		break;

		case 1:
			for(int i=0; i<jeuTemp.sizeMax; i++){
				if(jeuTemp.figures[i].isFree() == false){
					jeuTemp.figures[i].nbTile = trash_can.nbJet(jeuTemp.figures[i].tile);
				}
			}
			jeuTemp.triDigit(DESCENDANT);	//tri du plus grand au plus petit

			if(jeuTemp.getFirstFig(3,DESCENDANT)>=0){		//s'il y a des Tiles jet嶪s plus de 3 fois
				t = new Tile(jeuTemp.figures[rand.nextInt(jeuTemp.getFirstFig(3,DESCENDANT)+1)].tile);	//on en prend une au hasard
				figChoice = j.mainPlayer.getFigFromTile(t);							//et on retrouve la figure qui y correspond dans la main

			}
			else{ // il n'y a que des Tiles qui ont 彋�j彋�une fois ou jamais
				do{ // on jete une Tile au hasard sauf si l'ordi poss鋄e plus d'1 exemplaire 
					figChoice = rand.nextInt(14);
					if(cpt++>14 && j.mainPlayer.figures[figChoice].isFree() == false){
						break;
					}
					cpt++;
				}while(j.mainPlayer.figures[figChoice].isFree() == true 
						|| j.canTake(j.mainPlayer.figures[figChoice].tile));
			}
			break;

		case 2:
			for(int i=0; i<jeuTemp.sizeMax; i++){
				if(jeuTemp.figures[i].isFree() == false){
					jeuTemp.figures[i].nbTile = trash_can.nbJet(jeuTemp.figures[i].tile);
				}
			}
			jeuTemp.triDigit(DESCENDANT);	//tri du plus grand au plus petit

			if(jeuTemp.getFirstFig(3,DESCENDANT)>=0){		//s'il y a des Tiles jet嶪s plus de 3 fois
				t = new Tile(jeuTemp.figures[rand.nextInt(jeuTemp.getFirstFig(3,DESCENDANT)+1)].tile);	//on en prend une au hasard
				figChoice = j.mainPlayer.getFigFromTile(t);							//et on retrouve la figure qui y correspond dans la main

			}
			else if(jeuTemp.getFirstFig(2,DESCENDANT)>=0){	//s'il y a des Tiles jet嶪s plus de 2 fois
				t = new Tile(jeuTemp.figures[rand.nextInt(jeuTemp.getFirstFig(2,DESCENDANT)+1)].tile);	//on en prend une au hasard
				figChoice = j.mainPlayer.getFigFromTile(t);							//on en prend une au hasard
			}
			else{ // il n'y a que des Tiles qui ont 彋�j彋�une fois ou jamais
				do{ // on jete une Tile au hasard sauf si l'ordi poss鋄e plus d'1 exemplaire 
					figChoice = rand.nextInt(14);
					if(cpt++>14 && j.mainPlayer.figures[figChoice].isFree() == false){
						break;
					}
					cpt++;
				}while(j.mainPlayer.figures[figChoice].isFree() == true 
						|| j.canTake(j.mainPlayer.figures[figChoice].tile));
			}
			break;

		case 3:
			Figure figTemp = new Figure();
			figTemp.nbTile = 3;
			figTemp.type = Main.typeFig.PONG;
			figTemp.isCover = false;

			for(int i=0; i<jeuTemp.sizeMax; i++){
				if(jeuTemp.figures[i].isFree() == false){
					figTemp.tile = jeuTemp.figures[i].tile;
					jeuTemp.figures[i].nbTile = (int) (figTemp.getValue(true, j.風) * Math.pow(2, figTemp.getMulti(j.風)) * Math.pow(jeuTemp.figures[i].nbTile,3))/4; 
				}
			}
			jeuTemp.triDigit(ASCENDANT); //tri du plus petit au plus grand

			int index = jeuTemp.getFirstFig(0,ASCENDANT);	//index pour eviter les figures vides
			cpt=1;
			while(jeuTemp.getFirstFig(cpt,ASCENDANT)<=index ){
				cpt++;
			}
			t = new Tile(jeuTemp.figures[rand.nextInt(jeuTemp.getFirstFig(cpt,ASCENDANT)-index)+index+1].tile);	//on en prend une au hasard
			figChoice = j.mainPlayer.getFigFromTile(t);
			break;
		}
		return figChoice;
	}

	/**
	 * Calcul et affiche les scores des joueurs
	 * gagnant: num廨o du joueur gagnant
	 */
	void calculScore(int winner){
		int[] valeurMain = new int[4];
		int[] bonus = new int[4];
		int[] multiMain = new int[4];
		int[] scoreInter = new int[4];
		int[] scoreFinal = new int[4];
		int mahjongSpe = players[0].aMahjongSpecial();

		// calcul des scores des jeux (score intermediaire)
		for(int i =0; i<4; i++){
			// Score pour nahjong speciaux
			if(i==0 && winner==0 && mahjongSpe>0){
				valeurMain[i] = SCORE_MAHJONG_SPE[mahjongSpe];
				bonus[i]=0;
				multiMain[i] =0;
			}
			// Score pour mahjong normaux
			else{
				if(i == winner){
					valeurMain[i] = players[i].valeurMains(true,players[i].風);
					bonus[i] = 20;
				}
				else{
					valeurMain[i] = players[i].valeurMains(false,players[i].風);
					bonus[i] = 0;
				}
				multiMain[i] = players[i].multiMains(players[i].風);
				multiMain[i] += players[i].multiBonus(i==winner);
			}
			scoreInter[i] = (int)((valeurMain[i]+bonus[i])*Math.pow(2,multiMain[i]));
			scoreFinal[i]=0;
		}

		JPanel tabScore = new JPanel();
		tabScore.setLayout(new GridLayout(5, 5));    
		tabScore.add(new Label("",Label.CENTER)); 
		tabScore.add(new Label(players[0].name,Label.CENTER));
		tabScore.add(new Label(players[1].name,Label.CENTER));
		tabScore.add(new Label(players[2].name,Label.CENTER));
		tabScore.add(new Label(players[3].name,Label.CENTER));
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
					if(i == winner){		// je suis gagnant
						if(scoreInter[i]<scoreInter[j] && players[i].風==EAST){	//l'autre a un score plus grand que moi et je suis EST
							scoreFinal[i]-= 4*(scoreInter[j]-scoreInter[i]);	// je lui donne 4* la diff de nos score
						}
						else{										// conditions normales
							if(players[i].風==EAST || players[j].風==EAST){	// si je suis EST ou que l'aute est EST
								scoreFinal[i]+= 2*scoreInter[i];					// je recoit 2* mon score
							}
							else{
								scoreFinal[i]+= scoreInter[i];						// une seule fois sinon
							}
						}
					}
					else{	// j'ai perdu
						if(j == winner){	// l'autre a gagn�							
							if(scoreInter[i]>scoreInter[j] && players[j].風==EAST){	//j'ai un score plus grand et le gagnant est EST
								scoreFinal[i]+= 4*(scoreInter[i]-scoreInter[j]);	// je re蔞is 4* la diff de nos score
							}
							else{
								if(players[i].風==EAST || players[j].風==EAST){	// si je suis EST ou que l'aute est EST
									scoreFinal[i]-= 2*scoreInter[j];					// je donne 2* son score �l'autre
								}
								else{
									scoreFinal[i]-= scoreInter[j];						// une seule fois sinon
								}
							}
						}
						else{	// nous avons tout les 2 perdus
							if(players[i].風==EAST){	// si je suis EST 
								scoreFinal[i]+= 2*scoreInter[i]-scoreInter[j];	// mon score compte double
							}
							else if(players[j].風==EAST){ //l'autre est EST
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
			
			calculScore(winner);
		}
		JPanel tabScore2 = new JPanel();
		tabScore2.setLayout(new GridLayout(5, 5));    
		tabScore2.add(new Label("",Label.CENTER)); 
		tabScore2.add(new Label(players[0].name,Label.CENTER));
		tabScore2.add(new Label(players[1].name,Label.CENTER));
		tabScore2.add(new Label(players[2].name,Label.CENTER));
		tabScore2.add(new Label(players[3].name,Label.CENTER));
		tabScore2.add(new Label("Score partie"));
		tabScore2.add(new Label(String.valueOf(scoreInter[0]),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(scoreInter[1]),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(scoreInter[2]),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(scoreInter[3]),Label.CENTER));
		tabScore2.add(new Label("Former score"));
		tabScore2.add(new Label(String.valueOf(players[0].score),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(players[1].score),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(players[2].score),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(players[3].score),Label.CENTER));
		tabScore2.add(new Label("Gain/Loss"));
		tabScore2.add(new Label(String.valueOf(scoreFinal[0]),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(scoreFinal[1]),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(scoreFinal[2]),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(scoreFinal[3]),Label.CENTER));

		// maj des scores
		for(int i=0; i<4; i++){
			players[i].score += scoreFinal[i];
		}

		tabScore2.add(new Label("Nouveau Score"));
		tabScore2.add(new Label(String.valueOf(players[0].score),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(players[1].score),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(players[2].score),Label.CENTER));
		tabScore2.add(new Label(String.valueOf(players[3].score),Label.CENTER));

		JOptionPane.showMessageDialog(null,tabScore2,"TOTAUX",JOptionPane.INFORMATION_MESSAGE);

		
		/* maj des High scores */
		if(winner == 0){
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

		if(players[0].score>bestScore){
			bestScore = players[0].score;
		}
		if(players[0].score<worstScore || worstScore==0){
			worstScore = players[0].score;
		}
		saveHighScore();
		
	}
	
	void showHighScore(){
		JPanel tabScore = new JPanel();
		
		tabScore.setLayout(new GridLayout(6, 2));
		
		tabScore.add(new Label("Best Mahjong")); 
		tabScore.add(new Label("Best Score Final"));
		tabScore.add(new Label(""+highScore[0],Label.CENTER));
		tabScore.add(new Label(""+bestScore,Label.CENTER));
		tabScore.add(new Label(""+highScore[1],Label.CENTER));
		tabScore.add(new Label("",Label.CENTER));
		tabScore.add(new Label(""+highScore[2],Label.CENTER));
		tabScore.add(new Label("Plus bad Score Final",Label.CENTER));
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
