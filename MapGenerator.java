import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class MapGenerator
{
	public int[][] map;
	public int brickWidth;
	public int brickHeight;
	public int totalBricks;
	public int mapWidth;
	public int mapHeight;

	public MapGenerator (int mapHeight, int mapWidth)
	{
		this.mapHeight = mapHeight;
		this.mapWidth = mapWidth;
		totalBricks = mapHeight * mapWidth;

		map = new int[mapHeight][mapWidth];
		for(int i = 0; i<map.length; i++)
		{
			for(int j =0; j<map[0].length; j++)
			{
				map[i][j] = 1;
			}			
		}
		
		brickWidth = 540/mapWidth;
		brickHeight = 150/mapHeight;
	}	
	
	public void draw(Graphics2D g)
	{
		for(int i = 0; i<map.length; i++)
		{
			for(int j =0; j<map[0].length; j++)
			{
				if(map[i][j] > 0)
				{
					g.setColor(Color.white);
					g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
					
					//this is just to show separate brick, game can still run without it
					g.setStroke(new BasicStroke(3));
					g.setColor(Color.black);
					g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
				}
			}
		}
	}
	
	public void setBrickValue(int value, int row, int col)
	{
		map[row][col] = value;
	}
}
