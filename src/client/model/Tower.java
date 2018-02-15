package client.model;

/**
 * Created by Parsa on 1/22/2018 AD.
 */
public class Tower extends Entity {

    private int level;

    public Tower(int x, int y, Owner owner,int level,int id) {
        super(x, y, owner,id);
        this.level=level;

    }

    public int getLevel() {
        return level;
    }

    public int getDamage(int level){
        return 0;
    }

    public int getDamage(){return 0;}

    public int getPrice(int level){
        return 0;
    }

    public int getPrice(){return 0;}

    public int getAttackRange(){return 0;}

    public int getAttackSpeed(){
        return 0;
    }


    @Override
    public String toString() {
        return "Tower:: "+"level:"+level+" damage:"+getDamage()+
                " price:"+getPrice()+
                " attack range:"+getAttackRange()+
                " attack_speed:"+getAttackSpeed()+
                super.toString();
    }
}
