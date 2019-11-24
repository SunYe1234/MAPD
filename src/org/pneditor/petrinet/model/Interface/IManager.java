package org.pneditor.petrinet.model.Interface;

import org.pneditor.petrinet.model.entities.Arc;
import org.pneditor.petrinet.model.entities.Place;
import org.pneditor.petrinet.model.entities.Transition;

/**
 * Interface for the manager tasks, used to modify
 * the petri net.
 */

public interface IManager {
    /**
     * Method used to add an already existing transition to
     * the net
     * @param transition
     */
    void addTransition(Transition transition);

    /**
     * Method used to add an already existing arc to
     * the net
     * @param arc
     */
    void addArc(Arc arc);

    /**
     * Method used to add an already created place to
     * the net
     * @param place
     */
    void addPlace(Place place);

    /**
     * Method used to modify the net by changing the weight of a given arc
     * @param arc       arc to be changed
     * @param newWeight this is the total new wight, the previous one will be replaced
     */
    void changeWeight(Arc arc, int newWeight);

    /**
     * Method used to modify the quantity of jetons in a given place
     * @param place
     * @param jetonsQ this is the total jetons quantite, the previous one will be replaced
     */
    void changeJetonsInPlace(Place place, int jetonsQ);

}
