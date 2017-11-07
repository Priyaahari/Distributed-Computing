package project2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class Master implements Runnable 
{
	class Parents{
		private int hopcount;
		private int parent;
		public Parents(int parent,int hopcount){
			this.parent=parent;
			this.hopcount=hopcount;
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
			int tempI=AsynchBFS.nodesList.get(i);
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
		//System.out.println("Process " + id + " completed round "
		 //+roundCount);
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
		roundCount++;
		begin_Round();
		while (!isComplete) 
		{
			while (!isRoundComplete()) {}
			while(!MSTcomplete) 
			{
				roundConfirmation(); 
				roundCount++;
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
				int tempI=AsynchBFS.nodesList.get(i);
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
			matrix[id][parent_Process] = parentTracker_map.get(id).hopcount;
			matrix[parent_Process][id] = parentTracker_map.get(id).hopcount;
			
		}
		
		System.out.println("\n\n////-------------------BFS Matrix---------------//// ");
		System.out.println("\n");
		System.out.print("\t0" );
		for (int j2 = 1; j2 < totalProcesses; j2++) 
				System.out.print("\t"+j2);
				
		System.out.println("");
		
		for (int j2 = 0; j2 < totalProcesses; j2++) 
				System.out.print("\t__");
				
		for (int i2 = 0; i2 < totalProcesses; i2++) 
		{
			System.out.println("\n");
			System.out.print(i2+" | ");
			
			for (int j2 = 0; j2 < totalProcesses; j2++) 
				System.out.print("\t"+matrix[i2][j2]);	
		}
		System.out.println("\n");
	}

}
