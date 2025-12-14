import './PagesAndLayersTab.css';
import useBuilderStore from "@/store/useBuilderStore";
import ComponentsTree from "./ComponentsTree";

const PagesAndLayersTab = () => {
    const rootIds = useBuilderStore(state => state.rootIds);

    return (
        <div>
            <label>PAGES AND LAYERS</label>
            {rootIds.map(rootId => (
                <ComponentsTree key={rootId} nodeId={rootId} />
            ))}
        </div>
    );
};

export default PagesAndLayersTab;
