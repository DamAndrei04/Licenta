import {useDrag} from "react-dnd";

const DraggableItem = ({ type, name }) => {
    const [{ isDragging }, drag] = useDrag(() => ({
        type: 'COMPONENT',

        item: (monitor) => {
            const initialClientOffset = monitor.getInitialClientOffset();
            const initialSourceOffset = monitor.getInitialSourceClientOffset();
            const offset = {
                x: initialClientOffset.x - initialSourceOffset.x,
                y: initialClientOffset.y - initialSourceOffset.y,
            };
            return {componentType: type, name, offset};
        },

        collect: (monitor) => ({
            isDragging: monitor.isDragging(),
        }),
    }));

    return (
        <div ref={drag} className={`draggable-item ${isDragging ? 'dragging' : ''}`} >
            {name}
        </div>
    );
};

export default DraggableItem;