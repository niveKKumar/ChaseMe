import com.sun.istack.internal.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class Level implements ActionListener {

    public int level;
    public MapBase[] maps;
    public Character mover;
    public Runner[] enemy;
    public PathFinder pathFinder;
    public DisplayAnalytics[] analytics;
    public Tile start;
    public Tile target;
    public int click = 0;
    private GamePanel gamePanel;
    private int currentClick;
    private int currentSteps;
    private int dialog;
    private boolean active;
    private GridBagConstraints gbc = new GridBagConstraints();


    public Level(GamePanel gp) {
        this.gamePanel = gp;
        active = true;
    }

    public JPanel createLevelMenu() {
        JPanel levelPane;
        levelPane = new JPanel(new GridBagLayout());
        levelPane.setFocusable(false);
        levelPane.setBorder(BorderFactory.createLineBorder(Color.blue, 5));

        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.fill = GridBagConstraints.BOTH;
        levelPane.setBackground(null);
        int j = 2;
        MenuButton[] buttons = new MenuButton[5];
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new MenuButton(i);
            buttons[i].setFocusable(false);
            buttons[i].addActionListener(this);
            MenuUI.addObject(gbc, buttons[i], levelPane, 2, j++, 2, 2, true);
        }

        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 0, 10, 10);
        gbc.anchor = GridBagConstraints.LAST_LINE_END;
        JButton info = new JButton("Info (Platzhalter");
        MenuUI.addObject(gbc, info, levelPane, gbc.gridx + 1, gbc.gridy + 2, 0.5f, 0.5f, false);

        return levelPane;
    }

    public void createLevelObjects(int mapAmount, int enemyAmount) {
        maps = new Map[mapAmount];
        analytics = new DisplayAnalytics[mapAmount];

//        if (moverCords != null) {
//            System.out.println("!Null");
//            System.out.println("_________________________"+(int) (moverCords.getX()*Tile.TILEWIDTH)+"||"+ (int) (moverCords.getY()*Tile.TILEHEIGHT));
//            mover = new Character(gamePanel, (int) (moverCords.getX()*Tile.TILEWIDTH), (int) (moverCords.getY()*Tile.TILEHEIGHT),maps);
//                    mover.setLocation((int) (moverCords.getX()*Tile.TILEWIDTH), (int) (moverCords.getY()*Tile.TILEHEIGHT) );
//
////            System.out.println("1 create level objects :"+(moverCords.getX())+"||"+(moverCords.getY()));
////            System.out.println("2 create level objects :"+(mover.getLocation().getX())+"||"+(mover.getLocation().getY()));
//        }else {
//            try {
//                mover = new Character(gamePanel, (int) mover.getLocation().getX(), (int) mover.getLocation().getY(), maps);
////                System.out.println("3 create level objects :"+(mover.getLocation().getX())+"||"+(mover.getLocation().getY()));
//            } catch (Exception e) {
//                mover = new Character(gamePanel, 64, 64, maps);
////                System.out.println("4 create level objects :"+(mover.getLocation().getX())+"||"+(mover.getLocation().getY()));
//            }
//        }

        mover = new Character(gamePanel, 0, 0, maps);
        SpriteSheet playersheet = new SpriteSheet("Content/Graphics/player/playersheet.png", 4, 3);
        enemy = new Runner[enemyAmount];
        for (int i = 0; i < enemy.length; i++) {
            enemy[i] = new Runner(gamePanel, 0, 0, playersheet, maps);
        }
        pathFinder = new PathFinder(maps, mover, gamePanel);
        dialog = 0;
        active = true;
    }

    public void createLobby() {
        createLevelObjects(1, 0/*,null*/);
        //Einlesung der Maps:
        maps[0] = new Map(gamePanel, "Content/Maps/menu.txt", "Border", new Point(0, 0));
        gamePanel.camera = new Camera(0, 0);

        for (int i = 0; i < maps.length; i++) {
            analytics[i] = new DisplayAnalytics(gamePanel, maps[i], pathFinder);
        }
        // Intialisierung der Spieler:
        mover.setLocation(100, 100);
        System.out.println("Lobby loaded...");
        setLevel(999);
    }

    ////LEVEL 0 - Testumgebung (Editor) ////////////////////////////save//////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    public void createlevel0(MapBase[] pMaps, @Nullable Point moverCords) {
        // TODO: 14.04.2019 TileSet Path in Map File !!
        // TODO: 14.04.2019 EnemyAmount auch lesbar ! (Vllt Level Config) -> Editor soll nur Map testen können
        //                                                                   (vllt bei Spielupdate die Fähigkeit Level mit Spieler zu erstellen :) )
        // TODO: 14.04.2019 Abfrage von bestimmten Informationen (Mover Spawn Punkt) -> wird einfach abgefragt = ansonsten zu viel Aufwand
        createLevelObjects(pMaps.length, 0/*,moverCords*/);
        maps = pMaps;
        gamePanel.setCamera(maps[0].getMapSizeX(), maps[0].getMapSizeY());
        for (int i = 0; i < maps.length; i++) {
            analytics[i] = new DisplayAnalytics(gamePanel, maps[i], pathFinder);
        }
        // Intialisierung der Spieler:
        setLevel(0);

    }
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////

    public void level0GameMechanic() {
    }

    ////LEVEL 1 - Intro  /////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    public void createlevel1() {
        createLevelObjects(3, 5/*,new Point(10,20)*/);
        //Laden der Maps:
        maps[0] = new Map(gamePanel, "Content/Maps/0 Base.txt", "All", new Point(0, 0));
        maps[1] = new Map(gamePanel, "Content/Maps/0A Items.txt", "Item", new Point(5, 0));
        maps[2] = new Map(gamePanel, "Content/Maps/0B Items.txt", "Item", new Point(10, 0));
        gamePanel.setCamera(maps[0].getMapSizeX(), maps[0].getMapSizeY());
        for (int i = 0; i < maps.length; i++) {
            analytics[i] = new DisplayAnalytics(gamePanel, maps[i], pathFinder);
        }
        // Intialisierung der Spieler:
        mover.setLocation((5 * Tile.TILEWIDTH), 5 * Tile.TILEHEIGHT);
        mover.setSpeed(5);
        enemy[1].setLocation((16) * 64, (5) * 64);
        enemy[2].setLocation((13) * 64, (6) * 64);
        setLevel(1);
    }

    public void level1GameMechanic() {
//            enemy[0].enemystraightrun(64, mover.speed / 2, 0, 1);
//            level1Dialog();

    }

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////

    public void level1Dialog() {
        if (dialog == 1) {
            mover.saySomething("Wo bin ich?" + "\n" + "(Klicken für weiteren Dialog)", true, 100);
            mover.setSpeaking(true);
            currentClick = click;
            dialog = 2;
        }
        if (dialog == 2 && click - currentClick >= 1) {
            mover.saySomething("W-A-S-D drücken um sich zu bewegen", true, 100);
            mover.setSpeaking(false);
            currentSteps = mover.getSteps();
            dialog = 3;
        }
        if (dialog == 3 && mover.getSteps() - currentSteps >= 4) {
            System.out.println("Steps after Dialog 1:" + (mover.getSteps() - currentSteps));
            if (5 <= mover.getSteps() - currentSteps) {
                dialog = 4;
            }
        }
        if (dialog == 4) {
            mover.saySomething("Gut ! Laufe zum markierten Feld !", true, 100);
            maps[0].mapTiles[10][5].setForeground(Color.yellow);
            dialog = 5;
        }
        if (dialog == 5 && mover.isOnThisTile(10, 5)) {
            mover.saySomething("Die Gegner könne verschiedene Gestalten annehmen und haben verschiedene Charakter", true, 50);
            maps[0].mapTiles[10][5].setForeground(null);
            dialog = 6;
        }
    }

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


            for (int mapsAmount = 1; mapsAmount < maps.length; mapsAmount++) {
                maps[mapsAmount].renderMap(g2d);
                analytics[mapsAmount].renderAnalytics(g2d);
            }
        }
    }

    public void updateLevel() {
        if (active) {
            if (gamePanel.getGUI().keyInputToMove(gamePanel.keyManager).getX() != 0 || gamePanel.getGUI().keyInputToMove(gamePanel.keyManager).getY() != 0) {
                mover.setMove(gamePanel.getGUI().keyInputToMove(gamePanel.keyManager));
                gamePanel.camera.centerOnObject(mover);
                pathFinder.resetPath();
                gamePanel.getGUI().taAnzeige.setText("Level: " + level + "\n" + "X:" + mover.moverOnMapTile().x + "Y:" + mover.moverOnMapTile().y
                        + "\n" + "Steps:" + mover.getSteps()
                        + "\n" + "X- Offset:" + gamePanel.camera.getXOffset() + "\n" + "Y- Offset:" + gamePanel.camera.getYOffset());
            } // end of if
            Point2D from = pathFinder.getNextStep();
            Point2D to = pathFinder.getNextStep();
            if (!mover.isSpeaking() && to != null && gamePanel.getGUI().keyInputToMove(gamePanel.keyManager).getX() == 0 && gamePanel.getGUI().keyInputToMove(gamePanel.keyManager).getY() == 0) {
                mover.setPathMove(from, to);
            } // end of if

            switch (level) {
                case 0:
                    level0GameMechanic();
                    break;
                case 1:
                    level1GameMechanic();
                    break;
//            case 2 :
//                level2GameMechanic();
//                break;
//            case 3 :
//                level3GameMechanic();
//                break;
//            case 4 :
//                level4GameMechanic();
//                break;
//            case 5 :
//                level5GameMechanic();
//                break;

            }
        }
    }

    public void clear() {
        gamePanel.getGUI().getGamePanel().removeAll();
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public void mouseClicked(MouseEvent e) {
        for (int i = 0; i < maps.length; i++) {
            if (maps[i].isActiveInPosition(new Point(e.getX() + gamePanel.camera.getXOffset() + maps[i].chapterXOffset, e.getY() + gamePanel.camera.getYOffset() + +maps[i].chapterYOffset))) {
                start = maps[i].mapTiles[(int) (mover.getLocation().getX() / Tile.TILEWIDTH)][(int) mover.getLocation().getY() / Tile.TILEHEIGHT];
                target = maps[i].mapTiles[(e.getX() + gamePanel.camera.getXOffset()) / Tile.TILEWIDTH][(e.getY() + gamePanel.camera.getYOffset()) / Tile.TILEHEIGHT];
                pathFinder.searchPath(start, target);
                click++;
            }
        }
    }

    public int whichLevel() {
        return level;
    }

    public void setLevel(int level) {
        dialog = 1;
        this.level = level;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton temp = (JButton) e.getSource();
        switch (temp.getText()) {
            case "1":
                createlevel1();
                break;
            case "2":
                createlevel1();
                break;
            case "3":
                createlevel1();
                break;
            case "4":
                createlevel1();
                break;
            case "5":
                createlevel1();
                break;
        }
    }
}