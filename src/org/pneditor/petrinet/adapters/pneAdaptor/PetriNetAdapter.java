package org.pneditor.petrinet.adapters.pneAdaptor;

import org.pneditor.editor.gpetrinet.*;
import org.pneditor.petrinet.AbstractArc;
import org.pneditor.petrinet.AbstractPlace;
import org.pneditor.petrinet.AbstractTransition;
import org.pneditor.petrinet.ResetArcMultiplicityException;
import org.pneditor.petrinet.model.Interface.IManagerAdapt;
import org.pneditor.petrinet.model.control.PetriManager;
import org.pneditor.petrinet.model.entities.Arc;
import org.pneditor.petrinet.model.entities.Place;
import org.pneditor.petrinet.model.entities.Transition;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * Adapter Class which adapt the PetriManager to the need of the graphic interface.
 * It contains mainly add/delete elements in the model, get elements which has the same id with the graphic elements,
 * detects whether some elements of the graphic PetriNet have been deleted since the last time the user fired a transition
 * and keep the model PetriNet same with it.
 */
public class PetriNetAdapter implements IManagerAdapt {

    private PetriManager petriManager = PetriManager.getInstance();
    private static PetriNetAdapter petriNetAdapter;


    private PetriNetAdapter() {
        super();
    }

    /**
     * Return the PetriNetAdapter instance using singleton pattern
     * @return
     */
    public static PetriNetAdapter getInstance() {
        if (petriNetAdapter == null)
            petriNetAdapter = new PetriNetAdapter();
        return petriNetAdapter;
    }

    /**
     * add a new place to the place list in the model
     * @param place
     */
    @Override
    public void addPlace(Place place) {
        petriManager.addPlace(place);

    }

    /**
     * add a new transition to the transition list in the model
     * @param transition
     */
    @Override
    public void addTransition(Transition transition) {
        petriManager.addTransition(transition);

    }

    /**
     * add a new arc to the arc list in the model
     * @param arc
     */
    @Override
    public void addArc(Arc arc) {
        petriManager.addArc(arc);

    }


    /**
     * Given a graphic place, find the corresponding model place which has the same id
     * @param place AbstractPlace
     * @return
     */
    public Place getPlace(AbstractPlace place) {
        for (Place p : petriManager.getPlaces()) {
            if (p.getId() == place.getId())
                return p;
        }
        return null;

    }

    /**
     * Given an abstract transition, return the corresponding model place which has the same id
     * @param transition
     * @return
     */
    public Transition getTransition(AbstractTransition transition) {
        for (Transition t : petriManager.getTransitions()) {
            if (t.getId() == transition.getId()) {
                return t;
            }
        }
        return null;
    }

    /**
     * Given an abstract arc, return the mapped arc in petriNet model
     * @param arc
     * @return
     */
    public Arc getArc(AbstractArc arc) {
        for (Arc arcPetri : petriManager.getArcs()) {
            if (arcPetri.getConnectedPlace() == null) {
                return null;
            } else if (arcPetri.getConnectedPlace().getId() == arc.getSource().getId() || arcPetri.getConnectedPlace().getId() == arc.getDestination().getId()) {
                return arcPetri;
            }
        }
        return null;
    }

    public Place getPlace(int id) {
        for (Place p : petriManager.getPlaces()) {
            if (p.getId() == id)
                return p;
        }
        return null;

    }

    /**
     * Given a transition id, return the corresponding model transition
     * which has the same id
     * @param id
     * @return
     */
    public Transition getTransition(int id) {
        for (Transition p : petriManager.getTransitions()) {
            if (p.getId() == id)
                return p;
        }
        return null;

    }

    /**
     * Given a transition id, set the corresponding transition
     * as the selected transition in the model
     * @param id
     */
    public void setSelectedTransition(int id) {
        for (Transition p : petriManager.getTransitions()) {
            if (p.getId() == id)
                petriManager.setSelectedTrans(p);
        }
    }

    /**
     * Method used to connect with the logic involved in the firing process
     */
    public void upgradePetri() {
        this.petriManager.upgradePetri();
    }

    public void addAvailableTransition(int id) {
        for (Transition p : petriManager.getTransitions()) {
            if (p.getId() == id)
                petriManager.addAvailableTransition(p);
        }
    }

    /**
     * Get the connected arc of a given place
     *
     * @param placeId
     * @return
     */
    public Arc getArc(int placeId) {
        for (Transition t : petriManager.getTransitions()) {
            for (Arc a : t.getInArcs()) {
                if (a.getConnectedPlace().getId() == placeId)
                    return a;
            }

        }

        return null;
    }


