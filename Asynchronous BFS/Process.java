package project2;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

class Process implements Runnable 
{
	 Master masterProcess;
	 Process process[];
	 volatile String message = "";
	 int parent_Process;
	 volatile Queue<Message> messageQueue = new LinkedList<Message>();
	 volatile Map<Integer,String> stats = new HashMap<Integer,String>();
	 volatile HashMap<Integer,String> hash = new HashMap<Integer,String>();
	 long distance;
	 boolean isComplete = false;
	 HashMap<Integer, Integer> nbrs;
	 int id = 0;
	 Random random = new Random();
	 private int round = 0;
	 

	public String getMsg() 
	{
		return message;
	}

	public void setMsg(String message) 
	{
		this.message = message;
	}
	
	public Process(int id, HashMap<Integer, Integer> nbrs, int leaderID, Master masterProcess) 
	{
		this.id = id;
		this.masterProcess = masterProcess;
		this.nbrs = new HashMap<>();
		this.nbrs = nbrs;
		if (leaderID == id) 
		{
			distance = 0;
			this.parent_Process = id;
		} 
		else 
		{
			distance = Integer.MAX_VALUE;
			this.parent_Process = -2;
		}
		
		for (Integer nbr : nbrs.keySet()) 
		
			stats.put(nbr, "unknown");
		
	}

	public void setProcessNeighbors(Process process[]) 
	{
		this.process = process;
	}
	 
	public synchronized Message change_msgList(Message message, boolean add) 
	{
		
		if (add) 
		{
			messageQueue.add(message);
			return message;
		} 
		
			else {
			if (!messageQueue.isEmpty()) {
				Message m = messageQueue.peek();
				if (round >= m.getSentRound() + m.getTransmissionTime()) {
					return messageQueue.remove();
				} else
					return null;
			} else
				return null;
		}
		
	}
	
	public void run() 
	{
		masterProcess.roundCompletion(id);

		while (!isComplete) 
		{
			if (getMsg().equals("Start")) 
			{
				setMsg("");
				if (distance == 0) 
					sendMessages();
				
				masterProcess.roundCompletion(id);
			} 
			else if (getMsg().equals("Begin_Round")) 
			{
				round++;
				setMsg("");
				boolean change = receiveMessages();
				if (change) 
				
					sendMessages();
				
				masterProcess.roundCompletion(id);
				
			} 
			else if (getMsg().equals("getParent")) 
			{
				// Sending Parent details to Master after completing the
				// algorithm
				
				setMsg("");
				int weight = (id == parent_Process) ? 0 : nbrs.get(parent_Process);
				
				masterProcess.setParents(id, parent_Process, weight);
				masterProcess.roundCompletion(id);
			} 
			else if (getMsg().equals("Completed")) 
			{
				// Terminating the Algorithm
				System.out.println("Process ID: " + id +" \nParent of process "+id + " : " + parent_Process + " || Distance from Leader: " + distance);
				System.out.println("-------------------------------------------------------------------");
				System.out.println("");
				isComplete = true;
					
			}	
		}	
	}

	
	// Processing messages in the Queue and updating the current distance
	private boolean receiveMessages() 
	{
	//System.out.println("\nIn receive(): Process " + id);
		boolean change = false;
		Message msg= change_msgList(null,false);
		while (msg != null) 
		{
				if ((msg.getDistance() + 1) < distance) 
				{ 
					change = true;
					this.distance = msg.getDistance() + 1;
					if(parent_Process != -2)
						process[parent_Process].ackStats(id,"NACK",false);
					
					this.parent_Process = msg.getfromProcess();
					process[id].ackStats(id,"unkonwn",true);
				}
				else
					process[msg.getfromProcess()].ackStats(id,"NACK",false);
				
				msg = new Message();
				msg = change_msgList(null, false);
			}
		if(ACK(id)){
			//System.out.println("\nIn ACK(): Process " + id);
			if (parent_Process == id)
				Master.MSTcomplete=true;
		
			else
				process[parent_Process].ackStats(id, "done",false);
			
		}
		return change;
	}

	// Sending distance
	void sendMessages() 
	{
		Message msg = new Message();
		msg.setfromProcess(this.id);
		//System.out.println("\nIn send() : msg.setfromProcess(this.id) : " + id);
		msg.setDistance(this.distance);
		for (int n : nbrs.keySet()) 
		{
			
			msg.setTransmissionTime(random.nextInt((18 - 1) + 1) + 1);
			msg.setSentRound(round);
			process[n].change_msgList(msg, true);
			

		}
	}
	
	public synchronized boolean ackStats(int id, String reply, boolean rst){
		if(rst){
			for(Integer val : nbrs.keySet())
				stats.put(val,"unknown");
			
			return true;
		}else
			stats.put(id, reply);
		
		return true;
	}
	
	public synchronized boolean ACK(int id){
		for (Map.Entry<Integer, String> entry : stats.entrySet()) {
			if (entry.getKey() != parent_Process && entry.getValue().equals("unknown")) 
				return false;
			
		}
		return true;
	}	

}

class Message 
{
	int ID;
	long round;
	private long transmissionTime;
	private long sentRound;
	
	public int getfromProcess() 
	{
		return ID;
	}

	public void setfromProcess(int fromID) 
	{
		this.ID = fromID;
	}
	long currDistance;
	
	public long getSentRound() {
		return sentRound;
	}

	public void setSentRound(long sentRound) {
		this.sentRound = sentRound;
	}
	
	public long getDistance() 
	{
		return currDistance;
	}
	public void setDistance(long currDistance) 
	{
		this.currDistance = currDistance;
	}
	public long getTransmissionTime() {
		return transmissionTime;
	}

	public void setTransmissionTime(long transmissionTime) {
		this.transmissionTime = transmissionTime;
	}
	
}
