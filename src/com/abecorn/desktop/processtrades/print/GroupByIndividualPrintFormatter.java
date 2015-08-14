package com.abecorn.desktop.processtrades.print;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.abecorn.desktop.processtrades.TradeResult;


public class GroupByIndividualPrintFormatter{
	
	//All of the successful results in a trade
	private List<TradeResult> successfulResults;
	
	//Set of all participants in the trade
	private Set<String> participants;

	private Map<String,Set<TradeResult>> giverResults;
	private Map<String,Set<TradeResult>> receiverResults;

	private String results;
	
	

	public GroupByIndividualPrintFormatter(List<TradeResult> successfulResults, Set<String> participants){
		this.successfulResults = successfulResults;
		this.participants = participants;
		giverResults = new HashMap<String,Set<TradeResult>>();
		receiverResults = new HashMap<String,Set<TradeResult>>();
		results = "";
	}
	
	public String print(){
		
		organizeByUser();
		if(participants != null && participants.size() > 0)
		{
			Iterator<String> itr = participants.iterator();
			while(itr.hasNext())
			{
				String subject = itr.next();
				results += addUserResults(subject);
			}
		}
		return results;
		
	}
	
	private void organizeByUser(){
		if(successfulResults != null && successfulResults.size() > 0)
		{
			Iterator<TradeResult> itr = successfulResults.iterator();
			while(itr.hasNext())
			{
				TradeResult tResult = itr.next();
				addGiverResult(tResult);
				addReceiverResult(tResult);
			}
		}
		
		//now order by the largest number of trades
	}
	
	private void usersByLargestNumberOfTrades(){
		if(giverResults != null && giverResults.size() > 0)
		{
			Iterator<String> itr = giverResults.keySet().iterator();
			while(itr.hasNext())
			{
				String giver = itr.next();
				Set<TradeResult> itemsGiving = giverResults.get(giver);
				if(itemsGiving != null)
				{
					
				}
			}
		}
		
	}
	
	private void addGiverResult(TradeResult tResult)
	{
		
		Set<TradeResult> tSet = giverResults.get(tResult.getGiver());
		if(tSet == null)
		{
			tSet = new HashSet<TradeResult>();
		}
		tSet.add(tResult);
		giverResults.put(tResult.getGiver(), tSet);
		
	}
	
	private void addReceiverResult(TradeResult tResult)
	{
		
		Set<TradeResult> tSet = receiverResults.get(tResult.getReceiver());
		if(tSet == null)
		{
			tSet = new HashSet<TradeResult>();
		}
		tSet.add(tResult);
		receiverResults.put(tResult.getReceiver(), tSet);
		
	}
	
	private String addUserResults(String subject)
	{
		String userSection = null;
		String giveSection = addGiveList(subject);
		String receiveSection = addReceiveList(subject);
		
		if(giveSection != null)
		{
			userSection = giveSection;
		}
		if(receiveSection != null)
		{
			userSection += receiveSection;
		}
		return userSection;
	}
	
	private String addUsername(String username)
	{
		//We have to get the username from the result 
		//because he haven't added the header with the username
		//yet
		String value = "<br/><b style='font-size:18px'>" + username + "</b><br/>";
		return value;
	}
	
	private String addGiveList(String subject)
	{
		String giveSection = "";
		Set<TradeResult> gives = giverResults.get(subject);
		if(gives != null && gives.size() > 0)
		{
			int i = 0; 
			Iterator<TradeResult> itr = gives.iterator();
			while(itr.hasNext())
			{
				TradeResult giveResult = itr.next();
				if(i ==  0)
				{
					giveSection = addUsername(giveResult.getGiver());
				}
				giveSection += "Gives " + giveResult.getName() + " to " + giveResult.getReceiver() + "<br/>";
				i++;
			}
		}
		return giveSection;
	}
	
	private String addReceiveList(String subject)
	{
		String receiveSection = "";
		Set<TradeResult> receives = receiverResults.get(subject);
		if(receives != null && receives.size() > 0)
		{
			int i = 0; 
			Iterator<TradeResult> itr = receives.iterator();
			while(itr.hasNext())
			{
				TradeResult receiveResult = itr.next();
				if(i ==  0)
				{
					receiveSection = "<br/>";
				}
				receiveSection += "Receives " + receiveResult.getName() + " from " + receiveResult.getGiver() + "<br/>";
				i++;
			}
		}
		return receiveSection;
	}
	
	/**
	 * All of the successful results in a trade
	 * @return
	 */
	public List<TradeResult> getSuccessfulResults() {
		return successfulResults;
	}

	public void setSuccessfulResults(List<TradeResult> successfulResults) {
		this.successfulResults = successfulResults;
	}
	
	public Set<String> getParticipants() {
		return participants;
	}

	public void setParticipants(Set<String> participants) {
		this.participants = participants;
	}
	
	public String getResults() {
		return results;
	}
}
