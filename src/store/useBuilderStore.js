import { create } from 'zustand';
import { temporal } from 'zundo';

const createEmptyPage = (name) => ({
    name,
    route: `/${encodeURIComponent(name.toLowerCase().replace(/\s+/g, "-"))}`, // encodeURIComponent ensure the url is safe, regex converts spaces to -
    droppedItems: {},
    rootIds: [],
    selectedId: null,
});

const useBuilderStore = create(
    temporal(
        (set, get) => ({
            // State
            pages: {
                home: createEmptyPage("root"),
            },
            activePageId: 'home',

            // Actions
            handleDrop: (item, x, y, parentId = null) => {
                set((state) => {
                    const page = state.pages[state.activePageId];

                    const newId = `${item.componentType}-${Date.now()}`;
                    const newItem = {
                        id: newId,
                        type: item.componentType,
                        layout: {
                            x,
                            y,
                            width: item.defaultSize?.width || 100,
                            height: item.defaultSize?.height || 40
                        },
                        parentId: parentId,
                        childrenIds: [],
                        props: {},
                    };

                    const updatedDroppedItems = {
                        ...page.droppedItems,
                        [newId]: newItem
                    };

                    if (parentId && page.droppedItems[parentId]) {
                        updatedDroppedItems[parentId] = {
                            ...page.droppedItems[parentId],
                            childrenIds: [
                                ...page.droppedItems[parentId].childrenIds,
                                newId
                            ],
                        };
                    }

                    const rootIds = parentId ? page.rootIds : [...page.rootIds, newId];

                    return {
                        pages: {
                            ...state.pages,
                            [state.activePageId]: {
                                ...page,
                                droppedItems: updatedDroppedItems,
                                rootIds,
                            },
                        },
                    };
                });
            },

            updateItem: (id, updates) => {
                set((state) => {
                    const page = state.pages[state.activePageId];
                    const item = page.droppedItems[id];
                    if (!item) return state;

                    return {
                        pages: {
                            ...state.pages,
                            [state.activePageId]: {
                                ...page,
                                droppedItems: {
                                    ...page.droppedItems,
                                    [id]: {
                                        ...item,
                                        ...updates,
                                        layout:{
                                            ...item.layout,
                                            ...(updates.layout || {})
                                        },
                                        props: {
                                            ...item.props,
                                            ...(updates.props || {}),
                                            style: {
                                                ...item.props?.style,
                                                ...(updates.props?.style || {}),
                                            },
                                        },
                                    },
                                },
                            },
                        },
                    };
                });
            },

            selectItem: (id) => {
                set((state) => {
                    const page = state.pages[state.activePageId];
                    return {
                        pages: {
                            ...state.pages,
                            [state.activePageId]: {
                                ...page,
                                selectedId: id,
                            },
                        },
                    };
                });
            },

            deselectAll: () => {
                set((state) => {
                    const page = state.pages[state.activePageId];
                    return {
                        pages: {
                            ...state.pages,
                            [state.activePageId]: {
                                ...page,
                                selectedId: null,
                            },
                        },
                    };
                });
            },

            handleClear: () => {
                if (window.confirm('Are you sure you want to clear the whiteboard?')) {
                    const currentPage = get().pages[get().activePageId];
                    set((state) => {
                        return {
                            pages: {
                                ...state.pages,
                                [state.activePageId]: createEmptyPage(currentPage.name),
                            },
                        };
                    });
                }
            },

            deleteItem: (id) => {
                set((state) => {
                   const page = state.pages[state.activePageId];
                   const { [id]: removed, ...rest } = page.droppedItems;

                   const rootIds = page.rootIds.filter((rootId) => rootId !== id );
                   const selectedId = page.selectedId === id ? null : page.selectedId;

                   return {
                       pages: {
                           ...state.pages,
                           [state.activePageId]: {
                               ...page,
                               droppedItems: rest,
                               rootIds,
                               selectedId,
                           }
                       }
                   }
                });


            },

            // Computed/Derived values
            getRootItems: () => {
                const page = get().pages[get().activePageId];
                return page.rootIds.map((id) => page.droppedItems[id]);
            },

            getChildren: (parentId) => {
                const page = get().pages[get().activePageId];
                const parent = page.droppedItems[parentId];
                return parent
                    ? parent.childrenIds.map(childrenId => page.droppedItems[childrenId])
                    : [];
            },

            getSelectedElement: () => {
               const page = get().pages[get().activePageId];
               return page.droppedItems[page.selectedId];
            },

            addPage: (name) => {
                set((state) => {
                    if (!name) name = `page-${Date.now()}`;
                    const pageId = name;

                    if(state.pages[pageId]) return state;

                    return {
                        pages: {
                            ...state.pages,
                            [pageId]: createEmptyPage(name),
                        },
                        activePageId: pageId,
                    }
                })
            },

            setActivePageId: (pageId) => {
                set((state) => {
                    if(!state.pages[pageId]) return state;
                    return {activePageId: pageId };
                });
            },

            deletePage: (pageId) => {
                set((state) => {
                    if(!state.pages[pageId]) return state;

                    const { [pageId]: removed, ...rest } = state.pages;
                    const newActive = Object.keys(rest)[0] || null;

                    return {
                        pages: rest,
                        activePageId: newActive,
                    }
                })
            }
        }),
        {
            limit: 50,
            equality: (a, b) => a === b,
        }
    )

);

export default useBuilderStore;