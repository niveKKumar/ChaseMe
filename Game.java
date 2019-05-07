import com.sun.istack.internal.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class Game {

    private GamePanel gamePanel;
    public JTextArea levelAnzeige;
    
    public int level;
    public MapBase[] maps;
    public Character mover;
    public Runner[] enemy;
    public PathFinder pathFinder;
    public DisplayAnalytics[] analytics;
    public Tile start;
    public Tile target;
    public int click = 0;
    private int currentClick;
    private int currentSteps;
    private int dialog;
    private boolean active = false;
    private GridBagConstraints gbc = new GridBagConstraints();
    private KeyManager keyListener;


    public Game(GamePanel gp, KeyManager pKeyListener) {
        gamePanel = gp;
        keyListener = pKeyListener;
        levelAnzeige = new JTextArea("levelAnzeige");
        levelAnzeige.setFocusable(false);
        GUI.addToDebugPane(levelAnzeige);
        createLobby();
    }

    //    public JPanel createLevelMenu() {
//        JPanel levelPane;
//        levelPane = new JPanel(new GridBagLayout());
//        levelPane.setFocusable(false);
//        levelPane.setBorder(BorderFactory.createLineBorder(Color.blue, 5));
//
//        gbc.insets = new Insets(5, 0, 5, 0);
//        gbc.fill = GridBagConstraints.BOTH;
//        levelPane.setBackground(null);
//        int j = 2;
//        MenuButton[] buttons = new MenuButton[5];
//        for (int i = 0; i < buttons.length; i++) {
//            buttons[i] = new MenuButton(i);
//            buttons[i].setFocusable(false);
//            buttons[i].addActionListener(this);
//            MenuUI.addObject(gbc, buttons[i], levelPane, 2, j++, 2, 2, true);
//        }
//
//        gbc.fill = GridBagConstraints.NONE;
//        gbc.insets = new Insets(0, 0, 10, 10);
//        gbc.anchor = GridBagConstraints.LAST_LINE_END;
//        JButton info = new JButton("Info (Platzhalter");
//        MenuUI.addObject(gbc, info, levelPane, gbc.gridx + 1, gbc.gridy + 2, 0.5f, 0.5f, false);
//
//        return levelPane;
//    }
    public MenuUI.MenuTab createLevelMenu() {
        MenuUI.MenuTab menuTab = new MenuUI.MenuTab("Level", new String[]{"1", "2", "3", "4", "5"});
        return menuTab;
    }
    public void createLevelObjects(int mapAmount, int enemyAmount) {
        maps = new Map[mapAmount];
        mover = new Character(gamePanel, 0, 0, maps);
        SpriteSheet playersheet = new SpriteSheet("Content/Graphics/player/playersheet.png", 4, 3);
        enemy = new Runner[enemyAmount];
        for (int i = 0; i < enemy.length; i++) {
            enemy[i] = new Runner(gamePanel, 0, 0, playersheet, maps);
        }
        pathFinder = new PathFinder(maps, mover, gamePanel);
        dialog = 0;

        analytics = new DisplayAnalytics[mapAmount];

        active = true;
    }

    public void createLobby() {
        createLevelObjects(1, 0/*,null*/);
        //Einlesung der Maps:
        maps[0] = new Map(gamePanel, "Content/Maps/menu.txt", "Border", new Point(0, 0));
        gamePanel.setCamera(maps[0].mapSizeX, maps[0].getMapSizeY(), maps[0].getChapterOffset());
        // Intialisierung der Spieler:
        mover.setLocation(100, 100);
        System.out.println("Lobby loaded...");

        for (int i = 0; i < maps.length; i++) {
            analytics[i] = new DisplayAnalytics(gamePanel, maps[i], pathFinder);
        }

        active = true;
        setLevel(999);
    }

    ////LEVEL 0 - Testumgebung (Editor) ////////////////////////////save//////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    public void createlevel0(MapBase[] pMaps, @Nullable Point moverCords) {
        // TODO: 14.04.2019 TileSet Path in Map File !!
        // TODO: 14.04.2019 EnemyAmount auch lesbar ! (Vllt Game Config) -> Editor soll nur Map testen können
        //                                                                   (vllt bei Spielupdate die Fähigkeit Game mit Spieler zu erstellen :) )
        // TODO: 14.04.2019 Abfrage von bestimmten Informationen (Mover Spawn Punkt) -> wird einfach abgefragt = ansonsten zu viel Aufwand
        createLevelObjects(pMaps.length, 0/*,moverCords*/);
        maps = pMaps;
        gamePanel.setCamera(maps[0].getMapSizeX(), maps[0].getMapSizeY(), maps[0].getChapterOffset());
        // Intialisierung der Spieler:

        for (int i = 0; i < maps.length; i++) {
            analytics[i] = new DisplayAnalytics(gamePanel, maps[i], pathFinder);
        }

        setLevel(0);

    }

    public void level0GameMechanic() {
        // FIXME: 04.05.2019 Kann nichts weil, nix setzbar ist !
    }
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////


    //<editor-fold desc="LEVEL 1">
    ////LEVEL 1 - Intro  /////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    public void createlevel1() {
        createLevelObjects(2, 2);
        //Laden der Maps:
        maps[0] = new Map(gamePanel, "Content/Maps/0 Base Beach.txt", "null", new Point(0, 0));
        maps[1] = new Map(gamePanel, "Content/Maps/0 Base Items Full.txt", "null", new Point(0, 0));
