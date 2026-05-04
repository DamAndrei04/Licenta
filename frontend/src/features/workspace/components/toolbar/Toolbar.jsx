import DraggableItem from '../draggableItem/DraggableItem';
import { ComponentRegistry } from "@/components/registry/ComponentRegistry";
import './Toolbar.css';
import useBuilderStore from '@/store/useBuilderStore';
import { exportToJSON } from "@/utils/ExportToJson";
import { exportToSourceCode } from "@/utils/ExportToSourceCode";
import { Undo2, Redo2, Trash2, Download, Upload, Save, Code, ChevronDown, FileJson } from 'lucide-react';
import { useRef, useState, useEffect } from 'react';
import { createPortal } from 'react-dom';
import AgentChat from "@/features/workspace/components/agentChat/AgentChat";

const Toolbar = ({ handleClear, onSave, saving }) => {
    const fileInputRef = useRef(null);
    const exportToggleRef = useRef(null);
    const [exportOpen, setExportOpen] = useState(false);
    const [dropdownStyle, setDropdownStyle] = useState({});

    const importState = useBuilderStore((state) => state.importState);
    const undo = () => useBuilderStore.temporal.getState().undo();
    const redo = () => useBuilderStore.temporal.getState().redo();

    // Calculate portal position from toggle button — same pattern as AgentChat
    useEffect(() => {
        if (!exportOpen || !exportToggleRef.current) return;
        const rect = exportToggleRef.current.getBoundingClientRect();
        setDropdownStyle({
            position: 'fixed',
            top: rect.bottom + 6,
            left: rect.left,
        });
    }, [exportOpen]);

    // Close on outside click — same pattern as AgentChat
    useEffect(() => {
        if (!exportOpen) return;
        const handler = (e) => {
            if (
                exportToggleRef.current?.contains(e.target) ||
                document.getElementById('export-portal')?.contains(e.target)
            ) return;
            setExportOpen(false);
        };
        document.addEventListener('pointerdown', handler);
        return () => document.removeEventListener('pointerdown', handler);
    }, [exportOpen]);

    const handleImportClick = () => fileInputRef.current?.click();

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

    const handleExportJSON = () => {
        exportToJSON();
        setExportOpen(false);
    };

    const handleExportHTML = () => {
        exportToSourceCode();
        setExportOpen(false);
    };

    const exportDropdown = (
        <div
            id="export-portal"
            className={`export-dropdown ${exportOpen ? 'export-dropdown--open' : ''}`}
            style={dropdownStyle}
        >
            <button className="export-option" onClick={handleExportJSON}>
                <FileJson size={15} />
                Export as JSON
            </button>
            <button className="export-option" onClick={handleExportHTML}>
                <Code size={15} />
                Export as HTML
            </button>
        </div>
    );

    return (
        <div className="toolbar">
            <h2>TOOLBAR</h2>
            <div className="toolbar-items">
                {Object.entries(ComponentRegistry).map(([key, { displayName }]) => (
                    <DraggableItem key={key} type={key} displayName={displayName} />
                ))}
            </div>
            <AgentChat />
            <div className="toolbar-actions">
                <input
                    ref={fileInputRef}
                    type="file"
                    accept=".json"
                    onChange={handleFileImport}
                    style={{ display: 'none' }}
                />

                <button className="save-button" onClick={onSave} disabled={saving} title="Save to database">
                    <Save size={18} />
                    {saving ? 'Saving...' : 'Save'}
                </button>

                <button className="import-button" onClick={handleImportClick} title="Import from JSON">
                    <Upload size={18} />
                    Import
                </button>

                <button
                    ref={exportToggleRef}
                    className={`export-toggle ${exportOpen ? 'export-toggle--open' : ''}`}
                    onClick={() => setExportOpen((prev) => !prev)}
                    title="Export options"
                >
                    <Download size={18} />
                    Export
                    <ChevronDown
                        size={14}
                        className={`export-chevron ${exportOpen ? 'export-chevron--up' : ''}`}
                    />
                </button>

                {createPortal(exportDropdown, document.body)}

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
        </div>
    );
};

export default Toolbar;