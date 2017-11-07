package project1;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class Master implements Runnable 
{
	class Parents{
		private int wt;
		private int parent;
		public Parents(int parent,int wt){
			this.parent=parent;
			this.wt=wt;
		}
	}
	volatile Map<Integer, Boolean> roundTracker_map = new HashMap<>(); 
	volatile Map<Integer, Parents> parentTracker_map = new HashMap<>();
	int totalProcesses;
	int roundCount = 0;
	boolean isComplete = false;
	public static volatile boolean MSTcomplete=false;
	Process process[];
	
	
	//Master Constructor
	public Master(int totalProcesses) 
	{
		this.totalProcesses = totalProcesses;
		int i=0;
		while(i<totalProcesses)
		{
			int tempI=Bellman.nodesList.get(i);
			roundTracker_map.put(tempI, false);
			i++;
		}
	}
	
	//Begin round
	void begin_Round() 
	{
		int i = 0;
		while(i < totalProcesses)
		{
			process[i].setMsg("Begin_Round");
			i++;
		}
	}

	// Start/Commence
	void commence() 
	{
		int i=0;
		while(i < totalProcesses)
		{
			process[i].setMsg("Start");
			i++;
		}
	}

	// Send Completed message
	void terminate() 
	{
		int i=0;
		while(i < totalProcesses)
		{
			process[i].setMsg("Completed");
			i++;
		}
	}
	
	// Request for Parent Process for each Process for building the shortest path tree
	 void request_Parents() 
	{
		int i=0;
		while(i < totalProcesses)
		{
			process[i].setMsg("getParent");
			i++;
		}
		
	}

	// roundCompletion information into hash maps
	public synchronized void roundCompletion(int id) 
	{
		roundTracker_map.put(id, true);
	}
	
	// Set parents
	public synchronized void setParents(int id, int parent_Process, int weight) 
	{

		Parents p =new Parents(parent_Process,weight);
		parentTracker_map.put(id,p);
	}
	
	
	public void run() 
	{
		while (!isRoundComplete()) {}
		roundConfirmation(); 
		commence();
		while (!isRoundComplete()) {}
		roundConfirmation(); 
		begin_Round();
		while (!isComplete) 
		{
			while (!isRoundComplete()) {}
			while(!MSTcomplete) 
			{
				roundConfirmation(); 
				begin_Round();
				while (!isRoundComplete()){}
			}
			 
			roundConfirmation(); request_Parents();
			while (!isRoundComplete()) {}
			MSTree(); 
			terminate();
			isComplete = true;
			
		}
	}
	
	// Passing the Reference of the Process
		public void setProcesses(Process process[]) 
		{
			this.process = process;
			
				int i=0;
				
				while(i<totalProcesses)
				{
					process[i].setProcessNeighbors(process);
					i++;
				}
			
		}

		// Check whether round is completed or not 
		boolean isRoundComplete() 
		{
			for (boolean b : roundTracker_map.values()) 
				if (!b) return false;
			return true;
		}

		// Reset the Round information
		void roundConfirmation() 
		{	
			int i=0;
			while(i<totalProcesses)
			{
				int tempI=Bellman.nodesList.get(i);
				roundTracker_map.put(tempI, false);
				i++;
			}
			
		}

	//Shortest path tree
	public void MSTree() 
	{
		int matrix[][] = new int[totalProcesses][totalProcesses];
			for (int[] row: matrix)
				Arrays.fill(row, -1);
		for (Integer parentKey : parentTracker_map.keySet()) 
		{
			int id = parentKey;
			int parent_Process = parentTracker_map.get(id).parent;
			matrix[id][parent_Process] = parentTracker_map.get(id).wt;
			matrix[parent_Process][id] = parentTracker_map.get(id).wt;
			
		}
		System.out.println("////-------------------ShortestPath Matrix---------------//// ");
		for (int i = 0; i < totalProcesses; i++) 
		{
			System.out.println();
			for (int j = 0; j < totalProcesses; j++) 
				System.out.print(matrix[i][j] + "\t");	
		}
		System.out.println("\n");
	}

}
