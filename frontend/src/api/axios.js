import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080",
    headers: {
        "Content-Type": "application/json",
    },
});

// add auth header to request before any call
api.interceptors.request.use(
    (config) => {
        const username = localStorage.getItem("username");
        const password = localStorage.getItem("password");

        if (username && password) {
            const credentials = btoa(`${username}:${password}`);
            config.headers.Authorization = `Basic ${credentials}`;
        }

        return config;
    },
    (error) => {
        localStorage.removeItem("username");
        localStorage.removeItem("password");
        return Promise.reject(error);
    },
);

// handle unauthorized responses
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem("username");
            localStorage.removeItem("password");
            console.log(error);

            if (window.location.pathname !== "/login") {
                window.location.href = "/login";
            }
        }
        return Promise.reject(error);
    },
);

export default api;