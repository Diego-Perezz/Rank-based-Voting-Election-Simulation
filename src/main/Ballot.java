package main;

import data_structures.ArrayList;
import interfaces.List;


/**
 * The Ballot class represents a ballot used in an election.
 * Each ballot has a number to identify it, a list of candidates with ranks, and a type indicating its validity.
 */
public class Ballot {
	 /** The number of the ballot. */
	int ballotNumber;
	
	/** The list of candidates with ranks on the ballot. */
	private List <Candidate> candidates_w_ranks;
	
	/** Represents the type of the ballot: 0 for valid, 1 for blank, 2 for invalid. */
	int ballottype;
	

	
	/** Generates a Ballot default Constructor
	*/
	public Ballot() {
		this.ballotNumber = 0;
		this.candidates_w_ranks = null;
	}
	
    /**
     * Constructs a new Ballot object based on the provided line and list of candidates.
     * The line format should be ballot#,c1:1,c2:2,...,ck:k
     * @param line The string containing ballot information.
     * @param candidates The list of all candidates in the election.
     */
	public Ballot(String line, List<Candidate> candidates) {
		ballottype = 0;
		
		this.candidates_w_ranks = new ArrayList<Candidate>();
		
		String[] ballotinfo = line.split(",");
	
		this.ballotNumber = Integer.parseInt(ballotinfo[0]);
		
		for (int i = 1; i < ballotinfo.length; i++) {
			
			String[] ID_rank = ballotinfo[i].split(":");
			
			Integer ID = Integer.parseInt(ID_rank[0]);
			Integer rank = Integer.parseInt(ID_rank[1]);
	
			for (int j = 0; j < candidates.size(); j++) {
				if (candidates.get(j).getId() == ID) {
					candidates_w_ranks.add(new Candidate(ID,candidates.get(j).getName(),rank));
					break;
				}
			}
			
			
		}

		
		// Ballot type logic 
		
		if (candidates_w_ranks.isEmpty()) {
			ballottype = 1;
		}
		
		for (int i = 0; i < candidates_w_ranks.size();i++) {
			
			if (candidates_w_ranks.get(i).getRank() > candidates_w_ranks.size()) {
				
				ballottype = 2;
			}
			if ((candidates_w_ranks.get(i).getRank()) != (i + 1)) {
				ballottype =  2;
			}
				
			for (int j = i+1; j < candidates_w_ranks.size();j++) {
				if (candidates_w_ranks.get(j).getId() == candidates_w_ranks.get(i).getId() 
					|| candidates_w_ranks.get(j).getRank() == candidates_w_ranks.get(i).getRank()) {
					ballottype = 2;
				}
				
			}
		}
	}

	
    /**
     * Retrieves the number of the ballot.
     * @return The number of the ballot.
     */
	public int getBallotNum() {
		
		return this.ballotNumber;
		
	}
	
	
    /**
     * Retrieves the rank of the candidate with the given ID on the ballot.
     * @param candidateID The ID of the candidate.
     * @return The rank of the candidate, or -1 if the candidate is not found or inactive.
     */
	public int getRankByCandidate(int candidateID) {
		
		for (int i = 0; i < candidates_w_ranks.size(); i++) {
			if (candidates_w_ranks.get(i).getId() == candidateID && candidates_w_ranks.get(i).isActive()) {
				return candidates_w_ranks.get(i).getRank();
			}
		}
		return -1;
		
	}
	
	
    /**
     * Retrieves the ID of the candidate with the given rank on the ballot.
     * @param rank The rank of the candidate.
     * @return The ID of the candidate, or -1 if the candidate is not found or inactive.
     */

	public int getCandidateByRank(int rank) {
		for (int i = 0; i < candidates_w_ranks.size(); i++) {
			if (candidates_w_ranks.get(i).getRank() == rank && candidates_w_ranks.get(i).isActive()) {
				return candidates_w_ranks.get(i).getId();
			}
		}
		return -1;
	
		
	}
	
	
    /**
     * Eliminates the candidate with the given ID from the ballot.
     * @param candidateId The ID of the candidate to be eliminated.
     * @return true if the candidate is found and eliminated, false otherwise.
     */
	public boolean eliminate(int candidateId) {
		int actual_rank = candidates_w_ranks.size();
		boolean found = false;
		
		for (int i = 0; i < candidates_w_ranks.size(); i++) {
			if (candidates_w_ranks.get(i).getId() == candidateId) {
				candidates_w_ranks.get(i).setActive(false);
				actual_rank = candidates_w_ranks.get(i).getRank();
				found = true;
				break;
			}
		}
		
		for (int i = 0; i < candidates_w_ranks.size(); i++) {
			if (candidates_w_ranks.get(i).getRank() > actual_rank) {
				candidates_w_ranks.get(i).setRank(candidates_w_ranks.get(i).getRank() - 1);
			}
		}
		
		return found;
		
		
	}
	
	
    /**
     * Retrieves the type of the ballot.
     * @return An integer indicating the type of the ballot: 0 for valid, 1 for blank, 2 for invalid.
     */
	public int getBallotType() {
		
		return this.ballottype;
		
	}
	


	
	
}
