import React, { useState } from "react";
import useBuilderStore from "@/store/useBuilderStore";
import './ComponentsTree.css';
import { ChevronRight , ChevronDown } from 'lucide-react';

const ComponentsTree = ({ nodeId, depth = 0 }) => {
    const droppedItems = useBuilderStore(state => state.droppedItems);
    const selectedId = useBuilderStore(state => state.selectedId);
    const selectItem = useBuilderStore(state => state.selectItem);

    const node = droppedItems[nodeId];
    const [collapsed, setCollapsed] = useState(false);

    if (!node) return null;

    return (
        <div className="node" style={{ paddingLeft: depth * 16 }}>
            <div
                className={`node-item ${selectedId === nodeId ? "selected" : ""}`}
                onClick={() => selectItem(nodeId)}
            >
                {node.childrenIds.length > 0 && (
                    <button
                        className="toggle-btn"
                        onClick={(e) => {
                            e.stopPropagation();
                            setCollapsed(!collapsed);
                        }}
                    >
                        {collapsed ? <ChevronRight /> : <ChevronDown />}
                    </button>
                )}
                <span className="node-label">{node.type} ({nodeId})</span>
            </div>

            {!collapsed && node.childrenIds.length > 0 && (
                <div className="node-children">
                    {node.childrenIds.map(childId => (
                        <ComponentsTree key={childId} nodeId={childId} depth={depth + 1} />
                    ))}
                </div>
            )}
        </div>
    );
};

export default ComponentsTree;
