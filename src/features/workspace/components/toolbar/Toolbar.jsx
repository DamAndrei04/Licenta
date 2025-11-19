import DraggableItem from '../draggableItem/DraggableItem';
import { ComponentRegistry } from "@/components/registry/ComponentRegistry";
import './Toolbar.css';

const Toolbar = ({ handleClear }) => {
    return (
        <div className="toolbar">
            <h2>TOOLBAR</h2>
            <div className="toolbar-items">
                {Object.entries(ComponentRegistry).map(([key, { displayName }]) => (
                    <DraggableItem key={key} type={key} displayName={displayName} />
                ))}
            </div>

            <button className="export-button">EXPORT</button>
            <button className="clear-button" onClick={handleClear}>
                Clear All
            </button>
        </div>
    );
};

export default Toolbar;
