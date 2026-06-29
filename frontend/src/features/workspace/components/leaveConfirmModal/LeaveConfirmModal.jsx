import { createPortal } from 'react-dom';
import { useEffect } from 'react';
import { AlertTriangle } from 'lucide-react';
import './LeaveConfirmModal.css';

/**
 * Confirmation modal shown when the user tries to leave the workspace and
 * return to the dashboard. Offers to save the current project state before
 * leaving, leave without saving, or stay on the page.
 */
const LeaveConfirmModal = ({ onSaveAndLeave, onLeaveWithoutSaving, onCancel, saving }) => {
    // Close on Escape — same affordance as clicking the overlay / Cancel
    useEffect(() => {
        const onKeyDown = (e) => {
            if (e.key === 'Escape' && !saving) onCancel();
        };
        window.addEventListener('keydown', onKeyDown);
        return () => window.removeEventListener('keydown', onKeyDown);
    }, [onCancel, saving]);

    return createPortal(
        <div className="leave-overlay" onClick={saving ? undefined : onCancel}>
            <div className="leave-modal" onClick={(e) => e.stopPropagation()}>
                <div className="leave-icon">
                    <AlertTriangle size={28} />
                </div>
                <h2 className="leave-title">Leave this project?</h2>
                <p className="leave-sub">
                    Do you want to save the current state of the project before leaving?
                    The progress will be lost otherwise.
                </p>
                <div className="leave-actions">
                    <button
                        className="leave-btn leave-btn--cancel"
                        onClick={onCancel}
                        disabled={saving}
                    >
                        Cancel
                    </button>
                    <button
                        className="leave-btn leave-btn--discard"
                        onClick={onLeaveWithoutSaving}
                        disabled={saving}
                    >
                        Leave without saving
                    </button>
                    <button
                        className="leave-btn leave-btn--save"
                        onClick={onSaveAndLeave}
                        disabled={saving}
                    >
                        {saving ? 'Saving...' : 'Save & leave'}
                    </button>
                </div>
            </div>
        </div>,
        document.body
    );
};

export default LeaveConfirmModal;
