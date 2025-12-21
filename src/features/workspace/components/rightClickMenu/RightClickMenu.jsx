import { useEffect, useRef } from 'react';
import './RightClickMenu.css';

const RightClickMenu = ({ x, y, onClose, onDelete }) => {
    const menuRef = useRef(null);

    useEffect(() => {
        const handleClickOutside = (e) => {
            if (menuRef.current && !menuRef.current.contains(e.target)) {
                onClose();
            }
        };

        const handleEscape = (e) => {
            if (e.key === 'Escape') {
                onClose();
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        document.addEventListener('keydown', handleEscape);

        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
            document.removeEventListener('keydown', handleEscape);
        };
    }, [onClose]);

    return (
        <div
            ref={menuRef}
            className="context-menu"
            style={{
                position: 'fixed',
                left: x,
                top: y,
            }}
        >
            <button
                className="context-menu-item"
                onClick={onDelete}
            >
                Delete item
            </button>
        </div>
    );
};

export default RightClickMenu;