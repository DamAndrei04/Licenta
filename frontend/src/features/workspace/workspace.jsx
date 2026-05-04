import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import './workspace.css';

import Toolbar from './components/toolbar/Toolbar';
import LayersTab from '@/features/workspace/components/layers/LayersTab';
import CustomizeTab from './components/customizeTab/CustomizeTab';
import DropZone from './components/dropZone/DropZone';
import PagesTab from "@/features/workspace/components/pages/PagesTab";
import useBuilderStore from '@/store/useBuilderStore';
import {getProjectWorkspace, saveProjectState} from '@/api/WorkspaceService';
import { sendPromptToAgent } from '@/api/AgentService';
import { getProjectById } from '@/api/ProjectService';
import { useEffect, useMemo, useState } from "react";
import { useParams } from 'react-router-dom';

export default function Main() {
    const { projectId } = useParams();

    const [project, setProject] = useState(null);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [generating, setGenerating] = useState(false);
    const [prompt, setPrompt] = useState('');

    // Store state
    const activePageId = useBuilderStore((state) => state.activePageId);
    const pages = useBuilderStore((state) => state.pages);
    const page = pages[activePageId] || { droppedItems: {}, rootIds: [], selectedId: null };
    const { droppedItems, rootIds, selectedId } = page;

    // Store actions
    const handleDrop = useBuilderStore((state) => state.handleDrop);
    const updateItem = useBuilderStore((state) => state.updateItem);
    const selectItem = useBuilderStore((state) => state.selectItem);
    const deselectAll = useBuilderStore((state) => state.deselectAll);
    const handleClear = useBuilderStore((state) => state.handleClear);
    const deleteItem = useBuilderStore((state) => state.deleteItem);
    const loadState = useBuilderStore((state) => state.loadState);
    const importState = useBuilderStore((state) => state.importState);

    // Load project + existing workspace on mount
    useEffect(() => {
        if (!projectId) return;

        const fetchWorkspace = async () => {
            try {
                const [projectRes, workspaceRes] = await Promise.all([
                    getProjectById(projectId),
                    getProjectWorkspace(projectId),
                ]);

                setProject(projectRes.data);

                const backendPages = workspaceRes.data?.pages;
                if (backendPages && Object.keys(backendPages).length > 0) {
                    loadState(backendPages);
                }
            } catch (err) {
                console.error('Failed to load workspace', err);
            } finally {
                setLoading(false);
            }
        };

        fetchWorkspace();
    }, [projectId]);

    // Save current canvas state to backend
    const handleSave = async () => {
        setSaving(true);
        try {
            await saveProjectState({
                projectId: Number(projectId),
                pages,
            });
        } catch (err) {
            console.error('Failed to save workspace', err);
        } finally {
            setSaving(false);
        }
    };

    // Send prompt to MAS agent, merge generated pages into store
    const handleGenerate = async () => {
        if (!prompt.trim()) return;
        setGenerating(true);
        try {
            const res = await sendPromptToAgent({ prompt });
            const generatedPages = res.data?.pages;
            if (generatedPages) {
                importState({ pages: { ...pages, ...generatedPages } });
            }
        } catch (err) {
            console.error('Agent generation failed', err);
        } finally {
            setGenerating(false);
            setPrompt('');
        }
    };

    const getChildren = (parentId) => {
        const parent = droppedItems[parentId];
        return parent ? parent.childrenIds.map(childrenId => droppedItems[childrenId]) : [];
    };

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
            if (e.target.tagName === 'INPUT' || e.target.tagName === 'TEXTAREA') return;

            if (e.ctrlKey && e.key === 'z' && !e.shiftKey) {
                e.preventDefault();
                useBuilderStore.temporal.getState().undo();
            }

            if (e.ctrlKey && e.key === 'y') {
                e.preventDefault();
                useBuilderStore.temporal.getState().redo();
            }
        };

        window.addEventListener('keydown', handleKeyDown);
        return () => window.removeEventListener('keydown', handleKeyDown);
    }, []);

    if (loading) {
        return (
            <div className="container">
                <div className="workspace-loading">Loading project...</div>
            </div>
        );
    }

    return (
        <DndProvider backend={HTML5Backend}>
            <div className="container">
                <div className="wrapper">
                    <Toolbar
                        handleClear={handleClear}
                        projectName={project?.name}
                        onSave={handleSave}
                        saving={saving}
                    />
                    <div className="gridContainer">
                        <div className="pagesAndLayerTab">
                            <PagesTab />
                            <LayersTab
                                droppedItems={droppedItems}
                                rootIds={rootIds}
                            />
                        </div>
                        <DropZone
                            droppedItems={rootItems}
                            allItems={droppedItems}
                            getChildren={getChildren}
                            onDrop={handleDrop}
                            updateItem={updateItem}
                            selectItem={selectItem}
                            deselectAll={deselectAll}
                            selectedId={selectedId}
                            deleteItem={deleteItem}
                        />
                        <CustomizeTab
                            selectedElement={selectedElement}
                            allItems={droppedItems}
                            updateItem={updateItem}
                            setSelectedElement={selectItem}
                        />
                    </div>

                    {/* MAS Prompt bar */}
                    <div className="prompt-bar">
                        <input
                            type="text"
                            className="prompt-input"
                            placeholder="Describe the UI you want to generate..."
                            value={prompt}
                            onChange={(e) => setPrompt(e.target.value)}
                            onKeyDown={(e) => e.key === 'Enter' && !generating && handleGenerate()}
                        />
                        <button
                            className="prompt-btn"
                            onClick={handleGenerate}
                            disabled={generating || !prompt.trim()}
                        >
                            {generating ? 'Generating...' : 'Generate'}
                        </button>
                    </div>
                </div>
            </div>
        </DndProvider>
    );
}