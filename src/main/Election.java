package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;

import data_structures.ArrayList;
import interfaces.List;


public class Election {
	//uses constructor inserting a rank = 0
	private List<Candidate> candidatos;
	private List<Ballot> papeletas;
	private ArrayList<ArrayList<Ballot>> papeletas_by_candidato;
	private List<String> eliminatedCandidates;
	
	
   /** Generates a default Constructor
	*/
	public Election() {
		this("candidates.csv", "ballots.csv");
	}
	
   /** Constructor that receives the name of the candidate and ballot files and applies
	* the election logic. Note: The files should be found in the input folder. 
	*/
	public Election(String candidates_filename, String ballot_filename) {
		this.candidatos = new ArrayList<Candidate>();
		try{
			BufferedReader reader_candidates = new BufferedReader(new FileReader("inputFiles/" + candidates_filename));
			String data = "";
			while((data = reader_candidates.readLine()) != null) {
				this.candidatos.add( new Candidate(data) );
			}
			reader_candidates.close();
		}catch(IOException e){
			System.out.println("Problema reading candidates, from:" + candidates_filename);
			e.printStackTrace();
		}

		this.papeletas = new ArrayList<Ballot>();
		try{
			BufferedReader reader_ballots = new BufferedReader(new FileReader("inputFiles/" + ballot_filename));
			String data2 = "";
			while((data2 = reader_ballots.readLine()) != null) {
				this.papeletas.add( new Ballot(data2,this.candidatos) );
			}
			reader_ballots.close();
		}catch(IOException e){
			System.out.println("Problema reading ballots, from:" + ballot_filename);
			e.printStackTrace();
		}

		this.eliminatedCandidates = new ArrayList<String>( this.candidatos.size() );
		fill_papeletas_by_candidato();
	}
	
	//called on constructor to fill the list of lists
	private void fill_papeletas_by_candidato(){
		this.papeletas_by_candidato = new ArrayList<ArrayList<Ballot>>( this.candidatos.size() );
		for (int i = 0; i < this.candidatos.size();i++) {
			this.papeletas_by_candidato.add(new ArrayList<Ballot>( this.papeletas.size() ));
		}	                                                
		for (int i = 0; i < this.papeletas.size();i++) {
			if (this.papeletas.get(i).getBallotType() == 0) {
				int id_de_ganador_de_papeleta = this.papeletas.get(i).getCandidateByRank(1);		
					this.papeletas_by_candidato.get(-1 + id_de_ganador_de_papeleta).add(this.papeletas.get(i));									
			}
		}
	}











	
	//============= functions to iterate, count and get winner =============/
	
	
	
