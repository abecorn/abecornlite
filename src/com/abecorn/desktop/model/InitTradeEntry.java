package com.abecorn.desktop.model;

import com.abecorn.desktop.model.TradeEntry.TradeEntryStatus;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

// A Issue stub.
public class InitTradeEntry {
	private String id;
	private String tradeId;
	private String name;
	private String description;
	private TradeEntryStatus status;
	private String itemId;

	private String user;
	private String[] acceptableTrades;

	public InitTradeEntry() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TradeEntryStatus getStatus() {
		return status;
	}

	public void setStatus(TradeEntryStatus status) {
		this.status = status;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String[] getAcceptableTrades() {
		return acceptableTrades;
	}

	public void setAcceptableTrades(String[] acceptableTrades) {
		this.acceptableTrades = acceptableTrades;
	}

}