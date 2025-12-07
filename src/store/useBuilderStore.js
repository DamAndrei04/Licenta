import { create } from 'zustand';
import { temporal } from 'zundo';

const useBuilderStore = create(
    temporal(
        (set, get) => ({
            // State
            droppedItems: [],
            selectedId: null,

            // Actions
            handleDrop: (item, x, y, parentId = null) => {
                const newItem = {
                    id: `${item.componentType}-${Date.now()}`,
                    type: item.componentType,
                    x: parentId ? x : x,
                    y: parentId ? y : y,
                    width: item.defaultSize?.width || 100,
                    height: item.defaultSize?.height || 40,
                    parentId: parentId,
                    children: [],
                    props: {},
                };

                set((state) => {
                    const updated = [...state.droppedItems, newItem];

                    if (parentId) {
                        return {
                            droppedItems: updated.map(el =>
                                el.id === parentId
                                    ? { ...el, children: [...el.children, newItem.id] }
                                    : el
                            )
                        };
                    }

                    return { droppedItems: updated };
                });
            },

            updateItem: (id, updates) => {
                set((state) => ({
                    droppedItems: state.droppedItems.map(item =>
                        item.id === id ? { ...item, ...updates } : item
                    )
                }));
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
                    set({ droppedItems: [], selectedId: null });
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
                return get().droppedItems.filter(item => item.parentId === parentId);
            },

            getSelectedElement: () => {
                const { droppedItems, selectedId } = get();
                return droppedItems.find(item => item.id === selectedId);
            },
        }),
        {
            limit: 50,
            equality: (a, b) => a === b,
        }
    )
);

export default useBuilderStore;