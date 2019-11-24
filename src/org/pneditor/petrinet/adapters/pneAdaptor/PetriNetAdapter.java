package org.pneditor.petrinet.adapters.pneAdaptor;

import org.pneditor.editor.gpetrinet.GraphicPetriNet;
import org.pneditor.editor.gpetrinet.GraphicPlace;
import org.pneditor.petrinet.AbstractPlace;
import org.pneditor.petrinet.model.Interface.IManagerAdapt;
import org.pneditor.petrinet.model.control.PetriManager;
import org.pneditor.petrinet.model.entities.Arc;
import org.pneditor.petrinet.model.entities.Place;
import org.pneditor.petrinet.model.entities.Transition;

import java.util.List;

public class PetriNetAdapter implements IManagerAdapt {

    private PetriManager petriManager = PetriManager.getInstance();
    private static PetriNetAdapter petriNetAdapter;


    private PetriNetAdapter() {
        super();
    }

    public static PetriNetAdapter getInstance() {
        if (petriNetAdapter == null)
            petriNetAdapter = new PetriNetAdapter();
        return petriNetAdapter;
    }


    @Override
    public void addPlace(Place place) {
        petriManager.addPlace(place);

    }

    @Override
    public void addTransition(Transition transition) {
        petriManager.addTransition(transition);

    }

    @Override
    public void addArc(Arc arc) {
        petriManager.addArc(arc);

    }

//    public void setZeroArc(int id) {
//        for (Arc arc : petriManager.getArcs()) {
//            if (arc.)
//        }
//    }

    public Place getPlace(AbstractPlace place) {
        for (Place p : petriManager.getPlaces()) {
            if (p.getId() == place.getId())
                return p;
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


    public Transition getTransition(int id) {
        for (Transition p : petriManager.getTransitions()) {
            if (p.getId() == id)
                return p;
        }
        return null;

    }

    public void setSelectedTransition(int id) {
        for (Transition p : petriManager.getTransitions()) {
            if (p.getId() == id)
                petriManager.setSelectedTrans(p);
        }
    }

    public void upgradePetri() {
        this.petriManager.upgradePetri();
    }

    public void addAvailableTransition(int id) {
        for (Transition p : petriManager.getTransitions()) {
            if (p.getId() == id)
                petriManager.addAvailableTransition(p);
        }
    }

    public Arc getArc(int placeId) {
        for (Transition t : petriManager.getTransitions()) {
            for (Arc a : t.getInArcs()) {
                if (a.getConnectedPlace().getId() == placeId)
                    return a;
            }

        }

        return null;
    }

    public void replaceArc(Arc oldArc,Arc newArc,int transitionId)
    {
        this.petriManager.getArcs().remove(oldArc);
        this.petriManager.getArcs().add(newArc);
        if (oldArc.isIn())
            this.getTransition(transitionId).replaceoutArc(oldArc,newArc);
        else
            this.getTransition(transitionId).replaceInArc(oldArc,newArc);
    }

    public boolean hasAvailableTrans(int id) {
        for (Transition p : petriManager.getTransitions()) {
            if (p.getId() == id)
                return petriManager.hasAvailableTrans(p);
        }
        return false;
    }

    public void checkIfDeleted(GraphicPetriNet graphicPetriNet)
    {
        for (Place p:this.petriManager.getPlaces())
        {
            boolean deleted=true;
            for(GraphicPlace graphicPlace:graphicPetriNet.getPlaces())
            {
                if (p.getId()==graphicPlace.getPlace().getId()) {
                    deleted=false;
                    break;
                }
            }
            if (deleted) {
                petriManager.getPlaces().remove(p);
                deleteArcsOfPlace(p);

            }
        }


    }
    //delete the arc connected to the p and update the transition connected to this arc
    private void deleteArcsOfPlace(Place p)
    {
        for(Arc arc:petriManager.getArcs())
        {
            if (arc.getConnectedPlace().getId()==p.getId()) {
                petriManager.getArcs().remove(arc);
                for (Transition transition:petriManager.getTransitions())
                {
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



    public List<Transition> getTransitions() {
        return petriManager.getTransitions();
    }

    public List<Place> getPlaces() {
        return petriManager.getPlaces();
    }


}
