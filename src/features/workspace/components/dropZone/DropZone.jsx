import { useDrop } from "react-dnd";
import DroppedItem from '../droppedItem/DroppedItem';
import './DropZone.css';

const DropZone = ({
                      onDrop,
                      droppedItems,
                      allItems,
                      getChildren,
                      moveItem,
                      updateItem,
                      selectItem,
                      deselectAll,
                      selectedId
                    }) => {
    const [{ isOver }, drop] = useDrop(() => ({
        accept: ["COMPONENT", "DROPPED_ITEM"],

        drop: (item, monitor) => {

            if(monitor.didDrop()) {
                return;
            }

            const offset = monitor.getClientOffset();
            const dropZone = document.querySelector(".whiteboard");
            const rect = dropZone.getBoundingClientRect();

            const x = offset.x - rect.left + dropZone.scrollLeft - item.offset.x;
            const y = offset.y - rect.top + dropZone.scrollTop - item.offset.y;

            if (item.id) {
                // dacă e deja pe whiteboard -> muta
                moveItem(item.id, x, y);
            } else {
                // dacă vine din toolbar -> adauga
                onDrop(item, x, y);
            }
        },
        collect: (monitor) => ({
            isOver: monitor.isOver({shallow: true}),
        }),
    }));

    const handleCanvasClick = (e) => {
        if (e.target.classList.contains('whiteboard')) {
            deselectAll();
        }
    };

    return (
        <div
            ref={drop}
            className={`whiteboard ${isOver ? "drop-active" : ""}`}
            onClick={handleCanvasClick}
        >
            <h2>WHITEBOARD (Drop Zone)</h2>
            {droppedItems.map((item) => (
                <DroppedItem
                    key={item.id}
                    item={{
                        ...item,
                        isSelected: item.id === selectedId
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
    );
};

export default DropZone;