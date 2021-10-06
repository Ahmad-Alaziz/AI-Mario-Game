package application;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.Objects;


public class Block extends Pane {
    Image blocksImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/blockSet2.png")));
    ImageView block;
    public enum BlockType {
        PLATFORM, BRICK, BONUS, PIPE_TOP, PIPE_BOTTOM, INVISIBLE_BLOCK, STONE
    }
    public BlockType bt;
    public Block(BlockType blockType, int x, int y) {
    	bt = blockType;
        block = new ImageView(blocksImg);
        block.setFitWidth(Main.BLOCK_SIZE);
        block.setFitHeight(Main.BLOCK_SIZE);
        setTranslateX(x);
        setTranslateY(y);

        switch (blockType) {
            case PLATFORM -> block.setViewport(new Rectangle2D(0, 0, 16, 16));
            case BRICK -> block.setViewport(new Rectangle2D(16, 0, 15, 16));
            case BONUS -> {
                block.setViewport(new Rectangle2D(48, 0, 16, 16));
                bt = BlockType.BONUS;
            }
            case PIPE_TOP -> {
                block.setViewport(new Rectangle2D(0, 16, 32, 16));
                block.setFitWidth(Main.BLOCK_SIZE * 2);
            }
            case PIPE_BOTTOM -> {
                block.setViewport(new Rectangle2D(0, 33, 32, 14));
                block.setFitWidth(Main.BLOCK_SIZE * 2);
            }
            case INVISIBLE_BLOCK -> {
                block.setViewport(new Rectangle2D(0, 0, 16, 16));
                block.setOpacity(0);
            }
            case STONE -> block.setViewport(new Rectangle2D(32, 0, 16, 16));
        }

        getChildren().add(block);
        Main.platforms.add(this);
        Main.gameRoot.getChildren().add(this);
    }
}



