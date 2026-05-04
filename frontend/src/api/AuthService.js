import api from "./axios";

export const login = async (username, password) => {
    localStorage.setItem("username", username);
    localStorage.setItem("password", password);

    const response = await api.get("/user/current");
    return response.data;
};

export const register = async (userData) => {
    const response = await api.post("/user", userData);
    return response.data;
};