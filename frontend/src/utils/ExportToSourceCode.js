import useBuilderStore from '@/store/useBuilderStore';

// ─── Tag mapping ──────────────────────────────────────────────────────────────

const TAG_MAP = {
    card:   'div',
    label:  'span',
    button: 'button',
    input:  'input',
};

// ─── camelCase style object → CSS string ─────────────────────────────────────

const styleObjectToCSS = (styleObj = {}) =>
    Object.entries(styleObj)
        .map(([key, value]) => {
            const cssKey = key.replace(/([A-Z])/g, (m) => `-${m.toLowerCase()}`);
            return `${cssKey}: ${value}`;
        })
        .join('; ');

// ─── Merge props (mirrors DroppedItem.jsx) ────────────────────────────────────

const getMergedProps = (item) => {
    const defaults = {
        button: { children: 'Click me' },
        label:  { children: 'Label', text: 'Label' },
        card:   {},
        input:  { placeholder: 'Enter text here...', type: 'text' },
    };
    const merged = { ...(defaults[item.type] || {}), ...item.props };
    merged.children = item.props.children ?? item.props.text ?? merged.children ?? '';
    return merged;
};

// ─── Extract only background-related styles for the outer wrapper ─────────────

const BACKGROUND_PROPS = new Set([
    'backgroundColor', 'background', 'backgroundImage',
    'backgroundSize', 'backgroundPosition', 'backgroundRepeat',
]);

const extractBackgroundStyle = (styleObj = {}) => {
    const bg = {};
    for (const [key, value] of Object.entries(styleObj)) {
        if (BACKGROUND_PROPS.has(key)) bg[key] = value;
    }
    return bg;
};

// ─── Strip background props from component style (inner uses them differently) ─

const stripBackgroundStyle = (styleObj = {}) => {
    const stripped = {};
    for (const [key, value] of Object.entries(styleObj)) {
        if (!BACKGROUND_PROPS.has(key)) stripped[key] = value;
    }
    return stripped;
};

// ─── Convert px dimensions to vw/vh/% relative to canvas size ────────────────
// Canvas reference width — matches your builder canvas (1996px from the JSON)
const CANVAS_WIDTH = 1996;

const pxToVw = (px) => `${((px / CANVAS_WIDTH) * 100).toFixed(3)}vw`;
const pxToPercent = (px, parentPx) => `${((px / parentPx) * 100).toFixed(3)}%`;

// ─── Render a CHILD item — absolute positioned within its parent ──────────────

const renderChildItem = (item, allItems, parentWidth, parentHeight, depth = 0) => {
    const tag = TAG_MAP[item.type] || 'div';
    const mergedProps = getMergedProps(item);
    const indent = '  '.repeat(depth);

    const leftPct   = pxToPercent(item.layout.x, parentWidth);
    const topPct    = pxToPercent(item.layout.y, parentHeight);
    const widthPct  = pxToPercent(item.layout.width, parentWidth);
    const heightPct = pxToPercent(item.layout.height, parentHeight);

    const layoutStyle = [
        'position: absolute',
        `left: ${leftPct}`,
        `top: ${topPct}`,
        `width: ${widthPct}`,
        `height: ${heightPct}`,
        'box-sizing: border-box',
    ].join('; ');

    const componentStyle = styleObjectToCSS(mergedProps.style || {});
    const fullStyle = componentStyle
        ? `${layoutStyle}; ${componentStyle}`
        : layoutStyle;

    const childItems = (item.childrenIds || [])
        .map(id => allItems[id])
        .filter(Boolean);

    if (item.type === 'input') {
        const placeholder = mergedProps.placeholder ? ` placeholder="${mergedProps.placeholder}"` : '';
        const type = mergedProps.type ? ` type="${mergedProps.type}"` : '';
        return `${indent}<${tag} style="${fullStyle}"${placeholder}${type} />`;
    }

    if (childItems.length > 0) {
        const childrenHTML = childItems
            .map(child => renderChildItem(
                child, allItems,
                item.layout.width,
                item.layout.height,
                depth + 1
            ))
            .join('\n');
        return `${indent}<${tag} style="${fullStyle}">\n${childrenHTML}\n${indent}</${tag}>`;
    }

    const text = item.type !== 'card' ? (mergedProps.children || '') : '';
    return `${indent}<${tag} style="${fullStyle}">${text}</${tag}>`;
};

