import api from "./axios";

export const getAllUsers = () => {
    return api.get("/user");
};

export const getUserById = (userId) => {
    return api.get(`/user/${userId}`);
};

export const getCurrentUser = () => {
    return api.get("/user/current");
};

export const updateUser = (userId, updateUserRequestDto) => {
    return api.put(`/user/${userId}`, updateUserRequestDto);
};

export const deleteUser = (userId) => {
    return api.delete(`/user/${userId}`);
};