package Server;

public class Player {

    public String username;
    public String password;
    public int partiteGiocate;
    public int partiteVinte;
    public int streakVittorie;
    public int streakVittoreMax;
    public int percentualeVittorie;
    public int[] guessDistribution;

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        this.partiteGiocate = 0;
        this.partiteVinte = 0;
        this.streakVittorie = 0;
        this.streakVittoreMax = 0;
        this.percentualeVittorie = 0;
        this.guessDistribution = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    }

}