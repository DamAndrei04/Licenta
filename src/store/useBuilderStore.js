import { create } from 'zustand';
import { temporal } from 'zundo';

const useBuilderStore = create(
    temporal(
        (set, get) => ({
            // State
            droppedItems: {},
            rootIds: [],
            selectedId: null,

            // Actions
            handleDrop: (item, x, y, parentId = null) => {
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

                set((state) => {
                    const updatedDroppedItems = {
                        ...state.droppedItems,
                        [newId]: newItem
                    };

                    if (parentId && state.droppedItems[parentId]) {
                        updatedDroppedItems[parentId] = {
                            ...state.droppedItems[parentId],
                            childrenIds: [...state.droppedItems[parentId].childrenIds, newId]
                        };
                    }

                    const updatedRootItems = parentId
                        ? state.rootIds
                        : [...state.rootIds, newId];

                    return {
                        droppedItems: updatedDroppedItems,
                        rootIds: updatedRootItems
                    };
                });
            },

            updateItem: (id, updates) => {
                set((state) => {
                    const item = state.droppedItems[id];
                    if (!item) return state;

                    return {
                        droppedItems: {
                            ...state.droppedItems,
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
                                    }
                                }
                            }
                        }
                    };
                });
            },

            selectItem: (id) => {
                set({ selectedId: id });
            },

            deselectAll: () => {
                set({ selectedId: null });
            },

            moveItem: (id, x, y) => {
                set((state) => ({
                    droppedItems: state.droppedItems.map(el =>
                        el.id === id ? { ...el, x, y } : el
                    )
                }));
            },

            handleClear: () => {
                if (window.confirm('Are you sure you want to clear the whiteboard?')) {
                    set({ droppedItems: {}, rootIds: [], selectedId: null });
                }
            },

            deleteItem: (id) => {
                set((state) => ({
                    droppedItems: state.droppedItems.filter(item => item.id !== id),
                    selectedId: state.selectedId === id ? null : state.selectedId
                }));
            },

            // Computed/Derived values
            getRootItems: () => {
                return get().droppedItems.filter(item => !item.parentId);
            },

            getChildren: (parentId) => {
                const parent = get().droppedItems[parentId];
                return parent ? parent.childrenIds.map(childrenId => get().droppedItems[childrenId]) : [];
            },

            getSelectedElement: () => {
                const { droppedItems, selectedId } = get();
                return droppedItems[selectedId];
            },
        }),
        {
            limit: 50,
            equality: (a, b) => a === b,
        }
    )
);

export default useBuilderStore;