import { useState, useRef, useEffect } from 'react';
import { createPortal } from 'react-dom';
import './AgentChat.css';
import { sendPromptToAgent } from '@/api/AgentService';
import useBuilderStore from '@/store/useBuilderStore';

const INITIAL_MESSAGES = [
    {
        id: 1,
        role: 'agent',
        text: "Hello! Describe what you want to build or modify and I'll take care of it.",
    },
];

export default function AgentChat() {
    const [isOpen, setIsOpen] = useState(false);
    const [messages, setMessages] = useState(INITIAL_MESSAGES);
    const [input, setInput] = useState('');
    const [isThinking, setIsThinking] = useState(false);
    const [panelStyle, setPanelStyle] = useState({});

    const toggleRef = useRef(null);
    const messagesEndRef = useRef(null);
    const inputRef = useRef(null);

    const loadState = useBuilderStore((state) => state.loadState);

    useEffect(() => {
        if (!isOpen || !toggleRef.current) return;
        const rect = toggleRef.current.getBoundingClientRect();
        setPanelStyle({
            position: 'fixed',
            top: rect.bottom + 6,
            left: rect.left,
        });
    }, [isOpen]);

    useEffect(() => {
        if (isOpen) {
            messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
            inputRef.current?.focus();
        }
    }, [isOpen, messages]);

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

    const handleSend = async () => {
        const text = input.trim();
        if (!text || isThinking) return;

        setMessages((prev) => [...prev, { id: Date.now(), role: 'user', text }]);
        setInput('');
        setIsThinking(true);

        try {
            const res = await sendPromptToAgent({ prompt: text });
            const descriptor = res.data; // UIDescriptor — { pages: { ... } }

            if (descriptor?.pages) {
                loadState(descriptor.pages);
                setMessages((prev) => [
                    ...prev,
                    {
                        id: Date.now() + 1,
                        role: 'agent',
                        text: `Done! Generated ${Object.keys(descriptor.pages).length} page(s). You can now edit or save.`,
                    },
                ]);
            } else {
                throw new Error('Agent returned empty or invalid response');
            }
        } catch (err) {
            console.error('Agent call failed:', err);
            setMessages((prev) => [
                ...prev,
                {
                    id: Date.now() + 1,
                    role: 'agent',
                    text: 'Something went wrong. Please try again.',
                },
            ]);
        } finally {
            setIsThinking(false);
        }
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
            {createPortal(panel, document.body)}
        </div>
    );
}