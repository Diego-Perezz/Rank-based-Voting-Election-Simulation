package main;


/**
 * The Candidate class represents a candidate participating in an election.
 * It holds information such as the candidate's ID, name, rank, and active status.
 */
public class Candidate {

	/** The unique identifier of the candidate. */
	int Id;
	
	/** The name of the candidate. */
	String Name;
	
	/** The rank of the candidate. */
	int Rank;
	
	/** Indicates whether the candidate is active in the election. */
	boolean active;
	

	/** Generates a Candidate default Constructor
	*/
	public Candidate() {
		this.active = true;
		this.Name = "";
		this.Id = 0;
		this.Rank = 0;
	}

	/** Creates a Candidate from the line. The line will have the format
	ID#,candidate_name . */
	public Candidate(String line) {

		this.active = true;
		
		String[] info = line.split(",");
		
		this.Id = Integer.parseInt(info[0]);
		this.Name = info[1];
	}

	public Candidate(int id, String name,int rank) {
		this.active = true;
		this.Name = name;
		this.Id = id;
		this.Rank = rank;
	}
	
	


    /**
     * Retrieves the unique identifier of the candidate.
     *
     * @return The ID of the candidate.
     */
	public int getId() {
		return this.Id;
	}
	
	
	 /**
     * Checks whether the candidate is active in the election.
     *
     * @return True if the candidate is active, otherwise false.
     */
	public boolean isActive() {
		return this.active;
	}
	
	
	 /**
     * Retrieves the name of the candidate.
     *
     * @return The name of the candidate.
     */
	public String getName() {
		return this.Name;
	}
	
    /**
     * Retrieves the rank of the candidate.
     *
     * @return The rank of the candidate.
     */
	public int getRank() {
		return this.Rank;
	}
	
    /**
     * Sets the unique identifier of the candidate.
     *
     * @param id The ID to set for the candidate.
     */
	public void setId(int id) {
		this.Id = id;
	}
	
	/**
     * Sets the name of the candidate.
     *
     * @param name The name to set for the candidate.
     */
	public void setName(String name) {
		this.Name = name;
	}
	
    /**
     * Sets the rank of the candidate.
     *
     * @param rank The rank to set for the candidate.
     */
	public void setRank(int rank) {
		this.Rank = rank;
	}
	
	
    /**
     * Sets the active status of the candidate.
     *
     * @param active The active status to set for the candidate.
     */
	public void setActive(boolean active) {
		this.active = active;
	}

	
}
