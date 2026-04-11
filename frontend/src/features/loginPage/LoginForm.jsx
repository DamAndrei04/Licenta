import { useState } from "react";
import "./LoginForm.css";

export default function LoginForm() {
    const [form, setForm] = useState({ username: "", password: "" });
    const [errors, setErrors] = useState({});
    const [submitted, setSubmitted] = useState(false);

    const validate = () => {
        const e = {};
        if (!form.username.trim()) e.username = "Username is required.";
        if (!form.password) e.password = "Password is required.";
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
            <div className="lf-wrapper">
                <div className="lf-card lf-success">
                    <div className="lf-success-icon">✓</div>
                    <h2>Welcome back.</h2>
                    <p>Logged in as <strong>{form.username}</strong>.</p>
                </div>
            </div>
        );
    }

    return (
        <div className="lf-wrapper">
            <div className="lf-card">
                <div className="lf-header">
                    <span className="lf-eyebrow">Welcome back</span>
                    <h1 className="lf-title">Sign in</h1>
                </div>

                <form className="lf-form" onSubmit={handleSubmit} noValidate>
                    <Field
                        label="Username"
                        name="username"
                        type="text"
                        placeholder="Your username"
                        value={form.username}
                        error={errors.username}
                        onChange={handleChange}
                    />
                    <Field
                        label="Password"
                        name="password"
                        type="password"
                        placeholder="Your password"
                        value={form.password}
                        error={errors.password}
                        onChange={handleChange}
                    />

                    <div className="lf-forgot">
                        <a href="#">Forgot password?</a>
                    </div>

                    <button className="lf-btn" type="submit">
                        Log in
                    </button>
                </form>

                <p className="lf-footer">
                    Don't have an account? <a href="/register">Register</a>
                </p>
            </div>
        </div>
    );
}

function Field({ label, name, type, placeholder, value, error, onChange }) {
    return (
        <div className={`lf-field ${error ? "lf-field--error" : ""}`}>
            <label className="lf-label" htmlFor={name}>
                {label}
            </label>
            <input
                className="lf-input"
                id={name}
                name={name}
                type={type}
                placeholder={placeholder}
                value={value}
                onChange={onChange}
                autoComplete="off"
            />
            {error && <span className="lf-error">{error}</span>}
        </div>
    );
}