    /**
     * When we change the arc type, replace the old arc with a new zero arc or vider arc of same weight,
     * and replace the arc in the inarcs/outarcs of the connected transition.
     *
     * @param oldArc
     * @param newArc
     * @param transitionId
     */
    public void replaceArc(Arc oldArc, Arc newArc, int transitionId) {
        this.petriManager.getArcs().remove(oldArc);
        this.petriManager.getArcs().add(newArc);
        if (oldArc.isIn())
            this.getTransition(transitionId).replaceoutArc(oldArc, newArc);
        else
            this.getTransition(transitionId).replaceInArc(oldArc, newArc);
    }
    /**
     * check if the transition with a certain id is available or not
     *
     * @param id the id of transition
     * @return
     */
    public boolean hasAvailableTrans(int id) {
        for (Transition p : petriManager.getTransitions()) {
            if (p.getId() == id)
                return petriManager.hasAvailableTrans(p);
        }
        return false;
    }

    /**
     * Check if user has deleted the elements of the network, and keep the model same with the graphic network
     *
     * @param graphicPetriNet
     */
    public void checkIfDeleted(GraphicPetriNet graphicPetriNet) {
        //whether user has deleted places
        Iterator<Place> itPlace = this.petriManager.getPlaces().iterator();
        while (itPlace.hasNext()) {
            Place p = itPlace.next();
            boolean deleted = true;
            for (GraphicPlace graphicPlace : graphicPetriNet.getPlaces()) {
                //if found one graphicPlace with corresponding id of current place,
                //then it's not deleted
                if (p.getId() == graphicPlace.getPlace().getId()) {
                    deleted = false;
                    break;
                }
            }
            //otherwise, remove this place from the place list and remove all of the arcs connected to it,
            //update the transition connected to these arcs too.

            if (deleted) {
                itPlace.remove();
                deleteArcsOfPlace(p);

            }
        }
        //Whether user has deleted transitions
        Iterator<Transition> itTransition = this.petriManager.getTransitions().iterator();
        while (itTransition.hasNext()) {
            Transition t = itTransition.next();
            boolean deleted = true;
            for (GraphicElement graphicTransition : graphicPetriNet.getElements()) {
                //if found one graphicPlace with corresponding id of current transition,
                //then it's not deleted
                if (graphicTransition instanceof GraphicTransition) {
                    if (t.getId() == ((GraphicTransition) graphicTransition).getTransition().getId()) {
                        deleted = false;
                        break;
                    }
                }
            }
            //otherwise, remove this transition from the transition list and remove all of the arcs connected to it,
            //don't need to change places because it doesn't save the connected arc information
            if (deleted) {
                itTransition.remove();
                deleteArcsOfTransition(t);

            }
        }
        //Whether user has deleted arcs
        Iterator<Arc> itArc = this.petriManager.getArcs().iterator();
        while (itArc.hasNext()) {
            Arc arc = itArc.next();
            boolean deleted = true;
            //for every graphic arc exists
            for (GraphicElement graphicArc : graphicPetriNet.getElements()) {
                if (graphicArc instanceof GraphicArc) {
                    //if this graphic arc has a place source
                    //and this graphic is connected to the same place like the model arc,
                    //then it's not deleted
                    if (((GraphicArc) graphicArc).getSource().isPlace()) {
                        if (((GraphicPlace) (((GraphicArc) graphicArc).getSource())).getPlace().getId() == arc.getConnectedPlace().getId()) {
                            deleted = false;
                            break;
                        }
                    }
                    //if this graphic arc has a transition source
                    if (((GraphicArc) graphicArc).getSource().isTransition() && arc.isIn()) {
                        int modelPlaceId = arc.getConnectedPlace().getId();
                        int grapicPlaceId = ((GraphicPlace) (((GraphicArc) graphicArc).getDestination())).getPlace().getId();
                        //if the graphic arc and the model arc are connected to the same place,
                        //then it's not deleted

                        if (modelPlaceId == grapicPlaceId) {
                            deleted = false;
                            break;
                        }
                    }
                }
            }
            //otherwise, remove this arc from the list, and update the connected model transition
            // because only model transition save the arc information
            if (deleted) {
                itArc.remove();
                updatedConnectedTranst(arc);
            }
        }


    }

    /**
     * delete the arc connected to the p and update the transition connected to this arc
     *
     * @param p the removed place
     */
    private void deleteArcsOfPlace(Place p) {
        Iterator<Arc> iteratorArc = petriManager.getArcs().iterator();
        while (iteratorArc.hasNext()) {
            Arc arc = iteratorArc.next();
            //if this arc is connected to the removed place
            if (arc.getConnectedPlace().getId() == p.getId()) {
                //remove it
                iteratorArc.remove();
                //find the transition which is connected to this arc,
                //and update its inArcs and outArcs
                for (Transition transition : petriManager.getTransitions()) {
                    if (transition.getInArcs().contains(arc)) {
                        transition.getInArcs().remove(arc);
                        break;
                    }
                    if (transition.getOutArcs().contains(arc)) {
                        transition.getOutArcs().remove(arc);
                        break;
                    }
                }
            }
        }
    }
    /**
     * Delete the connected arcs of a transition from the arc list. This method is called after a transition is deleted.
     *
     * @param transition
     */
    private void deleteArcsOfTransition(Transition transition) {
        for (Arc arc : transition.getInArcs()) {
            petriManager.getArcs().remove(arc);
        }
        for (Arc arc : transition.getOutArcs()) {
            petriManager.getArcs().remove(arc);
        }

    }
    /**
     * Delete the arc from the inArcs/outArcs list of the connected transition.
     * This method is called after an arc is deleted.
     *
     * @param arc
     */
    private void updatedConnectedTranst(Arc arc) {
        for (Transition transition : petriManager.getTransitions()) {
            if (transition.getInArcs().contains(arc))
                transition.getInArcs().remove(arc);
            if (transition.getOutArcs().contains(arc))
                transition.getOutArcs().remove(arc);
        }

    }

