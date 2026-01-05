import "./ResizeHandle.css"
import {useRef} from "react";

function ResizeHandle({ dir, item, updateItem, canvasRef, parentWidth, parentHeight, onPreview }) {

    const resizeRef = useRef({
        width: item.layout.width,
        height: item.layout.height,
        x: item.layout.x,
        y: item.layout.y,
    });

    const startResize = (e) => {
        e.preventDefault();
        e.stopPropagation();

        const startX = e.clientX;
        const startY = e.clientY;

        const startW = item.layout.width;
        const startH = item.layout.height;

        const startLeft = item.layout.x;
        const startTop = item.layout.y;

        const minWidth = 40;
        const minHeight = 30;

        const onMove = (ev) => {

            let w = startW;
            let h = startH;
            let x = startLeft;
            let y = startTop;

            const dx = ev.clientX - startX;
            const dy = ev.clientY - startY;

            let maxWidth, maxHeight;

            if (parentWidth !== null && parentHeight !== null) {
                maxWidth = parentWidth - startLeft;
                maxHeight = parentHeight - startTop;
            } else if (canvasRef?.current) {
                const canvasRect = canvasRef.current.getBoundingClientRect();
                maxWidth = canvasRect.width - startLeft;
                maxHeight = canvasRect.height - startTop;
            }

            if (dir.includes("e")){
                w = Math.min(Math.max(minWidth, startW + dx), maxWidth);
            }
            if (dir.includes("w")) {
                const maxW = parentWidth ? startLeft + startW : startLeft + startW;
                w = Math.min(Math.max(minWidth, startW - dx), maxW);
                x = Math.min(startLeft + dx, startLeft + startW - minWidth);
                x = Math.max(0, x);
            }
            if (dir.includes("s")) {
                h = Math.min(Math.max(minHeight, startH + dy), maxHeight);
            }
            if (dir.includes("n")) {
                const maxH = parentHeight ? startTop + startH : startTop + startH;
                h = Math.min(Math.max(minHeight, startH - dy), maxH);
                y = Math.min(startTop + dy, startTop + startH - minHeight);
                y = Math.max(0, y);
            }

            resizeRef.current = { width: w, height: h, x, y };

            onPreview(resizeRef.current);
        };

        const onUp = () => {
            window.removeEventListener("mousemove", onMove);
            window.removeEventListener("mouseup", onUp);

            const { width, height, x, y } = resizeRef.current;

            updateItem(item.id, {
                layout: {
                    width,
                    height,
                    x,
                    y
                }
            });
            onPreview(null);
        };

        window.addEventListener("mousemove", onMove);
        window.addEventListener("mouseup", onUp);
    };

    const positions = {
        n: { top: 0, left: "50%", cursor: "ns-resize", transform: "translateX(-50%)" },
        s: { bottom: 0, left: "50%", cursor: "ns-resize", transform: "translateX(-50%)" },
        e: { right: 0, top: "50%", cursor: "ew-resize", transform: "translateY(-50%)" },
        w: { left: 0, top: "50%", cursor: "ew-resize", transform: "translateY(-50%)" },
        ne: { top: 0, right: 0, cursor: "nesw-resize" },
        nw: { top: 0, left: 0, cursor: "nwse-resize" },
        se: { bottom: 0, right: 0, cursor: "nwse-resize" },
        sw: { bottom: 0, left: 0, cursor: "nesw-resize" },
    };

    return (
        <div
            className="resize-handle"
            onMouseDown={startResize}
            style={{...positions[dir]}}
        />
    );
}

export default ResizeHandle;