import { useState } from "react";
import { login } from "../../api/AuthService";
import "./LoginForm.css";

export default function LoginForm() {
    const [form, setForm] = useState({ username: "", password: "" });
    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);
    const [apiError, setApiError] = useState(null);

    const validate = () => {
        const e = {};
        if (!form.username.trim()) e.username = "Username is required.";
        if (!form.password) e.password = "Password is required.";
        return e;
    };

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
        setErrors({ ...errors, [e.target.name]: undefined });
        setApiError(null);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const errs = validate();
        if (Object.keys(errs).length) {
            setErrors(errs);
            return;
        }

        setLoading(true);
        setApiError(null);
        try {
            await login(form.username, form.password);
            window.location.href = "/dashboard";
        } catch (err) {
            const status = err?.response?.status;
            if (status === 401) {
                setApiError("Invalid username or password.");
            } else {
                setApiError("Something went wrong. Please try again.");
            }
        } finally {
            setLoading(false);
        }
    };

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
                    {apiError && (
                        <div className="lf-api-error">{apiError}</div>
                    )}
                    <div className="lf-forgot">
                        <a href="#">Forgot password?</a>
                    </div>
                    <button className="lf-btn" type="submit" disabled={loading}>
                        {loading ? "Signing in..." : "Log in"}
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