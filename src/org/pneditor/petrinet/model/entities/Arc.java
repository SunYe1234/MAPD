package org.pneditor.petrinet.model.entities;

import java.util.logging.Logger;

/**
 * Entity class of the Arc
 */
public class Arc {

    private int weight;
    private boolean in;
    private Place connectedPlace;

    private final static Logger LOGGER = Logger.getLogger(Arc.class.getName());

    public Arc(int weight) {
//		super();
        this.weight = weight;
    }

    public Arc(int weight, boolean inOut) {
        super();
        this.weight = weight;
        this.in = inOut;
    }


    public Place getConnectedPlace() {
        return connectedPlace;
    }

    public void setConnectedPlace(Place connectedPlace) {
        this.connectedPlace = connectedPlace;
    }

    public int getWeight() {
        return weight;
    }


    public void setWeight(int weight) {
        this.weight = weight;
    }


    /**
     * Used to recognize the arc type
     *
     * @return true if it is inArc
     */
    public boolean isIn() {
        return in;
    }


    public void setIn(boolean in) {
        this.in = in;
    }


    @Override
    public String toString() {
        return super.toString() + "has weight " + weight + ", connected to Place" + this.connectedPlace.toString();
    }

    /**
     * Check the arc is available or not
     *
     * @return true         if it's an out arc or a in arc with enough jetons
     */
    public boolean isAvailable() {
        if (this instanceof ZeroArc) {
            if (((ZeroArc) this).isPlaceEmpty()) return true;
            return false;
        }
        if (this instanceof ViderArc)
            return true;
        if (this.isIn()) {
            return getConnectedPlace().getTokens() > getWeight();
        } else return true;
    }


}