// ─── Render a ROOT item as two-layer structure ────────────────────────────────
//
//  <section>                   ← outer: full viewport width, carries background
//    <div class="inner">       ← inner: centered, max-width = canvas content width
//      ... children ...        ← absolutely positioned relative to inner div
//    </div>
//  </section>

const renderRootItem = (item, allItems, depth = 0) => {
    const mergedProps = getMergedProps(item);
    const indent = '  '.repeat(depth);
    const innerIndent = '  '.repeat(depth + 1);

    const itemStyle = mergedProps.style || {};

    // Outer section: full width + background only
    const bgStyle = extractBackgroundStyle(itemStyle);
    const outerStyle = styleObjectToCSS({
        width: '100%',
        ...bgStyle,
    });

    // Inner div: centered content, height from canvas
    const innerHeightVw = pxToVw(item.layout.height);
    const strippedStyle = stripBackgroundStyle(itemStyle);
    const innerComponentStyle = styleObjectToCSS(strippedStyle);
    const innerStyle = [
        'position: relative',
        'width: 100%',
        'max-width: var(--content-width)',
        'margin: 0 auto',
        `height: ${innerHeightVw}`,
        'box-sizing: border-box',
        innerComponentStyle,
    ].filter(Boolean).join('; ');

    const childItems = (item.childrenIds || [])
        .map(id => allItems[id])
        .filter(Boolean);

    const childrenHTML = childItems
        .map(child => renderChildItem(
            child, allItems,
            item.layout.width,
            item.layout.height,
            depth + 2
        ))
        .join('\n');

    return [
        `${indent}<section style="${outerStyle}">`,
        `${innerIndent}<div style="${innerStyle}">`,
        childrenHTML,
        `${innerIndent}</div>`,
        `${indent}</section>`,
    ].join('\n');
};

// ─── Render a full page to HTML ───────────────────────────────────────────────

const renderPageToHTML = (page, pageName) => {
    const { droppedItems, rootIds } = page;

    const rootItems = (rootIds || [])
        .map(id => droppedItems[id])
        .filter(Boolean);

    // Sort top-to-bottom by canvas Y position
    const sortedRoots = [...rootItems].sort((a, b) => a.layout.y - b.layout.y);

    // Derive content width from the widest root item on the canvas
    const maxCanvasWidth = sortedRoots.reduce(
        (max, item) => Math.max(max, item.layout.width), 0
    );
    const contentWidthVw = `${((maxCanvasWidth / CANVAS_WIDTH) * 100).toFixed(3)}vw`;

    const bodyContent = sortedRoots
        .map(item => renderRootItem(item, droppedItems, 2))
        .join('\n');

    return `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>${pageName}</title>
  <style>
    :root {
      --content-width: ${contentWidthVw};
    }

    *, *::before, *::after {
      box-sizing: border-box;
      margin: 0;
      padding: 0;
    }

    html, body {
      width: 100%;
      overflow-x: hidden;
      font-family: Inter, system-ui, sans-serif;
      background: #ffffff;
    }

    .page-root {
      width: 100%;
      display: flex;
      flex-direction: column;
      align-items: stretch;
    }

    section {
      width: 100%;
    }

    button {
      cursor: pointer;
      font-family: inherit;
    }

    input {
      font-family: inherit;
      outline: none;
    }
  </style>
</head>
<body>
  <div class="page-root">
${bodyContent}
  </div>
</body>
</html>`;
};

// ─── Main export function ─────────────────────────────────────────────────────

export const exportToSourceCode = () => {
    const state = useBuilderStore.getState();
    const { pages } = state;
    const pageEntries = Object.entries(pages);

    if (pageEntries.length === 0) {
        alert('No pages to export.');
        return;
    }

    pageEntries.forEach(([pageId, page], index) => {
        const pageName = page.name || pageId;
        const html = renderPageToHTML(page, pageName);
        setTimeout(() => {
            downloadFile(html, `${pageName}.html`, 'text/html');
        }, index * 300);
    });
};

// ─── Helper ───────────────────────────────────────────────────────────────────

const downloadFile = (content, filename, mimeType) => {
    const blob = new Blob([content], { type: mimeType });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
};