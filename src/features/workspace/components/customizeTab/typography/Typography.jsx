import {Accordion, AccordionContent, AccordionItem, AccordionTrigger} from "@/components/ui/accordion";
import {RgbaColorPicker} from "react-colorful";
import {useEffect, useState} from "react";
import './Typography.css';
import { TextAlignCenter, TextAlignStart, TextAlignEnd } from 'lucide-react';

const Typography = ({updateItem, selectedElement}) => {

    const [color, setColor] = useState({r: 47, g: 34, b: 29, a: 1});
    const [showPicker, setShowPicker] = useState(false);
    const [fontName, setFontName] = useState('');
    const [fontSize, setFontSize] = useState('16');
    const [charSpace, setCharSpace] = useState('normal');
    const [lineHeight, setLineHeight] = useState('24');
    const [fontWeight, setFontWeight] = useState('400');
    const [isBold, setIsBold] = useState(false);
    const [isItalic, setIsItalic] = useState(false);
    const [isUnderline, setIsUnderline] = useState(false);
    const [isStrikethrough, setIsStrikethrough] = useState(false);
    const [textAlign, setTextAlign] = useState('left');
    const [textContent, setTextContent] = useState('');

    const parseColor = (colorString) => {
        if (!colorString) {
            return {r: 47, g: 34, b: 29, a: 1};
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
        return {r: 47, g: 34, b: 29, a: 1};
    };

    const parsePixelValue = (value) => {
        if (!value) return '';
        return value.replace('px', '');
    };

    useEffect(() => {
        if (!selectedElement) return;

        const style = selectedElement.props?.style || {};
        const textColor = style.color;

        setColor(parseColor(textColor));
        setFontName(style.fontFamily || '');
        setFontSize(parsePixelValue(style.fontSize) || '16');
        setCharSpace(style.letterSpacing || 'normal');
        setLineHeight(parsePixelValue(style.lineHeight) || '24');
        setFontWeight(style.fontWeight || '400');
        setIsBold(style.fontWeight === 'bold' || parseInt(style.fontWeight) >= 700);
        setIsItalic(style.fontStyle === 'italic');
        setTextAlign(style.textAlign || 'left');
        setTextContent(selectedElement.props?.children || '');

        const textDecoration = style.textDecoration || '';
        setIsUnderline(textDecoration.includes('underline'));
        setIsStrikethrough(textDecoration.includes('line-through'));
    }, [selectedElement]);

    const handleColorChange = (newColor) => {
        setColor(newColor);
    };

    const handleFontNameChange = (e) => {
        setFontName(e.target.value);
    };

    const handleFontSizeChange = (e) => {
        const value = e.target.value;
        if (value === '' || /^\d*\.?\d*(px)?$/.test(value)) {
            setFontSize(value.replace('px', ''));
        }
    };

    const handleCharSpaceChange = (e) => {
        setCharSpace(e.target.value);
    };

    const handleLineHeightChange = (e) => {
        const value = e.target.value;
        if (value === '' || /^\d*\.?\d*(px)?$/.test(value)) {
            setLineHeight(value.replace('px', ''));
        }
    };

    const handleFontWeightChange = (e) => {
        const value = e.target.value;
        if (value === '' || /^\d*$/.test(value) || ['normal', 'bold'].includes(value)) {
            setFontWeight(value);
            setIsBold(value === 'bold' || parseInt(value) >= 700);
        }
    };

    const handleBoldToggle = () => {
        const newBold = !isBold;
        setIsBold(newBold);
        setFontWeight(newBold ? 'bold' : '400');
    };

    const handleItalicToggle = () => {
        setIsItalic(!isItalic);
    };

    const handleUnderlineToggle = () => {
        setIsUnderline(!isUnderline);
    };

    const handleStrikethroughToggle = () => {
        setIsStrikethrough(!isStrikethrough);
    };

    const handleTextAlignChange = (align) => {
        setTextAlign(align);
    };

    const handleTextContentChange = (e) => {
        setTextContent(e.target.value);
    };

    const applyChanges = () => {
        if (!selectedElement) return;

        const updates = {};

        const rgbaString = `rgba(${color.r}, ${color.g}, ${color.b}, ${color.a})`;
        if (rgbaString !== 'rgba(255, 255, 255, 1)') {
            updates.color = rgbaString;
        }

        if (fontName) {
            updates.fontFamily = fontName;
        }

        if (fontSize) {
            updates.fontSize = `${fontSize}px`;
        }

        if (charSpace) {
            updates.letterSpacing = charSpace;
        }

        if (lineHeight) {
            updates.lineHeight = `${lineHeight}px`;
        }

        if (fontWeight) {
            updates.fontWeight = fontWeight;
        }

        if (isItalic) {
            updates.fontStyle = 'italic';
        } else {
            updates.fontStyle = 'normal';
        }

        const decorations = [];
        if (isUnderline) decorations.push('underline');
        if (isStrikethrough) decorations.push('line-through');
        updates.textDecoration = decorations.length > 0 ? decorations.join(' ') : 'none';

        if (textAlign) {
            updates.textAlign = textAlign;
        }

        if (Object.keys(updates).length > 0) {
            updateItem(selectedElement.id, {
                props: {
                    ...selectedElement.props,
                    children: textContent,
                    style: {
                        ...selectedElement.props?.style,
                        ...updates
                    }
                }
            });
        } else {
            updateItem(selectedElement.id, {
                props: {
                    children: textContent,
                }
            })
        }
    };

    const canEditText = selectedElement && ['button', 'label'].includes(selectedElement.type);
    const rgbaString = `rgba(${color.r}, ${color.g}, ${color.b}, ${color.a})`;

    return (
        <Accordion type="single" collapsible defaultValue="typography">
            <AccordionItem value="typography">
                <AccordionTrigger>Typography</AccordionTrigger>
                <AccordionContent className="typography-options">
                    <div>
                        {canEditText && (
                            <div className="typography-control" style={{ marginBottom: '16px' }}>
                                <input
                                    type="text"
                                    placeholder="Enter text..."
                                    value={textContent}
                                    onChange={handleTextContentChange}
                                />
                                <label>Text Content</label>
                            </div>
                        )}

                        <div className="font-name-select">
                            <select
                                className="font-name"
                                value={fontName}
                                onChange={handleFontNameChange}
                            >
                                <option value="">Font name</option>
                                <option value="Arial">Arial</option>
                                <option value="Helvetica">Helvetica</option>
                                <option value="'Times New Roman', serif">Times New Roman</option>
                                <option value="Georgia, serif">Georgia</option>
                                <option value="'Courier New', monospace">Courier New</option>
                                <option value="Verdana, sans-serif">Verdana</option>
                                <option value="'Segoe UI', sans-serif">Segoe UI</option>
                                <option value="Roboto, sans-serif">Roboto</option>
                            </select>
                            <label>Font</label>
                        </div>

                        <div className="text-color">
                            <button
                                className="color-swatch"
                                onClick={() => setShowPicker(!showPicker)}
                                style={{
                                    backgroundColor: rgbaString,
                                    border: '1px solid #ccc',
                                    width: '40px',
                                    height: '40px'
                                }}
                            >
                            </button>
                            <label>{rgbaString}</label>
                            <span className="text-color-label">Text Color</span>
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

                        <div className="typography-control-group">
                            <div className="typography-control">
                                <input
                                    type="text"
                                    placeholder="16"
                                    value={fontSize}
                                    onChange={handleFontSizeChange}
                                />
                                <label>Font Size</label>
                            </div>

                            <div className="typography-control">
                                <input
                                    type="text"
                                    placeholder="normal"
                                    value={charSpace}
                                    onChange={handleCharSpaceChange}
                                />
                                <label>Char Space</label>
                            </div>
                        </div>

                        <div className="typography-control-group">
                            <div className="typography-control">
                                <input
                                    type="text"
                                    placeholder="24"
                                    value={lineHeight}
                                    onChange={handleLineHeightChange}
                                />
                                <label>Line Height</label>
                            </div>

                            <div className="typography-control">
                                <input
                                    type="text"
                                    placeholder="400"
                                    value={fontWeight}
                                    onChange={handleFontWeightChange}
                                />
                                <label>Weight</label>
                            </div>
                        </div>

                        <div className="text-style-buttons">
                            <button
                                className={`style-btn ${isBold ? 'active' : ''}`}
                                onClick={handleBoldToggle}
                            >
                                <strong>B</strong>
                            </button>
                            <button
                                className={`style-btn ${isItalic ? 'active' : ''}`}
                                onClick={handleItalicToggle}
                            >
                                <em>I</em>
                            </button>
                            <button
                                className={`style-btn ${isUnderline ? 'active' : ''}`}
                                onClick={handleUnderlineToggle}
                            >
                                <u>U</u>
                            </button>
                            <button
                                className={`style-btn ${isStrikethrough ? 'active' : ''}`}
                                onClick={handleStrikethroughToggle}
                            >
                                <s>S</s>
                            </button>
                            <button
                                className={`style-btn ${textAlign === 'left' ? 'active' : ''}`}
                                onClick={() => handleTextAlignChange('left')}
                            >
                                <TextAlignStart />
                            </button>
                            <button
                                className={`style-btn ${textAlign === 'center' ? 'active' : ''}`}
                                onClick={() => handleTextAlignChange('center')}
                            >
                                <TextAlignCenter />
                            </button>
                            <button
                                className={`style-btn ${textAlign === 'right' ? 'active' : ''}`}
                                onClick={() => handleTextAlignChange('right')}
                            >
                                <TextAlignEnd />
                            </button>
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

export default Typography;