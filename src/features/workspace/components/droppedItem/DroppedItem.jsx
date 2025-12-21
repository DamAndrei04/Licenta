import {useEffect, useRef, useState} from "react";
import { ComponentRegistry } from "@/components/registry/ComponentRegistry";
import ResizeHandle from "../resizeHandlers/ResizeHandle";
import "./DroppedItem.css";
import { useDrop } from "react-dnd";
import RightClickMenu from "../rightClickMenu/RightClickMenu";

const DroppedItem = ({ item, allItems, getChildren, onDrop, selectItem, updateItem, selectedId, canvasRef, deleteItem }) => {

    console.log(` DroppedItem [${item.type}-${item.id}] RENDER`, {
        isSelected: item.id === selectedId
    });

    const interactionRef = useRef(null);
    const [rightClickMenu, setRightClickMenu] = useState(null);
    const registryItem = ComponentRegistry[item.type];
    const Component = registryItem.component;
    const mergedProps = { ...registryItem.defaultProps, ...item.props };
    const canHaveChildren = registryItem.canHaveChildren || false;
    const children = getChildren(item.id);

    const parent = item.parentId ? allItems[item.parentId] : null;
    const parentWidth = parent ? (parent.layout.width || ComponentRegistry[parent.type]?.defaultSize.width) : null;
    const parentHeight = parent ? (parent.layout.height || ComponentRegistry[parent.type]?.defaultSize.height) : null;

    const [{ isOver }, drop] = useDrop(() => ({
        accept: ["COMPONENT", "DROPPED_ITEM"],
        canDrop: () => canHaveChildren,
        drop: (draggedItem, monitor) => {
            if (monitor.didDrop()) {
                return;
            }

            const offset = monitor.getClientOffset();
            const containerRect = interactionRef.current.getBoundingClientRect();

            const x = offset.x - containerRect.left;
            const y = offset.y - containerRect.top;

            if (draggedItem.id) {
                updateItem(draggedItem.id, {
                    layout: {
                        ...allItems[draggedItem.id].layout,
                        x,
                        y
                    },
                    parentId: item.id
                });
            } else {
                onDrop(draggedItem, x, y, item.id);
            }
        },
        collect: (monitor) => ({
            isOver: monitor.isOver({ shallow: true }),
        }),
    }), [item.id, canHaveChildren, allItems]);

    const dragPosRef = useRef({
        x: item.layout.x,
        y: item.layout.y,
    });

    const [previewPos, setPreviewPos] = useState(null);

    const renderX = previewPos?.x ?? item.layout.x;
    const renderY = previewPos?.y ?? item.layout.y;

    const isSelected = item.id === selectedId;

    const startDrag = (e) => {

        e.stopPropagation();
        selectItem(item.id);

        const startX = e.clientX;
        const startY = e.clientY;
        const startLeft = item.layout.x;
        const startTop = item.layout.y;

        const onMove = (ev) => {
            let newX = startLeft + ev.clientX - startX;
            let newY = startTop + ev.clientY - startY;

            const itemWidth = item.layout.width || registryItem.defaultSize.width;
            const itemHeight = item.layout.height || registryItem.defaultSize.height;

            if (parent && parentWidth && parentHeight) {
                newX = Math.max(0, Math.min(newX, parentWidth - itemWidth));
                newY = Math.max(0, Math.min(newY, parentHeight - itemHeight));
            } else if (canvasRef.current) {
                const canvasRect = canvasRef.current.getBoundingClientRect();
                newY = Math.max(0, newY);
                newX = Math.max(0, Math.min(newX, canvasRect.width - itemWidth));
            } else {
                newY = Math.max(0, newY);
                newX = Math.max(0, newX);
            }

            dragPosRef.current = { x: newX, y: newY };
            setPreviewPos({ x: newX, y: newY });
        };

        const onUp = () => {
            window.removeEventListener("mousemove", onMove);
            window.removeEventListener("mouseup", onUp);

            const { x, y } = dragPosRef.current;

            updateItem(item.id, {
                layout: {
                    ...item.layout,
                    x,
                    y,
                }
            });

            setPreviewPos(null);
        };

        window.addEventListener("mousemove", onMove);
        window.addEventListener("mouseup", onUp);
    };

    const setRefs = (node) => {
        interactionRef.current = node;
        drop(node);
    };

    const [previewLayout, setPreviewLayout] = useState(null);
    const renderLayout = {
        x: previewLayout?.x ?? renderX,
        y: previewLayout?.y ?? renderY,
        width:
            previewLayout?.width ??
            item.layout.width ??
            registryItem.defaultSize.width,
        height:
            previewLayout?.height ??
            item.layout.height ??
            registryItem.defaultSize.height,
    };

    const handleDelete = () => {
        deleteItem(item.id);
        setRightClickMenu(null);
    };

    const handleRightClickMenu = (e) => {
        e.preventDefault();
        e.stopPropagation();

        selectItem(item.id);

        setRightClickMenu({
           x: e.clientX,
           y: e.clientY,
        });
    }

    return (
        <>
        <div
            className="layout-container"
            style={{
                position: "absolute",
                left: renderX,
                top: renderY,
                width: renderLayout.width,
                height: renderLayout.height,
                border: 'none',
            }}
        >
            <div
                ref={setRefs}
                onMouseDown={startDrag}
                onContextMenu={handleRightClickMenu}
                className={`interaction-layer ${isSelected ? "selected" : ""} ${isOver && canHaveChildren ? "drop-target" : ""}`}
                style={{ width: "100%", height: "100%", position: "relative" }}
            >
                {canHaveChildren ? (
                    <div className="component-content"
                         style={{
                            width: '100%',
                            height: '100%',
                            position: 'relative',
                             ...(mergedProps.style || {})
                        }}
                    >
                        <Component {...mergedProps} />
                        {children.map((child) => (
                            <DroppedItem
                                key={child.id}
                                item={child}
                                allItems={allItems}
                                getChildren={getChildren}
                                onDrop={onDrop}
                                selectItem={selectItem}
                                updateItem={updateItem}
                                selectedId={selectedId}
                                canvasRef={canvasRef}
                            />
                        ))}
                    </div>
                ) : (
                    <Component
                        {...mergedProps}
                        style={{
                            width: '100%',
                            height: '100%',
                            boxSizing: 'border-box',
                            ...(mergedProps.style || {}),
                        }}
                    />
                )}

                {isSelected && (
                    <>
                        <ResizeHandle dir="n" item={item} updateItem={updateItem} canvasRef={canvasRef} parentWidth={parentWidth} parentHeight={parentHeight} onPreview={setPreviewLayout} />
                        <ResizeHandle dir="s" item={item} updateItem={updateItem} canvasRef={canvasRef} parentWidth={parentWidth} parentHeight={parentHeight} onPreview={setPreviewLayout} />
                        <ResizeHandle dir="e" item={item} updateItem={updateItem} canvasRef={canvasRef} parentWidth={parentWidth} parentHeight={parentHeight} onPreview={setPreviewLayout} />
                        <ResizeHandle dir="w" item={item} updateItem={updateItem} canvasRef={canvasRef} parentWidth={parentWidth} parentHeight={parentHeight} onPreview={setPreviewLayout} />
                        <ResizeHandle dir="ne" item={item} updateItem={updateItem} canvasRef={canvasRef} parentWidth={parentWidth} parentHeight={parentHeight} onPreview={setPreviewLayout} />
                        <ResizeHandle dir="nw" item={item} updateItem={updateItem} canvasRef={canvasRef} parentWidth={parentWidth} parentHeight={parentHeight} onPreview={setPreviewLayout} />
                        <ResizeHandle dir="se" item={item} updateItem={updateItem} canvasRef={canvasRef} parentWidth={parentWidth} parentHeight={parentHeight} onPreview={setPreviewLayout} />
                        <ResizeHandle dir="sw" item={item} updateItem={updateItem} canvasRef={canvasRef} parentWidth={parentWidth} parentHeight={parentHeight} onPreview={setPreviewLayout} />
                    </>
                )}
            </div>
        </div>

        {rightClickMenu && (
            <RightClickMenu
                x={rightClickMenu.x}
                y={rightClickMenu.y}
                onClose={() => setRightClickMenu(null)}
                onDelete={handleDelete}
            />
        )}
        </>
    );
};


export default DroppedItem;