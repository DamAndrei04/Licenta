import { useState } from 'react';
import './TutorialModal.css';

const STEPS = [
    {
        icon: '⬡',
        iconColor: '#65ffb2',
        title: 'Welcome to ForgeUI',
        description:
            'ForgeUI is an AI-powered UI builder. Describe what you want to create and the agent will generate it instantly — or drag and drop components yourself.',
        hint: null,
    },
    {
        icon: '◈',
        iconColor: '#65b2ff',
        title: 'Create a Project',
        description:
            'From the Dashboard, click "New project" to get started. Give your project a name and optional description, then open it in the editor.',
        hint: 'Tip: All projects are saved to the cloud so you can pick up where you left off.',
    },
    {
        icon: '⊹',
        iconColor: '#ffb265',
        title: 'Drag & Drop Editor',
        description:
            'Inside the workspace, drag components (Button, Card, Input, Label) from the left toolbar onto the canvas. Click any element to select it, then resize or reposition it freely.',
        hint: 'Tip: Use Ctrl+Z / Ctrl+Y to undo and redo changes.',
    },
    {
        icon: '✦',
        iconColor: '#65ffb2',
        title: 'Chat with the AI Agent',
        description:
            'Click the "AI Agent" button in the toolbar to open the chat panel. Describe what you want to build in plain English — the agent will generate a full UI layout for you.',
        hint: 'Tip: Be specific! "Create a login form with an email input, password input, and a submit button" works great.',
    },
    {
        icon: '◎',
        iconColor: '#65b2ff',
        title: 'Save & Export',
        description:
            'Use the Save button to persist your work at any time. Export your project as a JSON file to share with teammates, or as a standalone HTML file ready to deploy.',
        hint: null,
    },
];

export default function TutorialModal({ onClose }) {
    const [step, setStep] = useState(0);

    const current = STEPS[step];
    const isFirst = step === 0;
    const isLast = step === STEPS.length - 1;

    const next = () => (isLast ? onClose() : setStep((s) => s + 1));
    const prev = () => !isFirst && setStep((s) => s - 1);

    return (
        <div className="tm-overlay" onClick={onClose}>
            <div className="tm-modal" onClick={(e) => e.stopPropagation()}>
                <div className="tm-header">
                    <div className="tm-dots">
                        {STEPS.map((_, i) => (
                            <button
                                key={i}
                                className={`tm-dot ${i === step ? 'tm-dot--active' : ''} ${i < step ? 'tm-dot--done' : ''}`}
                                onClick={() => setStep(i)}
                                aria-label={`Step ${i + 1}`}
                            />
                        ))}
                    </div>
                    <button className="tm-skip" onClick={onClose}>
                        Skip
                    </button>
                </div>

                <div className="tm-body">
                    <span className="tm-icon" style={{ color: current.iconColor }}>
                        {current.icon}
                    </span>
                    <h2 className="tm-title">{current.title}</h2>
                    <p className="tm-desc">{current.description}</p>
                    {current.hint && (
                        <div className="tm-hint">
                            <span className="tm-hint-bar" />
                            {current.hint}
                        </div>
                    )}
                </div>

                <div className="tm-footer">
                    <span className="tm-step-label">
                        {step + 1} / {STEPS.length}
                    </span>
                    <div className="tm-actions">
                        <button className="tm-btn tm-btn--ghost" onClick={prev} disabled={isFirst}>
                            Back
                        </button>
                        <button className="tm-btn tm-btn--primary" onClick={next}>
                            {isLast ? 'Get started' : 'Next'}
                            {!isLast && (
                                <svg viewBox="0 0 16 16" fill="none">
                                    <path
                                        d="M3 8h10M9 4l4 4-4 4"
                                        stroke="currentColor"
                                        strokeWidth="1.5"
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                    />
                                </svg>
                            )}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}
