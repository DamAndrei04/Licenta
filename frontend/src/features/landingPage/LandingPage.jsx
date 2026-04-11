import { useState, useEffect } from "react";
import "./LandingPage.css";

export default function LandingPage() {
    const [scrolled, setScrolled] = useState(false);
    const [menuOpen, setMenuOpen] = useState(false);

    useEffect(() => {
        const onScroll = () => setScrolled(window.scrollY > 40);
        window.addEventListener("scroll", onScroll);
        return () => window.removeEventListener("scroll", onScroll);
    }, []);

    return (
        <div className="lp-root">
            {/* ── Noise overlay ── */}
            <div className="lp-noise" />

            {/* ── Navbar ── */}
            <nav className={`lp-nav ${scrolled ? "lp-nav--scrolled" : ""}`}>
                <a href="#" className="lp-logo">
                    <span className="lp-logo-icon">⬡</span>
                    <span className="lp-logo-text">ForgeUI</span>
                </a>

                <button
                    className={`lp-burger ${menuOpen ? "open" : ""}`}
                    onClick={() => setMenuOpen(!menuOpen)}
                    aria-label="Toggle menu"
                >
                    <span /><span /><span />
                </button>

                <div className={`lp-nav-links ${menuOpen ? "lp-nav-links--open" : ""}`}>
                    <a href="/login" className="lp-nav-link">Sign in</a>
                    <a href="/register" className="lp-nav-btn">Register</a>
                </div>
            </nav>

            {/* ── Hero ── */}
            <section className="lp-hero">
                {/* Background orbs */}
                <div className="lp-orb lp-orb--1" />
                <div className="lp-orb lp-orb--2" />
                <div className="lp-orb lp-orb--3" />

                {/* Grid lines */}
                <div className="lp-grid" />

                <div className="lp-hero-inner">
                    <div className="lp-badge">
                        <span className="lp-badge-dot" />
                        Agentic AI · Drag & Drop · Live Preview
                    </div>

                    <h1 className="lp-headline">
                        <span className="lp-headline-line lp-headline-line--1">Build interfaces</span>
                        <span className="lp-headline-line lp-headline-line--2">
              at the speed of<br />
              <em className="lp-headline-accent">thought.</em>
            </span>
                    </h1>

                    <p className="lp-sub">
                        Describe what you need. Our multi-agent AI drafts the layout,<br className="lp-br" />
                        you refine it with drag & drop. Ship in minutes, not days.
                    </p>

                    <div className="lp-cta-row">
                        <a href="/register" className="lp-cta-btn">
                            <span>Let's get started</span>
                            <svg className="lp-cta-arrow" viewBox="0 0 20 20" fill="none">
                                <path d="M4 10h12M11 5l5 5-5 5" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"/>
                            </svg>
                        </a>
                        <a href="#how-it-works" className="lp-ghost-btn">See how it works</a>
                    </div>

                    {/* Floating UI mock */}
                    <div className="lp-mockup">
                        <div className="lp-mockup-bar">
                            <span /><span /><span />
                        </div>
                        <div className="lp-mockup-body">
                            <div className="lp-mockup-sidebar">
                                {["Button", "Card", "Input", "Modal", "Nav"].map((c) => (
                                    <div key={c} className="lp-mockup-chip">{c}</div>
                                ))}
                            </div>
                            <div className="lp-mockup-canvas">
                                <div className="lp-mockup-block lp-mockup-block--wide" />
                                <div className="lp-mockup-row">
                                    <div className="lp-mockup-block" />
                                    <div className="lp-mockup-block" />
                                </div>
                                <div className="lp-mockup-block lp-mockup-block--short" />
                                <div className="lp-mockup-cursor">
                                    <svg viewBox="0 0 14 18" fill="none">
                                        <path d="M1 1l11 7-5.5 1.5L4 16 1 1z" fill="#65ffb2" stroke="#65ffb2" strokeWidth="0.5"/>
                                    </svg>
                                </div>
                            </div>
                            <div className="lp-mockup-ai">
                                <div className="lp-mockup-ai-label">AI Agent</div>
                                <div className="lp-mockup-ai-msg">Generating navbar component…</div>
                                <div className="lp-mockup-ai-dots">
                                    <span /><span /><span />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* ── Features ── */}
            <section className="lp-features" id="how-it-works">
                <div className="lp-features-inner">
                    <p className="lp-section-label">How it works</p>
                    <h2 className="lp-section-title">From prompt to&nbsp;pixel.</h2>

                    <div className="lp-feature-grid">
                        {[
                            {
                                icon: "✦",
                                color: "#65ffb2",
                                title: "Describe your vision",
                                body: "Type what you want to build. Our analyst and planner agents break it down into a structured UI blueprint.",
                            },
                            {
                                icon: "◈",
                                color: "#65b2ff",
                                title: "AI builds the scaffold",
                                body: "Multiple specialised agents generate React components, layout, and styling — page by page, in seconds.",
                            },
                            {
                                icon: "⊹",
                                color: "#ffb265",
                                title: "Drag, drop & refine",
                                body: "Take control with an intuitive canvas. Move, resize, and restyle elements until every pixel is perfect.",
                            },
                        ].map((f) => (
                            <div className="lp-feature-card" key={f.title}>
                                <span className="lp-feature-icon" style={{ color: f.color }}>{f.icon}</span>
                                <h3 className="lp-feature-title">{f.title}</h3>
                                <p className="lp-feature-body">{f.body}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            {/* ── Footer CTA ── */}
            <section className="lp-footer-cta">
                <div className="lp-footer-cta-orb" />
                <p className="lp-footer-cta-label">Ready to build?</p>
                <h2 className="lp-footer-cta-title">Your next interface<br />starts here.</h2>
                <a href="/register" className="lp-cta-btn lp-cta-btn--large">
                    <span>Create free account</span>
                    <svg className="lp-cta-arrow" viewBox="0 0 20 20" fill="none">
                        <path d="M4 10h12M11 5l5 5-5 5" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"/>
                    </svg>
                </a>
            </section>

            {/* ── Site footer ── */}
            <footer className="lp-site-footer">
        <span className="lp-logo">
          <span className="lp-logo-icon">⬡</span>
          <span className="lp-logo-text">Forgeui</span>
        </span>
                <p>© 2026 ForgeUI. All rights reserved.</p>
            </footer>
        </div>
    );
}