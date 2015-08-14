/* 
 * Modified by Abecorn
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
package com.abecorn.desktop;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import com.abecorn.desktop.model.TradeEntry;
import com.abecorn.desktop.io.initialize.LoadDataReader;
import com.abecorn.desktop.model.ObservableTradeEntry;
import com.abecorn.desktop.model.TrackingService;
import com.abecorn.desktop.model.TrackingServiceStub;
import com.abecorn.desktop.model.TrackingServiceStub.TradeEntryStub;
import com.abecorn.desktop.model.TradeEntry.TradeEntryStatus;
import com.abecorn.desktop.processtrades.MathTradeProcessor;
import com.abecorn.desktop.processtrades.TradeMaximizer;
import com.abecorn.desktop.processtrades.TradeResultWrapper;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class AbcornTradingLiteController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML //  fx:id="colName"
    private TableColumn<ObservableTradeEntry, String> colName; // Value injected by FXMLLoader

    @FXML //  fx:id="colStatus"
    private TableColumn<ObservableTradeEntry, String> colOwner; // Value injected by FXMLLoader

    @FXML //  fx:id="colSynopsis"
    private TableColumn<ObservableTradeEntry, String> colSynopsis; // Value injected by FXMLLoader

    @FXML //  fx:id="deleteTradeEntry"
    private Button deleteTradeEntry; // Value injected by FXMLLoader

    @FXML //  fx:id="descriptionValue"
    private TextArea descriptionValue; // Value injected by FXMLLoader

    @FXML //  fx:id="details"
    private AnchorPane details; // Value injected by FXMLLoader

    @FXML //  fx:id="displayedIssueLabel"
    // It will contain a concatenation of the project name and the bug id.
    private Label displayedIssueLabel; // Value injected by FXMLLoader

    @FXML // fx:id=""
    //It will contain the item id
    private TextField itemId;
    
    @FXML // fx:id="owner"
    private TextField owner;
    
    @FXML
    private VBox acceptableTradesVBox;
    
    @FXML //  fx:id="list"
    private ListView<String> list; // Value injected by FXMLLoader

    @FXML //  fx:id="newTradeEntry"
    private Button newTradeEntry; // Value injected by FXMLLoader

    @FXML //  fx:id="saveTradeEntry"
    private Button saveTradeEntry; // Value injected by FXMLLoader

    @FXML //  fx:id="synopsis"
    private TextField synopsis; // Value injected by FXMLLoader

    @FXML //  fx:id="table"
    private TableView<ObservableTradeEntry> table; // Value injected by FXMLLoader

    private List<CheckBox> acceptableTrades;
    
    private String displayedBugId; // the id of the bug displayed in the details section.
    private String displayedBugProject; // the name of the project of the bug displayed in the detailed section.
    ObservableList<String> projectsView = FXCollections.observableArrayList();
    TrackingService model = null;
    private TextField statusValue = new TextField();
    final ObservableList<ObservableTradeEntry> tableContent = FXCollections.observableArrayList();
    
    /**
     * Initializes the controller class.
     */
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert colName != null : "fx:id=\"colName\" was not injected: check your FXML file 'AbecornTrackingLite.fxml'.";
        assert colOwner != null : "fx:id=\"colOwner\" was not injected: check your FXML file 'AbecornTrackingLite.fxml'.";
        assert colSynopsis != null : "fx:id=\"colSynopsis\" was not injected: check your FXML file 'AbecornTrackingLite.fxml'.";
        assert deleteTradeEntry != null : "fx:id=\"deleteTradeEntry\" was not injected: check your FXML file 'AbecornTrackingLite.fxml'.";
        assert descriptionValue != null : "fx:id=\"descriptionValue\" was not injected: check your FXML file 'AbecornTrackingLite.fxml'.";
        assert details != null : "fx:id=\"details\" was not injected: check your FXML file 'AbecornTrackingLite.fxml'.";
        assert displayedIssueLabel != null : "fx:id=\"displayedIssueLabel\" was not injected: check your FXML file 'AbecornTrackingLite.fxml'.";
        assert newTradeEntry != null : "fx:id=\"newTradeEntry\" was not injected: check your FXML file 'AbecornTrackingLite.fxml'.";
        assert saveTradeEntry != null : "fx:id=\"saveTradeEntry\" was not injected: check your FXML file 'AbecornTrackingLite.fxml'.";
        assert synopsis != null : "fx:id=\"synopsis\" was not injected: check your FXML file 'AbecornTrackingLite.fxml'.";
        assert itemId != null : "fx:id=\"itemId\" was not injected: check your FXML file 'AbecornTrackingLite.fxml'.";
        assert owner != null : "fx:id=\"owner\" was not injected: check your FXML file 'AbecornTrackingLite.fxml'.";
        assert table != null : "fx:id=\"table\" was not injected: check your FXML file 'AbecornTrackingLite.fxml'.";
        assert list != null : "fx:id=\"list\" was not injected: check your FXML file 'AbecornTrackingLite.fxml'.";
        
        System.out.println(this.getClass().getSimpleName() + ".initialize");
        configureButtons();
        configureDetails();
        configureTable();
        connectToService();
        if (list != null) {
            list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            list.getSelectionModel().selectedItemProperty().addListener(projectItemSelected);
            displayedProjectNames.addListener(projectNamesListener);
        }
    }

    /**
     * Called when the NewIssue button is fired.
     *
     * @param event the action event.
     */
    @FXML
    void newTradeEntryFired(ActionEvent event) {
        final String selectedProject = getSelectedProject();
        if (model != null && selectedProject != null) {
            ObservableTradeEntry tradeEntry = model.createTradeEntryFor(selectedProject);
            if (table != null) {
                // Select the newly created issue.
                table.getSelectionModel().clearSelection();
                table.getSelectionModel().select(tradeEntry);
            }
        }
    }
    
    @FXML
    void processTradeFired(ActionEvent event)
    {
    	ObservableList<ObservableTradeEntry> tradeEntries = table.getItems();
    	Iterator<ObservableTradeEntry> itr = tradeEntries.iterator();
    	List<TradeEntry> list = new ArrayList<TradeEntry>();
    	while(itr.hasNext())
    	{
    		TradeEntry te = itr.next();
    		list.add(te);
    		
    	}
    	
    	MathTradeProcessor processor = new MathTradeProcessor(list);
    	processor.process();
    	TradeMaximizer tradeMaximizer = processor.getTradeMaximizer();
    	List<TradeResultWrapper> results = tradeMaximizer.getResults();
    	
    	model.printResults(results);
    }
    @FXML
    void checkAllFired(ActionEvent event)
    {
    	Iterator<CheckBox> checkboxes = acceptableTrades.iterator();
    	while(checkboxes.hasNext())
    	{
    		CheckBox cb = checkboxes.next();
    		cb.setSelected(true);
    	}
    	updateSaveIssueButtonState();
    }
    
    @FXML
    void unCheckAllFired(ActionEvent event)
    {
    	Iterator<CheckBox> checkboxes = acceptableTrades.iterator();
    	while(checkboxes.hasNext())
    	{
    		CheckBox cb = checkboxes.next();
    		cb.setSelected(false);
    	}
    	updateSaveIssueButtonState();
    }
    /**
     * Called when the DeleteIssue button is fired.
     *
     * @param event the action event.
     */
    @FXML
    void deleteTradeEntryFired(ActionEvent event) {
        final String selectedProject = getSelectedProject();
        if (model != null && selectedProject != null && table != null) {
            // We create a copy of the current selection: we can't delete
            //    issue while looping over the live selection, since
            //    deleting selected issues will modify the selection.
            final List<?> selectedIssue = new ArrayList<>(table.getSelectionModel().getSelectedItems());
            for (Object o : selectedIssue) {
                if (o instanceof ObservableTradeEntry) {
                    model.deleteTradeEntry(((ObservableTradeEntry) o).getId());
                }
            }
            table.getSelectionModel().clearSelection();
        }
        
        //Now output the json file to save current application state
        model.writeState();
    }

    /**
     * Called when the SaveIssue button is fired.
     *
     * @param event the action event.
     */
    @FXML
    void saveTradeEntryFired(ActionEvent event) {
        final ObservableTradeEntry ref = getSelectedTradeEntry();
        final TradeEntry edited = new DetailsData();
        SaveState saveState = computeSaveState(edited, ref);
        if (saveState == SaveState.UNSAVED) {
            model.saveTradeEntry(ref.getId(), edited.getStatus(),
                    edited.getSynopsis(), edited.getDescription(), edited.getItemId(), edited.getAcceptableTrades(), edited.getUser());
        }
        // We refresh the content of the table because synopsis and/or description
        // are likely to have been modified by the user.
        int selectedRowIndex = table.getSelectionModel().getSelectedIndex();
        table.getItems().clear();
        displayedTradeEntries = model.getTradeEntryIds(getSelectedProject());
        for (String id : displayedTradeEntries) {
            final ObservableTradeEntry tradeEntry = model.getTradeEntry(id);
            table.getItems().add(tradeEntry);
        }
        table.getSelectionModel().select(selectedRowIndex);

        updateSaveIssueButtonState();
        
        //Now output the json file to save current application state
        model.writeState();
    }
    
    private void configureButtons() {
        if (newTradeEntry != null) {
        	newTradeEntry.setDisable(true);
        }
        if (saveTradeEntry != null) {
        	saveTradeEntry.setDisable(true);
        }
        if (deleteTradeEntry != null) {
        	deleteTradeEntry.setDisable(true);
        }
    }
    
    // An observable list of project names obtained from the model.
    // This is a live list, and we will react to its changes by removing
    // and adding project names to/from our list widget.
    private ObservableList<String> displayedProjectNames;
    
    // The list of Issue IDs relevant to the selected project. Can be null
    // if no project is selected. This list is obtained from the model.
    // This is a live list, and we will react to its changes by removing
    // and adding Issue objects to/from our table widget.
    private ObservableList<String> displayedTradeEntries;
    
    // This listener will listen to changes in the displayedProjectNames list,
    // and update our list widget in consequence.
    private final ListChangeListener<String> projectNamesListener = new ListChangeListener<String>() {

        @Override
        public void onChanged(Change<? extends String> c) {
            if (projectsView == null) {
                return;
            }
            while (c.next()) {
                if (c.wasAdded() || c.wasReplaced()) {
                    for (String p : c.getAddedSubList()) {
                        projectsView.add(p);
                    }
                }
                if (c.wasRemoved() || c.wasReplaced()) {
                    for (String p : c.getRemoved()) {
                        projectsView.remove(p);
                    }
                }
            }
            FXCollections.sort(projectsView);
        }
    };
    
    // This listener will listen to changes in the displayedIssues list,
    // and update our table widget in consequence.
    private final ListChangeListener<String> projectTradeEntriesListener = new ListChangeListener<String>() {

        @Override
        public void onChanged(Change<? extends String> c) {
            if (table == null) {
                return;
            }
            while (c.next()) {
                if (c.wasAdded() || c.wasReplaced()) {
                    for (String p : c.getAddedSubList()) {
                        table.getItems().add(model.getTradeEntry(p));
                    }
                }
                if (c.wasRemoved() || c.wasReplaced()) {
                    for (String p : c.getRemoved()) {
                        ObservableTradeEntry removed = null;
                        // Issue already removed:
                        // we can't use model.getIssue(issueId) to get it.
                        // we need to loop over the table content instead.
                        // Then we need to remove it - but outside of the for loop
                        // to avoid ConcurrentModificationExceptions.
                        for (ObservableTradeEntry t : table.getItems()) {
                            if (t.getId().equals(p)) {
                                removed = t;
                                break;
                            }
                        }
                        if (removed != null) {
                            table.getItems().remove(removed);
                        }
                    }
                }
            }
        }
    };

    // Connect to the model, get the project's names list, and listen to
    // its changes. Initializes the list widget with retrieved project names.
    private void connectToService() {
        if (model == null) {
        	LoadDataReader data = new LoadDataReader();
        	data.loadTrades();
            model = new TrackingServiceStub(data);
            displayedProjectNames = model.getProjectNames();
        }
        projectsView.clear();
        List<String> sortedProjects = new ArrayList<>(displayedProjectNames);
        Collections.sort(sortedProjects);
        projectsView.addAll(sortedProjects);
        list.setItems(projectsView);
    }
    
    // This listener listen to changes in the table widget selection and
    // update the DeleteIssue button state accordingly.
    private final ListChangeListener<ObservableTradeEntry> tableSelectionChanged =
            new ListChangeListener<ObservableTradeEntry>() {

                @Override
                public void onChanged(Change<? extends ObservableTradeEntry> c) {
                    updateDeleteIssueButtonState();
                    updateBugDetails();
                    updateSaveIssueButtonState();
                }
            };

    private static String nonNull(String s) {
        return s == null ? "" : s;
    }

    private void updateBugDetails() {
        final ObservableTradeEntry selectedIssue = getSelectedTradeEntry();
        if (details != null && selectedIssue != null) {
            if (displayedIssueLabel != null) {
                displayedBugId = selectedIssue.getId();
                displayedBugProject = selectedIssue.getProjectName();
                displayedIssueLabel.setText( displayedBugId + " / " + displayedBugProject );
            }
            if (synopsis != null) {
                synopsis.setText(nonNull(selectedIssue.getSynopsis()));
            }
            if (statusValue != null) {
                statusValue.setText(selectedIssue.getStatus().toString());
            }
            if(itemId != null) {
            	itemId.setText(selectedIssue.getItemId().toString());
            }
            
            if(owner != null)
            {
            	owner.setText(selectedIssue.getUser());
            }
            
            if(acceptableTradesVBox != null)
            {
            	acceptableTradesVBox.getChildren().clear();
            	ObservableList<ObservableTradeEntry> allItems = table.getItems();
            	
            	SelectAcceptableTrades selectAcceptableTrades = new SelectAcceptableTrades(selectedIssue, allItems);
            	selectAcceptableTrades.initCheckboxes();
            	if(selectAcceptableTrades.getCheckboxes() != null)
            	{	
            		Iterator<CheckBox> itr = selectAcceptableTrades.getCheckboxes().iterator();
            		while(itr.hasNext())
            		{
            			CheckBox cb = itr.next();
            			cb.setOnAction(e -> updateSaveIssueButtonState());
            		}
            		acceptableTradesVBox.getChildren().addAll(selectAcceptableTrades.getCheckboxes());
            		acceptableTrades = selectAcceptableTrades.getCheckboxes();
            	}
            }
            
            if (descriptionValue != null) {
                descriptionValue.selectAll();
                descriptionValue.cut();
                descriptionValue.setText(selectedIssue.getDescription());
            }
        } else {
            displayedIssueLabel.setText("");
            displayedBugId = null;
            displayedBugProject = null;
            
        }
        if (details != null) {
            details.setVisible(selectedIssue != null);
        }
    }

    
    private boolean isVoid(Object o) {
        if (o instanceof String) {
            return isEmpty((String) o);
        } else {
            return o == null;
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean equal(Object o1, Object o2) {
        if (isVoid(o1)) {
            return isVoid(o2);
        }
        return o1.equals(o2);
    }
    
    private boolean arraysEqual(String[] a1, String[] a2)
    {
    	Set<String> s1 = new HashSet<String>();
    	Set<String> s2 = new HashSet<String>();
    	if(a1 != null && a1.length > 0)
    	{
    		s1.addAll(Arrays.asList(a1));
    	}
    	if(a2 != null && a2.length > 0)
    	{
    		s2.addAll(Arrays.asList(a2));
    	}
    	if(s1.equals(s2))
    	{
    		return true;
    	}
    	return false;
    	
    }

    private static enum SaveState {

        INVALID, UNSAVED, UNCHANGED
    }

    private final class DetailsData implements TradeEntry {

        @Override
        public String getId() {
            if (displayedBugId == null || isEmpty(displayedIssueLabel.getText())) {
                return null;
            }
            return displayedBugId;
        }

        @Override
        public TradeEntryStatus getStatus() {
            if (statusValue == null || isEmpty(statusValue.getText())) {
                return null;
            }
            return TradeEntryStatus.valueOf(statusValue.getText().trim());
        }
        
        @Override
        public String getProjectName() {
            if (displayedBugProject == null || isEmpty(displayedIssueLabel.getText())) {
                return null;
            }
            return displayedBugProject;
        }

        @Override
        public String getSynopsis() {
            if (synopsis == null || isEmpty(synopsis.getText())) {
                return "";
            }
            return synopsis.getText();
        }

        @Override
        public String getDescription() {
            if (descriptionValue == null || isEmpty(descriptionValue.getText())) {
                return "";
            }
            return descriptionValue.getText();
        }
        
        @Override
        public String getUser() {
            if (owner == null || isEmpty(owner.getText())) {
                return "";
            }
            return owner.getText();
        }

		@Override
		public void setId(String id) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setProjectName(String projectName) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setStatus(TradeEntryStatus status) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setSynopsis(String synopsis) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setDescription(String description) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getItemId() {
			if (itemId == null || isEmpty(itemId.getText())) {
                return "";
            }
            return itemId.getText();
		}

		@Override
		public void setItemId(String itemId) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String[] getAcceptableTrades() {
			
			List<String> valueList = new ArrayList<String>();
			if(acceptableTrades != null && acceptableTrades.size() > 0)
			{
				Iterator<CheckBox> itr = acceptableTrades.iterator();
				while(itr.hasNext())
				{
					CheckBox cb = itr.next();
					if(cb.isSelected())
					{
						valueList.add(cb.getId());
					}
				}
			}
			
			return valueList.toArray(new String[0]);
		}

		@Override
		public void setUser(String user) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setAcceptableTrades(String[] acceptableTrades) {
			// TODO Auto-generated method stub
			
		}
    }

    private SaveState computeSaveState(TradeEntry edited, TradeEntry issue) {
        try {
            // These fields are not editable - so if they differ they are invalid
            // and we cannot save.
            if (!equal(edited.getId(), issue.getId())) {
                return SaveState.INVALID;
            }
            if (!equal(edited.getProjectName(), issue.getProjectName())) {
                return SaveState.INVALID;
            }

            // If these fields differ, the issue needs saving.
            if (!equal(edited.getStatus(), issue.getStatus())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getSynopsis(), issue.getSynopsis())) {
                return SaveState.UNSAVED;
            }
            if(!equal(edited.getItemId(), issue.getItemId()))
            {
            	return SaveState.UNSAVED;
            }
            
            if(!equal(edited.getUser(), issue.getUser()))
            {
            	return SaveState.UNSAVED;
            }
            
            if (!equal(edited.getDescription(), issue.getDescription())) {
                return SaveState.UNSAVED;
            }
            
            if(!arraysEqual(edited.getAcceptableTrades(),issue.getAcceptableTrades()))
            {
            	return SaveState.UNSAVED;
            }
            
        } catch (Exception x) {
            // If there's an exception, some fields are invalid.
            return SaveState.INVALID;
        }
        // No field is invalid, no field needs saving.
        return SaveState.UNCHANGED;
    }

    private void updateDeleteIssueButtonState() {
        boolean disable = true;
        if (deleteTradeEntry != null && table != null) {
            final boolean nothingSelected = table.getSelectionModel().getSelectedItems().isEmpty();
            disable = nothingSelected;
        }
        if (deleteTradeEntry != null) {
        	deleteTradeEntry.setDisable(disable);
        }
    }

    private void updateSaveIssueButtonState() {
        boolean disable = true;
        if (saveTradeEntry != null && table != null) {
            final boolean nothingSelected = table.getSelectionModel().getSelectedItems().isEmpty();
            disable = nothingSelected;
        }
        if (disable == false) {
            disable = computeSaveState(new DetailsData(), getSelectedTradeEntry()) != SaveState.UNSAVED;
        }
        if (saveTradeEntry != null) {
            saveTradeEntry.setDisable(disable);
        }
    }

    // Configure the table widget: set up its column, and register the
    // selection changed listener.
    private void configureTable() {
        colName.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSynopsis.setCellValueFactory(new PropertyValueFactory<>("synopsis"));
        colOwner.setCellValueFactory(new PropertyValueFactory<>("user"));

        // In order to limit the amount of setup in Getting Started we set the width
        // of the 3 columns programmatically but one can do it from SceneBuilder.
        colName.setPrefWidth(75);
        colOwner.setPrefWidth(75);
        colSynopsis.setPrefWidth(443);

        colName.setMinWidth(75);
        colOwner.setMinWidth(75);
        colSynopsis.setMinWidth(443);

        colName.setMaxWidth(750);
        colOwner.setMaxWidth(750);
        colSynopsis.setMaxWidth(4430);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.setItems(tableContent);
        assert table.getItems() == tableContent;

        final ObservableList<ObservableTradeEntry> tableSelection = table.getSelectionModel().getSelectedItems();

        tableSelection.addListener(tableSelectionChanged);
    }

    /**
     * Return the name of the project currently selected, or null if no project
     * is currently selected.
     *
     */
    public String getSelectedProject() {
        if (model != null && list != null) {
            final ObservableList<String> selectedProjectItem = list.getSelectionModel().getSelectedItems();
            final String selectedProject = selectedProjectItem.get(0);
            return selectedProject;
        }
        return null;
    }

    public ObservableTradeEntry getSelectedTradeEntry() {
        if (model != null && table != null) {
            List<ObservableTradeEntry> selectedTradeEntries = table.getSelectionModel().getSelectedItems();
            if (selectedTradeEntries.size() == 1) {
                final ObservableTradeEntry selectedIssue = selectedTradeEntries.get(0);
                return selectedIssue;
            }
        }
        return null;
    }
    
    /**
     * Listen to changes in the list selection, and updates the table widget and
     * DeleteIssue and NewIssue buttons accordingly.
     */
    private final ChangeListener<String> projectItemSelected = new ChangeListener<String>() {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            projectUnselected(oldValue);
            projectSelected(newValue);
        }
    };

    // Called when a project is unselected.
    private void projectUnselected(String oldProjectName) {
        if (oldProjectName != null) {
            displayedTradeEntries.removeListener(projectTradeEntriesListener);
            displayedTradeEntries = null;
            table.getSelectionModel().clearSelection();
            table.getItems().clear();
            if (newTradeEntry != null) {
                newTradeEntry.setDisable(true);
            }
            if (deleteTradeEntry != null) {
            	deleteTradeEntry.setDisable(true);
            }
        }
    }

    // Called when a project is selected.
    private void projectSelected(String newProjectName) {
        if (newProjectName != null) {
            table.getItems().clear();
            displayedTradeEntries = model.getTradeEntryIds(newProjectName);
            for (String id : displayedTradeEntries) {
                final ObservableTradeEntry issue = model.getTradeEntry(id);
                table.getItems().add(issue);
            }
            displayedTradeEntries.addListener(projectTradeEntriesListener);
            if (newTradeEntry != null) {
                newTradeEntry.setDisable(false);
            }
            updateDeleteIssueButtonState();
            updateSaveIssueButtonState();
        }
    }

    private void configureDetails() {
        if (details != null) {
            details.setVisible(false);
        }

        if (details != null) {
            details.addEventFilter(EventType.ROOT, new EventHandler<Event>() {

                @Override
                public void handle(Event event) {
                    if (event.getEventType() == MouseEvent.MOUSE_RELEASED
                            || event.getEventType() == KeyEvent.KEY_RELEASED) {
                        updateSaveIssueButtonState();
                    }
                }
            });
        }
    }
    
    
}
