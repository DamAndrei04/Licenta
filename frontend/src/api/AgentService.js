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

/**
 * Opens an SSE connection to receive real-time agent phase updates.
 *
 * @param {string} sessionId  - unique ID shared with the POST /agent request
 * @param {function} onEvent  - called with each parsed AgentStatusEvent object
 * @returns {EventSource}     - caller must call .close() when no longer needed
 */
export const subscribeToAgentStatus = (sessionId, onEvent) => {
    const url = `http://localhost:8081/agent/status/${sessionId}`;
    const es = new EventSource(url);

    es.addEventListener("agent-status", (e) => {
        try {
            const event = JSON.parse(e.data);
            onEvent(event);
        } catch {
            // ignore malformed events
        }
    });

    es.onerror = () => {
        es.close();
    };

    return es;
};