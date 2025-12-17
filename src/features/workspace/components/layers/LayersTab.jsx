import './LayersTab.css';
import useBuilderStore from "@/store/useBuilderStore";
import ComponentsTree from "./ComponentsTree";

const LayersTab = () => {
    const activePageId = useBuilderStore(state => state.activePageId)
    const pages = useBuilderStore(state => state.pages);

    const page = pages[activePageId] || { rootIds: [] };
    const { rootIds } = page;

    return (
        <div>
            <label>LAYERS</label>
            {rootIds.map(rootId => (
                <ComponentsTree key={rootId} nodeId={rootId} />
            ))}
        </div>
    );
};

export default LayersTab;
