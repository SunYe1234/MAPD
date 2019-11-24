package org.pneditor.petrinet.model.entities;

import org.pneditor.petrinet.model.control.PetriManager;

/**
 * Extends Class Arc. It's available only if the place connected is empty
 */
public class ZeroArc extends Arc {
	
	
	
	public ZeroArc(int weight) {
		super(weight);
	}




	public boolean isPlaceEmpty() {
		return this.getConnectedPlace().getTokens()==0;
	}

	/**
	 * If zeroarc is a in-arc, it's available only if the place connected is empty, that means it do nothing as a in-arc
	 *
	 * @return
	 */
	@Override
	public boolean isAvailable() {
		PetriManager.LOGGER.warning("There is a zero arc on this transition");
		if (this.isIn())
			return false;
		return true;
	}
	

	
}
