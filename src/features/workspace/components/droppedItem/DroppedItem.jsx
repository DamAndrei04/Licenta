import { useDrag } from "react-dnd";
import { ComponentRegistry } from "@/components/registry/ComponentRegistry";
import "./DroppedItem.css";

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

    const registryItem = ComponentRegistry[item.type];

    if (!registryItem) {
        return <div>Unknown component</div>;
    }

    const Component = registryItem.component;
    const mergedProps = { ...registryItem.defaultProps, ...item.props };

    return (
        <div
            ref={drag}
            className={`dropped-item dropped-item-${item.type} ${isDragging ? "dragging" : ""}`}
            style={{
                position: "absolute",
                left: item.x,
                top: item.y,
                cursor: "move",
                opacity: isDragging ? 0.5 : 1,
            }}
        >
            <Component {...mergedProps} />
        </div>
    );
};

export default DroppedItem;