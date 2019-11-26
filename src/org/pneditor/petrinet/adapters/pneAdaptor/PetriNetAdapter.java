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

    public Place getPlace(AbstractPlace place) {
        for (Place p : petriManager.getPlaces()) {
            if (p.getId() == place.getId())
                return p;
        }
        return null;

    }

    public Transition getTransition(AbstractTransition transition) {
        for (Transition t : petriManager.getTransitions()) {
            if (t.getId() == transition.getId()) {
                return t;
            }
        }
        return null;
    }

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

    public void replaceArc(Arc oldArc, Arc newArc, int transitionId) {
        this.petriManager.getArcs().remove(oldArc);
        this.petriManager.getArcs().add(newArc);
        if (oldArc.isIn())
            this.getTransition(transitionId).replaceoutArc(oldArc, newArc);
        else
            this.getTransition(transitionId).replaceInArc(oldArc, newArc);
    }

    public boolean hasAvailableTrans(int id) {
        for (Transition p : petriManager.getTransitions()) {
            if (p.getId() == id)
                return petriManager.hasAvailableTrans(p);
        }
        return false;
    }

    public void checkIfDeleted(GraphicPetriNet graphicPetriNet) {
        Iterator<Place> itPlace = this.petriManager.getPlaces().iterator();
        while (itPlace.hasNext()) {
            Place p = itPlace.next();
            boolean deleted = true;
            for (GraphicPlace graphicPlace : graphicPetriNet.getPlaces()) {
                if (p.getId() == graphicPlace.getPlace().getId()) { //todo: replace for the getPLace(AbstractPlace):Place
                    deleted = false;
                    break;
                }
            }
            if (deleted) {
                itPlace.remove();
                deleteArcsOfPlace(p);

            }
        }

        Iterator<Transition> itTransition = this.petriManager.getTransitions().iterator();
        while (itTransition.hasNext()) {
            Transition t = itTransition.next();
            boolean deleted = true;
            for (GraphicElement graphicTransition : graphicPetriNet.getElements()) {
                if (graphicTransition instanceof GraphicTransition) {
                    if (t.getId() == ((GraphicTransition) graphicTransition).getTransition().getId()) {
                        deleted = false;
                        break;
                    }
                }
            }
            if (deleted) {
                itTransition.remove();
                deleteArcsOfTransition(t);

            }
        }

        Iterator<Arc> itArc = this.petriManager.getArcs().iterator();
        while (itArc.hasNext()) {
            Arc arc = itArc.next();
            boolean deleted = true;
            for (GraphicElement graphicArc : graphicPetriNet.getElements()) {
                if (graphicArc instanceof GraphicArc) {
                    if (((GraphicArc) graphicArc).getSource().isPlace()) {
                        if (((GraphicPlace) ((((GraphicArc) graphicArc).getSource()))).getPlace().getId() == arc.getConnectedPlace().getId()) {
                            deleted = false;
                            break;
                        }
                    }
                    if (((GraphicArc) graphicArc).getSource().isTransition() && arc.isIn()) {
                        int modelPlaceId = arc.getConnectedPlace().getId();
                        int grapicPlaceId = ((GraphicPlace) ((((GraphicArc) graphicArc).getDestination()))).getPlace().getId();
                        if (modelPlaceId == grapicPlaceId) {
                            deleted = false;
                            break;
                        }
                    }
                }
            }
            if (deleted) {
                itArc.remove();
                updatedConnectedTranst(arc);
            }
        }


    }

    //delete the arc connected to the p and update the transition connected to this arc
    private void deleteArcsOfPlace(Place p) {
        Iterator<Arc> iteratorArc = petriManager.getArcs().iterator();
        while (iteratorArc.hasNext()) {
            Arc arc = iteratorArc.next();
            if (arc.getConnectedPlace().getId() == p.getId()) {
                iteratorArc.remove();
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

    private void deleteArcsOfTransition(Transition transition) {
        for (Arc arc : transition.getInArcs()) {
            petriManager.getArcs().remove(arc);
        }
        for (Arc arc : transition.getOutArcs()) {
            petriManager.getArcs().remove(arc);
        }

    }

    private void updatedConnectedTranst(Arc arc) {
        for (Transition transition : petriManager.getTransitions()) {
            if (transition.getInArcs().contains(arc))
                transition.getInArcs().remove(arc);
            if (transition.getOutArcs().contains(arc))
                transition.getOutArcs().remove(arc);
        }

    }


    public List<Transition> getTransitions() {
        return petriManager.getTransitions();
    }

    public List<Place> getPlaces() {
        return petriManager.getPlaces();
    }

    public boolean comparePetri(GraphicPetriNet editorNet) throws ResetArcMultiplicityException {
        if (editorNet.getPetriNet().getPlaces().size() != this.getPlaces().size()) {
            comparePlaces(editorNet).forEach(missingPlace -> addPlace(missingPlace));
            compareArcs(editorNet).forEach(missingArc -> addArc(missingArc));
            compareTransitions(editorNet).forEach(missingTransition -> addTransition(missingTransition));
            return comparePetri(editorNet);
        } else {
            if (comparePlaces(editorNet).isEmpty() && compareTransitions(editorNet).isEmpty() && compareArcs(editorNet).isEmpty()) {
                return true;
            }
        }
        return comparePetri(editorNet);
    }

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

    public void createPlace(AbstractPlace editorPlace) {
        Place place = new Place(editorPlace.getTokens());
        place.setId(editorPlace.getId());
        this.addPlace(place);
    }

    public void createTransition(AbstractTransition editorTransition) {
        Transition transition = new Transition();
        transition.setId(editorTransition.getId());
        this.addTransition(transition);
    }

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
