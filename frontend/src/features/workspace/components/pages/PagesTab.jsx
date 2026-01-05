import  useBuilderStore  from '@/store/useBuilderStore';
import {useRef, useState} from "react";
import { Trash } from "lucide-react";
import './PagesTab.css';

const PagesTab = () => {
    const pages = useBuilderStore((state) => state.pages);
    const activePageId = useBuilderStore((state) => state.activePageId);
    const addPage = useBuilderStore((state) => state.addPage);
    const setActivePageId = useBuilderStore((state) => state.setActivePageId);
    const deletePage = useBuilderStore((state) => state.deletePage);

    const [showPopup, setShowPopup] = useState(false);
    const [newPageName, setNewPageName] = useState("");
    const addButtonRef = useRef(null);

    const handleAddPage = () => {
        if (!newPageName.trim()) return;
        addPage(newPageName.trim());
        setNewPageName("");
        setShowPopup(false);
    }

    return (
        <div className="pages-panel">
            <div className="pages-header">
                <h3 className="pages-title">Pages</h3>
                <button
                    ref={addButtonRef}
                    onClick={() => setShowPopup(!showPopup)}
                    className="add-button"
                >
                    +
                </button>
            </div>

            <ul className="pages-list">
                {Object.entries(pages).map(([pageId, page]) => (
                    <li key={pageId} className="page-item">
                        <div className="page-item-content">
                            <button
                                className={`page-button ${pageId === activePageId ? "active" : ""}`}
                                onClick={() => setActivePageId(pageId)}
                            >
                                {page.name || pageId}
                            </button>
                            {Object.keys(pages).length > 1 && (
                                <button
                                    className="delete-button"
                                    onClick={() => deletePage(pageId)}
                                >
                                    <Trash />
                                </button>
                            )}
                        </div>
                    </li>
                ))}
            </ul>

            {showPopup && addButtonRef.current && (
                <div
                    className="popup"
                    style={{
                        top: addButtonRef.current.offsetTop + addButtonRef.current.offsetHeight + 4,
                        left: addButtonRef.current.offsetLeft,
                    }}
                >
                    <input
                        type="text"
                        value={newPageName}
                        onChange={(e) => setNewPageName(e.target.value)}
                        placeholder="Page Name"
                        className="popup-input"
                        onKeyDown={(e) => {
                            if (e.key === "Enter") handleAddPage();
                            if (e.key === "Escape") setShowPopup(false);
                        }}
                        autoFocus
                    />
                    <button onClick={handleAddPage} className="popup-ok">
                        Create
                    </button>
                    <button onClick={() => setShowPopup(false)} className="popup-cancel">
                        Cancel
                    </button>
                </div>
            )}
        </div>
    );
};


export default PagesTab;