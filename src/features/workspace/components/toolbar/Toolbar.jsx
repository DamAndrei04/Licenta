import DraggableItem from '../draggableItem/DraggableItem';
import { ComponentRegistry } from "@/components/registry/ComponentRegistry";
import './Toolbar.css';
import useBuilderStore from '@/store/useBuilderStore';
import { exportToJSON } from "@/utils/ExportToJson";
import { Undo2, Redo2, Trash2, Download } from 'lucide-react';

const Toolbar = ({ handleClear }) => {
    const undo = () => useBuilderStore.temporal.getState().undo();
    const redo = () => useBuilderStore.temporal.getState().redo();

    const handleExport = () => {
        exportToJSON();
    };

    return (
        <div className="toolbar">
            <h2>TOOLBAR</h2>
            <div className="toolbar-items">
                {Object.entries(ComponentRegistry).map(([key, { displayName }]) => (
                    <DraggableItem key={key} type={key} displayName={displayName} />
                ))}
            </div>

            <button className="export-button" onClick={handleExport} title="Export to JSON">
                <Download size={18} />
                Export
            </button>
            <button className="clear-button" onClick={handleClear}>
                <Trash2 size={18} />
                Clear All
            </button>
            <button className="undo-button" onClick={undo} title="Undo">
                <Undo2 size={18} />
                Undo
            </button>
            <button className="redo-button" onClick={redo} title="Redo">
                <Redo2 size={18} />
                Redo
            </button>
        </div>
    );
};

export default Toolbar;
