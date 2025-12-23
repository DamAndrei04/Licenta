import { ComponentRegistry } from '@/components/registry/ComponentRegistry';

export const importJSON = (importedData) => {
    try {
        if (!importedData || typeof importedData !== 'object') {
            console.error('Invalid JSON: Not an object');
            return null;
        }

        if (!importedData.pages || typeof importedData.pages !== 'object') {
            console.error('Invalid JSON: Missing pages object');
            return null;
        }

        const normalizedPages = {};

        for (const [pageId, pageData] of Object.entries(importedData.pages)) {
            const normalizedPage = normalizePage(pageData, pageId);
            if (normalizedPage) {
                normalizedPages[pageId] = normalizedPage;
            }
        }

        if (Object.keys(normalizedPages).length === 0) {
            console.error('Invalid JSON: No valid pages found');
            return null;
        }

        let activePageId = importedData.activePageId;
        if (!activePageId || !normalizedPages[activePageId]) {
            activePageId = Object.keys(normalizedPages)[0];
        }

        return {
            pages: normalizedPages,
            activePageId
        };

    } catch (error) {
        console.error('Import failed:', error);
        return null;
    }
};

const normalizePage = (pageData, pageId) => {
    if (!pageData || typeof pageData !== 'object') {
        console.warn(`Invalid page data for ${pageId}`);
        return null;
    }

    const droppedItems = pageData.droppedItems || {};
    const normalizedItems = {};
    const validIds = new Set();

    for (const [itemId, item] of Object.entries(droppedItems)) {
        const normalizedItem = normalizeItem(item, itemId);
        if (normalizedItem) {
            normalizedItems[itemId] = normalizedItem;
            validIds.add(itemId);
        }
    }

    for (const [itemId, item] of Object.entries(normalizedItems)) {
        item.childrenIds = item.childrenIds.filter(childId => validIds.has(childId));

        if (item.parentId && !validIds.has(item.parentId)) {
            item.parentId = null;
        }
    }

    const rootIds = Object.keys(normalizedItems).filter(
        itemId => normalizedItems[itemId].parentId === null
    );

    let selectedId = pageData.selectedId;
    if (selectedId && !validIds.has(selectedId)) {
        selectedId = null;
    }

    return {
        name: pageData.name || pageId,
        route: pageData.route || `/${pageId}`,
        droppedItems: normalizedItems,
        rootIds,
        selectedId
    };
};

const normalizeItem = (item, itemId) => {
    if (!item || typeof item !== 'object') {
        console.warn(`Invalid item: ${itemId}`);
        return null;
    }

    if (!item.type || typeof item.type !== 'string') {
        console.warn(`Item ${itemId}: Missing or invalid type`);
        return null;
    }

    if (!ComponentRegistry[item.type]) {
        console.warn(`Item ${itemId}: Unsupported component type "${item.type}"`);
        return null;
    }

    const layout = normalizeLayout(item.layout, item.type);
    if (!layout) {
        console.warn(`Item ${itemId}: Invalid layout`);
        return null;
    }

    const parentId = item.parentId || null;
    const childrenIds = Array.isArray(item.childrenIds) ? item.childrenIds : [];

    return {
        id: item.id || itemId,
        type: item.type,
        layout,
        parentId,
        childrenIds,
        props: item.props || {}
    };
};

const normalizeLayout = (layout, componentType) => {
    if (!layout || typeof layout !== 'object') {
        layout = {};
    }

    const defaults = ComponentRegistry[componentType]?.defaultSize || { width: 100, height: 40 };

    const x = typeof layout.x === 'number' && isFinite(layout.x) ? layout.x : 0;
    const y = typeof layout.y === 'number' && isFinite(layout.y) ? layout.y : 0;
    const width = typeof layout.width === 'number' && isFinite(layout.width) && layout.width > 0
        ? layout.width
        : defaults.width;
    const height = typeof layout.height === 'number' && isFinite(layout.height) && layout.height > 0
        ? layout.height
        : defaults.height;

    return { x, y, width, height };
};