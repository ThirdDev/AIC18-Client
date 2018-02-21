package client.classes.genes;

public class GeneCollections {

    // pathLength = 20
    public GeneCollection FourTowersDamage500;
    public GeneCollection FourTowersDamage1200;
    public GeneCollection FourTowersExplore200;

    public GeneCollection ThreeTowersDamage400;
    public GeneCollection ThreeTowersDamage600;
    public GeneCollection ThreeTowersExplore200;

    // pathLength = 40
    public GeneCollection TwoDoubleTowersDamage700;
    public GeneCollection TwoDoubleTowersExplore200;

    // pathLength = 100
    public GeneCollection TwoTowersDamage275;
    public GeneCollection TwoTowersDamage500;
    public GeneCollection TwoTowersExplore200;

    public GeneCollection SingleTowerDamage200;
    public GeneCollection SingleTowerDamage500;
    public GeneCollection SingleTowerExplore150;



    //Singleton class
    private GeneCollections() {
        long time = System.currentTimeMillis();

        FourTowersDamage500 = new GeneCollection("FourTowers-DamagePolicy 500-pathLength 20-2018-21-2--00-51-00.sgdf");
        FourTowersDamage1200 = new GeneCollection("FourTowers-DamagePolicy 1200-pathLength 20-2018-21-2--01-00-47.sgdf");
        FourTowersExplore200 = new GeneCollection("FourTowers-ExplorePolicy 200-pathLength 20-2018-21-2--00-52-30.sgdf");

        ThreeTowersDamage400 = new GeneCollection("ThreeTowers-DamagePolicy 400-pathLength 20-2018-20-2--20-49-25.sgdf");
        ThreeTowersDamage600 = new GeneCollection("ThreeTowers-DamagePolicy 600-pathLength 20-2018-20-2--20-38-47.sgdf");
        ThreeTowersExplore200 = new GeneCollection("ThreeTowers-ExplorePolicy 200-pathLength 20-2018-20-2--20-32-26.sgdf");

        TwoDoubleTowersDamage700 = new GeneCollection("TwoDoubleTowers-DamagePolicy 700-pathLength 40-2018-20-2--19-19-12.sgdf");
        TwoDoubleTowersExplore200 = new GeneCollection("TwoDoubleTowers-ExplorePolicy 200-pathLength 40-2018-20-2--19-05-53.sgdf");

        //TwoTowersDamage275 = new GeneCollection("");
        TwoTowersDamage500 = new GeneCollection("TwoTowers-DamagePolicy 500-pathLength 100-2018-21-2--13-25-39.sgdf");
        //TwoTowersExplore200 = new GeneCollection("");

        SingleTowerDamage200 = new GeneCollection("SingleTower-DamagePolicy 200-pathLength 100-2018-21-2--17-26-28.sgdf");
        //SingleTowerDamage500 = new GeneCollection("");
        SingleTowerExplore150 = new GeneCollection("SingleTower-ExplorePolicy 150-pathLength 100-2018-21-2--19-20-24.sgdf");

        long delta = System.currentTimeMillis() - time;
        System.out.println(delta + " ms");
    }

    private static GeneCollections instance = null;
    public static GeneCollections getCollections() {
        if (instance == null)
            instance = new GeneCollections();

        return instance;
    }
}
