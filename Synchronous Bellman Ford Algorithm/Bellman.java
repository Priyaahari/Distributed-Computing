/*
 * Project 1: Synchronous Bellman Ford Algorithm
 * 
 * Authors  : Deepak Shanmugam, Ashwath Santhanam, Haripriyaa U Manian
 * 
 */

package project1;

import java.util.*;
import java.io.*;

public class Bellman 
{
	public static ArrayList<Integer> nodesList = new ArrayList<Integer>();
	public static void main(String[] input)  
	{
		
		int totalProcesses=0;
		int leaderID=-1;
		int wt,i,j;
		i=0;
		
		if (input.length != 1)
		{
			System.out.println("Usage: java -cp . project1.Bellman <inputFiepath>");
			System.exit(0);
		}
		try 
		{
			File f = new File(input[0]);
			Scanner scanner = new Scanner(f);

		while(scanner.hasNextLine())
		{
		String line=scanner.nextLine();
		if(line.startsWith("#"))
		{	
			if(line.contains("number of nodes"))
				 totalProcesses = scanner.nextInt();
			
			else if(line.contains("node IDs"))
			{
				for(int k=0;k<totalProcesses;k++)
					nodesList.add(scanner.nextInt());
			}

			else if(line.contains("id of the leader"))
				leaderID = scanner.nextInt();
			
			else if(line.contains("means there is no edge"))
			{
			Master masterProcess = new Master(totalProcesses);
			Process process[]=new Process[totalProcesses + 1];
			
			
			while(i < totalProcesses)
			{
				//neighbors_hashmap
				int tempI=nodesList.get(i);
				HashMap<Integer, Integer> nbrs  = new HashMap<>();
				j=0;
				
				while(j< totalProcesses)
				{
					int tempJ=nodesList.get(j);
					wt = scanner.nextInt();
					if (wt != -1) 
						nbrs.put(tempJ, wt);
						
					j++;
					
				}
				process[i] = new Process(tempI, nbrs, leaderID, masterProcess);
				i++;
			}
			
			masterProcess.setProcesses(process);
			// Start Master Thread
			Thread masterThread = new Thread(masterProcess);
			masterThread.start();
			// Start all Processes threads
			i=0;
			while(i < totalProcesses)
			
			{
				Thread processThread = new Thread(process[i]);
				processThread.start();
				i++;
			}
			}
		}
		}
		scanner.close();
		}
		catch (IOException e) 
		{
			System.out.println("File not found");
			e.printStackTrace();
		}
	}
}