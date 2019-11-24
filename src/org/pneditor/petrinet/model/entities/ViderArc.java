package org.pneditor.petrinet.model.entities;

import org.pneditor.petrinet.model.entities.Arc;

/**
 * Extends Arc. ViderArc is available only if the place connected is not empty.
 * It provides the method to clear the place connected.
 */
public class ViderArc extends Arc {
	
	
	
	public ViderArc(int weight) {
		super(weight);
	}



	/**
	 * ViderArc is available only if the place connected is not empty
	 * @return true if the place connected is not empty
	 */
	@Override
	public boolean isAvailable() {
		if (this.getConnectedPlace().getTokens()>0)
			return true;
		return false;
	}

	/**
	 * Remove all of the tokens in the place connected
	 */
	public void clearPlace() {
		this.getConnectedPlace().setTokens(0);
	}




}
