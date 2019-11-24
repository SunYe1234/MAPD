package org.pneditor.petrinet.model.Interface;

import org.pneditor.petrinet.model.entities.Arc;
import org.pneditor.petrinet.model.entities.Place;
import org.pneditor.petrinet.model.entities.Transition;

public interface IManagerAdapt {
	/**
     * Method used to add an already created place to
     * the net
     * @param place
     */
    void addPlace(Place place);
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


}
