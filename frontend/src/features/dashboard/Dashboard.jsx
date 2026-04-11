import { useState } from "react";
import "./Dashboard.css";

const MOCK_USER = { id: 1, username: "andrei" };

const MOCK_PROJECTS = [
    {
        id: 1,
        user_id: 1,
        name: "E-Commerce UI",
        description: "A full storefront with cart, checkout, and product pages.",
        created_at: "2026-03-10T09:14:00Z",
        updated_at: "2026-04-08T16:42:00Z",
    },
    {
        id: 2,
        user_id: 1,
        name: "Admin Dashboard",
        description: "Internal analytics and user management panel.",
        created_at: "2026-03-22T11:00:00Z",
        updated_at: "2026-04-01T10:05:00Z",
    },
    {
        id: 3,
        user_id: 1,
        name: "Landing Page",
        description: "",
        created_at: "2026-04-05T08:30:00Z",
        updated_at: "2026-04-05T08:30:00Z",
    },
];

function timeAgo(iso) {
    const diff = Date.now() - new Date(iso).getTime();
    const mins = Math.floor(diff / 60000);
    if (mins < 1) return "just now";
    if (mins < 60) return `${mins}m ago`;
    const hours = Math.floor(mins / 60);
    if (hours < 24) return `${hours}h ago`;
    const days = Math.floor(hours / 24);
    if (days < 7) return `${days}d ago`;
    return new Date(iso).toLocaleDateString("en-GB", { day: "2-digit", month: "short", year: "numeric" });
}

function formatDate(iso) {
    return new Date(iso).toLocaleDateString("en-GB", {
        day: "2-digit",
        month: "short",
        year: "numeric",
    });
}

