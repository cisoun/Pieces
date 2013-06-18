package reversi.utils;

public class Joueur {
	public static final boolean NOIR = false;
	public static final boolean BLANC = true;
	
	public static final int TYPE_HUMAIN = 0;
	public static final int TYPE_IA1 = 1;
	public static final int TYPE_IA2 = 2;
	public static final int TYPE_IA3 = 3;
	public static final int TYPE_RESEAU = 4;
	
	private boolean couleur;
	private int type;
	private int score;
	
	public Joueur(boolean couleur, int type) {
		this.couleur = couleur;
		this.type = type;
	}
	
	public int getScore()
	{
		return score;
	}
	
	public int getType()
	{
		return type;
	}
	
	public boolean isIA()
	{
		return (type == TYPE_IA1 || type == TYPE_IA2 || type == TYPE_IA3);
	}
	
	public void reinitialiser()
	{
		score = 0;
	}
	
	public void gagne()
	{
		score++;
	}
	
	public void perds()
	{
		score--;
	}
	
	public void setType(int type)
	{
		this.type = type;
	}
}
