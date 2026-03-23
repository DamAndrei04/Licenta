
export const getEffectiveBackground = (componentId, droppedItems) => {
    let current = droppedItems[componentId];
    while (current) {
        const bg = current.props?.style?.backgroundColor;
        if (bg && bg !== 'rgba(0, 0, 0, 0)' && bg !== 'transparent') {
            return bg;
        }
        current = current.parentId ? droppedItems[current.parentId] : null;
    }
    return '#ffffff';
};