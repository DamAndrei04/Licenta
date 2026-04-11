import { useState, useEffect } from "react";
import "./RegisterForm.css";

export default function RegisterForm() {
    const [form, setForm] = useState({
        username: "",
        password: "",
        confirm: "",
    });
    const [errors, setErrors] = useState({});
    const [submitted, setSubmitted] = useState(false);
    const [countdown, setCountdown] = useState(2);

    useEffect(() => {
        if (!submitted) return;
        if (countdown <= 0) {
            window.location.href = "/workspace";
            return;
        }
        const timer = setTimeout(() => setCountdown((c) => c - 1), 1000);
        return () => clearTimeout(timer);
    }, [submitted, countdown]);

    const validate = () => {
        const e = {};
        if (!form.username.trim()) e.username = "Username is required.";
        if (form.password.length < 8)
            e.password = "Password must be at least 8 characters.";
        if (form.password !== form.confirm)
            e.confirm = "Passwords do not match.";
        return e;
    };

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
        setErrors({ ...errors, [e.target.name]: undefined });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const errs = validate();
        if (Object.keys(errs).length) {
            setErrors(errs);
        } else {
            setSubmitted(true);
        }
    };

    if (submitted) {
        return (
            <div className="rf-wrapper">
                <div className="rf-card rf-success">
                    <div className="rf-success-icon">✓</div>
                    <h2>You're in.</h2>
                    <p>Account created for <strong>{form.username}</strong>.</p>
                    <p className="rf-redirect-msg">
                        Redirecting to dashboard in <strong>{countdown}</strong>s…
                    </p>
                </div>
            </div>
        );
    }

    return (
        <div className="rf-wrapper">
            <div className="rf-card">
                <div className="rf-header">
                    <span className="rf-eyebrow">Get started</span>
                    <h1 className="rf-title">Create account</h1>
                </div>
                <form className="rf-form" onSubmit={handleSubmit} noValidate>
                    <Field
                        label="Username"
                        name="username"
                        type="text"
                        placeholder="Pick a username"
                        value={form.username}
                        error={errors.username}
                        onChange={handleChange}
                    />
                    <Field
                        label="Password"
                        name="password"
                        type="password"
                        placeholder="Min. 8 characters"
                        value={form.password}
                        error={errors.password}
                        onChange={handleChange}
                    />
                    <Field
                        label="Confirm Password"
                        name="confirm"
                        type="password"
                        placeholder="Repeat password"
                        value={form.confirm}
                        error={errors.confirm}
                        onChange={handleChange}
                    />
                    <button className="rf-btn" type="submit">
                        Register
                    </button>
                </form>
                <p className="rf-footer">
                    Already have an account? <a href="/login">Sign in</a>
                </p>
            </div>
        </div>
    );
}

function Field({ label, name, type, placeholder, value, error, onChange }) {
    return (
        <div className={`rf-field ${error ? "rf-field--error" : ""}`}>
            <label className="rf-label" htmlFor={name}>
                {label}
            </label>
            <input
                className="rf-input"
                id={name}
                name={name}
                type={type}
                placeholder={placeholder}
                value={value}
                onChange={onChange}
                autoComplete="off"
            />
            {error && <span className="rf-error">{error}</span>}
        </div>
    );
}