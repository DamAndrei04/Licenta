import { Accordion, AccordionItem, AccordionTrigger, AccordionContent } from '@/components/ui/accordion';
import './Padding.css';
import { useState, useEffect } from "react";

const Padding = ({ selectedElement, updateItem }) => {

    console.log('padding RENDER', {
        selectedId: selectedElement?.id
    });

    const [padding, setPadding] = useState({ top: '0px', right: '0px', bottom: '0px', left: '0px' });

    useEffect(() => {
        if (!selectedElement) return;

        const style = selectedElement.props?.style || {};

        const parseSides = (value) => {
            if (!value) return { top: '0px', right: '0px', bottom: '0px', left: '0px' };

            const parts = value.split(' ');
            switch (parts.length) {
                case 1:
                    return { top: parts[0], right: parts[0], bottom: parts[0], left: parts[0] };
                case 2:
                    return { top: parts[0], right: parts[1], bottom: parts[0], left: parts[1] };
                case 3:
                    return { top: parts[0], right: parts[1], bottom: parts[2], left: parts[1] };
                case 4:
                    return { top: parts[0], right: parts[1], bottom: parts[2], left: parts[3] };
                default:
                    return { top: '0px', right: '0px', bottom: '0px', left: '0px' };
            }
        };

        setPadding(parseSides(style.padding));
    }, [selectedElement]);

    const updatePaddingInput = (side, value) => {
        setPadding((prev) => ({ ...prev, [side]: value }));
    };

    const applyPadding = () => {
        if (!selectedElement) return;
        const shorthand = `${padding.top} ${padding.right} ${padding.bottom} ${padding.left}`;
        updateItem(selectedElement.id, {
            props: {
                style: {
                    ...selectedElement.props.style,
                    padding: shorthand
                }
            }
        });
    };

    return (
        <Accordion type="single" collapsible defaultValue="padding">
            <AccordionItem value="padding">
                <AccordionTrigger>Padding</AccordionTrigger>
                <AccordionContent className="padding-options">
                    <div className="spacing-section">
                        <div className="section">
                            <div className="title">PADDING</div>
                            <div className="row">
                                <label>Top</label>
                                <input
                                    type="text"
                                    value={padding.top.replace('px', '')}
                                    onChange={(e) => updatePaddingInput('top', e.target.value + 'px')}
                                    onBlur={applyPadding}
                                />
                                <label>Right</label>
                                <input
                                    type="text"
                                    value={padding.right.replace('px', '')}
                                    onChange={(e) => updatePaddingInput('right', e.target.value + 'px')}
                                    onBlur={applyPadding}
                                />
                            </div>
                            <div className="row">
                                <label>Left</label>
                                <input
                                    type="text"
                                    value={padding.left.replace('px', '')}
                                    onChange={(e) => updatePaddingInput('left', e.target.value + 'px')}
                                    onBlur={applyPadding}
                                />
                                <label>Bottom</label>
                                <input
                                    type="text"
                                    value={padding.bottom.replace('px', '')}
                                    onChange={(e) => updatePaddingInput('bottom', e.target.value + 'px')}
                                    onBlur={applyPadding}
                                />
                            </div>
                        </div>

                    </div>
                    <button onClick={applyPadding} className="apply-btn">Apply</button>
                </AccordionContent>
            </AccordionItem>

        </Accordion>
    );
};

export default Padding;