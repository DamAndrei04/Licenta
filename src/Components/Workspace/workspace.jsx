import './workspace.css';
import { DndProvider, useDrag, useDrop } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { useState } from 'react';

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
}

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
        <DroppedItem key={item.id} item={item} />
      ))}
    </div>
  );
};

export default function Main() {
  const [droppedItems, setDroppedItems] = useState([]);

  const handleDrop = (item, x, y) => {
    setDroppedItems(prev => [...prev, {
      ...item,
      x,
      y,
      id: Date.now(),
    }]);
    console.log(x, y, item);
  };

  const moveItem = (id, x, y) => {
    setDroppedItems((prev) =>
      prev.map((el) =>
        el.id === id ? { ...el, x, y } : el
      )
    );
    console.log(x, y, id);
  };

  return (
    <DndProvider backend={HTML5Backend}>
      <div className="container">
        <div className="wrapper">
          <div className="toolbar">
            <h2> TOOLBAR </h2>
            <DraggableItem type="button" name="Button" />
            <DraggableItem type="input" name="Input" />
            <DraggableItem type="Text" name="Text" />
            <DraggableItem type="div" name="Container" />

            <button className="export-button">EXPORT</button>

            <button
              onClick={() => {
                if (window.confirm("Are you sure  you want to clear the whiteboard?")) {
                  setDroppedItems([]);
                }
              }}
              className="clear-button"
            >
              Clear All
            </button>
          </div>
          <div className="gridContainer">
            <div className="leftColumn">
              <h2> PAGES AND LAYERS </h2>
            </div>

            <DropZone onDrop={handleDrop} droppedItems={droppedItems} moveItem={moveItem} />

            <div className="rightColumn">
              <h2> CUSTOMIZE </h2>
            </div>
          </div>
        </div>
      </div>
    </DndProvider>
  );
}