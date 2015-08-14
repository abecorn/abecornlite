package com.abecorn.desktop.processtrades;

import com.abecorn.desktop.processtrades.Graph.Vertex;


public class TradeResultWrapper {
		//These will be populated by the TradeMaximizer class after
		//the "run" method is called
		
		private Vertex receiver;
		
		private Vertex sender;
		
		private TradeResult tradeResult;

		public Vertex getReceiver() {
			return receiver;
		}

		public void setReceiver(Vertex receiver) {
			this.receiver = receiver;
		}

		public Vertex getSender() {
			return sender;
		}

		public void setSender(Vertex sender) {
			this.sender = sender;
		}

		public TradeResult getTradeResult() {
			return tradeResult;
		}

		public void setTradeResult(TradeResult tradeResult) {
			this.tradeResult = tradeResult;
		}
}