package application;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Main extends Application {
    public static ArrayList<Block> platforms = new ArrayList<>();
    private final HashMap<KeyCode, Boolean> keys = new HashMap<>();

    int pop_Size = 200;
    public CharacterAI[] Population = new CharacterAI[pop_Size];
    public int generation;
    double highestFitness;
    public boolean AllDead = false;
    public ArrayList<Integer> bestBrain = new ArrayList<>();

    //importing assets
    Image danceImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/dancingMarios.png")));
    Image danceImg2 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/dancingKids.png")));
    Image backgroundImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/background.png")));
    Image aiImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/backgroundAI.png")));
    Image homeScreenBg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/homeScreen.png")));
    Image wiggle = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/wiggle.png")));
    Image stripImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/strip.png")));
    
    ImageView stripView = new ImageView(stripImg);
    ImageView danceView = new ImageView(danceImg);
    ImageView danceView2 = new ImageView(danceImg2);
    ImageView background;

    public SpriteAnimation animation, animation2, animation3;

    long lastUpdate = 0;
    public static int BLOCK_SIZE = 45;
    public static int MARIO_SIZE = 58;
    public static int startJump = 400;
    public static Pane appRoot = new Pane();
    public static Pane gameRoot = new Pane();

    Stage Window;
    public Character player;
    public static int levelNumber = 0;
    private int levelWidth;
    Text topText = new Text("Generation: 0 , Highest Fit: 0");

    public static Pane mainPane = new Pane();
    Scene Menu;

    private void generateNextPhase(){
        generation++;
        topText.setText("Generation: " + generation + " , HighestFit: " + (int) (highestFitness));
        CharacterAI[] newPop = new CharacterAI[pop_Size];

        for (int i = 0; i < pop_Size; i++) {
            CharacterAI bot  = new CharacterAI();
            bot.updateBot(highestFitness, bestBrain);
            gameRoot.getChildren().add(bot);
            newPop[i] = bot;
        }

        appRoot.getChildren().add(gameRoot);
        Population = newPop;
        AllDead = false;
    }

    private void initContent() {
        //initializing views
        danceView2.setFitHeight(169);
        danceView2.setTranslateX(410);
        danceView2.setTranslateY(34);
        danceView2.setFitWidth(200);
        danceView2.setViewport(new Rectangle2D(0, 0, 200, 169));
        danceView2.setVisible(false);
        animation3 = new SpriteAnimation(this.danceView2, Duration.millis(360), 10, 10, 0, 0, 331, 281);

        danceView.setFitHeight(135);
        danceView.setTranslateX(1310);
        danceView.setTranslateY(45);
        danceView.setFitWidth(200);
        danceView.setViewport(new Rectangle2D(0, 0, 200, 135));
        danceView.setVisible(false);
        animation2 = new SpriteAnimation(this.danceView, Duration.millis(5200), 8, 8, 0, 0, 409, 264);

        stripView.setFitHeight(200);
        stripView.setTranslateX(1158);
        stripView.setTranslateY(-20);
        stripView.setFitWidth(200);
        stripView.setViewport(new Rectangle2D(0, 0, 100, 100));
        stripView.setVisible(false);
        animation = new SpriteAnimation(this.stripView, Duration.millis(700), 4, 4, 0, 0, 200, 200);

        gameRoot.getChildren().addAll(danceView2, danceView, stripView, topText);

        topText.setTranslateX(40);
        topText.setTranslateY(19);
        topText.setScaleX(1.4);
        topText.setScaleX(1.4);

        background = new ImageView(backgroundImg);
        background.setFitHeight(14 * BLOCK_SIZE);
        background.setFitWidth(212 * BLOCK_SIZE);

        ImageView backgroundAI = new ImageView(aiImg);
        backgroundAI.setFitHeight(14 * BLOCK_SIZE);
        backgroundAI.setFitWidth(71 * BLOCK_SIZE);

        levelWidth = LevelData.levels[levelNumber][0].length() * BLOCK_SIZE;
        for (int i = 0; i < LevelData.levels[levelNumber].length; i++) {
            String line = LevelData.levels[levelNumber][i];
            for (int j = 0; j < line.length(); j++) {
                switch (line.charAt(j)) {
                    case '0':
                        break;
                    case '1':
                        //platform
                        Block platformFloor = new Block(Block.BlockType.PLATFORM, j * BLOCK_SIZE, i * BLOCK_SIZE);
                        if (levelNumber == 1)
                            platformFloor.setOpacity(0);
                        break;
                    case '2':
                        //brick
                        new Block(Block.BlockType.BRICK, j * BLOCK_SIZE, i * BLOCK_SIZE);
                        break;
                    case '3':
                        //bonusTing
                        new Block(Block.BlockType.BONUS, j * BLOCK_SIZE, i * BLOCK_SIZE);
                        break;
                    case '4':
                        //Stone
                        new Block(Block.BlockType.STONE, j * BLOCK_SIZE, i * BLOCK_SIZE);
                        break;
                    case '5':
                        //Pipe..topPart
                        new Block(Block.BlockType.PIPE_TOP, j * BLOCK_SIZE, i * BLOCK_SIZE);
                        break;
                    case '6':
                        //Pipe..bottomPart
                        new Block(Block.BlockType.PIPE_BOTTOM, j * BLOCK_SIZE, i * BLOCK_SIZE);
                        break;
                    case '*':
                        //invisibleBlock
                        new Block(Block.BlockType.INVISIBLE_BLOCK, j * BLOCK_SIZE, i * BLOCK_SIZE);
                        break;
                }
            }
        }

        if (levelNumber == 0) {
            //If Normal game mode
            player = new Character();
            player.setTranslateX(0);
            player.setTranslateY(startJump);
            player.translateXProperty().addListener((obs, old, newValue) -> {
                int offset = newValue.intValue();
                if (offset > 640 && offset < levelWidth - 640) {
                    gameRoot.setLayoutX(-(offset - 640));
                    background.setLayoutX(-(offset - 640));
                }
            });

            gameRoot.getChildren().add(player);
            appRoot.getChildren().addAll(background, gameRoot);
        } else {
            //if game mode is AI
            for (int i = 0; i < pop_Size; i++) {
                Population[i] = new CharacterAI();
                Population[i].initializeBot();
                gameRoot.getChildren().addAll(Population[i]);
            }

            appRoot.getChildren().addAll(backgroundAI, gameRoot);
        }

    }

    private void update() {
        if (levelNumber == 0) {
            if (isPressed(KeyCode.UP) && player.getTranslateY() >= 5) {
                player.jumpPlayer();
            }
            if (isPressed(KeyCode.LEFT) && player.getTranslateX() >= 5) {
                player.setScaleX(-1);
                player.animation.play();
                player.moveX(-5);
            }
            if (isPressed(KeyCode.RIGHT) && player.getTranslateX() + 40 <= levelWidth - 5) {
                player.setScaleX(1);
                player.animation.play();
                player.moveX(5);
            }
            if (player.playerVelocity.getY() < 10) {
                player.playerVelocity = player.playerVelocity.add(0, 1);
            }

            player.moveY((int) player.playerVelocity.getY());

            //Hidden Feature: (Vlad Dancing)
            if(player.Vlad) {
                danceView.setVisible(true);
                animation2.play();
                danceView2.setVisible(true);
                animation3.play();
                stripView.setVisible(true);
                animation.play();
                ColorAdjust colorAdjust = new ColorAdjust();

                //Setting the contrast value
                colorAdjust.setContrast(0.4);
                colorAdjust.setBrightness(-0.6 * Math.random());

                //Setting the hue value
                colorAdjust.setHue(-2.6 * Math.random());
                background.setEffect(colorAdjust);
            }

        } else {
            for (CharacterAI bot : Population) {
                //move bot
                bot.move(levelWidth);

                //apply gravity
                bot.applyGravity();

                bot.moveY((int) bot.playerVelocity.getY());

                //fall in pit
                if ((bot.getTranslateY() > 200)) {
                    bot.setTranslateY(200);
                    bot.setRotate(90);
                }

                if ((bot.getTranslateY() <= 30) && (bot.getTranslateX() >= 540)) {
                    bot.up = true;
                }

                if (bot.step == bot.brain.size()-1) {
                    AllDead = true;
                    bot.brainDead = true;

                    double fitness = bot.calculateFitness();

                    if (fitness > highestFitness) {
                        highestFitness = fitness;
                        bestBrain = new ArrayList<>(bot.brain);
                    }
                    gameRoot.getChildren().remove(bot);
                    appRoot.getChildren().remove(gameRoot);
                }

            }

            //when they all die , start a new generation
            if (AllDead) {
                generateNextPhase();
            }
        }

    }

    private boolean isPressed(KeyCode key) {
        return keys.getOrDefault(key, false);
    }

    @Override
    public void start(Stage primaryStage) {
        Window = primaryStage;
        ImageView menuBG = new ImageView(homeScreenBg);
        ImageView wiggle = new ImageView(this.wiggle);
        ImageView wiggle2 = new ImageView(this.wiggle);
        menuBG.setFitWidth(1200);
        menuBG.setFitHeight(620);
        menuBG.toBack();

        //normalModeBtn ~~ button to play in Normal mode
        Button normalModeBtn = new Button();
        normalModeBtn.setTranslateX(270);
        normalModeBtn.setTranslateY(290);
        normalModeBtn.setOpacity(0);
        normalModeBtn.setPrefWidth(180);
        normalModeBtn.setPrefHeight(90);

        //aiModeBtn ~~ button to play in AI mode
        Button aiModeBtn = new Button();
        aiModeBtn.setTranslateX(normalModeBtn.getTranslateX() - normalModeBtn.getPrefWidth() + 40);
        aiModeBtn.setTranslateY(normalModeBtn.getTranslateY() + normalModeBtn.getPrefHeight() + 40);
        aiModeBtn.setOpacity(0);
        aiModeBtn.setPrefWidth(180);
        aiModeBtn.setPrefHeight(90);

        //Wiggle(on Hover) effect for normalModeBtn
        wiggle.setFitWidth(216);
        wiggle.setFitHeight(135);
        wiggle.setTranslateX(normalModeBtn.getTranslateX() - 17);
        wiggle.setTranslateY(normalModeBtn.getTranslateY() - 20);
        wiggle.setVisible(false);
        wiggle.toFront();

        //Wiggle(on Hover) effect for aiModeBtn
        wiggle2.setTranslateX(normalModeBtn.getTranslateX() - normalModeBtn.getPrefWidth() + 25);
        wiggle2.setTranslateY(normalModeBtn.getTranslateY() + normalModeBtn.getPrefHeight() + 18);
        wiggle2.setRotate(45);
        wiggle2.setVisible(false);

        normalModeBtn.addEventHandler(MouseEvent.MOUSE_ENTERED,
                e -> wiggle.setVisible(true));

        normalModeBtn.addEventHandler(MouseEvent.MOUSE_EXITED,
                e -> wiggle.setVisible(false));

        //event handlers for normalModeBtn
        normalModeBtn.setOnAction(arg0 -> {
            initContent();
            launchGame();
        });

        //event handlers for AI mode
        aiModeBtn.setOnAction(arg0 -> {
            levelNumber = 1;
            BLOCK_SIZE = 17;
            MARIO_SIZE = 21;
            startJump = 50;
            initContent();
            launchGame();
        });

        aiModeBtn.addEventHandler(MouseEvent.MOUSE_ENTERED,
                e -> wiggle2.setVisible(true));

        aiModeBtn.addEventHandler(MouseEvent.MOUSE_EXITED,
                e -> wiggle2.setVisible(false));

        mainPane.getChildren().addAll(menuBG, normalModeBtn, aiModeBtn, wiggle, wiggle2);
        Menu = new Scene(mainPane, 1200, 620);
        Window.setScene(Menu);
        Window.setTitle("I love my six packs so much , so I protect it with 40kgs of fat!");
        Window.show();
    }
    public static void main(String[] args) {
        launch(args);
    }

    private void launchGame() {
        Scene scene;
        if (levelNumber == 0) {
            scene = new Scene(appRoot, 1200, 620);
        } else {
            scene = new Scene(appRoot, 1206, 235);
        }
        Window.setScene(scene);
        Window.show();
        scene.setOnKeyPressed(event -> {
            keys.put(event.getCode(), true);
            if (!player.Vlad)
                player.animation.setOffsetX(75);
        });
        scene.setOnKeyReleased(event -> {
            keys.put(event.getCode(), false);
            player.animation.setOffsetX(0);
            player.animation.setCycleCount(1);
            player.animation.stop();
        });

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 7_000_000L + (35_000_000L*levelNumber) ) {
                    update();
                    lastUpdate = now;
                }
            }
        };

        timer.start();
    }
}
