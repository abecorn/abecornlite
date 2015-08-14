package com.abecorn.desktop.processtrades;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.abecorn.desktop.model.TradeEntry;

public class MathTradeProcessor {
	
	private List<TradeEntry> tradeEntries;
	
	private List<String[]> wantLists;
	
	private TradeMaximizer tradeMaximizer;
	
	List<String> options;
	
	
	public MathTradeProcessor(List<TradeEntry> tradeEntries)
	{
		this.tradeEntries = tradeEntries;
		
		//map to contain username key and full name values
		Map<String,String> fullNames = new HashMap<String,String>();

		//map to contain tradeEntryId key to game name values
		Map<String,String> gameNames = new HashMap<String,String>();
		
		
		tradeMaximizer = new TradeMaximizer(fullNames, gameNames);
		tradeMaximizer.allowDummies = true;
		options = new ArrayList<String>();
		wantLists = new ArrayList<String[]>();
		
	}
	
	/**
	 *The purpose of this method is to create a list of TradeResult objects
	 *populated with a receiver and sender Vertex contained in the TradeMaximizer.
	 *The user will still have to populate the TradeResult objects with the 
	 *rest of the information required about the Vertex objects
	 */
	public void process() {

		if (tradeEntries != null) {
			Iterator<TradeEntry> itr = tradeEntries.iterator();
			while (itr.hasNext()) {
				TradeEntry tradeEntry = itr.next();
				
				addItemToWantList(tradeEntry);
				
				
			}

		}
		
		if(tradeEntries != null)
		{
			tradeMaximizer.run(wantLists);
		}
	}
	
	private void addItemToWantList(TradeEntry tradeEntry)
	{
		List<String> wantList = new ArrayList<String>();
		String userName = tradeEntry.getUser();

		tradeMaximizer.addGameToMap(tradeEntry.getId(),
				tradeEntry.getSynopsis());
		userName = removeProblemCharacters(userName);
		wantList.add("(" + userName + ")");
		tradeMaximizer.addUserToMap("(" + userName + ")",
				tradeEntry.getUser());
		wantList.add(tradeEntry.getId());
		if (tradeEntry.getAcceptableTrades() != null) {
			wantList.addAll(Arrays.asList(tradeEntry
					.getAcceptableTrades()));
			wantLists.add((String[]) wantList
					.toArray(new String[0]));
		}
		else
		{
			wantList.addAll(new ArrayList<String>());
			wantLists.add((String[]) wantList
					.toArray(new String[0]));
		}
	}
	
	private String removeProblemCharacters(String userName)
	{
		
		if(userName != null && (userName.indexOf(" ") != -1 || userName.indexOf("#") != -1))
		{
			//first replace any # symbols with a series of x's
			userName = userName.replaceAll("#", "xoxoxoxo");
			
			//now we can replace spaces with # symbols
			userName = userName.replaceAll(" ", "#");
		}
		return userName;
	}
	
	public TradeMaximizer getTradeMaximizer() {
		return tradeMaximizer;
	}
	
}
