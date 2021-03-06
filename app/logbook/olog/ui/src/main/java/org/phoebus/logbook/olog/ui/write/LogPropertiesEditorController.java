package org.phoebus.logbook.olog.ui.write;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import org.phoebus.logbook.Property;
import org.phoebus.logbook.PropertyImpl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class LogPropertiesEditorController {

    // Model
    private LogEntryModel model;
    ObservableList<Property> availableProperties = FXCollections.observableArrayList();
    ObservableList<Property> selectedProperties = FXCollections.observableArrayList();

    @FXML
    TreeTableView<PropertyTreeNode> selectedPropertiesTree;

    @FXML
    TreeTableColumn name;
    @FXML
    TreeTableColumn value;

    @FXML
    TableView<Property> availablePropertiesView;

    @FXML
    TableColumn propertyName;

    public LogPropertiesEditorController()
    {
    }

    public LogPropertiesEditorController(LogEntryModel model)
    {
        this.model = model;
    }

    @FXML
    public void initialize()
    {
        if (this.model != null)
        {
            // this.model.fetchProperties();
            availableProperties = this.model.getProperties();
            selectedProperties = this.model.getSelectedProperties();
        }
        selectedProperties.addListener((ListChangeListener<Property>) p -> constructTree(selectedProperties));

        name.setMaxWidth(1f * Integer.MAX_VALUE * 40);
        name.setCellValueFactory(
                new Callback<TreeTableColumn.CellDataFeatures<PropertyTreeNode, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<PropertyTreeNode, String> p) {
                        return p.getValue().getValue().nameProperty();
                    }
                });

        value.setMaxWidth(1f * Integer.MAX_VALUE * 60);
        value.setCellValueFactory(
                new Callback<TreeTableColumn.CellDataFeatures<PropertyTreeNode, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<PropertyTreeNode, String> p) {
                        return p.getValue().getValue().valueProperty();
                    }
                });
        value.setEditable(true);
        value.setCellFactory(new Callback<TreeTableColumn<PropertyTreeNode, String>,
                                          TreeTableCell<PropertyTreeNode, String>>() {

            @Override
            public TreeTableCell<PropertyTreeNode, String> call(TreeTableColumn<PropertyTreeNode, String> param) {
                return new TreeTableCell<PropertyTreeNode, String> () {

                    private TextField textField;

                    @Override
                    public void startEdit() {
                        super.startEdit();

                        if (textField == null) {
                            createTextField();
                        }
                        setText(null);
                        setGraphic(textField);
                        textField.selectAll();
                        textField.requestFocus();
                    }

                    @Override
                    public void cancelEdit() {
                        super.cancelEdit();
                        setText((String) getItem());
                        setGraphic(getTreeTableRow().getGraphic());
                    }

                    @Override
                    public void updateItem(String item, boolean empty){
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            try {
                                URL url = new URL(item);
                                final Hyperlink link = new Hyperlink(url.toString());
                                setGraphic(link);
                            } catch (Exception e) {
                                setGraphic(new Label(item));
                            }
                        }
                    }

                    private void createTextField() {
                        textField = new TextField(getString());
                        textField.setOnKeyReleased((KeyEvent t) -> {
                            if (t.getCode() == KeyCode.ENTER) {
                                commitEdit(textField.getText());
                            } else if (t.getCode() == KeyCode.ESCAPE) {
                                cancelEdit();
                            }
                        });
                        // Update when cell exits edit mode.
                        // This will ensure that value is committed when focus is lost.
                        this.editingProperty().addListener((o, n, e) -> {
                            if(!e){
                                updateItem(textField.getText(), false);
                            }
                        });
                    }
                    private String getString() {
                        return getItem() == null ? "" : getItem().toString();
                    }
                };
            }
        });

        // Hide the headers
        selectedPropertiesTree.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                // Get the table header
                Pane header = (Pane) selectedPropertiesTree.lookup("TableHeaderRow");
                if(header!=null && header.isVisible()) {
                    header.setMaxHeight(0);
                    header.setMinHeight(0);
                    header.setPrefHeight(0);
                    header.setVisible(false);
                    header.setManaged(false);
                }
            }
        });

        propertyName.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Property, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<Property, String> p) {
                        return new SimpleStringProperty(p.getValue().getName());
                    }
                });
        availablePropertiesView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount() > 1) {
                    availablePropertySelection();
                }
            }
        });
        availablePropertiesView.setEditable(false);
        availablePropertiesView.setItems(availableProperties);
    }

    private void constructTree(Collection<Property> properties) {
        if (properties != null && !properties.isEmpty())
        {
            TreeItem root = new TreeItem(new PropertyTreeNode("properties", " "));
            AtomicReference<Double> rowCount = new AtomicReference<>((double) 1);
            root.getChildren().setAll(properties.stream().map(property -> {
                PropertyTreeNode node = new PropertyTreeNode(property.getName(), " ");
                rowCount.set(rowCount.get() + 1);
                TreeItem<PropertyTreeNode> treeItem = new TreeItem<>(node);
                property.getAttributes().entrySet().stream().forEach(entry -> {
                    rowCount.set(rowCount.get() + 1);
                    treeItem.getChildren().add(new TreeItem<>(new PropertyTreeNode(entry.getKey(), entry.getValue())));
                });
                treeItem.setExpanded(true);
                return treeItem;
            }).collect(Collectors.toSet()));
            selectedPropertiesTree.setRoot(root);
            selectedPropertiesTree.setShowRoot(false);
        }
    }

    /**
     * @return The list of logentry properties
     */
    public List<Property> getProperties()
    {
        List<Property> treeProperties = new ArrayList<>();
        if(selectedPropertiesTree.getRoot() == null){
            return treeProperties;
        }
        selectedPropertiesTree.getRoot().getChildren().stream().forEach(node -> {
            Map<String, String> att = node.getChildren().stream()
                    .map(TreeItem::getValue)
                    .collect(Collectors.toMap(PropertyTreeNode::getName, PropertyTreeNode::getValue));
            Property property = PropertyImpl.of(node.getValue().getName(), att);
            treeProperties.add(property);
        });
        return treeProperties;
    }

    public void setSelectedProperties(List<Property> properties) {
        selectedProperties.setAll(properties);
    }

    public void setAvailableProperties(List<Property> properties) {
        availableProperties.setAll(properties);
    }

    /**
     * Move the user selected available properties from the available list to the selected properties tree view
     */
    @FXML
    public void availablePropertySelection() {
        ObservableList<Property> userSelectedProperties = availablePropertiesView.getSelectionModel().getSelectedItems();
        // add user selected properties
        userSelectedProperties.forEach(selectedProperties::add);
        // remove the properties from the list of available properties
        availableProperties.removeAll(userSelectedProperties);
    }

    private static class PropertyTreeNode
    {
        private SimpleStringProperty name;
        private SimpleStringProperty value;

        public SimpleStringProperty nameProperty() {
            if (name == null) {
                name = new SimpleStringProperty(this, "name");
            }
            return name;
        }

        public SimpleStringProperty valueProperty() {
            if (value == null) {
                value = new SimpleStringProperty(this, "value");
            }
            return value;
        }

        private PropertyTreeNode(String name, String value) {
            this.name = new SimpleStringProperty(name);
            this.value = new SimpleStringProperty(value);
        }

        public String getName() {
            return name.get();
        }
        public void setName(String name) {
            this.name.set(name);
        }
        public String getValue() {
            return value.get();
        }
        public void setValue(String value) {
            this.value.set(value);
        }
    }
}
