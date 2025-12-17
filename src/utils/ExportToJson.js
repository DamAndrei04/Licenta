import useBuilderStore from '@/store/useBuilderStore';

export const exportToJSON = () => {
    const state = useBuilderStore.getState();
    const { pages, activePageId } = state;

    const exportData = {
        version: '1.0',
        exportedAt: new Date().toISOString(),
        activePageId,
        pages: Object.entries(pages).reduce((acc, [pageId, page]) => {
            acc[pageId] = {
                name: page.name || pageId,
                route: page.route || `/${encodeURIComponent(pageId.toLowerCase().replace(/\s+/g, '-'))}`,
                droppedItems: page.droppedItems,
                rootIds: page.rootIds,
                selectedId: page.selectedId,
            };
            return acc;
        }, {}),
    };

    const jsonString = JSON.stringify(exportData, null, 2);

    const blob = new Blob([jsonString], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `ui-builder-${Date.now()}.json`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
};
