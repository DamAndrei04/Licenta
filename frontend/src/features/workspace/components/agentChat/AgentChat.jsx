import { useState, useRef, useEffect } from 'react';
import { createPortal } from 'react-dom';
import './AgentChat.css';

const MOCK_MESSAGES = [
    {
        id: 1,
        role: 'agent',
        text: "Hello! Describe what you want to build or modify and I'll take care of it.",
    },
];

export default function AgentChat() {
    const [isOpen, setIsOpen] = useState(false);
    const [messages, setMessages] = useState(MOCK_MESSAGES);
    const [input, setInput] = useState('');
    const [isThinking, setIsThinking] = useState(false);
    const [panelStyle, setPanelStyle] = useState({});

    const toggleRef = useRef(null);
    const messagesEndRef = useRef(null);
    const inputRef = useRef(null);

    // Position the portal panel relative to the toggle button
    useEffect(() => {
        if (!isOpen || !toggleRef.current) return;

        const rect = toggleRef.current.getBoundingClientRect();
        setPanelStyle({
            position: 'fixed',
            top: rect.bottom + 6,
            left: rect.left,
        });
    }, [isOpen]);

    // Auto-scroll & focus when open
    useEffect(() => {
        if (isOpen) {
            messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
            inputRef.current?.focus();
        }
    }, [isOpen, messages]);

    // Close on outside click
    useEffect(() => {
        if (!isOpen) return;
        const onPointerDown = (e) => {
            if (
                toggleRef.current?.contains(e.target) ||
                document.getElementById('ac-portal')?.contains(e.target)
            ) return;
            setIsOpen(false);
        };
        document.addEventListener('pointerdown', onPointerDown);
        return () => document.removeEventListener('pointerdown', onPointerDown);
    }, [isOpen]);

    const handleToggle = () => setIsOpen((prev) => !prev);

    const handleSend = () => {
        const text = input.trim();
        if (!text || isThinking) return;

        setMessages((prev) => [...prev, { id: Date.now(), role: 'user', text }]);
        setInput('');
        setIsThinking(true);

        // TODO: replace with real MAS endpoint
        setTimeout(() => {
            setMessages((prev) => [
                ...prev,
                { id: Date.now() + 1, role: 'agent', text: 'Got it! Working on your request…' },
            ]);
            setIsThinking(false);
        }, 1500);
    };

    const handleKeyDown = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSend();
        }
    };

    const panel = (
        <div
            id="ac-portal"
            className={`ac-panel ${isOpen ? 'ac-panel--open' : ''}`}
            style={panelStyle}
        >
            {/* Messages */}
            <div className="ac-messages">
                {messages.map((msg) => (
                    <div key={msg.id} className={`ac-msg ac-msg--${msg.role}`}>
                        {msg.role === 'agent' && (
                            <span className="ac-msg-avatar">⬡</span>
                        )}
                        <span className="ac-msg-bubble">{msg.text}</span>
                    </div>
                ))}
                {isThinking && (
                    <div className="ac-msg ac-msg--agent">
                        <span className="ac-msg-avatar">⬡</span>
                        <span className="ac-msg-bubble ac-msg-bubble--thinking">
                            <span /><span /><span />
                        </span>
                    </div>
                )}
                <div ref={messagesEndRef} />
            </div>

            {/* Input row */}
            <div className="ac-input-row">
                <textarea
                    ref={inputRef}
                    className="ac-input"
                    placeholder="Describe what to build or change…"
                    value={input}
                    onChange={(e) => setInput(e.target.value)}
                    onKeyDown={handleKeyDown}
                    rows={1}
                    disabled={isThinking}
                />
                <button
                    className="ac-send"
                    onClick={handleSend}
                    disabled={!input.trim() || isThinking}
                    title="Send (Enter)"
                >
                    <svg viewBox="0 0 16 16" fill="none">
                        <path d="M2 8h12M9 3l5 5-5 5" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" />
                    </svg>
                </button>
            </div>
        </div>
    );

    return (
        <div className={`ac-root ${isOpen ? 'ac-root--open' : ''}`}>
            {/* Toggle button — stays in the toolbar flow */}
            <button ref={toggleRef} className="ac-toggle" onClick={handleToggle}>
                <span className="ac-toggle-left">
                    <span className="ac-icon">⬡</span>
                    <span className="ac-toggle-label">AI Agent</span>
                    {isThinking && (
                        <span className="ac-thinking-badge">
                            <span /><span /><span />
                        </span>
                    )}
                </span>
                <span className="ac-toggle-right">
                    <span className="ac-msg-count">{messages.length} messages</span>
                    <svg
                        className={`ac-chevron ${isOpen ? 'ac-chevron--up' : ''}`}
                        viewBox="0 0 16 16"
                        fill="none"
                    >
                        <path d="M4 10l4-4 4 4" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
                    </svg>
                </span>
            </button>

            {/* Panel rendered into document.body — escapes toolbar overflow/stacking */}
            {createPortal(panel, document.body)}
        </div>
    );
}