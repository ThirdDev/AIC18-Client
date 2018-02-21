package client.classes.genes;

public class GeneCollections {

    // pathLength = 20
    public FullStateGeneCollection FourTowersDamage500;
    public FullStateGeneCollection FourTowersDamage1200;
    public FullStateGeneCollection FourTowersExplore200;

    public FullStateGeneCollection ThreeTowersDamage400;
    public FullStateGeneCollection ThreeTowersDamage600;
    public FullStateGeneCollection ThreeTowersExplore200;

    // pathLength = 40
    public FullStateGeneCollection TwoDoubleTowersDamage700;
    public FullStateGeneCollection TwoDoubleTowersExplore200;

    // pathLength = 100
    public FullStateGeneCollection TwoTowersDamage275;
    public FullStateGeneCollection TwoTowersDamage500;
    public FullStateGeneCollection TwoTowersExplore200;

    public FullStateGeneCollection SingleTowerDamage200;
    public FullStateGeneCollection SingleTowerDamage500;
    public FullStateGeneCollection SingleTowerExplore150;



    //Singleton class
    private GeneCollections() {
        long time = System.currentTimeMillis();

        FourTowersDamage500 = new FullStateGeneCollection("FourTowers-DamagePolicy 500-pathLength 20-2018-21-2--00-51-00.sgdf");
        FourTowersDamage1200 = new FullStateGeneCollection("FourTowers-DamagePolicy 1200-pathLength 20-2018-21-2--01-00-47.sgdf");
        FourTowersExplore200 = new FullStateGeneCollection("FourTowers-ExplorePolicy 200-pathLength 20-2018-21-2--00-52-30.sgdf");

        ThreeTowersDamage400 = new FullStateGeneCollection("ThreeTowers-DamagePolicy 400-pathLength 20-2018-20-2--20-49-25.sgdf");
        ThreeTowersDamage600 = new FullStateGeneCollection("ThreeTowers-DamagePolicy 600-pathLength 20-2018-20-2--20-38-47.sgdf");
        ThreeTowersExplore200 = new FullStateGeneCollection("ThreeTowers-ExplorePolicy 200-pathLength 20-2018-20-2--20-32-26.sgdf");

        TwoDoubleTowersDamage700 = new FullStateGeneCollection("TwoDoubleTowers-DamagePolicy 700-pathLength 40-2018-20-2--19-19-12.sgdf");
        TwoDoubleTowersExplore200 = new FullStateGeneCollection("TwoDoubleTowers-ExplorePolicy 200-pathLength 40-2018-20-2--19-05-53.sgdf");

        //TwoTowersDamage275 = new FullStateGeneCollection("");
        TwoTowersDamage500 = new FullStateGeneCollection("TwoTowers-DamagePolicy 500-pathLength 100-2018-21-2--13-25-39.sgdf");
        //TwoTowersExplore200 = new FullStateGeneCollection("");

        SingleTowerDamage200 = new FullStateGeneCollection("SingleTower-DamagePolicy 200-pathLength 100-2018-21-2--17-26-28.sgdf");
        //SingleTowerDamage500 = new FullStateGeneCollection("");
        SingleTowerExplore150 = new FullStateGeneCollection("SingleTower-ExplorePolicy 150-pathLength 100-2018-21-2--19-20-24.sgdf");

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
