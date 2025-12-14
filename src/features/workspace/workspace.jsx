import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import './workspace.css';

import Toolbar from './components/toolbar/Toolbar';
import PagesAndLayersTab from './components/pagesAndLayersTab/PagesAndLayersTab';
import CustomizeTab from './components/customizeTab/CustomizeTab';
import DropZone from './components/dropZone/DropZone';
import useBuilderStore from '@/store/useBuilderStore';
import {useEffect, useMemo} from "react";

export default function Main() {
    console.log(' workspace.jsx RENDER');
    // Use selectors to only subscribe to specific pieces of state
    const selectedId = useBuilderStore((state) => state.selectedId);
    const droppedItems = useBuilderStore((state) => state.droppedItems);
    const rootIds = useBuilderStore((state) => state.rootIds);
    console.log(' State:', {
        itemCount: droppedItems.length,
        selectedId
    });

    // Get actions
    const handleDrop = useBuilderStore((state) => state.handleDrop);
    const updateItem = useBuilderStore((state) => state.updateItem);
    const selectItem = useBuilderStore((state) => state.selectItem);
    const deselectAll = useBuilderStore((state) => state.deselectAll);
    const moveItem = useBuilderStore((state) => state.moveItem);
    const handleClear = useBuilderStore((state) => state.handleClear);

    // Helper function that accesses current store state
    const getChildren = (parentId) => {
        const parent = droppedItems[parentId];
        return parent ? parent.childrenIds.map(childrenId => droppedItems[childrenId]) : [];
    };

    // Derive values
    const rootItems = useMemo(
        () => rootIds.map(id => droppedItems[id]),
        [droppedItems, rootIds]
    );

    const selectedElement = useMemo(
        () => selectedId ? droppedItems[selectedId] : null,
        [droppedItems, selectedId]
    );
    console.log(' State:', {
        itemCount: droppedItems.length,
        selectedId
    });

    useEffect(() => {
        const handleKeyDown = (e) => {
            if (e.target.tagName === 'INPUT' || e.target.tagName === 'TEXTAREA') {
                return;
            }

            if (e.ctrlKey && e.key === 'z' && !e.shiftKey) {
                e.preventDefault();
                useBuilderStore.temporal.getState().undo();
            }

            if (e.ctrlKey  && e.key === 'y' ) {
                e.preventDefault();
                useBuilderStore.temporal.getState().redo();
            }
        };

        window.addEventListener('keydown', handleKeyDown);
        return () => window.removeEventListener('keydown', handleKeyDown);
    }, []);

    return (
        <DndProvider backend={HTML5Backend}>
            <div className="container">
                <div className="wrapper">
                    <Toolbar handleClear={handleClear} />
                    <div className="gridContainer">
                        <PagesAndLayersTab />
                        <DropZone
                            droppedItems={rootItems} // only pass root item ( parentId = null )
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