import {Accordion, AccordionContent, AccordionItem, AccordionTrigger} from "@/components/ui/accordion";
import {RgbaColorPicker} from "react-colorful";
import {useEffect, useState} from "react";
import './Border.css';

const Border = ({updateItem, selectedElement}) => {

    const [color, setColor] = useState({r: 0, g: 0, b: 0, a: 1});
    const [showPicker, setShowPicker] = useState(false);
    const [borderWidth, setBorderWidth] = useState('1');
    const [borderRadius, setBorderRadius] = useState('0');
    const [borderStyle, setBorderStyle] = useState('solid');

    const parseColor = (colorString) => {
        if (!colorString) {
            return {r: 255, g: 255, b: 255, a: 1};
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
        return {r: 255, g: 255, b: 255, a: 1};
    };

    // Helper to extract numeric value from CSS string
    const parsePixelValue = (value) => {
        if (!value) return '0';
        return value.replace('px', '');
    };

    useEffect(() => {
        if (!selectedElement) return;

        const style = selectedElement.props?.style || {};
        const borderColor = style.borderColor;

        setColor(parseColor(borderColor));
        setBorderWidth(parsePixelValue(style.borderWidth) || '1');
        setBorderRadius(parsePixelValue(style.borderRadius) || '0');
        setBorderStyle(style.borderStyle || 'solid');
    }, [selectedElement]);

    const handleColorChange = (newColor) => {
        setColor(newColor);
    };

    const handleBorderStyleChange = (e) => {
        const newStyle = e.target.value;
        setBorderStyle(newStyle);
    };

    const handleBorderWidthChange = (e) => {
        const value = e.target.value;
        // Allow only numbers and decimals
        if (value === '' || /^\d*\.?\d*$/.test(value)) {
            setBorderWidth(value);
        }
    };

    const handleBorderRadiusChange = (e) => {
        const value = e.target.value;
        // Allow only numbers and decimals
        if (value === '' || /^\d*\.?\d*$/.test(value)) {
            setBorderRadius(value);
        }
    };

    const applyChanges = () => {
        if (!selectedElement) return;

        const updates = {};

        const rgbaString = `rgba(${color.r}, ${color.g}, ${color.b}, ${color.a})`;
        if (rgbaString !== 'rgba(255, 255, 255, 1)') {
            updates.borderColor = rgbaString;
        }

        if (borderWidth && borderWidth !== '0') {
            updates.borderWidth = `${borderWidth}px`;
        }

        if (borderStyle && borderStyle !== '') {
            updates.borderStyle = borderStyle;
        }

        if (borderRadius && borderRadius !== '0') {
            updates.borderRadius = `${borderRadius}px`;
        }

        if (Object.keys(updates).length > 0) {
            updateItem(selectedElement.id, {
                props: {
                    style: updates
                }
            });
        }
    };

    const rgbaString = `rgba(${color.r}, ${color.g}, ${color.b}, ${color.a})`;


    return (
        <Accordion type="single" collapsible defaultValue="border">
            <AccordionItem value="border">
                <AccordionTrigger>Border</AccordionTrigger>
                <AccordionContent className="border-options">
                    <div>
                        <div className="border-style-options">
                            <select
                                className="border-style"
                                value={borderStyle}
                                onChange={handleBorderStyleChange}
                            >
                                <option value="">Select your border style</option>
                                <option value="solid">Solid</option>
                                <option value="dotted">Dotted</option>
                                <option value="dashed">Dashed</option>
                                <option value="double">Double</option>
                                <option value="groove">Groove</option>
                                <option value="inset">Inset</option>
                                <option value="outset">Outset</option>
                                <option value="hidden">Hidden</option>
                            </select>
                            <label>Style</label>
                        </div>


                        <div className="border-color">
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
                        <div className="border-control-group">
                            <div className="border-control">
                                <input
                                    type="text"
                                    placeholder="0"
                                    value={borderWidth}
                                    onChange={handleBorderWidthChange}
                                />
                                <label>Size</label>
                            </div>

                            <div className="border-control">
                                <input
                                    type="text"
                                    placeholder="0"
                                    value={borderRadius}
                                    onChange={handleBorderRadiusChange}
                                />
                                <label>Border Radius</label>
                            </div>
                        </div>

                        <button onClick={applyChanges} className="apply-btn">
                            Apply
                        </button>
                    </div>
                </AccordionContent>
            </AccordionItem>
        </Accordion>
    );
};

export default Border;