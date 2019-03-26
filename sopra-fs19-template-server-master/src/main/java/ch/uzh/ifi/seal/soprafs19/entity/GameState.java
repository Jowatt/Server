package ch.uzh.ifi.seal.soprafs19.entity;

import java.lang.reflect.Array;
import java.util.Dictionary;

public class GameState {

    private Dictionary gameState;

    public void setState(Dictionary state){
        this.gameState = state;
    }

    public Dictionary StateRequest(){
        return this.gameState;
    }
}
