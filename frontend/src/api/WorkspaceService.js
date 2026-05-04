import api from "./axios";

export const saveProjectState = (workspaceRequestDto) => {
    return api.post("/workspace", workspaceRequestDto);
};

export const getProjectWorkspace = (projectId) => {
    return api.get(`/workspace/${projectId}`);
};