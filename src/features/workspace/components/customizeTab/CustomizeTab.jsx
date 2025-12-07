import './CustomizeTab.css';
import Layout from './layout/Layout';

const CustomizeTab = ({ selectedElement, allItems, updateItem, setSelectedElement }) => {
    console.log(' CustomizeTab RENDER', {
        hasSelection: !!selectedElement
    });
    return (
        <div className="customizeTab">
            <h2>CUSTOMIZE</h2>
            <Layout
                selectedElement={selectedElement}
                allItems={allItems}
                updateItem={updateItem}
                setSelectedElement={setSelectedElement}
            />
        </div>
    );
};

export default CustomizeTab;
