package client.model;

/**
 * Created by Parsa on 1/22/2018 AD.
 */
public class BlockCell extends Cell {

    public BlockCell(int x, int y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return super.toString() + " b ";
    }
}
