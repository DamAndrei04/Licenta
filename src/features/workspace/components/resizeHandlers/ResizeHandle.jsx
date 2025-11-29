import "./ResizeHandle.css"

function ResizeHandle({ dir, item, updateItem }) {
    const startResize = (e) => {
        e.preventDefault();
        e.stopPropagation();

        const startX = e.clientX;
        const startY = e.clientY;

        const startW = item.width;
        const startH = item.height;


        const onMove = (ev) => {
            let w = startW;
            let h = startH;

            const dx = ev.clientX - startX;
            const dy = ev.clientY - startY;

            if (dir.includes("e")) w = startW + dx;
            if (dir.includes("w")) w = startW - dx;
            if (dir.includes("s")) h = startH + dy;
            if (dir.includes("n")) h = startH - dy;

            updateItem(item.id, {
                width: Math.max(40, w),
                height: Math.max(30, h),
            });
        };

        const onUp = () => {
            window.removeEventListener("mousemove", onMove);
            window.removeEventListener("mouseup", onUp);
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