package com.abecorn.desktop.io.initialize;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.abecorn.desktop.io.ItemReader;
import com.abecorn.desktop.model.InitTradeEntry;
import com.abecorn.desktop.model.TradeEntry;
import com.abecorn.desktop.model.TradeEntry.TradeEntryStatus;

public class LoadDataReader extends ItemReader {

	private List<InitTradeEntry> entries;
	
	private Set<String> tradeIds;
	
	//Key: tradeId
	private Map<String,Integer> tradeEntryStartingIds;

	

	public LoadDataReader(){
		entries = new ArrayList<InitTradeEntry>();
		tradeEntryStartingIds = new HashMap<String,Integer>();
	}
	
	public void loadTrades() {
		JSONParser parser = new JSONParser();

		try {
			Object obj = parser.parse(new FileReader("c:\\abetrades\\trades.json"));
			
			JSONObject jsonObject = (JSONObject) obj;

			JSONArray trades = (JSONArray) jsonObject.get("trades");
			
			if(trades != null && trades.size() > 0)
			{
				Iterator<JSONObject> itr = trades.iterator();
				while(itr.hasNext()){
					JSONObject trade = itr.next();
					
					convertTradeJSON(trade);
					
				}
			}
			

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
	
	private void convertTradeJSON(JSONObject trade)
	{
		
		JSONArray tradeEntries = (JSONArray) trade.get("tradeEntries");
		String tradeId = (String) trade.get("tradeId");
		
		addTrade(tradeId);
		
		Long startingId = (Long)trade.get("startingId");
		tradeEntryStartingIds.put(tradeId, new Integer(startingId.intValue()));
		if(tradeEntries != null && tradeEntries.size() > 0)
		{
			Iterator<JSONObject> itr = tradeEntries.iterator();
			while(itr.hasNext())
			{
				JSONObject tradeEntryObj = itr.next();
				InitTradeEntry tradeEntry = convertTradeEntryJSON(tradeEntryObj, tradeId);
				entries.add(tradeEntry);
			}
		}
	}
	
	private InitTradeEntry convertTradeEntryJSON(JSONObject obj, String tradeId)
	{
		String name = (String) obj.get("name");
		String id = (String) obj.get("id");
		String itemId = (String) obj.get("itemId");
		String description = (String) obj.get("description");
		String user = (String) obj.get("user");
		JSONArray acceptableTrades = (JSONArray) obj.get("acceptableTrades");
		
		InitTradeEntry tradeEntry = new InitTradeEntry();
		tradeEntry.setTradeId(tradeId);
		tradeEntry.setId(id);
		tradeEntry.setDescription(description);
		tradeEntry.setName(name);
		tradeEntry.setItemId(itemId);
		tradeEntry.setStatus(TradeEntryStatus.NEW);
		tradeEntry.setUser(user);
		tradeEntry.setAcceptableTrades((String[])acceptableTrades.toArray(new String[0]));
		
		return tradeEntry;
		
	}
	
	private void addTrade(String tradeId){
		if(tradeIds == null)
		{
			tradeIds = new HashSet<String>();
		}
		tradeIds.add(tradeId);
	}
	
	public List<InitTradeEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<InitTradeEntry> entries) {
		this.entries = entries;
	}

	public Set<String> getTradeIds() {
		return tradeIds;
	}

	public void setTradeIds(Set<String> tradeIds) {
		this.tradeIds = tradeIds;
	}
	
	public Map<String, Integer> getTradeEntryStartingIds() {
		return tradeEntryStartingIds;
	}

	public void setTradeEntryStartingIds(Map<String, Integer> tradeEntryStartingIds) {
		this.tradeEntryStartingIds = tradeEntryStartingIds;
	}
	
}