    /**
     * Determines the winner of the election and writes the result to a file.
     * 
     * @return The name of the election winner.
     */
	public String getWinner() {
		int shiftRound = 0;
		

		printDistributionFirstsOnRound(shiftRound, "Nobody, initial condition of lol.");
		int winner_index = get_index_of_winner();
		while(winner_index == -1){
			shiftRound++;
			String goneOnThisRound = eliminateSomeone(shiftRound);
			//////////////////////////////////////////////////////
			this.printBallotDistribution();
			printDistributionFirstsOnRound(shiftRound, goneOnThisRound);
			winner_index = get_index_of_winner();
		}

		System.out.println();
		System.out.println("The winner is: " + this.candidatos.get(winner_index).getName() + "!!!!");
		
		int totalOnesofWinner = this.papeletas_by_candidato.get(winner_index).size();
		String NameofWinnerWithOnes = this.candidatos.get(winner_index).getName().toLowerCase().replaceAll(" ", "_") + totalOnesofWinner + ".txt";
		
	
		try {
			FileWriter file = new FileWriter("outputFiles/" + NameofWinnerWithOnes);
			BufferedWriter write = new BufferedWriter(file);
			
			write.write("Number of ballots: " + this.getTotalBallots() + "\n");
			write.write("Number of blank ballots: " + this.getTotalBlankBallots() + "\n");
			write.write("Number of invalid ballots: " + this.getTotalInvalidBallots() + "\n");
			for (int i = 0; i < this.eliminatedCandidates.size(); i++){
				write.write("Round " + (i + 1) + ": " + this.eliminatedCandidates.get(i).substring(0, this.eliminatedCandidates.get(i).indexOf("-")) + " was eliminated with "  + Integer.parseInt( this.eliminatedCandidates.get(i).substring(this.eliminatedCandidates.get(i).indexOf("-") + 1)) + " #1's" + "\n");
			}
			write.write("Winner: " + this.candidatos.get(winner_index).getName() + " wins with " + totalOnesofWinner + " #1's");
			write.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return this.candidatos.get(winner_index).getName();
	}

	//based on list of list, reports 1st position ballot distribution
	//after "n" rounds of 1st vote shifting
	private void printDistributionFirstsOnRound(int round, String goneOnThisRound){
		System.out.println("Shifting round: " + round + " - Eliminated on this round: " + goneOnThisRound);
		String un_candidato = "";
		int papeletas_ganadas = 0;
		double percent = 0.0;
		for (int i = 0; i < this.papeletas_by_candidato.size();i++) {
			un_candidato = this.candidatos.get(i).getName() + "\t";
			papeletas_ganadas = this.papeletas_by_candidato.get(i).size();
			percent = 100*(double)papeletas_ganadas / (double)getTotalValidBallots();
			System.out.println(un_candidato + " rank=1, en: " + papeletas_ganadas + " papeletas. [" + percent + " %]");
		}
		System.out.println("Eliminated so far: " + this.eliminatedCandidates.toString());
	}

	/**
	 * Finds the index of the winner based on the number of ballots won by each candidate.
	 * The winner is determined by having more than 50% of the valid ballots.
	 * 
	 * @return The index of the winner candidate, or -1 if there is no winner yet.
	 */
	private int get_index_of_winner(){
		int winner_index = -1;
		int papeletas_ganadas = 0;
		double percent = 0.0;
		for (int i = 0; i < this.papeletas_by_candidato.size();i++) {
			papeletas_ganadas = this.papeletas_by_candidato.get(i).size();
			percent = 100*(double)papeletas_ganadas / (double)getTotalValidBallots();
			if(percent > 50.0){
				winner_index = i;
				break;
			}	
		}
		return winner_index;
	}

	/**
	 * Eliminates a candidate from the election based on the number of ballots won by each candidate.
	 * If there's a tie among candidates with the least number of won ballots, a tie-breaking mechanism is applied.
	 * 
	 * @param round The current round of elimination.
	 * @return A string representing the name of the eliminated candidate and the number of first-place votes they received.
	 */
	private String eliminateSomeone(int round){
		String herNameAndNumberOfFirsts = "xxx-5";
		int indexOfPoorCandidate = 0;
		int lawb = leastAmountWonBallots();
		List<Integer> clawb = candidatesWithLeastAmountOfWonBallots( lawb );
		if(clawb.size() > 1){//we have a tie
			System.out.println("\n\nRound: "  + round + "\tReceived tie of " + clawb.size() + " candidates from previous round ");
			System.out.println("Amount of 1-rank votes to redistribute from previous round = " + lawb + "\t\tIndexes of those candidates with those votes: " + clawb.toString());		
			indexOfPoorCandidate = TieFunction( clawb );
			System.out.println("To break tie, go ahead and eliminate index = " + indexOfPoorCandidate + "  [" + this.candidatos.get(indexOfPoorCandidate).getName() + "]" );
		}else if(clawb.size() == 1){//we have someone to eliminate
			indexOfPoorCandidate = clawb.get(0);//first index
			System.out.println("\n\nRound: "  + round + "\tWill eliminate index " +  indexOfPoorCandidate + " without ties." + "  [" + this.candidatos.get(indexOfPoorCandidate).getName() + "]" );
			System.out.println("Amount of 1-rank votes to redistribute from previous round = " + lawb + "\t\tIndex of candidate with those votes: " + clawb.toString());
		}else{//bug if i get here, i should have found winner before getting here
			; //an Exception here?!?!
		}
		herNameAndNumberOfFirsts = this.candidatos.get(indexOfPoorCandidate).getName() + "-" + Integer.toString( this.papeletas_by_candidato.get(indexOfPoorCandidate).size() );
		this.eliminatedCandidates.add(herNameAndNumberOfFirsts);
		this.candidatos.get(indexOfPoorCandidate).setActive(false);
		eliminateHerOnAllHerBallots( indexOfPoorCandidate );
		reasignAllHisBallots( indexOfPoorCandidate );		
		return herNameAndNumberOfFirsts;
	}

	/**
	 * Determines the minimum number of won ballots among non-eliminated candidates.
	 * 
	 * @return The minimum number of won ballots.
	 */
	private int leastAmountWonBallots(){
		int lawb = this.papeletas.size();
		String nameOfCandidate = "";
		for (int i = 0; i < this.papeletas_by_candidato.size();i++) {
			nameOfCandidate = this.candidatos.get(i).getName(); 
			if( this.papeletas_by_candidato.get(i).size() < lawb && this.eliminatedCandidates.toString().indexOf( nameOfCandidate ) == -1 ){
				lawb = this.papeletas_by_candidato.get(i).size();
			}
		}		
		return lawb;
	}

	
	
	/**
	 * Determines the set of candidates who won the minimum amount of ballots.
	 * 
	 * @param lawb The minimum number of won ballots among candidates.
	 * @return The list of indexes of candidates with the minimum number of won ballots.
	 */
	private List<Integer> candidatesWithLeastAmountOfWonBallots( int lawb ){
		ArrayList<Integer> clawb = new ArrayList<Integer>(this.candidatos.size());
		
		for (int i = 0; i < this.papeletas_by_candidato.size();i++) {
			if( lawb == this.papeletas_by_candidato.get(i).size()){
				clawb.add(i);
			}
		}
		return clawb;
	}
	

	
	/**
	 * Breaks a tie between candidates with the same number of won ballots.
	 * 
	 * @param clawb The list of indexes of candidates with the same number of won ballots.
	 * @return The index of the candidate to be eliminated to break the tie.
	 */
	private int TieFunction(List<Integer> clawb){
		
		ArrayList<Integer> Ids_Of_candidates_w_tie = new ArrayList<Integer>(clawb.size());
		for (int j = 0; j < clawb.size(); j++) {
			Ids_Of_candidates_w_tie.add(clawb.get(j) + 1);
		}
		
		
		int IdTodelete = Ids_Of_candidates_w_tie.get(Ids_Of_candidates_w_tie.size() - 1);
		
		int rank = 2;
		boolean todosEmpate = true;
		while (rank <= this.candidatos.size() && todosEmpate) {
			int menor = getCountofRanks(IdTodelete, rank);
			
			for (int i = Ids_Of_candidates_w_tie.size() - 2; i > -1; i--) {
		
				int COR_actual_candidate = getCountofRanks(Ids_Of_candidates_w_tie.get(i),rank);
				
				if (COR_actual_candidate < menor) {
					menor = COR_actual_candidate;
					todosEmpate = false;
					IdTodelete = Ids_Of_candidates_w_tie.get(i);
				}
				else if (COR_actual_candidate > menor) {
					todosEmpate = false;
				}
				else if (COR_actual_candidate == menor) {
					
				}
			}
		rank++;
		}

		return -1+IdTodelete;
	}

	
	
	/**
	 * Counts the number of ballots where a candidate has a specific rank.
	 * 
	 * @param id   The ID of the candidate.
	 * @param rank The rank to count.
	 * @return The count of ballots where the candidate has the specified rank.
	 */
	public int getCountofRanks(int id, int rank) {
		int count = 0;
		for (int i = 0; i < this.papeletas.size(); i++) {
			if (this.papeletas.get(i).getRankByCandidate(id) == rank) {
				count++;
			}
		}
		return count;
	}

	
	/**
	 * Eliminates the specified candidate from all their ballots and reassigns the ballots to other candidates.
	 * 
	 * @param herIndex The index of the candidate to be eliminated.
	 */
	private void eliminateHerOnAllHerBallots( int herIndex ){
		int herId = 1+herIndex;
		for(int i=0; i < this.papeletas_by_candidato.get(herIndex).size(); i++){
			this.papeletas_by_candidato.get(herIndex).get(i).eliminate(herId);

			//if at this point the ballot would belong to an eliminated candidate...
			//keep eliminating from this ballot, the candidate in rank 1

			// generate a nameOfWhoGetsTheBallot, it controls the loop 
			int idOfWhoGetsTheBallot = this.papeletas_by_candidato.get(herIndex).get(i).getCandidateByRank(1);
			int indexOfWhoGetsTheBallot = -1 + idOfWhoGetsTheBallot; 
			String nameOfWhoGetsTheBallot = this.candidatos.get( indexOfWhoGetsTheBallot ).getName();
			while(this.eliminatedCandidates.toString().indexOf( nameOfWhoGetsTheBallot )  > 0){ // eliminated candidate condition
				this.papeletas_by_candidato.get(herIndex).get(i).eliminate( idOfWhoGetsTheBallot );
				//  regenerate a nameOfWhoGetsTheBallot 
				idOfWhoGetsTheBallot = this.papeletas_by_candidato.get(herIndex).get(i).getCandidateByRank(1);
				indexOfWhoGetsTheBallot = -1 + idOfWhoGetsTheBallot; 
				nameOfWhoGetsTheBallot = this.candidatos.get( indexOfWhoGetsTheBallot ).getName();
			}
		}
	}	

	
	
	/**
	 * Reassigns all ballots belonging to the eliminated candidate to other candidates.
	 * 
	 * @param hisIndex The index of the candidate whose ballots are to be reassigned.
	 */
	private void reasignAllHisBallots( int hisIndex ){
		int idOfWhoGetsTheBallot = 0;
		int indexOfWhoGetsTheBallot = 0;
		for(int i=0; i < this.papeletas_by_candidato.get(hisIndex).size(); i++){
			idOfWhoGetsTheBallot = this.papeletas_by_candidato.get(hisIndex).get(i).getCandidateByRank(1);
			indexOfWhoGetsTheBallot = -1 + idOfWhoGetsTheBallot;
			this.papeletas_by_candidato.get(indexOfWhoGetsTheBallot).add(  this.papeletas_by_candidato.get(hisIndex).get(i)  );
		}
		this.papeletas_by_candidato.get(hisIndex).clear();
	}















	//===============  initial status  functions ==================/

	// returns the total amount of ballots submitted
	public int getTotalBallots() {
		return this.papeletas.size();
	}
	
	// returns the total amount of invalid ballots 
	public int getTotalInvalidBallots() {
		int count = 0;
		for (int i = 0; i < this.papeletas.size(); i++) {
			if (this.papeletas.get(i).getBallotType() == 2) {
				count++;
			}
		}
		return count;
	}
	
	// returns the total amount of blank ballots 
	public int getTotalBlankBallots() {
		int count = 0;
		for (int i = 0; i < this.papeletas.size(); i++) {
			if (this.papeletas.get(i).getBallotType() == 1) {
				count++;
			}
		}
		return count;
	}
	
	// returns the total amount of valid ballots public 
	public int getTotalValidBallots() {
		int count = 0;
		for (int i = 0; i < this.papeletas.size(); i++) {
			if (this.papeletas.get(i).getBallotType() == 0) {
				count++;
			}
		}
		return count;
	}
	
	/**
	* Prints all the general information about the election as well as a * table with the vote distribution.
	* Meant for helping in the debugging process.
	*/
	public void printBallotDistribution() {
		//System.out.println();
		System.out.println("Total ballots:" + getTotalBallots()); 
		System.out.println("Total blank ballots:" + getTotalBlankBallots());
		System.out.println("Total invalid ballots:" + getTotalInvalidBallots()); 
		System.out.println("Total valid ballots:" + getTotalValidBallots()); 
		//System.out.println("Eliminated so far:" + getEliminatedCandidates());
		for(Candidate c: this.candidatos) {
			System.out.print(c.getName().substring(0, c.getName().indexOf(" ")) + "\t"); 
			for(Ballot b: this.papeletas) {
				int rank = b.getRankByCandidate(c.getId());
				String tableline = "| " + ((rank != -1) ? rank: "x") + " "; 
				System.out.print(tableline);
				
			}
			System.out.println("|"); 
		}
	}





	//===============  final status  functions ==================/
	
	
	
	/** List of names for the eliminated candidates with the numbers 
	of 1s they had, must be in order of elimination. 
	Format should be <candidate name>-<number of 1s when eliminated>
	*/
	public List<String> getEliminatedCandidates(){
		return this.eliminatedCandidates; 
	}





		
	//===============  bonus functions ==================/
	
	
	
	
	/**
	 * Prints information about each candidate using the provided function.
	 * 
	 * @param func A function that transforms a Candidate object into a String representation.
	 */
	public void printCandidates(Function<Candidate, String> func) {
		CandidateToStr printCandidates = (c) -> c.getId() + " " + c.getName() + "  [" +   (c.isActive() ? "act" : "Na")  +  "]";
		activeCandidateStrCreator(this.candidatos, printCandidates);
	}

	public interface CandidateToStr{
		public String creaStr(Candidate c);
	}

	public void activeCandidateStrCreator(List <Candidate> cList, CandidateToStr cToStr){
		for(Candidate c: cList){
			System.out.println( cToStr.creaStr( c ) );
		}
	}

	
	/**
	 * Counts the number of valid ballots that satisfy the provided condition.
	 * 
	 * @param func A function that determines whether a Ballot object meets a specific condition.
	 * @return The number of valid ballots that meet the condition.
	 */
	public int countBallots(Function<Ballot, Boolean> func) {
		
        int count = 0;
        for (Ballot ballot : this.papeletas) {
            if (func.apply(ballot) && ballot.getBallotType() == 0) {
                count++;
            }
        }
        return count;

	}
	
}


