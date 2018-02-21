package client.model;

import java.util.ArrayList;

/**
 * Created by Parsa on 1/22/2018 AD.
 */
public class Map {

    private Cell[][] cells;
    private int width;
    private int height;

    public Map(int width, int height, Cell[][] cells) {
        this.width = width;
        this.height = height;
        this.cells = cells;
    }

    public Cell[][] getCellsGrid() {
        return cells;
    }

    public Cell getCell(int x, int y) {
        return this.cells[y][x];
    }

    public ArrayList<Cell> getCellsList() {
        ArrayList<Cell> cellsList = new ArrayList<>();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                cellsList.add(this.cells[i][j]);
            }
        }
        return cellsList;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void empty() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Cell tmpCell = cells[i][j];
                if (tmpCell instanceof RoadCell)
                    ((RoadCell) tmpCell).getUnits().clear();
                else if (tmpCell instanceof GrassCell)
                    cells[i][j] = new GrassCell(j, i, null);
            }
        }
    }

    @Override
    public String toString() {
        String result = "Map:\n";

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                result += cells[i][j].toString();
            }
            result += "\n";
        }
        return result;
    }
}