    /**
     * get all of the transitions in the model in a list
     *
     * @return
     */
    public List<Transition> getTransitions() {
        return petriManager.getTransitions();
    }

    /**
     * Method used to compare all places between both net models. It will find and return missing elements in one of the nets.
     * @param editorNet
     * @return List of places that are only in one of the two nets.
     */
    private List<Place> comparePlaces(GraphicPetriNet editorNet) {
        List<Place> editorPlaces = new ArrayList<>();
        for (GraphicPlace editorPlace : editorNet.getPlaces()) {
            if (getPlace(editorPlace.getPlace()) == null)
                createPlace(editorPlace.getPlace());
            editorPlaces.add(getPlace(editorPlace.getPlace()));
        }
        List<Place> differentPlaces = new ArrayList<>(petriManager.getPlaces());
        differentPlaces.removeAll(editorPlaces);
        return differentPlaces;
    }

    /**
     * Method used to compare all transitions between both net models. It will find and return missing elements in one of the nets.
     * @param editorNet
     * @return List of transitions that are missing in one of the nets (could be any of them)
     */
    private List<Transition> compareTransitions(GraphicPetriNet editorNet) {
        List<Transition> editorTransitions = new ArrayList<>();
        for (AbstractTransition editorTransition : editorNet.getPetriNet().getTransitions()) {
            if (getTransition(editorTransition) == null)
                createTransition(editorTransition);
            editorTransitions.add(getTransition(editorTransition));
        }
        List<Transition> differentTransitions = new ArrayList<>(petriManager.getTransitions());
        differentTransitions.removeAll(editorTransitions);
        return differentTransitions;
    }

    /**
     * Method used to compare all arcs between both net models. It will find the missing elements in one of the nets.
     * @param editorNet
     * @return List of arcs that are missing in one of the two nets
     * @throws ResetArcMultiplicityException
     */
    private List<Arc> compareArcs(GraphicPetriNet editorNet) throws ResetArcMultiplicityException {
        List<Arc> editorArcs = new ArrayList<>();
        for (GraphicArc editorArc : editorNet.getArcs()) {
            if (getArc(editorArc.getArc()) == null)
                createArc(editorArc.getArc());
            editorArcs.add(getArc(editorArc.getArc()));
        }
        List<Arc> differentArcs = new ArrayList<>(petriManager.getArcs());
        differentArcs.removeAll(editorArcs);
        return differentArcs;
    }

    /**
     *  Method to create a place in the petriNet. Its purpose is to map the AbstractPlace
     *  model of the pne-editor project to the place of the petriNet model
     * @param editorPlace
     */
    public void createPlace(AbstractPlace editorPlace) {
        Place place = new Place(editorPlace.getTokens());
        place.setId(editorPlace.getId());
        this.addPlace(place);
    }

    /**
     * Method to create a transition in the petriNet. Its purpose is to map the AbstractPlace
     * model of the pne-editor project to the transition of the petriNet model
     * @param editorTransition
     */
    public void createTransition(AbstractTransition editorTransition) {
        Transition transition = new Transition();
        transition.setId(editorTransition.getId());
        this.addTransition(transition);
    }

    /**
     * Method used to create an arc in the PetriNet. As arcs are attached only to a place in
     * the petriNet model first the place must be found. Used to map arcs between models
     * @param editorArc
     * @throws ResetArcMultiplicityException
     */
    public void createArc(AbstractArc editorArc) throws ResetArcMultiplicityException {
        Arc arc = new Arc(editorArc.getMultiplicity());
        if (editorArc.getSource().isPlace()) {
            arc.setConnectedPlace(this.getPlace(editorArc.getSource().getId()));
            arc.setIn(false);
            this.getTransition((editorArc.getDestination()).getId()).addInArc(arc);
        } else {
            arc.setConnectedPlace(this.getPlace(editorArc.getDestination().getId()));
            arc.setIn(true);
            this.getTransition((editorArc.getSource()).getId()).addOutArc(arc);
        }
        this.addArc(arc);
    }
}
