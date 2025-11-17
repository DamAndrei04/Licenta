import DraggableItem from './DraggableItem';

const Toolbar = ({ handleClear }) => {
    return (
        <div className="toolbar">
            <h2>TOOLBAR</h2>
            <DraggableItem type="button" name="Button" />
            <DraggableItem type="input" name="Input" />
            <DraggableItem type="text" name="Text" />
            <DraggableItem type="div" name="Container" />

            <button className="export-button">EXPORT</button>
            <button className="clear-button" onClick={handleClear}>
                Clear All
            </button>
        </div>
    );
};

export default Toolbar;
