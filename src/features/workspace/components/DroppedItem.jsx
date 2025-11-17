import {useDrag} from "react-dnd";

const DroppedItem = ({ item }) => {
    const [{ isDragging }, drag] = useDrag(() => ({
        type: "DROPPED_ITEM",

        item: (monitor) => {
            const initialClientOffset = monitor.getInitialClientOffset();
            const initialSourceOffset = monitor.getInitialSourceClientOffset();
            const offset = {
                x: initialClientOffset.x - initialSourceOffset.x,
                y: initialClientOffset.y - initialSourceOffset.y,
            }
            return { id: item.id, offset };
        },

        collect: (monitor) => ({
            isDragging: monitor.isDragging(),
        }),
    }));

    return (
        <div
            ref={drag}
            className={`dropped-item ${isDragging ? "dragging" : ""}`}
            style={{
                position: "absolute",
                left: item.x,
                top: item.y,
                cursor: "move",
                opacity: isDragging ? 0.5 : 1,
            }}
        >
            {item.name}
        </div>
    );
};

export default DroppedItem;