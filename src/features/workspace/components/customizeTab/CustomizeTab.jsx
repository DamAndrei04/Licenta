import './CustomizeTab.css';
import Layout from './layout/Layout';
import Padding from "@/features/workspace/components/customizeTab/padding/Padding";
import Background from "@/features/workspace/components/customizeTab/background/Background";
import Border from "@/features/workspace/components/customizeTab/border/Border";
import Typography from "@/features/workspace/components/customizeTab/typography/Typography";
import { ComponentRegistry } from "@/components/registry/ComponentRegistry";

const CustomizeTab = ({ selectedElement, allItems, updateItem, setSelectedElement }) => {
    console.log(' CustomizeTab RENDER', {
        hasSelection: !!selectedElement
    });

    if (!selectedElement) {
        return (
            <div className="customizeTab-empty">
                <h2>CUSTOMIZE</h2>
                <div className="empty-state">
                    Please select a component to edit it
                </div>
            </div>
        );
    }

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
            <Background
                updateItem={updateItem}
                selectedElement={selectedElement}
            />
            <Border
                updateItem={updateItem}
                selectedElement={selectedElement}
            />
            {!(ComponentRegistry[selectedElement.type]?.canHaveChildren) && (
            <Typography
                updateItem={updateItem}
                selectedElement={selectedElement}
            />
            )}
        </div>
    );
};

export default CustomizeTab;
