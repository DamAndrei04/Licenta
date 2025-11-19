import { useDrop } from "react-dnd";
import DroppedItem from '../droppedItem/DroppedItem';
import './DropZone.css';

const DropZone = ({ onDrop, droppedItems, moveItem }) => {
    const [{ isOver }, drop] = useDrop(() => ({
        accept: ["COMPONENT", "DROPPED_ITEM"],

        drop: (item, monitor) => {
            const offset = monitor.getClientOffset();
            const dropZone = document.querySelector(".whiteboard");
            const rect = dropZone.getBoundingClientRect();

            const x = offset.x - rect.left + dropZone.scrollLeft - item.offset.x;
            const y = offset.y - rect.top + dropZone.scrollTop - item.offset.y;

            if (item.id) {
                // dacă e deja pe whiteboard -> mută
                moveItem(item.id, x, y);
            } else {
                // dacă vine din toolbar -> adaugă
                onDrop(item, x, y);
            }
        },
        collect: (monitor) => ({
            isOver: monitor.isOver(),
        }),
    }));

    return (
        <div ref={drop} className={`whiteboard ${isOver ? "drop-active" : ""}`}>
            <h2>WHITEBOARD (Drop Zone)</h2>
            {droppedItems.map((item) => (
                <DroppedItem
                    key={item.id}
                    item={item}
                />
            ))}
        </div>
    );
};

export default DropZone;