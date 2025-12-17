import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import './workspace.css';

import Toolbar from './components/toolbar/Toolbar';
import LayersTab from '@/features/workspace/components/layers/LayersTab';
import CustomizeTab from './components/customizeTab/CustomizeTab';
import DropZone from './components/dropZone/DropZone';
import useBuilderStore from '@/store/useBuilderStore';
import {useEffect, useMemo} from "react";
import PagesTab from "@/features/workspace/components/pages/PagesTab";

export default function Main() {
    console.log(' workspace.jsx RENDER');
    // Use selectors to only subscribe to specific pieces of state
    const activePageId = useBuilderStore((state) => state.activePageId);
    const pages = useBuilderStore((state) => state.pages);
    const page = pages[activePageId] || { droppedItems: {}, rootIds: [], selectedId: null };
    const { droppedItems, rootIds, selectedId } = page;

    console.log(' State:', {
        itemCount: Object.keys(droppedItems).length,
        selectedId
    });

    // Get actions
    const handleDrop = useBuilderStore((state) => state.handleDrop);
    const updateItem = useBuilderStore((state) => state.updateItem);
    const selectItem = useBuilderStore((state) => state.selectItem);
    const deselectAll = useBuilderStore((state) => state.deselectAll);
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
                        <div className="pagesAndLayerTab">
                            <PagesTab />
                            <LayersTab
                                droppedItems={droppedItems}
                                rootIds={rootIds}
                            />
                        </div>
                        <DropZone
                            droppedItems={rootItems} // only pass root item ( parentId = null )
                            allItems={droppedItems} // pass all items
                            getChildren={getChildren}
                            onDrop={handleDrop}
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