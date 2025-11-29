import { useRef } from "react";
import { ComponentRegistry } from "@/components/registry/ComponentRegistry";
import ResizeHandle from "../resizeHandlers/ResizeHandle";
import "./DroppedItem.css";
import { useDrop } from "react-dnd";

const DroppedItem = ({ item, allItems, getChildren, onDrop, selectItem, updateItem, selectedId }) => {
    const interactionRef = useRef(null);

    const registryItem = ComponentRegistry[item.type];
    const Component = registryItem.component;
    const mergedProps = { ...registryItem.defaultProps, ...item.props };
    const canHaveChildren = registryItem.canHaveChildren || false;
    const children = getChildren(item.id);

    const [{ isOver }, drop] = useDrop(() => ({
        accept: ["COMPONENT", "DROPPED_ITEM"],
        canDrop: () => canHaveChildren, // Only allow drops if component accepts children
        drop: (draggedItem, monitor) => {
            if (monitor.didDrop()) {
                return;
            }

            const offset = monitor.getClientOffset();
            const containerRect = interactionRef.current.getBoundingClientRect();

            // Calculate position relative to this container
            const x = offset.x - containerRect.left;
            const y = offset.y - containerRect.top;

            if (draggedItem.id) {
                // Moving existing item into this container
                updateItem(draggedItem.id, {
                    x,
                    y,
                    parentId: item.id
                });
            } else {
                // Adding new item as child
                onDrop(draggedItem, x, y, item.id);
            }
        },
        collect: (monitor) => ({
            isOver: monitor.isOver({ shallow: true }),
        }),
    }), [item.id, canHaveChildren]);

    // DRAGGING LOGIC
    const startDrag = (e) => {
        if (e.target.classList.contains('resize-handle')) {
            return;
        }

        e.stopPropagation();
        selectItem(item.id);

        const startX = e.clientX;
        const startY = e.clientY;
        const startLeft = item.x;
        const startTop = item.y;

        const parent = item.parentId ? allItems.find(p => p.id === item.parentId) : null;
        const parentWidth = parent ? (parent.width || ComponentRegistry[parent.type]?.defaultSize.width) : null;
        const parentHeight = parent ? (parent.height || ComponentRegistry[parent.type]?.defaultSize.height) : null;

        const onMove = (ev) => {
            let newX = startLeft + ev.clientX - startX;
            let newY = startTop + ev.clientY - startY;

            // Constrain to parent bounds if nested
            if (parent && parentWidth && parentHeight) {
                const itemWidth = item.width || registryItem.defaultSize.width;
                const itemHeight = item.height || registryItem.defaultSize.height;

                newX = Math.max(0, Math.min(newX, parentWidth - itemWidth));
                newY = Math.max(0, Math.min(newY, parentHeight - itemHeight));
            }

            updateItem(item.id, {
                x: newX,
                y: newY,
            });
        };

        const onUp = () => {
            window.removeEventListener("mousemove", onMove);
            window.removeEventListener("mouseup", onUp);
        };

        window.addEventListener("mousemove", onMove);
        window.addEventListener("mouseup", onUp);
    };

    const setRefs = (node) => {
        interactionRef.current = node;
        drop(node);
    };


    return (
        <div
            className="layout-container"
            style={{
                position: "absolute",
                left: item.x,
                top: item.y,
                width: item.width || registryItem.defaultSize.width,
                height: item.height || registryItem.defaultSize.height,
            }}
        >
            <div
                ref={setRefs}
                onMouseDown={startDrag}
                className={`interaction-layer ${item.isSelected ? "selected" : ""} ${isOver && canHaveChildren ? "drop-target" : ""}`}
                style={{ width: "100%", height: "100%", position: "relative" }}
            >
                {/* Component wrapper */}
                {canHaveChildren ? (
                    // Container component with children
                    <div className="component-content" style={{ width: '100%', height: '100%', position: 'relative' }}>
                        <Component {...mergedProps} />

                        {/* Render nested children */}
                        {children.map((child) => (
                            <DroppedItem
                                key={child.id}
                                item={{
                                    ...child,
                                    isSelected: child.id === selectedId
                                }}
                                allItems={allItems}
                                getChildren={getChildren}
                                onDrop={onDrop}
                                selectItem={selectItem}
                                updateItem={updateItem}
                                selectedId={selectedId}
                            />
                        ))}
                    </div>
                ) : (
                    // Regular component without children
                    <Component
                        {...mergedProps}
                        style={{
                            width: '100%',
                            height: '100%',
                            boxSizing: 'border-box',
                            ...(mergedProps.style || {})
                        }}
                    />
                )}

                {/* Resize handles */}
                {item.isSelected && (
                    <>
                        <ResizeHandle dir="n" item={item} updateItem={updateItem} />
                        <ResizeHandle dir="s" item={item} updateItem={updateItem} />
                        <ResizeHandle dir="e" item={item} updateItem={updateItem} />
                        <ResizeHandle dir="w" item={item} updateItem={updateItem} />
                        <ResizeHandle dir="ne" item={item} updateItem={updateItem} />
                        <ResizeHandle dir="nw" item={item} updateItem={updateItem} />
                        <ResizeHandle dir="se" item={item} updateItem={updateItem} />
                        <ResizeHandle dir="sw" item={item} updateItem={updateItem} />
                    </>
                )}
            </div>
        </div>
    );
};


export default DroppedItem;