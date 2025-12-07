import { Accordion, AccordionItem, AccordionTrigger, AccordionContent } from '@/components/ui/accordion';
import {useState, useEffect, useCallback} from 'react';
import { AlignStartVertical, AlignCenterVertical, AlignEndVertical, Move } from 'lucide-react';
import './Layout.css';


const Layout = ({ selectedElement, allItems, updateItem }) => {

    console.log('Layout RENDER', {
        selectedId: selectedElement?.id
    });

    const [ width, setWidth ] = useState(selectedElement?.width || '');
    const [ height, setHeight ] = useState(selectedElement?.height || '');
    const [ alignment, setAlignment ] = useState(selectedElement?.alignment || 'left');

    useEffect(() => {
        setWidth(selectedElement?.width || 0);
        setHeight(selectedElement?.height || 0);
        const actualAlignment = calculateAlignment();
        setAlignment(actualAlignment);
    }, [selectedElement])

    const calculateAlignment = useCallback(() => {
        if (!selectedElement) return 'custom';

        let containerWidth;

        if (selectedElement.parentId) {
            const parent = allItems.find(item => item.id === selectedElement.parentId);
            if (!parent) return 'custom';
            containerWidth = parent.width || 100;
        } else {
            const canvas = document.querySelector(".whiteboard");
            if (!canvas) return 'custom';
            containerWidth = canvas.offsetWidth;
        }

        const itemWidth = selectedElement.width || 100;
        const currentX = selectedElement.x || 0;

        const leftX = 0;
        const centerX = (containerWidth - itemWidth) / 2;
        const rightX = containerWidth - itemWidth;

        const tolerance = 1;

        if (Math.abs(currentX - leftX) < tolerance) return 'left';
        if (Math.abs(currentX - centerX) < tolerance) return 'center';
        if (Math.abs(currentX - rightX) < tolerance) return 'right';

        return 'custom';
    }, [selectedElement, allItems]);

    const updateAlignment = (type) => {
        if (!selectedElement) return;
        let containerWidth;

        if(selectedElement.parentId){
            const parent = allItems.find(item => item.id === selectedElement.parentId);
            containerWidth = parent.width || 100;
        } else {
            const canvas = document.querySelector(".whiteboard");
            if (!canvas) return;
            containerWidth = canvas.offsetWidth;
        }

        const itemWidth = selectedElement.width || 100;

        let newX = selectedElement.x || 0;
        if (type === "left") newX = 0;
        if (type === "center") newX = (containerWidth - itemWidth) / 2;
        if (type === "right") newX = containerWidth - itemWidth;

        console.log("Before update:", selectedElement);
        console.log("New X:", newX);
        console.log("Calling updateItem with:", selectedElement.id, { x: newX, alignment: type });

        updateItem(selectedElement.id, { x: newX, alignment: type });
    };

    const handleAlignment = (type) => {
        setAlignment(type);
        updateAlignment(type);
    };

    const handleWidthChange = (e) => {
        const newWidth = Number(e.target.value);
        setWidth(newWidth);
    };

    const handleHeightChange = (e) => {
        const newHeight = Number(e.target.value);
        setHeight(newHeight);
    };

    const applyWidthChange = () => {
        if (!selectedElement) return;
        const newWidth = Number(width);
        if (newWidth > 0) {
            updateItem(selectedElement.id, { width: newWidth });
        } else {
            setWidth(selectedElement.width || 0);
        }
    };

    const applyHeightChange = () => {
        if (!selectedElement) return;
        const newHeight = Number(height);
        if (newHeight > 0) {
            updateItem(selectedElement.id, { height: newHeight });
        } else {
            setHeight(selectedElement.height || 0);
        }
    };

    return (
        <Accordion type="single" collapsible defaultValue="layout">
            <AccordionItem value="layout">
                <AccordionTrigger>Layout</AccordionTrigger>
                <AccordionContent className="layout-options">
                    <div>
                        <label className="alignment">Alignment:</label>
                        <div className="alignment-options flex gap-2 mt-2">

                            <button
                                className={ alignment === "left" ? "active" : ""}
                                onClick={() => handleAlignment("left")}
                            >
                                <AlignStartVertical size={18} />
                            </button>

                            <button
                                className={alignment === "center" ? "active" : ""}
                                onClick={() => handleAlignment("center")}
                            >
                                <AlignCenterVertical size={18} />
                            </button>

                            <button
                                className={alignment === "right" ? "active" : ""}
                                onClick={() => handleAlignment("right")}
                            >
                                <AlignEndVertical size={18} />
                            </button>

                            <button
                                className={alignment === "custom" ? "active" : ""}
                                disabled
                            >
                                <Move size={18} />
                            </button>
                        </div>
                    </div>

                    <div className="dimension-control">
                        <div className="dimension-input-wrapper">
                            <label className="width-options">Width (px):</label>
                            <input
                                type="number"
                                value={width}
                                onChange={handleWidthChange}
                                className="width-input"
                            />
                        </div>
                        <button onClick={applyWidthChange} className="apply-btn">Apply</button>
                    </div>

                    <div className="dimension-control">
                        <div className="dimension-input-wrapper">
                            <label className="height-options">Height (px):</label>
                            <input
                                type="number"
                                value={height}
                                onChange={handleHeightChange}
                                className="height-input"
                            />
                        </div>
                        <button onClick={applyHeightChange} className="apply-btn">Apply</button>
                    </div>
                </AccordionContent>
            </AccordionItem>
        </Accordion>
    );
};

export default Layout;