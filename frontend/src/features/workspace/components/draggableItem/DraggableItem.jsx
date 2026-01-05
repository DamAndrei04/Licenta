import { useDrag } from "react-dnd";
import './DraggableItem.css';

const DraggableItem = ({ type, displayName }) => {
    const [{ isDragging }, drag] = useDrag(() => ({
        type: 'COMPONENT',

        item: (monitor) => {
            const initialClientOffset = monitor.getInitialClientOffset();
            const initialSourceOffset = monitor.getInitialSourceClientOffset();
            const offset = {
                x: initialClientOffset.x - initialSourceOffset.x,
                y: initialClientOffset.y - initialSourceOffset.y,
            };
            return {componentType: type, displayName, offset};
        },

        collect: (monitor) => ({
            isDragging: monitor.isDragging(),
        }),
    }));

    return (
        <div ref={drag} className={`draggable-item ${isDragging ? 'dragging' : ''}`} >
            {displayName}
        </div>
    );
};

export default DraggableItem;