package org.pneditor.petrinet.model.control;


import org.pneditor.petrinet.model.entities.*;
import org.pneditor.petrinet.model.exceptions.ExceptionDeleteTokens;
import org.pneditor.petrinet.model.exceptions.ExceptionIllegalTransNum;
import org.pneditor.petrinet.model.Interface.IManager;
import org.pneditor.petrinet.model.Interface.IStarter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PetriManager implements IManager, IStarter {



	private List<Transition> transitions;
    private List<Arc> arcs;
    private List<Place> places;
    private Transition selectedTrans;
    private List<Transition> availableTrans;
    private final int DEFAULT_NEW_NET_PARAMETER = 1;

    private static PetriManager instance;
    public final static Logger LOGGER = Logger.getLogger(PetriManager.class.getName());


    private PetriManager() {
        transitions = new ArrayList<Transition>();
        arcs = new ArrayList<Arc>();
        places = new ArrayList<Place>();
        availableTrans = new ArrayList<Transition>();

    }

    public List<Transition> getTransitions() {
		return transitions;
	}

	public List<Arc> getArcs() {
		return arcs;
	}

	public List<Place> getPlaces() {
		return places;
	}

	public void setTransitions(List<Transition> transitions) {
		this.transitions = transitions;
	}

	public void setArcs(List<Arc> arcs) {
		this.arcs = arcs;
	}

	public void setPlaces(List<Place> places) {
		this.places = places;
	}
    /**
     * Singleton pattern in order to have only one instance of the petri net
     * @return
     */
    public static PetriManager getInstance() {
        if (instance == null) {
            instance = new PetriManager();
        }
        return instance;
    }


    /**
     * Method used to start the simulation, it stops when there is no more available transitions
     */
    @Override
    public void start() {
        this.checkState();
        while (!this.availableTrans.isEmpty()) {
            this.upgradePetri();
            this.upgradeSelectedTran();
        }
        LOGGER.info("There is no available transition");
    }


    /**
     * Method used to check if there is at least one available transition.
     * If not, recalculate all of the transitions and update the available transition list
     *
     * @return false    if there is no available transitions
     */
    @Override
    public boolean checkState() {
        if (availableTrans != null && !availableTrans.isEmpty())
            return true;
        for (Transition transition : this.transitions) {
            if (transition.isAvailable()) {
                this.availableTrans.add(transition);
            }
        }
        this.selectedTrans = this.availableTrans.get(0);
        if (!this.availableTrans.isEmpty())
            return true;
        else
            return false;
    }

    /**
     * Method used to active the selected transition, upgrade its places connected by its inArcs and outArcs
     */
    @Override
    public void upgradePetri() {
        if (selectedTrans == null)
            return;
        for (Arc in : selectedTrans.getInArcs()) {
            if (in instanceof ViderArc) {
                ((ViderArc) in).clearPlace();
                continue;
            }
            try {
                in.getConnectedPlace().deleteTokens(in.getWeight());
            } catch (ExceptionDeleteTokens exceptionDeleteTokens) {
                exceptionDeleteTokens.printStackTrace();
            }
        }
        for (Arc out : selectedTrans.getOutArcs()) {
            out.getConnectedPlace().addTokens(out.getWeight());
        }


    }


    /**
     * create the petri network using default configuration
     * (one transition pair contains two transitions, each of them has one in arc and one out arc)
     *
     * @param transtPairQ the quantity of transition pairs connected
     */
    @Override
    public void createPetri(int transtPairQ) throws ExceptionIllegalTransNum {
        if (transtPairQ <= 0) {
            throw new ExceptionIllegalTransNum("the number of transitions is negative during the creation of network");
        }

        for (int i = 0; i < transtPairQ; i++) {

            //create two transitions A and B, A is connected with B by a common place A2

            //Configuration for transition A which has one inArcA(weight 1, jeton 2) and one outArcA(weight 1 jeton 2)
            ArrayList<Arc> inA = new ArrayList<Arc>();
            ArrayList<Arc> outA = new ArrayList<Arc>();

            Place defaultPlaceA1 = new Place(2);
            Place defaultPlaceA2 = new Place(2);
            Arc inArcA = new Arc(1);
            inArcA.setConnectedPlace(defaultPlaceA1);
            inArcA.setIn(true);
            inA.add(inArcA);

            Arc outArcA = new Arc(1);
            outArcA.setConnectedPlace(defaultPlaceA2);
            outArcA.setIn(false);
            outA.add(outArcA);

            Transition transitionA = new Transition(inA, outA);
            this.addArc(inArcA);
            this.addArc(outArcA);
            this.addPlace(defaultPlaceA1);
            this.addPlace(defaultPlaceA2);
            this.addTransition(transitionA);
            System.out.println(transitionA);

            //Configuration for transition B which has one inArcB with weight 1, 2 jetons and connected to outArcA's place ,
            // and one outArcB(weight 1, jeton 2)
            ArrayList<Arc> inB = new ArrayList<Arc>();
            ArrayList<Arc> outB = new ArrayList<Arc>();
            Place defaultPlaceB = new Place(2);

            //transition B share the place B with A
            Arc inArcB = new Arc(1);
            inArcB.setConnectedPlace(defaultPlaceA2);
            inArcB.setIn(true);
            inB.add(inArcB);

            Arc outArcB = new Arc(1);
            outArcB.setConnectedPlace(defaultPlaceB);
            outArcB.setIn(false);
            ArrayList<Arc> out = new ArrayList<Arc>();
            outB.add(outArcB);

            Transition transitionB = new Transition(inB, outB);
            this.addArc(inArcB);
            this.addArc(outArcB);
            this.addPlace(defaultPlaceB);
            this.addTransition(transitionB);


        }


    }

    public void setSelectedTrans(Transition transition)
    {
        this.selectedTrans=transition;
    }

    @Override
    public void addTransition(Transition transition) {
        this.transitions.add(transition);

    }

    public void addAvailableTransition(Transition transition) {
        this.availableTrans.add(transition);

    }

    public boolean hasAvailableTrans(Transition t)
    {
        return availableTrans.contains(t);
    }

    @Override
    public void addArc(Arc arc) {
        this.arcs.add(arc);
    }

    @Override
    public void addPlace(Place place) {
        this.places.add(place);
    }

    
    @Override
    public void changeWeight(Arc arc, int weight) {
        arc.setWeight(weight);
    }

    @Override
    public void changeJetonsInPlace(Place place, int jetonsQ) {
        place.setTokens(jetonsQ);
    }


    /**
     * upgrade the available transition list and selected transition
     * after we active a transition
     */
    private void upgradeSelectedTran() {
        checkState();
        int lastSelectedId = this.availableTrans.indexOf(selectedTrans);

        //remove the selectedTrans from availableTrans only if it becomes unavailable
        if (!selectedTrans.isAvailable()) {
            this.availableTrans.remove(selectedTrans);
            //if there are still available transitions,
            //select the next transition of the old selected transition, it has the same id with the old one removed
            if (!this.availableTrans.isEmpty()) {
                this.selectedTrans = this.availableTrans.get(lastSelectedId);
                return;
            }
        }

        //if there are more available transitions and the old selected transition is still available
        if (!this.availableTrans.isEmpty()) {

            //if there are transitions which are not used
            if (this.availableTrans.size() > 1)
                //select the next transition of the old selected transition.
                //If the old selected transition is the last one of the list, use the first transition of the list
                this.selectedTrans = this.availableTrans.get((lastSelectedId + 1) % this.availableTrans.size());

                //if there is no transition unused
            else
                //reuse the old selected one
                this.selectedTrans = this.availableTrans.get(lastSelectedId);
        }

    }


    public static void main(String[] args) {
        PetriManager petri = PetriManager.getInstance();

        try {
            petri.createPetri(1);
        } catch (ExceptionIllegalTransNum exceptionIllegalTransNum) {
            exceptionIllegalTransNum.printStackTrace();
        }
        petri.start();
    }


}
