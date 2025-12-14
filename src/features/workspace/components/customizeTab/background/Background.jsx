import { Accordion, AccordionItem, AccordionTrigger, AccordionContent } from '@/components/ui/accordion';
import { useEffect, useState } from "react";
import { RgbaColorPicker } from "react-colorful";

import './Background.css';

const Background = ({ selectedElement, updateItem }) => {

    const [color, setColor] = useState({ r: 0, g: 0, b: 0, a: 1 });
    const [showPicker, setShowPicker] = useState(false);


    const parseColor = (colorString) => {
        if (!colorString){
            return { r: 255, g: 255, b: 255, a: 1 };
        }

        if (colorString.startsWith('rgba')) {
            const match = colorString.match(/rgba?\((\d+),\s*(\d+),\s*(\d+),?\s*(\d*\.?\d+)?\)/);
            if (match) {
                return {
                    r: parseInt(match[1]),
                    g: parseInt(match[2]),
                    b: parseInt(match[3]),
                    a: match[4] ? parseFloat(match[4]) : 1
                };
            }
        }

        if (colorString.startsWith('rgb')) {
            const match = colorString.match(/rgb\((\d+),\s*(\d+),\s*(\d+)\)/);
            if (match) {
                return {
                    r: parseInt(match[1]),
                    g: parseInt(match[2]),
                    b: parseInt(match[3]),
                    a: 1
                };
            }
        }
        return { r: 255, g: 255, b: 255, a: 1 };
    };

    useEffect(() => {
        if (!selectedElement) return;

        const style = selectedElement.props?.style || {};
        const backgroundColor = style.backgroundColor;

        setColor(parseColor(backgroundColor));
    }, [selectedElement]);

    const handleColorChange = (newColor) => {
        setColor(newColor);
    };

    const applyColor = () => {
        if (!selectedElement) return;

        const rgbaString = `rgba(${color.r}, ${color.g}, ${color.b}, ${color.a})`;

        updateItem(selectedElement.id, {
            props: {
                style: {
                    backgroundColor: rgbaString
                }
            }
        });
    };

    const rgbaString = `rgba(${color.r}, ${color.g}, ${color.b}, ${color.a})`;


    return (
        <Accordion type="single" collapsible defaultValue="background">
            <AccordionItem value="background">
                <AccordionTrigger>Background</AccordionTrigger>
                <AccordionContent className="background-options">
                    <div>
                        <label>Background</label>
                        <div className="background-color">
                            <button
                                className="open-color-picker"
                                onClick={() => setShowPicker(!showPicker)}
                                style={{
                                    backgroundColor: rgbaString,
                                    color: color.r + color.g + color.b < 382 ? '#fff' : '#000',
                                    border: '1px solid #ccc'
                                }}
                            >
                                COLOR
                            </button>
                            <label>{rgbaString}</label>
                        </div>

                    {showPicker && (
                        <div style={{
                            marginTop: '10px',
                            position: 'relative'
                        }}>
                            <RgbaColorPicker
                                color={color}
                                onChange={handleColorChange}
                            />
                        </div>
                    )}
                    </div>
                    <button onClick={applyColor} className="apply-btn">
                        Apply
                    </button>

                </AccordionContent>
            </AccordionItem>
        </Accordion>
    );

};

export default Background;