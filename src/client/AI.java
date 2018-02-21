package client;

import client.classes.genes.GeneCollections;
import client.model.*;
import common.util.Log;

import java.util.Random;

import static common.network.JsonSocket.TAG;

/**
 * AI class.
 * You should fill body of the method {@link }.
 * Do not change name or modifiers of the methods or fields
 * and do not add constructor for this class.
 * You can add as many methods or fields as you want!
 * Use world parameter to access and modify game's
 * world!
 * See World interface for more details.
 */
public class AI {

    Random rnd = new Random();

    public AI() {
        GeneCollections.getCollections();
    }

    void simpleTurn(World game) {
        commonTurnFunctions(game);

        /*
        Log.d(TAG,"lightTurn Called"+" Turn:"+game.getCurrentTurn());

        int t=rnd.nextInt();
        if(t%3==2){
            game.createArcherTower(rnd.nextInt(4),rnd.nextInt(game.getDefenceMap().getWidth()),rnd.nextInt(game.getDefenceMap().getHeight()));

        }else if(t%3==1){
            game.createHeavyUnit(rnd.nextInt(game.getDefenceMapPaths().size()));

        }else if(t%3==0){
            game.createLightUnit(rnd.nextInt(game.getAttackMapPaths().size()));
        }
        */
    }

    void complexTurn(World game) {
        commonTurnFunctions(game);

        /*
        Log.d(TAG,"HeavyTurn Called"+" Turn:"+game.getCurrentTurn());

        int t=rnd.nextInt();
        if(t%3==2){
            game.createStorm(rnd.nextInt(game.getDefenceMap().getWidth()),rnd.nextInt(game.getDefenceMap().getHeight()));
        }else if(t%3==1){
            game.plantBean(rnd.nextInt(game.getDefenceMap().getWidth()),rnd.nextInt(game.getDefenceMap().getHeight()));

        }else if(t%3==0){
            game.createCannonTower(rnd.nextInt(4),rnd.nextInt(game.getDefenceMap().getWidth()),rnd.nextInt(game.getDefenceMap().getHeight()));

        }
        */
    }

    //This function will be called on both simple and complex turns
    private void commonTurnFunctions(World game) {
        BankController.handleMoney(game.getMyInformation());
    }
}
