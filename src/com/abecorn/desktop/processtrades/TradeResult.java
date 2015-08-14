package com.abecorn.desktop.processtrades;

import java.util.Set;

import com.abecorn.desktop.model.TradeEntry;

public class TradeResult {

	private String receiver;

	private String giver;

	private String name;

	private TradeEntry tradeEntry;

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getGiver() {
		return giver;
	}

	public void setGiver(String giver) {
		this.giver = giver;
	}

	public TradeEntry getTradeEntry() {
		return tradeEntry;
	}

	public void setTradeEntry(TradeEntry tradeEntry) {
		this.tradeEntry = tradeEntry;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
