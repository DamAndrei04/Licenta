import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { useState } from 'react';
import './workspace.css';

import Toolbar from './components/toolbar/Toolbar';
import PagesAndLayersTab from './components/pagesAndLayersTab/PagesAndLayersTab';
import CustomizeTab from './components/customizeTab/CustomizeTab';
import DropZone from './components/dropZone/DropZone';

export default function Main() {
    const [droppedItems, setDroppedItems] = useState([]);
    const [selectedId, setSelectedId] = useState(null);

    const handleDrop = (item, x, y, parentId = null) => {
        const newItem = {
            id: `${item.componentType}-${Date.now()}`,
            type: item.componentType,  // store component type from registry
            x: parentId ? x : x, // relative x to parent
            y: parentId ? y : y, // relative y to parent
            width: item.defaultSize?.width || 100,  // add default width
            height: item.defaultSize?.height || 40, // add default height
            parentId: parentId,
            children: [],
            props: {},
        };

        setDroppedItems(prev => {
            const updated = [...prev, newItem];

            // if dropped into a parent, add to parent's children
            if (parentId) {
                return updated.map(el =>
                    el.id === parentId
                        ? { ...el, children: [...el.children, newItem.id] }
                        : el
                );
            }

            return updated;
        });
    };

    const updateItem = (id, updates) => {
        setDroppedItems(prev =>
            prev.map(item => (item.id === id ? { ...item, ...updates } : item))
        );
    };

    const selectItem = (id) => {
        setSelectedId(id);
    };

    const selectedElement = droppedItems.find(item => item.id === selectedId);

    const deselectAll = () => {
        setSelectedId(null);
    };

    const moveItem = (id, x, y) => {
        setDroppedItems(prev =>
            prev.map(el => (el.id === id ? { ...el, x, y } : el))
        );
    };

    const handleClear = () => {
        if (window.confirm('Are you sure you want to clear the whiteboard?')) {
            setDroppedItems([]);
            setSelectedId(null);
        }
    };

    const getRootItems = () => droppedItems.filter(item => !item.parentId);

    const getChildren = (parentId) =>
        droppedItems.filter(item => item.parentId === parentId);

    return (
        <DndProvider backend={HTML5Backend}>
            <div className="container">
                <div className="wrapper">
                    <Toolbar handleClear={handleClear} />
                    <div className="gridContainer">
                        <PagesAndLayersTab />
                        <DropZone
                            droppedItems={getRootItems()} // only pass root item ( parentId = null )
                            allItems={droppedItems} // pass all items
                            getChildren={getChildren}
                            onDrop={handleDrop}
                            moveItem={moveItem}
                            updateItem={updateItem}
                            selectItem={selectItem}
                            deselectAll={deselectAll}
                            selectedId={selectedId}
                        />
                        <CustomizeTab
                            selectedElement={selectedElement}
                            allItems={droppedItems}
                            updateItem={updateItem}
                            setSelectedElement={selectItem}
                        />
                    </div>
                </div>
            </div>
        </DndProvider>
    );
}
