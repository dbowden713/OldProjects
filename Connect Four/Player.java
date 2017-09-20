public class Player
{
	private int numWins,numLoss,numTie;

	public Player(ClrPlr clr)
	{
		numWins=0;
		numLoss=0;
		numTie=0;
	}

	public void setWin()
	{
		numWins++;
	}

	public void setLoss()
	{
		numLoss++;
	}

	public void setTie()
	{
		numTie++;
	}

	public int getWins()
	{
		return numWins;
	}

	public int getLoss()
	{
		return numLoss;
	}

	public int getTie()
	{
		return numTie;
	}
}