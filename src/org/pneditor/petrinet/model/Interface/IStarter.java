package org.pneditor.petrinet.model.Interface;

import org.pneditor.petrinet.model.exceptions.ExceptionIllegalTransNum;

/**
 * Interface used to group the behavior of the petri net.
 * To use it the net must have already been created.
 */
public interface IStarter {

	/**
	 * Method use to start the analysis of the net and start calculating transitions. Do
	 * not use it unless the net is full.
	 */
	void start();

	/**
	 * Method used to verify if the conditions to start are true
	 * @return the availability of the  net to start
	 */
	boolean checkState();

	/**
	 * Each time there is a transition that has already fired use this method
	 * to change the structure of the net, it means, change the quantity of jetons in place
	 */
	void upgradePetri();

	/**
	 * Method used to create the first petriNet, if needed, use the @
	 * @param transtQ
	 */
	void createPetri(int transtQ) throws ExceptionIllegalTransNum;




	//void setPetri();
}
