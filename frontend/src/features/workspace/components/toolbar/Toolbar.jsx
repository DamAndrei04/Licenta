import DraggableItem from '../draggableItem/DraggableItem';
import { ComponentRegistry } from "@/components/registry/ComponentRegistry";
import './Toolbar.css';
import useBuilderStore from '@/store/useBuilderStore';
import { exportToJSON } from "@/utils/ExportToJson";
import { Undo2, Redo2, Trash2, Download, Upload } from 'lucide-react';
import { useRef } from 'react';

const Toolbar = ({ handleClear }) => {

    const fileInputRef = useRef(null);
    const importState = useBuilderStore((state) => state.importState);

    const undo = () => useBuilderStore.temporal.getState().undo();
    const redo = () => useBuilderStore.temporal.getState().redo();

    const handleExport = () => {
        exportToJSON();
    };

    const handleImportClick = () => {
        fileInputRef.current?.click();
    };

    const handleFileImport = (event) => {
        const file = event.target.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = (e) => {
            try {
                const json = JSON.parse(e.target.result);
                importState(json);
            } catch (error) {
                alert('Failed to parse JSON file');
                console.error(error);
            }
        };
        reader.readAsText(file);

        event.target.value = '';
    };

    return (
        <div className="toolbar">
            <h2>TOOLBAR</h2>
            <div className="toolbar-items">
                {Object.entries(ComponentRegistry).map(([key, { displayName }]) => (
                    <DraggableItem key={key} type={key} displayName={displayName} />
                ))}
            </div>

            <input
                ref={fileInputRef}
                type="file"
                accept=".json"
                onChange={handleFileImport}
                style={{ display: 'none' }}
            />

            <button className="import-button" onClick={handleImportClick} title="Import from JSON">
                <Upload size={18} />
                Import
            </button>

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
