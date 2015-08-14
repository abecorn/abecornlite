package com.abecorn.desktop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.abecorn.desktop.model.ObservableTradeEntry;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;

public class SelectAcceptableTrades {
	private Map<String,String[]> acceptableTadesMap;
	
	private List<CheckBox> checkboxes;

	private ObservableTradeEntry selectedTradeEntry;
	private ObservableList<ObservableTradeEntry> allItems;
	
	private List<String> acceptableTrades;
	
	public SelectAcceptableTrades(ObservableTradeEntry selectedTradeEntry, ObservableList<ObservableTradeEntry> allItems)
	{
		checkboxes = new ArrayList<CheckBox>();
		this.selectedTradeEntry = selectedTradeEntry;
		if(selectedTradeEntry.getAcceptableTrades() != null && selectedTradeEntry.getAcceptableTrades().length > 0)
		{
			acceptableTrades = Arrays.asList(selectedTradeEntry.getAcceptableTrades());
		}
		
		this.allItems = allItems;
	}
	
	public void initCheckboxes(){
		Iterator<ObservableTradeEntry> itr = allItems.iterator();
		while(itr.hasNext()){
    		
    		ObservableTradeEntry te = itr.next();
    		if(!selectedTradeEntry.getId().equals(te.getId()))
    		{
    			CheckBox cb = new CheckBox();
    			cb.setText(te.getSynopsis() + " ("+te.getUser()+")");
    			cb.setId(te.getId());
    			
    			cb.setSelected(selected(cb));
    			
    			checkboxes.add(cb);
    		}
    	}
	}
	
	private boolean selected(CheckBox checkBox)
	{
		boolean checked = false;
		if(acceptableTrades != null && acceptableTrades.contains(checkBox.getId()))
		{
			checked = true;
		}
		return checked;
	}
	
	
	
	public Map<String, String[]> getAcceptableTadesMap() {
		return acceptableTadesMap;
	}

	public void setAcceptableTadesMap(Map<String, String[]> acceptableTadesMap) {
		this.acceptableTadesMap = acceptableTadesMap;
	}
	
	public List<CheckBox> getCheckboxes() {
		return checkboxes;
	}

	public String[] getAcceptableTrades() {
		return selectedTradeEntry.getAcceptableTrades();
	}
}
