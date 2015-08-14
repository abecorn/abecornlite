/*
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.abecorn.desktop.model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.abecorn.desktop.io.initialize.LoadDataReader;
import com.abecorn.desktop.model.TradeEntry.TradeEntryStatus;
import com.abecorn.desktop.processtrades.Graph.Vertex;
import com.abecorn.desktop.processtrades.TradeResult;
import com.abecorn.desktop.processtrades.TradeResultWrapper;
import com.abecorn.desktop.processtrades.print.GroupByIndividualPrintFormatter;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.MapChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class TrackingServiceStub implements TrackingService {

    // You add a trade by adding an entry with an empty observable array list
    // of tradeEntry IDs in the projects Map.
    private ObservableMap<String, ObservableList<String>> projectsMap;
    
    private ObservableMap<String, TradeEntryStub> tradeEntriesMap;

    private ObservableList<String> projectNames;
    
    private Map<String,AtomicInteger> tradeEntryCounters;
    
    private Set<String> allUsers;
    
    // The projectNames list is kept in sync with the project's map by observing
    // the projectsMap and modifying the projectNames list in consequence.
    final MapChangeListener<String, ObservableList<String>> projectsMapChangeListener = new MapChangeListener<String, ObservableList<String>>() {
        @Override
        public void onChanged(Change<? extends String, ? extends ObservableList<String>> change) {
            if (change.wasAdded()) projectNames.add(change.getKey());
            if (change.wasRemoved()) projectNames.remove(change.getKey());
        }
    };
    
    public TrackingServiceStub(LoadDataReader data){
    	 final Map<String, ObservableList<String>> map = new TreeMap<>();
    	 allUsers = new HashSet<String>();
    	 tradeEntryCounters = new HashMap<String, AtomicInteger>();
         projectsMap = FXCollections.observableMap(map);
         for (String s : data.getTradeIds()) {
             projectsMap.put(s, FXCollections.<String>observableArrayList());
             //add the counter for each trade
             tradeEntryCounters.put(s, new AtomicInteger(data.getTradeEntryStartingIds().get(s)));
         }
         
         projectNames = FXCollections.<String>observableArrayList();
         projectNames.addAll(projectsMap.keySet());
         projectsMap.addListener(projectsMapChangeListener);
         
         final Map<String, TradeEntryStub> tradeEntriesTreeMap = new TreeMap<>();
         tradeEntriesMap = FXCollections.observableMap(tradeEntriesTreeMap);
         tradeEntriesMap.addListener(tradeEntriesMapChangeListener);
         TradeEntryStub ts;
         for (InitTradeEntry entry: data.getEntries())
         {
        	 allUsers.add(entry.getUser());
        	 this.loadTradeEntry(entry);
         }
         
    }

    // A TradeEntry stub.
    public final class TradeEntryStub implements ObservableTradeEntry {
       

		private final SimpleStringProperty id;
        private final SimpleStringProperty projectName;
        private final SimpleStringProperty title;
        private final SimpleStringProperty description;
        private final SimpleStringProperty itemId;
        private final SimpleStringProperty user;
        private SimpleListProperty<String> acceptableTrades;
        private final SimpleObjectProperty<TradeEntryStatus> status =
                new SimpleObjectProperty<>(TradeEntryStatus.NEW);

        TradeEntryStub(String projectName, String id) {
            this(projectName, id, null);
        }
        TradeEntryStub(String projectName, String id, String title) {
            assert projectNames.contains(projectName);
            assert ! projectsMap.get(projectName).contains(id);
            assert ! tradeEntriesMap.containsKey(id);
            this.projectName = new SimpleStringProperty(projectName);
            this.id = new SimpleStringProperty(id);
            this.title = new SimpleStringProperty(title);
            this.description = new SimpleStringProperty("");
            this.itemId = new SimpleStringProperty("");
            this.user = new SimpleStringProperty("");
            this.acceptableTrades = new SimpleListProperty<String>();
        }

        @Override
        public TradeEntryStatus getStatus() {
            return status.get();
        }

        @Override
        public String getId() {
            return id.get();
        }

        @Override
        public String getProjectName() {
            return projectName.get();
        }

        @Override
        public String getSynopsis() {
            return title.get();
        }

        public void setSynopsis(String title) {
            this.title.set(title);
        }

        @Override
        public String getDescription() {
            return description.get();
        }

        public void setDescription(String description) {
            this.description.set(description);
        }

        public void setStatus(TradeEntryStatus tradeEntryStatus) {
            this.status.set(tradeEntryStatus);
        }
        
        public void setProjectName(String projectName){
        	this.projectName.set(projectName);
        }

        public void setId(String id)
        {
        	this.id.set(id);
        }
        
        @Override
        public ObservableValue<String> idProperty() {
            return id;
        }

        @Override
        public ObservableValue<String> projectNameProperty() {
            return projectName;
        }

        @Override
        public ObservableValue<TradeEntryStatus> statusProperty() {
            return status;
        }

        @Override
        public ObservableValue<String> synopsisProperty() {
            return title;
        }

        @Override
        public ObservableValue<String> descriptionProperty() {
            return description;
        }
        
        public SimpleStringProperty getTitle() {
			return title;
		}
        
        public void setTitle(String title) {
        	this.title.set(title);
        }
        
		public String getItemId() {
			return itemId.get();
		}
		
		public void setItemId(String itemId)
		{
			this.itemId.set(itemId);
		}
		@Override
		public String getUser() {
			
			return user.get();
		}
		@Override
		public String[] getAcceptableTrades() {
			// TODO Auto-generated method stub
			return this.acceptableTrades.toArray(new String[0]);
		}
		@Override
		public void setUser(String user) {
			this.user.set(user);
			
		}
		@Override
		public void setAcceptableTrades(String[] acceptableTrades) {
			if(acceptableTrades != null)
			{
				ObservableList<String> observableList = FXCollections.observableArrayList(acceptableTrades);
				this.acceptableTrades = new SimpleListProperty<String>(observableList);
				
			}
			
		}
    }

    // You create new trade entry by adding a TradeEntryStub instance to the tradeEntriesMap.
    // the new id will be automatically added to the corresponding list in
    // the projectsMap.
    //
    final MapChangeListener<String, TradeEntryStub> tradeEntriesMapChangeListener = new MapChangeListener<String, TradeEntryStub>() {
        @Override
        public void onChanged(Change<? extends String, ? extends TradeEntryStub> change) {
            if (change.wasAdded()) {
                final TradeEntryStub val = change.getValueAdded();
                projectsMap.get(val.getProjectName()).add(val.getId());
            }
            if (change.wasRemoved()) {
                final TradeEntryStub val = change.getValueRemoved();
                projectsMap.get(val.getProjectName()).remove(val.getId());
            }
        }
    };
    
    

    private static <T> List<T> newList(T... items) {
        return Arrays.asList(items);
    }


    @Override
    public TradeEntryStub createTradeEntryFor(String projectName) {
        assert projectNames.contains(projectName);
        AtomicInteger tradeEntriesCounter = tradeEntryCounters.get(projectName);
        final TradeEntryStub tradeEntry = new TradeEntryStub(projectName, "TE-"+tradeEntriesCounter.incrementAndGet());
        assert tradeEntriesMap.containsKey(tradeEntry.getId()) == false;
        assert projectsMap.get(projectName).contains(tradeEntry.getId()) == false;
        tradeEntriesMap.put(tradeEntry.getId(), tradeEntry);
        return tradeEntry;
    }
    
    @Override
    public TradeEntryStub loadTradeEntry(InitTradeEntry tradeEntry) {
        assert projectNames.contains(tradeEntry.getTradeId());
        assert tradeEntriesMap.containsKey(tradeEntry.getId()) == false;
        assert projectsMap.get(tradeEntry.getTradeId()).contains(tradeEntry.getId()) == false;
        TradeEntryStub stub = new TradeEntryStub(tradeEntry.getTradeId(), tradeEntry.getId());
        stub.setDescription(tradeEntry.getDescription());
        stub.setSynopsis(tradeEntry.getName());
        stub.setItemId(tradeEntry.getItemId());
        stub.setUser(tradeEntry.getUser());
        stub.setAcceptableTrades(tradeEntry.getAcceptableTrades());
        tradeEntriesMap.put(tradeEntry.getId(), stub);
        return stub;
    }

    @Override
    public void deleteTradeEntry(String tradeEntryId) {
        assert tradeEntriesMap.containsKey(tradeEntryId);
        tradeEntriesMap.remove(tradeEntryId);
    }

    @Override
    public ObservableList<String> getProjectNames() {
        return projectNames;
    }

    @Override
    public ObservableList<String> getTradeEntryIds(String projectName) {
        return projectsMap.get(projectName);
    }

    @Override
    public TradeEntryStub getTradeEntry(String tradeEntryId) {
        return tradeEntriesMap.get(tradeEntryId);
    }

    @Override
    public void saveTradeEntry(String tradeEntryId, TradeEntryStatus status,
            String synopsis, String description, String itemId, String[] acceptableTrades, String user) {
        TradeEntryStub issue = getTradeEntry(tradeEntryId);
        issue.setDescription(description);
        issue.setSynopsis(synopsis);
        issue.setStatus(status);
        issue.setItemId(itemId);
        issue.setAcceptableTrades(acceptableTrades);
        issue.setUser(user);
    }

    public void writeState(){
    	if(projectsMap != null && projectsMap.size() > 0)
    	{
    		Set<String> keys = projectsMap.keySet();
    		Iterator<String> itr = keys.iterator();
    		JSONArray trades = new JSONArray();
    		while(itr.hasNext())
    		{
    			String tradeId = itr.next();
    			ObservableList<String> tradeEntries = projectsMap.get(tradeId);
    			Iterator<String> tradeEntryIterator = tradeEntries.iterator();
    			JSONArray tradeEntriesArray = new JSONArray();
    			while(tradeEntryIterator.hasNext())
    			{
    				String tradeEntryId = tradeEntryIterator.next();
    				TradeEntry tradeEntry = tradeEntriesMap.get(tradeEntryId);
    				if(tradeEntry.getUser() != null)
    				{
    					allUsers.add(tradeEntry.getUser());
    				}
    				JSONObject tradeEntryJson = createTradeEntry(tradeEntry);
    				tradeEntriesArray.add(tradeEntryJson);
    			}
    			JSONObject trade = createTradeObject(tradeId, tradeEntryCounters.get(tradeId).get(), tradeEntriesArray);
    			trades.add(trade);
    		}
    		JSONObject currentState = new JSONObject();
    		currentState.put("trades", trades);
    		
    		try {
    			FileWriter fileWriter = new FileWriter("c:\\abetrades\\trades.json");
				currentState.writeJSONString(fileWriter);
				fileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		
    	}
    }
    
    private JSONObject createTradeObject(String tradeId, Integer startingId, JSONArray tradeEntries){
    	JSONObject obj = new JSONObject();
    	obj.put("tradeId", tradeId);
    	obj.put("startingId", startingId);
    	obj.put("tradeEntries", tradeEntries);
    	return obj;
    }
    
    private JSONObject createTradeEntry(TradeEntry tradeEntry)
    {
    	JSONObject obj = new JSONObject();
    	obj.put("id", tradeEntry.getId());
    	obj.put("itemId", tradeEntry.getItemId());
    	obj.put("name", tradeEntry.getSynopsis());
    	obj.put("description", tradeEntry.getDescription());
    	obj.put("user", tradeEntry.getUser());
    	JSONArray acceptableTrades = new JSONArray();
    	if(tradeEntry.getAcceptableTrades() != null && tradeEntry.getAcceptableTrades().length > 0)
    	{
    		acceptableTrades.addAll(Arrays.asList(tradeEntry.getAcceptableTrades()));
    	}
    	obj.put("acceptableTrades", acceptableTrades);
    	return obj;
    }
     
    public void printResults(List<TradeResultWrapper> results)
    {
    	List<TradeResult> tradeResults = getItemsTrading(results);
    	GroupByIndividualPrintFormatter byIndividualPrintFormatter = new GroupByIndividualPrintFormatter(tradeResults, allUsers);
    	byIndividualPrintFormatter.print();
    	
    	try {
			FileWriter fileWriter = new FileWriter("c:\\abetrades\\results.html");
			fileWriter.write(byIndividualPrintFormatter.getResults());
			fileWriter.close();
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    private List<TradeResult> getItemsTrading(
			List<TradeResultWrapper> results) {
		List<TradeResult> tradeResults = new ArrayList<TradeResult>();
		// Now store the new results
		Iterator<TradeResultWrapper> itr = results.iterator();
		int i = 0;
		while (itr.hasNext()) {
			TradeResultWrapper tradeResultWrapper = itr.next();

			Vertex receiver = tradeResultWrapper.getReceiver();
			Vertex sender = tradeResultWrapper.getSender();

			if (receiver != null && sender != null) {

				String tradeEntryId = sender.getName();
				if (tradeEntryId != null) {
					// Find the TradeEntry
					TradeEntry tradeEntry = tradeEntriesMap.get(tradeEntryId);
							
					if (tradeEntry != null) {
						// Set the sender information
						TradeResult tradeResult = new TradeResult();
						tradeResult = setSenderInformation(tradeResult, sender,
								tradeEntry);

						// We also need the profile link and profile image url
						// of the user
						// we are going to send the item to. This requires
						// getting the TradeEntry
						// item the receiver may send to someone else. However,
						// that TradeEntry item
						// will have the same profile information we need.
						String receiverUsername = receiver.getUser();
						String receiverTradeEntryId = receiver.getName();
						receiverUsername = restoreUsername(receiverUsername);
						if (receiverUsername != null) {
							setReceiverInformation(tradeResult,
									receiverTradeEntryId);
						}

						tradeResults.add(tradeResult);
						
					}

				}

			}
			i++;
		}
		return tradeResults;
	}
    
    private TradeResult setSenderInformation(TradeResult tradeResult,
			Vertex sender, TradeEntry tradeEntry) {
		String senderUsername = sender.getUser();

		if (senderUsername != null) {
			// Remove the parentheses from around the username
			senderUsername = restoreUsername(senderUsername);
			if (tradeEntry != null) {
				tradeResult.setName(tradeEntry.getSynopsis());
				tradeResult.setGiver(tradeEntry.getUser());

				tradeResult.setTradeEntry(tradeEntry);
				

			}
		}
		return tradeResult;
	}

	private void setReceiverInformation(TradeResult tradeResult,
			String receiverTradeEntryId) {

		if (receiverTradeEntryId != null) {
			TradeEntry receiverTradeEntry = tradeEntriesMap.get(receiverTradeEntryId);
			tradeResult.setReceiver(receiverTradeEntry.getUser());

		}
	}
	
	private String restoreUsername(String username) {
		username = username.replace("(", "");
		username = username.replace(")", "");
		username = username.replaceAll("#", " ");
		username = username.replaceAll("xoxoxoxo", "#");
		return username;
	}
}
