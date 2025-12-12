import './CustomizeTab.css';
import Layout from './layout/Layout';
import Padding from "@/features/workspace/components/customizeTab/padding/Padding";

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
            <Padding
                updateItem={updateItem}
                selectedElement={selectedElement}
            />
        </div>
    );
};

export default CustomizeTab;
