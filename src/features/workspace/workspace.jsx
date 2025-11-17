import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { useState } from 'react';
import './workspace.css';

import Toolbar from './components/Toolbar';
import PagesAndLayersTab from './components/PagesAndLayersTab';
import CustomizeTab from './components/CustomizeTab';
import DropZone from './components/DropZone';

export default function Main() {
    const [droppedItems, setDroppedItems] = useState([]);

    const handleDrop = (item, x, y) => {
        setDroppedItems(prev => [
            ...prev,
            { ...item, x, y, id: Date.now() },
        ]);
    };

    const moveItem = (id, x, y) => {
        setDroppedItems(prev =>
            prev.map(el => (el.id === id ? { ...el, x, y } : el))
        );
    };

    const handleClear = () => {
        if (window.confirm('Are you sure you want to clear the whiteboard?')) {
            setDroppedItems([]);
        }
    };

    return (
        <DndProvider backend={HTML5Backend}>
            <div className="container">
                <div className="wrapper">
                    <Toolbar handleClear={handleClear} />
                    <div className="gridContainer">
                        <PagesAndLayersTab />
                        <DropZone droppedItems={droppedItems} onDrop={handleDrop} moveItem={moveItem} />
                        <CustomizeTab />
                    </div>
                </div>
            </div>
        </DndProvider>
    );
}
