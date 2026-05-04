import api from "./axios";

export const createProject = (projectRequestDto) => {
    return api.post("/project", projectRequestDto);
};

export const getAllProjects = () => {
    return api.get("/project");
};

export const getProjectById = (projectId) => {
    return api.get(`/project/${projectId}`);
};


export const getProjectsByUserId = (userId) => {
    return api.get(`/project/user/${userId}`);
};


export const getCurrentUserProjects = () => {
    return api.get("/project/user/current");
};

export const updateProject = (projectId, projectRequestDto) => {
    return api.put(`/project/${projectId}`, projectRequestDto);
};

export const deleteProject = (projectId) => {
    return api.delete(`/project/${projectId}`);
};