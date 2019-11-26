package org.pneditor.petrinet.model.entities;

import org.pneditor.petrinet.model.exceptions.ExceptionDeleteTokens;


/**
 * Entity class of Place, it contains tokens and provides the methods to add and delete its tokens
 */
public class Place {
	
	private int tokens;
	private int id;

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Place(int tokens) {
		super();
		this.tokens = tokens;
	}

	public Place(int tokens, int neededJetons) {
		super();
		this.tokens = tokens;
	}

	public int getTokens() {
		return tokens;
	}

	public void setTokens(int tokens) {
		this.tokens = tokens;
	}



	public void addTokens(int quantite) {
		this.tokens+=quantite;
		
	}
	
	public void deleteTokens(int quantite) throws ExceptionDeleteTokens {
		if (quantite>this.tokens)
			throw new ExceptionDeleteTokens("Trying to delete more tokens than the palce actually has");
		this.tokens-=quantite;
	}
}
