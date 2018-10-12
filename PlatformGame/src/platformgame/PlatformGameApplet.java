/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package platformgame;

import control.GameControler;
import domain.AIControlled;
import domain.Collisions.CollisionDirection;
import domain.Collisions.CollisionType;
import domain.Environment;
import domain.GameClient;
import domain.ImagesManager;
import domain.Media;
import domain.OffLine;
import domain.PerCoordinatesStaticEnvironment;
import domain.Player;
import domain.Scenario;
import domain.SoundsManager;
import domain.Sprite;
import domain.interfaces.FiredAction;
import domain.interfaces.ReactedAction;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JApplet;
import persistence.State;
import userInterface.GameApplet;
import userInterface.GameEngine;

/**
 *
 * @author harmonicaherman
 */
public class PlatformGameApplet extends JApplet{

    private static GameEngine gen;
    private GameControler gc;
    private GameClient gcl;
    private Environment environment;
    private PlatformPlayer player;
    private Scenario scenario;
    private ArrayList<Sprite> enemies;
    private Media mediaManager;

    protected class PlatformWorld extends PerCoordinatesStaticEnvironment {

        public void loadFromState() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void saveToState() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void updateFromState(State state) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public State getState() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean isGamePauseable() {
            return false;
        }

        public boolean isGameOvereable() {
            return true;
        }

        public boolean isGameMenuifiable() {
            return false;
        }

        public boolean isGameMultiStage() {
            return false;
        }

        public void toDoIfPaused() {
        }

        public void toDoIfOver() {
        }

        public void toDoIfMenuified() {
        }

        public void toDoIfStageCompleted() {
        }

        public boolean isGameOver() {
            return enemies.isEmpty();
        }

        public boolean isStageCompleted() {
            return false;
        }

        public void pauseGame() {
        }

        public void gameOver() {
            System.exit(0);
        }

        public void showMenu() {
        }

        public void changeStage() {
        }