export default function Dashboard() {
    const [projects, setProjects] = useState(MOCK_PROJECTS);
    const [modalOpen, setModalOpen] = useState(false);
    const [deleteTarget, setDeleteTarget] = useState(null);
    const [form, setForm] = useState({ name: "", description: "" });
    const [errors, setErrors] = useState({});

    const openModal = () => {
        setForm({ name: "", description: "" });
        setErrors({});
        setModalOpen(true);
    };

    const closeModal = () => setModalOpen(false);

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
        setErrors({ ...errors, [e.target.name]: undefined });
    };

    const handleCreate = (e) => {
        e.preventDefault();
        const errs = {};
        if (!form.name.trim()) errs.name = "Project name is required.";
        if (Object.keys(errs).length) { setErrors(errs); return; }

        const now = new Date().toISOString();
        const newProject = {
            id: Date.now(),
            user_id: MOCK_USER.id,
            name: form.name.trim(),
            description: form.description.trim(),
            created_at: now,
            updated_at: now,
        };
        setProjects([newProject, ...projects]);
        closeModal();
    };

    const confirmDelete = (id) => setDeleteTarget(id);
    const cancelDelete = () => setDeleteTarget(null);
    const handleDelete = () => {
        setProjects(projects.filter((p) => p.id !== deleteTarget));
        setDeleteTarget(null);
    };

    return (
        <div className="db-root">
            {/* ── Navbar ── */}
            <nav className="db-nav">
                <a href="/" className="db-logo">
                    <span className="db-logo-icon">⬡</span>
                    <span className="db-logo-text">ForgeUI</span>
                </a>
                <div className="db-nav-right">
          <span className="db-user-badge">
            <span className="db-user-dot" />
              {MOCK_USER.username}
          </span>
                    <a href="/login" className="db-logout">Sign out</a>
                </div>
            </nav>

            {/* ── Main ── */}
            <main className="db-main">
                {/* Header row */}
                <div className="db-header">
                    <div>
                        <p className="db-eyebrow">Your workspace</p>
                        <h1 className="db-title">Projects</h1>
                    </div>
                    <button className="db-create-btn" onClick={openModal}>
                        <svg viewBox="0 0 16 16" fill="none" className="db-plus-icon">
                            <path d="M8 2v12M2 8h12" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                        </svg>
                        New project
                    </button>
                </div>

                {/* Stats row */}
                <div className="db-stats">
                    <div className="db-stat">
                        <span className="db-stat-value">{projects.length}</span>
                        <span className="db-stat-label">Total projects</span>
                    </div>
                    <div className="db-stat">
            <span className="db-stat-value">
              {projects.filter(p => {
                  const d = new Date(p.updated_at);
                  const now = new Date();
                  return (now - d) < 7 * 24 * 60 * 60 * 1000;
              }).length}
            </span>
                        <span className="db-stat-label">Updated this week</span>
                    </div>
                </div>

                {/* Project grid */}
                {projects.length === 0 ? (
                    <div className="db-empty">
                        <div className="db-empty-icon">◈</div>
                        <p className="db-empty-title">No projects yet</p>
                        <p className="db-empty-sub">Create your first project to get started.</p>
                        <button className="db-create-btn" onClick={openModal}>New project</button>
                    </div>
                ) : (
                    <div className="db-grid">
                        {projects.map((project, i) => (
                            <ProjectCard
                                key={project.id}
                                project={project}
                                index={i}
                                onDelete={() => confirmDelete(project.id)}
                            />
                        ))}
                    </div>
                )}
            </main>

            {/* ── Create Modal ── */}
            {modalOpen && (
                <div className="db-overlay" onClick={closeModal}>
                    <div className="db-modal" onClick={(e) => e.stopPropagation()}>
                        <div className="db-modal-header">
                            <h2 className="db-modal-title">New project</h2>
                            <button className="db-modal-close" onClick={closeModal}>✕</button>
                        </div>
                        <form className="db-modal-form" onSubmit={handleCreate} noValidate>
                            <div className={`db-field ${errors.name ? "db-field--error" : ""}`}>
                                <label className="db-label" htmlFor="name">
                                    Name <span className="db-required">*</span>
                                </label>
                                <input
                                    className="db-input"
                                    id="name"
                                    name="name"
                                    type="text"
                                    placeholder="My awesome project"
                                    value={form.name}
                                    onChange={handleChange}
                                    autoComplete="off"
                                    autoFocus
                                />
                                {errors.name && <span className="db-error">{errors.name}</span>}
                            </div>

                            <div className="db-field">
                                <label className="db-label" htmlFor="description">
                                    Description <span className="db-optional">(optional)</span>
                                </label>
                                <textarea
                                    className="db-input db-textarea"
                                    id="description"
                                    name="description"
                                    placeholder="What is this project about?"
                                    value={form.description}
                                    onChange={handleChange}
                                    rows={3}
                                />
                            </div>

                            <div className="db-modal-meta">
                <span className="db-meta-row">
                  <span className="db-meta-key">user_id</span>
                  <span className="db-meta-val">{MOCK_USER.id}</span>
                </span>
                                <span className="db-meta-row">
                  <span className="db-meta-key">created_at</span>
                  <span className="db-meta-val">now()</span>
                </span>
                            </div>

                            <div className="db-modal-actions">
                                <button type="button" className="db-cancel-btn" onClick={closeModal}>Cancel</button>
                                <button type="submit" className="db-submit-btn">Create project</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* ── Delete Confirm ── */}
            {deleteTarget && (
                <div className="db-overlay" onClick={cancelDelete}>
                    <div className="db-modal db-modal--sm" onClick={(e) => e.stopPropagation()}>
                        <div className="db-delete-icon">⚠</div>
                        <h2 className="db-modal-title">Delete project?</h2>
                        <p className="db-delete-sub">This action cannot be undone.</p>
                        <div className="db-modal-actions">
                            <button className="db-cancel-btn" onClick={cancelDelete}>Cancel</button>
                            <button className="db-delete-btn" onClick={handleDelete}>Delete</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

function ProjectCard({ project, index, onDelete }) {
    const isUpdated = project.updated_at !== project.created_at;

    return (
        <div className="db-card" style={{ animationDelay: `${index * 0.07}s` }}>
            <div className="db-card-top">
                <div className="db-card-title-row">
                    <h3 className="db-card-name">{project.name}</h3>
                    <button className="db-card-delete" onClick={onDelete} title="Delete project">
                        <svg viewBox="0 0 16 16" fill="none">
                            <path d="M3 4h10M6 4V3h4v1M5 4l.5 9h5L11 4" stroke="currentColor" strokeWidth="1.3" strokeLinecap="round" strokeLinejoin="round"/>
                        </svg>
                    </button>
                </div>
                <p className="db-card-desc">
                    {project.description || <span className="db-card-no-desc">No description</span>}
                </p>
            </div>

            <div className="db-card-dates">
                <div className="db-card-date-row">
                    <svg className="db-date-icon" viewBox="0 0 14 14" fill="none">
                        <rect x="1" y="2" width="12" height="11" rx="2" stroke="currentColor" strokeWidth="1.2"/>
                        <path d="M1 6h12" stroke="currentColor" strokeWidth="1.2"/>
                        <path d="M4 1v2M10 1v2" stroke="currentColor" strokeWidth="1.2" strokeLinecap="round"/>
                    </svg>
                    <span className="db-date-label">Created</span>
                    <span className="db-date-val" title={formatDate(project.created_at)}>
            {timeAgo(project.created_at)}
          </span>
                </div>
                {isUpdated && (
                    <div className="db-card-date-row">
                        <svg className="db-date-icon" viewBox="0 0 14 14" fill="none">
                            <path d="M1 7a6 6 0 1 0 6-6 6 6 0 0 0-4.5 2M1 3v2h2" stroke="currentColor" strokeWidth="1.2" strokeLinecap="round" strokeLinejoin="round"/>
                        </svg>
                        <span className="db-date-label">Updated</span>
                        <span className="db-date-val" title={formatDate(project.updated_at)}>
              {timeAgo(project.updated_at)}
            </span>
                    </div>
                )}
            </div>

            <a href={`/editor/${project.id}`} className="db-card-open">
                Open in editor
                <svg viewBox="0 0 16 16" fill="none" className="db-card-arrow">
                    <path d="M3 8h10M9 4l4 4-4 4" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                </svg>
            </a>
        </div>
    );
}