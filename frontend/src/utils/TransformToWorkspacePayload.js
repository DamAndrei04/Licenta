function transformToWorkspacePayload(storeState, projectId) {
    const pages = Object.values(storeState).map(page => ({
        name: page.name,
        route: page.route
    }));

    const componentsByPageTempId = {};

    Object.values(storeState).forEach(page => {
        componentsByPageTempId[page.name] = Object.values(page.droppedItems).map(item => ({
            externalId: item.id,
            parentExternalId: item.parentId,
            type: item.type,
            props: item.props,
            layout: item.layout,
            events: item.events ?? null,
            state: item.state ?? null
        }));
    });

    return { projectId, pages, componentsByPageTempId };
}

module.exports = { transformToWorkspacePayload };
