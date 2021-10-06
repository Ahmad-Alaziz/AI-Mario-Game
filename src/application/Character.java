package application;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.io.File;
import java.util.Objects;


public class Character extends Pane {
    public SpriteAnimation animation;
    public Point2D playerVelocity = new Point2D(0, 0);
    public boolean canJump = false;
    MediaPlayer player;

    Image marioImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/mario.png")));
    Image vlad = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/vladiSprite.png")));
    ImageView imageView = new ImageView(marioImg);
    boolean Vlad = false;
    int  count = 3, columns = 3, offsetX = 75, offsetY = 0, width = 75, height = 150;
    int MarioSizeY = 2 * Main.MARIO_SIZE;

    public Character() {
        Media sound = new Media(new File("src/application/assets/music/cocaine.mp3").toURI().toString());
        player = new MediaPlayer(sound);
        player.setVolume(2);

        imageView.setFitHeight(MarioSizeY);
        imageView.setFitWidth(Main.MARIO_SIZE);
        imageView.setViewport(new Rectangle2D(offsetX, offsetY, width, height));
        animation = new SpriteAnimation(this.imageView, Duration.millis(300), count, columns, offsetX, offsetY, width, height);


        getChildren().addAll(this.imageView);
    }

    public void moveX(int value) {
        boolean movingRight = value > 0;
        for (int i = 0; i < Math.abs(value); i++) {
            for (Node platform : Main.platforms) {
                if (this.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                    if (movingRight) {
                        if (this.getTranslateX() + Main.MARIO_SIZE == platform.getTranslateX()) {
                            this.setTranslateX(this.getTranslateX() - 1);
                            return;
                        }
                    } else {
                        if (this.getTranslateX() == platform.getTranslateX() + Main.BLOCK_SIZE) {
                            this.setTranslateX(this.getTranslateX() + 1);
                            return;
                        }
                    }
                }
            }
            this.setTranslateX(this.getTranslateX() + (movingRight ? 1 : -1));
        }
    }

    public void moveY(int value) {
        boolean movingDown = value > 0;
        for (int i = 0; i < Math.abs(value); i++) {
            for (Block platform : Main.platforms) {
                if (getBoundsInParent().intersects(platform.getBoundsInParent())) {
                    if (movingDown) {
                        if (this.getTranslateY() + MarioSizeY == platform.getTranslateY()) {
                            this.setTranslateY(this.getTranslateY() - 1);
                            canJump = true;
                            return;
                        }
                    } else {
                        if (this.getTranslateY() == platform.getTranslateY() + Main.BLOCK_SIZE) {
                            this.setTranslateY(this.getTranslateY() + 1);
                            playerVelocity = new Point2D(0, 10);
                            if (platform.bt == Block.BlockType.BONUS) {
                                Vlad = true;
                                if (Main.levelNumber == 0) {
                                    player.play();
                                    getChildren().remove(this.imageView);
                                    imageView = new ImageView(vlad);
                                    imageView.setFitHeight(MarioSizeY);
                                    imageView.setFitWidth(Main.MARIO_SIZE);
                                    imageView.setViewport(new Rectangle2D(offsetX, offsetY, width, height));
                                    animation = new SpriteAnimation(this.imageView, Duration.millis(350), count, columns, offsetX, offsetY, width, height - 5);
                                    getChildren().addAll(this.imageView);
                                }
                            }
                            return;
                        }
                    }
                }
            }
            this.setTranslateY(this.getTranslateY() + (movingDown ? 1 : -1));
            if (this.getTranslateY() > 640) {
                this.setTranslateX(0);
                this.setTranslateY(400);
                Main.gameRoot.setLayoutX(0);
            }
        }
    }

    public void jumpPlayer() {
        if (canJump) {
            playerVelocity = playerVelocity.add(0, -34);
            canJump = false;
        }
    }
}