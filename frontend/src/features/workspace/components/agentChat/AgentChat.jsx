import { useState, useRef, useEffect } from 'react';
import { createPortal } from 'react-dom';
import './AgentChat.css';
import { sendPromptToAgent, subscribeToAgentStatus } from '@/api/AgentService';
import useBuilderStore from '@/store/useBuilderStore';

const INITIAL_MESSAGES = [
    {
        id: 1,
        role: 'agent',
        text: "Hello! Describe what you want to build or modify and I'll take care of it.",
    },
];

// Matches AgentPhase enum order in the backend
const PHASES = [
    { id: 'ANALYST',   label: 'Analyst',      desc: 'Analyzing your request' },
    { id: 'PLANNER',   label: 'Planner',       desc: 'Planning the layout' },
    { id: 'BUILDER',   label: 'Page Builder',  desc: 'Building components' },
    { id: 'VALIDATOR', label: 'Validator',     desc: 'Validating output' },
];

const PHASE_INDEX = { ANALYST: 0, PLANNER: 1, BUILDER: 2, VALIDATOR: 3 };

export default function AgentChat() {
    const [isOpen, setIsOpen] = useState(false);
    const [messages, setMessages] = useState(INITIAL_MESSAGES);
    const [input, setInput] = useState('');
    const [isThinking, setIsThinking] = useState(false);
    const [panelStyle, setPanelStyle] = useState({});

    // Phase state driven by real SSE events from MAS
    const [activePhaseIdx, setActivePhaseIdx] = useState(-1);
    const [completedPhaseIdxs, setCompletedPhaseIdxs] = useState(new Set());
    const [builderPageInfo, setBuilderPageInfo] = useState(null); // { name, index, total }
    const [retryInfo, setRetryInfo] = useState(null);             // { attempt, maxAttempts }

    const toggleRef = useRef(null);
    const messagesEndRef = useRef(null);
    const inputRef = useRef(null);
    const sseRef = useRef(null);          // holds the active EventSource
    const failureRef = useRef(null);      // populated by the FAILED SSE event

    const loadState = useBuilderStore((state) => state.loadState);

    // Clean up SSE and phase state when thinking ends
    useEffect(() => {
        if (!isThinking) {
            if (sseRef.current) {
                sseRef.current.close();
                sseRef.current = null;
            }
            // Keep completed phases visible briefly then clear
            const t = setTimeout(() => {
                setActivePhaseIdx(-1);
                setCompletedPhaseIdxs(new Set());
                setBuilderPageInfo(null);
                setRetryInfo(null);
            }, 1200);
            return () => clearTimeout(t);
        }
    }, [isThinking]);

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
        setActivePhaseIdx(-1);
        setCompletedPhaseIdxs(new Set());
        setBuilderPageInfo(null);
        setRetryInfo(null);
        failureRef.current = null;

        // Open the SSE stream before sending the prompt so no events are missed
        const sessionId = crypto.randomUUID();
        const es = subscribeToAgentStatus(sessionId, (event) => {
            const idx = PHASE_INDEX[event.phase];

            if (event.status === 'RETRY') {
                // Validation failed — new attempt starting. Reset all phase state
                // and show the retry badge so the user knows what's happening.
                setActivePhaseIdx(-1);
                setCompletedPhaseIdxs(new Set());
                setBuilderPageInfo(null);
                setRetryInfo({ attempt: event.attemptNumber, maxAttempts: event.maxAttempts });
                return;
            }

            if (event.status === 'FAILED') {
                // All retries exhausted — capture details so the catch block can
                // show a meaningful message. The HTTP error arrives shortly after.
                failureRef.current = {
                    maxAttempts: event.maxAttempts,
                    violations: event.violations || [],
                };
                setRetryInfo(null);
                return;
            }

            if (idx === undefined) return;

            if (event.status === 'STARTED') {
                setActivePhaseIdx(idx);
            } else if (event.status === 'COMPLETED') {
                setCompletedPhaseIdxs((prev) => new Set([...prev, idx]));
                setActivePhaseIdx(-1);
            } else if (event.status === 'PAGE_STARTED') {
                setActivePhaseIdx(idx);
                setBuilderPageInfo({
                    name: event.currentPageName,
                    index: event.currentPageIndex,
                    total: event.totalPages,
                });
            } else if (event.status === 'PAGE_COMPLETED') {
                setBuilderPageInfo((prev) =>
                    prev ? { ...prev, name: event.currentPageName, index: event.currentPageIndex } : prev
                );
            }
        });
        sseRef.current = es;

        try {
            const res = await sendPromptToAgent({ prompt: text, sessionId });
            const descriptor = res.data;

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
            const failure = failureRef.current;
            failureRef.current = null;
            setMessages((prev) => [
                ...prev,
                {
                    id: Date.now() + 1,
                    role: 'agent',
                    text: failure
                        ? `Generation failed after ${failure.maxAttempts} attempt(s) — the validator kept rejecting the output. Try rephrasing your prompt with more detail.`
                        : 'Something went wrong. Please try again.',
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

                {/* Agent workflow status — driven by real SSE events from MAS */}
                {(isThinking || completedPhaseIdxs.size > 0) && (
                    <div className={`ac-status ${!isThinking && completedPhaseIdxs.size === PHASES.length ? 'ac-status--done' : ''}`}>
                        <div className="ac-status-header">
                            <span className="ac-status-icon">⬡</span>
                            <span className="ac-status-label">
                                {isThinking ? 'Agent working…' : 'Done'}
                            </span>
                            {retryInfo && (
                                <span className="ac-retry-badge">
                                    retry {retryInfo.attempt}/{retryInfo.maxAttempts}
                                </span>
                            )}
                            {isThinking && !retryInfo && (
                                <span className="ac-status-dots">
                                    <span /><span /><span />
                                </span>
                            )}
                        </div>
                        <div className="ac-phases">
                            {PHASES.map((phase, i) => {
                                const isDone = completedPhaseIdxs.has(i);
                                const isActive = isThinking && i === activePhaseIdx;
                                const isBuilder = phase.id === 'BUILDER';
                                return (
                                    <div
                                        key={phase.id}
                                        className={`ac-phase ${isDone ? 'ac-phase--done' : ''} ${isActive ? 'ac-phase--active' : ''}`}
                                    >
                                        <span className="ac-phase-indicator">
                                            {isDone ? (
                                                <svg viewBox="0 0 12 12" fill="none">
                                                    <path d="M2 6l3 3 5-5" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                                                </svg>
                                            ) : isActive ? (
                                                <span className="ac-phase-pulse" />
                                            ) : (
                                                <span className="ac-phase-idle" />
                                            )}
                                        </span>
                                        <div className="ac-phase-text">
                                            <span className="ac-phase-name">{phase.label}</span>
                                            {isActive && isBuilder && builderPageInfo ? (
                                                <span className="ac-phase-desc">
                                                    Building page {builderPageInfo.index}/{builderPageInfo.total}: <em>{builderPageInfo.name}</em>
                                                </span>
                                            ) : isActive && (
                                                <span className="ac-phase-desc">{phase.desc}</span>
                                            )}
                                        </div>
                                        {isActive && isBuilder && builderPageInfo && (
                                            <span className="ac-phase-page-badge">
                                                {builderPageInfo.index}/{builderPageInfo.total}
                                            </span>
                                        )}
                                    </div>
                                );
                            })}
                        </div>
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