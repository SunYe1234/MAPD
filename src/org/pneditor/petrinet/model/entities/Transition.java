package org.pneditor.petrinet.model.entities;

import org.pneditor.petrinet.model.entities.Arc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Entity class of transition
 */
public class Transition {

    private List<Arc> inArcs;
    private List<Arc> outArcs;
    private boolean available;
    private int id;

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Arc> getInArcs() {
        return inArcs;
    }

    public void setInArcs(List<Arc> inArcs) {
        this.inArcs = inArcs;
    }

    public List<Arc> getOutArcs() {
        return outArcs;
    }

    public void setOutArcs(List<Arc> outArcs) {
        this.outArcs = outArcs;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Transition(List inArcs, List outArcs) {
        super();
        this.inArcs = inArcs;
        this.outArcs = outArcs;
    }

    public Transition() {
		super();
	}

	public void addInArc(Arc arc) {
        if (this.inArcs==null)
            this.inArcs=new ArrayList<Arc>();
        this.inArcs.add(arc);
    }

    public void replaceInArc(Arc oldArc, Arc newArc)
    {
        this.inArcs.remove(oldArc);
        this.inArcs.add(newArc);
    }

    public void addOutArc(Arc arc) {
        if (this.outArcs==null)
            this.outArcs=new ArrayList<Arc>();
        this.outArcs.add(arc);
    }
    public void replaceoutArc(Arc oldArc, Arc newArc)
    {
        this.outArcs.remove(oldArc);
        this.outArcs.add(newArc);
    }

    public boolean isAvailable() {
        return this.getInArcs().stream().anyMatch(Arc::isAvailable);
    }

    public void setArcWeight(Arc arc, int weight) {
        if (arc.isIn())
            inArcs.get(inArcs.indexOf(arc)).setWeight(weight);
        else
            outArcs.get(outArcs.indexOf(arc)).setWeight(weight);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transition that = (Transition) o;
        return available == that.available &&
                id == that.id &&
                inArcs.equals(that.inArcs) &&
                outArcs.equals(that.outArcs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inArcs, outArcs, available, id);
    }
}
