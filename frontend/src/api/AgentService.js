import axios from "axios";

const agentApi = axios.create({
    baseURL: "http://localhost:8081",
    headers: {
        "Content-Type": "application/json",
    },
});

export const sendPromptToAgent = (promptRequestDto) => {
    return agentApi.post("/agent", promptRequestDto);
};