//        maps[2] = new Map(gamePanel, "Content/Maps/0B Items.txt", "Item", new Point(10, 0));
        gamePanel.setCamera(maps[0].getMapSizeX(), maps[0].getMapSizeY(), maps[0].getChapterOffset());
        // Intialisierung der Spieler:
        mover.setLocation((10 * Tile.TILEWIDTH), 0 * Tile.TILEHEIGHT);
        mover.setSpeed(5);
        gamePanel.getCamera().centerOnObject(mover);
        enemy[0].setLocation((13) * 64, (3) * 64);
        enemy[1].setLocation((15) * 64, (1) * 64);

        for (int i = 0; i < maps.length; i++) {
            analytics[i] = new DisplayAnalytics(gamePanel, maps[i], pathFinder);
        }

        setLevel(1);
    }

    public void level1GameMechanic() {
        /**
         * Spielmechanik
         */
        level1Dialog();
    }

    public void level1Dialog() {
        if (dialog == 1) {
            mover.saySomething("Wo bin ich?" + "\n" + "(Klicken für weiteren Dialog)", true, 100, null);
            mover.setSpeaking(true);
            currentClick = click;
            dialog = 2;
        }
//        System.out.println("Click in Dialog 1 "+ click +"-"+ currentClick+"="+ (click - currentClick));
        if (dialog == 2 && click - currentClick >= 1) {
            mover.saySomething("W-A-S-D drücken um sich zu bewegen", true, 100, null);
            mover.setSpeaking(false);
            currentSteps = mover.getSteps();
            dialog = 3;
        }
        if (dialog == 3 && mover.getSteps() - currentSteps >= 150) {
            System.out.println("Gut du kannst laufen!");
                dialog = 4;
        }
        if (dialog == 4) {
            mover.saySomething("Gehe zum markierten Feld", true, 100, null);
            maps[0].mapTiles[15][5].setBorderInsets(new Insets(10, 10, 10, 10));
            dialog = 5;
        }

        if (dialog == 5) {
            System.out.println("Enemy Straight Run");
            enemy[0].enemystraightrun(10, 0.25, 0, 1);
        }
        if (dialog == 5 && mover.isOnThisTile(15, 5)) {
            mover.saySomething("Du hast es erreicht! Das Intro ist vorzeitig beendet. Viel Spaß!", true, 50, null);
            maps[0].mapTiles[15][5].setBorderInsets(new Insets(0, 0, 0, 0));
            dialog = 6;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //</editor-fold>

    //<editor-fold desc="LEVEL 2">


    ////LEVEL 2 - First Chapter - "The bright world"  ////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    public void createlevel2() {
        createLevelObjects(0, 0);
        /** Laden der Maps:
         *
         */
        maps[0] = new Map(gamePanel, "Content/Maps/XXXX", "All", new Point(0, 0));
        //Immer auf BaseMap !! daher nicht im Map Konstruktor:
        gamePanel.setCamera(maps[0].getMapSizeX(), maps[0].getMapSizeY(), maps[0].getChapterOffset());

        /** Intialisierung der Spieler:
         *
         */
        mover.setLocation((5 * Tile.TILEWIDTH), 5 * Tile.TILEHEIGHT);
        mover.setSpeed(5);
        enemy[1].setLocation((16) * 64, (5) * 64);
        enemy[2].setLocation((13) * 64, (6) * 64);

        for (int i = 0; i < maps.length; i++) {
            analytics[i] = new DisplayAnalytics(gamePanel, maps[i], pathFinder);
        }
        setLevel(2);
    }

    public void level2GameMechanic() {
        /**
         * Spielmechanik
         */
    }


    public void level2Dialog() {
        /**
         * Dialog
         */
    }

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //</editor-fold>

    //<editor-fold desc="LEVEL 3">
    ////LEVEL 3 - "XXXXXXX"  /////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    public void createlevel3() {
        createLevelObjects(0, 0);
        /** Laden der Maps:
         *
         */
        maps[0] = new Map(gamePanel, "Content/Maps/XXXX", "All", new Point(0, 0));
        //Immer auf BaseMap !! daher nicht im Map Konstruktor:
        gamePanel.setCamera(maps[0].getMapSizeX(), maps[0].getMapSizeY(), maps[0].getChapterOffset());

        /** Intialisierung der Spieler:
         *
         */
        mover.setLocation((5 * Tile.TILEWIDTH), 5 * Tile.TILEHEIGHT);
        mover.setSpeed(5);
        enemy[1].setLocation((16) * 64, (5) * 64);
        enemy[2].setLocation((13) * 64, (6) * 64);

        for (int i = 0; i < maps.length; i++) {
            analytics[i] = new DisplayAnalytics(gamePanel, maps[i], pathFinder);
        }
        setLevel(3);
    }

    public void level3GameMechanic() {
        /**
         * Spielmechanik
         */
    }


    public void level3Dialog() {
        /**
         * Dialog
         */
    }

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //</editor-fold>

    //<editor-fold desc="LEVEL 4">
    ////LEVEL 4 - "XXXXXXX"  /////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    public void createlevel4() {
        createLevelObjects(0, 0);
        /** Laden der Maps:
         *
         */
        maps[0] = new Map(gamePanel, "Content/Maps/XXXX", "All", new Point(0, 0));
        //Immer auf BaseMap !! daher nicht im Map Konstruktor:
        gamePanel.setCamera(maps[0].getMapSizeX(), maps[0].getMapSizeY(), maps[0].getChapterOffset());

        /** Intialisierung der Spieler:
         *
         */
        mover.setLocation((5 * Tile.TILEWIDTH), 5 * Tile.TILEHEIGHT);
        mover.setSpeed(5);
        enemy[1].setLocation((16) * 64, (5) * 64);
        enemy[2].setLocation((13) * 64, (6) * 64);

        for (int i = 0; i < maps.length; i++) {
            analytics[i] = new DisplayAnalytics(gamePanel, maps[i], pathFinder);
        }
        setLevel(4);
    }

    public void level4GameMechanic() {
        /**
         * Spielmechanik
         */
    }


    public void level4Dialog() {
        /**
         * Dialog
         */
    }

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //</editor-fold>

    //<editor-fold desc="LEVEL 5">
    ////LEVEL 5 - "XXXXXXX"  /////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    public void createlevel5() {
        createLevelObjects(0, 0);
        /** Laden der Maps:
         *
         */
        maps[0] = new Map(gamePanel, "Content/Maps/XXXX", "All", new Point(0, 0));
        //Immer auf BaseMap !! daher nicht im Map Konstruktor:
        gamePanel.setCamera(maps[0].getMapSizeX(), maps[0].getMapSizeY(), maps[0].getChapterOffset());

        /** Intialisierung der Spieler:
         *
         */
        mover.setLocation((5 * Tile.TILEWIDTH), 5 * Tile.TILEHEIGHT);
        mover.setSpeed(5);
        enemy[1].setLocation((16) * 64, (5) * 64);
        enemy[2].setLocation((13) * 64, (6) * 64);

        for (int i = 0; i < maps.length; i++) {
            analytics[i] = new DisplayAnalytics(gamePanel, maps[i], pathFinder);
        }
        setLevel(5);
    }

    public void level5GameMechanic() {
        /**
         * Spielmechanik
         */

    }


    public void level5Dialog() {
        /**
         * Dialog
         */
    }

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //</editor-fold>



    public void renderLevel(Graphics2D g2d) {
        if (active && level != 0) {
            maps[0].renderMap(g2d);
            mover.draw(g2d);
            if (enemy.length > 0) {
                for (int enemyamount = 0; enemyamount < enemy.length; enemyamount++) {
                    if (enemy[enemyamount] != null) {
                        enemy[enemyamount].draw(g2d);
                    }
                }
            }
            //Item Maps :
            for (int mapsAmount = 1; mapsAmount < maps.length; mapsAmount++) {
                maps[mapsAmount].renderMap(g2d);
                analytics[mapsAmount].renderAnalytics(g2d);
            }
        }
    }

    public void updateLevel() {
        if (active) {
            if (GUI.keyInputToMove(keyListener).getX() != 0 || GUI.keyInputToMove(keyListener).getY() != 0) {
                mover.setMove(GUI.keyInputToMove(keyListener));
                System.out.println("setmove");
                gamePanel.getCamera().centerOnObject(mover);
                pathFinder.resetPath();
            } // end of if

            Point2D from = pathFinder.getNextStep();
            Point2D to = pathFinder.getNextStep();
            if (to != null && GUI.keyInputToMove(keyListener).getX() == 0 && GUI.keyInputToMove(keyListener).getY() == 0) {
                mover.setPathMove(from, to);
            } // end of if

            switch (level) {
                case 0:
                    level0GameMechanic();
                    break;
                case 1:
                    level1GameMechanic();
                    break;
                case 2:
                    level2GameMechanic();
                    break;
                case 3:
                    level3GameMechanic();
                    break;
                case 4:
                    level4GameMechanic();
                    break;
                case 5:
                    level5GameMechanic();
                    break;

            }
        }
        //DEBUGGING THINGS
        try {
            levelAnzeige.setText("Game: " + level + "\n" + "X:" + mover.moverOnMapTile().x + "Y:" + mover.moverOnMapTile().y
                    + "\n" + "Steps:" + mover.getSteps()
                    + "\n" + "X- Offset:" + gamePanel.getCamera().getXOffset() + "\n" + "Y- Offset:" + gamePanel.getCamera().getYOffset()
                    + "\n" + "Camera Size:" + gamePanel.getCamera().getxSize() + "\n" + gamePanel.getCamera().getySize()
                    + "\n" + "KeyInput:" + GUI.keyInputToMove(keyListener).getLocation()
                    + "\n" + "Mover Kords:" + mover.getLocation());
        } catch (Exception e) {
        }

    }

    public void clear() {
        gamePanel.removeAll();
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public void mouseClicked(MouseEvent e) {
        for (int i = 0; i < maps.length; i++) {
            if (maps[i].isActiveInPosition(new Point(e.getX() + gamePanel.getCamera().getXOffset() + maps[i].chapterXOffset, e.getY() + gamePanel.getCamera().getYOffset() + +maps[i].chapterYOffset))) {
                start = maps[i].mapTiles[(int) (mover.getLocation().getX() / Tile.TILEWIDTH)][(int) mover.getLocation().getY() / Tile.TILEHEIGHT];
                target = maps[i].mapTiles[(e.getX() + gamePanel.getCamera().getXOffset()) / Tile.TILEWIDTH][(e.getY() + gamePanel.getCamera().getYOffset()) / Tile.TILEHEIGHT];
                pathFinder.searchPath(start, target);
            }
        }
        click++;
    }

    public int whichLevel() {
        return level;
    }

    public void setLevel(int level) {
        dialog = 1;
        this.level = level;
    }

    public void actionPerformed(ActionEvent e) {
        JButton temp = (JButton) e.getSource();
        switch (temp.getText()) {
            case "1":
                createlevel1();
                break;
            case "2":
                createlevel2();
                break;
            case "3":
                createlevel3();
                break;
            case "4":
                createlevel4();
                break;
            case "5":
                createlevel5();
                break;
        }
    }
}