        public boolean isGamePaused() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean isGameMenuified() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    protected class PlatformEnemy extends AIControlled {

        public PlatformEnemy(Point relativeToScreen, int width, int height, int zIndex) {
            super(relativeToScreen, width, height, zIndex);
        }

        @Override
        public void receiveCollisionFromSprite(Sprite sprite, CollisionType type) {
            if (sprite == player && type.equals(CollisionType.UNDEFINED)) {
                System.out.println("I'm an enemy an I'm diying");
            }
        }

        @Override
        public void loadFromState() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void saveToState() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void updateFromState(State state) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public State getState() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    protected class PlatformPlayer extends Player {

        private int jumpHeight;
        private int actualGroundYCoordinate;
        private boolean canJump;
        private boolean canWalk;
        private boolean jumping;
        private boolean falling;
        private boolean standing;
        private boolean landingOn;
        private boolean walkingRight;
        private boolean walkingLeft;
        private int stepCapacity;

        public PlatformPlayer(Point pointRelativeToScreen, int width, int height, int zIndex, int jumpHeight, int stepCapacity) {
            super(pointRelativeToScreen, width, height, zIndex);
            this.jumpHeight = jumpHeight;
            this.stepCapacity = stepCapacity;
            this.actualGroundYCoordinate = this.pointRelativeToScenario.y + this.height;
            this.canJump = true;
            this.canWalk = true;
        }

        public void jump() {
            if (canJump) {
                standing = false;
                jumping = true;
                falling = false;
                canJump = false;
                setYIncrement(-1);
            }
        }

        public void fall() {
            standing = false;
            jumping = false;
            falling = true;
            canJump = false;
            setYIncrement(1);
        }

        public void landOn() {

            landingOn = true;
            canJump = true;
            canWalk = true;
            jumping = false;
            falling = false;
            walkingLeft = false;
            walkingRight = false;
            setYIncrement(0);

            actualGroundYCoordinate = this.getScenarioCoordinates().y + this.height + 1;
        }

        public void walkRight() {
            if (canWalk) {
                landingOn = false;
                standing = false;
                walkingLeft = false;
                walkingRight = true;
                setXIncrement(1);
            }
        }

        public void stopWalkingRight() {
            walkingRight = false;
            if (!jumping && !falling) {
                stand();
            }
            if (walkingLeft) {
                walkLeft();
            }
        }

        public void stopWalkingLeft() {
            walkingLeft = false;
            if (!jumping && !falling) {
                stand();
            }
            if (walkingRight) {
                walkRight();
            }
        }

        public void walkLeft() {
            if (canWalk) {
                landingOn = false;
                standing = false;
                walkingRight = false;
                walkingLeft = true;
                setXIncrement(-1);
            }
        }

        public void stand() {
            standing = true;
            canJump = true;
            canWalk = true;
            landingOn = false;
            jumping = false;
            falling = false;
            walkingLeft = false;
            walkingRight = false;
            setXIncrement(0);
            setYIncrement(0);
        }

        @Override
        @SuppressWarnings("static-access")
        public void update() {
            super.update();

            //environment.getPhisics().getCollisions().checkFallingCollisionPlatformGame(this, scenario);

            environment.getPhisics().getCollisions().checkScenarioFallingCollisionPlatformGame(player, scenario);

            if (jumping) {

                if (this.getScenarioCoordinates().y <= actualGroundYCoordinate - jumpHeight) {
                    fall();
                }

                if (walkingRight) {
                    if (!player.getAnimatorName().equals("player-jumpRight")) {
                        player.setAnimator("player-jumpRight", 1500, 5000, false);
                    }
                }

                if (walkingLeft) {
                    if (!player.getAnimatorName().equals("player-jumpLeft")) {
                        player.setAnimator("player-jumpLeft", 1500, 5000, false);
                    }
                }

                //setYIncrement(-1);
            }

            if (falling) {

                //setYIncrement(1);
            }

            if(landingOn){
                if(walkingRight)
                    if (!player.getAnimatorName().equals("player-landOnRight")) {
                            player.setAnimator("player-landOnRight", 1500, 3000, false);
                        }
                if(walkingLeft)
                    if (!player.getAnimatorName().equals("player-landOnLeft")) {
                            player.setAnimator("player-landOnLeft", 1500, 3000, false);
                        }

                //stand();
                //standing = false;
                //setYIncrement(0);
                //setXIncrement(0);
            }

            if (walkingRight) {
                if (!jumping && !falling) {
                    if (!player.getAnimatorName().equals("player-walkRight")) {
                        player.setAnimator("player-walkRight", 1500, 3000, true);
                    }
                }

                //setXIncrement(stepCapacity);
            }

            if (walkingLeft) {
                if (!jumping && !falling) {
                    if (!player.getAnimatorName().equals("player-walkLeft")) {
                        player.setAnimator("player-walkLeft", 1500, 3000, true);
                    }
                }

                //setXIncrement(-stepCapacity);
            }

            if (standing) {
                if (!player.getAnimatorName().equals("player-stand") &&
                        (!player.getAnimatorName().equals("player-stand"))) {
                    player.setAnimator("player-stand", 500, 4000, false);
                }

                //setYIncrement(0);
               // setXIncrement(0);
            }

        }
    }

    public PlatformGameApplet() {
        ImagesManager imagesManager = ImagesManager.instantiate();
        SoundsManager soundsManager = SoundsManager.instantiate();

        for (int i = 0; i < 20; i++) {

            imagesManager.loadImage("player-stand", loadFile("./images/player-stand-" + i + ".gif"));

        }

        for (int i = 0; i < 9; i++) {

            imagesManager.loadImage("player-walkRight", loadFile("./images/player-walkRight-" + i + ".gif"));
            imagesManager.loadImage("player-walkLeft", loadFile("./images/player-walkLeft-" + i + ".gif"));

        }

        for (int i = 0; i < 11; i++) {

            imagesManager.loadImage("player-landOnRight", loadFile("./images/player-landOnRight-" + i + ".gif"));
            imagesManager.loadImage("player-landOnLeft", loadFile("./images/player-landOnLeft-" + i + ".gif"));

        }

        for (int i = 0; i < 11; i++) {

            imagesManager.loadImage("player-jumpRight", loadFile("./images/player-jumpRight-" + i + ".gif"));
            imagesManager.loadImage("player-jumpLeft", loadFile("./images/player-jumpLeft-" + i + ".gif"));

        }

        imagesManager.loadImage("scenario", loadFile("./images/scenario.gif"));

        imagesManager.loadImage("enemy-stand", loadFile("./images/enemy-stand-0.gif"));
        imagesManager.loadImage("enemy-stand", loadFile("./images/enemy-stand-1.gif"));
        imagesManager.loadImage("enemy-stand", loadFile("./images/enemy-stand-2.gif"));


        soundsManager.loadClip("bark", loadFile("./sounds/dog.wav"));
        soundsManager.loadClip("clank", loadFile("./sounds/clank.wav"));
        soundsManager.loadClip("spacemusic", loadFile("./sounds/spacemusic.au"));

        //ga = GameApplet.instantiate();

        gen = GameEngine.instantiate();
        gen.setEngineDimension(800, 600);
        gen.showEngineStatus();
        gen.setFps(100);

        gcl = OffLine.instantiate();

        environment = new PlatformWorld();

        mediaManager = Media.instantiate();

        //player = new Player(new Point(100,450), 30, 30, 0);
        player = new PlatformPlayer(new Point(200, 200), 30, 30, 0, 100, 1);


        Rectangle[] scenarioCollisionableAreas = new Rectangle[3];
        scenarioCollisionableAreas[0] = new Rectangle(new Point(196,400), new Dimension(129, 25));
        scenarioCollisionableAreas[1] = new Rectangle(new Point(272, 364), new Dimension(129, 25));
        scenarioCollisionableAreas[2] = new Rectangle(new Point(439,328), new Dimension(129, 25));
//        scenarioCollisionableAreas[2] = new Rectangle(new Point(100,450), new Dimension(800, 10));

        scenario = new Scenario(new Point(0, 0), 800, 600, 0, scenarioCollisionableAreas);
        scenario.setWestLimit(82);
        scenario.setSouthLimit(478);
        enemies = new ArrayList<Sprite>();

        PlatformEnemy enemy1 = new PlatformEnemy(new Point(759, 388), 40, 90, 0);
        enemy1.addAction(new ReactedAction() {

            public String getActionName() {
                return "defending himself";
            }

            public CollisionType getCollisionTypeCausable() {
                return CollisionType.DEFENDING;
            }

            public CollisionType[] getCollisionTypesReactionable() {

                CollisionType[] reactionableTypes = new CollisionType[1];
                reactionableTypes[0] = CollisionType.ATTACKING;

                return reactionableTypes;
            }

            public void executeAction() {
                System.out.println("I'm an enemy and i'm trying to defend my life from an atacker!!");
            }

            public void finishAction() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public CollisionDirection getDirectionReactionable() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });

        enemies.add(enemy1);

        player.addAction(new FiredAction() {

            public int getDefaultEventCode() {
                return KeyEvent.VK_UP;
            }

            public String getActionName() {
                return "Jump";
            }

            public CollisionType getCollisionTypeCausable() {
                return CollisionType.JUMPING;
            }

            public void executeAction() {

                player.jump();

            }

            public void finishAction() {

                player.fall();
            }
        });

        player.addAction(new FiredAction() {

            public int getDefaultEventCode() {
                return KeyEvent.VK_LEFT;
            }

            public String getActionName() {
                return "LEFT";
            }

            public CollisionType getCollisionTypeCausable() {
                return CollisionType.MOVING;
            }

            public void executeAction() {

                player.walkLeft();

            }

            public void finishAction() {

                player.stopWalkingLeft();
            }
        });

        player.addAction(new FiredAction() {

            public int getDefaultEventCode() {
                return KeyEvent.VK_RIGHT;
            }

            public String getActionName() {
                return "RIGHT";
            }

            public CollisionType getCollisionTypeCausable() {
                return CollisionType.MOVING;
            }

            public CollisionType getCollisionTypesReactionable() {
                return CollisionType.UNDEFINED;
            }

            public void executeAction() {

                player.walkRight();

            }

            public void finishAction() {

                player.stopWalkingRight();
            }
        });

        player.addAction(new FiredAction() {

            public int getDefaultEventCode() {
                return KeyEvent.VK_DOWN;
            }

            public String getActionName() {
                return "DOWN";
            }

            public CollisionType getCollisionTypeCausable() {
                return CollisionType.MOVING;
            }

            public CollisionType getCollisionTypesReactionable() {
                return CollisionType.UNDEFINED;
            }

            public void executeAction() {
                //player.setYIncrement(1);
//                System.out.println("Posicion " + player.getScenarioCoordinates());
                player.stand();
            }

            public void finishAction() {
                //player.stop();
            }
        });

        player.addAction(new FiredAction() {

            public int getDefaultEventCode() {
                return KeyEvent.VK_C;
            }

            public String getActionName() {
                return "clank";
            }

            public CollisionType getCollisionTypeCausable() {
                return CollisionType.UNDEFINED;
            }

            public CollisionType getCollisionTypesReactionable() {
                return CollisionType.UNDEFINED;
            }

            public void executeAction() {
                player.setClipPlayer("clank");
            }

            public void finishAction() {
                player.playClip();
            }
        });

        player.addAction(new ReactedAction() {

            public String getActionName() {
                return "landingOn";
            }

            public CollisionType getCollisionTypeCausable() {
                return CollisionType.UNDEFINED;
            }

            public CollisionType[] getCollisionTypesReactionable() {
                CollisionType[] reactionableTypes = new CollisionType[1];
                reactionableTypes[0] = CollisionType.LANDING;
                //reactionableTypes[1] = CollisionType.MOVING;

                return reactionableTypes;
            }

            public void executeAction() {
                //if (player.falling) {
                    player.landOn();
                //}
            }

            public void finishAction() {
            }

            public CollisionDirection getDirectionReactionable() {
                return CollisionDirection.SOUTH;
            }
        });

        player.addAction(new ReactedAction() {

            public String getActionName() {
                return "falling";
            }

            public CollisionType getCollisionTypeCausable() {
                return CollisionType.UNDEFINED;
            }

            public CollisionType[] getCollisionTypesReactionable() {
                CollisionType[] reactionableTypes = new CollisionType[1];
                reactionableTypes[0] = CollisionType.FALLING;

                return reactionableTypes;
            }

            public void executeAction() {
                if (!player.jumping) {
                    player.fall();
                }
            }

            public void finishAction() {
            }

            public CollisionDirection getDirectionReactionable() {
                return CollisionDirection.SOUTH;
            }
        });

        player.addAction(new ReactedAction() {

            public String getActionName() {
                return "collision";
            }

            public CollisionType getCollisionTypeCausable() {
                return CollisionType.MOVING;
            }

            public CollisionType[] getCollisionTypesReactionable() {
                CollisionType[] reactionableTypes = new CollisionType[2];
                reactionableTypes[0] = CollisionType.MOVING;
                reactionableTypes[1] = CollisionType.LIMIT_REACHED;

                return reactionableTypes;
            }

            public void executeAction() {
                player.fall();
            }

            public void finishAction(){
            }

            public CollisionDirection getDirectionReactionable() {
                return CollisionDirection.ANY;
            }
        });

        player.setVisibilityRange(20);
        player.setImage("player-stand");
        player.setAnimator("player-stand", 500, 4000, false);

        scenario.setImage("scenario");
        enemy1.setImage("enemy-stand");

        enemy1.setAnimator("enemy-stand", 900, 1000, true);


        player.setClipPlayer("bark");


        scenario.setClipPlayer("spacemusic");
        scenario.loopClip();

        environment.setPlayer((PlatformPlayer) player);
        environment.setScenario(scenario);
        environment.setOthers(enemies);

        gcl.initialize(environment);

        gc = GameControler.instanciate(gen, gcl);

        //ga.addGameEngine(gen);
        //ga.pack();
        //ga.setVisible(true);
        //ga.start();
    }

    private File loadFile(String path) {

        File file;

        try {
            file = new File(getClass().getResource(path).getPath());
        } catch (NullPointerException nullEx) {
            file = new File(path);
        }

        return file;
    }

    @Override
    public void init(){
        Container c = getContentPane();
        c.setLayout( new BorderLayout() );

        ImagesManager imagesManager = ImagesManager.instantiate();
        SoundsManager soundsManager = SoundsManager.instantiate();

        for (int i = 0; i < 20; i++) {

            imagesManager.loadImage("player-stand", loadFile("./images/player-stand-" + i + ".gif"));

        }

        for (int i = 0; i < 9; i++) {

            imagesManager.loadImage("player-walkRight", loadFile("./images/player-walkRight-" + i + ".gif"));
            imagesManager.loadImage("player-walkLeft", loadFile("./images/player-walkLeft-" + i + ".gif"));

        }

        for (int i = 0; i < 11; i++) {

            imagesManager.loadImage("player-landOnRight", loadFile("./images/player-landOnRight-" + i + ".gif"));
            imagesManager.loadImage("player-landOnLeft", loadFile("./images/player-landOnLeft-" + i + ".gif"));

        }

        for (int i = 0; i < 11; i++) {

            imagesManager.loadImage("player-jumpRight", loadFile("./images/player-jumpRight-" + i + ".gif"));
            imagesManager.loadImage("player-jumpLeft", loadFile("./images/player-jumpLeft-" + i + ".gif"));

        }

        imagesManager.loadImage("scenario", loadFile("./images/scenario.gif"));

        imagesManager.loadImage("enemy-stand", loadFile("./images/enemy-stand-0.gif"));
        imagesManager.loadImage("enemy-stand", loadFile("./images/enemy-stand-1.gif"));
        imagesManager.loadImage("enemy-stand", loadFile("./images/enemy-stand-2.gif"));


        soundsManager.loadClip("bark", loadFile("./sounds/dog.wav"));
        soundsManager.loadClip("clank", loadFile("./sounds/clank.wav"));
        soundsManager.loadClip("spacemusic", loadFile("./sounds/spacemusic.au"));

        //ga = GameApplet.instantiate();

        gen = GameEngine.instantiate();
        gen.setEngineDimension(800, 600);
        gen.showEngineStatus();
        gen.setFps(100);

        gcl = OffLine.instantiate();

        environment = new PlatformWorld();

        mediaManager = Media.instantiate();

        //player = new Player(new Point(100,450), 30, 30, 0);
        player = new PlatformPlayer(new Point(200, 200), 30, 30, 0, 100, 1);


        Rectangle[] scenarioCollisionableAreas = new Rectangle[3];
        scenarioCollisionableAreas[0] = new Rectangle(new Point(196,400), new Dimension(129, 25));
        scenarioCollisionableAreas[1] = new Rectangle(new Point(272, 364), new Dimension(129, 25));
        scenarioCollisionableAreas[2] = new Rectangle(new Point(439,328), new Dimension(129, 25));
//        scenarioCollisionableAreas[2] = new Rectangle(new Point(100,450), new Dimension(800, 10));

        scenario = new Scenario(new Point(0, 0), 800, 600, 0, scenarioCollisionableAreas);
        scenario.setWestLimit(82);
        scenario.setSouthLimit(478);
        enemies = new ArrayList<Sprite>();

        PlatformEnemy enemy1 = new PlatformEnemy(new Point(759, 388), 40, 90, 0);
        enemy1.addAction(new ReactedAction() {

            public String getActionName() {
                return "defending himself";
            }

            public CollisionType getCollisionTypeCausable() {
                return CollisionType.DEFENDING;
            }

            public CollisionType[] getCollisionTypesReactionable() {

                CollisionType[] reactionableTypes = new CollisionType[1];
                reactionableTypes[0] = CollisionType.ATTACKING;

                return reactionableTypes;
            }

            public void executeAction() {
                System.out.println("I'm an enemy and i'm trying to defend my life from an atacker!!");
            }

            public void finishAction() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public CollisionDirection getDirectionReactionable() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });

        enemies.add(enemy1);

        player.addAction(new FiredAction() {

            public int getDefaultEventCode() {
                return KeyEvent.VK_UP;
            }

            public String getActionName() {
                return "Jump";
            }

            public CollisionType getCollisionTypeCausable() {
                return CollisionType.JUMPING;
            }

            public void executeAction() {

                player.jump();

            }

            public void finishAction() {

                player.fall();
            }
        });

        player.addAction(new FiredAction() {

            public int getDefaultEventCode() {
                return KeyEvent.VK_LEFT;
            }

            public String getActionName() {
                return "LEFT";
            }

            public CollisionType getCollisionTypeCausable() {
                return CollisionType.MOVING;
            }

            public void executeAction() {

                player.walkLeft();

            }

            public void finishAction() {

                player.stopWalkingLeft();
            }
        });

        player.addAction(new FiredAction() {

            public int getDefaultEventCode() {
                return KeyEvent.VK_RIGHT;
            }

            public String getActionName() {
                return "RIGHT";
            }

            public CollisionType getCollisionTypeCausable() {
                return CollisionType.MOVING;
            }

            public CollisionType getCollisionTypesReactionable() {
                return CollisionType.UNDEFINED;
            }

            public void executeAction() {

                player.walkRight();

            }

            public void finishAction() {

                player.stopWalkingRight();
            }
        });

        player.addAction(new FiredAction() {

            public int getDefaultEventCode() {
                return KeyEvent.VK_DOWN;
            }

            public String getActionName() {
                return "DOWN";
            }

            public CollisionType getCollisionTypeCausable() {
                return CollisionType.MOVING;
            }

            public CollisionType getCollisionTypesReactionable() {
                return CollisionType.UNDEFINED;
            }

            public void executeAction() {
                //player.setYIncrement(1);
//                System.out.println("Posicion " + player.getScenarioCoordinates());
                player.stand();
            }

            public void finishAction() {
                //player.stop();
            }
        });

        player.addAction(new FiredAction() {

            public int getDefaultEventCode() {
                return KeyEvent.VK_C;
            }

            public String getActionName() {
                return "clank";
            }

            public CollisionType getCollisionTypeCausable() {
                return CollisionType.UNDEFINED;
            }

            public CollisionType getCollisionTypesReactionable() {
                return CollisionType.UNDEFINED;
            }

            public void executeAction() {
                player.setClipPlayer("clank");
            }

            public void finishAction() {
                player.playClip();
            }
        });

        player.addAction(new ReactedAction() {

            public String getActionName() {
                return "landingOn";
            }

            public CollisionType getCollisionTypeCausable() {
                return CollisionType.UNDEFINED;
            }

            public CollisionType[] getCollisionTypesReactionable() {
                CollisionType[] reactionableTypes = new CollisionType[1];
                reactionableTypes[0] = CollisionType.LANDING;
                //reactionableTypes[1] = CollisionType.MOVING;

                return reactionableTypes;
            }

            public void executeAction() {
                //if (player.falling) {
                    player.landOn();
                //}
            }

            public void finishAction() {
            }

            public CollisionDirection getDirectionReactionable() {
                return CollisionDirection.SOUTH;
            }
        });

        player.addAction(new ReactedAction() {

            public String getActionName() {
                return "falling";
            }

            public CollisionType getCollisionTypeCausable() {
                return CollisionType.UNDEFINED;
            }

            public CollisionType[] getCollisionTypesReactionable() {
                CollisionType[] reactionableTypes = new CollisionType[1];
                reactionableTypes[0] = CollisionType.FALLING;

                return reactionableTypes;
            }

            public void executeAction() {
                if (!player.jumping) {
                    player.fall();
                }
            }

            public void finishAction() {
            }

            public CollisionDirection getDirectionReactionable() {
                return CollisionDirection.SOUTH;
            }
        });

        player.addAction(new ReactedAction() {

            public String getActionName() {
                return "collision";
            }

            public CollisionType getCollisionTypeCausable() {
                return CollisionType.MOVING;
            }

            public CollisionType[] getCollisionTypesReactionable() {
                CollisionType[] reactionableTypes = new CollisionType[2];
                reactionableTypes[0] = CollisionType.MOVING;
                reactionableTypes[1] = CollisionType.LIMIT_REACHED;

                return reactionableTypes;
            }

            public void executeAction() {
                player.fall();
            }

            public void finishAction(){
            }

            public CollisionDirection getDirectionReactionable() {
                return CollisionDirection.ANY;
            }
        });

        player.setVisibilityRange(20);
        player.setImage("player-stand");
        player.setAnimator("player-stand", 500, 4000, false);

        scenario.setImage("scenario");
        enemy1.setImage("enemy-stand");

        enemy1.setAnimator("enemy-stand", 900, 1000, true);


        player.setClipPlayer("bark");


        scenario.setClipPlayer("spacemusic");
        scenario.loopClip();

        environment.setPlayer((PlatformPlayer) player);
        environment.setScenario(scenario);
        environment.setOthers(enemies);

        gcl.initialize(environment);

        gc = GameControler.instanciate(gen, gcl);

        c.add(gen, "Center");
    }
    @Override
    public void start()
   {  gen.addNotify();  }

    @Override
   public void stop()
   {   }

    @Override
   public void destroy()
   {  gen.terminate();  }
}
