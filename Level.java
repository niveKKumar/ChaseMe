public class Level {
    /**
     * Dialog = 1 -> Level Dialog fängt an
     * = 0 -> Level Dialog startet nicht
     */
    public int dialog = 0;
    public Character mover;
    public Runner[] enemy;
    public MapBase[] maps;
    public PathFinder pathFinder;
    public DisplayAnalytics[] analytics;
    private int level;
    private GamePanel gamePanel;

    public Level(GamePanel gp, int level) {
        this.level = level;
        gamePanel = gp;
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
    }

    public DisplayAnalytics[] getAnalytics() {
        return analytics;
    }

    public MapBase[] getMaps() {
        return maps;
    }

    public int getLevel() {
        return level;
    }

    public int getDialog() {
        return dialog;
    }
}
